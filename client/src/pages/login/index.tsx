import { create, tsx, invalidator } from "@dojo/framework/core/vdom";
import icache from "@dojo/framework/core/middleware/icache";
import store from "../../store";
import FontAwesomeIcon from "dojo-fontawesome/FontAwesomeIcon";
import * as c from "bootstrap-classes";
import { loginProcess } from "../../processes/loginProcesses";
import errors from "../../middleware/errors";
import Link from "@dojo/framework/routing/Link";
import * as css from "./index.m.css";

export interface LoginProperties {}

const usernameIsBlank = "请输入用户名！";
const passwordIsBlank = "请输入密码！";

const factory = create({ icache, store, invalidator, errors }).properties<LoginProperties>();

export default factory(function Login({ properties, middleware: { icache, store, invalidator, errors } }) {
	const {} = properties();
	const { executor } = store;

	const username = icache.getOrSet<string>("username", "");
	const password = icache.getOrSet<string>("password", "");
	const serverGlobalErrors = errors.getServerGlobalError();

	const toLogin = () => {
		// 注意：
		// 在点击登录按钮时，用户名和密码输入框都要校验
		// 但只在用户名或密码输入框中输入文本时，则只校验当前输入的文本框，不要考虑其他输入框

		// 客户端校验
		// 规则一：用户名和密码不能为空
		if (username.trim() === "") {
			errors.rejectValue("username", usernameIsBlank);
		} else {
			errors.passValue("username");
		}

		if (password.trim() === "") {
			errors.rejectValue("password", passwordIsBlank);
		} else {
			errors.passValue("password");
		}

		if (errors.hasErrors()) {
			invalidator();
			return;
		}

		executor(loginProcess)({ username, password });
	};

	const handleKeyDown = (event: KeyboardEvent<HTMLInputElement>) => {
		if (event.keyCode === 13) {
			toLogin();
		}
	};

	const usernameErrors = errors.getFieldError("username");
	const passwordErrors = errors.getFieldError("password");

	const showMessage = () => {
		let valid = true;
		let message = "登录";
		if (serverGlobalErrors) {
			message = serverGlobalErrors;
			valid = false;
		}
		return <p classes={["login-box-msg", valid ? undefined : c.text_danger]}>{message}</p>;
	};

	return (
		<div key="root" classes={["login-page"]}>
			<div classes={["login-box"]}>
				<div classes={["login-logo"]}>后台管理系统</div>
				<div classes={[c.card]}>
					<div classes={[c.card_body, "login-card-body"]}>
						{showMessage()}
						<form classes={[c.needs_validation]} novalidate="novalidate">
							<div
								key="username"
								classes={[c.input_group, usernameErrors.valid ? css.marginBottom : undefined]}
							>
								<input
									type="text"
									autocomplete="username"
									placeholder="用户名"
									focus={true}
									classes={[c.form_control, usernameErrors.valid ? undefined : c.is_invalid]}
									onkeydown={handleKeyDown}
									oninput={(event: KeyboardEvent<HTMLInputElement>) => {
										const username = event.target.value;
										if (username.trim() === "") {
											errors.rejectValue("username", usernameIsBlank);
										} else {
											errors.passValue("username");
										}
										icache.set("username", username);
									}}
								/>
								<div classes={[c.input_group_append]}>
									<div classes={[c.input_group_text]}>
										<FontAwesomeIcon icon="user" />
									</div>
								</div>
								{!usernameErrors.valid && (
									<div key="error" classes={[c.invalid_feedback, css.error]}>
										{usernameErrors.message}
									</div>
								)}
							</div>
							<div
								key="password"
								classes={[c.input_group, passwordErrors.valid ? css.marginBottom : undefined]}
							>
								<input
									type="password"
									autocomplete="current-password"
									placeholder="密码"
									classes={[c.form_control, passwordErrors.valid ? undefined : c.is_invalid]}
									oninput={(event: KeyboardEvent<HTMLInputElement>) => {
										const password = event.target.value;
										if (password.trim() === "") {
											errors.rejectValue("password", passwordIsBlank);
										} else {
											errors.passValue("password");
										}
										icache.set("password", password);
									}}
									onkeydown={handleKeyDown}
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
							<div classes={[c.row]}>
								<div classes={[c.col_8]}>
									{
										// <div classes={["icheck-primary"]}>
										// <input type="checkbox" id="remember" />
										// <label for="remember">记住我</label>
										// </div>
									}
								</div>
								<div classes={[c.col_4]}>
									<button
										type="button"
										classes={[c.btn, c.btn_primary, c.btn_block]}
										onclick={() => {
											toLogin();
										}}
									>
										登录
									</button>
								</div>
							</div>
						</form>
						<p classes={[c.mb_0]}>
							还没有帐号？<Link to="register">立即注册</Link>
						</p>
					</div>
				</div>
			</div>
		</div>
	);
});
