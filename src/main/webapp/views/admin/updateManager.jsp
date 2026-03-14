<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<t:layout>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/views/admin/updateManage.css"/>

    <div class="update-wrapper">

        <div class="update-card">

            <h2 class="update-title">Update Manager</h2>

            <form method="post"
                  action="${pageContext.request.contextPath}/admin/accounts/update">

                <input type="hidden" name="id" value="${manager.accountId}"/>

                <div class="form-grid">

                    <div class="form-group">
                        <label>Full Name</label>
                        <input type="text" name="fullName" value="${manager.fullName}" required/>
                    </div>

                    <div class="form-group">
                        <label>Phone Number</label>
                        <input type="text" value="${manager.phone}" readonly/>
                    </div>

                    <div class="form-group">
                        <label>Email</label>
                        <input type="email" name="email" value="${manager.email}" required/>
                    </div>

                    <div class="form-group">
                        <label>Gender</label>
                        <input type="text"
                               value="${manager.gender == 1 ? 'Male' : 'Female'}"
                               readonly/>
                    </div>

                    <div class="form-group">
                        <label>Date of Birth</label>
                        <input type="text" value="${manager.dateOfBirth}" readonly/>
                    </div>

                    <div class="form-group">
                        <label>Citizen ID</label>
                        <input type="text" value="${manager.identityCode}" readonly/>
                    </div>

                    <div class="form-group full">
                        <label>Status</label>
                        <input type="text" value="${manager.status}" readonly/>
                    </div>

                </div>

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