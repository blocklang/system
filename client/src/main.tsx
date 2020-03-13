import renderer, { tsx } from '@dojo/framework/core/vdom';

import "admin-lte/plugins/icheck-bootstrap/icheck-bootstrap.css";
import "admin-lte/dist/css/adminlte.css";
// TODO: 如果 admin-lte 中的 bootstrap 升级到4.4.1 后，则不再导入 bootstrap
// import "bootstrap/dist/css/bootstrap.css";

import App from './App';
import { registry } from './store';

const r = renderer(() => <App />);
r.mount({ registry });
