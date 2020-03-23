import { create, tsx } from '@dojo/framework/core/vdom';
import FontAwesomeIcon from "dojo-fontawesome/FontAwesomeIcon";
import icache from '@dojo/framework/core/middleware/icache';
import Link from '@dojo/framework/routing/Link';
import {findIndex} from '@dojo/framework/shim/array';
import store from "../../store";
import * as c from "bootstrap-classes";
import { IconProp} from '@fortawesome/fontawesome-svg-core';
import * as css from "./BasicLayout.m.css";
import { logoutProcess, loadUserMenusProcess } from '../../processes/loginProcesses';
import ActiveLink from '@dojo/framework/routing/ActiveLink';
import { Menu } from '../../interfaces';

export interface BasicLayoutProperties {
}

const factory = create({ icache, store }).properties<BasicLayoutProperties>();

const rootParentId = "-1";

export default factory(function BasicLayout({ properties, children, middleware: { icache, store } }){
    const {} = properties;
    const {get, path, executor} = store;
    const sidebarOpen = icache.getOrSet<boolean>("sidebarOpen", true);
    const {username} = get(path("session"));
    const outlet = get(path("routing", "outlet"));
    // menus 的值包括：
    // 1. undefined 表示正在查询
    // 2. length = 0 表示查询完成，只是记录数为 0 
    // 3. length > 0 表示查询完成，查出了记录
    const menus = get(path("menus"));
    console.log("menus", menus)
    if(!menus) {
        // 默认加载根节点下的直属子资源
        executor(loadUserMenusProcess)({resourceId: rootParentId});
        // TODO: 显示正在加载中
        return;
    }

    const activeMenuId = icache.getOrSet<string>("activeMenuId", rootParentId);
    let activeMenuIndex = -1;
    // FIXME: 是否应该从判断 outlet 开始?
    if(activeMenuId !== rootParentId) { // 不显示根节点
        activeMenuIndex =  findIndex(menus, item => item.id === activeMenuId);
    }
    if(activeMenuIndex == -1) {
        activeMenuIndex =  outlet?findIndex(menus, item => item.url === outlet):-1;
    }

    const activeMenuPath:string[]= [];
    // 不需要设置叶节点，因为 ActiveLink 已支持自动显示 active 样式
    if(activeMenuIndex > -1){
        let currentMenuId = menus[activeMenuIndex].id;
        for(let i = activeMenuIndex; i >= 0; i--) {
            const currentMenu = menus[i]
            if(currentMenuId === currentMenu.id) {
                activeMenuPath.push(currentMenuId);
            }
            currentMenuId = currentMenu.parentId;
        }
        activeMenuPath.reverse();
    }

    const getChildren = (id: string) => {
        return menus.filter(item => item.parentId === id);
    }

    const renderMenuItem = (menuItem: Menu) => {
        const menuId = menuItem.id;
        
        const icon = menuItem.icon;
        const isFolder = menuItem.type === "01";

        let iconName: IconProp;
        const iconArray = icon.split(" ");
        if(iconArray.length == 2) {
            iconName = iconArray as IconProp;
        }else {
            iconName = iconArray[0] as IconProp
        }
        
        if(isFolder) { // 目录
            const isActive = findIndex(activeMenuPath, item => item === menuId) > -1;
            const openState = icache.getOrSet<boolean>(`folderOpenState-${menuId}`, false);
            
            return (<li key={menuId} classes={[c.nav_item, "has-treeview", openState? "menu-open": undefined]}>
                <a href="#" classes={[c.nav_link,isActive ?c.active : undefined]} onclick={(event: MouseEvent<EventTarget>)=>{
                    event.preventDefault();
                    icache.set<boolean>(`folderOpenState-${menuId}`, !openState);
                    icache.set<string>("activeMenuId", menuId);
                    executor(loadUserMenusProcess)({resourceId: menuId});
                }}>
                    <FontAwesomeIcon classes={["nav-icon", css.navIcon]} icon={iconName}/>
                    <p>
                        {menuItem.name}
                        <FontAwesomeIcon classes={["right"]} icon={openState?"angle-down":"angle-left"}/>
                    </p>
                </a>
                {
                    openState && (<ul classes={[c.nav, "nav-treeview"]}> {getChildren(menuId).map(item => renderMenuItem(item))}</ul>)
                }
            </li>);
        }

        // 菜单
        return (<li key={menuId} classes={[c.nav_item]}>
            <ActiveLink to={menuItem.url!} params={{resid: menuId, page: "0"}} activeClasses={[c.active]} classes={[c.nav_link]}>
                <FontAwesomeIcon classes={["nav-icon", css.navIcon]} icon={iconName}/>
                <p>
                    {menuItem.name}
                </p>
            </ActiveLink>
        </li>);
    }

    const renderMenus = () => {
        return getChildren(rootParentId).map(menuItem => renderMenuItem(menuItem));
    }

    return (
        <div classes={["hold-transition", "sidebar-mini", "layout-navbar-fixed","layout-fixed", sidebarOpen?undefined:"sidebar-collapse"]}>
            <div classes={["wrapper"]}>
                <nav classes={["main-header", c.navbar, "navbar-expand", "navbar-white", "navbar-light"]}>
                    <ul classes={[c.navbar_nav]}>
                        <li classes={[c.nav_item]}>
                            <a classes={[c.nav_link]} data-widget="pushmenu" href="#" role="button" onclick={(event: MouseEvent<EventTarget>)=> {
                                event.preventDefault();
                                icache.set<boolean>("sidebarOpen", !sidebarOpen);
                            }}>
                                <FontAwesomeIcon icon="bars"/>
                            </a>
                        </li>
                    </ul>
                    <ul classes={[c.navbar_nav, c.ml_auto]}>
                        <span classes={["navbar-text"]}>{username}</span>
                        <li classes={[c.nav_item]}>
                            <a href="#" classes={[c.nav_link]} title="注销" onclick={(event: MouseEvent<EventTarget>)=>{
                                event.preventDefault();
                                executor(logoutProcess)({});
                            }}>
                                <FontAwesomeIcon icon="sign-out-alt"/>
                            </a>
                        </li>
                    </ul>
                </nav>
                <aside classes={["main-sidebar", "sidebar-dark-primary", "elevation-4"]}>
                    <Link to="home" classes={["brand-link"]}>
                        <span classes={["brand-text",c.font_weight_light]}>BlockLang 后台管理</span>
                    </Link>
                    <div class="sidebar">
                        <nav classes={[c.mt_2]}>
                            <ul classes={[c.nav, "nav-pills", "nav-sidebar", c.flex_column]} data-widget="treeview" role="menu" data-accordion="false">
                                {renderMenus()}
                            </ul>
                        </nav>
                    </div>
                </aside>
                <div classes={["content-wrapper"]}>
                    {children()}
                </div>
                <footer classes={["main-footer"]}>

                </footer>
                <aside classes={["control-sidebar", "control-sidebar-dark"]}>

                </aside>
            </div>
        </div>
    );
});
