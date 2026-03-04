<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<t:layout title="Create Tenant" active="a_accounts">

    <style>

        .create-container{
            max-width:1000px;
            margin:40px auto;
        }

        .profile-card{
            background:#fff;
            padding:35px;
            border-radius:12px;
            box-shadow:0 6px 15px rgba(0,0,0,0.08);
        }

        .section-title{
            font-size:22px;
            font-weight:600;
            margin-bottom:25px;
        }

        .profile-form{
            width:100%;
        }

        .form-grid{
            display:grid;
            grid-template-columns:1fr 1fr;
            gap:22px;
        }

        .form-group{
            display:flex;
            flex-direction:column;
        }

        .form-group label{
            font-weight:500;
            margin-bottom:6px;
        }

        .form-group input,
        .form-group select{
            width:100%;
            padding:11px 14px;
            border:1px solid #d1d5db;
            border-radius:8px;
        }

        .form-actions{
            margin-top:30px;
            display:flex;
            justify-content:flex-end;
            gap:12px;
        }

        .btn-save{
            background:#0B5ED7;
            color:#fff;
            border:none;
            padding:11px 20px;
            border-radius:8px;
            cursor:pointer;
        }

        .btn-save:hover{
            background:#2563eb;
        }

        .btn-cancel{
            background:#e5e7eb;
            padding:11px 20px;
            border-radius:8px;
            text-decoration:none;
            color:#374151;
        }

    </style>


    <div class="create-container">

        <h2>Create New Account</h2>

        <c:if test="${not empty error}">
            <div class="ma-alert ma-alert-danger">
                ${error}
            </div>
        </c:if>

        <div class="profile-card">

            <h3 class="section-title">Create Account</h3>

            <form method="post" class="profile-form">

                <div class="form-grid">

                    <div class="form-group">
                        <label>Full Name</label>
                        <input type="text" name="fullName" required>
                    </div>

                    <div class="form-group">
                        <label>Phone Number</label>
                        <input type="text" name="phoneNumber" required>
                    </div>

                    <div class="form-group">
                        <label>Email</label>
                        <input type="email" name="email" required>
                    </div>

                    <div class="form-group">
                        <label>Role</label>
                        <select name="role">
                            <option value="TENANT">Tenant</option>
                            <option value="MANAGER">Manager</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Date of Birth</label>
                        <input type="date" name="dob" required>
                    </div>

                    <div class="form-group">
                        <label>Gender</label>
                        <select name="gender">
                            <option value="0">Male</option>
                            <option value="1">Female</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Citizen ID</label>
                        <input type="text" name="identityCode">
                    </div>

                    <div class="form-group">
                        <label>Address</label>
                        <input type="text" name="address">
                    </div>

                    <div class="form-group">
                        <label>Password</label>
                        <input type="password" name="password" required>
                    </div>

                </div>

                <div class="form-actions">

                    <a href="${pageContext.request.contextPath}/admin/accounts"
                       class="btn-cancel">
                        Cancel
                    </a>

                    <button type="submit" class="btn-save">
                        Create Account
                    </button>

                </div>

            </form>

        </div>

    </div>

</t:layout>