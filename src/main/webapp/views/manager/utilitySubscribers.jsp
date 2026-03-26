<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

<layout:layout title="Subscribers - ${utilityName}"
               active="m_utilities"
               cssFile="${pageContext.request.contextPath}/assets/css/views/utilitySubscribers.css">

    <div class="ms-container">
        <div class="ms-bg-orb orb-1"></div>
        <div class="ms-bg-orb orb-2"></div>
        <div class="ms-bg-grid"></div>

        <!-- HERO HEADER -->
        <section class="ms-hero">
            <div class="ms-hero-left">
                <div class="ms-top-row">
                    <a href="${pageContext.request.contextPath}/manager/utilities" class="ms-back-btn">
                        <i class="bi bi-arrow-left"></i>
                        <span>Back</span>
                    </a>

                    <div class="ms-badge">
                        <i class="bi bi-lightning-charge-fill"></i>
                        Subscriber Management
                    </div>
                </div>

                <div class="ms-header-title">
                    <h2>Subscribers</h2>
                    <p>
                        Rooms currently using
                        <span class="ms-utility-badge">
                            <i class="bi bi-lightning-charge-fill"></i>
                            ${utilityName}
                        </span>
                    </p>
                </div>

                <div class="ms-stats-row">
                    <div class="ms-stat-card">
                        <span class="ms-stat-icon"><i class="bi bi-people-fill"></i></span>
                        <div>
                            <div class="ms-stat-value"><c:out value="${totalRecords}"/></div>
                            <div class="ms-stat-label">Total Subscribers</div>
                        </div>
                    </div>

                    <div class="ms-stat-card">
                        <span class="ms-stat-icon"><i class="bi bi-search"></i></span>
                        <div>
                            <div class="ms-stat-value">Live Search</div>
                            <div class="ms-stat-label">Filter by room or tenant</div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- SEARCH -->
        <section class="ms-toolbar glass-card">
            <div class="ms-search-box">
                <div class="ms-search-form">
                    <i class="bi bi-search"></i>
                    <input type="text"
                           id="subSearchInput"
                           placeholder="Search by room number or tenant name...">
                </div>
            </div>

            <div class="ms-toolbar-right">
                <div class="ms-chip">
                    <i class="bi bi-stars"></i>
                    Smooth animated table
                </div>
            </div>
        </section>

        <!-- MAIN CARD -->
        <section class="ms-card glass-card">
            <div class="ms-card-header">
                <div>
                    <div class="ms-card-title">All Subscribers</div>
                    <div class="ms-card-subtitle">
                        Total: <strong><c:out value="${totalRecords}"/></strong> records
                    </div>
                </div>
            </div>

            <c:choose>
                <c:when test="${empty subscribers}">
                    <div class="ms-empty-state">
                        <div class="ms-empty-icon">
                            <i class="bi bi-people"></i>
                        </div>
                        <h4>No subscribers found</h4>
                        <p>
                            There are currently no subscribers for
                            <strong>${utilityName}</strong>.
                        </p>
                    </div>
                </c:when>

                <c:otherwise>
                    <div class="ms-table-wrap">
                        <table class="ms-table">
                            <thead>
                                <tr>
                                    <th>Room ID</th>
                                    <th>Room Number</th>
                                    <th>Tenant Name</th>
                                    <th>Usage Date</th>
                                </tr>
                            </thead>

                            <tbody id="subscriberTable">
                                <c:forEach var="s" items="${subscribers}" varStatus="loop">
                                    <tr class="subscriber-row" style="--delay:${loop.index};">
                                        <td>
                                            <span class="ms-id-pill">
                                                #${s.utilityId}
                                            </span>
                                        </td>

                                        <td>
                                            <span class="ms-room-badge">
                                                <i class="bi bi-door-open"></i>
                                                ${s.utilityName}
                                            </span>
                                        </td>

                                        <td>
                                            <div class="ms-tenant-cell">
                                                <div class="ms-tenant-avatar">
                                                    <i class="bi bi-person-fill"></i>
                                                </div>
                                                <span>${s.unit}</span>
                                            </div>
                                        </td>

                                        <td>
                                            <span class="ms-date-pill">
                                                <i class="bi bi-calendar-event"></i>
                                                ${s.status}
                                            </span>
                                        </td>
                                    </tr>
                                </c:forEach>

                                <tr id="notFoundSub" style="display:none;">
                                    <td colspan="4" class="ms-not-found-cell">
                                        <div class="ms-empty-inline">
                                            <i class="bi bi-search"></i>
                                            <span>No results found.</span>
                                        </div>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

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
        </section>
    </div>

    <script src="${pageContext.request.contextPath}/assets/js/pages/utilitySubscribers.js"></script>
</layout:layout>