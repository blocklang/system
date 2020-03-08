import { create, tsx, invalidator } from '@dojo/framework/core/vdom';
import FontAwesomeIcon from 'dojo-fontawesome/FontAwesomeIcon';
import icache from '@dojo/framework/core/middleware/icache';
import store from '../../store';
import errors from "../../middleware/errors";
import * as c from "bootstrap-classes";
import Link from '@dojo/framework/routing/Link';
import { registerProcess } from '../../processes/loginProcesses';

export interface indexProperties {
}

const factory = create({icache, store, invalidator, errors}).properties<indexProperties>();

export default factory(function Register({ properties, middleware: {icache, store, invalidator, errors} }){
    const {  } = properties();
    const { executor } = store;

    const username = icache.getOrSet<string>("username", "");
    const password = icache.getOrSet<string>("password", "");
    const retryPassword = icache.getOrSet<string>("retryPassword", "");

    const toRegister = () => {
        // 客户端校验
        // 规则一：用户名和密码不能为空
        if (username.trim() === "") {
            errors.rejectValue("username", "请输入用户名！");
        }
        if (password === "") {
            errors.rejectValue("password", "请输入密码！");
        }
        if (retryPassword === "") {
            errors.rejectValue("retryPassword", "请确认密码！");
        } else if(password !== retryPassword) {
            errors.rejectValue("retryPassword", "两次输入的密码不匹配!");
        }

        if (errors.hasErrors()) {
            invalidator();
            return;
        }

        executor(registerProcess)({ username, password });
    }

    const handleKeyDown = (event: KeyboardEvent<HTMLInputElement>) => {
        if (event.keyCode === 13) {
            toRegister();
        }
    }

    const usernameValidity = errors.getError("username");
    const passwordValidity = errors.getError("password");
    const retryPasswordValidity = errors.getError("retryPassword");

    return (
        <div classes={["register-page"]}>
            <div classes={["register-box"]}>
                <div classes={["register-logo"]}>
                    后台管理系统
                </div>
                <div classes={[c.card]}>
                    <div classes={[c.card_body, "register-card-body"]}>
                        <p classes={["login-box-msg"]}>注册用户</p>
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
                                        <FontAwesomeIcon icon="user"/>
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
                                    onkeydown={handleKeyDown}
                                    oninput={(event: KeyboardEvent<HTMLInputElement>) => {
                                        const value = event.target.value;
                                        if (value.trim() === "") {
                                            errors.rejectValue("password", "请输入密码！");
                                        } else {
                                            errors.passValue("password");
                                        }

                                        if(value !== retryPassword) {
                                            errors.rejectValue("retryPassword", "两次输入的密码不匹配!");
                                        }else {
                                            errors.passValue("retryPassword");
                                        }

                                        icache.set("password", value);
                                    }}
                                />
                                <div classes={[c.input_group_append]}>
                                    <div classes={[c.input_group_text]}>
                                        <FontAwesomeIcon icon="lock"/>
                                    </div>
                                </div>
                                {!passwordValidity.valid && <div classes={[c.invalid_feedback]}>{passwordValidity.message}</div>}
                            </div>
                            <div classes={[c.input_group, c.mb_3]}>
                                <input 
                                    type="password" 
                                    classes={[c.form_control, retryPasswordValidity.valid ? undefined : c.is_invalid]} 
                                    placeholder="确认密码" 
                                    onkeydown={handleKeyDown}
                                    oninput={(event: KeyboardEvent<HTMLInputElement>) => {
                                        const value = event.target.value;
                                        if (value.trim() === "") {
                                            errors.rejectValue("retryPassword", "请确认密码！");
                                        } else if(value !== password) {
                                            errors.rejectValue("retryPassword", "两次输入的密码不匹配!");
                                        }else {
                                            errors.passValue("retryPassword");
                                        }

                                        icache.set("retryPassword", value);
                                    }}
                                />
                                <div classes={[c.input_group_append]}>
                                    <div classes={[c.input_group_text]}>
                                        <FontAwesomeIcon icon="lock"/>
                                    </div>
                                </div>
                                {!retryPasswordValidity.valid && <div classes={[c.invalid_feedback]}>{retryPasswordValidity.message}</div>}
                            </div>
                            <div classes={[c.row]}>
                                <div classes={[c.col_8]}>
                                </div>
                                <div classes={[c.col_4]}>
                                    <button type="button" classes={[c.btn, c.btn_primary, c.btn_block]} onclick={()=>{toRegister();}}>注册</button>
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
