import { create, tsx } from '@dojo/framework/core/vdom';
import FontAwesomeIcon from "dojo-fontawesome/FontAwesomeIcon";
import icache from '@dojo/framework/core/middleware/icache';
import Link from '@dojo/framework/routing/Link';
import {findIndex} from '@dojo/framework/shim/array';
import store from "../../store";
import * as c from "bootstrap-classes";
import { IconProp} from '@fortawesome/fontawesome-svg-core';
import * as css from "./BasicLayout.m.css";
import { logoutProcess } from '../../processes/loginProcesses';
import ActiveLink from '@dojo/framework/routing/ActiveLink';

interface MenuInfo {
    id: number;
    parentId: number;
    name: string;
    url?: string;
    icon: IconProp;
    menuType: MenuType;
}

/**
 * * 目录 1
 * * 菜单 2
 * * 按钮 3
 */
type MenuType = 1 | 2 | 3;

const menus: MenuInfo[] = [
    {
        id: 1,
        parentId: -1,
        name: "系统管理",
        icon: "cog",
        menuType: 1
    }, {
        id: 2,
        parentId: 1,
        name: "用户管理",
        url: "users",
        icon: "user-edit",
        menuType: 2
    }, {
        id: 3,
        parentId: 1,
        name: "角色管理",
        url: "roles",
        icon: "users",
        menuType: 2
    }, {
        id: 4,
        parentId: 1,
        name: "菜单管理",
        url: "menus",
        icon: ["far","circle"],
        menuType: 2
    }, {
        id: 5,
        parentId: 1,
        name: "部门管理",
        url: "departments",
        icon: ["far","circle"],
        menuType: 2
    }, {
        id: 6,
        parentId: 1,
        name: "APP管理",
        url: "apps",
        icon: ["far","circle"],
        menuType: 2
    }
]

export interface BasicLayoutProperties {
}

const factory = create({ icache, store }).properties<BasicLayoutProperties>();

export default factory(function BasicLayout({ properties, children, middleware: { icache, store } }){
    const {} = properties;
    const {get, path, executor} = store;
    const sidebarOpen = icache.getOrSet<boolean>("sidebarOpen", true);
    const {username} = get(path("session"));
    const outlet = get(path("routing", "outlet"));

    const activeMenuId = icache.getOrSet<number>("activeMenuId", -1);
    let activeMenuIndex = -1;
    if(activeMenuId !== -1) {
        activeMenuIndex =  findIndex(menus, item => item.id === activeMenuId);
    }
    if(activeMenuIndex == -1) {
        activeMenuIndex =  findIndex(menus, item => item.url === outlet);
    }

    const activeMenuPath:number[]= [];
    // 不需要设置叶节点，因为 ActiveLink 已支持自动激活
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
    
    
    debugger;

    const getChildren = (id: number) => {
        return menus.filter(item => item.parentId === id);
    }

    const renderMenuItem = (menuItem: MenuInfo) => {
        const menuId = menuItem.id;
        const isFolder = menuItem.menuType === 1;
        
        if(isFolder) { // 目录
            const isActive = findIndex(activeMenuPath, item => item === menuId) > -1;
            debugger;
            const openState = icache.getOrSet<boolean>(`folderOpenState-${menuId}`, false);
            
            return (<li classes={[c.nav_item, "has-treeview", openState? "menu-open": undefined]}>
                <a href="#" classes={[c.nav_link,isActive ?c.active : undefined]} onclick={(event: MouseEvent<EventTarget>)=>{
                    event.preventDefault();
                    icache.set<boolean>(`folderOpenState-${menuId}`, !openState);
                    icache.set<number>("activeMenuId", menuId);
                }}>
                    <FontAwesomeIcon classes={["nav-icon", css.navIcon]} icon={menuItem.icon}/>
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
        return (<li classes={[c.nav_item]}>
            <ActiveLink to={menuItem.url!} activeClasses={[c.active]} classes={[c.nav_link]}>
                <FontAwesomeIcon classes={["nav-icon", css.navIcon]} icon={menuItem.icon}/>
                <p>
                    {menuItem.name}
                </p>
            </ActiveLink>
        </li>);
    }

    const renderMenus = () => {
        return getChildren(-1).map(menuItem => renderMenuItem(menuItem));
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
                        <span classes={["brand-text",c.font_weight_light]}>xx平台</span>
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
