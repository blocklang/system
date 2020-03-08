import global from "@dojo/framework/shim/global";
import { createProcess } from "@dojo/framework/stores/process";
import {replace} from "@dojo/framework/stores/state/operations";
import { commandFactory } from './utils';
import { post } from './request';

const loginCommand = commandFactory<{ loginName: string; password: string }>(async ({ path, payload: {loginName, password} }) => {
    const response = await post("login", {loginName, password});
	const user = await response.json();

    global.sessionStorage.setItem("blocklang-session", JSON.stringify(user));

	return [
		replace(path("routing", "outlet"), "home"),
		replace(path("session"), user),
	];
});

export const loginProcess = createProcess("login", [loginCommand]);