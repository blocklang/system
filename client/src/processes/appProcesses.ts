import { createProcess } from "@dojo/framework/stores/process";
import { commandFactory } from "./utils";
import * as request from "../utils/request";
import { replace, remove } from "@dojo/framework/stores/state/operations";
import { AppInfo, Errors } from "../interfaces";
import { ValidateStatus } from "../constant";
import { findIndex } from "@dojo/framework/shim/array";

const getPagedAppCommand = commandFactory<{ page?: number }>(async ({ get, path, payload: { page = 0 } }) => {
	const token = get(path("session", "token"));
	const response = await request.get(`apps?page=${page}`, token);
	const json = await response.json();
	if (response.ok) {
		return [replace(path("pagedApp"), json)];
	}

	return [remove(path("pagedApp"))];
});

const getAppCommand = commandFactory<{ id: string }>(async ({ get, path, payload: { id } }) => {
	const token = get(path("session", "token"));
	const response = await request.get(`apps/${id}`, token);
	const app = await response.json();
	if (response.ok) {
		return [replace(path("app"), app)];
	}
	return [remove(path("app"))];
});

const setAppFieldCommand = commandFactory<{ field: keyof AppInfo; value: string }>(
	({ path, payload: { field, value = "" } }) => {
		const result = [];
		result.push(replace(path("app", field), value));
		if (field === "name") {
			if (value.trim() === "") {
				result.push(
					replace(path("formValidation", field), {
						status: ValidateStatus.INVALID,
						message: "请输入APP名称！",
					})
				);
			} else {
				// 校验通过，则清空错误信息
				result.push(replace(path("formValidation", field), { status: ValidateStatus.VALID, message: "" }));
			}
		}
		return result;
	}
);

const saveAppCommand = commandFactory(async ({ get, path }) => {
	// 保存前校验
	const appInfo = get(path("app"));
	const { name = "" } = appInfo;
	if (name.trim() === "") {
		return [
			replace(path("formValidation", "name"), { status: ValidateStatus.INVALID, message: "请输入APP名称！" }),
		];
	}

	const token = get(path("session", "token"));
	const response = await request.post(`apps`, appInfo, token);
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

	return [remove(path("app")), remove(path("formValidation")), replace(path("globalTip"), "保存成功！")];
});

// 注意：所有更新方法，只传界面上允许用户修改的数据项
const updateAppCommand = commandFactory(async ({ at, get, path }) => {
	const appInfo = get(path("app"));
	const { name = "" } = appInfo;
	if (name.trim() === "") {
		return [
			replace(path("formValidation", "name"), { status: ValidateStatus.INVALID, message: "请输入APP名称！" }),
		];
	}

	const token = get(path("session", "token"));
	// 排除掉多余的字段
	const updatedApp = { name: appInfo.name, icon: appInfo.icon, url: appInfo.url, description: appInfo.description };
	const response = await request.put(`apps/${appInfo.id}`, updatedApp, token);
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

	const apps = get(path("pagedApp", "content"));
	const index = findIndex(apps, (app) => app.id === appInfo.id);

	return [
		remove(path("app")),
		remove(path("formValidation")),
		remove(path("globalTip")),
		replace(path("pageView"), "list"),
		// 更新时，则不从服务器端查询列表，而是使用返回的数据，更新列表中的数据
		replace(at(path("pagedApp", "content"), index), json),
	];
});

// 将 app 中的字段值清空，然后设置默认值
const resetAppCommand = commandFactory<Partial<AppInfo>>(({ path, payload }) => {
	return [replace(path("app"), payload)];
});

export const getPagedAppProcess = createProcess("get-paged-apps", [getPagedAppCommand]);
export const getAppProcess = createProcess("get-app", [getAppCommand]);
export const setAppFieldProcess = createProcess("set-app-field", [setAppFieldCommand]);
export const saveAppProcess = createProcess("save-app", [saveAppCommand]);
export const updateAppProcess = createProcess("update-app", [updateAppCommand]);
export const resetAppProcess = createProcess("reset-app", [resetAppCommand]);
