import global from "@dojo/framework/shim/global";
import { createProcess } from "@dojo/framework/stores/process";
import {replace, remove, add} from "@dojo/framework/stores/state/operations";
import { commandFactory } from './utils';
import * as request from '../utils/request';
import { SetSessionPayload } from './interfaces';
import { findIndex } from '@dojo/framework/shim/array';

const setSessionCommand = commandFactory<SetSessionPayload>(({ path, payload: { session } }) => {
	return [replace(path("session"), session)];
});

const loginCommand = commandFactory<{ username: string; password: string }>(async ({ path, payload: {username, password} }) => {
	const response = await request.post("user/login", {username, password});
	const json = await response.json();
	if (!response.ok) {
		return [
			replace(path("errors"), json.errors),
			remove(path("session"))
		];
	}

    global.sessionStorage.setItem("blocklang-session", JSON.stringify(json.user));

	return [
		replace(path("routing", "outlet"), "home"),
		remove(path("errors")),
		replace(path("session"), json.user),
	];
});

const registerCommand = commandFactory<{ username: string; password: string }>(async ({ path, payload: {username, password} }) => {
	const response = await request.post("user/register", {username, password});
	const json = await response.json();
	if (!response.ok) {
		return [
			replace(path("errors"), json.errors),
			remove(path("session"))
		];
	}

    global.sessionStorage.setItem("blocklang-session", JSON.stringify(json.user));

	return [
		replace(path("routing", "outlet"), "home"),
		remove(path("errors")),
		replace(path("session"), json.user),
	];
});

const checkUsernameCommand = commandFactory<{username: string}>(async ({path, payload: {username}}) => {
	const response = await request.post("user/check-username", {username});
	const json = await response.json();
	if (!response.ok) {
		return [
			replace(path("errors"), json.errors),
			remove(path("session"))
		];
	}

	return [
		remove(path("errors"))
	];
});

const logoutCommand = commandFactory(({ path }) => {
	global.sessionStorage.removeItem("blocklang-session");
	return [
		remove(path("session")), 
		replace(path("routing", "outlet"), "home"),
		// 用户注销时，要清除为用户加载的菜单列表
		remove(path("menus"))
	];
});

const loadUserMenusCommand = commandFactory<{resourceId: string}>(async ({at,get,path, payload: {resourceId = "-1"}}) => {
	// 判断该节点的子节点是否已加载，如果是根节点，则判断 menus 是否存在，如果是其他节点，则判断 childrenLoaded 是否为 true
	const menus = get(path("menus"));

	let childrenLoaded = false;
	let currentMenuIndex = -1;
	if(resourceId === "-1") {
		if(menus){
			childrenLoaded = true;
		}
	}else {
		currentMenuIndex = findIndex(menus, item => item.id === resourceId);
		const currentMenu = menus[currentMenuIndex];
		childrenLoaded = currentMenu && currentMenu.childrenLoaded || false;
	}
	if(childrenLoaded) {
		return;
	}

	const token = get(path("session", "token"));
	const response = await request.get(`user/resources/${resourceId}/children`, token);
	const json = await response.json();
	
	if(response.ok){
		// 将这些菜单追加到父菜单的后面
		if(currentMenuIndex === -1) {
			return [replace(path("menus"), json)];
		} else {
			const result = [];
			result.push(replace(path(at(path("menus"), currentMenuIndex), "childrenLoaded"), true));
			let insertedIndex = currentMenuIndex + 1;
			for(let i = 0; i < json.length; i++) {
				result.push(add(at(path("menus"), insertedIndex), json[i]));
				insertedIndex++;
			}
			return result;
		}
	}
	return [remove(path("menus"))];
});

export const setSessionProcess = createProcess("set-session", [setSessionCommand]);
export const loginProcess = createProcess("login", [loginCommand]);
export const registerProcess = createProcess("register", [registerCommand]);
export const checkUsernameProcess = createProcess("check-username", [checkUsernameCommand]);
export const logoutProcess = createProcess("logout", [logoutCommand]);
export const loadUserMenusProcess = createProcess("load-user-menus", [loadUserMenusCommand]);