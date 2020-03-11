import { create, tsx } from '@dojo/framework/core/vdom';

export interface MenusProperties {
}

const factory = create().properties<MenusProperties>();

export default factory(function Menus({ properties }){
    const {  } = properties();
    return (
        <div>菜单管理</div>
    );
});
