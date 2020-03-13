import global from '@dojo/framework/shim/global';
import { createProcess } from '@dojo/framework/stores/process';
import { commandFactory } from './utils';
import * as request from "../utils/request";
import { replace, remove } from '@dojo/framework/stores/state/operations';
import { Session } from '../interfaces';
/**
 * 获取登录用户信息
 */
export const getCurrentUserCommand = commandFactory(async ({ path }) => {
	const response = await request.get("/user");
	const json: Session = await response.json();
	if (!response.ok) {
		// 没有获取到登录用户信息，则清空缓存的用户信息
		global.sessionStorage.removeItem('blocklang-session');
		return [remove(path('session'))];
	}

	// 如果用户未登录，也会返回 json 对象
	if (json.username) {
		global.sessionStorage.setItem('blocklang-session', JSON.stringify(json));
	} else {
		// 说明用户未登录，所以清空登录信息
		global.sessionStorage.removeItem('blocklang-session');
	}

	return [replace(path('session'), json)];
});

export const getCurrentUserProcess = createProcess('get-current-user', [getCurrentUserCommand]);