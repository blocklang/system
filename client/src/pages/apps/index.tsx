import { create, tsx } from '@dojo/framework/core/vdom';
import {createICacheMiddleware} from '@dojo/framework/core/middleware/icache';
import * as c from "bootstrap-classes";
import FontAwesomeIcon from "dojo-fontawesome/FontAwesomeIcon";
import Link from '@dojo/framework/routing/Link';
import * as request from '../../utils/request';
import { AppInfo, Pagination, ResourceProperties } from '../../interfaces';
import store from "../../store";
import * as moment from "moment";

export interface AppsProperties extends ResourceProperties{
}

export const defaultPagination = {
    totalElements: 0,
    totalPages: 1,
    number: 0,
    size: 0,
    numberOfElements: 0,
    first: true,
    last: true,
    empty: true,
    content: []
};

interface FetchResult {
    pagedApps: Pagination<AppInfo>;
}
const icache = createICacheMiddleware<FetchResult>();
const factory = create({ icache, store }).properties<AppsProperties>();

export default factory(function Apps({ properties, middleware: { icache, store } }){
    const { resId, page=0 } = properties();
    const token = store.get(store.path("session", "token"));

    const pagedApps = icache.getOrSet("pagedApps", async () => {
        const response = await request.get(`apps?resid=${resId}&page=${page}`, token);
        const pagedApps = await response.json();
        if(response.ok){
            return pagedApps;
        }
    }) || defaultPagination;
    // TODO: 使用TypeScript 新增的 ？ 语法
    const {content: apps=[], first, last, empty,  totalElements, number, size, numberOfElements} = pagedApps;

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
                        <Link to="new-app" params={{resid: resId}} classes={[c.btn, c.btn_primary]}><FontAwesomeIcon icon="plus"/> 新增</Link>
                    </div>
                    <table classes={[c.table, c.table_bordered, c.table_hover]}>
                        <thead>
                            <tr>
                                <th>名称</th>
                                <th>图标</th>
                                <th>url</th>
                                <th>描述</th>
                                <th>创建时间</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            {
                                empty ? <tr key="empty"><td colspan="6" classes={[c.text_center, c.text_muted]}>没有记录！</td></tr> : apps.map(app => {
                                    return (<tr key={app.id}>
                                        <td key="name">{app.name}</td>
                                        <td key="icon">{app.icon}</td>
                                        <td key="url">{app.url}</td>
                                        <td key="description">{app.description}</td>
                                        <td key="createTime">{moment(app.createTime).format("YYYY-MM-DD h:mm")}</td>
                                        <td key="operators">
                                            <Link to="edit-app" params={{resid: resId, appId: app.id}}>编辑</Link>
                                        </td>
                                    </tr>)
                                })
                            }
                        </tbody>
                    </table>
                    <div classes={[c.d_flex, c.justify_content_between]}>
                        <div classes={[c.text_muted]}>{`第 ${number * size + 1} 到 ${number * size +numberOfElements} 条，共 ${totalElements} 条记录。`}</div>
                        <nav>
                            <ul classes={[c.pagination]}>
                                <li classes={[c.page_item, first?c.disabled:undefined]}>
                                    {
                                        first?<a class="page-link" href="#" tabindex="-1" aria-disabled="true">上一页</a>:
                                        <Link to="apps" params={{resid: resId, page: (number-1)+""}} classes={[c.page_link]} onClick={()=>{
                                            icache.clear();
                                        }}>上一页</Link>
                                    }
                                    
                                </li>
                                <li classes={[c.page_item, last?c.disabled:undefined]}>
                                    {
                                        last?<a class="page-link" href="#" tabindex="-1" aria-disabled="true">下一页</a>:
                                        <Link to="apps" params={{resid: resId, page: (number+1)+""}} classes={[c.page_link]} onClick={()=>{
                                            icache.clear();
                                        }}>下一页</Link>
                                    }
                                </li>
                            </ul>
                        </nav>
                    </div>
                </div>
            </section>
        </virtual>
        
    );
});
