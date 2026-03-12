// ===== Manage Accounts JS (realtime search + toggle status + reset password modal) =====
(function () {
    const ctxPath = window.MA_CTX || "";

    /* ================= REALTIME SEARCH ================= */
    (function () {
        const form = document.getElementById("maSearchForm");
        const keyword = document.getElementById("maKeyword");
        const role = document.getElementById("maRole");
        let timer = null;

        function debounceSubmit() {
            if (timer)
                clearTimeout(timer);
            timer = setTimeout(() => {
                if (form)
                    form.submit();
            }, 350);
        }

        if (keyword)
            keyword.addEventListener("input", debounceSubmit);
        if (role)
            role.addEventListener("change", () => form && form.submit());
    })();

    /* ================= TOGGLE STATUS (confirm modal + fetch) ================= */
    (function () {
        var _id = null,
                _type = null,
                _status = null;

        var modal = document.getElementById("maToggleModal");
        if (!modal)
            return;

        var msgEl = document.getElementById("maToggleMsg");
        var subEl = document.getElementById("maToggleSub");
        var confirmBtn = document.getElementById("maToggleConfirm");
        var cancelBtn = document.getElementById("maToggleCancel");
        var closeBtn = document.getElementById("maToggleClose");
        var backdrop = document.getElementById("maToggleBackdrop");

        function openModal(btn) {
            _id = btn.dataset.accountId;
            _type = btn.dataset.accountType;
            _status = btn.dataset.currentStatus;

            var name = btn.dataset.fullname || "this account";
            var nextStatus = _status.toUpperCase() === "ACTIVE" ? "LOCKED" : "ACTIVE";

            if (subEl)
                subEl.textContent = name + " (" + _type + ")";
            if (msgEl)
                msgEl.innerHTML =
                        "Do you really want to change status from <strong>" +
                        _status +
                        "</strong> to <strong>" +
                        nextStatus +
                        "</strong>?";

            modal.style.display = "flex";
            modal.setAttribute("aria-hidden", "false");
        }

        function closeModal() {
            modal.style.display = "none";
            modal.setAttribute("aria-hidden", "true");
        }

        function showToast(msg, type) {
            var t = document.getElementById("maToast");
            if (!t)
                return;
            t.className = "ma-toast toast-" + type;
            t.innerHTML =
                    (type === "success"
                            ? '<i class="bi bi-check-circle-fill"></i>'
                            : '<i class="bi bi-x-circle-fill"></i>') +
                    " " +
                    msg;
            t.style.display = "flex";
            clearTimeout(t._tid);
            t._tid = setTimeout(function () {
                t.style.display = "none";
            }, 4000);
        }

        function applyUI(type, id, newStatus) {
            var key = type + "-" + id;
            var badge = document.getElementById("statusBadge-" + key);
            var btn = document.getElementById("toggleBtn-" + key);

            if (badge) {
                badge.className = "ma-badge status-" + newStatus.toLowerCase();
                badge.textContent = newStatus;
            }

            if (btn) {
                var isActive = newStatus.toUpperCase() === "ACTIVE";
                btn.classList.toggle("on", isActive);
                btn.classList.toggle("off", !isActive);
                btn.dataset.currentStatus = newStatus;

                var lbl = btn.querySelector(".ma-switch-label");
                if (lbl) {
                    lbl.innerHTML = isActive
                            ? '<i class="bi bi-unlock-fill"></i> Active'
                            : '<i class="bi bi-lock-fill"></i> Locked';
                }
            }
        }

        // confirm click => fetch
        if (confirmBtn) {
            confirmBtn.addEventListener("click", function () {
                closeModal();

                var params = new URLSearchParams();
                params.append("action", "toggle-status");
                params.append("accountId", _id);
                params.append("accountType", _type);
                params.append("currentStatus", _status);

                fetch(ctxPath + "/admin/accounts", {
                    method: "POST",
                    headers: {"Content-Type": "application/x-www-form-urlencoded"},
                    body: params.toString(),
                })
                        .then(function (res) {
                            return res.json();
                        })
                        .then(function (data) {
                            if (data && data.ok) {
                                var newStatus =
                                        _status.toUpperCase() === "ACTIVE" ? "LOCKED" : "ACTIVE";
                                applyUI(_type, _id, newStatus);
                                showToast(
                                        data.message || "Status updated successfully.",
                                        "success",
                                        );
                            } else {
                                showToast(
                                        (data && data.message) || "Failed to update status.",
                                        "error",
                                        );
                            }
                        })
                        .catch(function () {
                            showToast("Network error. Please try again.", "error");
                        });
            });
        }

        // bind open buttons
        document.querySelectorAll(".ma-open-toggle").forEach(function (btn) {
            btn.addEventListener("click", function () {
                openModal(btn);
            });
        });

        // close handlers
        if (closeBtn)
            closeBtn.addEventListener("click", closeModal);
        if (cancelBtn)
            cancelBtn.addEventListener("click", closeModal);
        if (backdrop)
            backdrop.addEventListener("click", closeModal);
        document.addEventListener("keydown", function (e) {
            if (e.key === "Escape")
                closeModal();
        });
    })();

    /* ================= RESET PASSWORD MODAL (AJAX) ================= */
    (function () {
        const modal = document.getElementById("maPassModal");
        if (!modal)
            return;

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
            if (newPass)
                newPass.type = type;
            if (confirmPass)
                confirmPass.type = type;
        }

        function openModal(btn) {
            lastFocused = document.activeElement;

            const accountId = (btn.dataset.accountId || "").trim();
            const accountType = (btn.dataset.accountType || "").trim();
            const fullName = btn.dataset.fullname || "User";
            const email = btn.dataset.email || "";

            if (sub)
                sub.textContent = email ? `${fullName} • ${email}` : fullName;
            if (hiddenId)
                hiddenId.value = accountId;
            if (hiddenType)
                hiddenType.value = accountType;

            if (newPass)
                newPass.value = "";
            if (confirmPass)
                confirmPass.value = "";
            if (errorBox)
                errorBox.textContent = "";

            if (showPass)
                showPass.checked = false;
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
                    if (errorBox)
                        errorBox.textContent = "Confirm password does not match.";
                    confirmPass && confirmPass.focus();
                    return;
                }

                if (errorBox)
                    errorBox.textContent = "";

                const params = new URLSearchParams(new FormData(form));

                try {
                    const res = await fetch(form.action, {
                        method: "POST",
                        body: params,
                        headers: {
                            "X-Requested-With": "XMLHttpRequest",
                            Accept: "application/json",
                            "Content-Type":
                                    "application/x-www-form-urlencoded; charset=UTF-8",
                        },
                    });

                    const data = await res.json().catch(() => null);

                    if (!res.ok || !data || !data.ok) {
                        const msg = data && data.message ? data.message : "RESET_FAILED";
                        if (errorBox)
                            errorBox.textContent = mapErr(msg);
                        return;
                    }

                    // success
                    if (errorBox)
                        errorBox.textContent = "";
                    if (sub)
                        sub.textContent = " Reset password thành công";

                    const submitBtn = form.querySelector("button[type='submit']");
                    if (submitBtn)
                        submitBtn.disabled = true;

                    setTimeout(() => {
                        if (submitBtn)
                            submitBtn.disabled = false;
                        closeModal();
                        window.location.reload();
                    }, 600);
                } catch (err) {
                    if (errorBox)
                        errorBox.textContent = "Network error. Please try again.";
                }
            });
        }
    })();
    /* ===== AUTO HIDE SUCCESS ALERT ===== */
    (function () {

        const alert = document.getElementById("successAlert");
        if (!alert)
            return;

        setTimeout(() => {

            alert.style.transition = "opacity 0.5s";
            alert.style.opacity = "0";

            setTimeout(() => {
                alert.remove();
            }, 500);

        }, 3000);

    })();

})();
