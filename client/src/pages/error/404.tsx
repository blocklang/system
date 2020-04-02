import { create, tsx } from "@dojo/framework/core/vdom";
import * as c from "bootstrap-classes";
import FontAwesomeIcon from "dojo-fontawesome/FontAwesomeIcon";
import Link from "@dojo/framework/routing/Link";

export interface NotFoundProperties {}

const factory = create().properties<NotFoundProperties>();

export default factory(function NotFound({ properties }) {
	const {} = properties();
	return (
		<virtual>
			<section classes={["content-header"]}></section>
			<section classes={["content"]}>
				<div classes={["error-page"]}>
					<h2 classes={["headline", c.text_warning, c.text_center, c.float_none]}>404</h2>
					<div classes={["error-content", c.text_center, c.ml_0]}>
						<h3>
							<FontAwesomeIcon icon="exclamation-triangle" classes={[c.text_warning]} />{" "}
							你访问的页面不存在。
						</h3>
						<div classes={[c.mt_5]}>
							<Link classes={[c.btn, c.btn_primary]} to="home">
								返回首页
							</Link>
						</div>
					</div>
				</div>
			</section>
		</virtual>
	);
});
