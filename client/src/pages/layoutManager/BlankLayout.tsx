import { create, tsx } from "@dojo/framework/core/vdom";

export interface BlankLayoutProperties {}

const factory = create().properties<BlankLayoutProperties>();

export default factory(function BlankLayout({ properties, children }) {
	const {} = properties();
	return <virtual>{children()}</virtual>;
});
