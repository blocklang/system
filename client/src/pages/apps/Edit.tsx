import { create, tsx } from '@dojo/framework/core/vdom';
import {createICacheMiddleware} from '@dojo/framework/core/middleware/icache';
import * as c from 'bootstrap-classes';
import Link from '@dojo/framework/routing/Link';
import FontAwesomeIcon from 'dojo-fontawesome/FontAwesomeIcon';
import * as request from '../../utils/request';
import store from '../../store';
import { AppInfo } from '../../interfaces';
import { redirectToProcess } from '../../processes/routeProcesses';

export interface EditProperties {
    appId: string
}

interface FetchResult {
    app: AppInfo;
}
const icache = createICacheMiddleware<FetchResult>();
const factory = create({ icache, store }).properties<EditProperties>();

export default factory(function Edit({ properties, middleware: { icache, store } }){
    const { appId } = properties();
    const token = store.get(store.path("session", "token"));
    const app = icache.getOrSet("app", async () => {
        const response = await request.get(`apps/${appId}`, token);
        const app = await response.json();
        if(response.ok){
            return app;
        }
    });
    if(!app) {
        return <div>APP 不存在</div>
    }

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
                            <h3 classes={[c.card_title]}>编辑APP</h3>
                        </div>
                        <form role="form">
                            <div classes={[c.card_body]}>
                                <div classes={[c.form_group]}>
                                    <label for="iptName">名称<small classes={[c.text_muted, c.ml_1]}>必填</small></label>
                                    <input type="text" classes={[c.form_control]} id="iptName" value={app.name} oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                        icache.set("app", {...app, name:event.target.value});
                                    }}/>
                                </div>
                                <div classes={[c.form_group]}>
                                    <label for="iptIcon">Icon</label>
                                    <input type="text" classes={[c.form_control]} id="iptIcon" value={app.icon} oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                        icache.set("app", {...app, icon:event.target.value});
                                    }}/>
                                </div>
                                <div classes={[c.form_group]}>
                                    <label for="iptUrl">url</label>
                                    <input type="text" classes={[c.form_control]} id="iptUrl" value={app.url} oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                        icache.set("app", {...app, url:event.target.value});
                                    }}/>
                                </div>
                                <div classes={[c.form_group]}>
                                    <label for="iptDescription">描述</label>
                                    <textarea classes={[c.form_control]} id="iptDescription" value={app.description} oninput={(event: KeyboardEvent<HTMLTextAreaElement>)=>{
                                        icache.set("app", {...app, description:event.target.value});
                                    }}/>
                                </div>
                            </div>
                            <div classes={[c.card_footer]}>
                                <button type="button" classes={[c.btn, c.btn_primary]} onclick={() => {
                                    if(app.name.trim() === "") {
                                        alert("名称不能为空");
                                        return;
                                    }

                                    const post = async () => {
                                        const response = await request.put("apps", app, token);
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
