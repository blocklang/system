import { tsx, create } from '@dojo/framework/core/vdom';
import theme from '@dojo/framework/core/middleware/theme';
import Outlet from '@dojo/framework/routing/Outlet';
import dojo from '@dojo/themes/dojo';

import Home from './pages/home';
import Login from './pages/login';
import Register from './pages/register';
import * as icons from "./icons";
import * as css from './App.m.css';

icons.init();

const factory = create({ theme });

export default factory(function App({ middleware: { theme } }) {
	if (!theme.get()) {
		theme.set(dojo);
	}
	return (
		<div classes={[css.root]}>
			<div>
				<Outlet id="login" renderer={() => <Login />} />
				<Outlet id="register" renderer={() => <Register />} />
				<Outlet id="home" renderer={() => <Home />} />
			</div>
		</div>
	);
});
