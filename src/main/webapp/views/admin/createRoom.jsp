<%-- 
    Document   : createRoom
    Created on : Mar 3, 2026, 1:31:33 PM
    Author     : truon
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<t:layout>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/views/admin/addRoom.css"/>

    <div class="add-room-wrapper">
        <div class="add-room-card">

            <div class="add-room-title">
                Create New Room
            </div>

            <c:if test="${not empty err}">
                <div class="error-msg">${err}</div>
            </c:if>

            <form method="post"
                  action="${pageContext.request.contextPath}/admin/rooms/create"
                  enctype="multipart/form-data">

                <!-- BLOCK -->
                <div class="form-group">
                    <label>Block Name</label>
                    <input type="text" name="blockName" class="form-control" 
                           placeholder="Enter Block Name" required>
                </div>

                <!-- ROOM INFO -->
                <div class="form-group">
                    <label>Room Number</label>
                    <input type="text" name="roomNumber" required/>
                </div>

                <div class="form-row">
                    <div class="form-group half">
                        <label>Price</label>
                        <input type="number" step="0.01" name="price" required/>
                    </div>
                    <div class="form-group half">
                        <label>Area (m²)</label>
                        <input type="number" step="0.01" name="area" required/>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group half">
                        <label>Floor</label>
                        <input type="number" name="floor" required/>
                    </div>
                    <div class="form-group half">
                        <label>Max Tenants</label>
                        <input type="number" name="maxTenants" required/>
                    </div>
                </div>

                <!-- CHECKBOX -->
                <div class="checkbox-group">
                    <label>
                        <input type="checkbox" name="isMezzanine"/>
                        Mezzanine
                    </label>
                    <label>
                        <input type="checkbox" name="airConditioning"/>
                        Air Conditioning
                    </label>
                </div>

                <!-- DESCRIPTION -->
                <div class="form-group">
                    <label>Description</label>
                    <textarea name="description"></textarea>
                </div>

                <!-- IMAGES -->
                <div class="form-group">
                    <label>Room Images (0-12)</label>
                    <input type="file"
                           name="images"
                           multiple
                           accept="image/*"
                           required/>
                </div>

                <!-- BUTTON -->
                <div class="add-room-actions">
                    <button type="submit" class="btn-add">Create Room</button>
                    <a href="${pageContext.request.contextPath}/admin/rooms" class="btn-cancel">Cancel</a>
                </div>

            </form>

        </div>
    </div>

</t:layout>