<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<t:layout>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/views/admin/dashboard.css"/>

<div class="dashboard-wrapper">

    <!-- TITLE -->
    <div class="dashboard-title">
        <h2>Admin Dashboard</h2>
        <p>Overview of your rental management system</p>
    </div>


    <!-- STATS -->
    <div class="dashboard-grid">

        <!-- Total Tenants -->
        <div class="dashboard-card">
            <div class="card-header">
                <span>Total Tenants</span>
                <i class="bi bi-people"></i>
            </div>

            <h3>${totalTenants}</h3>
            <p>Active tenant accounts</p>
        </div>


        <!-- Available Rooms -->
        <div class="dashboard-card">
            <div class="card-header">
                <span>Available Rooms</span>
                <i class="bi bi-house"></i>
            </div>

            <h3>${availableRooms}</h3>
            <p>Ready for rent</p>
        </div>


        <!-- Maintenance -->
        <div class="dashboard-card">
            <div class="card-header">
                <span>Maintenance Requests</span>
                <i class="bi bi-tools"></i>
            </div>

            <h3>${maintenanceRequests}</h3>
            <p>Pending requests</p>
        </div>


        <!-- Occupied -->
        <div class="dashboard-card">
            <div class="card-header">
                <span>Occupied Rooms</span>
                <i class="bi bi-building"></i>
            </div>

            <h3>${occupiedRooms}</h3>
            <p>Currently rented</p>
        </div>


        <!-- Monthly Revenue -->
        <div class="dashboard-card">
            <div class="card-header">
                <span>Monthly Revenue</span>
                <i class="bi bi-currency-dollar"></i>
            </div>

            <h3>
                <fmt:formatNumber value="${monthlyRevenue}" type="number"/> đ
            </h3>

            <p>This month's income</p>
        </div>


        <!-- Total Revenue -->
        <div class="dashboard-card">
            <div class="card-header">
                <span>Total Revenue</span>
                <i class="bi bi-graph-up"></i>
            </div>

            <h3>
                <fmt:formatNumber value="${totalRevenue}" type="number"/> đ
            </h3>

            <p>All-time revenue</p>
        </div>

    </div>

</div>

</t:layout>