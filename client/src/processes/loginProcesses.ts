import global from "@dojo/framework/shim/global";
import { createProcess } from "@dojo/framework/stores/process";
import {replace, remove} from "@dojo/framework/stores/state/operations";
import { commandFactory } from './utils';
import * as request from '../utils/request';
import { SetSessionPayload } from './interfaces';

const setSessionCommand = commandFactory<SetSessionPayload>(({ path, payload: { session } }) => {
	return [replace(path("session"), session)];
});

const loginCommand = commandFactory<{ username: string; password: string }>(async ({ path, payload: {username, password} }) => {
	const response = await request.post("users/login", {username, password});
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
	const response = await request.post("users", {username, password});
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
	const response = await request.post("users/check-username", {username});
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
	return [remove(path("session")), replace(path("routing", "outlet"), "home")];
});

export const setSessionProcess = createProcess("set-session", [setSessionCommand]);
export const loginProcess = createProcess("login", [loginCommand]);
export const registerProcess = createProcess("register", [registerCommand]);
export const checkUsernameProcess = createProcess("check-username", [checkUsernameCommand]);
export const logoutProcess = createProcess("logout", [logoutCommand]);