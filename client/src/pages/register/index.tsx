import { create, tsx, invalidator } from "@dojo/framework/core/vdom";
import FontAwesomeIcon from "dojo-fontawesome/FontAwesomeIcon";
import icache from "@dojo/framework/core/middleware/icache";
import store from "../../store";
import errors from "../../middleware/errors";
import * as c from "bootstrap-classes";
import Link from "@dojo/framework/routing/Link";
import { registerProcess, checkUsernameProcess } from "../../processes/loginProcesses";
import * as css from "./index.m.css";

export interface indexProperties {}

const usernameIsBlank = "请输入用户名！";
const passwordIsBlank = "请输入密码！";
const retryPasswordIsBlank = "请确认密码！";
const passwordIsNotMatch = "两次输入的密码不匹配!";

const factory = create({ icache, store, invalidator, errors }).properties<indexProperties>();
export default factory(function Register({ properties, middleware: { icache, store, invalidator, errors } }) {
	const {} = properties();
	const { executor } = store;

	const username = icache.getOrSet<string>("username", "");
	const password = icache.getOrSet<string>("password", "");
	const retryPassword = icache.getOrSet<string>("retryPassword", "");
	const serverUsernameErrors = errors.getServerFieldError("username");

	const toRegister = () => {
		// 客户端校验
		// 规则一：用户名和密码不能为空
		if (username.trim() === "") {
			errors.rejectValue("username", usernameIsBlank);
		} else {
			errors.passValue("username");
		}
		if (password === "") {
			errors.rejectValue("password", passwordIsBlank);
		} else {
			errors.passValue("password");
		}

		if (retryPassword === "") {
			errors.rejectValue("retryPassword", retryPasswordIsBlank);
		} else if (password !== retryPassword) {
			errors.rejectValue("retryPassword", passwordIsNotMatch);
		} else {
			errors.passValue("retryPassword");
		}

		if (errors.hasErrors()) {
			invalidator();
			return;
		}

		executor(registerProcess)({ username, password });
	};

	const handleKeyDown = (event: KeyboardEvent<HTMLInputElement>) => {
		if (event.keyCode === 13) {
			toRegister();
		}
	};

	const clientUsernameErrors = errors.getFieldError("username");
	const passwordErrors = errors.getFieldError("password");
	const retryPasswordErrors = errors.getFieldError("retryPassword");

	const usernameErrors = {
		valid: clientUsernameErrors.valid && serverUsernameErrors.valid,
		message: !serverUsernameErrors.valid ? serverUsernameErrors.message : clientUsernameErrors.message,
	};

	return (
		<div key="root" classes={["register-page"]}>
			<div classes={["register-box"]}>
				<div classes={["register-logo"]}>后台管理系统</div>
				<div classes={[c.card]}>
					<div classes={[c.card_body, "register-card-body"]}>
						<p classes={["login-box-msg"]}>注册用户</p>
						<form classes={[c.needs_validation]} novalidate="novalidate">
							<div classes={[c.input_group, usernameErrors.valid ? css.marginBottom : undefined]}>
								<input
									type="text"
									autocomplete="username"
									placeholder="用户名"
									focus={true}
									classes={[c.form_control, usernameErrors.valid ? undefined : c.is_invalid]}
									onkeydown={handleKeyDown}
									oninput={(event: KeyboardEvent<HTMLInputElement>) => {
										errors.clearServerFieldError("username");
										const value = event.target.value.trim();
										if (value === "") {
											errors.rejectValue("username", usernameIsBlank);
											icache.set("username", value);
											return;
										}

										errors.passValue("username");
										icache.set("username", value);
										executor(checkUsernameProcess)({ username: value });
									}}
								/>
								<div classes={[c.input_group_append]}>
									<div classes={[c.input_group_text]}>
										<FontAwesomeIcon icon="user" />
									</div>
								</div>
								{!usernameErrors.valid && (
									<div
										key="error"
										classes={[c.invalid_feedback, css.error]}
										innerHTML={usernameErrors.message}
									></div>
								)}
							</div>
							<div classes={[c.input_group, passwordErrors.valid ? css.marginBottom : undefined]}>
								<input
									type="password"
									autocomplete="current-password"
									placeholder="密码"
									classes={[c.form_control, passwordErrors.valid ? undefined : c.is_invalid]}
									onkeydown={handleKeyDown}
									oninput={(event: KeyboardEvent<HTMLInputElement>) => {
										const value = event.target.value;
										if (value === "") {
											errors.rejectValue("password", passwordIsBlank);
										} else {
											errors.passValue("password");
										}
										if (value !== retryPassword) {
											errors.rejectValue("retryPassword", passwordIsNotMatch);
										}
										icache.set("password", value);
									}}
								/>
								<div classes={[c.input_group_append]}>
									<div classes={[c.input_group_text]}>
										<FontAwesomeIcon icon="lock" />
									</div>
								</div>
								{!passwordErrors.valid && (
									<div key="error" classes={[c.invalid_feedback, css.error]}>
										{passwordErrors.message}
									</div>
								)}
							</div>
							<div classes={[c.input_group, retryPasswordErrors.valid ? css.marginBottom : undefined]}>
								<input
									type="password"
									classes={[c.form_control, retryPasswordErrors.valid ? undefined : c.is_invalid]}
									placeholder="确认密码"
									onkeydown={handleKeyDown}
									oninput={(event: KeyboardEvent<HTMLInputElement>) => {
										const value = event.target.value;
										if (value === "") {
											errors.rejectValue("retryPassword", retryPasswordIsBlank);
										} else if (value !== password) {
											errors.rejectValue("retryPassword", passwordIsNotMatch);
										} else {
											errors.passValue("retryPassword");
										}

										icache.set("retryPassword", value);
									}}
								/>
								<div classes={[c.input_group_append]}>
									<div classes={[c.input_group_text]}>
										<FontAwesomeIcon icon="lock" />
									</div>
								</div>
								{!retryPasswordErrors.valid && (
									<div key="error" classes={[c.invalid_feedback, css.error]}>
										{retryPasswordErrors.message}
									</div>
								)}
							</div>
							<div classes={[c.row]}>
								<div classes={[c.col_8]}></div>
								<div classes={[c.col_4]}>
									<button
										type="button"
										classes={[c.btn, c.btn_primary, c.btn_block]}
										onclick={() => {
											toRegister();
										}}
									>
										注册
									</button>
								</div>
							</div>
						</form>
						已有帐号，<Link to="login">直接登录</Link>
					</div>
				</div>
			</div>
		</div>
	);
});
