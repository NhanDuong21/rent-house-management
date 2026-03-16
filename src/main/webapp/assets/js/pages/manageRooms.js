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
    }).then(() => location.reload());
};

// SEARCH + FILTER
(function () {

    const input = document.getElementById("roomSearch");
    const status = document.getElementById("roomStatus");

    let timer = null;

    // SEARCH realtime
    if (input) {
        input.addEventListener("input", function () {

            if (timer)
                clearTimeout(timer);

            timer = setTimeout(() => {
                input.closest("form").submit();
            }, 400);

        });
    }

    // FILTER when change status
    if (status) {
        status.addEventListener("change", function () {
            status.closest("form").submit();
        });
    }

})();