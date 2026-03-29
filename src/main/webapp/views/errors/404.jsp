<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>404 - Page Not Found</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="icon" type="image/png"
          href="${pageContext.request.contextPath}/assets/images/logo/favicon_logo.png">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/views/error404.css">
</head>
<body>
    <!-- background fx -->
    <div class="fx-bg">
        <div class="grid-overlay"></div>
        <div class="noise-overlay"></div>

        <span class="orb orb-1"></span>
        <span class="orb orb-2"></span>
        <span class="orb orb-3"></span>

        <span class="floating-shape shape-1"></span>
        <span class="floating-shape shape-2"></span>
        <span class="floating-shape shape-3"></span>
        <span class="floating-shape shape-4"></span>

        <div class="particles" id="particles"></div>
    </div>

    <main class="error-shell">
        <section class="error-container" id="errorCard">
            <div class="left">
                <div class="badge reveal reveal-delay-1">
                    <span class="badge-dot"></span>
                    Access Error
                </div>

                <div class="code-wrap reveal reveal-delay-2">
                    <div class="glow-ring"></div>
                    <h1 class="code" data-text="404">404</h1>
                </div>

                <div class="title reveal reveal-delay-3">
                    Page Not Found
                </div>

                <div class="desc reveal reveal-delay-4">
                    The page you are trying to access does not exist, may have been moved,
                    or the system may have just been redeployed so the link is temporarily unavailable.
                </div>

                <div class="status-box reveal reveal-delay-5">
                    <div class="status-icon">!</div>
                    <div class="status-content">
                        <strong>RentHouse Management</strong>
                        <span>The system is still running normally. Only this resource is currently unavailable.</span>
                    </div>
                </div>

                <div class="actions reveal reveal-delay-6">
                    <a class="btn btn-primary magnetic"
                       href="${pageContext.request.contextPath}/home">
                        <span>Go to Home</span>
                    </a>

                    <a class="btn btn-secondary magnetic"
                       href="javascript:history.back()">
                        <span>Go Back</span>
                    </a>
                </div>
            </div>

            <div class="right">
                <div class="right-inner reveal reveal-delay-4">
                    <div class="logo-box float-slow">
                        <img src="${pageContext.request.contextPath}/assets/images/logo/logo.png" alt="Logo">
                    </div>

                    <h3>RentHouse Management</h3>

                    <p>
                        A management system for rental houses, contracts, invoices, and residents.
                    </p>

                    <div class="suggestion glass-card">
                        <div class="suggestion-title">Quick Suggestions</div>
                        <ul>
                            <li>Check the URL again.</li>
                            <li>Return to the home page and try accessing the feature again.</li>
                            <li>If the system was just deployed, try refreshing after a few seconds.</li>
                        </ul>
                    </div>

                    <div class="pulse-lines">
                        <span></span>
                        <span></span>
                        <span></span>
                    </div>
                </div>
            </div>

            <div class="scan-line"></div>
            <div class="border-light"></div>
        </section>
    </main>

    <script src="${pageContext.request.contextPath}/assets/js/pages/error404.js"></script>
</body>
</html>