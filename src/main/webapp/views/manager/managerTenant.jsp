<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<layout:layout title="Manage Tenants" active="m_tenants">

    <!-- Page CSS -->
    <link rel="stylesheet" href="${ctx}/assets/css/views/managerTenant.css">

    <div class="mt-container">

        <!-- HEADER -->
        <div class="mt-header">
            <h2>Manage Tenants</h2>
            <p>View and manage all tenant information</p>
        </div>

        <!-- SEARCH -->
        <div class="mt-search-box">
            <form method="get"
                  action="${ctx}/manager/tenants"
                  class="mt-search-form">
                <input type="text"
                       name="keyword"
                       value="${keyword}"
                       class="mt-search-input"
                       placeholder="Search by tenant ID, name, phone or email...">
                <button type="submit" class="mt-btn mt-btn-outline">
                    <i class="bi bi-search"></i> Search
                </button>
            </form>
        </div>

        <!-- CARD -->
        <div class="mt-card">
            <div class="mt-card-title">
                All Tenants (<c:out value="${totalRecords}"/>)
            </div>

            <div class="mt-table-wrap">
                <table class="mt-table">
                    <thead>
                        <tr>
                            <th>Tenant ID</th>
                            <th>Full Name</th>
                            <th>Phone Number</th>
                            <th>Email</th>
                            <th>Citizen ID</th>
                            <th>Date of Birth</th>
                            <th>Status</th>
                            <th style="width:140px;">Action</th>
                        </tr>
                    </thead>

                    <tbody>
                        <c:forEach var="t" items="${tenants}">
                            <tr>
                                <td class="mt-mono">${t.tenantId}</td>
                                <td class="mt-name">${t.fullName}</td>
                                <td>${t.phoneNumber}</td>
                                <td>${t.email}</td>
                                <td>
                                    ${t.identityCode.substring(0,2)}******${t.identityCode.substring(t.identityCode.length()-2)}
                                </td>
                                <td>
                                    <fmt:formatDate value="${t.dateOfBirth}" pattern="yyyy-MM-dd"/>
                                </td>

                                <!-- STATUS BUTTON -->
                                <td>
                                    <button type="button"
                                            class="mt-btn-status
                                            <c:choose>
                                                <c:when test="${t.accountStatus == 'ACTIVE'}">mt-btn-active</c:when>
                                                <c:when test="${t.accountStatus == 'LOCKED'}">mt-btn-locked</c:when>
                                                <c:otherwise>mt-btn-pending</c:otherwise>
                                            </c:choose>"
                                            data-tenant-id="${t.tenantId}"
                                            data-current-status="${t.accountStatus}"
                                            data-tenant-name="${t.fullName}">
                                        <c:choose>
                                            <c:when test="${t.accountStatus == 'ACTIVE'}">
                                                <i class="bi bi-unlock-fill"></i> ACTIVE
                                            </c:when>
                                            <c:when test="${t.accountStatus == 'LOCKED'}">
                                                <i class="bi bi-lock-fill"></i> LOCKED
                                            </c:when>
                                            <c:otherwise>
                                                <i class="bi bi-hourglass-split"></i> ${t.accountStatus}
                                            </c:otherwise>
                                        </c:choose>
                                    </button>
                                </td>

                                <!-- ACTION: EDIT -->
                                <td>
                                    <button type="button"
                                            class="mt-btn mt-btn-edit js-open-edit"
                                            data-tenant-id="${t.tenantId}"
                                            data-fullname="${t.fullName}"
                                            data-identity="${t.identityCode}"
                                            data-phone="${t.phoneNumber}"
                                            data-email="${t.email}"
                                            data-dob="<fmt:formatDate value='${t.dateOfBirth}' pattern='yyyy-MM-dd'/>"
                                            data-gender="${t.gender}"
                                            data-address="${t.address}">
                                        <i class="bi bi-pencil-square"></i> Edit
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty tenants}">
                            <tr>
                                <td colspan="8" class="mt-empty">No tenants found</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <!-- PAGINATION -->
            <c:if test="${totalPages > 1}">
                <div class="mt-pagination">
                    <c:set var="baseUrl" value="${ctx}/manager/tenants"/>
                    <c:choose>
                        <c:when test="${not empty keyword}">
                            <c:set var="baseUrl" value="${baseUrl}?keyword=${keyword}&amp;page="/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="baseUrl" value="${baseUrl}?page="/>
                        </c:otherwise>
                    </c:choose>

                    <!-- Prev -->
                    <c:choose>
                        <c:when test="${currentPage <= 1}">
                            <span class="mt-page-btn mt-page-disabled"><i class="bi bi-chevron-left"></i></span>
                            </c:when>
                            <c:otherwise>
                            <a class="mt-page-btn" href="${baseUrl}${currentPage - 1}">
                                <i class="bi bi-chevron-left"></i>
                            </a>
                        </c:otherwise>
                    </c:choose>

                    <!-- Page numbers + ellipsis -->
                    <c:forEach var="i" begin="1" end="${totalPages}">
                        <c:choose>
                            <c:when test="${i == 1 || i == 2 || i == 3 || i == totalPages
                                            || i == currentPage || i == currentPage - 1 || i == currentPage + 1}">
                                <c:choose>
                                    <c:when test="${i == currentPage}">
                                        <span class="mt-page-btn mt-page-active">${i}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <a class="mt-page-btn" href="${baseUrl}${i}">${i}</a>
                                    </c:otherwise>
                                </c:choose>
                            </c:when>

                            <c:when test="${i == totalPages - 1 && currentPage < totalPages - 2}">
                                <span class="mt-page-ellipsis">...</span>
                            </c:when>

                            <c:when test="${i == 4 && currentPage > 5}">
                                <span class="mt-page-ellipsis">...</span>
                            </c:when>
                        </c:choose>
                    </c:forEach>

                    <!-- Next -->
                    <c:choose>
                        <c:when test="${currentPage >= totalPages}">
                            <span class="mt-page-btn mt-page-disabled"><i class="bi bi-chevron-right"></i></span>
                            </c:when>
                            <c:otherwise>
                            <a class="mt-page-btn" href="${baseUrl}${currentPage + 1}">
                                <i class="bi bi-chevron-right"></i>
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>

        </div>
    </div>

    <!-- ===== EDIT MODAL ===== -->
    <div class="modal-overlay" id="editModal" aria-hidden="true">
        <div class="modal-box" role="dialog" aria-modal="true" aria-labelledby="editModalTitle">
            <button class="modal-close-btn" type="button" data-close="1" aria-label="Close">
                <i class="bi bi-x-lg"></i>
            </button>

            <div class="modal-title" id="editModalTitle">Edit Tenant Information</div>
            <div class="modal-subtitle">Update tenant details or remove incorrect information</div>

            <form method="post" action="${ctx}/manager/tenant/edit" id="editTenantForm">
                <input type="hidden" name="tenantId" id="modal_tenantId"/>
                <input type="hidden" name="page" value="${currentPage}"/>
                <input type="hidden" name="keyword" value="${keyword}"/>

                <div class="modal-grid">

                    <div class="modal-field">
                        <label>Full Name</label>
                        <div class="modal-field-row">
                            <input type="text" name="fullName" id="modal_fullName" placeholder="Full Name"/>
                            <button type="button" class="modal-clear-btn" data-clear="modal_fullName" title="Clear">
                                <i class="bi bi-trash3"></i>
                            </button>
                        </div>
                    </div>

                    <div class="modal-field">
                        <label>Phone Number</label>
                        <div class="modal-field-row">
                            <input type="text" name="phoneNumber" id="modal_phoneNumber" placeholder="Phone Number"/>
                            <button type="button" class="modal-clear-btn" data-clear="modal_phoneNumber" title="Clear">
                                <i class="bi bi-trash3"></i>
                            </button>
                        </div>
                    </div>

                    <div class="modal-field">
                        <label>Email</label>
                        <div class="modal-field-row">
                            <input type="email" name="email" id="modal_email" placeholder="Email"/>
                            <button type="button" class="modal-clear-btn" data-clear="modal_email" title="Clear">
                                <i class="bi bi-trash3"></i>
                            </button>
                        </div>
                    </div>

                    <div class="modal-field">
                        <label>Citizen ID (12 digits)</label>
                        <div class="modal-field-row">
                            <input type="text" name="identityCode" id="modal_identityCode" placeholder="Citizen ID" maxlength="12"/>
                            <button type="button" class="modal-clear-btn" data-clear="modal_identityCode" title="Clear">
                                <i class="bi bi-trash3"></i>
                            </button>
                        </div>
                    </div>

                    <div class="modal-field">
                        <label>Date of Birth</label>
                        <div class="modal-field-row">
                            <input type="date" name="dateOfBirth" id="modal_dateOfBirth"/>
                            <button type="button" class="modal-clear-btn" data-clear="modal_dateOfBirth" title="Clear">
                                <i class="bi bi-trash3"></i>
                            </button>
                        </div>
                    </div>

                    <div class="modal-field">
                        <label>Gender</label>
                        <div class="modal-field-row">
                            <select name="gender" id="modal_gender">
                                <option value="">-- Select --</option>
                                <option value="1">Male</option>
                                <option value="0">Female</option>
                            </select>
                            <button type="button" class="modal-clear-btn" data-clear-select="modal_gender" title="Clear">
                                <i class="bi bi-trash3"></i>
                            </button>
                        </div>
                    </div>

                    <div class="modal-field" style="grid-column: span 2;">
                        <label>Address</label>
                        <div class="modal-field-row">
                            <input type="text" name="address" id="modal_address" placeholder="Address"/>
                            <button type="button" class="modal-clear-btn" data-clear="modal_address" title="Clear">
                                <i class="bi bi-trash3"></i>
                            </button>
                        </div>
                    </div>

                </div>

                <div class="modal-actions">
                    <button type="button" class="modal-btn-cancel" data-close="1">
                        <i class="bi bi-x-circle"></i> Cancel
                    </button>
                    <button type="button" class="modal-btn-save" id="openConfirmBtn">
                        <i class="bi bi-check2-circle"></i> Save Changes
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- ===== CONFIRM DIALOG (SAVE) ===== -->
    <div class="confirm-overlay" id="confirmDialog" aria-hidden="true">
        <div class="confirm-box" role="dialog" aria-modal="true" aria-labelledby="confirmTitle">
            <div class="confirm-icon"><i class="bi bi-save2"></i></div>
            <div class="confirm-title" id="confirmTitle">Confirm update</div>
            <div class="confirm-subtitle">Are you sure you want to update this tenant?</div>
            <div class="confirm-actions">
                <button type="button" class="confirm-btn-cancel" data-close-confirm="1">
                    <i class="bi bi-x-circle"></i> Cancel
                </button>
                <button type="button" class="confirm-btn-ok" id="confirmSaveBtn">
                    <i class="bi bi-check2-circle"></i> OK
                </button>
            </div>
        </div>
    </div>

    <!-- ===== TOGGLE STATUS CONFIRM ===== -->
    <div class="confirm-overlay" id="toggleStatusDialog" aria-hidden="true">
        <div class="confirm-box" role="dialog" aria-modal="true" aria-labelledby="toggleStatusTitle">
            <div class="confirm-icon" id="toggleStatusIcon">
                <i class="bi bi-arrow-repeat"></i>
            </div>
            <div class="confirm-title" id="toggleStatusTitle">Confirm status change</div>
            <div class="confirm-subtitle" id="toggleStatusSubtitle"></div>
            <div class="confirm-actions">
                <button type="button" class="confirm-btn-cancel" data-close-toggle="1">
                    <i class="bi bi-x-circle"></i> Cancel
                </button>
                <button type="button" class="confirm-btn-ok" id="toggleStatusOkBtn">
                    <i class="bi bi-check2-circle"></i> OK
                </button>
            </div>
        </div>
    </div>

    <!-- TOAST -->
    <div class="toast" id="errorToast" aria-hidden="true">
        <div class="toast-icon"><i class="bi bi-x-circle-fill"></i></div>
        <div class="toast-body">
            <div class="toast-title">Validation Error</div>
            <div class="toast-message" id="toastMessage"></div>
        </div>
        <button class="toast-close" type="button" id="toastCloseBtn" aria-label="Close">
            <i class="bi bi-x-lg"></i>
        </button>
    </div>

    <!-- Page JS -->
    <script src="${ctx}/assets/js/pages/managerTenant.js"></script>

</layout:layout>