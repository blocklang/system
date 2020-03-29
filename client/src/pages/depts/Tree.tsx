import { create, tsx } from '@dojo/framework/core/vdom';
import store from '../../store';
import { loadChildDeptsProcess } from '../../processes/deptProcesses';
import { DeptInfo } from '../../interfaces';
import icache from '@dojo/framework/core/middleware/icache';
import * as c from 'bootstrap-classes';
import FontAwesomeIcon from 'dojo-fontawesome/FontAwesomeIcon';
import * as css from './Tree.m.css';

export interface TreeProperties {
    onSelectNode: (id: string) => void;
}

const factory = create({store, icache}).properties<TreeProperties>();

export default factory(function Tree({ properties, middleware: {store, icache} }){
    const { onSelectNode } = properties();
    
    const {get, path, executor} = store;
    const depts = get(path("depts"));
    if(!depts) {
        executor(loadChildDeptsProcess)({});
        return;
    }

    let level = -1;

    const getChildren = (id: string) => {
        level++;
        return depts.filter(item => item.parentId === id);
    }

    const activeNodeId = icache.getOrSet<string>("activeNodeId", "-1");

    function timesIndent(count: number) {
        const result = [];
        for(let i = 0; i < count; i++) {
            result.push(<span key={`${i}`} classes={[css.indent]}></span>);
        }
        return <span classes={["align-self-stretch"]}>{result}</span>;
    }

    const renderTreeNode = (item: DeptInfo) => {
        const {id, name, hasChildren, parentId} = item;

        if(parentId === "-1") {
            level = 1;
        }

        const isActive = item.id === activeNodeId;
        
        if(hasChildren) { 
            // 默认展开根节点，其余节点默认不展开
            const openState = icache.getOrSet<boolean>(`folderOpenState-${id}`, id === "-1"?true:false);
            
            return (<div key={id} classes={[]}>
                <div key="item" classes={[ c.d_flex, css.node, c.align_items_start, isActive ?c.bg_secondary : undefined]} onclick={(event: MouseEvent<EventTarget>)=>{
                    event.preventDefault();
                    
                    icache.set<string>("activeNodeId", id);
                    executor(loadChildDeptsProcess)({deptId: id});

                    onSelectNode && onSelectNode(id);
                }}>
                    {timesIndent(level)}
                    <span classes={[css.icon, c.mx_1, isActive?c.text_white:c.text_muted]}
                        onclick={(event: MouseEvent)=>{
                            event.preventDefault();
                            event.stopPropagation();
                            executor(loadChildDeptsProcess)({deptId: id});
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
        // 菜单
        const result = (<div key={id} classes={[]}>
            <div classes={[c.d_flex, css.node, c.align_items_start, isActive ? c.bg_primary :undefined]} onclick={()=>{
                icache.set<string>("activeNodeId", id);
                onSelectNode && onSelectNode(id);
                executor(loadChildDeptsProcess)({deptId: id});
            }}>
                {timesIndent(level)}
                <span classes={[css.icon, c.mx_1]}></span>
                {name}
            </div>
        </div>);
        return result;
    }

    const renderTree = () => {
        return getChildren("-2").map(item => renderTreeNode(item));
    }

    return (
        <div classes={[css.root]}>
            {renderTree()} 
        </div>
    );
});
