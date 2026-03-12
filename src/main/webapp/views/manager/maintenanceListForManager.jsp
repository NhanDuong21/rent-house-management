<%-- 
    Document   : maintenanceListForManager
    Created on : Mar 8, 2026
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:layout title="Maintenance Requests"
          active="maintenance"
          cssFile="${ctx}/assets/css/views/maintenanceList.css">

    <div class="ml-container">

        <!-- HEADER -->
        <div class="ml-header">
            <div>
                <h2>Maintenance Requests</h2>
                <p>View all maintenance requests submitted by tenants</p>
            </div>
        </div>

        <!-- CARD -->
        <div class="ml-card">

            <div class="ml-card-head">
                <div class="ml-card-title">
                    All Requests (${totalRequest})
                </div>

                <!-- SEARCH -->
                <form class="ml-search-form"
                      method="get"
                      action="${ctx}/manager/maintenance">

                    <span class="ml-search-ico">
                        <i class="bi bi-search"></i>
                    </span>

                    <input type="text"
                           name="search"
                           id="mlSearch"
                           placeholder="Search by room number..."
                           value="${param.search}"
                           autocomplete="off">

                </form>
            </div>

            <!-- TABLE -->
            <div class="ml-table-wrap">
                <table class="ml-table">

                    <thead>
                        <tr>
                            <th style="width:80px">ID</th>
                            <th style="width:90px">Room</th>
                            <th>Tenant</th>
                            <th>Category</th>
                            <th>Description</th>
                            <th style="width:140px">Status</th>
                            <th style="width:120px">Action</th>
                        </tr>
                    </thead>

                    <tbody>

                        <c:forEach items="${requests}" var="r">
                            <tr>

                                <td class="ml-mono">
                                    ${r.requestId}
                                </td>

                                <td class="ml-room">
                                    ${r.roomNumber}
                                </td>

                                <td>${r.fullName}</td>

                                <td>${r.issueCategory}</td>

                                <td class="ml-desc">
                                    ${r.description}
                                </td>

                                <td>
                                    <span class="ml-badge status-${r.status}">
                                        ${r.status}
                                    </span>
                                </td>

                                <td>
                                    <a href="${ctx}/manager/maintenance?action=edit&id=${r.requestId}"
                                       class="ml-action-btn">

                                        <i class="bi bi-eye"></i>
                                        Edit

                                    </a>
                                </td>

                            </tr>
                        </c:forEach>

                        <c:if test="${empty requests}">
                            <tr>
                                <td colspan="7" class="ml-empty">
                                    No maintenance request found
                                </td>
                            </tr>
                        </c:if>

                    </tbody>
                </table>
            </div>

            <!-- PAGINATION -->
            <div class="ml-pager">

                <ul class="ml-pagination">

                    <li class="${pageIndex == 1 ? 'disabled' : ''}">
                        <a href="${ctx}/manager/maintenance?page=${pageIndex - 1}&search=${param.search}">
                            <i class="bi bi-chevron-left"></i>
                        </a>
                    </li>

                    <c:forEach begin="1" end="${totalPage}" var="i">
                        <li class="${i == pageIndex ? 'active' : ''}">
                            <a href="${ctx}/manager/maintenance?page=${i}&search=${param.search}">
                                ${i}
                            </a>
                        </li>
                    </c:forEach>

                    <li class="${pageIndex == totalPage ? 'disabled' : ''}">
                        <a href="${ctx}/manager/maintenance?page=${pageIndex + 1}&search=${param.search}">
                            <i class="bi bi-chevron-right"></i>
                        </a>
                    </li>

                </ul>

            </div>

        </div>

    </div>

    <script src="${ctx}/assets/js/pages/maintenanceList.js"></script>

</t:layout>