import { create, tsx } from '@dojo/framework/core/vdom';
import * as c from "bootstrap-classes";
import store from '../../store';
import icache from '@dojo/framework/core/middleware/icache';
import { getPagedAppProcess } from '../../processes/appProcesses';
import { Pagination as PageInfo, RoleInfo } from '../../interfaces';
import { defaultPagination } from '../../config';
import { getPagedRoleProcess, resetRoleProcess, getRoleProcess} from '../../processes/roleProcesses';
import { changeViewProcess } from '../../processes/pageProcesses';
import FontAwesomeIcon from 'dojo-fontawesome/FontAwesomeIcon';
import * as moment from 'moment';
import { find } from '@dojo/framework/shim/array';
import Pagination from '../../widgets/Pagination';
import { getRoleUsersProcess } from '../../processes/userRoleProcesses';
import { getRoleResourcesProcess, clearAppResourcesProcess } from '../../processes/roleResourceProcesses';

export interface ListProperties {
    appId?: string;
    onSelectApp: (appId: string) => void;
}

const factory = create({store, icache}).properties<ListProperties>();

export default factory(function List({ properties, middleware: {store, icache} }){
    const { appId, onSelectApp } = properties();

    const {get, path, executor} = store;
    // TODO: 这里只查出 10 个 APP，因为目前 10 个已能满足需求，后续扩展
    const pagedApp = get(path("pagedApp"));
    if(!pagedApp) {
        executor(getPagedAppProcess)({page: 0});
    }

    const {content: apps=[]} = pagedApp || {};

    const defaultAppId = apps.length > 0 ? apps[0].id : undefined
    if(!appId && apps.length > 0) {
        onSelectApp && onSelectApp(apps[0].id);
    }
    const selectedAppId = appId || defaultAppId;
    
    let pagedRole: PageInfo<RoleInfo> | undefined;
    if(selectedAppId){
        pagedRole = get(path("pagedRole"));
        if(!pagedRole) {
            // 初始化查询第一页
            executor(getPagedRoleProcess)({appId: selectedAppId, page: 0});
        }
    }
    const {content: roles, empty, first, last, number, size, numberOfElements, totalElements} = pagedRole || defaultPagination;

    return (
        <div classes={[c.container_fluid]}>
            <div classes={[c.d_flex, c.justify_content_between, c.mb_2]}>
                <form classes={["form-inline"]}>
                    <div classes={[c.form_group]}>
                        <label for="iptApp">APP</label>
                        <select id="iptApp" classes={[c.mx_2]} onchange={(event: MouseEvent<HTMLSelectElement>)=>{
                            const appId = event.target.value;
                            onSelectApp && onSelectApp(appId);
                            executor(getPagedRoleProcess)({appId, page: 0});
                            executor(clearAppResourcesProcess)({});
                        }}>
                        {
                            apps.map(app => <option selected={app.id===selectedAppId?"selected":undefined} value={app.id}>{app.name}</option>)
                        }
                        </select>
                        <small classes={[c.text_muted]}>查询 APP 下的角色</small>
                    </div>
                </form>
                <button type="button" classes={[c.btn, c.btn_primary]} disabled={selectedAppId==undefined} onclick={()=>{
                    executor(changeViewProcess)({view: "new"});
                    const currentApp = find(apps, app => app.id === selectedAppId);
                    executor(resetRoleProcess)({appId: selectedAppId, appName: currentApp?.name});
                }}><FontAwesomeIcon icon="plus"/> 新增</button>
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
                                <td key="operators" styles={{width: "230px"}}>
                                    <button type="button" classes={[c.btn, c.btn_secondary, c.btn_sm, c.mr_1]} onclick={()=>{
                                         executor(changeViewProcess)({view: "edit"});
                                         executor(getRoleProcess)({id: role.id});
                                    }}>编辑</button>
                                    <button type="button" classes={[c.btn, c.btn_secondary, c.btn_sm, c.mr_1]} onclick={()=>{
                                         executor(changeViewProcess)({view: "role_user"});
                                         executor(resetRoleProcess)({id: role.id, name: role.name});
                                         executor(getRoleUsersProcess)({roleId: role.id});
                                    }}>分配用户</button>
                                    <button type="button" classes={[c.btn, c.btn_secondary, c.btn_sm]} onclick={()=>{
                                         executor(changeViewProcess)({view: "role_resource"});
                                         executor(resetRoleProcess)({id: role.id, name: role.name});
                                         executor(getRoleResourcesProcess)({roleId: role.id});
                                    }}>分配资源</button>
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
                    executor(getPagedRoleProcess)({appId: selectedAppId!, page});
                }}
            />
        </div>
    );
});
