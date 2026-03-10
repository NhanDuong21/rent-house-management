<%-- 
    Document   : tenantUtility
    Created on : Mar 5, 2026, 1:26:29 PM
    Author     : DELL
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<layout:layout title="Tenant Utility"
               active="t_utility"
               cssFile="${pageContext.request.contextPath}/assets/css/views/tenantUtility.css">
    <div class="mb-container">
        <!-- HEADER -->
        <div class="tu-header">
            <h2>Utility Services</h2>
            <p>Manage your utility service subscriptions</p>
        </div>

        <!-- BANNER -->
        <div class="tu-banner">
            <div>
                <h4>Available Services</h4>
                <p>Subscribe to additional services to enhance your living experience. Select the services you want and confirm your selection.</p>
            </div>
            <button class="tu-manage-btn">Manage Services</button>
        </div>

        <!-- CARDS -->
        <div class="tu-cards">
            <c:forEach var="u" items="${utility}">
                <div class="tu-card">
                    <div class="tu-card-icon">
                        <c:choose>
                            <c:when test="${u.utilityName.toLowerCase().contains('electric')}">
                                <i class="bi bi-lightning-charge-fill" style="color:#f59e0b;"></i>
                            </c:when>
                            <c:when test="${u.utilityName.toLowerCase().contains('water')}">
                                <i class="bi bi-droplet-fill" style="color:#3b82f6;"></i>
                            </c:when>
                            <c:when test="${u.utilityName.toLowerCase().contains('internet') || u.utilityName.toLowerCase().contains('wifi')}">
                                <i class="bi bi-wifi" style="color:#8b5cf6;"></i>
                            </c:when>
                            <c:when test="${u.utilityName.toLowerCase().contains('trash') || u.utilityName.toLowerCase().contains('garbage')}">
                                <i class="bi bi-trash-fill" style="color:#22c55e;"></i>
                            </c:when>
                            <c:when test="${u.utilityName.toLowerCase().contains('laundry')}">
                                <i class="bi bi-bag-fill" style="color:#3b82f6;"></i>
                            </c:when>
                            <c:when test="${u.utilityName.toLowerCase().contains('parking')}">
                                <i class="bi bi-p-circle-fill" style="color:#6b7280;"></i>
                            </c:when>
                            <c:otherwise>
                                <i class="bi bi-gear-fill" style="color:#6b7280;"></i>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="tu-card-name">${u.utilityName}</div>
                    <div class="tu-card-price">
                        <fmt:formatNumber value="${u.standardPrice}" type="number" groupingUsed="true"/>
                        VND/${u.unit}
                    </div>
                </div>
            </c:forEach>

            <c:if test="${empty utility}">
                <p style="color:#9ca3af; text-align:center; width:100%;">No utility services available.</p>
            </c:if>
        </div>
    </div>
</layout:layout>
