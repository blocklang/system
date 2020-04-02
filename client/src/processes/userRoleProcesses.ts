import { createProcess } from "@dojo/framework/stores/process";
import { remove, replace } from "@dojo/framework/stores/state/operations";
import { findIndex } from "@dojo/framework/shim/array";
import { commandFactory } from "./utils";
import * as request from "../utils/request";

const getRoleUserCommand = commandFactory<{ roleId?: string }>(async ({ get, path, payload: { roleId } }) => {
	const token = get(path("session", "token"));
	const response = await request.get(`roles/${roleId}/users`, token);
	const json = await response.json();
	if (response.ok) {
		return [replace(path("users"), json)];
	}

	return [remove(path("users"))];
});

const addUserToRoleCommand = commandFactory<{ roleId: string; userId: string }>(
	async ({ at, get, path, payload: { roleId, userId } }) => {
		const token = get(path("session", "token"));
		const users = get(path("users"));
		const response = await request.post(`roles/${roleId}/users/${userId}/assign`, {}, token);
		const json = await response.json();
		debugger;
		if (response.ok) {
			return [replace(at(path("users"), users.length), json)];
		}
	}
);

const removeUserFromRoleCommand = commandFactory<{ roleId: string; userId: string }>(
	async ({ at, get, path, payload: { roleId, userId } }) => {
		const token = get(path("session", "token"));
		const users = get(path("users"));
		const response = await request.post(`roles/${roleId}/users/${userId}/unassign`, {}, token);
		const index = findIndex(users, (user) => user.id === userId);
		debugger;
		if (response.ok) {
			return [remove(at(path("users"), index))];
		}
	}
);

export const getRoleUsersProcess = createProcess("get-role-users", [getRoleUserCommand]);
export const addUserToRoleProcess = createProcess("add-user-to-role", [addUserToRoleCommand]);
export const removeUserFromRoleProcess = createProcess("remove-user-from-role", [removeUserFromRoleCommand]);
