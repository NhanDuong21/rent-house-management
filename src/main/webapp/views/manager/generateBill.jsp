<%-- 
    Document   : generateBill
    Created on : Mar 6, 2026, 1:17:22 PM
    Author     : To Thi Thao Trang - CE191027
--%>

<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<layout:layout title="Generate Bill"
               active="m_billing"
               cssFile="${pageContext.request.contextPath}/assets/css/views/managerGenerateBill.css">

    <div class="tbg-container">

        <!-- HEADER -->
        <div class="tbg-pagehead">

            <div class="tbg-pagehead-left">

                <a href="${pageContext.request.contextPath}/manager/billing" class="tbg-back">
                    <i class="bi bi-arrow-left"></i>
                    <span>Back to Bills</span>
                </a>

                <div class="tbg-breadcrumb">
                    <span>Manager</span>
                    <span>/</span>
                    <span>Billing</span>
                    <span>/</span>
                    <span class="active">Generate</span>
                </div>

            </div>

            <div class="tbg-pagehead-right">
                <div class="tbg-minihelp">
                    <i class="bi bi-lightning-charge"></i>
                    <span>Generate monthly bill</span>
                </div>
            </div>

        </div>

        <!-- CARD -->
        <div class="tbg-card">
            <div class="tbg-card-body">

                <div class="tbg-title">
                    <i class="bi bi-receipt"></i>
                    Generate New Bill
                </div>

                <div class="tbg-sub">
                    Create a monthly rental bill for a specific room
                </div>

                <div class="tbg-divider"></div>

                <!-- FORM -->
                <form action="${pageContext.request.contextPath}/manager/billing/generate"
                      method="post"
                      class="tbg-form">

                    <!-- TOP -->
                    <div class="tbg-form-top">

                        <div class="tbg-left">

                            <!-- ROOM -->
                            <div class="tbg-field">
                                <label>Room</label>
                                <select id="roomSelect" name="roomId" required>
                                    <c:choose>
                                        <c:when test="${empty listRoom}">
                                            <option value="">No room available</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="">Select Room</option>
                                            <c:forEach items="${listRoom}" var="r">
                                                <option value="${r.roomId}"
                                                        data-tenant="${r.tenantName}"
                                                        data-rent="${r.monthlyRent}">
                                                    Room ${r.roomNumber}
                                                </option>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </select>
                            </div>

                            <!-- TENANT -->
                            <div class="tbg-field">
                                <label>Tenant</label>
                                <input type="text" id="tenantName" readonly>
                            </div>

                        </div>

                        <div class="tbg-right">

                            <!-- BILL MONTH -->
                            <div class="tbg-field">
                                <label>Bill Month</label>
                                <input type="month" name="billMonth" required>
                            </div>

                            <!-- DUE DATE -->
                            <div class="tbg-field">
                                <label>Due Date</label>
                                <input type="date" name="dueDate" required>
                            </div>

                        </div>

                    </div>
                    <div class="tbg-meter-title">
                        <i class="bi bi-speedometer2"></i>
                        Meter Input
                    </div>
                    <div class="tbg-meter-grid">

                        <!-- OLD ELECTRIC -->
                        <div class="tbg-field">
                            <label>Old Electric</label>
                            <input type="number" id="oldElectric" name="oldElectric" required>
                        </div>

                        <!-- NEW ELECTRIC -->
                        <div class="tbg-field">
                            <label>New Electric</label>
                            <input type="number" id="newElectric" name="newElectric" required>
                        </div> 
                        <!-- ELECTRIC USAGE -->
                        <div class="tbg-field">
                            <label>Electric Usage</label>
                            <input type="number" id="electricUsage" readonly> </div>
                        <!-- OLD WATER -->
                        <div class="tbg-field">
                            <label>Old Water</label>
                            <input type="number" id="oldWater" name="oldWater" required>
                        </div>
                        <!-- NEW WATER -->
                        <div class="tbg-field">
                            <label>New Water</label>
                            <input type="number" id="newWater" name="newWater" required>
                        </div>
                        <!-- WATER USAGE -->
                        <div class="tbg-field">
                            <label>Water Usage</label>
                            <input type="number" id="waterUsage" readonly>
                        </div>
                    </div>
                    <!-- ACTION -->
                    <div class="tbg-actions">

                        <button type="submit" class="tbg-btn-generate">
                            <i class="bi bi-lightning-charge"></i>
                            Generate Bill
                        </button>

                    </div>
                    <!-- ERROR -->
                    <c:if test="${not empty error}">
                        <div class="tbg-error">
                            ${error}
                        </div>
                    </c:if>

                </form>

            </div>
        </div>

    </div>

    <script src="${pageContext.request.contextPath}/assets/js/pages/managerGenerateBill.js"></script>

</layout:layout>
