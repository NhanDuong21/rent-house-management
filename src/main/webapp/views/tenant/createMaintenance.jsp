<%-- 
    Document   : createMaintenance
    Created on : Mar 8, 2026, 4:51:53 PM
    Author     : truon
--%>

<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<t:layout title="Create Maintenance Request"
          active="maintenance"
          cssFile="${pageContext.request.contextPath}/assets/css/views/createMaintenance.css">

    <div class="create-maintenance-wrapper">
        <div class="create-maintenance-card">
            <div class="create-maintenance-title">Create Maintenance Request</div>
            <c:if test="${not empty error}">
                <div class="error-msg">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/tenant/maintenance"
                  method="post"
                  enctype="multipart/form-data">

                <div class="form-group">
                    <label>Room</label>
                    <select name="roomId" class="form-input" required>
                        <c:forEach var="room" items="${rooms}">
                            <option value="${room.roomId}">${room.roomNumber}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label>Issue Category</label>
                    <div class="category-group">
                        <input class="category-radio" type="radio" id="electric" name="category" value="ELECTRIC" checked>
                        <label class="category-label" for="electric">
                            <i class="bi bi-lightning-charge-fill"></i> Electric
                        </label>

                        <input class="category-radio" type="radio" id="water" name="category" value="WATER">
                        <label class="category-label" for="water">
                            <i class="bi bi-droplet-fill"></i> Water
                        </label>

                        <input class="category-radio" type="radio" id="other" name="category" value="OTHER">
                        <label class="category-label" for="other">
                            <i class="bi bi-wrench-adjustable"></i> Other
                        </label>
                    </div>
                </div>

                <div class="form-group">
                    <label>Description</label>
                    <textarea name="description"
                              class="form-input"
                              rows="4"
                              placeholder="Describe the issue in detail..."
                              required></textarea>
                </div>

                <div class="form-group">
                    <label>
                        Upload Images
                        <span class="hint">(optional, max 3)</span>
                    </label>
                    <input type="file"
                           name="images"
                           class="form-input file-input"
                           multiple
                           accept="image/*">
                </div>

                <div class="create-maintenance-actions">
                    <button type="submit" class="btn-save">
                        <i class="bi bi-send-fill"></i> Submit Request
                    </button>
                    <a href="${pageContext.request.contextPath}/tenant/maintenance" class="btn-cancel">
                        <i class="bi bi-x-lg"></i> Cancel
                    </a>
                </div>

            </form>
        </div>
    </div>

</t:layout>