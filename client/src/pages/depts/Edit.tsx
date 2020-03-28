import { create, tsx } from '@dojo/framework/core/vdom';
import store from '../../store';
import * as c from 'bootstrap-classes';
import { ValidateStatus } from '../../constant';
import { setDeptFieldProcess, updateDeptProcess } from '../../processes/deptProcesses';
import { changeViewProcess } from '../../processes/pageProcesses';

export interface EditProperties {
}

const factory = create({store}).properties<EditProperties>();

export default factory(function Edit({ properties, middleware: {store} }){
    const {  } = properties();
    const {get, path, executor} = store;
    const dept = get(path("dept")) || {};
    const {name=""} = dept;

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
                    <h3 classes={[c.card_title]}>修改部门</h3>
                </div>
                <form role="form" classes={[c.needs_validation]} novalidate={true}>
                    <div classes={[c.card_body]}>
                        <div classes={[c.form_group, c.position_relative]}>
                            <label for="iptname">名称<small classes={[c.text_muted, c.ml_1]}>必填</small></label>
                            <input type="text" value={name} classes={[c.form_control, showValidationClass("name")]} id="iptUsername" focus={true} oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
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
                            executor(updateDeptProcess)({});
                        }}>保存</button>
                    </div>
                </form>
            </div>
        </div>
    );
});
