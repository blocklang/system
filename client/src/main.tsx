import renderer, { tsx } from '@dojo/framework/core/vdom';

import "admin-lte/plugins/icheck-bootstrap/icheck-bootstrap.css";
import "admin-lte/dist/css/adminlte.css";
import "bootstrap/dist/css/bootstrap.css";

import App from './App';
import { registry } from './store';

const r = renderer(() => <App />);
r.mount({ registry });
