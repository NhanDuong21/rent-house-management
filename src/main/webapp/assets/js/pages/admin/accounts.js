(function () {
  const modal = document.getElementById("maPassModal");
  if (!modal) return;

  const sub = document.getElementById("maPassSub");

  const hiddenId = document.getElementById("maPassAccountId");
  const hiddenType = document.getElementById("maPassAccountType");

  const newPass = document.getElementById("maNewPass");
  const confirmPass = document.getElementById("maConfirmPass");
  const showPass = document.getElementById("maShowPass");
  const errorBox = document.getElementById("maPassError");
  const closeBtn = document.getElementById("maPassClose");

  const form = document.getElementById("maPassForm");

  let lastFocused = null;

  function setInputsType(type) {
    if (newPass) newPass.type = type;
    if (confirmPass) confirmPass.type = type;
  }

  function openModal(btn) {
    lastFocused = document.activeElement;

    const accountId = (btn.dataset.accountId || "").trim();
    const accountType = (btn.dataset.accountType || "").trim();
    const fullName = btn.dataset.fullname || "User";
    const email = btn.dataset.email || "";

    if (sub) sub.textContent = email ? `${fullName} • ${email}` : fullName;
    if (hiddenId) hiddenId.value = accountId;
    if (hiddenType) hiddenType.value = accountType;

    if (newPass) newPass.value = "";
    if (confirmPass) confirmPass.value = "";
    if (errorBox) errorBox.textContent = "";

    if (showPass) showPass.checked = false;
    setInputsType("password");

    modal.classList.add("is-open");
    modal.setAttribute("aria-hidden", "false");
    document.body.style.overflow = "hidden";

    setTimeout(() => newPass && newPass.focus(), 0);
  }

  function closeModal() {
    modal.classList.remove("is-open");
    modal.setAttribute("aria-hidden", "true");
    document.body.style.overflow = "";

    if (lastFocused && typeof lastFocused.focus === "function") {
      lastFocused.focus();
    }
  }

  function mapErr(code) {
    const m = {
      NO_PERMISSION: "Bạn không có quyền thao tác.",
      PASSWORD_REQUIRED: "Vui lòng nhập mật khẩu.",
      CONFIRM_MISMATCH: "Xác nhận mật khẩu không khớp.",
      PASSWORD_MINLEN: "Mật khẩu tối thiểu 6 ký tự.",
      TENANT_NOT_FOUND: "Không tìm thấy tenant.",
      MANAGER_NOT_FOUND: "Không tìm thấy manager.",
      INVALID_TYPE: "Loại tài khoản không hợp lệ.",
      RESET_FAILED: "Reset thất bại, vui lòng thử lại.",
    };
    return m[code] || code || "Có lỗi xảy ra.";
  }

  // Open buttons + close via backdrop/cancel
  document.addEventListener("click", (e) => {
    const btn = e.target.closest(".ma-open-pass");
    if (btn) {
      e.preventDefault();
      openModal(btn);
      return;
    }

    const closeTarget = e.target.closest("[data-close='1']");
    if (closeTarget && modal.classList.contains("is-open")) {
      e.preventDefault();
      closeModal();
    }
  });

  // Close via X
  if (closeBtn) {
    closeBtn.addEventListener("click", (e) => {
      e.preventDefault();
      closeModal();
    });
  }

  // ESC close
  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape" && modal.classList.contains("is-open")) {
      closeModal();
    }
  });

  // Show/Hide passwords
  if (showPass) {
    showPass.addEventListener("change", () => {
      setInputsType(showPass.checked ? "text" : "password");
    });
  }

  // AJAX submit
  if (form) {
    form.addEventListener("submit", async (e) => {
      e.preventDefault();

      const p1 = (newPass?.value || "").trim();
      const p2 = (confirmPass?.value || "").trim();

      // client validate
      if (p1.length < 6) {
        if (errorBox)
          errorBox.textContent = "Password must be at least 6 characters.";
        newPass && newPass.focus();
        return;
      }
      if (p1 !== p2) {
        if (errorBox) errorBox.textContent = "Confirm password does not match.";
        confirmPass && confirmPass.focus();
        return;
      }

      if (errorBox) errorBox.textContent = "";

      const params = new URLSearchParams(new FormData(form));

      try {
        const res = await fetch(form.action, {
          method: "POST",
          body: params,
          headers: {
            "X-Requested-With": "XMLHttpRequest",
            Accept: "application/json",
            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
          },
        });

        const data = await res.json().catch(() => null);

        if (!res.ok || !data || !data.ok) {
          const msg = data && data.message ? data.message : "RESET_FAILED";
          if (errorBox) errorBox.textContent = mapErr(msg);
          return;
        }

        // success
        if (errorBox) errorBox.textContent = "";
        if (sub) sub.textContent = " Reset password thành công";

        // Optional: disable submit briefly
        const submitBtn = form.querySelector("button[type='submit']");
        if (submitBtn) submitBtn.disabled = true;

        setTimeout(() => {
          if (submitBtn) submitBtn.disabled = false;
          closeModal();
          // reload để list/alert đồng bộ
          window.location.reload();
        }, 600);
      } catch (err) {
        if (errorBox) errorBox.textContent = "Network error. Please try again.";
      }
    });
  }
})();
