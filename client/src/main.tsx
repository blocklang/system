import renderer, { tsx } from '@dojo/framework/core/vdom';

import App from './App';
import { registry } from './store';

const r = renderer(() => <App />);
r.mount({ registry });
