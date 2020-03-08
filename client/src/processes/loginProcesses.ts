import global from "@dojo/framework/shim/global";
import { createProcess } from "@dojo/framework/stores/process";
import {replace} from "@dojo/framework/stores/state/operations";
import { commandFactory } from './utils';
import * as request from './request';
import { SetSessionPayload } from './interfaces';

const setSessionCommand = commandFactory<SetSessionPayload>(({ path, payload: { session } }) => {
	return [replace(path("session"), session)];
});

const loginCommand = commandFactory<{ username: string; password: string }>(async ({ path, payload: {username, password} }) => {
    const response = await request.post("login", {username, password});
	const user = await response.json();

    global.sessionStorage.setItem("blocklang-session", JSON.stringify(user));

	return [
		replace(path("routing", "outlet"), "home"),
		replace(path("session"), user),
	];
});

const registerCommand = commandFactory<{ username: string; password: string }>(async ({ path, payload: {username, password} }) => {
    const response = await request.post("users", {username, password});
	const user = await response.json();

    global.sessionStorage.setItem("blocklang-session", JSON.stringify(user));

	return [
		replace(path("routing", "outlet"), "home"),
		replace(path("session"), user),
	];
});

export const setSessionProcess = createProcess("set-session", [setSessionCommand]);
export const loginProcess = createProcess("login", [loginCommand]);
export const registerProcess = createProcess("register", [registerCommand]);
