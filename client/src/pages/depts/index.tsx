import { create, tsx } from '@dojo/framework/core/vdom';

export interface DepartmentsProperties {
}

const factory = create().properties<DepartmentsProperties>();

export default factory(function Departments({ properties }){
    const {  } = properties();
    return (
        <div>部门管理</div>
    );
});
