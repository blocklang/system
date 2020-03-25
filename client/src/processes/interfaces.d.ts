import { OutletContext } from "@dojo/framework/routing/interfaces";
import { Session } from "../interfaces";

export interface ChangeRoutePayload {
	outlet: string;
	context: OutletContext;
}

export interface SetSessionPayload {
	session: Session;
}

export interface AppPayload {
	name: string;
	icon?: string;
	url?: string;
	description?: string;
}