import { create, tsx } from '@dojo/framework/core/vdom';
import {createICacheMiddleware} from '@dojo/framework/core/middleware/icache';
import * as c from "bootstrap-classes";
import FontAwesomeIcon from "dojo-fontawesome/FontAwesomeIcon";
import Link from '@dojo/framework/routing/Link';
import * as request from '../../utils/request';
import { AppInfo } from '../../interfaces';
import store from "../../store";
import * as moment from "moment";

export interface AppsProperties {
}

interface FetchResult {
    apps: AppInfo[];
}
const icache = createICacheMiddleware<FetchResult>();
const factory = create({ icache, store }).properties<AppsProperties>();

export default factory(function Apps({ properties, middleware: { icache, store } }){
    const {  } = properties();
    const token = store.get(store.path("session", "token"));

    const apps = icache.getOrSet("apps", async () => {
        const response = await request.get("apps", token);
        const apps = await response.json();
        return apps;
    }) || [];

    return (
        <virtual>
            <section classes={["content-header"]}>
                <div classes={[c.container_fluid]}>
                    <h1>APP管理</h1>
                </div>
            </section>
            <section classes={["content"]}>
                <div classes={[c.container_fluid]}>
                    <div classes={[c.d_flex, c.justify_content_end, c.mb_2]}>
                        <Link to="new-app" classes={[c.btn, c.btn_primary]}><FontAwesomeIcon icon="plus"/> 新增</Link>
                    </div>
                    <table classes={[c.table, c.table_bordered, c.table_hover]}>
                        <thead>
                            <tr>
                                <th>名称</th>
                                <th>Logo</th>
                                <th>url</th>
                                <th>描述</th>
                                <th>创建时间</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            {
                                apps.length === 0 ? <tr key="empty"><td colspan="6" classes={[c.text_center, c.text_muted]}>没有记录！</td></tr> : apps.map(app => {
                                    return (<tr key={app.id}>
                                        <td key="name">{app.name}</td>
                                        <td key="icon">{app.icon}</td>
                                        <td key="url">{app.url}</td>
                                        <td key="description">{app.description}</td>
                                        <td key="createTime">{moment(app.createTime).format("YYYY-MM-DD h:mm")}</td>
                                        <td key="operators">
                                            <Link to="edit-app" params={{appId: app.id}}>编辑</Link>
                                        </td>
                                    </tr>)
                                })
                            }
                        </tbody>
                    </table>
                </div>
            </section>
        </virtual>
        
    );
});
