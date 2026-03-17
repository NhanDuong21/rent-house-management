<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<t:layout>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/views/admin/updateManage.css"/>

    <div class="update-wrapper">

        <div class="update-card">

            <h2 class="update-title">Update Tenant</h2>

            <form method="post"
                  action="${pageContext.request.contextPath}/admin/accounts/update-tenant">

                <!-- ID -->
                <input type="hidden" name="id" value="${tenant.tenantId}"/>

                <div class="form-grid">

                    <!-- FULL NAME -->
                    <div class="form-group">
                        <label>Full Name</label>
                        <input type="text"
                               name="fullName"
                               value="${tenant.fullName}"
                               required>
                    </div>

                    <!-- PHONE -->
                    <div class="form-group">
                        <label>Phone Number</label>
                        <input type="text"
                               name="phone"
                               value="${tenant.phoneNumber}"
                               required>
                    </div>

                    <!-- EMAIL -->
                    <div class="form-group">
                        <label>Email</label>
                        <input type="email"
                               name="email"
                               value="${tenant.email}"
                               required>
                    </div>

                    <!-- GENDER -->
                    <div class="form-group">
                        <label>Gender</label>
                        <select name="gender">

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

                        <!-- DATE OF BIRTH -->
                        <div class="form-group">
                            <label>Date of Birth</label>
                            <input type="date"
                                   name="dateOfBirth"
                                   value="${tenant.dateOfBirth}">
                    </div>

                    <!-- CITIZEN ID -->
                    <div class="form-group">
                        <label>Citizen ID</label>
                        <input type="text"
                               name="identityCode"
                               value="${tenant.identityCode}">
                    </div>

                    <!-- ADDRESS -->
                    <div class="form-group full">
                        <label>Address</label>
                        <input type="text"
                               name="address"
                               value="${tenant.address}">
                    </div>

                    <!-- STATUS -->
                    <div class="form-group full">
                        <label>Status</label>

                        <select name="status">

                            <option value="ACTIVE"
                                    <c:if test="${tenant.accountStatus == 'ACTIVE'}">selected</c:if>>
                                        Active
                                    </option>

                                    <option value="LOCKED"
                                    <c:if test="${tenant.accountStatus == 'LOCKED'}">selected</c:if>>
                                        Locked
                                    </option>

                            </select>

                        </div>

                    </div>

                    <!-- BUTTON -->
                    <div class="form-actions">

                        <button type="submit" class="btn-save">
                            Save
                        </button>

                        <a href="${pageContext.request.contextPath}/admin/accounts"
                       class="btn-cancel">
                        Cancel
                    </a>

                </div>

            </form>

        </div>

    </div>

</t:layout>