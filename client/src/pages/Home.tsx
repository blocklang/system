import { create, tsx } from '@dojo/framework/core/vdom';

export interface HomeProperties {
}

const factory = create().properties<HomeProperties>();

export default factory(function Home({ properties }){
    const {  } = properties();
    return (
        <div></div>
    );
});
