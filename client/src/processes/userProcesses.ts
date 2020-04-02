import global from "@dojo/framework/shim/global";
import { createProcess } from "@dojo/framework/stores/process";
import { commandFactory } from "./utils";
import * as request from "../utils/request";
import { replace, remove } from "@dojo/framework/stores/state/operations";
import { Session, UserInfo, Errors } from "../interfaces";
import { ValidateStatus } from "../constant";
import { findIndex } from "@dojo/framework/shim/array";

/**
 * 获取登录用户信息
 */
const getCurrentUserCommand = commandFactory(async ({ path }) => {
	const response = await request.get("/user");
	const json: Session = await response.json();
	if (!response.ok) {
		// 没有获取到登录用户信息，则清空缓存的用户信息
		global.sessionStorage.removeItem("blocklang-session");
		return [remove(path("session"))];
	}

	// 如果用户未登录，也会返回 json 对象
	if (json.username) {
		global.sessionStorage.setItem("blocklang-session", JSON.stringify(json));
	} else {
		// 说明用户未登录，所以清空登录信息
		global.sessionStorage.removeItem("blocklang-session");
	}

	return [replace(path("session"), json)];
});

const getPagedUserCommand = commandFactory<{ page?: number; excludeAdmin?: boolean }>(
	async ({ get, path, payload: { page = 0, excludeAdmin = false } }) => {
		const token = get(path("session", "token"));
		const response = await request.get(`users?page=${page}&exclude_admin=${excludeAdmin}`, token);
		const json = await response.json();
		if (response.ok) {
			return [replace(path("pagedUser"), json)];
		}

		return [remove(path("pagedUser"))];
	}
);

const getUserCommand = commandFactory<{ id: string }>(async ({ get, path, payload: { id } }) => {
	const token = get(path("session", "token"));
	const response = await request.get(`users/${id}`, token);
	const user = await response.json();
	if (response.ok) {
		return [replace(path("user"), user)];
	}
	return [remove(path("user"))];
});

const setUserFieldCommand = commandFactory<{ field: keyof UserInfo; value: string }>(
	({ path, payload: { field, value = "" } }) => {
		const result = [];
		result.push(replace(path("user", field), value));
		if (field === "username") {
			if (value.trim() === "") {
				result.push(
					replace(path("formValidation", field), {
						status: ValidateStatus.INVALID,
						message: "请输入登录名！",
					})
				);
			} else {
				// 校验通过，则清空错误信息
				result.push(replace(path("formValidation", field), { status: ValidateStatus.VALID, message: "" }));
			}
		}
		if (field === "password") {
			if (value.trim() === "") {
				result.push(
					replace(path("formValidation", field), { status: ValidateStatus.INVALID, message: "请输入密码！" })
				);
			} else {
				// 校验通过，则清空错误信息
				result.push(replace(path("formValidation", field), { status: ValidateStatus.VALID, message: "" }));
			}
		}
		return result;
	}
);

const saveUserCommand = commandFactory(async ({ get, path }) => {
	// 保存前校验
	const userInfo = get(path("user"));
	const { username = "", password = "" } = userInfo;
	const result = [];
	if (username.trim() === "") {
		result.push(
			replace(path("formValidation", "username"), { status: ValidateStatus.INVALID, message: "请输入登录名！" })
		);
	}
	if (password.trim() === "") {
		result.push(
			replace(path("formValidation", "password"), { status: ValidateStatus.INVALID, message: "请输入密码！" })
		);
	}
	if (result.length > 0) {
		return result;
	}

	const token = get(path("session", "token"));
	const response = await request.post("users", userInfo, token);
	const json = await response.json();
	if (!response.ok) {
		// 如果保存出错，不要清除 app 数据；保存成功则清除 app 数据

		// 根据 status 来区分
		// 如果是编程阶段必须要纠正的错误，则在控制台打印错误信息
		// 如果是运行时出现的校验和权限错误，则在页面上给出友好提示
		return Object.entries(json.errors as Errors).map(([key, value]) => {
			return replace(path("formValidation", key), { status: ValidateStatus.INVALID, message: value[0] });
		});
	}

	return [
		remove(path("user")),
		replace(path("user", "password"), "123456"), // 也要在此处设置默认值
		remove(path("formValidation")),
		replace(path("globalTip"), "保存成功！"),
	];
});

// 注意：所有更新方法，只传界面上允许用户修改的数据项
const updateUserCommand = commandFactory(async ({ at, get, path }) => {
	const userInfo = get(path("user"));
	const { username = "" } = userInfo;
	const result = [];
	if (username.trim() === "") {
		result.push(
			replace(path("formValidation", "username"), { status: ValidateStatus.INVALID, message: "请输入登录名！" })
		);
	}
	if (result.length > 0) {
		return result;
	}

	const token = get(path("session", "token"));
	// 排除掉多余的字段
	const updatedUser = {
		username: userInfo.username,
		nickname: userInfo.nickname,
		sex: userInfo.sex,
		phoneNumber: userInfo.phoneNumber,
		deptId: userInfo.deptId,
	};
	const response = await request.put(`users/${userInfo.id}`, updatedUser, token);
	const json = await response.json();
	if (!response.ok) {
		// 如果保存出错，不要清除 app 数据；保存成功则清除 app 数据

		// 根据 status 来区分
		// 如果是编程阶段必须要纠正的错误，则在控制台打印错误信息
		// 如果是运行时出现的校验和权限错误，则在页面上给出友好提示
		return Object.entries(json.errors as Errors).map(([key, value]) => {
			return replace(path("formValidation", key), { status: ValidateStatus.INVALID, message: value[0] });
		});
	}

	const users = get(path("pagedUser", "content"));
	const index = findIndex(users, (user) => user.id === userInfo.id);

	return [
		remove(path("user")),
		remove(path("formValidation")),
		remove(path("globalTip")),
		replace(path("pageView"), "list"),
		// 更新时，则不从服务器端查询列表，而是使用返回的数据，更新列表中的数据
		replace(at(path("pagedUser", "content"), index), json), // TODO: 要返回部门名称
	];
});

// 将 user 中的字段值清空，然后设置默认值
const resetUserCommand = commandFactory<Partial<UserInfo>>(({ path, payload }) => {
	return [replace(path("user"), payload)];
});

export const getCurrentUserProcess = createProcess("get-current-user", [getCurrentUserCommand]);
export const getPagedUserProcess = createProcess("get-paged-user", [getPagedUserCommand]);
export const getUserProcess = createProcess("get-user", [getUserCommand]);
export const setUserFieldProcess = createProcess("set-user-field", [setUserFieldCommand]);
export const saveUserProcess = createProcess("save-user", [saveUserCommand]);
export const updateUserProcess = createProcess("update-user", [updateUserCommand]);
export const resetUserProcess = createProcess("reset-user", [resetUserCommand]);
