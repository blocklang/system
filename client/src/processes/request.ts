
export function getHeaders():  { [key: string]: string } {
	const headers: { [key: string]: string } = {
        'X-Requested-With': 'FetchApi',
        'Content-type': 'application/json;charset=UTF-8'
	};

	return headers;
}

export function post(url: string, data: any): Promise<Response> {
    return fetch(url, {
        method: "POST",
        body: JSON.stringify(data),
        headers: getHeaders()
    });
}