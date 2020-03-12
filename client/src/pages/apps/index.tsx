import { create, tsx } from '@dojo/framework/core/vdom';

export interface AppsProperties {
}

const factory = create().properties<AppsProperties>();

export default factory(function Apps({ properties }){
    const {  } = properties();
    return (
        <div>APP管理</div>
    );
});
