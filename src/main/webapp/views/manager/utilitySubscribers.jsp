<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

<layout:layout title="Subscribers - ${utilityName}"
               active="m_utilities"
               cssFile="${pageContext.request.contextPath}/assets/css/views/utilitySubscribers.css">

    <div class="ms-container">

        <!-- HEADER -->
        <div class="ms-header">
            <div class="ms-header-left">
                <a href="${pageContext.request.contextPath}/manager/utilities" class="ms-back-btn">
                    <i class="bi bi-arrow-left"></i> Back
                </a>
                <div class="ms-header-title">
                    <h2>Subscribers</h2>
                    <p>
                        Rooms currently using &nbsp;
                        <span class="ms-utility-badge">
                            <i class="bi bi-lightning-charge-fill"></i>
                            ${utilityName}
                        </span>
                    </p>
                </div>
            </div>
        </div>

        <!-- SEARCH -->
        <div class="ms-search-box">
            <div class="ms-search-form">
                <i class="bi bi-search"></i>
                <input type="text" id="subSearchInput"
                       placeholder="Search by room number or tenant name...">
            </div>
        </div>

        <!-- TABLE TITLE -->
        <div class="ms-card">
            <div class="ms-card-title">
                All Subscribers (<c:out value="${totalRecords}"/>)
            </div>

            <c:choose>
                <c:when test="${empty subscribers}">
                    <div class="ms-empty-state">
                        <div class="ms-empty-icon"><i class="bi bi-people"></i></div>
                        <p>No subscribers found for <strong>${utilityName}</strong>.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <table class="ms-table">
                        <thead>
                            <tr>
                                <th>Room_ID</th>
                                <th>Room_Number</th>
                                <th>Tenant Name</th>
                                <th>Usage_date</th>
                            </tr>
                        </thead>
                        <tbody id="subscriberTable">
                            <c:forEach var="s" items="${subscribers}" varStatus="loop">
                                <tr>
                                    <td>${s.utilityId}</td>
                                    <td>
                                        <span class="ms-room-badge">
                                            <i class="bi bi-door-open"></i>
                                            ${s.utilityName}
                                        </span>
                                    </td>
                                    <td>${s.unit}</td>
                                    <td>${s.status}</td> 
                                </tr>
                            </c:forEach>
                            <tr id="notFoundSub" style="display:none;">
                                <td colspan="4">No results found.</td>
                            </tr>
                        </tbody>
                    </table>

                    <c:if test="${totalPages > 1}">
                        <div class="ms-pagination">
                            <c:set var="baseUrl"
                                   value="${pageContext.request.contextPath}/manager/utilities?action=subscribers&id=${utilityId}&amp;name=${utilityName}&amp;page="/>

                            <!-- Prev -->
                            <c:choose>
                                <c:when test="${currentPage <= 1}">
                                    <span class="ms-page-btn ms-page-disabled">
                                        <i class="bi bi-chevron-left"></i>
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <a class="ms-page-btn" href="${baseUrl}${currentPage - 1}">
                                        <i class="bi bi-chevron-left"></i>
                                    </a>
                                </c:otherwise>
                            </c:choose>

                            <!-- Page numbers -->
                            <c:forEach var="i" begin="1" end="${totalPages}">
                                <c:choose>
                                    <c:when test="${i == currentPage}">
                                        <span class="ms-page-btn ms-page-active">${i}</span>
                                    </c:when>

                                    <c:when test="${i >= currentPage - 2 && i <= currentPage + 2}">
                                        <a class="ms-page-btn" href="${baseUrl}${i}">${i}</a>
                                    </c:when>

                                    <c:when test="${i == 1 || i == totalPages}">
                                        <a class="ms-page-btn" href="${baseUrl}${i}">${i}</a>
                                    </c:when>

                                    <c:when test="${i == totalPages - 1 && currentPage < totalPages - 3}">
                                        <span class="ms-page-ellipsis">...</span>
                                    </c:when>

                                    <c:when test="${i == 2 && currentPage > 4}">
                                        <span class="ms-page-ellipsis">...</span>
                                    </c:when>
                                </c:choose>
                            </c:forEach>

                            <!-- Next -->
                            <c:choose>
                                <c:when test="${currentPage >= totalPages}">
                                    <span class="ms-page-btn ms-page-disabled">
                                        <i class="bi bi-chevron-right"></i>
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <a class="ms-page-btn" href="${baseUrl}${currentPage + 1}">
                                        <i class="bi bi-chevron-right"></i>
                                    </a>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </div>

        <script>
            const subSearchInput = document.getElementById('subSearchInput');
            if (subSearchInput) {
                subSearchInput.addEventListener('input', function () {
                    const keyword = this.value.toLowerCase();
                    const rows = document.querySelectorAll('#subscriberTable tr:not(#notFoundSub)');
                    let hasResult = false;
                    rows.forEach(row => {
                        const text = row.innerText.toLowerCase();
                        if (text.includes(keyword)) {
                            row.style.display = '';
                            hasResult = true;
                        } else {
                            row.style.display = 'none';
                        }
                    });
                    const notFound = document.getElementById('notFoundSub');
                    if (notFound)
                        notFound.style.display = hasResult ? 'none' : '';
                });
            }
        </script>

    </layout:layout>