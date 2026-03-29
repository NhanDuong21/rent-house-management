<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String errorMessage = (String) request.getAttribute("javax.servlet.error.message");
    Throwable exception = (Throwable) request.getAttribute("javax.servlet.error.exception");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>500 - Lỗi hệ thống</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="icon" type="image/png"
          href="${pageContext.request.contextPath}/assets/images/logo/favicon_logo.png">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/views/error500.css">
</head>
<body>
    <div class="bg-blobs">
        <span class="blob blob-1"></span>
        <span class="blob blob-2"></span>
        <span class="blob blob-3"></span>
        <span class="grid-glow"></span>
    </div>

    <div class="particles" id="particles"></div>

    <main class="error-stage">
        <section class="error-shell" id="errorShell">

            <div class="error-card">
                <div class="error-left">
                    <div class="badge-wrap">
                        <span class="badge pulse-badge">Lỗi hệ thống</span>
                        <span class="status-dot"></span>
                    </div>

                    <div class="code-wrap">
                        <h1 class="code glitch-text" data-text="500">500</h1>
                        <div class="code-shadow">500</div>
                    </div>

                    <h2 class="title reveal-up">Đã xảy ra lỗi nội bộ máy chủ</h2>

                    <p class="desc reveal-up delay-1">
                        Hệ thống đang gặp sự cố khi xử lý yêu cầu của bạn.
                        Vui lòng tải lại trang hoặc quay về trang chủ để tiếp tục sử dụng.
                    </p>

                    <div class="tips reveal-up delay-2">
                        <div class="tip-item">
                            <span class="tip-icon">⚡</span>
                            <span>Tải lại để thử gửi lại yêu cầu</span>
                        </div>
                        <div class="tip-item">
                            <span class="tip-icon">🛡</span>
                            <span>Logic backend không bị thay đổi</span>
                        </div>
                        <div class="tip-item">
                            <span class="tip-icon">📩</span>
                            <span>Liên hệ quản trị viên nếu lỗi lặp lại</span>
                        </div>
                    </div>

                    <div class="actions reveal-up delay-3">
                        <a class="btn btn-primary magnetic"
                           href="${pageContext.request.contextPath}/home">
                            <span>Về trang chủ</span>
                        </a>

                        <a class="btn btn-secondary magnetic"
                           href="javascript:location.reload()">
                            <span>Tải lại</span>
                        </a>
                    </div>
                </div>

                <div class="error-right">
                    <div class="right-content">
                        <div class="logo-ring">
                            <div class="ring ring-1"></div>
                            <div class="ring ring-2"></div>
                            <img src="${pageContext.request.contextPath}/assets/images/logo/logo.png" alt="logo">
                        </div>

                        <h3 class="brand-title">RentHouse System</h3>
                        <p class="brand-subtitle">Hệ thống đang xử lý lỗi nội bộ</p>

                        <div class="monitor-card">
                            <div class="monitor-top">
                                <span></span><span></span><span></span>
                            </div>

                            <div class="monitor-screen">
                                <div class="scan-line"></div>
                                <div class="wave wave-1"></div>
                                <div class="wave wave-2"></div>
                                <div class="system-text" id="systemText">
                                    Initializing recovery protocol...
                                </div>
                            </div>
                        </div>

                        <% if (errorMessage != null) { %>
                            <div class="error-detail detail-enter">
                                <strong>Message:</strong>
                                <div class="detail-content"><%= errorMessage %></div>
                            </div>
                        <% } %>

                        <% if (exception != null) { %>
                            <div class="error-detail detail-enter">
                                <strong>Exception:</strong>
                                <div class="detail-content"><%= exception.getClass().getName() %></div>
                            </div>
                        <% } %>
                    </div>
                </div>
            </div>

        </section>
    </main>

    <script>
        window.APP_CONTEXT = "${pageContext.request.contextPath}";
    </script>
    <script src="${pageContext.request.contextPath}/assets/js/pages/error500.js"></script>
</body>
</html>