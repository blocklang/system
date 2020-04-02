import { create, tsx } from "@dojo/framework/core/vdom";
import store from "../../store";
import * as c from "bootstrap-classes";
import { IconName } from "@fortawesome/fontawesome-svg-core";
import FontAwesomeIcon from "dojo-fontawesome/FontAwesomeIcon";
import { changeViewProcess } from "../../processes/pageProcesses";
import { getResourceProcess, resetResourceProcess } from "../../processes/resourceProcesses";
import { ResourceType } from "../../interfaces";
import { find } from "@dojo/framework/shim/array";

export interface ListProperties {
	appId: string;
	selectedNodeId: string;
}

const factory = create({ store }).properties<ListProperties>();

export default factory(function List({ properties, middleware: { store } }) {
	const { appId, selectedNodeId } = properties();
	const { get, path, executor } = store;
	const resources = get(path("resources")) || [];

	const selectedNode = find(resources, (res) => res.id === selectedNodeId);
	if (selectedNode && selectedNode.resourceType === "03") {
		return (
			<div key="operator" classes={[c.text_muted, c.text_center, c.mt_5, c.pb_5]}>
				按钮资源下不允许配置资源
			</div>
		);
	}

	const children = resources.filter((item) => item.parentId === selectedNodeId);
	// TODO: 改为从系统参数中读取
	function getResourceTypeName(resourceType: ResourceType) {
		if (resourceType === "01") {
			return "目录";
		}
		if (resourceType === "02") {
			return "菜单";
		}
		return "按钮";
	}

	return (
		<div classes={[c.container_fluid]}>
			<div classes={[c.d_flex, c.justify_content_end, c.mb_2]}>
				<button
					type="button"
					classes={[c.btn, c.btn_primary]}
					disabled={selectedNodeId == undefined}
					onclick={() => {
						executor(changeViewProcess)({ view: "new" });
						executor(resetResourceProcess)({ appId, parentId: selectedNodeId, resourceType: "02" });
					}}
				>
					<FontAwesomeIcon icon="plus" /> 新增
				</button>
			</div>
			<table classes={[c.table, c.table_bordered, c.table_hover]}>
				<thead>
					<tr>
						<th>名称</th>
						<th>类型</th>
						<th>权限标识</th>
						<th>url</th>
						<th>图标</th>
						<th>操作</th>
					</tr>
				</thead>
				<tbody>
					{children.length === 0 ? (
						<tr key="empty">
							<td colspan="6" classes={[c.text_center, c.text_muted]}>{`没有配置资源！`}</td>
						</tr>
					) : (
						children.map((item) => {
							return (
								<tr key={item.id}>
									<td key="name">{item.name}</td>
									<td key="resourceType">{getResourceTypeName(item.resourceType)}</td>
									<td key="auth">{item.auth}</td>
									<td key="url">{item.url}</td>
									<td key="icon">
										<FontAwesomeIcon icon={item.icon as IconName} /> {item.icon}
									</td>
									<td key="operators">
										<button
											type="button"
											classes={[c.btn, c.btn_secondary, c.btn_sm]}
											onclick={() => {
												executor(changeViewProcess)({ view: "edit" });
												executor(getResourceProcess)({ id: item.id });
											}}
										>
											编辑
										</button>
									</td>
								</tr>
							);
						})
					)}
				</tbody>
			</table>
		</div>
	);
});
