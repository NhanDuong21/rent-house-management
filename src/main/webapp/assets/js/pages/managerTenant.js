(function () {
  const ctx = document.body.dataset?.ctx || ""; // optional nếu bạn muốn set
  const editModal = document.getElementById("editModal");
  const confirmDialog = document.getElementById("confirmDialog");
  const toggleDialog = document.getElementById("toggleStatusDialog");
  const toast = document.getElementById("errorToast");

  const toastMessage = document.getElementById("toastMessage");
  const toastCloseBtn = document.getElementById("toastCloseBtn");

  const editForm = document.getElementById("editTenantForm");

  // Edit modal fields
  const fTenantId = document.getElementById("modal_tenantId");
  const fFullName = document.getElementById("modal_fullName");
  const fIdentity = document.getElementById("modal_identityCode");
  const fPhone = document.getElementById("modal_phoneNumber");
  const fEmail = document.getElementById("modal_email");
  const fDob = document.getElementById("modal_dateOfBirth");
  const fGender = document.getElementById("modal_gender");
  const fAddress = document.getElementById("modal_address");

  // Confirm buttons
  const openConfirmBtn = document.getElementById("openConfirmBtn");
  const confirmSaveBtn = document.getElementById("confirmSaveBtn");

  // Toggle dialog elements
  const toggleSubtitle = document.getElementById("toggleStatusSubtitle");
  const toggleOkBtn = document.getElementById("toggleStatusOkBtn");
  const toggleIcon = document.getElementById("toggleStatusIcon");

  let toggleTenantId = null;
  let toggleNextStatus = null;

  /* ---------------- Toast ---------------- */
  function showToast(msg) {
    if (!toast || !toastMessage) return;
    toastMessage.textContent = msg || "";
    toast.classList.add("show");
    setTimeout(() => hideToast(), 4000);
  }

  function hideToast() {
    if (!toast) return;
    toast.classList.remove("show");
  }

  if (toastCloseBtn) {
    toastCloseBtn.addEventListener("click", hideToast);
  }

  // Read ?error=... and show toast, then remove from URL
  (function readErrorParam() {
    const params = new URLSearchParams(window.location.search);
    const err = params.get("error");
    if (err) {
      showToast(decodeURIComponent(err));
      const url = new URL(window.location.href);
      url.searchParams.delete("error");
      window.history.replaceState({}, "", url);
    }
  })();

  /* ---------------- Helpers: open/close overlays ---------------- */
  function openOverlay(el) {
    if (!el) return;
    el.classList.add("active");
    el.setAttribute("aria-hidden", "false");
    document.body.style.overflow = "hidden";
  }

  function closeOverlay(el) {
    if (!el) return;
    el.classList.remove("active");
    el.setAttribute("aria-hidden", "true");
    document.body.style.overflow = "";
  }

  /* ---------------- Edit Modal ---------------- */
  function openEditModalFromButton(btn) {
    const id = btn.dataset.tenantId || "";
    const fullName = btn.dataset.fullname || "";
    const identity = btn.dataset.identity || "";
    const phone = btn.dataset.phone || "";
    const email = btn.dataset.email || "";
    const dob = btn.dataset.dob || "";
    const gender = btn.dataset.gender || "";
    const address = btn.dataset.address || "";

    if (fTenantId) fTenantId.value = id;
    if (fFullName) fFullName.value = fullName;
    if (fIdentity) fIdentity.value = identity;
    if (fPhone) fPhone.value = phone;
    if (fEmail) fEmail.value = email;
    if (fDob) fDob.value = dob;
    if (fAddress) fAddress.value = address === "null" ? "" : address;

    if (fGender) {
      if (gender === "0") fGender.value = "0";
      else if (gender === "1") fGender.value = "1";
      else fGender.value = "";
    }

    openOverlay(editModal);
  }

  /* ---------------- Confirm Save ---------------- */
  function openConfirm() {
    openOverlay(confirmDialog);
  }

  function closeConfirm() {
    closeOverlay(confirmDialog);
  }

  function submitEditForm() {
    closeConfirm();
    if (editForm) editForm.submit();
  }

  if (openConfirmBtn) {
    openConfirmBtn.addEventListener("click", openConfirm);
  }

  if (confirmSaveBtn) {
    confirmSaveBtn.addEventListener("click", submitEditForm);
  }

  /* ---------------- Toggle Status Confirm ---------------- */
  function openToggleConfirm(btn) {
    toggleTenantId = btn.dataset.tenantId || null;
    const currentStatus = btn.dataset.currentStatus || "";
    const name = btn.dataset.tenantName || "this tenant";

    toggleNextStatus = currentStatus === "ACTIVE" ? "LOCKED" : "ACTIVE";

    const isLock = toggleNextStatus === "LOCKED";
    if (toggleSubtitle) {
      toggleSubtitle.textContent = `Do you want to change "${name}" to ${toggleNextStatus}?`;
    }

    if (toggleOkBtn) {
      toggleOkBtn.style.background = isLock ? "#ef4444" : "#22c55e";
    }

    if (toggleIcon) {
      toggleIcon.innerHTML = isLock
        ? '<i class="bi bi-lock-fill"></i>'
        : '<i class="bi bi-unlock-fill"></i>';
    }

    openOverlay(toggleDialog);
  }

  function closeToggleConfirm() {
    closeOverlay(toggleDialog);
  }

  function confirmToggleStatus() {
    if (!toggleTenantId) return;

    const params = new URLSearchParams(window.location.search);
    const keyword = params.get("keyword") || "";
    const page = params.get("page") || "1";

    // giữ đúng pattern URL cũ của bạn
    let url = `${window.location.origin}${window.location.pathname}`; // /manager/tenants
    // nhưng vì app có contextPath, dùng relative:
    url = "manager/tenants";

    // để an toàn: build bằng current path + ctx
    const ctxGuess = document.querySelector(
      'link[href*="/assets/css/views/manager/tenants.css"]',
    );
    // fallback theo pathname hiện tại: /<ctx>/manager/tenants
    const base = window.location.pathname.replace(
      /\/manager\/tenants.*$/,
      "/manager/tenants",
    );

    let finalUrl = `${base}?action=toggleStatus&id=${encodeURIComponent(toggleTenantId)}`;
    if (keyword) finalUrl += `&keyword=${encodeURIComponent(keyword)}`;
    finalUrl += `&page=${encodeURIComponent(page)}`;

    window.location.href = finalUrl;
  }

  if (toggleOkBtn) {
    toggleOkBtn.addEventListener("click", confirmToggleStatus);
  }

  /* ---------------- Global Click Handling ---------------- */
  document.addEventListener("click", (e) => {
    const editBtn = e.target.closest(".js-open-edit");
    if (editBtn) {
      e.preventDefault();
      openEditModalFromButton(editBtn);
      return;
    }

    const statusBtn = e.target.closest(".mt-btn-status");
    if (statusBtn) {
      e.preventDefault();
      openToggleConfirm(statusBtn);
      return;
    }

    // close edit modal (x/backdrop/cancel)
    if (
      e.target.closest("#editModal [data-close='1']") ||
      e.target === editModal
    ) {
      closeOverlay(editModal);
      return;
    }

    // clear input buttons
    const clearBtn = e.target.closest("[data-clear]");
    if (clearBtn) {
      const id = clearBtn.getAttribute("data-clear");
      const el = document.getElementById(id);
      if (el) el.value = "";
      return;
    }

    const clearSelectBtn = e.target.closest("[data-clear-select]");
    if (clearSelectBtn) {
      const id = clearSelectBtn.getAttribute("data-clear-select");
      const el = document.getElementById(id);
      if (el) el.value = "";
      return;
    }

    // close confirm save
    if (
      e.target.closest("[data-close-confirm='1']") ||
      e.target === confirmDialog
    ) {
      closeConfirm();
      return;
    }

    // close toggle confirm
    if (
      e.target.closest("[data-close-toggle='1']") ||
      e.target === toggleDialog
    ) {
      closeToggleConfirm();
      return;
    }
  });

  // ESC closes top-most dialog
  document.addEventListener("keydown", (e) => {
    if (e.key !== "Escape") return;

    if (toggleDialog && toggleDialog.classList.contains("active")) {
      closeToggleConfirm();
      return;
    }
    if (confirmDialog && confirmDialog.classList.contains("active")) {
      closeConfirm();
      return;
    }
    if (editModal && editModal.classList.contains("active")) {
      closeOverlay(editModal);
    }
  });
})();
