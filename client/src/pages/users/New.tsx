import { create, tsx } from '@dojo/framework/core/vdom';
import * as c from 'bootstrap-classes';
import {Toast} from "../../utils/sweetalert2";
import store from '../../store';
import { clearGlobalTipProcess, changeViewProcess } from '../../processes/pageProcesses';
import { ValidateStatus } from '../../constant';
import { setUserFieldProcess, getPagedUserProcess, saveUserProcess } from '../../processes/userProcesses';
import "bootstrap";
import * as $ from "jquery";
import Tree from '../depts/Tree';

export interface NewProperties{ }

const factory = create({ store }).properties<NewProperties>();

export default factory(function New({ properties, middleware: { store } }){
    const { } = properties();
    const {get, path, executor} = store;

    const globalTip = get(path("globalTip"));
    if(globalTip) {
        Toast.fire(globalTip);
        executor(clearGlobalTipProcess)({});
    }

    const userInfo = get(path("user")) || {};
    const {username="", password = "", nickname="", sex, phoneNumber="", deptName=""} = userInfo;

    const formValidation = get(path("formValidation")) || {};
    const showInvalidMessage = (field: string) => {
        const {status=ValidateStatus.UNVALIDATED, message=""} = formValidation[field] || {};
        if(status === ValidateStatus.INVALID) {
            return <div classes={[c.invalid_tooltip]} innerHTML={message}></div>
        }
    }

    const showValidationClass = (field: string) => {
        const {status = ValidateStatus.UNVALIDATED} = formValidation[field] || {};
        if (status === ValidateStatus.INVALID) {
            return c.is_invalid;
        }
    }
    
    return (
        <div classes={[c.container_fluid]}>
            <div classes={[c.card]}>
                <div classes={[c.card_header]}>
                    <h3 classes={[c.card_title]}>新建用户</h3>
                </div>
                <form role="form" classes={[c.needs_validation]} novalidate={true}>
                    <div classes={[c.card_body]}>
                        <div classes={[c.form_group, c.position_relative]}>
                            <label for="iptUsername">登录名<small classes={[c.text_muted, c.ml_1]}>必填</small></label>
                            <input type="text" value={username} classes={[c.form_control, showValidationClass("username")]} id="iptUsername" focus={true} oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                executor(setUserFieldProcess)({field: "username", value: event.target.value});
                            }}/>
                            {showInvalidMessage("username")}
                        </div>
                        <div classes={[c.form_group, c.position_relative]}>
                            <label for="iptPassword">密码<small classes={[c.text_muted, c.ml_1]}>必填</small></label>
                            <input type="text" value={password} classes={[c.form_control, showValidationClass("password")]} id="iptPassword" oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                executor(setUserFieldProcess)({field: "password", value: event.target.value});
                            }}/>
                            {showInvalidMessage("password")}
                        </div>
                        <div classes={[c.form_group]}>
                            <label for="iptNickname">用户名</label>
                            <input type="text" value={nickname} classes={[c.form_control]} id="iptNickname" oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                executor(setUserFieldProcess)({field: "nickname", value: event.target.value});
                            }}/>
                        </div>
                        <div classes={[c.form_group]}>
                            <label for="iptDept">部门</label>
                            <div class="dropdown">
                                <button 
                                    classes={["btn", "btn-secondary","dropdown-toggle"]}
                                    type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"
                                    onclick = {(event: MouseEvent) => {
                                        ($(event.srcElement!) as any).dropdown();
                                    }}>
                                    {deptName===""?"请选择":deptName}
                                </button>
                                <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                                    <Tree onSelectNode={(id, label="")=>{
                                        executor(setUserFieldProcess)({field: "deptId", value: id});
                                        executor(setUserFieldProcess)({field: "deptName", value: label});
                                    }}/>
                                </div>
                            </div>
                        </div>
                        <div classes={[c.form_group]}>
                            <label>性别</label>
                            <div>
                                <div classes={[c.form_check, c.form_check_inline]}>
                                    <input checked={sex === "1"} classes={[c.form_check_input]} type="radio" name="sex" id="rdoMale" value="1" onclick={()=>{
                                        executor(setUserFieldProcess)({field: "sex", value: "1"});
                                    }}/>
                                    <label classes={[c.form_check_label]} for="rdoMale">男</label>
                                </div>
                                <div classes={[c.form_check, c.form_check_inline]}>
                                    <input checked={sex === "2"} classes={[c.form_check_input]} type="radio" name="sex" id="rdoFeMale" value="2" onclick={()=>{
                                        executor(setUserFieldProcess)({field: "sex", value: "2"});
                                    }}/>
                                    <label classes={[c.form_check_label]} for="rdoFeMale">女</label>
                                </div>
                            </div>
                        </div>
                        <div classes={[c.form_group]}>
                            <label for="iptPhoneNumber">手机号码</label>
                            <input type="text" value={phoneNumber} classes={[c.form_control]} id="iptPhoneNumber" maxLength="11" oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                executor(setUserFieldProcess)({field: "phoneNumber", value: event.target.value});
                            }}/>
                        </div>
                    </div>
                    <div classes={[c.card_footer]}>
                    <button type="button" classes={[c.btn, c.btn_secondary, c.mr_2]} onclick={()=>{
                            // 切换到列表页面后要刷新
                            executor(changeViewProcess)({view: "list"});
                            executor(getPagedUserProcess)({page: 0});
                        }}>取消</button>
                        <button type="button" classes={[c.btn, c.btn_primary]} onclick={() => {
                            executor(saveUserProcess)({});
                            // 保存成功后，不跳转，只给出成功提示
                        }}>保存</button>
                    </div>
                </form>
            </div>
        </div>
    );
});
