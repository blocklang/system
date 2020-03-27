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
		path: "menus?{resid}",
		outlet: "resources"
	}, 
	{
		path: "depts?{resid}",
		outlet: "depts"
	}
];
