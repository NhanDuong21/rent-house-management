document.addEventListener("DOMContentLoaded", function () {



    /* ================= MODAL ================= */

    const modal = document.getElementById("billModal");

    if (modal) {

        const modalContent = modal.querySelector(".custom-modal-content");

        window.openModal = function () {
            modal.style.display = "flex";
            document.body.style.overflow = "hidden";
        };

        window.closeModal = function () {
            modal.style.display = "none";
            document.body.style.overflow = "auto";
        };

        modal.addEventListener("click", function (event) {
            if (modalContent && !modalContent.contains(event.target)) {
                window.closeModal();
            }
        });

    }
    /* ================= PAYMENT ================= */

    const paymentSelect = document.getElementById("paymentMethod");
    const qrContainer = document.getElementById("qrContainer");

    if (paymentSelect && qrContainer) {
            console.log("JS LOADED");
        //ẩn
        qrContainer.style.display = "none";

        paymentSelect.addEventListener("change", function () {

            if (this.value === "BANK") {
                //hiện
                qrContainer.style.display = "block";
            } else {
                qrContainer.style.display = "none";
            }

        });

    }

});