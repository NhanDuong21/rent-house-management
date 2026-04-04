<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<t:layout>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/views/admin/updateManage.css"/>

    <div class="update-wrapper">

        <div class="bg-orb orb-1"></div>
        <div class="bg-orb orb-2"></div>
        <div class="bg-grid"></div>

        <div class="update-shell">

            <div class="update-head reveal-up">
                <span class="page-badge">Admin Panel</span>
                <h2 class="update-title">Update Tenant</h2>
                <p class="update-subtitle">Edit tenant information with a smoother and more premium UI experience.</p>
            </div>

            <div class="update-card reveal-scale">

                <div class="card-shine"></div>

                <form method="post"
                      class="update-form"
                      id="updateManageForm"
                      action="${pageContext.request.contextPath}/admin/accounts/update-tenant">

                    <!-- ID -->
                    <input type="hidden" name="id" value="${tenant.tenantId}"/>

                    <div class="form-grid">

                        <!-- FULL NAME -->
                        <div class="form-group stagger-item">
                            <label for="fullName">Full Name</label>
                            <div class="input-shell">
                                <i class="bi bi-person-fill input-icon"></i>
                                <input type="text"
                                       id="fullName"
                                       name="fullName"
                                       value="${tenant.fullName}"
                                       required
                                       placeholder="Enter full name">
                            </div>
                        </div>

                        <!-- PHONE -->
                        <div class="form-group stagger-item">
                            <label for="phone">Phone Number</label>
                            <div class="input-shell">
                                <i class="bi bi-telephone-fill input-icon"></i>
                                <input type="text"
                                       id="phone"
                                       name="phone"
                                       value="${tenant.phoneNumber}"
                                       required
                                       placeholder="Enter phone number">
                            </div>
                        </div>

                        <!-- EMAIL -->
                        <div class="form-group stagger-item">
                            <label for="email">Email</label>
                            <div class="input-shell">
                                <i class="bi bi-envelope-fill input-icon"></i>
                                <input type="email"
                                       id="email"
                                       name="email"
                                       value="${tenant.email}"
                                       required
                                       placeholder="Enter email address">
                            </div>
                        </div>

                        <!-- GENDER -->
                        <div class="form-group stagger-item">
                            <label for="gender">Gender</label>
                            <div class="input-shell">
                                <i class="bi bi-gender-ambiguous input-icon"></i>
                                <select id="gender" name="gender">
                                    <option value="1"
                                            <c:if test="${tenant.gender == 1}">selected</c:if>>
                                        Male
                                    </option>

                                    <option value="0"
                                            <c:if test="${tenant.gender == 0}">selected</c:if>>
                                        Female
                                    </option>
                                </select>
                            </div>
                        </div>

                        <!-- DATE OF BIRTH -->
                        <div class="form-group stagger-item">
                            <label for="dateOfBirth">Date of Birth</label>
                            <div class="input-shell">
                                <i class="bi bi-calendar-event-fill input-icon"></i>
                                <input type="date"
                                       id="dateOfBirth"
                                       name="dateOfBirth"
                                       value="${tenant.dateOfBirth}">
                            </div>
                        </div>

                        <!-- CITIZEN ID -->
                        <div class="form-group stagger-item">
                            <label for="identityCode">Citizen ID</label>
                            <div class="input-shell">
                                <i class="bi bi-credit-card-2-front-fill input-icon"></i>
                                <input type="text"
                                       id="identityCode"
                                       name="identityCode"
                                       value="${tenant.identityCode}"
                                       placeholder="Enter citizen ID">
                            </div>
                        </div>

                        <!-- ADDRESS -->
                        <div class="form-group full stagger-item">
                            <label for="address">Address</label>
                            <div class="input-shell">
                                <i class="bi bi-geo-alt-fill input-icon"></i>
                                <input type="text"
                                       id="address"
                                       name="address"
                                       value="${tenant.address}"
                                       placeholder="Enter address">
                            </div>
                        </div>

                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn-save magnetic-btn" id="submitBtn">
                            <i class="bi bi-check-circle-fill"></i>
                            <span>Save Changes</span>
                        </button>

                        <a href="${pageContext.request.contextPath}/admin/accounts"
                           class="btn-cancel magnetic-btn">
                            <i class="bi bi-arrow-left"></i>
                            <span>Cancel</span>
                        </a>
                    </div>

                </form>

            </div>

        </div>

    </div>

    <script src="${pageContext.request.contextPath}/assets/js/pages/admin/updateManage.js"></script>

</t:layout>