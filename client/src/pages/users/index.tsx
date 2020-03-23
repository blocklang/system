import { create, tsx } from '@dojo/framework/core/vdom';
import {createICacheMiddleware} from '@dojo/framework/core/middleware/icache';
import * as c from "bootstrap-classes";
import FontAwesomeIcon from "dojo-fontawesome/FontAwesomeIcon";
import Link from '@dojo/framework/routing/Link';
import * as request from '../../utils/request';
import { UserInfo, ResourceProperties, Pagination } from '../../interfaces';
import store from "../../store";
import * as moment from "moment";
import { defaultPagination } from '../apps';

export interface UsersProperties extends ResourceProperties{
}

interface FetchResult {
    pagedUsers: Pagination<UserInfo>;
}
const icache = createICacheMiddleware<FetchResult>();
const factory = create({ icache, store }).properties<UsersProperties>();

export default factory(function Users({ properties, middleware: { icache, store } }){
    const { resId, page=0 } = properties();
    const token = store.get(store.path("session", "token"));

    const pagedUsers = icache.getOrSet("pagedUsers", async () => {
        const response = await request.get(`users?resid=${resId}&page=${page}`, token);
        const apps = await response.json();
        if(response.ok) {
            return apps;
        }
        console.error(response);
    }) || defaultPagination;

    const {content: users=[], first, last, empty,  totalElements, number, size, numberOfElements} = pagedUsers;

    return (
        <virtual>
            <section classes={["content-header"]}>
                <div classes={[c.container_fluid]}>
                    <h1>用户管理</h1>
                </div>
            </section>
            <section classes={["content"]}>
                <div classes={[c.container_fluid]}>
                    <div classes={[c.d_flex, c.justify_content_end, c.mb_2]}>
                        <Link to="new-user" params={{resid: resId}} classes={[c.btn, c.btn_primary]}><FontAwesomeIcon icon="plus"/> 新增</Link>
                    </div>
                    <table classes={[c.table, c.table_bordered, c.table_hover]}>
                        <thead>
                            <tr>
                                <th>登录名</th>
                                <th>用户名</th>
                                <th>性别</th>
                                <th>手机</th>
                                <th>创建时间</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            {
                                empty ? <tr key="empty"><td colspan="6" classes={[c.text_center, c.text_muted]}>没有记录！</td></tr> :
                                    users.map(user => {
                                    return (<tr key={user.id}>
                                        <td key="username">{user.username}</td>
                                        <td key="nickname">{user.nickname}</td>
                                        <td key="sex">{user.sex === "1"? "男": "女"}</td>
                                        <td key="phoneNumber">{user.phoneNumber}</td>
                                        <td key="createTime">{moment(user.createTime).format("YYYY-MM-DD h:mm")}</td>
                                        <td key="operators">
                                            <Link to="edit-user" params={{resid: resId, userId: user.id, page: number+""}}>编辑</Link>
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
                                        <Link to="users" params={{resid: resId, page: (number-1)+""}} classes={[c.page_link]} onClick={()=>{
                                            icache.clear();
                                        }}>上一页</Link>
                                    }
                                    
                                </li>
                                <li classes={[c.page_item, last?c.disabled:undefined]}>
                                    {
                                        last?<a class="page-link" href="#" tabindex="-1" aria-disabled="true">下一页</a>:
                                        <Link to="users" params={{resid: resId, page: (number+1)+""}} classes={[c.page_link]} onClick={()=>{
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
