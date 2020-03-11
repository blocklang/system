export interface Session {
    username: string;
    token: string;
}


export interface Routing {
	outlet: string;
	params: { [index: string]: string };
}

//[index: string]: string[];
export interface Errors {
    globalErrors: string[];
    [index: string]: string[];
}

export interface State {
    routing: Routing;
    session: Session;
    errors: Errors;
}