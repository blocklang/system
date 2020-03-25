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
