(function () {
  // ===== Auto hide server alert =====
  const alertWrap = document.getElementById("arAlertWrap");
  if (alertWrap) {
    setTimeout(() => {
      alertWrap.style.display = "none";
    }, 3000);
  }

  // ===== Search + Tabs filter (client-side) =====
  const input = document.getElementById("arSearchInput");
  const clearBtn = document.getElementById("arClearBtn");
  const rows = Array.from(document.querySelectorAll(".ar-row"));

  const tabs = Array.from(document.querySelectorAll(".ar-tab"));
  let tabStatus = "ALL";

  function applyFilters() {
    const q = (input?.value || "").trim().toLowerCase();

    rows.forEach((row) => {
      const room = (row.dataset.room || "").toLowerCase();
      const block = (row.dataset.block || "").toLowerCase();
      const status = (row.dataset.status || "").toLowerCase();

      const okSearch =
        !q || room.includes(q) || block.includes(q) || status.includes(q);
      const okTab = tabStatus === "ALL" || row.dataset.status === tabStatus;

      row.style.display = okSearch && okTab ? "" : "none";
    });
  }

  if (input) input.addEventListener("input", applyFilters);

  if (clearBtn) {
    clearBtn.addEventListener("click", () => {
      if (!input) return;
      input.value = "";
      applyFilters();
      input.focus();
    });
  }

  tabs.forEach((btn) => {
    btn.addEventListener("click", () => {
      tabs.forEach((x) => x.classList.remove("active"));
      btn.classList.add("active");
      tabStatus = btn.dataset.filter || "ALL";
      applyFilters();
    });
  });

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
      if (e.target && e.target.getAttribute("data-close") === "1") closeModal();
    });
  }

  document.addEventListener("keydown", (e) => {
    if (!modal || !modal.classList.contains("is-open")) return;
    if (e.key === "Escape") closeModal();
  });

  applyFilters();
})();
