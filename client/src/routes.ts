export default [
	{
		path: '',
		outlet: 'home',
		defaultRoute: true
	},
	{
		path: 'login',
		outlet: 'login'
	},
	{
		path: 'register',
		outlet: 'register'
	}, 
	{
		path: "apps?{resid}&{page}",
		outlet: "apps"
	},
	{
		path: "apps/new?{resid}",
		outlet: "new-app"
	},
	{
		path: "apps/{appId}/edit?{resid}&{page}",
		outlet: "edit-app"
	},
	{
		path: "users?{resid}&{page}",
		outlet: "users"
	},
	{
		path: "users/new?{resid}",
		outlet: "new-user"
	},
	{
		path: "users/{userId}/edit?{resid}&{page}",
		outlet: "edit-user"
	}, 
	{
		path: "roles?{resid}",
		outlet: "roles"
	}, 
	{
		path: "roles/new?{appid}",
		outlet: "new-role"
	},
	{
		path: "roles/{roleId}/edit",
		outlet: "edit-role"
	},
	{
		path: "menus?{resid}",
		outlet: "resources"
	}, 
	{
		path: "depts?{resid}",
		outlet: "depts"
	}
];
