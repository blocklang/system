import { library } from "@fortawesome/fontawesome-svg-core";

import { faUser } from "@fortawesome/free-solid-svg-icons/faUser";
import { faLock } from "@fortawesome/free-solid-svg-icons/faLock";

export function init() {
	library.add(faUser, faLock);
}
