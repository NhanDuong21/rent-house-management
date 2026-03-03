<%-- 
    Document   : layout
    Created on : Feb 6, 2026, 6:17:05 AM
    Author     : Duong Thien Nhan - CE190741
--%>
<%@tag pageEncoding="UTF-8" body-content="scriptless"%>
<%@tag import="Models.authentication.AuthResult"%>
<%@tag import="Models.entity.Tenant"%>
<%@tag import="Models.entity.Staff"%>

<%@attribute name="title" required="false" type="java.lang.String"%>
<%@attribute name="active" required="false" type="java.lang.String"%>
<%@attribute name="cssFile" required="false" type="java.lang.String"%>

<%
    String ctx = request.getContextPath();

    AuthResult auth = (AuthResult) session.getAttribute("auth");
    Tenant tenant = (auth == null) ? null : auth.getTenant();
    Staff staff = (auth == null) ? null : auth.getStaff();

    // ===== ROLE fallback (fix OTP login role null => Guest) =====
    String role;
    if (auth == null) {
        role = "GUEST";
    } else if (auth.getRole() != null && !auth.getRole().isBlank()) {
        role = auth.getRole();
    } else if (staff != null) {
        // staffRole: MANAGER/ADMIN
        role = (staff.getStaffRole() == null || staff.getStaffRole().isBlank()) ? "STAFF" : staff.getStaffRole();
    } else if (tenant != null) {
        role = "TENANT";
    } else {
        role = "GUEST";
    }

    String tenantStatus = (tenant == null || tenant.getAccountStatus() == null) ? null : tenant.getAccountStatus();
    boolean isTenantPending = (tenant != null && "PENDING".equalsIgnoreCase(tenantStatus));
    boolean isTenantActive = (tenant != null && "ACTIVE".equalsIgnoreCase(tenantStatus));

    String displayName = "Guest";
    if (!"GUEST".equalsIgnoreCase(role)) {
        displayName = (tenant != null && tenant.getFullName() != null) ? tenant.getFullName()
                : (staff != null && staff.getFullName() != null) ? staff.getFullName()
                : "User";
    }

    String firstLetter = (displayName != null && !displayName.isBlank())
            ? displayName.trim().substring(0, 1).toUpperCase()
            : "G";

    String _title = (title == null || title.isBlank()) ? "RentHouse" : title;
    String _active = (active == null) ? "" : active;

    // cssFile optional (page css)
    String pageCss = (cssFile == null || cssFile.isBlank()) ? null : cssFile;

    boolean isTenant = "TENANT".equalsIgnoreCase(role);
    boolean isManager = "MANAGER".equalsIgnoreCase(role);
    boolean isAdmin = "ADMIN".equalsIgnoreCase(role);

    // label show in header
    String roleLabel = role;
    if (isTenant && tenantStatus != null) {
        roleLabel = "TENANT • " + tenantStatus.toUpperCase();
    }
%>

<!doctype html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title><%=_title%></title>
        <link rel="icon" type="image/png"
              href="${pageContext.request.contextPath}/assets/images/logo/favicon_logo.png">

        <link rel="stylesheet" href="<%=ctx%>/assets/css/layout/layout.css">
        <!-- only added: bootstrap icons -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

        <% if (pageCss != null) {%>
        <link rel="stylesheet" href="<%=pageCss%>">
        <% }%>
    </head>

    <body>
        <div class="rh-layout">

            <!-- SIDEBAR -->
            <aside class="rh-sidebar" id="rhSidebar">
                <div class="rh-logo">
                    <a href="<%=ctx%>/home">
                        <img src="<%=ctx%>/assets/images/logo/logo.png" alt="RentHouse">
                    </a>
                </div>

                <nav class="rh-menu">
                    <a class="rh-link <%= "home".equals(_active) ? "active" : ""%>" href="<%=ctx%>/home">
                        <i class="bi bi-house-door me-2"></i> Home
                    </a>

                    <a class="rh-link <%= "contact".equals(_active) ? "active" : ""%>" href="<%=ctx%>/contact">
                        <i class="bi bi-envelope me-2"></i> Contact
                    </a>

                    <% if (isTenant) { %>
                    <div class="rh-section">Tenant</div>

                    <% if (isTenantPending) {%>
                    <!-- PENDING: chỉ được xem hợp đồng để chuyển khoản + xác nhận -->
                    <a class="rh-link <%= "t_contract".equals(_active) ? "active" : ""%>"
                       href="<%=ctx%>/tenant/contract">
                        <i class="bi bi-file-earmark-text me-2"></i> My Contract
                    </a>
                    <% } else {%>
                    <!-- ACTIVE: đầy đủ -->
                    <a class="rh-link <%= "t_room".equals(_active) ? "active" : ""%>"
                       href="<%=ctx%>/tenant/room">
                        <i class="bi bi-door-open me-2"></i> My Room
                    </a>

                    <a class="rh-link <%= "t_contract".equals(_active) ? "active" : ""%>"
                       href="<%=ctx%>/tenant/contract">
                        <i class="bi bi-file-earmark-text me-2"></i> My Contract
                    </a>

                    <a class="rh-link <%= "t_bill".equals(_active) ? "active" : ""%>"
                       href="<%=ctx%>/tenant/bill">
                        <i class="bi bi-receipt me-2"></i> My Bills
                    </a>

                    <a class="rh-link <%= "t_maintenance".equals(_active) ? "active" : ""%>"
                       href="<%=ctx%>/maintenance">
                        <i class="bi bi-tools me-2"></i> Maintenance Requests
                    </a>

                    <a class="rh-link <%= "t_utility".equals(_active) ? "active" : ""%>"
                       href="<%=ctx%>/tenant/utility">
                        <i class="bi bi-lightning-charge me-2"></i> Utility
                    </a>

                    <a class="rh-link <%= "t_profile".equals(_active) ? "active" : ""%>"
                       href="<%=ctx%>/profile">
                        <i class="bi bi-person-circle me-2"></i> Profile
                    </a>
                    <% } %>
                    <% } %>

                    <%-- STAFF / MANAGER / ADMIN --%>

                    <% if (isManager) {%>
                    <div class="rh-section">Manager</div>

                    <a class="rh-link <%= "m_profile".equals(_active) ? "active" : ""%>"
                       href="<%=ctx%>/profile">
                        <i class="bi bi-person-badge me-2"></i> Manager Profile
                    </a>

                    <a class="rh-link <%= "m_rooms".equals(_active) ? "active" : ""%>"
                       href="<%=ctx%>/manager/rooms">
                        <i class="bi bi-building me-2"></i> Manager Rooms
                    </a>

                    <a class="rh-link <%= "m_tenants".equals(_active) ? "active" : ""%>"
                       href="<%=ctx%>/manager/tenants">
                        <i class="bi bi-people me-2"></i> Manager Tenants
                    </a>

                    <a class="rh-link <%= "m_utilities".equals(_active) ? "active" : ""%>"
                       href="<%=ctx%>/manager/utilities">
                        <i class="bi bi-lightning me-2"></i> Utilities
                    </a>

                    <a class="rh-link <%= "m_contracts".equals(_active) ? "active" : ""%>"
                       href="<%=ctx%>/manager/contracts">
                        <i class="bi bi-file-earmark-check me-2"></i> Manager Contracts
                    </a>

                    <a class="rh-link <%= "m_billing".equals(_active) ? "active" : ""%>"
                       href="<%=ctx%>/manager/billing">
                        <i class="bi bi-cash-stack me-2"></i> Manage Billing
                    </a>

                    <a class="rh-link <%= "m_maintenance".equals(_active) ? "active" : ""%>"
                       href="<%=ctx%>/manager/maintenance">
                        <i class="bi bi-wrench-adjustable me-2"></i> Manage Maintenance
                    </a>

                    <a class="rh-link <%= "m_accounts".equals(_active) ? "active" : ""%>"
                       href="<%=ctx%>/manager/accounts">
                        <i class="bi bi-person-gear me-2"></i> Manage Accounts
                    </a>

                    <div class="rh-spacer"></div>
                    <a class="rh-dashboard manager" href="<%=ctx%>/manager/home">Manager Dashboard</a>
                    <% } %>

                    <% if (isAdmin) {%>
                    <div class="rh-section">Admin</div>

                    <a class="rh-link <%= "a_home".equals(_active) ? "active" : ""%>"
                       href="<%=ctx%>/home">
                        <i class="bi bi-house-door me-2"></i> Home
                    </a>

                    <a class="rh-link <%= "a_dashboard".equals(_active) ? "active" : ""%>"
                       href="<%=ctx%>/admin/home">
                        <i class="bi bi-speedometer2 me-2"></i> Admin Dashboard
                    </a>

                    <a class="rh-link <%= "a_profile".equals(_active) ? "active" : ""%>"
                       href="<%=ctx%>/profile">
                        <i class="bi bi-person-circle me-2"></i> Admin Profile
                    </a>

                    <a class="rh-link <%= "a_accounts".equals(_active) ? "active" : ""%>"
                       href="<%=ctx%>/admin/accounts">
                        <i class="bi bi-people-fill me-2"></i> Manage Accounts
                    </a>

                    <a class="rh-link <%= "a_settings".equals(_active) ? "active" : ""%>"
                       href="<%=ctx%>/admin/settings">
                        <i class="bi bi-gear me-2"></i> Configure System Settings
                    </a>

                    <a class="rh-link <%= "a_contracts".equals(_active) ? "active" : ""%>"
                       href="<%=ctx%>/admin/contracts">
                        <i class="bi bi-file-earmark-text me-2"></i> View All Contracts
                    </a>

                    <a class="rh-link <%= "a_rooms".equals(_active) ? "active" : ""%>"
                       href="<%=ctx%>/admin/rooms">
                        <i class="bi bi-building-gear me-2"></i> Room Administration
                    </a>

                    <div class="rh-spacer"></div>
                    <a class="rh-dashboard admin" href="<%=ctx%>/admin/home">Admin Dashboard</a>
                    <% }%>

                </nav>
            </aside>

            <!-- MAIN -->
            <main class="rh-main">

                <!-- HEADER -->
                <header class="rh-topbar">
                    <div class="rh-topbar-left">
                        <button class="rh-icon-btn" type="button" id="rhToggleSidebar">☰</button>

                        <!-- TOP NAV (all roles) -->
                        <nav class="rh-topnav" aria-label="Main navigation">
                            <a class="rh-topnav-link <%= "home".equals(_active) ? "active" : ""%>"
                               href="<%=ctx%>/home">HOME</a>

                            <a class="rh-topnav-link <%= "about".equals(_active) ? "active" : ""%>"
                               href="<%=ctx%>/home">ABOUT US</a>

                            <a class="rh-topnav-link <%= "news".equals(_active) ? "active" : ""%>"
                               href="<%=ctx%>/home">NEWS</a>

                            <a class="rh-topnav-link <%= "recruit".equals(_active) ? "active" : ""%>"
                               href="<%=ctx%>/home">RECRUITMENT</a>

                            <a class="rh-topnav-link <%= "contact".equals(_active) ? "active" : ""%>"
                               href="<%=ctx%>/contact">CONTACT</a>
                        </nav>
                    </div>

                    <div class="rh-topbar-right">
                        <!-- ❌ removed rhOpenFilter button -->

                        <div class="rh-user">
                            <div class="rh-avatar">
                                <img src="<%=ctx%>/assets/images/avatar/avtDefault.png" alt="Avatar">
                            </div>
                            <div class="rh-user-meta">
                                <div class="rh-user-name"><%=displayName%></div>
                                <div class="rh-user-role"><%=roleLabel%></div>
                            </div>

                            <% if (auth == null) {%>
                            <a class="rh-btn primary" href="<%=ctx%>/login">Login</a>
                            <% } else {%>
                            <a class="rh-btn outline js-logout" href="<%=ctx%>/logout">Logout</a>
                            <% }%>
                        </div>
                    </div>
                </header>

                <!-- BODY -->
                <section class="rh-content">
                    <jsp:doBody/>
                </section>

                <!-- FOOTER -->
                <footer class="rh-footer">
                    <span>2026 © SWP391 - Group 4</span>
                    <span class="rh-dot">•</span>
                    <a href="https://github.com/NhanDuong21/land-house-management"
                       target="_blank">LandHouseManagement</a>
                </footer>

            </main>
        </div>

        <script src="<%=ctx%>/assets/js/core/layout.js"></script>
    </body>
</html>