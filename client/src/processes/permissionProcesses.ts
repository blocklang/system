import { commandFactory } from './utils';
import * as request from '../utils/request';
import { replace } from '@dojo/framework/stores/state/operations';
import { createProcess } from '@dojo/framework/stores/process';

const loadPermissionCommand = commandFactory<{resId: string}>(async ({get, path, payload: {resId}}) => {
    const token = get(path("session", "token"));
	const response = await request.get(`user/resources/${resId}/permissions`, token);
	const json = await response.json();
	if (!response.ok) {
		return [replace(path("errors"), json.errors)];
	}

	return [replace(path("permission"), json)];
});

export const loadPermissionProcess = createProcess('load-permission', [loadPermissionCommand]);