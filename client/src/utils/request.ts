import { baseUrl } from "../config";

//TODO: 进一步封装 fetch，提供处理公共错误的地方
// 参考：https://dev.to/webcoderkz/my-fetch-wrapper-with-async-await-and-typescript-1158
// https://www.jianshu.com/p/cba66a50aa9b
// https://segmentfault.com/a/1190000014733098

export function getHeaders(token?: string): { [key: string]: string } {
	const headers: { [key: string]: string } = {
		//'X-Requested-With': 'FetchApi',
		"Content-Type": "application/json;charset=UTF-8",
	};
	if (token) {
		headers["Authorization"] = `Token ${token}`;
	}

	return headers;
}

export function get(url: string, token?: string): Promise<Response> {
	return fetch(`${baseUrl}${url}`, {
		headers: getHeaders(token),
	});
}

export function post(url: string, jsonData: any, token?: string): Promise<Response> {
	return fetch(`${baseUrl}${url}`, {
		method: "POST",
		body: JSON.stringify(jsonData),
		headers: getHeaders(token),
	});
}

export function put(url: string, jsonData: any, token?: string): Promise<Response> {
	return fetch(`${baseUrl}${url}`, {
		method: "PUT",
		body: JSON.stringify(jsonData),
		headers: getHeaders(token),
	});
}
