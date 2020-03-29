import { create, tsx } from '@dojo/framework/core/vdom';
import {Toast} from "../../utils/sweetalert2";
import store from '../../store';
import { clearGlobalTipProcess, changeViewProcess } from '../../processes/pageProcesses';
import { ValidateStatus } from '../../constant';
import * as c from 'bootstrap-classes';
import { setDeptFieldProcess, saveDeptProcess } from '../../processes/deptProcesses';

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

    const deptInfo = get(path("dept")) || {};
    const {name=""} = deptInfo;

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
                    <h3 classes={[c.card_title]}>新建部门</h3>
                </div>
                <form role="form" classes={[c.needs_validation]} novalidate={true}>
                    <div classes={[c.card_body]}>
                        <div classes={[c.form_group, c.position_relative]}>
                            <label for="iptName">名称<small classes={[c.text_muted, c.ml_1]}>必填</small></label>
                            <input type="text" value={name} classes={[c.form_control, showValidationClass("name")]} id="iptName" focus={true} oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                executor(setDeptFieldProcess)({field: "name", value: event.target.value});
                            }}/>
                            {showInvalidMessage("name")}
                        </div>
                    </div>
                    <div classes={[c.card_footer]}>
                    <button type="button" classes={[c.btn, c.btn_secondary, c.mr_2]} onclick={()=>{
                            executor(changeViewProcess)({view: "list"});
                        }}>取消</button>
                        <button type="button" classes={[c.btn, c.btn_primary]} onclick={() => {
                            executor(saveDeptProcess)({});
                            // 保存成功后，不跳转，只给出成功提示
                        }}>保存</button>
                    </div>
                </form>
            </div>
        </div>
    );
});
