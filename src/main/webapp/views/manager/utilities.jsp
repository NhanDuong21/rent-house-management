<%-- 
    Document   : utilities
    Created on : Feb 25, 2026, 2:44:36 PM
    Author     : DELL
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<layout:layout title="Manage Utilities"
               active="m_utilities"
               cssFile="${pageContext.request.contextPath}/assets/css/views/managerUtilities.css">
    <div class="mb-container">

        <!-- Header -->
        <div class="mb-header">
            <div>
                <h2>Manage Utilities</h2>
                <p>Manage utility services and view subscribers</p>
            </div>

            <button onclick="document.getElementById('addModal').style.display = 'flex'"
                    class="mb-generate-btn">
                + Add Utility
            </button>
            <div id="addModal" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%;
                 background:rgba(0,0,0,0.5); z-index:9999; justify-content:center; align-items:center;">
                <div style="background:white; padding:2rem; border-radius:16px; width:480px;">
                    <div style="display:flex; justify-content:space-between; align-items:start; margin-bottom:1.5rem;">
                        <div>
                            <h4 style="margin:0; font-weight:700;">Add New Utility</h4>
                            <small style="color:gray;">Create a new utility service for your property</small>
                        </div>
                        <button onclick="document.getElementById('addModal').style.display = 'none'"
                                style="background:none; border:none; font-size:1.3rem; cursor:pointer; color:#666;">×</button>
                    </div>
                    <form action="${pageContext.request.contextPath}/manager/utilities" method="POST">
                        <input type="hidden" name="action" value="add"/>
                        <div style="margin-bottom:1rem;">
                            <label style="display:block; font-weight:500; margin-bottom:6px;">Utility Name</label>
                            <input type="text" name="utilityName" placeholder="e.g., Parking Service"
                                   style="width:100%; padding:10px 14px; border:none; background:#f3f4f6; border-radius:8px; box-sizing:border-box; font-size:14px;"/>
                        </div>
                        <div style="margin-bottom:1rem;">
                            <label style="display:block; font-weight:500; margin-bottom:6px;">Price (VND)</label>
                            <input type="number" name="price" value="0" min="0"
                                   style="width:100%; padding:10px 14px; border:none; background:#f3f4f6; border-radius:8px; box-sizing:border-box; font-size:14px;"/>
                        </div>
                        <div style="margin-bottom:2rem;">
                            <label style="display:block; font-weight:500; margin-bottom:6px;">Unit</label>
                            <input type="text" name="unit" placeholder="month"
                                   style="width:100%; padding:10px 14px; border:none; background:#f3f4f6; border-radius:8px; box-sizing:border-box; font-size:14px;"/>
                        </div>
                        <div style="display:flex; justify-content:flex-end; gap:10px;">
                            <button type="button"
                                    onclick="document.getElementById('addModal').style.display = 'none'"
                                    style="padding:10px 20px; border:1px solid #ddd; border-radius:8px; cursor:pointer; background:white; font-size:14px;">
                                ✕ Cancel
                            </button>
                            <button type="submit"
                                    style="padding:10px 20px; background:#22c55e; color:white; border:none; border-radius:8px; cursor:pointer; font-size:14px; font-weight:600;">
                                + Add Utility
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>


        <!-- SEARCH -->
        <div class="mb-search-box">
            <div class="mb-search-form">
                <i class="bi bi-search"></i>
                <input type="text" id="searchInput"
                       placeholder="Search by utility name...">
            </div>
        </div>

        <!-- TABLE CARD -->
        <div class="mb-card-title">
            All Utilities (<c:out value="${empty utilities ? 0 : utilities.size()}"/>)
        </div>

        <table class="mb-table">
            <thead>
                <tr>
                    <th>Utility Name</th>
                    <th>Price</th>
                    <th>View Subscribers</th>
                    <th>Action</th>
                </tr>
            </thead>

            <tbody id="utilityTable">
                <c:forEach var="u" items="${utilities}">
                    <tr>
                        <td class="utilityName">${u.utilityName}</td>                        <td>
                            <fmt:formatNumber value="${u.standardPrice}"
                                              type="number" groupingUsed="true"/> đ/${u.unit}
                        </td>

                        <td style="white-space: nowrap;">
                            <a href="${pageContext.request.contextPath}/manager/utilities?action=subscribers&id=${u.utilityId}&name=${u.utilityName}"
                               class="mb-view-btn">
                                👁 View
                            </a>
                        </td>

                        <td class="action-buttons">
                            <a href="${pageContext.request.contextPath}/manager/utilities?action=edit&id=${u.utilityId}"
                               class="mb-edit-btn">
                                <i class="bi bi-pencil-square"></i>
                                Edit
                            </a>
                            <div id="editModal" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%;
                                 background:rgba(0,0,0,0.5); z-index:9999; justify-content:center; align-items:center;">
                                <div style="background:white; padding:2rem; border-radius:16px; width:480px;">
                                    <div style="display:flex; justify-content:space-between; align-items:start; margin-bottom:1.5rem;">
                                        <div>
                                            <h4 style="margin:0; font-weight:700;">Edit Utility - ${editUtility.utilityName}</h4>
                                            <small style="color:gray;">Update the price for this utility service</small>
                                        </div>
                                        <button onclick="document.getElementById('editModal').style.display = 'none'"
                                                style="background:none; border:none; font-size:1.3rem; cursor:pointer;">×</button>
                                    </div>
                                    <form action="${pageContext.request.contextPath}/manager/utilities" method="POST">
                                        <input type="hidden" name="action" value="edit"/>
                                        <input type="hidden" name="id" value="${editUtility.utilityId}"/>
                                        <div style="margin-bottom:1.5rem;">
                                            <label style="display:block; font-weight:500; margin-bottom:6px;">Price (VND)</label>
                                            <input type="number" name="price" value="${editUtility.standardPrice}" min="0"
                                                   style="width:100%; padding:10px 14px; border:1px solid #ddd; border-radius:8px; box-sizing:border-box;"/>
                                        </div>
                                        <div style="display:flex; justify-content:flex-end; gap:10px;">
                                            <button type="button"
                                                    onclick="document.getElementById('editModal').style.display = 'none'"
                                                    style="padding:10px 20px; border:1px solid #ddd; border-radius:8px; cursor:pointer; background:white;">
                                                ✕ Cancel
                                            </button>
                                            <button type="submit"
                                                    style="padding:10px 20px; background:#22c55e; color:white; border:none; border-radius:8px; cursor:pointer; font-weight:600;">
                                                ✓ Save Changes
                                            </button>
                                        </div>
                                    </form>
                                </div>
                            </div>

                            <%-- tự mở modal nếu có editUtility --%>
                            <c:if test="${editUtility != null}">
                                <script>
                                    document.getElementById('editModal').style.display = 'flex';
                                </script>
                            </c:if>

                            <a href="${pageContext.request.contextPath}/manager/utilities?action=delete&id=${u.utilityId}"
                               class="mb-delete-btn"
                               onclick="return confirm('Are you sure you want to delete this utility?')">
                                <i class="bi bi-trash-fill"></i>
                                Delete
                            </a>
                        </td>

                    </tr>
                </c:forEach>
                <tr id="notFoundUtility" style="display:none;">
                    <td colspan="4" class="mb-empty">No utilities found.</td>
                </tr>
            </tbody>
        </table>
        <!-- THÔNG BÁO -->
        <c:if test="${successMsg != null}">
            <div class="mb-alert mb-alert-success">
                ✅ ${successMsg}
                <button class="mb-alert-close" onclick="this.parentElement.remove()">×</button>
            </div>
        </c:if>
        <c:if test="${errorMsg != null}">
            <div class="mb-alert mb-alert-error">
                ❌ ${errorMsg}
                <button class="mb-alert-close" onclick="this.parentElement.remove()">×</button>
            </div>
        </c:if>
    </div>
</div> <!-- đóng mb-container -->

<!-- Modal Subscribers -->
<c:if test="${subscribers != null}">
    <script>
        window.onload = function () {
            document.getElementById('subscribersModal').style.display = 'flex';
        }
    </script>
</c:if>

<div id="subscribersModal" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%;
     background:rgba(0,0,0,0.5); z-index:9999; justify-content:center; align-items:center;">
    <div style="background:white; padding:2rem; border-radius:16px; width:600px; max-height:80vh; overflow-y:auto;">
        <div style="display:flex; justify-content:space-between; align-items:start; margin-bottom:1.5rem;">
            <div>
                <h4 style="margin:0; font-weight:700;">Subscribers - ${utilityName}</h4>
                <small style="color:gray;">List of tenants subscribed to this utility service</small>
            </div>
            <a href="${pageContext.request.contextPath}/manager/utilities"
               style="background:none; border:1px solid #ddd; border-radius:8px; width:30px; height:30px;
               cursor:pointer; font-size:16px; display:flex; align-items:center; justify-content:center;
               text-decoration:none; color:#333;">×</a>
        </div>
        <table style="width:100%; border-collapse:collapse;">
            <thead>
                <tr>
                    <th style="text-align:left; padding:10px; border-bottom:2px solid #e5e7eb; font-weight:600;">Room ID</th>
                    <th style="text-align:left; padding:10px; border-bottom:2px solid #e5e7eb; font-weight:600;">Room Number</th>
                    <th style="text-align:left; padding:10px; border-bottom:2px solid #e5e7eb; font-weight:600;">Tenant Name</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="s" items="${subscribers}">
                    <tr>
                        <td style="padding:12px 10px; border-bottom:1px solid #e5e7eb;">${s.utilityId}</td>
                        <td style="padding:12px 10px; border-bottom:1px solid #e5e7eb;">${s.utilityName}</td>
                        <td style="padding:12px 10px; border-bottom:1px solid #e5e7eb;">${s.unit}</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty subscribers}">
                    <tr>
                        <td colspan="3" style="text-align:center; padding:20px; color:#9ca3af;">No subscribers found.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/pages/managerUtilities.js"></script>
</layout:layout>