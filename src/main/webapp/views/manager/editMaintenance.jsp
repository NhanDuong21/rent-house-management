<%-- 
    Document   : editMaintenance
    Created on : Mar 8, 2026, 2:50:20 AM
    Author     : truon
--%>

<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:layout 
    title="Edit Maintenance Request"
    active="m_maintenance"
    cssFile="${ctx}/assets/css/views/editMaintenance.css">

    <c:set var="m" value="${maintenance}" />

    <div class="em-container">

        <!-- HEADER -->
        <div class="em-header">
            <div>
                <h2>Maintenance Request #${m.requestId}</h2>
                <p>Update maintenance status and view request details.</p>
            </div>

            <a class="em-btn" href="${ctx}/manager/maintenance">
                <i class="bi bi-arrow-left"></i>
                Back
            </a>
        </div>

        <!-- REQUEST INFORMATION -->
        <div class="em-card">
            <div class="em-card-title">Request Information</div>

            <div class="em-grid">
                <div>
                    <label>Room</label>
                    <input type="text"
                           class="form-control"
                           value="${m.roomNumber}"
                           readonly>
                </div>

                <div>
                    <label>Tenant</label>
                    <input type="text"
                           class="form-control"
                           value="${m.fullName}"
                           readonly>
                </div>

                <div>
                    <label>Category</label>
                    <input type="text"
                           class="form-control"
                           value="${m.issueCategory}"
                           readonly>
                </div>

                <div>
                    <label>Created At</label>
                    <input type="text"
                           class="form-control"
                           value="<fmt:formatDate value='${m.createdAt}' pattern='yyyy-MM-dd HH:mm'/>"
                           readonly>
                </div>
            </div>

            <div class="em-block">
                <label>Description</label>
                <textarea class="form-control"
                          rows="4"
                          readonly>${m.description}</textarea>
            </div>
        </div>

        <!-- REQUEST IMAGE -->
        <div class="em-card">
            <div class="em-card-title">Request Image</div>

            <c:choose>
                <c:when test="${not empty m.imageUrl}">
                    <div class="em-image-wrap">
                        <img class="em-image"
                             src="${ctx}/assets/images/maintenance/${m.imageUrl}"
                             alt="Maintenance Image">
                    </div>
                </c:when>

                <c:otherwise>
                    <div class="no-image">
                        No image provided
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- UPDATE STATUS -->
        <div class="em-card">
            <div class="em-card-title">Update Status</div>

            <form method="post" action="${ctx}/manager/maintenance" id="emForm">
                <input type="hidden" name="requestId" value="${m.requestId}" />

                <div class="em-grid">
                    <div>
                        <label>Status</label>
                        <select name="status" class="form-select" id="emStatus">
                            <option value="PENDING"
                                    ${m.status == 'PENDING' ? 'selected' : ''}>
                                PENDING
                            </option>
                            <option value="CANCELLED"
                                    ${m.status == 'CANCELLED' ? 'selected' : ''}>
                                CANCELLED
                            </option>
                            <option value="IN_PROGRESS"
                                    ${m.status == 'IN_PROGRESS' ? 'selected' : ''}>
                                IN PROGRESS
                            </option>

                            <option value="DONE"
                                    ${m.status == 'DONE' ? 'selected' : ''}>
                                DONE
                            </option>
                        </select>
                    </div>

                    <div>
                        <label>Completed At</label>
                        <input type="text"
                               class="form-control"
                               id="emCompletedAt"
                               value="<fmt:formatDate value='${m.completedAt}' pattern='yyyy-MM-dd HH:mm'/>"
                               readonly>
                    </div>
                </div>

                <div class="em-actions">
                    <button class="em-btn primary" type="submit" id="emSubmitBtn">
                        <i class="bi bi-check-circle"></i>
                        Update Status
                    </button>
                </div>
            </form>
        </div>

    </div>

    <script src="${ctx}/assets/js/pages/editMaintenance.js"></script>
</t:layout>