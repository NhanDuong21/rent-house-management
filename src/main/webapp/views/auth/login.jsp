<%--
    Document   : login
    Author     : Duong Thien Nhan - CE190741
--%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1"/>
        <title>Login - RentHouse</title>

        <link rel="icon" type="image/png"
              href="${pageContext.request.contextPath}/assets/images/logo/favicon_logo.png">

        <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/base/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/views/login.css">

        <style>
            /* ── Forgot-password overlay panel ── */
            #forgotPanel {
                display: none;
                position: absolute;
                inset: 0;
                background: var(--card-bg, #fff);
                border-radius: inherit;
                padding: inherit;
                z-index: 10;
                flex-direction: column;
                animation: fadeSlideIn .22s ease;
            }
            #forgotPanel.is-open { display: flex; }

            @keyframes fadeSlideIn {
                from { opacity: 0; transform: translateY(10px); }
                to   { opacity: 1; transform: translateY(0); }
            }

            .forgot-back-btn {
                display: inline-flex;
                align-items: center;
                gap: 6px;
                background: none;
                border: none;
                padding: 0;
                color: #64748b;
                font-size: .85rem;
                cursor: pointer;
                margin-bottom: 18px;
            }
            .forgot-back-btn:hover { color: var(--primary, #6366f1); }

            /* success state inside forgot panel */
            .fp-success {
                display: none;
                flex-direction: column;
                align-items: center;
                gap: 12px;
                padding: 24px 0;
                text-align: center;
            }
            .fp-success.is-show { display: flex; }
            .fp-success-ico {
                width: 56px; height: 56px;
                border-radius: 50%;
                background: #dcfce7;
                display: flex; align-items: center; justify-content: center;
                font-size: 1.6rem; color: #16a34a;
            }
            .fp-success-title { font-weight: 700; font-size: 1rem; color: #1e293b; }
            .fp-success-sub   { font-size: .85rem; color: #64748b; }

            /* ── step panels bên trong forgotPanel ── */
            .fp-step { display: none; }
            .fp-step.is-active { display: block; }
        </style>
    </head>

    <body>
        <div class="login-shell">

            <!-- LEFT: BRAND -->
            <section class="login-brand">
                <div class="brand-top">
                    <div class="brand-mark">
                        <img src="<%=request.getContextPath()%>/assets/images/logo/logo.png" alt="RentHouse"/>
                    </div>

                    <div class="brand-name">RentHouse</div>
                    <div class="brand-tagline">
                        Quản lý nhà trọ • Hợp đồng • Hóa đơn • Bảo trì
                    </div>

                    <div class="brand-pills">
                        <span class="pill"><i class="bi bi-building"></i> Rooms</span>
                        <span class="pill"><i class="bi bi-file-earmark-text"></i> Contracts</span>
                        <span class="pill"><i class="bi bi-receipt"></i> Bills</span>
                        <span class="pill"><i class="bi bi-tools"></i> Maintenance</span>
                    </div>
                </div>

                <div class="brand-card">
                    <div class="brand-card-title">Tối ưu cho quản lý nhà trọ</div>
                    <div class="brand-card-sub">
                        Theo dõi phòng trống, tenant, hợp đồng và hóa đơn nhanh – gọn – rõ ràng.
                    </div>

                    <div class="brand-stats">
                        <div class="stat">
                            <div class="stat-ico"><i class="bi bi-house-door"></i></div>
                            <div class="stat-body">
                                <div class="stat-num">Rooms</div>
                                <div class="stat-text">Danh sách phòng & trạng thái</div>
                            </div>
                        </div>

                        <div class="stat">
                            <div class="stat-ico"><i class="bi bi-clipboard-check"></i></div>
                            <div class="stat-body">
                                <div class="stat-num">Contracts</div>
                                <div class="stat-text">Tạo & quản lý hợp đồng thuê</div>
                            </div>
                        </div>

                        <div class="stat">
                            <div class="stat-ico"><i class="bi bi-lightning-charge"></i></div>
                            <div class="stat-body">
                                <div class="stat-num">Utilities</div>
                                <div class="stat-text">Điện nước & thanh toán</div>
                            </div>
                        </div>
                    </div>

                    <div class="brand-note">
                        <i class="bi bi-shield-check"></i>
                        Bảo mật tài khoản và phân quyền theo vai trò.
                    </div>
                </div>

                <div class="brand-footer">
                    <span>2026 © SWP391 - Group 4</span>
                </div>
            </section>

            <!-- RIGHT: LOGIN PANEL -->
            <section class="login-panel">
                <div class="login-card" style="position:relative;">

                    <!-- ══════════════════════════════════════════
                         FORGOT PASSWORD PANEL (overlay, ẩn mặc định)
                    ══════════════════════════════════════════ -->
                    <div id="forgotPanel">

                        <button type="button" class="forgot-back-btn" id="btnBackToLogin">
                            <i class="bi bi-arrow-left"></i> Quay lại đăng nhập
                        </button>

                        <div class="login-head" style="margin-bottom:20px;">
                            <div class="login-title" id="fpPanelTitle">Quên mật khẩu</div>
                            <div class="login-sub" id="fpPanelSub">Nhập email để nhận OTP xác thực</div>
                        </div>

                        <!-- Thông báo lỗi dùng chung cho cả 3 bước -->
                        <div id="fpError" class="login-error" style="display:none;">
                            <i class="bi bi-x-circle-fill"></i>
                            <span id="fpErrorMsg"></span>
                        </div>

                        <!-- ── BƯỚC 1: Nhập email ── -->
                        <div class="fp-step is-active" id="fpStep1">
                            <form id="formForgot" class="login-form" style="margin-bottom:0;">
                                <div class="field">
                                    <label class="field-label">Email đã đăng ký</label>
                                    <div class="field-control">
                                        <span class="field-icon"><i class="bi bi-envelope"></i></span>
                                        <input class="field-input"
                                               type="email"
                                               id="fpEmailInput"
                                               placeholder="your.email@example.com"
                                               autocomplete="email"
                                               required>
                                    </div>
                                </div>
                                <button class="login-btn" type="submit" id="btnSendOtp">
                                    <i class="bi bi-send"></i> Gửi OTP
                                </button>
                            </form>
                        </div>

                        <!-- ── BƯỚC 2: Nhập OTP ── -->
                        <div class="fp-step" id="fpStep2">
                            <form id="formVerifyOtp" class="login-form" style="margin-bottom:0;">
                                <div class="field">
                                    <label class="field-label">Mã OTP</label>
                                    <div class="field-control">
                                        <span class="field-icon"><i class="bi bi-key-fill"></i></span>
                                        <input class="field-input"
                                               type="text"
                                               id="fpOtpInput"
                                               inputmode="numeric"
                                               maxlength="6"
                                               placeholder="Nhập OTP 6 số"
                                               autocomplete="one-time-code"
                                               required>
                                    </div>
                                </div>
                                <button class="login-btn" type="submit" id="btnVerifyOtp">
                                    <i class="bi bi-check-circle"></i> Xác nhận OTP
                                </button>
                                <div class="login-hint" style="margin-top:10px;">
                                    Không nhận được OTP?
                                    <a href="#" id="btnResendOtp" style="color:var(--primary,#6366f1);">Gửi lại</a>
                                </div>
                            </form>
                        </div>

                        <!-- ── BƯỚC 3: Đặt mật khẩu mới ── -->
                        <div class="fp-step" id="fpStep3">
                            <form id="formResetPassword" class="login-form" style="margin-bottom:0;">
                                <div class="field">
                                    <label class="field-label">Mật khẩu mới</label>
                                    <div class="field-control">
                                        <span class="field-icon"><i class="bi bi-shield-lock"></i></span>
                                        <input class="field-input"
                                               type="password"
                                               id="fpNewPassword"
                                               placeholder="Tối thiểu 6 ký tự"
                                               autocomplete="new-password"
                                               required>
                                        <button class="field-suffix-btn" type="button" id="toggleFpNew" aria-label="Show/Hide">
                                            <i class="bi bi-eye"></i>
                                        </button>
                                    </div>
                                </div>
                                <div class="field">
                                    <label class="field-label">Xác nhận mật khẩu</label>
                                    <div class="field-control">
                                        <span class="field-icon"><i class="bi bi-shield-lock"></i></span>
                                        <input class="field-input"
                                               type="password"
                                               id="fpConfirmPassword"
                                               placeholder="Nhập lại mật khẩu mới"
                                               autocomplete="new-password"
                                               required>
                                        <button class="field-suffix-btn" type="button" id="toggleFpConfirm" aria-label="Show/Hide">
                                            <i class="bi bi-eye"></i>
                                        </button>
                                    </div>
                                </div>
                                <button class="login-btn" type="submit" id="btnResetPassword">
                                    <i class="bi bi-check2-circle"></i> Đặt mật khẩu mới
                                </button>
                            </form>
                        </div>

                    </div>
                    <!-- /forgotPanel -->


                    <!-- ══════════════════════════════════════════
                         NỘI DUNG LOGIN BÌNH THƯỜNG
                    ══════════════════════════════════════════ -->
                    <div id="loginContent">

                        <div class="login-head">
                            <div class="login-title">Đăng nhập</div>
                            <div class="login-sub">Vào hệ thống quản lý RentHouse</div>
                        </div>

                        <% String error = (String) request.getAttribute("error"); %>
                        <% if (error != null) {%>
                        <div class="login-error">
                            <i class="bi bi-x-circle-fill"></i>
                            <span><%= error%></span>
                        </div>
                        <% }%>

                        <!-- TABS -->
                        <div class="login-tabs" role="tablist" aria-label="Login method">
                            <button type="button"
                                    class="tab-btn is-active"
                                    id="tabPassword"
                                    data-target="formPassword"
                                    aria-selected="true">
                                <i class="bi bi-lock"></i> Password
                            </button>

                            <button type="button"
                                    class="tab-btn"
                                    id="tabOtp"
                                    data-target="formOtp"
                                    aria-selected="false">
                                <i class="bi bi-key"></i> OTP
                            </button>
                        </div>

                        <!-- PASSWORD LOGIN -->
                        <form action="<%=request.getContextPath()%>/login"
                              method="post"
                              class="login-form"
                              id="formPassword">

                            <input type="hidden" name="mode" value="PASSWORD"/>

                            <div class="field">
                                <label class="field-label">Email</label>
                                <div class="field-control">
                                    <span class="field-icon"><i class="bi bi-envelope"></i></span>
                                    <input class="field-input"
                                           type="text"
                                           name="email"
                                           placeholder="your.email@example.com"
                                           autocomplete="username"
                                           required>
                                </div>
                            </div>

                            <div class="field">
                                <label class="field-label">Password</label>
                                <div class="field-control">
                                    <span class="field-icon"><i class="bi bi-shield-lock"></i></span>
                                    <input class="field-input"
                                           type="password"
                                           name="password"
                                           id="passwordInput"
                                           placeholder="Enter your password"
                                           autocomplete="current-password"
                                           required>

                                    <button class="field-suffix-btn"
                                            type="button"
                                            id="togglePassword"
                                            aria-label="Show/Hide password">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                            </div>

                            <div class="login-row">
                                <label class="check">
                                    <input type="checkbox" name="remember" value="on">
                                    <span>Remember me</span>
                                </label>

                                <%-- ★ Đổi href thành onclick mở forgotPanel --%>
                                <a class="login-link" href="#" id="linkForgotPassword">
                                    Forgot password?
                                </a>
                            </div>

                            <button class="login-btn" type="submit">
                                <i class="bi bi-box-arrow-in-right"></i> Login
                            </button>

                            <div class="login-hint">
                                Lần đầu đăng nhập? Chọn tab <strong>OTP</strong>.
                            </div>
                        </form>

                        <!-- OTP LOGIN -->
                        <form action="<%=request.getContextPath()%>/login"
                              method="post"
                              class="login-form"
                              id="formOtp"
                              style="display:none;">

                            <input type="hidden" name="mode" value="OTP"/>

                            <div class="field">
                                <label class="field-label">Email</label>
                                <div class="field-control">
                                    <span class="field-icon"><i class="bi bi-envelope"></i></span>
                                    <input class="field-input"
                                           type="text"
                                           name="email"
                                           id="otpEmailInput"
                                           placeholder="your.email@example.com"
                                           autocomplete="username"
                                           required>
                                </div>
                            </div>

                            <div class="field">
                                <label class="field-label">OTP</label>
                                <div class="field-control">
                                    <span class="field-icon"><i class="bi bi-key-fill"></i></span>
                                    <input class="field-input"
                                           type="text"
                                           name="otp"
                                           id="otpCodeInput"
                                           inputmode="numeric"
                                           maxlength="6"
                                           placeholder="Nhập OTP 6 số"
                                           required>
                                </div>
                            </div>

                            <button class="login-btn" type="submit">
                                <i class="bi bi-box-arrow-in-right"></i> Login bằng OTP
                            </button>

                            <div class="login-hint">
                                OTP chỉ dùng cho lần đầu / khi hệ thống yêu cầu xác thực.
                            </div>
                        </form>

                    </div>
                    <!-- /loginContent -->

                </div>
            </section>
        </div>

        <script src="<%=request.getContextPath()%>/assets/js/vendor/bootstrap.bundle.min.js"></script>
        <script src="<%=request.getContextPath()%>/assets/js/pages/login.js"></script>
        <script>
            // ── Forgot Password inline panel ─────────────────────────────────────
            const forgotPanel  = document.getElementById('forgotPanel');
            const fpError      = document.getElementById('fpError');
            const fpErrorMsg   = document.getElementById('fpErrorMsg');
            const fpEmailInput = document.getElementById('fpEmailInput');
            const fpPanelTitle = document.getElementById('fpPanelTitle');
            const fpPanelSub   = document.getElementById('fpPanelSub');
            const btnSendOtp   = document.getElementById('btnSendOtp');

            // Mở panel forgot
            document.getElementById('linkForgotPassword').addEventListener('click', e => {
                e.preventDefault();
                openForgotPanel();
            });

            // Đóng panel forgot, quay lại login
            document.getElementById('btnBackToLogin').addEventListener('click', () => {
                closeForgotPanel();
            });

            function openForgotPanel() {
                hideFpError();
                showStep(1);
                forgotPanel.classList.add('is-open');
                fpEmailInput.focus();
            }

            function closeForgotPanel() {
                forgotPanel.classList.remove('is-open');
                // Reset về bước 1
                fpEmailInput.value = '';
                document.getElementById('fpOtpInput').value = '';
                document.getElementById('fpNewPassword').value = '';
                document.getElementById('fpConfirmPassword').value = '';
                hideFpError();
                showStep(1);
                fpPanelTitle.textContent = 'Quên mật khẩu';
                fpPanelSub.textContent   = 'Nhập email để nhận OTP xác thực';
            }

            function showStep(n) {
                document.querySelectorAll('.fp-step').forEach(el => el.classList.remove('is-active'));
                document.getElementById('fpStep' + n).classList.add('is-active');
            }

            function showFpError(msg) {
                fpErrorMsg.textContent = msg;
                fpError.style.display = 'flex';
            }

            function hideFpError() {
                fpError.style.display = 'none';
            }

            // ── Bước 1: Submit gửi OTP ───────────────────────────────────────────
            document.getElementById('formForgot').addEventListener('submit', async e => {
                e.preventDefault();
                const email = fpEmailInput.value.trim();
                if (!email) return;

                btnSendOtp.disabled = true;
                btnSendOtp.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span> Đang gửi…';
                hideFpError();

                try {
                    const res  = await fetch('<%=request.getContextPath()%>/forgot-password', {
                        method : 'POST',
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                        body   : 'step=email&email=' + encodeURIComponent(email)
                    });
                    const data = await res.json();

                    if (data.success) {
                        // Chuyển sang bước 2
                        fpPanelTitle.textContent = 'Nhập mã OTP';
                        fpPanelSub.textContent   = 'OTP đã gửi đến ' + maskEmail(email) + '. Có hiệu lực trong 10 phút.';
                        showStep(2);
                        document.getElementById('fpOtpInput').focus();
                    } else {
                        showFpError(data.message || 'Email không tồn tại trong hệ thống.');
                    }
                } catch (err) {
                    showFpError('Có lỗi xảy ra, vui lòng thử lại.');
                } finally {
                    btnSendOtp.disabled = false;
                    btnSendOtp.innerHTML = '<i class="bi bi-send"></i> Gửi OTP';
                }
            });

            // ── Bước 2: Xác nhận OTP ────────────────────────────────────────────
            document.getElementById('formVerifyOtp').addEventListener('submit', async e => {
                e.preventDefault();
                const otp = document.getElementById('fpOtpInput').value.trim();
                if (!otp) return;

                const btnVerify = document.getElementById('btnVerifyOtp');
                btnVerify.disabled = true;
                btnVerify.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span> Đang xác nhận…';
                hideFpError();

                try {
                    const res  = await fetch('<%=request.getContextPath()%>/forgot-password', {
                        method : 'POST',
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                        body   : 'step=verifyOtp&otp=' + encodeURIComponent(otp)
                    });
                    const data = await res.json();

                    if (data.success) {
                        // Chuyển sang bước 3
                        fpPanelTitle.textContent = 'Đặt mật khẩu mới';
                        fpPanelSub.textContent   = 'Nhập mật khẩu mới cho tài khoản của bạn';
                        showStep(3);
                        document.getElementById('fpNewPassword').focus();
                    } else {
                        showFpError(data.message || 'OTP không đúng hoặc đã hết hạn.');
                    }
                } catch (err) {
                    showFpError('Có lỗi xảy ra, vui lòng thử lại.');
                } finally {
                    btnVerify.disabled = false;
                    btnVerify.innerHTML = '<i class="bi bi-check-circle"></i> Xác nhận OTP';
                }
            });

            // ── Bước 3: Đặt mật khẩu mới ────────────────────────────────────────
            document.getElementById('formResetPassword').addEventListener('submit', async e => {
                e.preventDefault();
                const newPwd     = document.getElementById('fpNewPassword').value;
                const confirmPwd = document.getElementById('fpConfirmPassword').value;

                if (newPwd.length < 6) {
                    showFpError('Mật khẩu phải từ 6 ký tự trở lên.');
                    return;
                }
                if (newPwd !== confirmPwd) {
                    showFpError('Xác nhận mật khẩu không khớp.');
                    return;
                }

                const btnReset = document.getElementById('btnResetPassword');
                btnReset.disabled = true;
                btnReset.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span> Đang xử lý…';
                hideFpError();

                try {
                    const res  = await fetch('<%=request.getContextPath()%>/forgot-password', {
                        method : 'POST',
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                        body   : 'step=resetPassword'
                             + '&newPassword='     + encodeURIComponent(newPwd)
                             + '&confirmPassword=' + encodeURIComponent(confirmPwd)
                    });
                    const data = await res.json();

                    if (data.success && data.redirect) {
                        // Đổi pass thành công → tự động vào trang luôn
                        window.location.href = data.redirect;
                    } else {
                        showFpError(data.message || 'Có lỗi xảy ra, vui lòng thử lại.');
                        btnReset.disabled = false;
                        btnReset.innerHTML = '<i class="bi bi-check2-circle"></i> Đặt mật khẩu mới';
                    }
                } catch (err) {
                    showFpError('Có lỗi xảy ra, vui lòng thử lại.');
                    btnReset.disabled = false;
                    btnReset.innerHTML = '<i class="bi bi-check2-circle"></i> Đặt mật khẩu mới';
                }
            });

            // ── Gửi lại OTP ──────────────────────────────────────────────────────
            document.getElementById('btnResendOtp').addEventListener('click', async e => {
                e.preventDefault();
                const email = fpEmailInput.value.trim();
                if (!email) { showStep(1); return; }

                hideFpError();
                try {
                    const res  = await fetch('<%=request.getContextPath()%>/forgot-password', {
                        method : 'POST',
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                        body   : 'step=email&email=' + encodeURIComponent(email)
                    });
                    const data = await res.json();
                    if (data.success) {
                        document.getElementById('fpOtpInput').value = '';
                        fpPanelSub.textContent = 'OTP mới đã gửi đến ' + maskEmail(email) + '. Có hiệu lực trong 10 phút.';
                    } else {
                        showFpError(data.message || 'Không thể gửi lại OTP.');
                    }
                } catch (err) {
                    showFpError('Có lỗi xảy ra, vui lòng thử lại.');
                }
            });

            // ── Toggle show/hide password bước 3 ────────────────────────────────
            document.getElementById('toggleFpNew').addEventListener('click', () => {
                const inp = document.getElementById('fpNewPassword');
                const ico = document.querySelector('#toggleFpNew i');
                if (inp.type === 'password') { inp.type = 'text';     ico.className = 'bi bi-eye-slash'; }
                else                         { inp.type = 'password'; ico.className = 'bi bi-eye'; }
            });
            document.getElementById('toggleFpConfirm').addEventListener('click', () => {
                const inp = document.getElementById('fpConfirmPassword');
                const ico = document.querySelector('#toggleFpConfirm i');
                if (inp.type === 'password') { inp.type = 'text';     ico.className = 'bi bi-eye-slash'; }
                else                         { inp.type = 'password'; ico.className = 'bi bi-eye'; }
            });

            function maskEmail(email) {
                const at = email.indexOf('@');
                if (at <= 1) return email;
                return email[0] + '**' + email.slice(at);
            }
        </script>
    </body>
</html>
