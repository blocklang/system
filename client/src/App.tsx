import { tsx, create } from '@dojo/framework/core/vdom';
import theme from '@dojo/framework/core/middleware/theme';
import Outlet from '@dojo/framework/routing/Outlet';
import dojo from '@dojo/themes/dojo';

import * as icons from "./icons";
import * as css from './App.m.css';

import LayoutManager from "./pages/layoutManager";
import Home from './pages/home';
import Login from './pages/login';
import Register from './pages/register';

import Menus from './pages/menus';
import Depts from './pages/depts';

import Users from './pages/users';

import Roles from './pages/roles';

import Apps from './pages/apps';
import NotFoundPage from './pages/error/404';

icons.init();

const factory = create({ theme });

export default factory(function App({ middleware: { theme } }) {
	if (!theme.get()) {
		theme.set(dojo);
	}
	return (
		<div classes={[css.root]}>
			<LayoutManager>
				<Outlet id="login" key="login" renderer={() => <Login />} />
				<Outlet id="register" key="register" renderer={() => <Register />} />
				<Outlet id="home" key="home" renderer={() => <Home />} />

				<Outlet id="apps" key="apps" renderer={(details) => <Apps resId={details.queryParams.resid}/>} />
				<Outlet id="users" key="users" renderer={(details) => <Users resId={details.queryParams.resid}/>} />
				<Outlet id="roles" key="roles" renderer={(details) => <Roles resId={details.queryParams.resid}/>} />
	
				<Outlet id="menus" key="menus" renderer={() => <Menus />} />
				<Outlet id="depts" key="depts" renderer={() => <Depts />} />

				<Outlet id="errorOutlet" key="errorOutlet" renderer={() => <NotFoundPage />}/>
			</LayoutManager>
		</div>
	);
});
