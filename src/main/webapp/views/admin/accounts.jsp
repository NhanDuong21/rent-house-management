<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="role" value="${empty role ? 'ALL' : role}" />

<t:layout title="Manage Accounts"
          active="a_accounts"
          cssFile="${ctx}/assets/css/views/admin/accounts.css">

    <div class="ma-container">

        <!-- HEADER -->
        <div class="ma-header">
            <div>
                <h2>Manage Accounts</h2>
                <p>View and manage tenant &amp; manager accounts</p>
            </div>

            <a href="${ctx}/admin/accounts/create" class="ma-primary-btn">
                <span class="ma-btn-ico">
                    <i class="bi bi-plus-lg"></i>
                </span>
                Create Account
            </a>
        </div>

        <!-- ALERT -->
        <c:if test="${not empty success}">
            <div class="ma-alert ma-alert-success">
                <span class="ma-alert-ico">
                    <i class="bi bi-check-circle-fill"></i>
                </span>
                <div class="ma-alert-title">${success}</div>
            </div>
        </c:if>

        <c:if test="${not empty error}">
            <div class="ma-alert ma-alert-danger">
                <span class="ma-alert-ico">
                    <i class="bi bi-x-circle-fill"></i>
                </span>
                <div class="ma-alert-title">${error}</div>
            </div>
        </c:if>

        <!-- SEARCH + FILTER -->
        <div class="ma-search-box">
            <form id="maSearchForm" class="ma-search-form"
                  method="get" action="${ctx}/admin/accounts">

                <input id="maKeyword"
                       type="text"
                       name="keyword"
                       value="${param.keyword}"
                       placeholder="Search by name or email..."
                       autocomplete="off">

                <select id="maRole" name="role">
                    <option value="ALL" ${fn:toUpperCase(role) == 'ALL' ? 'selected' : ''}>All Roles</option>
                    <option value="TENANT" ${fn:toUpperCase(role) == 'TENANT' ? 'selected' : ''}>TENANT</option>
                    <option value="MANAGER" ${fn:toUpperCase(role) == 'MANAGER' ? 'selected' : ''}>MANAGER</option>
                </select>

                <c:if test="${not empty param.keyword || fn:toUpperCase(role) ne 'ALL'}">
                    <a class="ma-clear-btn" href="${ctx}/admin/accounts">Clear</a>
                </c:if>
            </form>
        </div>

        <!-- TABLE -->
        <div class="ma-card">
            <div class="ma-card-title">Accounts List</div>

            <div class="ma-table-wrap">
                <table class="ma-table">
                    <thead>
                        <tr>
                            <th style="width:90px;">ID</th>
                            <th>Full Name</th>
                            <th>Email</th>
                            <th style="width:120px;">Role</th>
                            <th style="width:140px;">Status</th>
                            <th style="width:190px;">Created At</th>
                            <th style="width:260px;">Actions</th>
                        </tr>
                    </thead>

                    <tbody>
                        <c:if test="${empty accounts}">
                            <tr>
                                <td colspan="7" class="ma-empty">No accounts found</td>
                            </tr>
                        </c:if>

                        <c:forEach var="a" items="${accounts}">
                            <tr>
                                <td class="ma-mono">${a.accountId}</td>
                                <td class="ma-name">${a.fullName}</td>
                                <td>${a.email}</td>

                                <td>
                                    <span class="ma-badge role-${fn:toLowerCase(a.role)}">
                                        ${a.role}
                                    </span>
                                </td>

                                <td>
                                    <span class="ma-badge status-${fn:toLowerCase(a.status)}">
                                        ${a.status}
                                    </span>
                                </td>

                                <td>${a.createdAt}</td>

                                <!-- ACTIONS -->
                                <td>
                                    <div class="ma-actions">

                                        <!-- TOGGLE STATUS -->
                                        <form method="post"
                                              action="${ctx}/admin/accounts/toggle-status"
                                              class="ma-inline">

                                            <input type="hidden" name="accountType" value="${a.accountType}">
                                            <input type="hidden" name="accountId" value="${a.accountId}">
                                            <input type="hidden" name="currentStatus" value="${a.status}">

                                            <button type="submit"
                                                    class="ma-switch ${fn:toLowerCase(a.status) == 'active' ? 'on' : 'off'}"
                                                    title="Toggle Active / Locked">

                                                <span class="ma-switch-track">
                                                    <span class="ma-switch-thumb"></span>
                                                </span>

                                                <span class="ma-switch-label">
                                                    <c:choose>
                                                        <c:when test="${fn:toLowerCase(a.status) == 'active'}">
                                                            <i class="bi bi-unlock-fill"></i> Active
                                                        </c:when>
                                                        <c:otherwise>
                                                            <i class="bi bi-lock-fill"></i> Locked
                                                        </c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </button>
                                        </form>

                                        <!-- SET PASSWORD (OPEN MODAL) -->
                                        <button type="button"
                                                class="ma-action-btn warn ma-open-pass"
                                                data-account-id="${a.accountId}"
                                                data-account-type="${a.accountType}"
                                                data-fullname="${fn:escapeXml(a.fullName)}"
                                                data-email="${fn:escapeXml(a.email)}"
                                                title="Set new password">
                                            <span class="ma-action-ico">
                                                <i class="bi bi-key-fill"></i>
                                            </span>
                                            Reset Password
                                        </button>

                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- PAGINATION -->
        <c:if test="${totalPages > 1}">
            <div class="ma-pager">

                <div>
                    Total: <strong>${totalRecords}</strong>
                </div>

                <div class="ma-pager-right">

                    <c:choose>
                        <c:when test="${page > 1}">
                            <a class="ma-page-btn"
                               href="${ctx}/admin/accounts?page=${page-1}&pageSize=${pageSize}&role=${role}&keyword=${param.keyword}">
                                <i class="bi bi-chevron-left"></i>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <span class="ma-page-btn disabled">
                                <i class="bi bi-chevron-left"></i>
                            </span>
                        </c:otherwise>
                    </c:choose>

                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <a class="ma-page-btn ${i == page ? 'active' : ''}"
                           href="${ctx}/admin/accounts?page=${i}&pageSize=${pageSize}&role=${role}&keyword=${param.keyword}">
                            ${i}
                        </a>
                    </c:forEach>

                    <c:choose>
                        <c:when test="${page < totalPages}">
                            <a class="ma-page-btn"
                               href="${ctx}/admin/accounts?page=${page+1}&pageSize=${pageSize}&role=${role}&keyword=${param.keyword}">
                                <i class="bi bi-chevron-right"></i>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <span class="ma-page-btn disabled">
                                <i class="bi bi-chevron-right"></i>
                            </span>
                        </c:otherwise>
                    </c:choose>

                </div>
            </div>
        </c:if>

    </div>

    <!-- ===== SET PASSWORD MODAL (ONE TIME) ===== -->
    <div class="ma-modal" id="maPassModal" aria-hidden="true">
        <div class="ma-modal-backdrop" data-close="1"></div>

        <div class="ma-modal-dialog" role="dialog" aria-modal="true" aria-labelledby="maPassTitle">
            <div class="ma-modal-card">

                <div class="ma-modal-head">
                    <div>
                        <div class="ma-modal-title" id="maPassTitle">
                            <i class="bi bi-shield-lock-fill"></i>
                            Set new password
                        </div>
                        <div class="ma-modal-sub" id="maPassSub">—</div>
                    </div>

                    <button type="button" class="ma-modal-x" id="maPassClose" aria-label="Close">
                        <i class="bi bi-x-lg"></i>
                    </button>
                </div>

                <form id="maPassForm" class="ma-modal-body" method="post" action="${ctx}/admin/accounts/reset-password">
                    <input type="hidden" name="accountId" id="maPassAccountId">
                    <input type="hidden" name="accountType" id="maPassAccountType">

                    <div class="ma-field">
                        <label class="ma-label" for="maNewPass">New password</label>
                        <div class="ma-input">
                            <span class="ma-input-ico"><i class="bi bi-key"></i></span>
                            <input id="maNewPass" name="newPassword" type="password" placeholder="Enter new password">
                        </div>
                        <div class="ma-hint">Use at least 6 characters.</div>
                    </div>

                    <div class="ma-field">
                        <label class="ma-label" for="maConfirmPass">Confirm password</label>
                        <div class="ma-input">
                            <span class="ma-input-ico"><i class="bi bi-check2-circle"></i></span>
                            <input id="maConfirmPass" name="confirmPassword" type="password" placeholder="Re-enter new password">
                        </div>
                        <div class="ma-error" id="maPassError"></div>
                    </div>

                    <label class="ma-check">
                        <input type="checkbox" id="maShowPass">
                        <span>Show passwords</span>
                    </label>

                    <div class="ma-modal-foot">
                        <button type="button" class="ma-btn ghost" data-close="1">
                            Cancel
                        </button>

                        <button type="submit" class="ma-btn primary">
                            <i class="bi bi-save2"></i>
                            Update password
                        </button>
                    </div>
                </form>

            </div>
        </div>
    </div>

    <!-- REALTIME SEARCH -->
    <script>
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
    </script>

    <!-- MODAL JS (separate file) -->
    <script src="${ctx}/assets/js/pages/admin/accounts.js"></script>

</t:layout>