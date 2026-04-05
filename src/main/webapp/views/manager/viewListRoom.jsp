<%-- 
    Document   : viewListRoom
    Created on : Mar 5, 2026
    Author     : truon
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:layout title="Room List"
          active="room"
          cssFile="${ctx}/assets/css/views/manageRooms.css">

    <div class="manage-room container-fluid">

        <!-- FLOATING BG -->
        <div class="room-bg room-bg-1"></div>
        <div class="room-bg room-bg-2"></div>
        <div class="room-bg room-bg-3"></div>

        <!-- HEADER -->
        <div class="page-header page-reveal">
            <div>
                <h2>Room List</h2>
                <p>View all rooms in the system</p>
            </div>
        </div>

        <!-- CARD -->
        <div class="room-card page-reveal">

            <div class="room-card-head">
                <h5>All Rooms (${totalRoom})</h5>

                <form class="room-search-form"
                      method="get"
                      action="${ctx}/manager/rooms">

                    <div class="room-search-box">
                        <span class="room-search-ico">
                            <i class="bi bi-search"></i>
                        </span>

                        <input class="searchRoom"
                               type="text"
                               name="search"
                               id="roomSearch"
                               placeholder="Search by room number..."
                               value="${param.search}"
                               autocomplete="off">
                    </div>

                    <select name="status" id="roomStatus" class="form-select">
                        <option value="">All Status</option>

                        <option value="AVAILABLE"
                                ${param.status == 'AVAILABLE' ? 'selected' : ''}>
                            AVAILABLE
                        </option>

                        <option value="MAINTENANCE"
                                ${param.status == 'MAINTENANCE' ? 'selected' : ''}>
                            MAINTENANCE
                        </option>
                    </select>

                    <button type="submit" class="room-action-btn">
                        <i class="bi bi-funnel"></i>
                        Filter
                    </button>
                </form>
            </div>

            <div class="room-table-wrap">
                <table class="room-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Block</th>
                            <th>Room</th>
                            <th>Area</th>
                            <th>Price</th>
                            <th>Floor</th>
                            <th>Max</th>
                            <th>Mezzanine</th>
                            <th>AC</th>
                            <th>Status</th>
                        </tr>
                    </thead>

                    <tbody id="roomTable">
                        <c:forEach items="${Rooms}" var="r" varStatus="loop">
                            <tr class="room-row"
                                style="--row-delay: ${loop.index * 0.05}s;">
                                <td class="room-mono">${r.roomId}</td>
                                <td>${r.blockName}</td>

                                <td class="fw-bold roomNumber">
                                    ${r.roomNumber}
                                </td>

                                <td>${r.area}</td>

                                <td class="price">
                                    <c:choose>
                                        <c:when test="${not empty r.price}">
                                            <fmt:formatNumber value="${r.price}" pattern="#,##0" /> đ
                                        </c:when>
                                        <c:otherwise>
                                            0 đ
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td>${r.floor}</td>
                                <td>${r.maxTenants}</td>

                                <td>
                                    <span class="status ${r.mezzanine ? 'AVAILABLE' : 'MAINTENANCE'} room-chip">
                                        <c:choose>
                                            <c:when test="${r.mezzanine}">
                                                <i class="bi bi-check2-circle"></i> Yes
                                            </c:when>
                                            <c:otherwise>
                                                <i class="bi bi-x-circle"></i> No
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </td>

                                <td>
                                    <c:choose>
                                        <c:when test="${r.airConditioning}">
                                            <span class="room-ico room-ico-ac">
                                                <i class="bi bi-snow"></i>
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">No</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td>
                                    <span class="status ${r.status} room-status-btn"
                                          data-room-id="${r.roomId}"
                                          data-status="${r.status}">
                                        ${r.status}
                                    </span>
                                </td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty Rooms}">
                            <tr>
                                <td colspan="10" class="room-empty">
                                    No room found
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <!-- PAGINATION -->
            <div class="pagination-wrapper">
                <ul class="pagination">

                    <li class="${pageIndex == 1 ? 'disabled' : ''}">
                        <a href="${ctx}/manager/rooms?page=${pageIndex - 1}&search=${param.search}&status=${param.status}">
                            <i class="bi bi-chevron-left"></i>
                        </a>
                    </li>

                    <c:set var="window" value="2" />
                    <c:set var="start" value="${pageIndex - window}" />
                    <c:set var="end" value="${pageIndex + window}" />

                    <c:if test="${start < 2}">
                        <c:set var="start" value="2" />
                    </c:if>

                    <c:if test="${end > totalPage - 1}">
                        <c:set var="end" value="${totalPage - 1}" />
                    </c:if>

                    <li class="${pageIndex == 1 ? 'active' : ''}">
                        <a href="${ctx}/manager/rooms?page=1&search=${param.search}&status=${param.status}">1</a>
                    </li>

                    <c:if test="${start > 2}">
                        <li class="disabled">
                            <a>...</a>
                        </li>
                    </c:if>

                    <c:forEach begin="${start}" end="${end}" var="i">
                        <li class="${i == pageIndex ? 'active' : ''}">
                            <a href="${ctx}/manager/rooms?page=${i}&search=${param.search}&status=${param.status}">
                                ${i}
                            </a>
                        </li>
                    </c:forEach>

                    <c:if test="${end < totalPage - 1}">
                        <li class="disabled">
                            <a>...</a>
                        </li>
                    </c:if>

                    <c:if test="${totalPage > 1}">
                        <li class="${pageIndex == totalPage ? 'active' : ''}">
                            <a href="${ctx}/manager/rooms?page=${totalPage}&search=${param.search}&status=${param.status}">
                                ${totalPage}
                            </a>
                        </li>
                    </c:if>

                    <li class="${pageIndex == totalPage ? 'disabled' : ''}">
                        <a href="${ctx}/manager/rooms?page=${pageIndex + 1}&search=${param.search}&status=${param.status}">
                            <i class="bi bi-chevron-right"></i>
                        </a>
                    </li>

                </ul>
            </div>

        </div>

    </div>

    <!-- STATUS EDIT MODAL -->
    <div id="statusModal" class="room-modal">
        <div class="room-modal-box">
            <div class="modal-glow"></div>

            <h4>Change Room Status</h4>
            <p class="room-modal-subtitle">Choose a new status for this room.</p>

            <div class="room-status-options">
                <button type="button"
                        class="status AVAILABLE"
                        data-status="AVAILABLE">
                    AVAILABLE
                </button>

                <button type="button"
                        class="status MAINTENANCE"
                        data-status="MAINTENANCE">
                    MAINTENANCE
                </button>
            </div>

            <div class="room-modal-actions">
                <button id="cancelBtn" type="button">
                    Cancel
                </button>

                <button id="saveBtn" type="button">
                    Save
                </button>
            </div>
        </div>
    </div>

    <!-- TOAST -->
    <div id="roomToast" class="room-toast">
        <i class="bi bi-check2-circle"></i>
        <span id="roomToastText">Updating room status...</span>
    </div>

    <script src="${ctx}/assets/js/pages/manageRooms.js"></script>

</t:layout>