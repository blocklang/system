import { create, tsx } from "@dojo/framework/core/vdom";
import store from "../../store";
import * as c from "bootstrap-classes";
import FontAwesomeIcon from "dojo-fontawesome/FontAwesomeIcon";
import { getPagedAppProcess, getAppProcess, resetAppProcess } from "../../processes/appProcesses";
import { defaultPagination } from "../../config";
import * as moment from "moment";
import { changeViewProcess } from "../../processes/pageProcesses";
import Pagination from "../../widgets/Pagination";

export interface ListProperties {}

const factory = create({ store }).properties<ListProperties>();

export default factory(function List({ properties, middleware: { store } }) {
	const { get, path, executor } = store;
	const pagedApp = get(path("pagedApp"));
	if (!pagedApp) {
		// 页面初始化时加载指定页数的列表, 默认加载第一页
		executor(getPagedAppProcess)({ page: 0 });
	}

	const { content: apps, empty, first, last, number, size, numberOfElements, totalElements } =
		pagedApp || defaultPagination;

	return (
		<div classes={[c.container_fluid]}>
			<div classes={[c.d_flex, c.justify_content_end, c.mb_2]}>
				<button
					type="button"
					classes={[c.btn, c.btn_primary]}
					onclick={() => {
						executor(changeViewProcess)({ view: "new" });
						executor(resetAppProcess)({});
					}}
				>
					<FontAwesomeIcon icon="plus" /> 新增
				</button>
			</div>
			<table classes={[c.table, c.table_bordered, c.table_hover]}>
				<thead>
					<tr>
						<th>名称</th>
						<th>图标</th>
						<th>url</th>
						<th>描述</th>
						<th>创建时间</th>
						<th>操作</th>
					</tr>
				</thead>
				<tbody>
					{empty ? (
						<tr key="empty">
							<td colspan="6" classes={[c.text_center, c.text_muted]}>
								没有记录！
							</td>
						</tr>
					) : (
						apps.map((app) => {
							return (
								<tr key={app.id}>
									<td key="name">{app.name}</td>
									<td key="icon">{app.icon}</td>
									<td key="url">{app.url}</td>
									<td key="description">{app.description}</td>
									<td key="createTime">{moment(app.createTime).format("YYYY-MM-DD h:mm")}</td>
									<td key="operators">
										<button
											type="button"
											classes={[c.btn, c.btn_secondary, c.btn_sm]}
											onclick={() => {
												executor(changeViewProcess)({ view: "edit" });
												executor(getAppProcess)({ id: app.id });
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
			<Pagination
				first={first}
				last={last}
				number={number}
				size={size}
				numberOfElements={numberOfElements}
				totalElements={totalElements}
				onPageChanged={(page) => {
					executor(getPagedAppProcess)({ page });
				}}
			/>
		</div>
	);
});
