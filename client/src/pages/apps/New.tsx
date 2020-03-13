import { create, tsx } from '@dojo/framework/core/vdom';
import icache from '@dojo/framework/core/middleware/icache';
import * as c from 'bootstrap-classes';
import Link from '@dojo/framework/routing/Link';
import FontAwesomeIcon from 'dojo-fontawesome/FontAwesomeIcon';
import * as request from '../../utils/request';
import store from '../../store';
import { redirectToProcess } from '../../processes/routeProcesses';

export interface NewProperties {
}

const factory = create({ icache, store }).properties<NewProperties>();

export default factory(function New({ properties, middleware: { icache, store } }){
    const {  } = properties();
    return (
        <virtual>
            <section classes={["content-header"]}>
                <div classes={[c.container_fluid]}>
                    <h1>APP管理</h1>
                </div>
            </section>
            <section classes={["content"]}>
                <div classes={[c.container_fluid]}>
                    <div classes={[c.d_flex, c.justify_content_start, c.mb_2]}>
                        <Link to="apps" classes={[c.btn, c.btn_secondary]}><FontAwesomeIcon icon="angle-left"/> 返回</Link>
                    </div>
                    <div classes={[c.card]}>
                        <div classes={[c.card_header]}>
                            <h3 classes={[c.card_title]}>新建APP</h3>
                        </div>
                        <form role="form">
                            <div classes={[c.card_body]}>
                                <div classes={[c.form_group]}>
                                    <label for="iptName">名称<small classes={[c.text_muted, c.ml_1]}>必填</small></label>
                                    <input type="text" classes={[c.form_control]} id="iptName" oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                        icache.set("appName", event.target.value);
                                    }}/>
                                </div>
                                <div classes={[c.form_group]}>
                                    <label for="iptIcon">Icon</label>
                                    <input type="text" classes={[c.form_control]} id="iptIcon" oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                        icache.set("icon", event.target.value);
                                    }}/>
                                </div>
                                <div classes={[c.form_group]}>
                                    <label for="iptUrl">url</label>
                                    <input type="text" classes={[c.form_control]} id="iptUrl" oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                        icache.set("url", event.target.value);
                                    }}/>
                                </div>
                                <div classes={[c.form_group]}>
                                    <label for="iptDescription">描述</label>
                                    <textarea classes={[c.form_control]} id="iptDescription" oninput={(event: KeyboardEvent<HTMLTextAreaElement>)=>{
                                        icache.set("description", event.target.value);
                                    }}/>
                                </div>
                            </div>
                            <div classes={[c.card_footer]}>
                                <button type="button" classes={[c.btn, c.btn_primary]} onclick={() => {
                                    const token = store.get(store.path("session", "token"));

                                    const name = icache.get<string>("appName") || "";
                                    if(name.trim() === "") {
                                        alert("名称不能为空");
                                        return;
                                    }
                                    const icon = icache.get<string>("icon") || "";
                                    const url = icache.get<string>("url") || "";
                                    const description = icache.get<string>("description") || "";

                                    const post = async () => {
                                        const response = await request.post("apps", {name, icon, url, description}, token);
                                        if(response.ok) {
                                            store.executor(redirectToProcess)({outlet: "apps"});
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
