<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Admin Reset Password</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base/bootstrap.min.css">
    </head>

    <body class="container py-4">

        <h3 class="mb-4">Admin Reset Password</h3>

        <!-- Message -->
        <c:if test="${not empty success}">
            <div class="alert alert-success">
                ${success}
            </div>
        </c:if>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">
                ${error}
            </div>
        </c:if>

        <form method="post"
              action="${pageContext.request.contextPath}/admin/reset-password"
              class="card p-4 shadow-sm">

            <!-- Target Type -->
            <div class="mb-3">
                <label class="form-label">Loại tài khoản</label>
                <select name="targetType"
                        id="targetType"
                        class="form-select"
                        onchange="toggleTarget()">

                    <option value="TENANT">TENANT</option>
                    <option value="MANAGER">MANAGER</option>
                </select>
            </div>

            <!-- Tenant Select -->
            <div class="mb-3" id="tenantBox">
                <label class="form-label">Chọn Tenant (ACTIVE)</label>
                <select name="targetId" class="form-select">
                    <c:forEach items="${tenants}" var="t">
                        <option value="${t.tenantId}">
                            ${t.fullName} - ${t.email} - ${t.phoneNumber}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <!-- Manager Select -->
            <div class="mb-3 d-none" id="managerBox">
                <label class="form-label">Manager</label>
                <select name="targetId" class="form-select">
                    <c:choose>
                        <c:when test="${manager != null}">
                            <option value="${manager.staffId}">
                                ${manager.fullName} - ${manager.email}
                            </option>
                        </c:when>
                        <c:otherwise>
                            <option value="">
                                Không có manager ACTIVE
                            </option>
                        </c:otherwise>
                    </c:choose>
                </select>
            </div>

            <!-- New Password -->
            <div class="mb-3">
                <label class="form-label">Mật khẩu mới</label>
                <input type="password"
                       name="newPassword"
                       class="form-control"
                       minlength="6"
                       required>
            </div>

            <!-- Confirm Password -->
            <div class="mb-4">
                <label class="form-label">Xác nhận mật khẩu</label>
                <input type="password"
                       name="confirmPassword"
                       class="form-control"
                       minlength="6"
                       required>
            </div>

            <button type="submit" class="btn btn-primary">
                Reset Password
            </button>

        </form>

        <script>
            function toggleTarget() {
                const type = document.getElementById("targetType").value;
                const tenantBox = document.getElementById("tenantBox");
                const managerBox = document.getElementById("managerBox");

                if (type === "TENANT") {
                    tenantBox.classList.remove("d-none");
                    managerBox.classList.add("d-none");
                } else {
                    managerBox.classList.remove("d-none");
                    tenantBox.classList.add("d-none");
                }
            }

            // init
            toggleTarget();
        </script>

    </body>
</html>