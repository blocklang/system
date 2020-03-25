import { create, tsx } from '@dojo/framework/core/vdom';

export interface EditProperties {
    roleId: string
}

const factory = create().properties<EditProperties>();

export default factory(function Edit({ properties }){
    const {  } = properties();
    return (
        <div></div>
    );
});
