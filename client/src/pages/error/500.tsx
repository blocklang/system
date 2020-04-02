import { create, tsx } from "@dojo/framework/core/vdom";
import * as c from "bootstrap-classes";
import FontAwesomeIcon from "dojo-fontawesome/FontAwesomeIcon";
import Link from "@dojo/framework/routing/Link";

export interface ServerErrorProperties {}

const factory = create().properties<ServerErrorProperties>();

export default factory(function ServerError({ properties }) {
	const {} = properties();
	return (
		<virtual>
			<section classes={["content-header"]}></section>
			<section classes={["content"]}>
				<div classes={["error-page"]}>
					<h2 classes={["headline", c.text_danger, c.text_center, c.float_none]}>500</h2>
					<div classes={["error-content", c.text_center, c.ml_0]}>
						<h3>
							<FontAwesomeIcon icon="exclamation-triangle" classes={[c.text_danger]} />{" "}
							抱歉，服务器出错了。
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
