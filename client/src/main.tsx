import renderer, { tsx } from '@dojo/framework/core/vdom';
import Registry from '@dojo/framework/core/Registry';
import { registerRouterInjector } from '@dojo/framework/routing/RouterInjector';
import { StateHistory } from '@dojo/framework/routing/history/StateHistory';
import "bootstrap/dist/css/bootstrap.css"
import '@dojo/themes/dojo/index.css';

import routes from './routes';
import App from './App';

const registry = new Registry();
registerRouterInjector(routes, registry, { HistoryManager: StateHistory });

const r = renderer(() => <App />);
r.mount({ registry });
