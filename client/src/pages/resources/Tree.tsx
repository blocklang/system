import { create, tsx } from '@dojo/framework/core/vdom';
import store from '../../store';
import { ResourceInfo } from '../../interfaces';
import icache from '@dojo/framework/core/middleware/icache';
import * as c from 'bootstrap-classes';
import FontAwesomeIcon from 'dojo-fontawesome/FontAwesomeIcon';
import * as css from './Tree.m.css';
import { loadChildResourcesProcess, resetResourceProcess } from '../../processes/resourceProcesses';
import { getPagedAppProcess } from '../../processes/appProcesses';
import { changeViewProcess } from '../../processes/pageProcesses';

export interface TreeProperties {
    appId: string;
    onSelectNode: (id: string) => void;
    onSelectApp: (id: string) => void;
}

const factory = create({store, icache}).properties<TreeProperties>();

export default factory(function Tree({ properties, middleware: {store, icache} }){
    const { appId, onSelectNode, onSelectApp } = properties();
    const {get, path, executor} = store;

    // TODO: 这里只查出 10 个 APP，因为目前 10 个已能满足需求，后续扩展
    const pagedApp = get(path("pagedApp"));
    if(!pagedApp) {
        executor(getPagedAppProcess)({page: 0});
    }
    const {content: apps=[]} = pagedApp || {}; // 如果未返回，则要显示 Select 部件，只是其中没有数据
    const selectedAppId = appId || (apps.length > 0?apps[0].id:undefined);
    if(!appId && apps.length >0){
        onSelectApp&&onSelectApp(apps[0].id); // 默认选择第一个元素
    }

    // TODO: 设置初始化的 appid

    let resources = get(path("resources"));
    if(selectedAppId && !resources) {
        executor(loadChildResourcesProcess)({appId: selectedAppId, resourceId: "-1"});
    }
    if(!resources) {
        resources = [];
    }

    const getChildren = (id: string) => {
        // 在树结构中不显示按钮资源
        return resources.filter(item => item.parentId === id);
    }

    const activeNodeId = icache.getOrSet<string>("activeNodeId", "-1");

    function timesIndent(count: number) {
        const result = [];
        for(let i = 0; i < count; i++) {
            result.push(<span key={`${i}`} classes={[css.indent]}></span>);
        }
        return <span classes={["align-self-stretch"]}>{result}</span>;
    }

    const renderTreeNode = (item: ResourceInfo) => {
        const {id, name, resourceType, level} = item;

        const hasChildren = resourceType !== "03";
        const isActive = item.id === activeNodeId;
        
        if(hasChildren) { 
            // 默认展开根节点，其余节点默认不展开
            const openState = icache.getOrSet<boolean>(`folderOpenState-${id}`, id === "-1"?true:false);
            
            return (<div key={id} classes={[]}>
                <div key="item" classes={[ c.d_flex, css.node, c.align_items_start, isActive ?c.bg_secondary : undefined]} onclick={(event: MouseEvent<EventTarget>)=>{
                    event.preventDefault();
                    
                    icache.set<string>("activeNodeId", id);
                    executor(changeViewProcess)({view: "list"});
                    executor(loadChildResourcesProcess)({appId, resourceId: id});

                    onSelectNode && onSelectNode(id);
                }}>
                    {timesIndent(level)}
                    <span classes={[css.icon, c.mx_1, isActive?c.text_white:c.text_muted]}
                        onclick={(event: MouseEvent)=>{
                            event.preventDefault();
                            event.stopPropagation();
                            
                            executor(loadChildResourcesProcess)({appId, resourceId: id});
                            icache.set<boolean>(`folderOpenState-${id}`, !openState);
                        }}
                    ><FontAwesomeIcon icon={openState?"angle-down":"angle-right"} title={openState?"收缩":"展开"}/></span>
                    {name}
                        
                </div>
                {
                    openState && (<div key="container" classes={[]}> {getChildren(id).map(item => renderTreeNode(item))}</div>)
                }
            </div>);
        }
       
        const result = (<div key={id} classes={[]}>
            <div classes={[c.d_flex, css.node, c.align_items_start, isActive ? c.bg_secondary :undefined]} onclick={()=>{
                icache.set<string>("activeNodeId", id);
                onSelectNode && onSelectNode(id);
                executor(changeViewProcess)({view: "list"});
                executor(loadChildResourcesProcess)({appId, resourceId: id});
            }}>
                {timesIndent(level)}
                <span classes={[css.icon, c.mx_1]}></span>
                {name}
            </div>
        </div>);

        return result;
    }

    const renderTree = () => {
        return <virtual>
        <div classes={[c.mt_2]}>{getChildren("-2").map(item => renderTreeNode(item))}</div>
        {
            resources.length === 1 && <div classes={[c.text_muted, c.mt_5]}>
            点击 <button type="button" classes={[c.btn, c.btn_secondary, c.btn_sm]} disabled={selectedAppId==undefined} onclick={()=>{
                executor(changeViewProcess)({view: "new"});
                executor(resetResourceProcess)({appId, parentId: "-1", resourceType: "02"});
            }}>新增</button> 配置资源！
        </div>
        }
        </virtual>;
    }

    const renderAppSelect = () => {
        return <form classes={["form-inline"]}>
            <div classes={[c.form_group]}>
                <label for="iptApp">APP</label>
                <select id="iptApp" classes={[c.mx_2]} onchange={(event: MouseEvent<HTMLSelectElement>) => {
                    const appId = event.target.value;
                    onSelectApp && onSelectApp(appId);
                    icache.set<string>("activeNodeId", "-1");
                    executor(changeViewProcess)({view: "list"});
                    executor(loadChildResourcesProcess)({ appId, resourceId: "-1" });
                }}>
                    {
                        apps.map(app => <option selected={app.id === selectedAppId ? "selected" : undefined} value={app.id}>{app.name}</option>)
                    }
                </select>
            </div>
        </form>
    }

    return (
        <div classes={[css.root]}>
            {renderAppSelect()}
            {renderTree()} 
        </div>
    );
});
