import { create, tsx } from '@dojo/framework/core/vdom';
import {Toast} from "../../utils/sweetalert2";
import store from '../../store';
import * as c from 'bootstrap-classes';
import { clearGlobalTipProcess, changeViewProcess } from '../../processes/pageProcesses';
import { ValidateStatus } from '../../constant';
import { setResourceFieldProcess, saveResourceProcess } from '../../processes/resourceProcesses';

export interface NewProperties {
}

const factory = create({store}).properties<NewProperties>();

export default factory(function New({ properties, middleware:{store} }){
    const {  } = properties();
    const {get, path, executor} = store;

    const globalTip = get(path("globalTip"));
    if(globalTip) {
        Toast.fire(globalTip);
        executor(clearGlobalTipProcess)({});
    }
    
    const resourceInfo = get(path("resource")) || {};
    const {name="", url="", icon="", resourceType, description="", auth=""} = resourceInfo;

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
                    <h3 classes={[c.card_title]}>新建资源</h3>
                </div>
                <form role="form" classes={[c.needs_validation]} novalidate={true}>
                    <div classes={[c.card_body]}>
                        <div classes={[c.form_group]}>
                            <label>资源类型</label>
                            <div>
                                <div classes={[c.form_check, c.form_check_inline]}>
                                    <input checked={resourceType === "01"} classes={[c.form_check_input]} type="radio" name="resourceType" id="rdoFunction" value="01" onclick={()=>{
                                        executor(setResourceFieldProcess)({field: "resourceType", value: "01"});
                                    }}/>
                                    <label classes={[c.form_check_label]} for="rdoFunction">目录</label>
                                </div>
                                <div classes={[c.form_check, c.form_check_inline]}>
                                    <input checked={resourceType === "02"} classes={[c.form_check_input]} type="radio" name="resourceType" id="rdoProgram" value="02" onclick={()=>{
                                        executor(setResourceFieldProcess)({field: "resourceType", value: "02"});
                                    }}/>
                                    <label classes={[c.form_check_label]} for="rdoProgram">菜单</label>
                                </div>
                                <div classes={[c.form_check, c.form_check_inline]}>
                                    <input checked={resourceType === "03"} classes={[c.form_check_input]} type="radio" name="resourceType" id="rdoOperator" value="03" onclick={()=>{
                                        executor(setResourceFieldProcess)({field: "resourceType", value: "03"});
                                    }}/>
                                    <label classes={[c.form_check_label]} for="rdoOperator">按钮</label>
                                </div>
                            </div>
                        </div>
                        <div classes={[c.form_group, c.position_relative]}>
                            <label for="iptName">名称<small classes={[c.text_muted, c.ml_1]}>必填</small></label>
                            <input type="text" value={name} classes={[c.form_control, showValidationClass("name")]} id="iptName" focus={true} oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                executor(setResourceFieldProcess)({field: "name", value: event.target.value});
                            }}/>
                            {showInvalidMessage("name")}
                        </div>
                        <div classes={[c.form_group, c.position_relative]}>
                            <label for="iptAuth">权限标识<small classes={[c.text_muted, c.ml_1]}>必填</small></label>
                            <input type="text" value={auth} classes={[c.form_control, showValidationClass("auth")]} id="iptAuth" oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                executor(setResourceFieldProcess)({field: "auth", value: event.target.value});
                            }}/>
                            {showInvalidMessage("auth")}
                            <small key="help" class="form-text text-muted">
                            {`唯一标识每个资源，在 Controller 中根据该值做权限校验，推荐格式为 “{app}/{目录}/{菜单}/{按钮}”`}
                            </small>
                        </div>
                        <div classes={[c.form_group, c.position_relative]}>
                            <label for="iptUrl">url</label>
                            <input type="text" value={url} classes={[c.form_control]} id="iptUrl" oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                executor(setResourceFieldProcess)({field: "url", value: event.target.value});
                            }}/>
                        </div>
                        <div classes={[c.form_group, c.position_relative]}>
                            <label for="iptIcon">图标</label>
                            <input type="text" value={icon} classes={[c.form_control]} id="iptIcon" oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                executor(setResourceFieldProcess)({field: "icon", value: event.target.value});
                            }}/>
                        </div>
                        <div classes={[c.form_group, c.position_relative]}>
                            <label for="iptDescription">描述</label>
                            <input type="text" value={description} classes={[c.form_control]} id="iptDescription" oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                executor(setResourceFieldProcess)({field: "description", value: event.target.value});
                            }}/>
                        </div>
                    </div>
                    <div classes={[c.card_footer]}>
                    <button type="button" classes={[c.btn, c.btn_secondary, c.mr_2]} onclick={()=>{
                            executor(changeViewProcess)({view: "list"});
                        }}>取消</button>
                        <button type="button" classes={[c.btn, c.btn_primary]} onclick={() => {
                            executor(saveResourceProcess)({});
                            // 保存成功后，不跳转，只给出成功提示
                        }}>保存</button>
                    </div>
                </form>
            </div>
        </div>
    );
});
