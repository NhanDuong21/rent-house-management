<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:layout 
    title="Create Account"
    active="a_accounts"
    cssFile="${ctx}/assets/css/views/admin/create-account.css">

    <div class="create-container">
        
        <!-- DECOR BACKGROUND -->
        <div class="bg-orb orb-1"></div>
        <div class="bg-orb orb-2"></div>
        <div class="bg-grid"></div>

        <div class="ca-header reveal-up">
            <span class="ca-badge">Admin Panel</span>
            <h2>Create New Account</h2>
            <p>Fill in the information to create a tenant or manager account.</p>
        </div>

        <!-- ERROR ALERT -->
        <c:if test="${not empty error}">
            <div class="ma-alert ma-alert-danger error-pop">
                <i class="bi bi-x-circle-fill"></i>
                <span>${error}</span>
            </div>
        </c:if>

        <div class="profile-card glass-card reveal-scale">

            <div class="card-shine"></div>

            <form method="post" class="profile-form" id="createAccountForm">

                <div class="form-grid">

                    <!-- FULL NAME -->
                    <div class="form-group stagger-item">
                        <label for="fullName">Full Name</label>
                        <div class="input-shell">
                            <i class="bi bi-person-fill input-icon"></i>
                            <input type="text" id="fullName" name="fullName" required placeholder="Enter full name">
                        </div>
                    </div>

                    <!-- PHONE -->
                    <div class="form-group stagger-item">
                        <label for="phoneNumber">Phone Number</label>
                        <div class="input-shell">
                            <i class="bi bi-telephone-fill input-icon"></i>
                            <input type="text" id="phoneNumber" name="phoneNumber" required placeholder="Enter phone number">
                        </div>
                    </div>

                    <!-- EMAIL -->
                    <div class="form-group stagger-item">
                        <label for="email">Email</label>
                        <div class="input-shell">
                            <i class="bi bi-envelope-fill input-icon"></i>
                            <input type="email" id="email" name="email" required placeholder="Enter email address">
                        </div>
                    </div>

                    <!-- ROLE -->
                    <div class="form-group stagger-item">
                        <label for="role">Role</label>
                        <div class="input-shell">
                            <i class="bi bi-person-badge-fill input-icon"></i>
                            <select id="role" name="role">
                                <option value="TENANT">Tenant</option>
                                <option value="MANAGER">Manager</option>
                            </select>
                        </div>
                    </div>

                    <!-- DATE OF BIRTH -->
                    <div class="form-group stagger-item">
                        <label for="dob">Date of Birth</label>
                        <div class="input-shell">
                            <i class="bi bi-calendar-event-fill input-icon"></i>
                            <input type="date" id="dob" name="dob" required>
                        </div>
                    </div>

                    <!-- GENDER -->
                    <div class="form-group stagger-item">
                        <label for="gender">Gender</label>
                        <div class="input-shell">
                            <i class="bi bi-gender-ambiguous input-icon"></i>
                            <select id="gender" name="gender">
                                <option value="0">Male</option>
                                <option value="1">Female</option>
                            </select>
                        </div>
                    </div>

                    <!-- IDENTITY -->
                    <div class="form-group stagger-item">
                        <label for="identityCode">Citizen ID</label>
                        <div class="input-shell">
                            <i class="bi bi-credit-card-2-front-fill input-icon"></i>
                            <input type="text" id="identityCode" name="identityCode" placeholder="Enter citizen ID">
                        </div>
                    </div>

                    <!-- ADDRESS -->
                    <div class="form-group stagger-item">
                        <label for="address">Address</label>
                        <div class="input-shell">
                            <i class="bi bi-geo-alt-fill input-icon"></i>
                            <input type="text" id="address" name="address" placeholder="Enter address">
                        </div>
                    </div>

                    <!-- PASSWORD -->
                    <div class="form-group stagger-item full-span">
                        <label for="password">Password</label>
                        <div class="input-shell">
                            <i class="bi bi-shield-lock-fill input-icon"></i>
                            <input type="password" id="password" name="password" required placeholder="Enter password">
                            <button type="button" class="toggle-password" id="togglePassword" tabindex="-1">
                                <i class="bi bi-eye-fill"></i>
                            </button>
                        </div>
                        <small class="input-hint">Password must be at least 6 characters.</small>
                        <div class="password-strength" id="passwordStrength">
                            <span></span>
                        </div>
                    </div>

                </div>

                <!-- BUTTONS -->
                <div class="form-actions">
                    <a href="${ctx}/admin/accounts" class="btn-cancel magnetic-btn">
                        <i class="bi bi-arrow-left"></i>
                        <span>Cancel</span>
                    </a>

                    <button type="submit" class="btn-save magnetic-btn" id="submitBtn">
                        <i class="bi bi-person-plus-fill"></i>
                        <span>Create Account</span>
                    </button>
                </div>

            </form>

        </div>

    </div>

    <!-- JS -->
    <script src="${ctx}/assets/js/pages/admin/create-account.js"></script>

</t:layout>