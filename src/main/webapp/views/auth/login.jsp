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
                            <div class="login-title">Quên mật khẩu</div>
                            <div class="login-sub">Nhập email để nhận OTP xác thực</div>
                        </div>

                        <!-- Lỗi gửi OTP -->
                        <div id="fpError" class="login-error" style="display:none;">
                            <i class="bi bi-x-circle-fill"></i>
                            <span id="fpErrorMsg"></span>
                        </div>

                        <!-- FORM nhập email -->
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

                        <!-- Trạng thái gửi thành công -->
                        <div class="fp-success" id="fpSuccess">
                            <div class="fp-success-ico"><i class="bi bi-envelope-check-fill"></i></div>
                            <div class="fp-success-title">OTP đã được gửi!</div>
                            <div class="fp-success-sub" id="fpSuccessSub">
                                Kiểm tra hộp thư của bạn, OTP có hiệu lực trong 10 phút.
                            </div>
                            <button class="login-btn" type="button" id="btnGoToOtp" style="margin-top:8px;">
                                <i class="bi bi-key"></i> Nhập OTP ngay
                            </button>
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
            const forgotPanel   = document.getElementById('forgotPanel');
            const loginContent  = document.getElementById('loginContent');
            const fpError       = document.getElementById('fpError');
            const fpErrorMsg    = document.getElementById('fpErrorMsg');
            const fpSuccess     = document.getElementById('fpSuccess');
            const fpSuccessSub  = document.getElementById('fpSuccessSub');
            const fpEmailInput  = document.getElementById('fpEmailInput');
            const btnSendOtp    = document.getElementById('btnSendOtp');

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
                fpError.style.display   = 'none';
                fpSuccess.classList.remove('is-show');
                document.getElementById('formForgot').style.display = '';
                forgotPanel.classList.add('is-open');
                fpEmailInput.focus();
            }

            function closeForgotPanel() {
                forgotPanel.classList.remove('is-open');
                fpEmailInput.value = '';
            }

            // Submit gửi OTP qua AJAX
            document.getElementById('formForgot').addEventListener('submit', async e => {
                e.preventDefault();
                const email = fpEmailInput.value.trim();
                if (!email) return;

                // Loading state
                btnSendOtp.disabled = true;
                btnSendOtp.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span> Đang gửi…';
                fpError.style.display = 'none';

                try {
                    const res = await fetch('<%=request.getContextPath()%>/forgot-password', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                        body: 'step=email&email=' + encodeURIComponent(email)
                    });

                    const data = await res.json();

                    if (data.success) {
                        // Ẩn form, hiện success
                        document.getElementById('formForgot').style.display = 'none';
                        fpSuccessSub.textContent =
                            'OTP đã gửi đến ' + maskEmail(email) + '. Có hiệu lực trong 10 phút.';
                        fpSuccess.classList.add('is-show');
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

            // Nút "Nhập OTP ngay" → đóng panel, chuyển sang tab OTP, điền sẵn email
            document.getElementById('btnGoToOtp').addEventListener('click', () => {
                const email = fpEmailInput.value.trim();

                // Điền email vào ô email của tab OTP
                document.getElementById('otpEmailInput').value = email;

                // Focus vào ô OTP
                document.getElementById('otpCodeInput').focus();

                // Đóng panel
                closeForgotPanel();

                // Kích hoạt tab OTP (dùng lại logic của login.js)
                document.getElementById('tabOtp').click();
            });

            function showFpError(msg) {
                fpErrorMsg.textContent = msg;
                fpError.style.display = 'flex';
            }

            function maskEmail(email) {
                const at = email.indexOf('@');
                if (at <= 1) return email;
                return email[0] + '**' + email.slice(at);
            }
        </script>
    </body>
</html>
