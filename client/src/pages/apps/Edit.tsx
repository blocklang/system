import { create, tsx } from '@dojo/framework/core/vdom';
import * as c from 'bootstrap-classes';
import store from '../../store';
import { updateAppProcess, setAppFieldProcess } from '../../processes/appProcesses';
import { changeViewProcess } from '../../processes/pageProcesses';

export interface EditProperties{

}

const factory = create({ store }).properties<EditProperties>();

export default factory(function Edit({ properties, middleware: {  store } }){
    const {get, path, executor} = store;
    const app = get(path("app")) || {};

    return (
        <div classes={[c.container_fluid]}>
            <div classes={[c.card]}>
                <div classes={[c.card_header]}>
                    <h3 classes={[c.card_title]}>编辑APP</h3>
                </div>
                <form role="form">
                    <div classes={[c.card_body]}>
                        <div classes={[c.form_group]}>
                            <label for="iptName">名称<small classes={[c.text_muted, c.ml_1]}>必填</small></label>
                            <input type="text" classes={[c.form_control]} id="iptName" value={app.name} oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                executor(setAppFieldProcess)({field: "name", value: event.target.value})
                            }}/>
                        </div>
                        <div classes={[c.form_group]}>
                            <label for="iptIcon">Icon</label>
                            <input type="text" classes={[c.form_control]} id="iptIcon" value={app.icon} oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                executor(setAppFieldProcess)({field: "icon", value: event.target.value})
                            }}/>
                        </div>
                        <div classes={[c.form_group]}>
                            <label for="iptUrl">url</label>
                            <input type="text" classes={[c.form_control]} id="iptUrl" value={app.url} oninput={(event: KeyboardEvent<HTMLInputElement>)=>{
                                executor(setAppFieldProcess)({field: "url", value: event.target.value})
                            }}/>
                        </div>
                        <div classes={[c.form_group]}>
                            <label for="iptDescription">描述</label>
                            <textarea classes={[c.form_control]} id="iptDescription" value={app.description} oninput={(event: KeyboardEvent<HTMLTextAreaElement>)=>{
                                executor(setAppFieldProcess)({field: "description", value: event.target.value})
                            }}/>
                        </div>
                    </div>
                    <div classes={[c.card_footer]}>
                        <button type="button" classes={[c.btn, c.btn_secondary, c.mr_2]} onclick={()=>{
                            // 要保留在原页面
                            executor(changeViewProcess)({view: "list"});
                        }}>取消</button>
                        <button type="button" classes={[c.btn, c.btn_primary]} onclick={() => {
                            // 更新成功之后要跳转，并在刷新列表
                            executor(updateAppProcess)({});
                        }}>保存</button>
                    </div>
                </form>
            </div>
        </div>
    );
});
