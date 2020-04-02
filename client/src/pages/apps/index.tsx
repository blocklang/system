import { create, tsx } from "@dojo/framework/core/vdom";
import * as c from "bootstrap-classes";
import { ResourceProperties } from "../../interfaces";
import store from "../../store";
import List from "./List";
import New from "./New";
import Edit from "./Edit";
import { loadPermissionProcess } from "../../processes/permissionProcesses";
import NotFound from "../error/404";

export interface AppsProperties extends ResourceProperties {}

const factory = create({ store }).properties<AppsProperties>();

export default factory(function Apps({ properties, middleware: { store } }) {
	const { resId } = properties();
	const { get, path, executor } = store;

	const permission = get(path("permission"));
	if (!permission) {
		executor(loadPermissionProcess)({ resId });
		return;
	}
	if (!permission.canAccess) {
		return <NotFound />;
	}

	const view = get(path("pageView")) || "list";

	return (
		<virtual>
			<section classes={["content-header"]}>
				<div classes={[c.container_fluid]}>
					<h1>APP管理</h1>
				</div>
			</section>
			<section classes={["content"]}>
				{view === "list" && <List />}
				{view === "new" && <New />}
				{view === "edit" && <Edit />}
			</section>
		</virtual>
	);
});
