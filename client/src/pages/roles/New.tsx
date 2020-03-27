import { create, tsx } from '@dojo/framework/core/vdom';
import * as c from 'bootstrap-classes';
import store from '../../store';
import { Toast } from '../../utils/sweetalert2';
import { clearGlobalTipProcess, changeViewProcess } from '../../processes/pageProcesses';
import { ValidateStatus } from '../../constant';
import { getPagedRoleProcess, setRoleFieldProcess, saveRoleProcess } from '../../processes/roleProcesses';

export interface NewProperties{
}

const factory = create({ store }).properties<NewProperties>();

export default factory(function New({ properties, middleware: { store } }){
    const {  } = properties();

    const {get, path, executor} = store;

    const globalTip = get(path("globalTip"));
    if(globalTip) {
        Toast.fire(globalTip);
        executor(clearGlobalTipProcess)({});
    }

    const role = get(path("role")) || {};
    const {appId, appName, name, description} = role;

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
                    <h3 classes={[c.card_title]}>新建角色</h3>
                </div>
                <form role="form" classes={[c.needs_validation]} novalidate={true}>
                    <div classes={[c.card_body]}>
                        <div classes={[c.form_group]}>
                            <label for="txtApp">APP</label>
                            <input type="text" classes={[c.form_control]} value={appName} id="txtApp" readonly="readonly"/>
                        </div>
                        <div classes={[c.form_group, c.position_relative]}>
                            <label for="iptName">角色名<small classes={[c.text_muted, c.ml_1]}>必填</small></label>
                            <input type="text" value={name} classes={[c.form_control, showValidationClass("name")]} id="iptName" focus={true} oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                executor(setRoleFieldProcess)({field: "name", value: event.target.value});
                            }}/>
                            {showInvalidMessage("name")}
                        </div>
                        <div classes={[c.form_group]}>
                            <label for="iptDescription">描述</label>
                            <input type="text" value={description} classes={[c.form_control]} id="iptDescription" oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                executor(setRoleFieldProcess)({field: "description", value: event.target.value});
                            }}/>
                        </div>
                    </div>
                    <div classes={[c.card_footer]}>
                        <button type="button" classes={[c.btn, c.btn_secondary, c.mr_2]} onclick={()=>{
                            // 切换到列表页面后要刷新
                            executor(changeViewProcess)({view: "list"});
                            executor(getPagedRoleProcess)({appId, page: 0});
                        }}>取消</button>
                        <button type="button" classes={[c.btn, c.btn_primary]} onclick={() => {
                            executor(saveRoleProcess)({});
                            // 保存成功后，不跳转，只给出成功提示
                        }}>保存</button>
                    </div>
                </form>
            </div>
        </div>
    );
});
