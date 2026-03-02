<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:layout title="Room Administration"
          active="a_rooms"
          cssFile="${ctx}/assets/css/views/admin/rooms.css">

    <div class="ar-container">

        <!-- HEADER -->
        <div class="ar-header">
            <div>
                <h2>Room Administration</h2>
                <p>Manage all rooms in the system</p>
            </div>

            <a class="ar-primary-btn" href="${ctx}/admin/rooms/create">
                <span class="ar-btn-ico"><i class="bi bi-plus-lg"></i></span>
                Create Room
            </a>
        </div>

        <!-- ALERTS (server redirect msg/err) -->
        <c:if test="${not empty param.msg || not empty param.err}">
            <div class="ar-alert-wrap" id="arAlertWrap">
                <c:choose>
                    <c:when test="${param.msg == 'deleted'}">
                        <div class="ar-alert success">
                            <i class="bi bi-check-circle-fill"></i>
                            Room has been set to <b>INACTIVE</b> successfully.
                        </div>
                    </c:when>

                    <c:when test="${param.err == 'room_in_use'}">
                        <div class="ar-alert danger">
                            <i class="bi bi-exclamation-triangle-fill"></i>
                            Cannot set this room to INACTIVE because it has an <b>ACTIVE/PENDING</b> contract.
                        </div>
                    </c:when>

                    <c:when test="${param.err == 'delete_fail'}">
                        <div class="ar-alert danger">
                            <i class="bi bi-exclamation-triangle-fill"></i>
                            Action failed. Please try again.
                        </div>
                    </c:when>

                    <c:when test="${param.err == 'invalid_room'}">
                        <div class="ar-alert danger">
                            <i class="bi bi-exclamation-triangle-fill"></i>
                            Invalid room id.
                        </div>
                    </c:when>

                    <c:otherwise>
                        <div class="ar-alert info">
                            <i class="bi bi-info-circle-fill"></i>
                            Action completed.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>

        <!-- SEARCH -->
        <div class="ar-search-box">
            <i class="bi bi-search"></i>
            <input id="arSearchInput"
                   type="text"
                   placeholder="Search by room number, block, status..."
                   autocomplete="off">
            <button type="button" class="ar-clear" id="arClearBtn" title="Clear">
                <i class="bi bi-x-lg"></i>
            </button>
        </div>

        <!-- STATUS TABS -->
        <div class="ar-tabs" id="arTabs">
            <button type="button" class="ar-tab active" data-filter="ALL">
                <i class="bi bi-grid-3x3-gap-fill"></i> All
            </button>
            <button type="button" class="ar-tab" data-filter="AVAILABLE">
                <i class="bi bi-check-circle-fill"></i> Available
            </button>
            <button type="button" class="ar-tab" data-filter="OCCUPIED">
                <i class="bi bi-people-fill"></i> Occupied
            </button>
            <button type="button" class="ar-tab" data-filter="MAINTENANCE">
                <i class="bi bi-tools"></i> Maintenance
            </button>
            <button type="button" class="ar-tab" data-filter="INACTIVE">
                <i class="bi bi-slash-circle-fill"></i> Inactive
            </button>
        </div>

        <!-- CARD -->
        <div class="ar-card">
            <div class="ar-card-title">
                All Rooms (<c:out value="${total}"/>)
            </div>

            <div class="ar-table-wrap">
                <table class="ar-table" id="arTable">
                    <thead>
                        <tr>
                            <th style="width:90px;">Room ID</th>
                            <th>Room Number</th>
                            <th>Block</th>
                            <th style="width:90px;">Floor</th>
                            <th style="width:110px;">Area (m²)</th>
                            <th style="width:160px;">Price</th>
                            <th style="width:160px;">Status</th>
                            <th style="width:280px;">Actions</th>
                        </tr>
                    </thead>

                    <tbody>
                        <c:forEach items="${rooms}" var="r">
                            <tr class="ar-row"
                                data-room="${r.roomNumber}"
                                data-block="${r.blockName}"
                                data-status="${r.status}">
                                <td class="ar-mono">${r.roomId}</td>

                                <td class="ar-strong">
                                    ${r.roomNumber}
                                </td>

                                <td>${r.blockName}</td>
                                <td><c:out value="${r.floor}"/></td>
                                <td><c:out value="${r.area}"/></td>

                                <td class="ar-price">
                                    <fmt:formatNumber value="${r.price}" type="number" groupingUsed="true"/> đ
                                </td>

                                <td>
                                    <c:choose>
                                        <c:when test="${r.status == 'AVAILABLE'}">
                                            <span class="ar-badge status-available">
                                                <i class="bi bi-check-circle-fill"></i> AVAILABLE
                                            </span>
                                        </c:when>
                                        <c:when test="${r.status == 'OCCUPIED'}">
                                            <span class="ar-badge status-occupied">
                                                <i class="bi bi-people-fill"></i> OCCUPIED
                                            </span>
                                        </c:when>
                                        <c:when test="${r.status == 'MAINTENANCE'}">
                                            <span class="ar-badge status-maintenance">
                                                <i class="bi bi-tools"></i> MAINTENANCE
                                            </span>
                                        </c:when>
                                        <c:when test="${r.status == 'INACTIVE'}">
                                            <span class="ar-badge status-inactive">
                                                <i class="bi bi-slash-circle-fill"></i> INACTIVE
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="ar-badge status-other">
                                                <i class="bi bi-info-circle-fill"></i> ${r.status}
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td>
                                    <div class="ar-actions">

                                        <a class="ar-action-btn"
                                           href="${ctx}/admin/rooms/edit?id=${r.roomId}">
                                            <span class="ar-action-ico"><i class="bi bi-pencil-square"></i></span>
                                            Edit
                                        </a>

                                        <!-- AVAILABLE / MAINTENANCE: allow set inactive -->
                                        <c:if test="${r.status == 'AVAILABLE' || r.status == 'MAINTENANCE'}">
                                            <button type="button"
                                                    class="ar-action-btn danger js-delete-room"
                                                    data-delete-url="${ctx}/admin/rooms/delete?id=${r.roomId}"
                                                    data-room-name="${r.roomNumber}">
                                                <span class="ar-action-ico"><i class="bi bi-trash3-fill"></i></span>
                                                Set INACTIVE
                                            </button>
                                        </c:if>

                                        <!-- OCCUPIED: show disabled + tooltip -->
                                        <c:if test="${r.status == 'OCCUPIED'}">
                                            <div class="ar-tooltip"
                                                 data-tip="Room has active contract. Cannot set INACTIVE.">
                                                <button type="button"
                                                        class="ar-action-btn danger is-disabled"
                                                        disabled>
                                                    <span class="ar-action-ico"><i class="bi bi-lock-fill"></i></span>
                                                    Set INACTIVE
                                                </button>
                                            </div>
                                        </c:if>

                                        <!-- INACTIVE: do not show button -->
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty rooms}">
                            <tr>
                                <td colspan="8" class="ar-empty">
                                    No rooms found.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <!-- Pagination -->
            <c:if test="${totalPages > 1}">
                <div class="ar-pager">

                    <!-- Prev -->
                    <c:choose>
                        <c:when test="${page <= 1}">
                            <span class="ar-page-btn disabled" aria-disabled="true">
                                <i class="bi bi-chevron-left"></i>
                            </span>
                        </c:when>
                        <c:otherwise>
                            <a class="ar-page-btn"
                               href="${ctx}/admin/rooms?page=${page - 1}">
                                <i class="bi bi-chevron-left"></i>
                            </a>
                        </c:otherwise>
                    </c:choose>

                    <!-- Always show page 1 -->
                    <a class="ar-page-btn ${page == 1 ? 'active' : ''}"
                       href="${ctx}/admin/rooms?page=1">1</a>

                    <!-- Left ellipsis -->
                    <c:if test="${page > 4}">
                        <span class="ar-page-ellipsis">...</span>
                    </c:if>

                    <!-- Middle window: currentPage ± 1 (bounded) -->
                    <c:set var="start" value="${page - 1}" />
                    <c:set var="end" value="${page + 1}" />

                    <c:if test="${start < 2}">
                        <c:set var="start" value="2" />
                    </c:if>

                    <c:if test="${end > totalPages - 1}">
                        <c:set var="end" value="${totalPages - 1}" />
                    </c:if>

                    <c:forEach begin="${start}" end="${end}" var="i">
                        <c:if test="${i > 1 && i < totalPages}">
                            <a class="ar-page-btn ${i == page ? 'active' : ''}"
                               href="${ctx}/admin/rooms?page=${i}">
                                ${i}
                            </a>
                        </c:if>
                    </c:forEach>

                    <!-- Right ellipsis -->
                    <c:if test="${page < totalPages - 3}">
                        <span class="ar-page-ellipsis">...</span>
                    </c:if>

                    <!-- Always show last page (if > 1) -->
                    <c:if test="${totalPages > 1}">
                        <a class="ar-page-btn ${page == totalPages ? 'active' : ''}"
                           href="${ctx}/admin/rooms?page=${totalPages}">
                            ${totalPages}
                        </a>
                    </c:if>

                    <!-- Next -->
                    <c:choose>
                        <c:when test="${page >= totalPages}">
                            <span class="ar-page-btn disabled" aria-disabled="true">
                                <i class="bi bi-chevron-right"></i>
                            </span>
                        </c:when>
                        <c:otherwise>
                            <a class="ar-page-btn"
                               href="${ctx}/admin/rooms?page=${page + 1}">
                                <i class="bi bi-chevron-right"></i>
                            </a>
                        </c:otherwise>
                    </c:choose>

                </div>
            </c:if>

        </div>
    </div>

    <!-- DELETE CONFIRM MODAL -->
    <div class="ar-modal" id="arDeleteModal" aria-hidden="true">
        <div class="ar-modal-backdrop" data-close="1"></div>

        <div class="ar-modal-box" role="dialog" aria-modal="true">
            <button type="button" class="ar-modal-close" data-close="1" title="Close">
                <i class="bi bi-x-lg"></i>
            </button>

            <div class="ar-modal-ico">
                <i class="bi bi-exclamation-triangle-fill"></i>
            </div>

            <div class="ar-modal-title">Confirm action</div>
            <div class="ar-modal-sub" id="arDeleteText">
                Are you sure you want to set this room to INACTIVE?
            </div>

            <div class="ar-modal-actions">
                <button type="button" class="ar-modal-btn outline" data-close="1">
                    <i class="bi bi-x"></i> Cancel
                </button>

                <a href="#" class="ar-modal-btn danger" id="arDeleteGo">
                    <i class="bi bi-trash3-fill"></i> Set INACTIVE
                </a>
            </div>
        </div>
    </div>

    <script src="${ctx}/assets/js/pages/admin/rooms.js"></script>

</t:layout>