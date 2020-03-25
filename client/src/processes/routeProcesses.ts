import { commandFactory } from './utils';
import { createProcess } from '@dojo/framework/stores/process';
import { replace, remove } from '@dojo/framework/stores/state/operations';
import { ChangeRoutePayload } from './interfaces';
import { Params } from '@dojo/framework/routing/interfaces';

const changeRouteCommand = commandFactory<ChangeRoutePayload>(({ path, payload: { outlet, context } }) => {
	return [
		replace(path("routing", "outlet"), outlet),
		replace(path("routing", "params"), context.params),
		remove(path("errors")), // FIXME: 删除 errors
		remove(path("permission"))
	];
});

const redirectToLoginCommand = commandFactory(({ path }) => {
	return [
		replace(path("routing", "outlet"), "login"),
		remove(path("errors"))
	];
});

const redirectToCommand = commandFactory<{outlet: string; params?: Params}>(({path, payload: {outlet, params}}) => {
	return [
		replace(path("routing", "outlet"), outlet),
		replace(path("routing", "params"), params),
		remove(path("errors"))
	];
});

export const changeRouteProcess = createProcess("change-route", [changeRouteCommand]);
export const redirectToLoginProcess = createProcess("redirect-to-login", [redirectToLoginCommand]);
export const redirectToProcess = createProcess("redirect-to", [redirectToCommand]);