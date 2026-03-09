<%-- 
    Document   : maintenanceList
    Created on : Mar 8, 2026, 1:15:47 AM
    Author     : truon
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<t:layout title="Maintenance Requests"
          active="maintenance"
          cssFile="${pageContext.request.contextPath}/assets/css/views/maintenanceList.css">

    <div class="manage-maintenance container-fluid">
        <div class="page-header">
            <h2>Maintenance Requests</h2>
            <p>View all maintenance requests submitted by tenants</p>
        </div>

        <div class="maintenance-card">
            <h5>All Requests (${totalRequest})</h5>

            <form class="search-box-wrap mb-3" style="max-width:360px"
                  method="get"
                  action="${pageContext.request.contextPath}/manager/maintenance">
                <div class="input-group">
                    <span class="input-group-text">
                        <i class="bi bi-search"></i>
                    </span>
                    <input type="text"
                           name="search"
                           class="form-control"
                           placeholder="Search by room number"
                           value="${param.search}"
                           autocomplete="off">
                </div>
            </form>

            <div class="maintenance-table-wrap">
                <table class="maintenance-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Room</th>
                            <th>Tenant</th>
                            <th>Category</th>
                            <th>Description</th>
                            <th>Status</th>
                            <th style="width:130px;">Action</th>
                        </tr>
                    </thead>

                    <tbody>
                        <c:forEach items="${requests}" var="r">
                            <tr>
                                <td class="mono">${r.requestId}</td>
                                <td class="fw-bold">${r.roomNumber}</td>
                                <td>${r.fullName}</td>
                                <td>${r.issueCategory}</td>
                                <td class="desc">${r.description}</td>
                                <td>
                                    <span class="status ${r.status}">
                                        ${r.status}
                                    </span>
                                </td>
                                <td class="text-center align-middle">
                                    <a href="${pageContext.request.contextPath}/manager/maintenance?action=edit&id=${r.requestId}"
                                       class="action-btn">
                                        <i class="bi bi-eye"></i>
                                        Edit
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty requests}">
                            <tr>
                                <td colspan="7" class="empty">
                                    No maintenance request found
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <div class="pagination-wrapper">
                <ul class="pagination">
                    <li class="${pageIndex == 1 ? 'disabled' : ''}">
                        <a href="${pageContext.request.contextPath}/manager/maintenance?page=${pageIndex - 1}&search=${param.search}">
                            <i class="bi bi-chevron-left"></i>
                        </a>
                    </li>

                    <c:forEach begin="1" end="${totalPage}" var="i">
                        <li class="${i == pageIndex ? 'active' : ''}">
                            <a href="${pageContext.request.contextPath}/manager/maintenance?page=${i}&search=${param.search}">
                                ${i}
                            </a>
                        </li>
                    </c:forEach>

                    <li class="${pageIndex == totalPage ? 'disabled' : ''}">
                        <a href="${pageContext.request.contextPath}/manager/maintenance?page=${pageIndex + 1}&search=${param.search}">
                            <i class="bi bi-chevron-right"></i>
                        </a>
                    </li>
                </ul>
            </div>

        </div>
    </div>

</t:layout>