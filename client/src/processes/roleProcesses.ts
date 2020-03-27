import { createProcess } from '@dojo/framework/stores/process';
import { commandFactory } from './utils';
import * as request from '../utils/request';
import { replace, remove } from '@dojo/framework/stores/state/operations';
import { RoleInfo, Errors } from '../interfaces';
import { ValidateStatus } from '../constant';
import { findIndex } from '@dojo/framework/shim/array';

const getPagedRoleCommand = commandFactory<{ appId: string, page?: number }>(async ({ get, path, payload: {appId, page=0} }) => {
    const token = get(path("session", "token"));
    const response = await request.get(`roles?appId=${appId}&page=${page}`, token);
    const json = await response.json();
    if(response.ok) {
        return [replace(path("pagedRole"), json)];
    }
    
    return [remove(path("pagedRole"))];
});

const getRoleCommand = commandFactory<{id: string}>(async ({get, path, payload: {id}}) => {
    const token = get(path("session", "token"));
    const response = await request.get(`roles/${id}`, token);
    const role = await response.json();
    if(response.ok){
        return [replace(path("role"), role)];
    }
    return [remove(path("role"))];
});

const setRoleFieldCommand = commandFactory<{field: keyof RoleInfo, value: string}>(({path, payload:{field, value=""}}) => {
    const result = [];
    result.push(replace(path("role", field), value));
    if(field === "name") {
        if(value.trim() === "") {
            result.push(replace(path("formValidation", field), {status: ValidateStatus.INVALID, message: "请输入角色名！"}));
        } else {
            // 校验通过，则清空错误信息
            result.push(replace(path("formValidation", field), {status: ValidateStatus.VALID, message: ""}));
        }
    }
    return result;
});

const saveRoleCommand = commandFactory(async({get, path})=>{
    // 保存前校验
    const roleInfo = get(path("role"));
    const {appId="", name = ""} = roleInfo;
    const result = [];
    if(appId.trim() === "") {
        result.push(replace(path("formValidation", "appId"), {status: ValidateStatus.INVALID, message: "请选择APP！"}))
    }
    if(name.trim() === "") {
        result.push(replace(path("formValidation", "name"), {status: ValidateStatus.INVALID, message: "请输入角色名！"}));
    }
    if(result.length > 0) {
        return result;
    }

    const token = get(path("session", "token"));
    const response = await request.post(`roles`, roleInfo, token);
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
    
    return [
        replace(path("role"), {appId, appName: roleInfo.appName} as Partial<RoleInfo>), // 保存成功后，不能清除 role,因为还需要使用 role 中的 appId 和 appName
        remove(path("formValidation")),
        replace(path("globalTip"), "保存成功！")
    ];
});

// 注意：所有更新方法，只传界面上允许用户修改的数据项
const updateRoleCommand = commandFactory(async ({at, get, path}) => {debugger;
    const roleInfo = get(path("role"));
    const {name = ""} = roleInfo;
    if(name.trim() === "") {
        return [replace(path("formValidation", "name"), {status: ValidateStatus.INVALID, message: "请输入角色名！"})];
    }
    
    const token = get(path("session", "token"));
    // 排除掉多余的字段
    const updatedRole = {name: roleInfo.name, description: roleInfo.description};
    const response = await request.put(`roles/${roleInfo.id}`, updatedRole, token);
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

    const roles = get(path("pagedRole", "content"));
    const index = findIndex(roles, role => role.id === roleInfo.id);
    
    return [
        remove(path("role")), 
        remove(path("formValidation")),
        remove(path("globalTip")),
        replace(path("pageView"), "list"),
        // 更新时，则不从服务器端查询列表，而是使用返回的数据，更新列表中的数据
        replace(at(path("pagedRole", "content"), index), json)
    ];
});

// 将 role 中的字段值清空，然后设置默认值
const resetRoleCommand = commandFactory<Partial<RoleInfo>>(({path, payload}) => {
    return [replace(path("role"), payload)];
});

export const getPagedRoleProcess = createProcess('get-paged-role', [getPagedRoleCommand]);
export const getRoleProcess = createProcess("get-role", [getRoleCommand]);
export const setRoleFieldProcess = createProcess('set-role-field', [setRoleFieldCommand]);
export const resetRoleProcess = createProcess('reset-role', [resetRoleCommand]);
export const saveRoleProcess = createProcess('save-role', [saveRoleCommand]);
export const updateRoleProcess = createProcess('update-role', [updateRoleCommand]);
