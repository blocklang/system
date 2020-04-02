import { create, tsx } from "@dojo/framework/core/vdom";
import * as c from "bootstrap-classes";
import store from "../../store";
import icache from "@dojo/framework/core/middleware/icache";
import { changeViewProcess } from "../../processes/pageProcesses";
import { getPagedUserProcess } from "../../processes/userProcesses";
import Pagination from "../../widgets/Pagination";
import { defaultPagination } from "../../config";
import { addUserToRoleProcess, removeUserFromRoleProcess } from "../../processes/userRoleProcesses";
import { findIndex } from "@dojo/framework/shim/array";

export interface RoleUsersProperties {}

const factory = create({ store, icache }).properties<RoleUsersProperties>();

// 用户列表，默认根据用户名排序，要排除掉系统管理员
// 如果用户已添加，则在用户列表中不删除，标记出已添加即可

export default factory(function RoleUsers({ properties, middleware: { store, icache } }) {
	const {} = properties();
	const { get, path, executor } = store;
	const role = get(path("role")) || {};
	const { id: roleId, name: roleName } = role;
	const roleUsers = get(path("users")) || [];
	const pagedUser = get(path("pagedUser"));
	if (!pagedUser) {
		executor(getPagedUserProcess)({ page: 0, excludeAdmin: true });
	}
	const { content: users, first, last, totalElements, number, size, numberOfElements } =
		pagedUser || defaultPagination;
	const view = icache.get("view") || "view"; // view | edit

	function isAssigned(userId: string) {
		return findIndex(roleUsers, (item) => item.id === userId) > -1;
	}

	return (
		<div classes={[c.container_fluid]}>
			<div classes={[c.card]}>
				<div classes={[c.card_header]}>
					<h3 classes={[c.card_title]}>
						<strong>{roleName}</strong>: {`${roleUsers.length}`} 个用户
					</h3>
					<div classes={["form-check", "form-check-inline", c.ml_3]}>
						<input
							type="checkbox"
							class="form-check-input"
							id="cbxAssign"
							onclick={(event: MouseEvent<HTMLInputElement>) => {
								const checked = event.target.checked;
								const view = checked ? "edit" : "view";
								icache.set("view", view);
							}}
						/>
						<label class="form-check-label" for="cbxAssign">
							分配
						</label>
					</div>
					<a
						href="#"
						classes={[c.float_right]}
						onclick={(event: MouseEvent) => {
							event.stopPropagation();
							event.preventDefault();
							executor(changeViewProcess)({ view: "list" });
						}}
					>
						取消
					</a>
				</div>
				{view === "view" && (
					<table classes={[c.table, c.table_bordered, c.table_hover]}>
						<thead>
							<tr>
								<th>登录名</th>
								<th>用户名</th>
								<th>部门</th>
								<th>性别</th>
								<th>手机</th>
							</tr>
						</thead>
						<tbody>
							{roleUsers.length === 0 ? (
								<tr key="empty">
									<td colspan="5" classes={[c.text_center, c.text_muted]}>
										没有分配用户！
									</td>
								</tr>
							) : (
								roleUsers.map((user) => (
									<tr key={user.id}>
										<td key="username">{user.username}</td>
										<td key="nickname">{user.nickname}</td>
										<td key="dept">{user.deptName}</td>
										<td key="sex">{user.sex === "1" ? "男" : "女"}</td>
										<td key="phoneNumber">{user.phoneNumber}</td>
									</tr>
								))
							)}
						</tbody>
					</table>
				)}
				{view === "edit" && (
					<virtual>
						<table classes={[c.table, c.table_bordered, c.table_hover]}>
							<thead>
								<tr>
									<th styles={{ width: "60px" }}>选择</th>
									<th>登录名</th>
									<th>用户名</th>
									<th>部门</th>
									<th>性别</th>
									<th>手机</th>
								</tr>
							</thead>
							<tbody>
								{users.length === 0 ? (
									<tr key="empty">
										<td colspan="6" classes={[c.text_center, c.text_muted]}>
											没有用户！
										</td>
									</tr>
								) : (
									users.map((user) => (
										<tr key={user.id}>
											<td>
												<input
													type="checkbox"
													checked={isAssigned(user.id)}
													onclick={(event: MouseEvent<HTMLInputElement>) => {
														const checked = event.target.checked;
														if (checked) {
															executor(addUserToRoleProcess)({ roleId, userId: user.id });
														} else {
															executor(removeUserFromRoleProcess)({
																roleId,
																userId: user.id,
															});
														}
													}}
												/>
											</td>
											<td key="username">{user.username}</td>
											<td key="nickname">{user.nickname}</td>
											<td key="dept">{user.deptName}</td>
											<td key="sex">{user.sex === "1" ? "男" : "女"}</td>
											<td key="phoneNumber">{user.phoneNumber}</td>
										</tr>
									))
								)}
							</tbody>
						</table>
						<div classes={[c.px_2]}>
							<Pagination
								first={first}
								last={last}
								number={number}
								size={size}
								numberOfElements={numberOfElements}
								totalElements={totalElements}
								onPageChanged={(page) => {
									executor(getPagedUserProcess)({ page });
								}}
							/>
						</div>
					</virtual>
				)}
			</div>
		</div>
	);
});
