import { create, tsx } from '@dojo/framework/core/vdom';

export interface UsersProperties {
}

const factory = create().properties<UsersProperties>();

export default factory(function Users({ properties }){
    const {  } = properties();
    return (
        <div>用户管理</div>
    );
});
