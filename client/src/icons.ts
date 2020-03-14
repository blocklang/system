import { library } from "@fortawesome/fontawesome-svg-core";

import { faUser } from "@fortawesome/free-solid-svg-icons/faUser";
import { faLock } from "@fortawesome/free-solid-svg-icons/faLock";
import { faBars } from "@fortawesome/free-solid-svg-icons/faBars";
import { faCog } from "@fortawesome/free-solid-svg-icons/faCog";
import { faAngleLeft } from "@fortawesome/free-solid-svg-icons/faAngleLeft";
import { faAngleDown } from "@fortawesome/free-solid-svg-icons/faAngleDown";
import { faUserEdit } from "@fortawesome/free-solid-svg-icons/faUserEdit";
import { faUsers } from "@fortawesome/free-solid-svg-icons/faUsers";
import { faCircle } from "@fortawesome/free-regular-svg-icons/faCircle";
import { faSignOutAlt } from "@fortawesome/free-solid-svg-icons/faSignOutAlt";
import { faPlus } from "@fortawesome/free-solid-svg-icons/faPlus";
import {faExclamationTriangle} from "@fortawesome/free-solid-svg-icons/faExclamationTriangle";

export function init() {
	library.add(
		faUser, 
		faLock, 
		faBars, 
		faCog, 
		faAngleLeft, 
		faAngleDown, 
		faUserEdit,
		faUsers,
		faCircle, 
		faSignOutAlt,
		faPlus,
		faExclamationTriangle
	);
}