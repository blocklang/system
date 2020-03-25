import { create, tsx } from '@dojo/framework/core/vdom';
import {createICacheMiddleware} from '@dojo/framework/core/middleware/icache';
import * as c from 'bootstrap-classes';
import Link from '@dojo/framework/routing/Link';
import FontAwesomeIcon from 'dojo-fontawesome/FontAwesomeIcon';
import * as request from '../../utils/request';
import store from '../../store';
import { redirectToProcess } from '../../processes/routeProcesses';
import { AppInfo, ResourceProperties } from '../../interfaces';

export interface NewProperties extends ResourceProperties{
    appId: string;
}

interface FetchResult {
    app: AppInfo;
    name: string;
    description: string;
}
const icache = createICacheMiddleware<FetchResult>();
const factory = create({ icache, store }).properties<NewProperties>();

export default factory(function New({ properties, middleware: { icache, store } }){
    const { appId } = properties();

    console.log(appId)
    const token = store.get(store.path("session", "token"));
    const app = icache.getOrSet("app", async () => {
        const response = await request.get(`apps/${appId}`, token);
        const app = await response.json();
        if(response.ok) {
            return app;
        }
    });

    if(!app) {
        return <div>Loading</div>
    }

    return (
        <virtual>
            <section classes={["content-header"]}>
                <div classes={[c.container_fluid]}>
                    <h1>角色管理</h1>
                </div>
            </section>
            <section classes={["content"]}>
                <div classes={[c.container_fluid]}>
                    <div classes={[c.d_flex, c.justify_content_start, c.mb_2]}>
                        <Link to="roles" params={{appid: appId}} classes={[c.btn, c.btn_secondary]}><FontAwesomeIcon icon="angle-left"/> 返回</Link>
                    </div>
                    <div classes={[c.card]}>
                        <div classes={[c.card_header]}>
                            <h3 classes={[c.card_title]}>新建角色</h3>
                        </div>
                        <form role="form">
                            <div classes={[c.card_body]}>
                                <div classes={[c.form_group]}>
                                    <label for="txtApp">APP</label>
                                    <input type="text" classes={[c.form_control]} value={app.name} id="txtApp" readonly="readonly"/>
                                </div>
                                <div classes={[c.form_group]}>
                                    <label for="iptName">角色名<small classes={[c.text_muted, c.ml_1]}>必填</small></label>
                                    <input type="text" classes={[c.form_control]} id="iptName" focus={true} oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                        icache.set("name", event.target.value);
                                    }}/>
                                </div>
                                <div classes={[c.form_group]}>
                                    <label for="iptDescription">描述</label>
                                    <input type="text" classes={[c.form_control]} id="iptDescription" oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                        icache.set("description", event.target.value);
                                    }}/>
                                </div>
                            </div>
                            <div classes={[c.card_footer]}>
                                <button type="button" classes={[c.btn, c.btn_primary]} onclick={() => {
                                    const token = store.get(store.path("session", "token"));

                                    const name = icache.get("name") || "";
                                    if(name.trim() === "") {
                                        alert("请输入角色名");
                                        return;
                                    }
                                    const description = icache.get("description") || "";

                                    const post = async () => {
                                        const response = await request.post("roles", {appId, name, description}, token);
                                        if(response.ok) {
                                            store.executor(redirectToProcess)({outlet: "roles"});
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
