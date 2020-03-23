import { create, tsx } from '@dojo/framework/core/vdom';
import {createICacheMiddleware} from '@dojo/framework/core/middleware/icache';
import * as c from 'bootstrap-classes';
import Link from '@dojo/framework/routing/Link';
import FontAwesomeIcon from 'dojo-fontawesome/FontAwesomeIcon';
import * as request from '../../utils/request';
import store from '../../store';
import { UserInfo, ResourceProperties } from '../../interfaces';
import { redirectToProcess } from '../../processes/routeProcesses';

export interface EditProperties extends ResourceProperties{
    userId: string
}

interface FetchResult {
    user: UserInfo;
}
const icache = createICacheMiddleware<FetchResult>();
const factory = create({ icache, store }).properties<EditProperties>();

export default factory(function Edit({ properties, middleware: { icache, store } }){
    const { userId, resId, page } = properties();
    const token = store.get(store.path("session", "token"));
    const user = icache.getOrSet("user", async () => {
        const response = await request.get(`users/${userId}?resid=${resId}`, token);
        const user = await response.json();
        if(response.ok){
            return user;
        }
    });
    if(!user) {
        return <div>用户不存在</div>
    }

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
                        <Link to="users" classes={[c.btn, c.btn_secondary]}><FontAwesomeIcon icon="angle-left"/> 返回</Link>
                    </div>
                    <div classes={[c.card]}>
                        <div classes={[c.card_header]}>
                            <h3 classes={[c.card_title]}>修改用户</h3>
                        </div>
                        <form role="form">
                            <div classes={[c.card_body]}>
                                <div classes={[c.form_group]}>
                                    <label for="iptUsername">登录名</label>
                                    <input type="text" classes={[c.form_control]} id="iptName" value={user.username} disabled={true}/>
                                </div>
                                <div classes={[c.form_group]}>
                                    <label for="iptNickname">用户名</label>
                                    <input type="text" classes={[c.form_control]} id="iptNickname" value={user.nickname} oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                        icache.set("user", {...user, nickname: event.target.value});
                                    }}/>
                                </div>
                                <div classes={[c.form_group]}>
                                    <label>性别</label>
                                    <div>
                                        <div classes={[c.form_check, c.form_check_inline]}>
                                            <input classes={[c.form_check_input]} type="radio" name="sex" id="rdoMale" checked={user.sex==="1"} onclick={()=>{
                                                icache.set("user", {...user, sex: "1"});
                                            }}/>
                                            <label classes={[c.form_check_label]} for="rdoMale">男</label>
                                        </div>
                                        <div classes={[c.form_check, c.form_check_inline]}>
                                            <input classes={[c.form_check_input]} type="radio" name="sex" id="rdoFeMale" checked={user.sex==="2"} onclick={()=>{
                                                icache.set("user", {...user, sex: "2"});;
                                            }}/>
                                            <label classes={[c.form_check_label]} for="rdoFeMale">女</label>
                                        </div>
                                    </div>
                                </div>
                                <div classes={[c.form_group]}>
                                    <label for="iptPhoneNumber">手机号码</label>
                                    <input type="text" classes={[c.form_control]} id="iptPhoneNumber" maxLength="11" oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                        icache.set("user", {...user, phoneNumber: event.target.value});
                                    }}/>
                                </div>
                            </div>
                            <div classes={[c.card_footer]}>
                                <button type="button" classes={[c.btn, c.btn_primary]} onclick={() => {
                                    if(user.username.trim() === "") {
                                        alert("登录名不能为空");
                                        return;
                                    }

                                    const post = async () => {
                                        const response = await request.put(`users/${userId}?resid=${resId}`, user, token);
                                        if(response.ok) {
                                            store.executor(redirectToProcess)({outlet: "users", params: {resid: resId, page: page+""}});
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
