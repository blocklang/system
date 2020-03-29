import { createProcess } from '@dojo/framework/stores/process';
import { commandFactory } from './utils';
import * as request from '../utils/request';
import { add, replace, remove } from '@dojo/framework/stores/state/operations';
import { DeptInfo, Errors } from '../interfaces';
import { ValidateStatus } from '../constant';
import { findIndex } from '@dojo/framework/shim/array';
import { getAllChildCount } from '../utils/treeUtil';

const loadChildDeptsCommand = commandFactory<{deptId?: string}>(async ({at,get,path, payload: {deptId = "-1"}}) => {
    // 要显示虚拟根节点，并默认加载父节点为 -1 的所有子节点
    const depts = get(path("depts")) || [];
    
    const result = [];

    const token = get(path("session", "token"));
    if(depts.length === 0) {
        // 根节点
        const rootNode = {id:"-1", parentId: "-2", name:"部门", active: true, childrenLoaded: true, hasChildren: true};
        result.push(add(at(path("depts"), 0), rootNode));
        const response = await request.get(`depts/${deptId}/children`, token);
        const json = await response.json();
        if(response.ok) {
            let insertedIndex = 1;
			for(let i = 0; i < json.length; i++) {
				result.push(add(at(path("depts"), insertedIndex), json[i]));
				insertedIndex++;
			}
        }
        return result;
    }

    const selectedIndex = findIndex(depts, item => item.id === deptId);
    if(depts[selectedIndex].childrenLoaded) {
        return;
    }

    const response = await request.get(`depts/${deptId}/children`, token);
    const json = await response.json();
    if(response.ok) {
        let insertedIndex = selectedIndex + 1;
        for(let i = 0; i < json.length; i++) {
            result.push(add(at(path("depts"), insertedIndex), json[i]));
            insertedIndex++;
        }
        result.push(replace(path(at(path("depts"), selectedIndex), "childrenLoaded"), true));
    }
    return result;
});

const getDeptCommand = commandFactory<{id: string}>(async ({get, path, payload: {id}}) => {
    const token = get(path("session", "token"));
    const response = await request.get(`depts/${id}`, token);
    const dept = await response.json();
    if(response.ok){
        return [replace(path("dept"), dept)];
    }
    return [remove(path("dept"))];
});

const saveDeptCommand = commandFactory(async({at, get, path})=>{
    // 保存前校验
    const deptInfo = get(path("dept"));
	const {name = ""} = deptInfo;
	const result = [];
    if(name.trim() === "") {
		result.push(replace(path("formValidation", "username"), {status: ValidateStatus.INVALID, message: "请输入部门名！"}));
	}
	if(result.length > 0) {
		return result;
	}

    const token = get(path("session", "token"));
    const response = await request.post("depts", deptInfo, token);
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
    const depts = get(path("depts"));
    const selectedIndex = findIndex(depts, item => item.id === deptInfo.parentId);
    const childCount = getAllChildCount(depts, selectedIndex);
    const insertedIndex = selectedIndex+childCount + 1;

    const resetDept = {parentId: json.parentId};

    result.push(replace(path("dept"), resetDept));
    result.push(add(at(path("depts"), insertedIndex), json));
    result.push(remove(path("formValidation")));
    result.push(replace(path("globalTip"), "保存成功！"));
    if(childCount === 0) {
         // 修改父节点的加载状态
        result.push(replace(path(at(path("depts"), selectedIndex), "hasChildren"), true));
    }
    return result;
});

const updateDeptCommand = commandFactory(async ({at, get, path}) => {
    const deptInfo = get(path("dept"));
    const {name = ""} = deptInfo;
    const result = [];
    if(name.trim() === "") {
		result.push(replace(path("formValidation", "name"), {status: ValidateStatus.INVALID, message: "请输入部门名称！"}));
    }
	if(result.length > 0) {
		return result;
	}
    
    const token = get(path("session", "token"));
    // 排除掉多余的字段
    const updatedDept = {
        parentId: deptInfo.parentId,
        name: deptInfo.name
	};
    const response = await request.put(`depts/${deptInfo.id}`, updatedDept, token);
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

    const depts = get(path("depts"));
    const selectedIndex = findIndex(depts, dept => dept.id === deptInfo.id);
    
    return [
        remove(path("dept")), 
        remove(path("formValidation")),
        remove(path("globalTip")),
        replace(path("pageView"), "list"),
        // 更新时，则不从服务器端查询列表，而是使用返回的数据，更新列表中的数据
        replace(at(path("depts"), selectedIndex), json)
    ];
});

const setDeptFieldCommand = commandFactory<{field: keyof DeptInfo, value: string}>(({path, payload:{field, value=""}}) => {
    const result = [];
    result.push(replace(path("dept", field), value));
    if(field === "name") {
        if(value.trim() === "") {
            result.push(replace(path("formValidation", field), {status: ValidateStatus.INVALID, message: "请输入部门名称！"}));
        } else {
            // 校验通过，则清空错误信息
            result.push(replace(path("formValidation", field), {status: ValidateStatus.VALID, message: ""}));
        }
	}
    return result;
});

// 将 user 中的字段值清空，然后设置默认值
const resetDeptCommand = commandFactory<Partial<DeptInfo>>(({path, payload}) => {
    return [replace(path("dept"), payload)];
});

export const loadChildDeptsProcess = createProcess("load-child-depts", [loadChildDeptsCommand]);
export const resetDeptProcess = createProcess('reset-dept', [resetDeptCommand]);
export const getDeptProcess = createProcess("get-dept", [getDeptCommand]);
export const setDeptFieldProcess = createProcess('set-dept-field', [setDeptFieldCommand]);
export const saveDeptProcess = createProcess('save-dept', [saveDeptCommand]);
export const updateDeptProcess = createProcess('update-dept', [updateDeptCommand]);
