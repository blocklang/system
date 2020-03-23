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
import Departments from './pages/departments';

import Users from './pages/users';
import NewUser from './pages/users/New';
import EditUser from './pages/users/Edit';

import Roles from './pages/roles';
import NewRole from './pages/roles/New';
import EditRole from './pages/roles/Edit';

import Apps from './pages/apps';
import NewApp from './pages/apps/New';
import EditApp from './pages/apps/Edit';
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
				<Outlet id="login" renderer={() => <Login />} />
				<Outlet id="register" renderer={() => <Register />} />
				<Outlet id="home" renderer={() => <Home />} />

				<Outlet id="apps" renderer={(details) => <Apps resId={details.queryParams.resid} page={Number(details.queryParams.page)}/>} />
				<Outlet id="new-app" renderer={(details) => <NewApp resId={details.queryParams.resid}/>} />
				<Outlet id="edit-app" renderer={(details) => <EditApp resId={details.queryParams.resid} appId={details.params.appId}/>} />

				<Outlet id="users" renderer={(details) => <Users resId={details.queryParams.resid} page={Number(details.queryParams.page)}/>} />
				<Outlet id="new-user" renderer={(details) => <NewUser resId={details.queryParams.resid}/>} />
				<Outlet id="edit-user" renderer={(details) => <EditUser resId={details.queryParams.resid} userId={details.params.userId} page={Number(details.queryParams.page)}/>} />

				<Outlet id="roles" renderer={() => <Roles />} />
				<Outlet id="new-role" renderer={(details) => {
					debugger;
					return <NewRole appId={details.queryParams.appid}/>
				}} />
				<Outlet id="edit-role" renderer={(details) => <EditRole roleId={details.params.roleId}/>} />
				
				<Outlet id="menus" renderer={() => <Menus />} />
				<Outlet id="departments" renderer={() => <Departments />} />

				

				<Outlet id="errorOutlet" renderer={() => <NotFoundPage />}/>
			</LayoutManager>
		</div>
	);
});
