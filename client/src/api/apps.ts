import * as request from '../utils/request';

export async function newApp() {
    await request.post("apps", {})
}