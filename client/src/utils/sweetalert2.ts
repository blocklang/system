import Swal from "sweetalert2";

export const Toast = Swal.mixin({
	toast: true,
	showCloseButton: false,
	icon: "success",
	showConfirmButton: false,
	timer: 2000,
	position: "top",
});
