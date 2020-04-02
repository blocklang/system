import { create, tsx } from "@dojo/framework/core/vdom";
import store from "../../../store";
import { ResourceInfo } from "../../../interfaces";
import icache from "@dojo/framework/core/middleware/icache";
import * as c from "bootstrap-classes";
import * as css from "./Tree.m.css";
import { loadChildResourcesProcess } from "../../../processes/resourceProcesses";
import { findIndex } from "@dojo/framework/shim/array";
import { prepareRoleResourcesProcess } from "../../../processes/roleResourceProcesses";

export interface TreeProperties {
	appId?: string;
	onSelectNode: (id: string) => void;
}

const factory = create({ store, icache }).properties<TreeProperties>();

export default factory(function Tree({ properties, middleware: { store, icache } }) {
	const { appId, onSelectNode } = properties();
	const { get, path, executor } = store;

	if (!appId) {
		return;
	}

	let resources = get(path("resources"));
	if (appId && !resources) {
		executor(loadChildResourcesProcess)({ appId, resourceId: "-1", showRoot: false, recursive: true });
	}
	if (!resources) {
		resources = [];
	}

	const roleResources = get(path("roleResources")) || [];

	const isChecked = (id: string) => {
		return findIndex(roleResources, (item) => item === id) > -1;
	};

	const getChildren = (id: string) => {
		// 在树结构中不显示按钮资源
		return resources.filter((item) => item.parentId === id);
	};

	function timesIndent(count: number) {
		const result = [];
		for (let i = 0; i < count; i++) {
			result.push(<span key={`${i}`} classes={[css.indent]}></span>);
		}
		return <span classes={["align-self-stretch"]}>{result}</span>;
	}

	const renderTreeNode = (item: ResourceInfo) => {
		const { id, name, resourceType } = item;

		const hasChildren = resourceType !== "03";
		const level = icache.get(`${item.id}-level`) || 0;
		if (hasChildren) {
			// 默认展开根节点，其余节点默认不展开
			return (
				<div key={id} classes={[]}>
					<div
						key="item"
						classes={[c.d_flex, css.node, c.align_items_start]}
						onclick={(event: MouseEvent<EventTarget>) => {
							icache.set<string>("activeNodeId", id);
							onSelectNode && onSelectNode(id);
						}}
					>
						{timesIndent(level)}
						<div classes={[c.form_check]}>
							<input
								id={item.id}
								onclick={(event: MouseEvent<HTMLInputElement>) => {
									const checked = event.target.checked;
									const action = checked ? "add" : "remove";
									executor(prepareRoleResourcesProcess)({ resourceId: id, action });
								}}
								checked={isChecked(id)}
								classes={[c.form_check_input]}
								type="checkbox"
							/>
							<label for={item.id} classes={[c.form_check_label]}>
								{name}
							</label>
						</div>
						<span classes={[c.flex_grow_1, c.d_flex, "flex-row", c.ml_4]}>
							{getChildren(id)
								.filter((item) => item.resourceType === "03")
								.map((item) => {
									icache.set(`${item.id}-level`, level + 1);
									return renderTreeNode(item);
								})}
						</span>
					</div>
					{
						<div key="container" classes={[]}>
							{" "}
							{getChildren(id)
								.filter((item) => item.resourceType !== "03")
								.map((item) => {
									icache.set(`${item.id}-level`, level + 1);
									return renderTreeNode(item);
								})}
						</div>
					}
				</div>
			);
		}

		if (item.resourceType === "03") {
			return (
				<span
					key={item.id}
					classes={[css.node, c.mx_2, c.text_muted]}
					onclick={() => {
						icache.set<string>("activeNodeId", id);
						onSelectNode && onSelectNode(id);
					}}
				>
					<div classes={[c.form_check]}>
						<input
							id={item.id}
							onclick={(event: MouseEvent<HTMLInputElement>) => {
								const checked = event.target.checked;
								const action = checked ? "add" : "remove";
								executor(prepareRoleResourcesProcess)({ resourceId: id, action });
							}}
							checked={isChecked(id)}
							classes={[c.form_check_input]}
							type="checkbox"
						/>
						<label for={item.id} classes={[c.form_check_label]}>
							{name}
						</label>
					</div>
				</span>
			);
		}

		return (
			<div key={id} classes={[]}>
				<div
					classes={[c.d_flex, css.node, c.align_items_start]}
					onclick={() => {
						icache.set<string>("activeNodeId", id);
						onSelectNode && onSelectNode(id);
					}}
				>
					{timesIndent(level)}
					<div classes={[c.form_check]}>
						<input
							id={item.id}
							checked={isChecked(id)}
							classes={[c.form_check_input]}
							onclick={(event: MouseEvent<HTMLInputElement>) => {
								const checked = event.target.checked;
								const action = checked ? "add" : "remove";
								executor(prepareRoleResourcesProcess)({ resourceId: id, action });
							}}
							type="checkbox"
						/>
						<label for={item.id} classes={[c.form_check_label]}>
							{name}
						</label>
					</div>
				</div>
			</div>
		);
	};

	const renderTree = () => {
		return (
			<virtual>
				<div classes={[c.mt_2]}>{getChildren("-1").map((item) => renderTreeNode(item))}</div>
				{resources.length === 0 && (
					<div classes={[c.text_muted, c.text_center, c.my_5]}>当前 APP 下没有配置资源！</div>
				)}
			</virtual>
		);
	};

	return <div classes={[css.root]}>{renderTree()}</div>;
});
