<%-- 
    Document   : viewListRoom
    Created on : Mar 5, 2026
    Author     : truon
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:layout title="Room List"
          active="room"
          cssFile="${ctx}/assets/css/views/manageRooms.css">

    <div class="manage-room container-fluid">

        <!-- HEADER -->
        <div class="page-header">
            <h2>Room List</h2>
            <p>View all rooms in the system</p>
        </div>

        <!-- CARD -->
        <div class="room-card">

            <div class="room-card-head">
                <h5>All Rooms (${totalRoom})</h5>

                <form class="room-search-form"
                      method="get"
                      action="${ctx}/manager/rooms">

                    <!-- SEARCH -->
                    <span class="room-search-ico">
                        <i class="bi bi-search"></i>
                    </span>

                    <input class="searchRoom"
                           type="text"
                           name="search"
                           id="roomSearch"
                           placeholder="Search by room number"
                           value="${param.search}"
                           autocomplete="off">

                    <!-- STATUS FILTER -->
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

                    <!-- FILTER BUTTON -->
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

                        <c:forEach items="${Rooms}" var="r">
                            <tr>
                                <td class="room-mono">${r.roomId}</td>

                                <td>${r.blockName}</td>

                                <td class="fw-bold roomNumber">
                                    ${r.roomNumber}
                                </td>

                                <td>${r.area}</td>

                                <td class="price">
                                    ${r.price} đ
                                </td>

                                <td>${r.floor}</td>

                                <td>${r.maxTenants}</td>

                                <td>
                                    <span class="status ${r.mezzanine ? 'AVAILABLE' : 'MAINTENANCE'}">
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
            <h4>Change Room Status</h4>

            <div class="room-status-options">
                <button class="status AVAILABLE"
                        data-status="AVAILABLE">
                    AVAILABLE
                </button>

                <button class="status MAINTENANCE"
                        data-status="MAINTENANCE">
                    MAINTENANCE
                </button>
            </div>

            <div class="room-modal-actions">
                <button id="cancelBtn">
                    Cancel
                </button>

                <button id="saveBtn">
                    Save
                </button>
            </div>
        </div>
    </div>

    <script src="${ctx}/assets/js/pages/manageRooms.js"></script>

</t:layout>