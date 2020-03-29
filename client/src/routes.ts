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
		path: "depts?{resid}",
		outlet: "depts"
	}, 
	{
		path: "resources?{resid}",
		outlet: "resources"
	}
];
