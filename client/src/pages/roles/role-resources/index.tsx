import { create, tsx } from '@dojo/framework/core/vdom';
import * as c from "bootstrap-classes";
import store from '../../../store';
import { changeViewProcess, clearGlobalTipProcess } from '../../../processes/pageProcesses';
import Tree from './Tree';
import icache from '@dojo/framework/core/middleware/icache';
import { clearRoleResourcesProcess, updateRoleResourcesProcess } from '../../../processes/roleResourceProcesses';
import { Toast } from '../../../utils/sweetalert2';

export interface RoleResourcesProperties {
    appId?: string;
}

const factory = create({store, icache}).properties<RoleResourcesProperties>();

export default factory(function RoleResources({ properties, middleware: {store, icache} }){
    const { appId } = properties();
    const{get, path, executor} = store;
    const role = get(path("role")) || {};
    const {name: roleName} = role;
    //const selectedNodeId = icache.getOrSet<string>("selectedNodeId", "-1");
    
    const globalTip = get(path("globalTip"));
    debugger;
    if(globalTip) {
        Toast.fire(globalTip);
        executor(clearGlobalTipProcess)({});
    }

    return (
        <div classes={[c.container_fluid]}>
            <div classes={[c.card]}>
                <div classes={[c.card_header]}>
                    <h3 classes={[c.card_title]}><strong>{roleName}</strong></h3>
                    <a href="#" classes={[c.float_right]} onclick={(event: MouseEvent) => {
                        event.stopPropagation();
                        event.preventDefault();
                        executor(clearRoleResourcesProcess)({});
                        executor(changeViewProcess)({ view: "list" });
                    }}>取消</a>
                </div>
                <div classes={[c.card_body]}>
                        <Tree appId={appId} 
                            onSelectNode={(id)=>{
                                icache.set("selectedNodeId", id);
                            }}
                        />
                </div>
                <div classes={[c.card_footer]}>
                        <button type="button" classes={[c.btn, c.btn_primary]} onclick={() => {
                            executor(updateRoleResourcesProcess)({appId: appId!});
                            // 保存成功后，不跳转，只给出成功提示
                        }}>保存</button>
                    </div>
            </div>
        </div>
    );
});
