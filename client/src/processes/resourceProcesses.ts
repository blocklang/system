import { createProcess } from '@dojo/framework/stores/process';
import { commandFactory } from './utils';
import { findIndex, find } from '@dojo/framework/shim/array';
import * as request from '../utils/request';
import { add, replace, remove } from '@dojo/framework/stores/state/operations';
import { ResourceInfo, Errors } from '../interfaces';
import { ValidateStatus } from '../constant';
import { getAllChildCount } from '../utils/treeUtil';

const loadChildResourcesCommand = commandFactory<{appId: string, resourceId: string}>(async ({at,get,path, payload: {appId, resourceId}}) => {
    const resources = get(path("resources")) || [];
    
    const token = get(path("session", "token"));
    const result = [];
    if(resourceId === "-1") {
        // 在切换 app 时要清除 resources
        result.push(remove(path("resources")));
        // 让 app 作为根节点
        const apps = get(path("pagedApp", "content")) || [];
        const app = find(apps, item => item.id === appId);
        const rootNode = {id: "-1", parentId: "-2", name: `${app!.name} (APP)`, childrenLoaded: true, level: 0};
        result.push(add(at(path("resources"), 0), rootNode));
        const response = await request.get(`resources/${resourceId}/children?appid=${appId}`, token);
        const json = await response.json();
        if(response.ok) {
            let insertedIndex = 0;
            for(let i = 0; i < json.length; i++) {
                result.push(add(at(path("resources"), insertedIndex), {...json[i], level: 1}));
                insertedIndex++;
            }
        }
        return result;
    }

    const selectedIndex = findIndex(resources, item => item.id === resourceId);
    if(resources[selectedIndex]?.childrenLoaded) {
        return;
    }
    
    const response = await request.get(`resources/${resourceId}/children?&appid=${appId}`, token);
    const json = await response.json();
    if(response.ok) {
        let insertedIndex = selectedIndex + 1;
        for(let i = 0; i < json.length; i++) {
            result.push(add(at(path("resources"), insertedIndex), {...json[i], level: resources[selectedIndex].level+1}));
            insertedIndex++;
        }
        if(selectedIndex > -1) {
            result.push(replace(path(at(path("resources"), selectedIndex), "childrenLoaded"), true));
        }
    }
    return result;
});

const getResourceCommand = commandFactory<{id: string}>(async ({get, path, payload: {id}}) => {
    const token = get(path("session", "token"));
    const response = await request.get(`resources/${id}`, token);
    const resource = await response.json();
    if(response.ok){
        return [replace(path("resource"), resource)];
    }
    return [remove(path("resource"))];
});

// 将 role 中的字段值清空，然后设置默认值
const resetResourceCommand = commandFactory<Partial<ResourceInfo>>(({path, payload}) => {
    return [replace(path("resource"), payload)];
});

const setResourceFieldCommand = commandFactory<{field: keyof ResourceInfo, value: string}>(({path, payload:{field, value=""}}) => {
    const result = [];
    result.push(replace(path("resource", field), value));
    if(field === "name") {
        if(value.trim() === "") {
            result.push(replace(path("formValidation", field), {status: ValidateStatus.INVALID, message: "请输入资源名称！"}));
        } else {
            // 校验通过，则清空错误信息
            result.push(replace(path("formValidation", field), {status: ValidateStatus.VALID, message: ""}));
        }
    }
    if(field === "auth") {
        if(value.trim() === "") {
            result.push(replace(path("formValidation", field), {status: ValidateStatus.INVALID, message: "请输入权限标识！"}));
        } else {
            // 校验通过，则清空错误信息
            result.push(replace(path("formValidation", field), {status: ValidateStatus.VALID, message: ""}));
        }
    }
    return result;
});

const saveResourceCommand = commandFactory(async({at, get, path})=>{
    debugger;
    // 保存前校验
    const resourceInfo = get(path("resource"));
	const {name = "", auth=""} = resourceInfo;
	const result = [];
    if (name.trim() === "") {
        result.push(replace(path("formValidation", "name"), { status: ValidateStatus.INVALID, message: "请输入资源名称！" }));
    }
    if(auth.trim() === "") {
        result.push(replace(path("formValidation", "auth"), {status: ValidateStatus.INVALID, message: "请输入权限标识！"}));
    }
	if(result.length > 0) {
		return result;
	}

    const token = get(path("session", "token"));
    const response = await request.post("resources", resourceInfo, token);
    const json = await response.json();
    if(!response.ok) {
        // 如果保存出错，不要清除 app 数据；保存成功则清除 app 数据
        
        // 根据 status 来区分
        // 如果是编程阶段必须要纠正的错误，则在控制台打印错误信息
        // 如果是运行时出现的校验和权限错误，则在页面上给出友好提示
        return Object.entries(json.errors as Errors).map(([key, value]) => {
            return replace(path("formValidation", key), {status: ValidateStatus.INVALID, message: value[0]});
        });
    }

    // 添加到父节点的最后：
    // 1. 获取父节点所在的索引
    // 2. 然后获取父节点的子节点个数
    const resources = get(path("resources"));
    const selectedIndex = findIndex(resources, item => item.id === resourceInfo.parentId);
    const childCount = getAllChildCount(resources, selectedIndex);
    const insertedIndex = selectedIndex+childCount + 1;

    const resetResource = {appId: json.appId, parentId: json.parentId, resourceType: "02"};

    result.push(replace(path("resource"), resetResource));
    result.push(add(at(path("resources"), insertedIndex), {...json, level: resources[selectedIndex].level+1}));
    result.push(remove(path("formValidation")));
    result.push(replace(path("globalTip"), "保存成功！"));

    return result;
});

const updateResourceCommand = commandFactory(async ({at, get, path}) => {
    const resourceInfo = get(path("resource"));
    const {name = "", auth = ""} = resourceInfo;
    const result = [];
    if (name.trim() === "") {
        result.push(replace(path("formValidation", "name"), { status: ValidateStatus.INVALID, message: "请输入资源名称！" }));
    }
    if(auth.trim() === "") {
        result.push(replace(path("formValidation", "auth"), {status: ValidateStatus.INVALID, message: "请输入权限标识！"}));
    }
	if(result.length > 0) {
		return result;
	}
    
    const token = get(path("session", "token"));
    // 排除掉多余的字段
    const updatedResource = {
        appId: resourceInfo.appId,
        parentId: resourceInfo.parentId,
        name: resourceInfo.name,
        url: resourceInfo.url,
        icon: resourceInfo.icon,
        resourceType: resourceInfo.resourceType,
        description: resourceInfo.description,
        auth: resourceInfo.auth
	};
    const response = await request.put(`resources/${resourceInfo.id}`, updatedResource, token);
    const json = await response.json();
    if(!response.ok) {
        // 如果保存出错，不要清除 app 数据；保存成功则清除 app 数据
        
        // 根据 status 来区分
        // 如果是编程阶段必须要纠正的错误，则在控制台打印错误信息
        // 如果是运行时出现的校验和权限错误，则在页面上给出友好提示
        return Object.entries(json.errors as Errors).map(([key, value]) => {
            return replace(path("formValidation", key), {status: ValidateStatus.INVALID, message: value[0]});
        });
    }

    const resources = get(path("resources"));
    const selectedIndex = findIndex(resources, res => res.id === resourceInfo.id);
    
    return [
        remove(path("resource")), 
        remove(path("formValidation")),
        remove(path("globalTip")),
        replace(path("pageView"), "list"),
        // 更新时，则不从服务器端查询列表，而是使用返回的数据，更新列表中的数据
        replace(at(path("resources"), selectedIndex), json)
    ];
});

export const loadChildResourcesProcess = createProcess('load-child-resources', [loadChildResourcesCommand]);
export const getResourceProcess = createProcess("get-resource", [getResourceCommand]);
export const resetResourceProcess = createProcess("reset-resource", [resetResourceCommand]);
export const setResourceFieldProcess = createProcess("set-resource-field", [setResourceFieldCommand]);
export const saveResourceProcess = createProcess("save-resource", [saveResourceCommand]);
export const updateResourceProcess = createProcess("update-resource", [updateResourceCommand]);