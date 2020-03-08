import { create, tsx, invalidator } from '@dojo/framework/core/vdom';
import icache from '@dojo/framework/core/middleware/icache';
import store from '../../store';
import FontAwesomeIcon from 'dojo-fontawesome/FontAwesomeIcon';
import * as c from "bootstrap-classes";
import { loginProcess } from '../../processes/loginProcesses';
import errors from "../../middleware/errors";
import Link from '@dojo/framework/routing/Link';

export interface LoginProperties {

}

const factory = create({ icache, store, invalidator, errors }).properties<LoginProperties>();

export default factory(function Login({ properties, middleware: { icache, store, invalidator, errors } }) {
    const { } = properties();
    const { executor } = store;

    const toLogin = () => {
        const username = icache.getOrSet<string>("username", "");
        const password = icache.getOrSet<string>("password", "");

        // 客户端校验
        // 规则一：用户名和密码不能为空
        if (username.trim() === "") {
            errors.rejectValue("username", "请输入用户名！");
        }
        if (password.trim() === "") {
            errors.rejectValue("password", "请输入密码！");
        }

        if (errors.hasErrors()) {
            invalidator();
            return;
        }

        executor(loginProcess)({ username, password });
    }

    const handleKeyDown = (event: KeyboardEvent<HTMLInputElement>) => {
        if (event.keyCode === 13) {
            toLogin();
        }
    }

    const usernameValidity = errors.getError("username");
    const passwordValidity = errors.getError("password");

    return (
        <div classes={["login-page"]} >
            <div classes={["login-box"]}>
                <div classes={["login-logo"]}>
                    后台管理系统
                </div>
                <div classes={[c.card]}>
                    <div classes={[c.card_body, "login-card-body"]}>
                        <p classes={["login-box-msg"]}>登录</p>
                        <form classes={[c.needs_validation]} novalidate="novalidate">
                            <div classes={[c.input_group, c.mb_3]}>
                                <input
                                    type="text"
                                    autocomplete="username"
                                    placeholder="用户名"
                                    focus={true}
                                    classes={[c.form_control, usernameValidity.valid ? undefined : c.is_invalid]}
                                    onkeydown={handleKeyDown} 
                                    oninput={(event: KeyboardEvent<HTMLInputElement>) => {
                                        const value = event.target.value;
                                        if (value.trim() === "") {
                                            errors.rejectValue("username", "请输入用户名！");
                                        } else {
                                            errors.passValue("username");
                                        }
                                        icache.set("username", value);
                                    }}
                                />
                                <div classes={[c.input_group_append]}>
                                    <div classes={[c.input_group_text]}>
                                        <FontAwesomeIcon icon="user" />
                                    </div>
                                </div>
                                {!usernameValidity.valid && <div classes={[c.invalid_feedback]}>{usernameValidity.message}</div>}
                            </div>

                            <div classes={[c.input_group, c.mb_3]}>
                                <input
                                    type="password"
                                    autocomplete="current-password"
                                    placeholder="密码"
                                    classes={[c.form_control, passwordValidity.valid ? undefined : c.is_invalid]}
                                    oninput={(event: KeyboardEvent<HTMLInputElement>) => {
                                        const value = event.target.value;
                                        if (value.trim() === "") {
                                            errors.rejectValue("password", "请输入密码！");
                                        } else {
                                            errors.passValue("password");
                                        }

                                        icache.set("password", value);
                                    }}
                                    onkeydown={handleKeyDown} />
                                <div classes={[c.input_group_append]}>
                                    <div classes={[c.input_group_text]}>
                                        <FontAwesomeIcon icon="lock" />
                                    </div>
                                </div>
                                {!passwordValidity.valid && <div classes={[c.invalid_feedback]}>{passwordValidity.message}</div>}
                            </div>

                            <div classes={[c.row]}>
                                <div classes={[c.col_8]}>
                                    <div classes={["icheck-primary"]}>
                                        <input type="checkbox" id="remember" />
                                        <label for="remember">记住我</label>

                                    </div>
                                </div>
                                <div classes={[c.col_4]}>
                                    <button type="button" classes={[c.btn, c.btn_primary, c.btn_block]} onclick={() => { toLogin(); }}>登录</button>
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
