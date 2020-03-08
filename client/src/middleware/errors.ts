import { create } from "@dojo/framework/core/vdom";

const factory = create({});

export function createErrorsMiddleware() {
    const errors = factory(({middleware: {}}) => {
        const errorsMap = new Map<string, string>();
        return {
            rejectValue(field: string, errorMessage: string): void {
                errorsMap.set(field, errorMessage);
            },
            passValue(field: string) : void {
                if(errorsMap.has(field)) {
                    errorsMap.delete(field);
                }
            },
            hasErrors() : boolean {
                return errorsMap.size > 0;
            },
            getError(field: string): {valid: boolean; message: string} {
                const errorMessage = errorsMap.get(field);
                if(errorMessage === undefined) {
                    return {valid: true, message: ""};
                }
                return {valid: false, message: errorMessage};
            }
        };
    });

    return errors;
}

export const errors = createErrorsMiddleware();
export default errors;