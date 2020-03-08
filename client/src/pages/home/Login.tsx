import { create, tsx, invalidator } from '@dojo/framework/core/vdom';
import icache from '@dojo/framework/core/middleware/icache';
import store from '../../store';
import * as c from "bootstrap-classes";
import * as css from "./Login.m.css";
import { loginProcess } from '../../processes/loginProcesses';
import errors from "../../middleware/errors";

export interface LoginProperties {
    
}

const factory = create({ icache, store,invalidator,  errors }).properties<LoginProperties>();

export default factory(function Login({ properties, middleware: { icache, store,invalidator, errors } }) {
    const { } = properties();
    const { executor } = store;

    const toLogin = () => {
        const loginName = icache.getOrSet<string>("loginName", "");
        const password = icache.getOrSet<string>("password", "");

        // 客户端校验
        // 规则一：用户名和密码不能为空
        if(loginName.trim() === "") {
            errors.rejectValue("loginName", "请输入用户名！");
        }
        if(password.trim() === "") {
            errors.rejectValue("password", "请输入密码！");
        }
        
        if(errors.hasErrors()){
            invalidator();
            return;
        }

        executor(loginProcess)({loginName, password});
    }

    const handleKeyDown = (event: KeyboardEvent<HTMLInputElement>) => {
        if(event.keyCode === 13) {
            toLogin();
        }
    }

    const loginNameValidity = errors.getError("loginName");
    const passwordValidity = errors.getError("password");

    return (
        <div>
            <div>
                <div classes={[c.ml_5]}>
                    <h1 classes={[c.my_5, c.text_center]}>Block Lang 后台管理系统</h1>
                </div>
                <div classes={[css.form, c.mx_auto]}>
                    <form classes={[c.needs_validation]} novalidate="novalidate">
                        <div classes={[c.form_group, c.position_relative]}>
                            <label for="loginName">用户名</label>
                            <input 
                                type="input" 
                                id="loginName"
                                autocomplete="username"
                                focus={true} 
                                classes={[c.form_control, loginNameValidity.valid? undefined : c.is_invalid]}
                                oninput={(event: KeyboardEvent<HTMLInputElement>) => {
                                    const value = event.target.value;
                                    if(value.trim() === "") {
                                        errors.rejectValue("loginName", "请输入用户名！");
                                    } else {
                                        errors.passValue("loginName");
                                    }
                                    icache.set("loginName", value);
                                }}
                                onkeydown={handleKeyDown}/>
                            {!loginNameValidity.valid && <div classes={[c.invalid_tooltip]}>{loginNameValidity.message}</div>}
                        </div>
                        <div classes={[c.form_group, c.position_relative]}>
                            <label for="password">密码</label>
                            <input 
                                type="password" 
                                id="password" 
                                autocomplete="current-password"
                                classes={[c.form_control, passwordValidity.valid? undefined : c.is_invalid]} 
                                oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                    const value = event.target.value;
                                    if(value.trim() === "") {
                                        errors.rejectValue("password", "请输入密码！");
                                    }else {
                                        errors.passValue("password");
                                    }

                                    icache.set("password", value);
                                }}
                                onkeydown={handleKeyDown}/>
                            {!passwordValidity.valid && <div classes={[c.invalid_tooltip]}>{passwordValidity.message}</div>}
                        </div>

                        <button type="button" classes={[c.btn, c.btn_primary, c.btn_block, c.mt_5]} onclick={() => {toLogin();}}>登 录</button>
                    </form>
                </div>

            </div>
        </div>
    );
});
