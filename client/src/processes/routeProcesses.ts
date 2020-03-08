import { commandFactory } from './utils';
import { createProcess } from '@dojo/framework/stores/process';
import { replace } from '@dojo/framework/stores/state/operations';
import { ChangeRoutePayload } from './interfaces';

const changeRouteCommand = commandFactory<ChangeRoutePayload>(({ path, payload: { outlet, context } }) => {
	return [
		replace(path("routing", "outlet"), outlet),
		replace(path("routing", "params"), context.params),
	];
});

const redirectToLoginCommand = commandFactory(({ path }) => {
	return [
		replace(path("routing", "outlet"), "login")
	];
});


export const changeRouteProcess = createProcess("change-route", [changeRouteCommand]);
export const redirectToLoginProcess = createProcess("redirect-to-login", [redirectToLoginCommand]);