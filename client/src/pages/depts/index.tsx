import { create, tsx } from '@dojo/framework/core/vdom';
import { ResourceProperties } from '../../interfaces';
import store from '../../store';
import icache from '@dojo/framework/core/middleware/icache';
import { loadPermissionProcess } from '../../processes/permissionProcesses';
import NotFound from "../error/404";
import * as c from "bootstrap-classes";
import List from "./List";
import New from './New';
import Edit from './Edit';
import Tree from './Tree';

export interface DepartmentsProperties extends ResourceProperties{
}

const factory = create({store, icache}).properties<DepartmentsProperties>();

export default factory(function Departments({ properties, middleware: {store, icache} }){
    const {resId} = properties();
    const {get, path, executor} = store;

    const permission = get(path("permission"));
    if(!permission) {
        executor(loadPermissionProcess)({resId});
        return;
    }
    if(!permission.canAccess) {
        return <NotFound />
    }

    const selectedNodeId = icache.getOrSet<string>("selectedNodeId", "-1");

    const view = get(path("pageView")) || "list";

    return (
        <virtual>
            <section classes={["content-header"]}>
                <div classes={[c.container_fluid]}>
                    <h1>部门管理</h1>
                </div>
            </section>
            <section classes={["content"]}>
                <div classes={[c.d_flex]}>
                    <div classes={[c.mr_2]}>
                        <Tree onSelectNode={(id)=>{
                            icache.set("selectedNodeId", id);
                        }}/>
                    </div>
                    <div classes={[c.flex_grow_1]}>
                    {
                        view === "list" && <List selectedNodeId={selectedNodeId}/>
                    }
                    {
                        view === "new" && <New />
                    }
                    {
                        view === "edit" && <Edit />
                    }
                    </div>
                </div>
            </section>
        </virtual>
    );
});
