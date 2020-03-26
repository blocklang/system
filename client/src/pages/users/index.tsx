import { create, tsx } from '@dojo/framework/core/vdom';
import * as c from "bootstrap-classes";
import { ResourceProperties } from '../../interfaces';
import store from "../../store";
import { loadPermissionProcess } from '../../processes/permissionProcesses';
import List from "./List";
import New from './New';
import Edit from './Edit';
import NotFound from "../error/404";

export interface UsersProperties extends ResourceProperties{
}


const factory = create({ store }).properties<UsersProperties>();

export default factory(function Users({ properties, middleware: { store } }){
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

    const view = get(path("pageView")) || "list";

    return (
        <virtual>
            <section classes={["content-header"]}>
                <div classes={[c.container_fluid]}>
                    <h1>用户管理</h1>
                </div>
            </section>
            <section classes={["content"]}>
            {
                view === "list" && <List />
            }
            {
                view === "new" && <New />
            }
            {
                view === "edit" && <Edit />
            }
            </section>
        </virtual>
        
    );
});
