import { create, tsx } from '@dojo/framework/core/vdom';
import store from "../../store";
import { redirectToLoginProcess } from '../../processes/routeProcesses';

export interface HomeProperties {
}

const factory = create({store}).properties<HomeProperties>();

export default factory(function Home({ properties, middleware: {store} }){
    const {  } = properties();

    // 1. 先显示加载中的图标
    // 2. 获取登录用户信息
    // 3. 如果用户未登录，则跳转到登录页面
    // 4. 如果用户已登录，则跳转到个人主页

    // 如果用户未登录，则跳转到登录页面

    const {get, path, executor} = store;
    const isAuthenticated = !!get(path("session"));
    if(!isAuthenticated) {
        executor(redirectToLoginProcess)({});
        return;
    }
    
    return (<div></div>);
});
