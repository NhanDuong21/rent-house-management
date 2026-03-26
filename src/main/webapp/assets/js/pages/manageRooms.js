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
        document.querySelectorAll(".room-status-options button")
            .forEach(b => b.classList.remove("active"));
        btn.classList.add("active");

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

    if (input) {
        input.addEventListener("input", function () {
            if (timer) {
                clearTimeout(timer);
            }

            timer = setTimeout(() => {
                const form = input.closest("form");
                if (!form) {
                    return;
                }

                form.submit();
            }, 400);
        });
    }

    if (status) {
        status.addEventListener("change", function () {
            const form = status.closest("form");
            if (form) {
                form.submit();
            }
        });
    }
})();