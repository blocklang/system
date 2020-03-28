import { create, tsx } from '@dojo/framework/core/vdom';
import store from '../../store';
import * as c from 'bootstrap-classes';
import { changeViewProcess } from '../../processes/pageProcesses';
import FontAwesomeIcon from 'dojo-fontawesome/FontAwesomeIcon';
import * as moment from 'moment';
import { resetDeptProcess, getDeptProcess } from '../../processes/deptProcesses';
import { find } from '@dojo/framework/shim/array';

export interface ListProperties {
    selectedNodeId: string;
}

const factory = create({store}).properties<ListProperties>();

export default factory(function List({ properties, middleware: {store} }){
    const { selectedNodeId} = properties();
    const {get, path, executor} = store;
    const depts = get(path("depts")) || [];

    console.log("depts", depts);

    const children = depts.filter(item => item.parentId === selectedNodeId);
    const selectedNode = find(depts, dept => dept.id === selectedNodeId);

    console.log(children);
    
    return (
        <div classes={[c.container_fluid]}>
            <div classes={[c.d_flex, c.justify_content_end, c.mb_2]}>
                <button type="button" classes={[c.btn, c.btn_primary]} disabled={selectedNodeId==undefined} onclick={()=>{
                    executor(changeViewProcess)({view: "new"});
                    
                    executor(resetDeptProcess)({parentId: selectedNodeId});
                }}><FontAwesomeIcon icon="plus"/> 新增</button>
            </div>
            <table classes={[c.table, c.table_bordered, c.table_hover]}>
                <thead>
                    <tr>
                        <th>名称</th>
                        <th>创建时间</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    {
                        children.length === 0 ? <tr key="empty"><td colspan="3" classes={[c.text_center, c.text_muted]}>{`${selectedNode?.name}下没有子部门！`}</td></tr> :
                        children.map(item => {
                            return (<tr key={item.id}>
                                <td key="name">{item.name}</td>
                                <td key="createTime">{moment(item.createTime).format("YYYY-MM-DD h:mm")}</td>
                                <td key="operators">
                                    <button type="button" classes={[c.btn, c.btn_secondary, c.btn_sm]} onclick={()=>{
                                         executor(changeViewProcess)({view: "edit"});
                                         executor(getDeptProcess)({id: item.id});
                                    }}>编辑</button>
                                </td>
                            </tr>)
                        })
                    }
                </tbody>
            </table>
        </div>
    );
});
