import { create, tsx } from '@dojo/framework/core/vdom';
import {createICacheMiddleware} from '../../middleware/icache';
import * as c from "bootstrap-classes";
import FontAwesomeIcon from "dojo-fontawesome/FontAwesomeIcon";
import Link from '@dojo/framework/routing/Link';
import * as request from '../../utils/request';
import { RoleInfo, AppInfo, ResourceProperties, Pagination } from '../../interfaces';
import store from "../../store";
import * as moment from "moment";
import { defaultPagination } from '../../config';

export interface RolesProperties extends ResourceProperties{
}

interface FetchResult {
    appId?: string;
    apps: AppInfo[];
    pagedRole: Pagination<RoleInfo> | undefined;
}
const icache = createICacheMiddleware<FetchResult>();
const factory = create({ icache, store }).properties<RolesProperties>();

export default factory(function Roles({ properties, middleware: { icache, store } }){
    const { resId } = properties();
    const token = store.get(store.path("session", "token"));

    const apps = icache.getOrSet("apps", async () => {
        // TODO: 这里只查出 10 个 APP，因为目前 10 个已能满足需求，后续扩展
        const response = await request.get(`apps?resid=${resId}`, token);
        const pagedApps = await response.json();
        if(response.ok) {
            return pagedApps.content;
        }
    }) || [];

    const defaultAppId = apps.length > 0 ? apps[0].id : undefined
    const appId = icache.get("appId") || defaultAppId;
    
    let pagedRole: Pagination<RoleInfo> = defaultPagination;
    if(appId){
        pagedRole = icache.getOrSet("pagedRole", async () => {
            const response = await request.get(`roles?appId=${appId}`, token);
            const pagedRole = await response.json();
            if(response.ok) {
                return pagedRole;
            }
        }) || defaultPagination;
    }
    const {content: roles, empty} = pagedRole;

    return (
        <virtual>
            <section classes={["content-header"]}>
                <div classes={[c.container_fluid]}>
                    <h1>角色管理</h1>
                </div>
            </section>
            <section classes={["content"]}>
                <div classes={[c.container_fluid]}>
                    <div classes={[c.d_flex, c.justify_content_between, c.mb_2]}>
                        <select onchange={(event: MouseEvent<HTMLSelectElement>)=>{
                            const appId = event.target.value;
                            icache.set("appId", event.target.value);
                            icache.set("pagedRole", async () => {
                                const response = await request.get(`roles?appId=${appId}`, token);
                                const pagedRole = await response.json();
                                if(response.ok) {
                                    return pagedRole;
                                }
                            });
                        }}>
                        {
                            apps.map(app => <option selected={app.id===appId?"selected":undefined} value={app.id}>{app.name}</option>)
                        }
                        </select>
                        {
                            appId ? 
                                <Link to="new-role" params={{appid: appId, resid: resId}} classes={[c.btn, c.btn_primary]}><FontAwesomeIcon icon="plus"/> 新增</Link> :
                                <a href="#" disabled={true} classes={[c.btn, c.btn_primary]}><FontAwesomeIcon icon="plus"/>{appId} 新增</a>
                        }
                        
                    </div>
                    <table classes={[c.table, c.table_bordered, c.table_hover]}>
                        <thead>
                            <tr>
                                <th>名称</th>
                                <th>描述</th>
                                <th>创建时间</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            {
                                empty ? <tr key="empty"><td colspan="4" classes={[c.text_center, c.text_muted]}>没有记录！</td></tr> :
                                roles.map(role => {
                                    return (<tr key={role.id}>
                                        <td key="name">{role.name}</td>
                                        <td key="description">{role.description}</td>
                                        <td key="createTime">{moment(role.createTime).format("YYYY-MM-DD h:mm")}</td>
                                        <td key="operators">
                                            <Link to="edit-role" params={{roleId: role.id}}>编辑</Link>
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
