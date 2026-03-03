<%-- 
    Document   : editRoom
    Created on : Feb 28, 2026, 1:31:41 AM
    Author     : truon
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<t:layout title="Edit Room"
          active="room"
          cssFile="${pageContext.request.contextPath}/assets/css/views/editRoom.css">

    <div class="edit-room-wrapper">

        <div class="edit-room-card">

            <div class="edit-room-title">
                Edit Room Status
            </div>

            <c:if test="${not empty error}">
                <div class="error-msg">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/manager/rooms" method="post">

                <input type="hidden" name="roomId" value="${room.roomId}">

                <div class="form-group">
                    <label>Room ID</label>
                    <input type="text" value="${room.roomId}" readonly>
                </div>
                <div class="mb-3">
                    <label class="form-label">Current Status</label>
                    <div>
                        <span class="edit-room-status status-${room.status}">
                            ${room.status}
                        </span>
                    </div>
                </div>

                <div>
                    <label class="form-label">Change Status</label>

                    <div class="status-group">
                        <input class="status-radio" type="radio" id="available" name="status"
                               value="AVAILABLE"
                               ${room.status == 'AVAILABLE' ? 'checked' : ''}>
                        <label class="status-label" for="available">AVAILABLE</label>

                        <input class="status-radio" type="radio" id="maintenance" name="status"
                               value="MAINTENANCE"
                               ${room.status == 'MAINTENANCE' ? 'checked' : ''}>
                        <label class="status-label" for="maintenance">MAINTENANCE</label>
                    </div>
                </div>

                <div class="edit-room-actions">
                    <button class="btn-save">Save</button>

                    <a href="${pageContext.request.contextPath}/manager/rooms"
                       class="btn-cancel">
                        Cancel
                    </a>
                </div>

            </form>

        </div>
    </div>

</t:layout>