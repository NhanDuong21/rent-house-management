(function () {
  // ===== Auto hide server alert =====
  const alertWrap = document.getElementById("arAlertWrap");
  if (alertWrap) {
    setTimeout(() => {
      alertWrap.style.display = "none";
    }, 3000);
  }

  // ===== Delete modal (Set INACTIVE) =====
  const modal = document.getElementById("arDeleteModal");
  const delGo = document.getElementById("arDeleteGo");
  const delText = document.getElementById("arDeleteText");

  function openModal(url, roomName) {
    if (!modal) return;
    modal.classList.add("is-open");
    modal.setAttribute("aria-hidden", "false");
    document.body.style.overflow = "hidden";

    if (delGo) delGo.setAttribute("href", url || "#");
    if (delText) {
      delText.textContent =
        'Xác nhận ngừng kinh doanh phòng "' +
        (roomName || "") +
        '"? Phòng sẽ chuyển sang trạng thái INACTIVE.';
    }
  }

  function closeModal() {
    if (!modal) return;
    modal.classList.remove("is-open");
    modal.setAttribute("aria-hidden", "true");
    document.body.style.overflow = "";
  }

  document.querySelectorAll(".js-delete-room").forEach((btn) => {
    btn.addEventListener("click", () => {
      const url = btn.getAttribute("data-delete-url");
      const name = btn.getAttribute("data-room-name");
      openModal(url, name);
    });
  });

  if (modal) {
    modal.addEventListener("click", (e) => {
      if (e.target && e.target.getAttribute("data-close") === "1") {
        closeModal();
      }
    });
  }

  document.addEventListener("keydown", (e) => {
    if (!modal || !modal.classList.contains("is-open")) return;
    if (e.key === "Escape") closeModal();
  });
})();
