import { create, tsx } from '@dojo/framework/core/vdom';
import { ResourceProperties } from '../../interfaces';

export interface DepartmentsProperties extends ResourceProperties{
}

const factory = create().properties<DepartmentsProperties>();

export default factory(function Departments({ properties }){
    const {  } = properties();
    return (
        <div>部门管理</div>
    );
});
