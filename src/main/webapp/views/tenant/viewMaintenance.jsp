<%-- 
    Document   : viewMaintenance
    Created on : Mar 8, 2026, 3:57:21 PM
    Author     : truon
--%>

<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<t:layout
    title="Maintenance Request Detail"
    active="maintenance"
    cssFile="${ctx}/assets/css/views/editMaintenance.css">
    <c:set var="m" value="${maintenance}" />
    <div class="em-container">
        <div class="em-header">
            <div>
                <h2>Maintenance Request #${m.requestId}</h2>
                <p>View maintenance request details.</p>
            </div>
            <a class="em-btn" href="${ctx}/tenant/maintenance">
                <i class="bi bi-arrow-left"></i> Back
            </a>
        </div>

        <div class="em-card">
            <div class="em-card-title">Request Information</div>
            <div class="em-grid">
                <div>
                    <label>Room</label>
                    <input type="text" class="form-control" value="${m.roomNumber}" readonly>
                </div>
                <div>
                    <label>Tenant</label>
                    <input type="text" class="form-control" value="${m.fullName}" readonly>
                </div>
                <div>
                    <label>Category</label>
                    <input type="text" class="form-control" value="${m.issueCategory}" readonly>
                </div>
                <div>
                    <label>Status</label>
                    <input type="text" class="form-control" value="${m.status}" readonly>
                </div>
                <div>
                    <label>Created At</label>
                    <input type="text" class="form-control"
                           value="<fmt:formatDate value='${m.createdAt}' pattern='yyyy-MM-dd HH:mm'/>"
                           readonly>
                </div>
                <div>
                    <label>Completed At</label>
                    <input type="text" class="form-control"
                           value="<fmt:formatDate value='${m.completedAt}' pattern='yyyy-MM-dd HH:mm'/>"
                           readonly>
                </div>
            </div>
            <div style="margin-top: 20px">
                <label>Description</label>
                <textarea class="form-control" rows="4" readonly>${m.description}</textarea>
            </div>
        </div>

        <div class="em-card">
            <div class="em-card-title">Request Image</div>
            <c:choose>
                <c:when test="${not empty m.imageUrl}">
                    <div class="em-images">
                        <c:forEach var="img" items="${fn:split(m.imageUrl, ',')}">
                            <img class="em-image"
                                 src="${ctx}/assets/images/maintenance/${img}"
                                 alt="Maintenance Image">
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="no-image">No image provided</div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</t:layout>