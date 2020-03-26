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
		path: "apps?{resid}",
		outlet: "apps"
	},
	{
		path: "users?{resid}",
		outlet: "users"
	},
	{
		path: "roles?{resid}",
		outlet: "roles"
	}, 
	{
		path: "roles/new?{resid}&{appid}",
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
