import { create } from "@dojo/framework/core/vdom";
import store from "../store";
import Store from "@dojo/framework/stores/Store";
import { State } from "../interfaces";
import { replace } from "@dojo/framework/stores/state/operations";

const factory = create({ store });

export function createErrorsMiddleware() {
	const errors = factory(({ middleware: { store } }) => {
		const { executor, path, get } = store;
		const errorsMap = new Map<string, string>();
		return {
			rejectValue(field: string, errorMessage: string): void {
				errorsMap.set(field, errorMessage);
			},
			passValue(field: string): void {
				if (errorsMap.has(field)) {
					errorsMap.delete(field);
				}
			},
			reject(errorMessage: string): void {
				errorsMap.set("_globalErrors_", errorMessage);
			},
			pass(): void {
				if (errorsMap.has("_globalErrors_")) {
					errorsMap.delete("_globalErrors_");
				}
			},
			hasErrors(): boolean {
				return errorsMap.size > 0;
			},
			getFieldError(field: string): { valid: boolean; message: string } {
				const errorMessage = errorsMap.get(field);
				if (errorMessage === undefined) {
					return { valid: true, message: "" };
				}
				return { valid: false, message: errorMessage };
			},
			getGlobalError(): string | undefined {
				return errorsMap.get("_globalErrors_");
			},
			clearServerGlobalError(): void {
				const globalErrors = get(path("errors", "globalErrors")) || [];
				if (globalErrors.length > 0) {
					executor(((storeObject: Store<State>) => {
						storeObject.apply([replace(path("errors", "globalErrors"), [])]);
					}) as any);
				}
			},
			clearServerFieldError(field: string): void {
				const fieldErrors = get(path("errors", field)) || [];
				if (fieldErrors.length > 0) {
					executor(((storeObject: Store<State>) => {
						storeObject.apply([replace(path("errors", field), [])]);
					}) as any);
				}
			},
			getServerGlobalError(): string | undefined {
				const globalErrors = get(path("errors", "globalErrors")) || [];
				if (globalErrors.length === 0) {
					return;
				}
				return globalErrors[0];
			},
			getServerFieldError(field: string): { valid: boolean; message: string } {
				const fieldErrors = get(path("errors", field)) || [];

				if (fieldErrors.length === 0) {
					return { valid: true, message: "" };
				}
				return { valid: false, message: fieldErrors[0] };
			},
		};
	});

	return errors;
}

export const errors = createErrorsMiddleware();
export default errors;
