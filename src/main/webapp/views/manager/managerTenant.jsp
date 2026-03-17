<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<layout:layout title="Manage Tenants" active="m_tenants">

    <!-- Page CSS -->
    <link rel="stylesheet" href="${ctx}/assets/css/views/managerTenant.css">
    <style>
        /* ===== STATUS BADGE (tự động, chỉ đọc) ===== */
        .mt-status-badge {
            display: inline-flex;
            align-items: center;
            gap: 5px;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 600;
            letter-spacing: 0.3px;
            cursor: default;
            user-select: none;
        }
        .mt-badge-active {
            background-color: #dcfce7;
            color: #15803d;
            border: 1px solid #86efac;
        }
        .mt-badge-locked {
            background-color: #fee2e2;
            color: #b91c1c;
            border: 1px solid #fca5a5;
        }
        .mt-badge-pending {
            background-color: #fef9c3;
            color: #854d0e;
            border: 1px solid #fde047;
        }

        /* ===== ROOM BADGE ===== */
        .mt-room-badge {
            display: inline-flex;
            align-items: center;
            gap: 4px;
            padding: 3px 10px;
            border-radius: 12px;
            font-size: 0.8rem;
            font-weight: 600;
            background-color: #eff6ff;
            color: #1d4ed8;
            border: 1px solid #bfdbfe;
            white-space: nowrap;
        }
        .mt-room-none {
            color: #9ca3af;
            font-size: 0.9rem;
        }

        /* ===== VIEW-ONLY BADGE trong modal ===== */
        .modal-view-badge {
            display: inline-flex;
            align-items: center;
            gap: 5px;
            padding: 3px 10px;
            border-radius: 12px;
            font-size: 0.75rem;
            font-weight: 600;
            background-color: #f1f5f9;
            color: #64748b;
            border: 1px solid #cbd5e1;
            margin-left: 8px;
            vertical-align: middle;
        }

        /* ===== RESET PASSWORD MODAL ===== */
        .rp-modal-overlay {
            display: none;
            position: fixed;
            inset: 0;
            background: rgba(0,0,0,0.45);
            z-index: 1100;
            align-items: center;
            justify-content: center;
        }
        .rp-modal-overlay.is-open { display: flex; }
        .rp-modal-box {
            background: #fff;
            border-radius: 14px;
            padding: 32px 28px 24px;
            width: 100%;
            max-width: 420px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.18);
            position: relative;
        }
        .rp-modal-title {
            font-size: 1.1rem;
            font-weight: 700;
            color: #1e293b;
            margin-bottom: 4px;
        }
        .rp-modal-sub {
            font-size: 0.82rem;
            color: #64748b;
            margin-bottom: 20px;
        }
        .rp-field { margin-bottom: 14px; }
        .rp-field label {
            font-size: 0.82rem;
            font-weight: 600;
            color: #374151;
            display: block;
            margin-bottom: 5px;
        }
        .rp-field input {
            width: 100%;
            padding: 8px 12px;
            border: 1px solid #d1d5db;
            border-radius: 8px;
            font-size: 0.9rem;
            outline: none;
            box-sizing: border-box;
        }
        .rp-field input:focus {
            border-color: #6366f1;
            box-shadow: 0 0 0 3px rgba(99,102,241,.12);
        }
        .rp-actions {
            display: flex;
            gap: 10px;
            justify-content: flex-end;
            margin-top: 20px;
        }
        .rp-btn-cancel {
            padding: 8px 18px;
            border-radius: 8px;
            border: 1px solid #d1d5db;
            background: #f8fafc;
            color: #374151;
            font-size: 0.85rem;
            cursor: pointer;
        }
        .rp-btn-save {
            padding: 8px 18px;
            border-radius: 8px;
            border: none;
            background: #6366f1;
            color: #fff;
            font-size: 0.85rem;
            font-weight: 600;
            cursor: pointer;
        }
        .rp-btn-save:hover { background: #4f46e5; }
        .rp-error {
            display: none;
            padding: 8px 12px;
            border-radius: 8px;
            background: #fee2e2;
            color: #b91c1c;
            font-size: 0.82rem;
            margin-bottom: 12px;
        }
        .rp-error.is-show { display: block; }

        /* nút Reset Password trong modal */
        .modal-btn-reset {
            padding: 8px 16px;
            border-radius: 8px;
            border: 1px solid #fca5a5;
            background: #fff0f0;
            color: #b91c1c;
            font-size: 0.85rem;
            font-weight: 600;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            gap: 6px;
        }
        .modal-btn-reset:hover { background: #fee2e2; }
    </style>

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
                            <th>Room</th>
                            <th>Status</th>
                            <th style="width:140px;">Action</th>
                        </tr>
                    </thead>

                    <tbody>
                        <c:forEach var="t" items="${tenants}">
                            <c:set var="room"    value="${activeRoomMap[t.tenantId]}"/>
                            <c:set var="hasRoom" value="${not empty room}"/>
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

                                <!-- ROOM (active contract) -->
                                <td>
                                    <c:choose>
                                        <c:when test="${hasRoom}">
                                            <span class="mt-room-badge">
                                                <i class="bi bi-door-open-fill"></i> ${room}
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="mt-room-none">—</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <!-- STATUS BADGE -->
                                <td>
                                    <span class="mt-status-badge
                                        <c:choose>
                                            <c:when test="${t.accountStatus == 'ACTIVE'}">mt-badge-active</c:when>
                                            <c:when test="${t.accountStatus == 'LOCKED'}">mt-badge-locked</c:when>
                                            <c:otherwise>mt-badge-pending</c:otherwise>
                                        </c:choose>">
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
                                    </span>
                                </td>

                                <!-- ACTION: Edit (active contract) hoặc View (không có) -->
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
                                            data-address="${t.address}"
                                            data-has-room="${hasRoom}">
                                        <c:choose>
                                            <c:when test="${hasRoom}">
                                                <i class="bi bi-pencil-square"></i> Edit
                                            </c:when>
                                            <c:otherwise>
                                                <i class="bi bi-eye"></i> View
                                            </c:otherwise>
                                        </c:choose>
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty tenants}">
                            <tr>
                                <td colspan="9" class="mt-empty">No tenants found</td>
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

                    <!-- Page numbers -->
                    <c:forEach var="i" begin="1" end="${totalPages}">
                        <c:choose>
                            <c:when test="${i == currentPage}">
                                <span class="mt-page-btn mt-page-active">${i}</span>
                            </c:when>
                            <c:when test="${i >= currentPage - 2 && i <= currentPage + 2}">
                                <a class="mt-page-btn" href="${baseUrl}${i}">${i}</a>
                            </c:when>
                            <c:when test="${i == 1 || i == totalPages}">
                                <a class="mt-page-btn" href="${baseUrl}${i}">${i}</a>
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

    <!-- ===== EDIT / VIEW MODAL ===== -->
    <div class="modal-overlay" id="editModal" aria-hidden="true">
        <div class="modal-box" role="dialog" aria-modal="true" aria-labelledby="editModalTitle">
            <button class="modal-close-btn" type="button" data-close="1" aria-label="Close">
                <i class="bi bi-x-lg"></i>
            </button>

            <div class="modal-title" id="editModalTitle">
                Edit Tenant Information
                <span class="modal-view-badge" id="viewOnlyBadge" style="display:none;">
                    <i class="bi bi-eye"></i> View Only
                </span>
            </div>
            <div class="modal-subtitle" id="editModalSubtitle">Update tenant details or remove incorrect information</div>

            <form method="post" action="${ctx}/manager/tenant/edit" id="editTenantForm">
                <input type="hidden" name="tenantId" id="modal_tenantId"/>
                <input type="hidden" name="page" value="${currentPage}"/>
                <input type="hidden" name="keyword" value="${keyword}"/>

                <div class="modal-grid">

                    <div class="modal-field">
                        <label>Full Name</label>
                        <div class="modal-field-row">
                            <input type="text" name="fullName" id="modal_fullName" placeholder="Full Name"/>
                            <button type="button" class="modal-clear-btn js-clear-btn" data-clear="modal_fullName" title="Clear">
                                <i class="bi bi-trash3"></i>
                            </button>
                        </div>
                    </div>

                    <div class="modal-field">
                        <label>Phone Number</label>
                        <div class="modal-field-row">
                            <input type="text" name="phoneNumber" id="modal_phoneNumber" placeholder="Phone Number"/>
                            <button type="button" class="modal-clear-btn js-clear-btn" data-clear="modal_phoneNumber" title="Clear">
                                <i class="bi bi-trash3"></i>
                            </button>
                        </div>
                    </div>

                    <div class="modal-field">
                        <label>Email</label>
                        <div class="modal-field-row">
                            <input type="email" name="email" id="modal_email" placeholder="Email"/>
                            <button type="button" class="modal-clear-btn js-clear-btn" data-clear="modal_email" title="Clear">
                                <i class="bi bi-trash3"></i>
                            </button>
                        </div>
                    </div>

                    <div class="modal-field">
                        <label>Citizen ID (12 digits)</label>
                        <div class="modal-field-row">
                            <input type="text" name="identityCode" id="modal_identityCode" placeholder="Citizen ID" maxlength="12"/>
                            <button type="button" class="modal-clear-btn js-clear-btn" data-clear="modal_identityCode" title="Clear">
                                <i class="bi bi-trash3"></i>
                            </button>
                        </div>
                    </div>

                    <div class="modal-field">
                        <label>Date of Birth</label>
                        <div class="modal-field-row">
                            <input type="date" name="dateOfBirth" id="modal_dateOfBirth"/>
                            <button type="button" class="modal-clear-btn js-clear-btn" data-clear="modal_dateOfBirth" title="Clear">
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
                            <button type="button" class="modal-clear-btn js-clear-btn" data-clear-select="modal_gender" title="Clear">
                                <i class="bi bi-trash3"></i>
                            </button>
                        </div>
                    </div>

                    <div class="modal-field" style="grid-column: span 2;">
                        <label>Address</label>
                        <div class="modal-field-row">
                            <input type="text" name="address" id="modal_address" placeholder="Address"/>
                            <button type="button" class="modal-clear-btn js-clear-btn" data-clear="modal_address" title="Clear">
                                <i class="bi bi-trash3"></i>
                            </button>
                        </div>
                    </div>

                </div>

                <div class="modal-actions">
                    <button type="button" class="modal-btn-cancel" data-close="1">
                        <i class="bi bi-x-circle"></i> Cancel
                    </button>
                    <!-- Chỉ hiện khi có active contract -->
                    <button type="button" class="modal-btn-reset" id="btnOpenResetPassword" style="display:none;">
                        <i class="bi bi-key-fill"></i> Reset Password
                    </button>
                    <button type="button" class="modal-btn-save" id="openConfirmBtn" style="display:none;">
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

    <!-- ===== RESET PASSWORD MODAL ===== -->
    <div class="rp-modal-overlay" id="resetPasswordModal">
        <div class="rp-modal-box" role="dialog" aria-modal="true">
            <button class="modal-close-btn" type="button" id="btnCloseResetPassword" aria-label="Close">
                <i class="bi bi-x-lg"></i>
            </button>
            <div class="rp-modal-title">Reset Password</div>
            <div class="rp-modal-sub" id="rpModalSub">Đặt lại mật khẩu cho tenant</div>

            <div class="rp-error" id="rpError"></div>

            <form id="resetPasswordForm">
                <input type="hidden" id="rp_tenantId"/>

                <div class="rp-field">
                    <label>Mật khẩu mới</label>
                    <input type="password" id="rp_newPassword" placeholder="Tối thiểu 6 ký tự" autocomplete="new-password"/>
                </div>
                <div class="rp-field">
                    <label>Xác nhận mật khẩu</label>
                    <input type="password" id="rp_confirmPassword" placeholder="Nhập lại mật khẩu mới" autocomplete="new-password"/>
                </div>

                <div class="rp-actions">
                    <button type="button" class="rp-btn-cancel" id="btnCancelResetPassword">
                        <i class="bi bi-x-circle"></i> Cancel
                    </button>
                    <button type="submit" class="rp-btn-save">
                        <i class="bi bi-key-fill"></i> Reset Password
                    </button>
                </div>
            </form>
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
    <script>
        // ===== MỞ MODAL EDIT / VIEW =====
        document.querySelectorAll('.js-open-edit').forEach(btn => {
            btn.addEventListener('click', () => {
                const hasRoom = btn.dataset.hasRoom === 'true';

                // Điền dữ liệu
                document.getElementById('modal_tenantId').value     = btn.dataset.tenantId  || '';
                document.getElementById('modal_fullName').value     = btn.dataset.fullname  || '';
                document.getElementById('modal_identityCode').value = btn.dataset.identity  || '';
                document.getElementById('modal_phoneNumber').value  = btn.dataset.phone     || '';
                document.getElementById('modal_email').value        = btn.dataset.email     || '';
                document.getElementById('modal_dateOfBirth').value  = btn.dataset.dob       || '';
                document.getElementById('modal_address').value      = btn.dataset.address   || '';

                const g = btn.dataset.gender;
                document.getElementById('modal_gender').value =
                    (g !== undefined && g !== 'null' && g !== '') ? g : '';

                // Tiêu đề & subtitle
                const titleNode = document.getElementById('editModalTitle').childNodes[0];
                titleNode.nodeValue = hasRoom ? 'Edit Tenant Information' : 'View Tenant Information';
                document.getElementById('editModalSubtitle').textContent = hasRoom
                    ? 'Update tenant details or remove incorrect information'
                    : 'This tenant has no active contract. Information is read-only.';

                // View-only badge
                document.getElementById('viewOnlyBadge').style.display = hasRoom ? 'none' : '';

                // Readonly / editable cho tất cả input text/date/email
                document.querySelectorAll(
                    '#editTenantForm input:not([type=hidden])'
                ).forEach(f => {
                    f.readOnly = !hasRoom;
                    f.style.background = hasRoom ? '' : '#f8fafc';
                });
                document.getElementById('modal_gender').disabled = !hasRoom;

                // Clear buttons
                document.querySelectorAll('.js-clear-btn').forEach(b => {
                    b.style.display = hasRoom ? '' : 'none';
                });

                // Nút Save + Reset Password
                document.getElementById('openConfirmBtn').style.display       = hasRoom ? '' : 'none';
                document.getElementById('btnOpenResetPassword').style.display = hasRoom ? '' : 'none';

                // Lưu tenantId để dùng khi mở reset password modal
                document.getElementById('btnOpenResetPassword').dataset.tenantId  = btn.dataset.tenantId;
                document.getElementById('btnOpenResetPassword').dataset.tenantName = btn.dataset.fullname;

                // Mở modal
                const modal = document.getElementById('editModal');
                modal.setAttribute('aria-hidden', 'false');
                modal.style.display = 'flex';
            });
        });

        // ===== MỞ RESET PASSWORD MODAL =====
        document.getElementById('btnOpenResetPassword').addEventListener('click', () => {
            const rpBtn = document.getElementById('btnOpenResetPassword');
            document.getElementById('rp_tenantId').value         = rpBtn.dataset.tenantId;
            document.getElementById('rpModalSub').textContent    = 'Đặt lại mật khẩu cho: ' + (rpBtn.dataset.tenantName || '');
            document.getElementById('rp_newPassword').value      = '';
            document.getElementById('rp_confirmPassword').value  = '';
            document.getElementById('rpError').classList.remove('is-show');

            document.getElementById('editModal').style.display = 'none';
            document.getElementById('resetPasswordModal').classList.add('is-open');
        });

        // ===== ĐÓNG RESET PASSWORD MODAL → quay lại edit modal =====
        ['btnCloseResetPassword', 'btnCancelResetPassword'].forEach(id => {
            document.getElementById(id).addEventListener('click', () => {
                document.getElementById('resetPasswordModal').classList.remove('is-open');
                document.getElementById('editModal').style.display = 'flex';
            });
        });

        // ===== SUBMIT RESET PASSWORD =====
        document.getElementById('resetPasswordForm').addEventListener('submit', function(e) {
            e.preventDefault();

            const tenantId   = document.getElementById('rp_tenantId').value;
            const newPwd     = document.getElementById('rp_newPassword').value;
            const confirmPwd = document.getElementById('rp_confirmPassword').value;
            const rpError    = document.getElementById('rpError');

            rpError.classList.remove('is-show');

            if (!newPwd || newPwd.length < 6) {
                rpError.textContent = 'Mật khẩu phải từ 6 ký tự trở lên.';
                rpError.classList.add('is-show');
                return;
            }
            if (newPwd !== confirmPwd) {
                rpError.textContent = 'Xác nhận mật khẩu không khớp.';
                rpError.classList.add('is-show');
                return;
            }

            // Build hidden form và submit
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${ctx}/manager/tenant/edit';

            const hiddenFields = {
                action      : 'resetPassword',
                tenantId    : tenantId,
                newPassword : newPwd,
                page        : '${currentPage}',
                keyword     : '${keyword}'
            };
            Object.entries(hiddenFields).forEach(([k, v]) => {
                const inp = document.createElement('input');
                inp.type = 'hidden'; inp.name = k; inp.value = v;
                form.appendChild(inp);
            });
            document.body.appendChild(form);
            form.submit();
        });
    </script>

</layout:layout>
