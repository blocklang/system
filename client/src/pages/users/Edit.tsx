import { create, tsx } from '@dojo/framework/core/vdom';
import * as c from 'bootstrap-classes';
import store from '../../store';
import { setUserFieldProcess, updateUserProcess } from '../../processes/userProcesses';
import { changeViewProcess, } from '../../processes/pageProcesses';

export interface EditProperties{}

const factory = create({ store }).properties<EditProperties>();

export default factory(function Edit({ properties, middleware: { store } }){
    const {get, path, executor} = store;
    const user = get(path("user")) || {};
    const {username="", nickname="", sex, phoneNumber=""} = user;

    return (
        <div classes={[c.container_fluid]}>
            <div classes={[c.card]}>
                <div classes={[c.card_header]}>
                    <h3 classes={[c.card_title]}>修改用户</h3>
                </div>
                <form role="form">
                    <div classes={[c.card_body]}>
                        <div classes={[c.form_group]}>
                            <label for="iptUsername">登录名</label>
                            <input type="text" classes={[c.form_control]} id="iptName" value={username} disabled={true}/>
                        </div>
                        <div classes={[c.form_group]}>
                            <label for="iptNickname">用户名</label>
                            <input type="text" classes={[c.form_control]} id="iptNickname" value={nickname} oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                executor(setUserFieldProcess)({field: "nickname", value: event.target.value});
                            }}/>
                        </div>
                        <div classes={[c.form_group]}>
                            <label>性别</label>
                            <div>
                                <div classes={[c.form_check, c.form_check_inline]}>
                                    <input classes={[c.form_check_input]} type="radio" name="sex" id="rdoMale" checked={sex==="1"} onclick={()=>{
                                        executor(setUserFieldProcess)({field: "sex", value:"1"});
                                    }}/>
                                    <label classes={[c.form_check_label]} for="rdoMale">男</label>
                                </div>
                                <div classes={[c.form_check, c.form_check_inline]}>
                                    <input classes={[c.form_check_input]} type="radio" name="sex" id="rdoFeMale" checked={sex==="2"} onclick={()=>{
                                        executor(setUserFieldProcess)({field: "sex", value:"2"});
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
                            // 要保留在原页面
                            executor(changeViewProcess)({view: "list"});
                        }}>取消</button>
                        <button type="button" classes={[c.btn, c.btn_primary]} onclick={() => {
                            // 更新成功之后要跳转，并在刷新列表
                            executor(updateUserProcess)({});
                        }}>保存</button>
                    </div>
                </form>
            </div>
        </div>
    );
});
