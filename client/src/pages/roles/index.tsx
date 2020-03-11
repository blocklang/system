import { create, tsx } from '@dojo/framework/core/vdom';

export interface RolesProperties {
}

const factory = create().properties<RolesProperties>();

export default factory(function Roles({ properties }){
    const {  } = properties();
    return (
        <div>
            角色管理
        </div>
    );
});
