import { create, tsx } from '@dojo/framework/core/vdom';
import * as moment from "moment";
import FontAwesomeIcon from "dojo-fontawesome/FontAwesomeIcon";
import * as c from "bootstrap-classes";
import store from '../../store';
import { getPagedUserProcess, getUserProcess, resetUserProcess } from '../../processes/userProcesses';
import { changeViewProcess } from '../../processes/pageProcesses';
import { defaultPagination } from '../../config';
import Pagination from '../../widgets/Pagination';

export interface ListProperties {
}

const factory = create({store}).properties<ListProperties>();

export default factory(function List({ properties, middleware: {store} }){
    const {get, path, executor} = store;
    const pagedUser = get(path("pagedUser"));
    if(!pagedUser) {
        executor(getPagedUserProcess)({page: 0});
    }

    const {content: users=[], first, last, empty,  totalElements, number, size, numberOfElements} = pagedUser|| defaultPagination;
    return (
        <div classes={[c.container_fluid]}>
            <div classes={[c.d_flex, c.justify_content_end, c.mb_2]}>
                <button type="button" classes={[c.btn, c.btn_primary]} onclick={()=>{
                    executor(changeViewProcess)({view: "new"});
                    // 在此处设置初始化时的默认值。
                    executor(resetUserProcess)({password: "123456"});
                }}><FontAwesomeIcon icon="plus"/> 新增</button>
            </div>
            <table classes={[c.table, c.table_bordered, c.table_hover]}>
                <thead>
                    <tr>
                        <th>登录名</th>
                        <th>用户名</th>
                        <th>部门</th>
                        <th>性别</th>
                        <th>手机</th>
                        <th>创建时间</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    {
                        empty ? <tr key="empty"><td colspan="7" classes={[c.text_center, c.text_muted]}>没有记录！</td></tr> :
                            users.map(user => {
                            return (<tr key={user.id}>
                                <td key="username">{user.username}</td>
                                <td key="nickname">{user.nickname}</td>
                                <td key="dept">{user.deptId}</td>
                                <td key="sex">{user.sex === "1"? "男": "女"}</td>
                                <td key="phoneNumber">{user.phoneNumber}</td>
                                <td key="createTime">{moment(user.createTime).format("YYYY-MM-DD h:mm")}</td>
                                <td key="operators">
                                    <button type="button" classes={[c.btn, c.btn_secondary, c.btn_sm]} onclick={()=>{
                                         executor(changeViewProcess)({view: "edit"});
                                         executor(getUserProcess)({id: user.id});
                                    }}>编辑</button>
                                </td>
                            </tr>)
                        })
                    }
                </tbody>
            </table>
            <Pagination 
                first={first} 
                last={last} 
                number={number} 
                size={size} 
                numberOfElements={numberOfElements} 
                totalElements={totalElements}
                onPageChanged={(page)=> {
                    executor(getPagedUserProcess)({page});
                }}
            />
        </div>
    );
});
