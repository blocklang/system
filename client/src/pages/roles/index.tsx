import { create, tsx } from '@dojo/framework/core/vdom';
import * as c from "bootstrap-classes";
import { ResourceProperties} from '../../interfaces';
import store from "../../store";
import icache from "@dojo/framework/core/middleware/icache";
import { loadPermissionProcess } from '../../processes/permissionProcesses';
import NotFound from "../error/404";
import List from "./List";
import New from './New';
import Edit from './Edit';
import RoleUsers from './RoleUsers';
import RoleResources from './role-resources/';

export interface RolesProperties extends ResourceProperties{
}

const factory = create({ store, icache }).properties<RolesProperties>();

export default factory(function Roles({ properties, middleware: {  store, icache } }){
    const { resId } = properties();
    const {get, path, executor} = store;
    const permission = get(path("permission"));
    if(!permission) {
        executor(loadPermissionProcess)({resId});
        return;
    }
    if(!permission.canAccess) {
        return <NotFound />
    }

    const view = get(path("pageView")) || "list";

    const appId = icache.get<string>("appId");

    return (
        <virtual>
            <section classes={["content-header"]}>
                <div classes={[c.container_fluid]}>
                    <h1>角色管理</h1>
                </div>
            </section>
            <section classes={["content"]}>
                {
                    view === "list" && <List appId={appId} onSelectApp={(appId)=> {
                        icache.set("appId", appId);
                    }}/>
                }
                {
                    view === "new" && <New />
                }
                {
                    view === "edit" && <Edit />
                }
                {
                    view === "role_user" && <RoleUsers />
                }
                {
                    view === "role_resource" && <RoleResources appId={appId}/>
                }
            </section>
        </virtual>
        
    );
});
