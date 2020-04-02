import { create, tsx } from "@dojo/framework/core/vdom";
import store from "../../store";
import { ValidateStatus } from "../../constant";
import * as c from "bootstrap-classes";
import { setRoleFieldProcess, updateRoleProcess } from "../../processes/roleProcesses";
import { changeViewProcess } from "../../processes/pageProcesses";

export interface EditProperties {}

const factory = create({ store }).properties<EditProperties>();

export default factory(function Edit({ properties, middleware: { store } }) {
	const {} = properties();
	const { get, path, executor } = store;
	const role = get(path("role"));
	const { appName, name, description } = role || {};

	const formValidation = get(path("formValidation")) || {};
	const showInvalidMessage = (field: string) => {
		const { status = ValidateStatus.UNVALIDATED, message = "" } = formValidation[field] || {};
		if (status === ValidateStatus.INVALID) {
			return <div classes={[c.invalid_tooltip]} innerHTML={message}></div>;
		}
	};
	const showValidationClass = (field: string) => {
		const { status = ValidateStatus.UNVALIDATED } = formValidation[field] || {};
		if (status === ValidateStatus.INVALID) {
			return c.is_invalid;
		}
	};

	return (
		<div classes={[c.container_fluid]}>
			<div classes={[c.card]}>
				<div classes={[c.card_header]}>
					<h3 classes={[c.card_title]}>编辑角色</h3>
				</div>
				<form role="form" classes={[c.needs_validation]} novalidate={true}>
					<div classes={[c.card_body]}>
						<div classes={[c.form_group]}>
							<label for="txtApp">APP</label>
							<input
								type="text"
								classes={[c.form_control]}
								value={appName}
								id="txtApp"
								readonly="readonly"
							/>
						</div>
						<div classes={[c.form_group, c.position_relative]}>
							<label for="iptName">
								角色名<small classes={[c.text_muted, c.ml_1]}>必填</small>
							</label>
							<input
								type="text"
								value={name}
								classes={[c.form_control, showValidationClass("name")]}
								id="iptName"
								focus={true}
								oninput={(event: KeyboardEvent<HTMLInputElement>) => {
									executor(setRoleFieldProcess)({ field: "name", value: event.target.value });
								}}
							/>
							{showInvalidMessage("name")}
						</div>
						<div classes={[c.form_group]}>
							<label for="iptDescription">描述</label>
							<input
								type="text"
								value={description}
								classes={[c.form_control]}
								id="iptDescription"
								oninput={(event: KeyboardEvent<HTMLInputElement>) => {
									executor(setRoleFieldProcess)({ field: "description", value: event.target.value });
								}}
							/>
						</div>
					</div>
					<div classes={[c.card_footer]}>
						<button
							type="button"
							classes={[c.btn, c.btn_secondary, c.mr_2]}
							onclick={() => {
								// 要保留在原页面
								executor(changeViewProcess)({ view: "list" });
							}}
						>
							取消
						</button>
						<button
							type="button"
							classes={[c.btn, c.btn_primary]}
							onclick={() => {
								// 更新成功之后要跳转，并在刷新列表
								executor(updateRoleProcess)({});
							}}
						>
							保存
						</button>
					</div>
				</form>
			</div>
		</div>
	);
});
