import createStoreMiddleware from "@dojo/framework/core/middleware/store";
import global from "@dojo/framework/shim/global";
import { State } from "./interfaces";
import Store from '@dojo/framework/stores/Store';
import Registry from '@dojo/framework/core/Registry';
import { registerRouterInjector } from '@dojo/framework/routing/RouterInjector';
import { StateHistory } from '@dojo/framework/routing/history/StateHistory';
import config from "./routes";
import { changeRouteProcess } from './processes/routeProcesses';
import { setSessionProcess } from './processes/loginProcesses';

export const registry = new Registry();
const router = registerRouterInjector(config, registry, { HistoryManager: StateHistory });

export default createStoreMiddleware<State>((store: Store) => {

    const session = global.sessionStorage.getItem("blocklang-session");
    if(session) {
        setSessionProcess(store)({session: JSON.parse(session)});
    }

    router.on("nav", ({outlet, context}: any) => {
        changeRouteProcess(store)({outlet, context});
    });

    function onRouteChange() {
		const outlet = store.get(store.path("routing", "outlet"));
		const params = store.get(store.path("routing", "params"));
		if (outlet) {
			const link = router.link(outlet, params);
			if (link !== undefined) {
				router.setPath(link);
			}
		}
	}

	store.onChange(store.path("routing", "outlet"), onRouteChange);
	store.onChange(store.path("routing", "params"), onRouteChange);
});
