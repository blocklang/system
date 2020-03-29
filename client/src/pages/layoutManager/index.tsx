import { create, tsx } from '@dojo/framework/core/vdom';
import {findIndex} from '@dojo/framework/shim/array';
import store from "../../store";
import BlankLayout from './BlankLayout';
import BasicLayout from './BasicLayout';

export interface LayoutProperties {
}

const factory = create({store }).properties<LayoutProperties>();

const notUseLayoutRoutes = ["login", "register"];

export default factory(function Layout({ properties, children, middleware: { store } }){
    const {  } = properties();
    const {get, path} = store;

    const isAuthenticated = !!get(path("session", "token"));
    if(!isAuthenticated) {
        return <BlankLayout>{children()}</BlankLayout>;
    }

    const outlet = get(path("routing", "outlet"));
    if(findIndex(notUseLayoutRoutes, item=>item === outlet) > -1) {
        return <BlankLayout>{children()}</BlankLayout>;
    }
    
    return <BasicLayout>{children()}</BasicLayout>
});
