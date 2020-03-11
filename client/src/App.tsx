import { tsx, create } from '@dojo/framework/core/vdom';
import theme from '@dojo/framework/core/middleware/theme';
import Outlet from '@dojo/framework/routing/Outlet';
import dojo from '@dojo/themes/dojo';


import * as icons from "./icons";
import * as css from './App.m.css';

import Home from './pages/home';
import Login from './pages/login';
import Register from './pages/register';
import Users from './pages/users';
import Roles from './pages/roles';
import Menus from './pages/menus';
import Departments from './pages/departments';
import LayoutManager from "./pages/layoutManager";


icons.init();

const factory = create({ theme });

export default factory(function App({ middleware: { theme } }) {
	if (!theme.get()) {
		theme.set(dojo);
	}
	return (
		<div classes={[css.root]}>
			<LayoutManager>
				<Outlet id="login" renderer={() => <Login />} />
				<Outlet id="register" renderer={() => <Register />} />
				<Outlet id="home" renderer={() => <Home />} />
				<Outlet id="users" renderer={() => <Users />} />
				<Outlet id="roles" renderer={() => <Roles />} />
				<Outlet id="menus" renderer={() => <Menus />} />
				<Outlet id="departments" renderer={() => <Departments />} />
			</LayoutManager>
		</div>
	);
});
