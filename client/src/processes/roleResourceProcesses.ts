import { createProcess } from '@dojo/framework/stores/process';
import { commandFactory } from './utils';
import { replace, remove } from '@dojo/framework/stores/state/operations';
import { includes, find } from '@dojo/framework/shim/array';
import * as request from '../utils/request';

const getRoleResourceCommand = commandFactory<{ roleId?: string }>(async ({get, path, payload: {roleId}}) => {
	const token = get(path("session", "token"));
    const response = await request.get(`roles/${roleId}/resources`, token);
    const json = await response.json();
    if(response.ok) {
        return [replace(path("roleResources"), json)];
    }
    
    return [remove(path("roleResources"))];
});

const prepareRoleResourcesCommand = commandFactory<{resourceId: string, action: "add" | "remove"}>(({ get, path, payload: {resourceId, action} }) => {
    // 存储选中的资源
    const roleResources = get(path("roleResources")) || [];
    // app 下的所有资源
    const allResources = get(path("resources")) || [];
    
    let actualResources: string[] = [];

    const children = [resourceId]; // 要包含节点本身
    const parents:string[] = [];
    if(action === "add") {
        // 如果添加的节点包含子节点，则要选中所有子节点
        // 1. 先从 allResources 找到当前选中的资源，及其所有子资源
        // 2. 再从 allResources 中找到当前资源的所有父资源
        // 3. 往 roleResources 中添加资源，如果已存在则忽略

        findChildren(resourceId);
        findParent(resourceId);

        // 先从 children 中剔除掉 roleResources 中已存在的，然后再合并
        actualResources = [
            ...roleResources, 
            ...children.filter(item => !includes(roleResources, item)),
            ...parents.filter(item => !includes(roleResources, item))
        ];
    }else if(action === "remove") {
        // 如果删除的节点包含子节点，则要删除所有子节点
        findChildren(resourceId);
        actualResources = roleResources.filter(item => !includes(children, item));
    }

    function findChildren(id: string) {
        allResources.filter(item => item.parentId === id).forEach(item => {
            children.push(item.id);
            findChildren(item.id);
        })
    }

    function findParent(id: string) {
        const current = find(allResources, item => item.id === id);
        let parentId = current?.parentId || "-1";
        parents.push(parentId);
        while(parentId !== "-1") {
            const parent = find(allResources, item => item.id === parentId);
            parentId = parent?.parentId || "-1";
            parents.push(parentId);
        }
    }

	return [
		replace(path("roleResources"), actualResources)
	];
});

const clearAppResourcesCommand = commandFactory(({path}) => {
	return [
		remove(path("resources"))
	];
});

const clearRoleResourcesCommand = commandFactory(({path}) => {
	return [
		remove(path("roleResources"))
	];
});

const updateRoleResourcesCommand = commandFactory<{appId: string}>(async ({get, path, payload: {appId}}) => {
    const token = get(path("session", "token"));
    const role = get(path("role"));
    const resources = get(path("roleResources")) || [];

    const response = await request.put(`roles/${role.id}/resources`, {appId, resources}, token);
    if(response.ok) {
        // 停留在保存页面，并弹出提示信息
        return [replace(path("globalTip"), "保存成功！")];
    }

});

export const getRoleResourcesProcess = createProcess("get-role-resources", [getRoleResourceCommand]);
export const prepareRoleResourcesProcess = createProcess("prepare-role-resources", [prepareRoleResourcesCommand]);
export const updateRoleResourcesProcess = createProcess("update-role-resources", [updateRoleResourcesCommand]);
export const clearAppResourcesProcess = createProcess("clear-app-resources", [clearAppResourcesCommand]);
export const clearRoleResourcesProcess = createProcess("clear-role-resources", [clearRoleResourcesCommand]);