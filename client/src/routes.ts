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
		path: "users",
		outlet: "users"
	}, 
	{
		path: "roles",
		outlet: "roles"
	}, 
	{
		path: "menus",
		outlet: "menus"
	}, 
	{
		path: "departments",
		outlet: "departments"
	},
	{
		path: "apps",
		outlet: "apps"
	},
	{
		path: "apps/new",
		outlet: "new-app"
	},
	{
		path: "apps/{appId}/edit",
		outlet: "edit-app"
	}
];
