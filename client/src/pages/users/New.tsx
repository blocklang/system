import { create, tsx } from '@dojo/framework/core/vdom';
import icache from '@dojo/framework/core/middleware/icache';
import * as c from 'bootstrap-classes';
import Link from '@dojo/framework/routing/Link';
import FontAwesomeIcon from 'dojo-fontawesome/FontAwesomeIcon';
import * as request from '../../utils/request';
import store from '../../store';
import { redirectToProcess } from '../../processes/routeProcesses';
import { ResourceProperties } from '../../interfaces';

export interface NewProperties extends ResourceProperties{
}

const factory = create({ icache, store }).properties<NewProperties>();

export default factory(function New({ properties, middleware: { icache, store } }){
    const { resId  } = properties();
    const defaultPassword = "123456";
    return (
        <virtual>
            <section classes={["content-header"]}>
                <div classes={[c.container_fluid]}>
                    <h1>用户管理</h1>
                </div>
            </section>
            <section classes={["content"]}>
                <div classes={[c.container_fluid]}>
                    <div classes={[c.d_flex, c.justify_content_start, c.mb_2]}>
                        <Link to="users" params={{resid: resId, page: "0"}} classes={[c.btn, c.btn_secondary]}><FontAwesomeIcon icon="angle-left"/> 返回</Link>
                    </div>
                    <div classes={[c.card]}>
                        <div classes={[c.card_header]}>
                            <h3 classes={[c.card_title]}>新建用户</h3>
                        </div>
                        <form role="form">
                            <div classes={[c.card_body]}>
                                <div classes={[c.form_group]}>
                                    <label for="iptUsername">登录名<small classes={[c.text_muted, c.ml_1]}>必填</small></label>
                                    <input type="text" classes={[c.form_control]} id="iptUsername" focus={true} oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                        icache.set("username", event.target.value);
                                    }}/>
                                </div>
                                <div classes={[c.form_group]}>
                                    <label for="iptPassword">密码<small classes={[c.text_muted, c.ml_1]}>必填</small></label>
                                    <input type="text" classes={[c.form_control]} id="iptPassword" value={defaultPassword} oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                        icache.set("password", event.target.value);
                                    }}/>
                                </div>
                                <div classes={[c.form_group]}>
                                    <label for="iptNickname">用户名</label>
                                    <input type="text" classes={[c.form_control]} id="iptNickname" oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                        icache.set("nickname", event.target.value);
                                    }}/>
                                </div>
                                <div classes={[c.form_group]}>
                                    <label>性别</label>
                                    <div>
                                        <div classes={[c.form_check, c.form_check_inline]}>
                                            <input classes={[c.form_check_input]} type="radio" name="sex" id="rdoMale" value="1" onclick={()=>{
                                                icache.set("sex", "1");
                                            }}/>
                                            <label classes={[c.form_check_label]} for="rdoMale">男</label>
                                        </div>
                                        <div classes={[c.form_check, c.form_check_inline]}>
                                            <input classes={[c.form_check_input]} type="radio" name="sex" id="rdoFeMale" value="2" onclick={()=>{
                                                icache.set("sex", "2");
                                            }}/>
                                            <label classes={[c.form_check_label]} for="rdoFeMale">女</label>
                                        </div>
                                    </div>
                                </div>
                                <div classes={[c.form_group]}>
                                    <label for="iptPhoneNumber">手机号码</label>
                                    <input type="text" classes={[c.form_control]} id="iptPhoneNumber" maxLength="11" oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                        icache.set("phoneNumber", event.target.value);
                                    }}/>
                                </div>
                            </div>
                            <div classes={[c.card_footer]}>
                                <button type="button" classes={[c.btn, c.btn_primary]} onclick={() => {
                                    const token = store.get(store.path("session", "token"));

                                    const username = icache.get<string>("username") || "";
                                    if(username.trim() === "") {
                                        alert("请输入登录名");
                                        return;
                                    }
                                    const password = icache.get<string>("password") || defaultPassword;
                                    if(password.trim() === "") {
                                        alert("请输入密码！");
                                        return;
                                    }
                                    const nickname = icache.get<string>("nickname") || "";
                                    const sex = icache.get<string>("sex") || "";
                                    const phoneNumber = icache.get<string>("phoneNumber") || "";

                                    const post = async () => {
                                        const response = await request.post(`users?resid=${resId}`, {username, password, nickname, sex, phoneNumber}, token);
                                        if(response.ok) {
                                            store.executor(redirectToProcess)({outlet: "users", params: {resId, page: "0"}});
                                        }
                                    }

                                    post();
                                }}>保存</button>
                            </div>
                        </form>
                    </div>
                </div>
            </section>
        </virtual>
    );
});
