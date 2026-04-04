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
                <h2 class="update-title">Update Manager</h2>
                <p class="update-subtitle">Edit manager information with a smoother and more premium UI experience.</p>
            </div>

            <div class="update-card reveal-scale">

                <div class="card-shine"></div>

                <form method="post"
                      class="update-form"
                      id="updateManageForm"
                      action="${pageContext.request.contextPath}/admin/accounts/update">

                    <input type="hidden" name="id" value="${manager.accountId}"/>

                    <div class="form-grid">

                        <div class="form-group stagger-item">
                            <label for="fullName">Full Name</label>
                            <div class="input-shell">
                                <i class="bi bi-person-fill input-icon"></i>
                                <input type="text"
                                       id="fullName"
                                       name="fullName"
                                       value="${manager.fullName}"
                                       required
                                       placeholder="Enter full name"/>
                            </div>
                        </div>

                        <div class="form-group stagger-item">
                            <label for="phone">Phone Number</label>
                            <div class="input-shell">
                                <i class="bi bi-telephone-fill input-icon"></i>
                                <input type="text"
                                       id="phone"
                                       name="phone"
                                       value="${manager.phone}"
                                       required
                                       placeholder="Enter phone number"/>
                            </div>
                        </div>

                        <div class="form-group stagger-item">
                            <label for="email">Email</label>
                            <div class="input-shell">
                                <i class="bi bi-envelope-fill input-icon"></i>
                                <input type="email"
                                       id="email"
                                       name="email"
                                       value="${manager.email}"
                                       required
                                       placeholder="Enter email address"/>
                            </div>
                        </div>

                        <div class="form-group stagger-item">
                            <label for="gender">Gender</label>
                            <div class="input-shell">
                                <i class="bi bi-gender-ambiguous input-icon"></i>
                                <select id="gender" name="gender">
                                    <option value="1" ${manager.gender == 1 ? 'selected' : ''}>Male</option>
                                    <option value="0" ${manager.gender == 0 ? 'selected' : ''}>Female</option>
                                </select>
                            </div>
                        </div>

                        <div class="form-group stagger-item">
                            <label for="dateOfBirth">Date of Birth</label>
                            <div class="input-shell">
                                <i class="bi bi-calendar-event-fill input-icon"></i>
                                <input type="date"
                                       id="dateOfBirth"
                                       name="dateOfBirth"
                                       value="${manager.dateOfBirth}"/>
                            </div>
                        </div>

                        <div class="form-group stagger-item">
                            <label for="identityCode">Citizen ID</label>
                            <div class="input-shell">
                                <i class="bi bi-credit-card-2-front-fill input-icon"></i>
                                <input type="text"
                                       id="identityCode"
                                       name="identityCode"
                                       value="${manager.identityCode}"
                                       placeholder="Enter citizen ID"/>
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