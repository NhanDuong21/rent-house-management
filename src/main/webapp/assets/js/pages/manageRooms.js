let selectedRoomId = null;
let selectedStatus = null;

const modal = document.getElementById("statusModal");

document.querySelectorAll(".room-status-btn").forEach(btn => {

    btn.addEventListener("click", () => {

        selectedRoomId = btn.dataset.roomId;
        selectedStatus = btn.dataset.status;

        modal.style.display = "flex";

    });

});

document.querySelectorAll(".room-status-options button").forEach(btn => {

    btn.addEventListener("click", () => {

        selectedStatus = btn.dataset.status;

    });

});

document.getElementById("cancelBtn").onclick = () => {

    modal.style.display = "none";

};

document.getElementById("saveBtn").onclick = () => {

    fetch("rooms", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `roomId=${selectedRoomId}&status=${selectedStatus}`
    })
            .then(() => location.reload());

};
