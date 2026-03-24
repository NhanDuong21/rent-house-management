<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

<layout:layout title="Subscribers - ${utilityName}"
               active="m_utilities"
               cssFile="${pageContext.request.contextPath}/assets/css/views/utilitySubscribers.css">

    <div class="ms-container">

        <!-- HEADER -->
        <div class="ms-header">
            <div class="ms-header-left">
                <a href="${pageContext.request.contextPath}/manager/utilities" class="ms-back-btn">
                    <i class="bi bi-arrow-left"></i> Back
                </a>
                <div class="ms-header-title">
                    <h2>Subscribers</h2>
                    <p>
                        Rooms currently using &nbsp;
                        <span class="ms-utility-badge">
                            <i class="bi bi-lightning-charge-fill"></i>
                            ${utilityName}
                        </span>
                    </p>
                </div>
            </div>
        </div>

        <!-- SEARCH -->
        <div class="ms-search-box">
            <div class="ms-search-form">
                <i class="bi bi-search"></i>
                <input type="text" id="subSearchInput"
                       placeholder="Search by room number or tenant name...">
            </div>
        </div>

        <!-- TABLE TITLE -->
        <div class="ms-card">
            <div class="ms-card-title">
                All Subscribers (<c:out value="${empty subscribers ? 0 : subscribers.size()}"/>)
            </div>

            <!-- TABLE -->
            <c:choose>
                <c:when test="${empty subscribers}">
                    <div class="ms-empty-state">
                        <div class="ms-empty-icon"><i class="bi bi-people"></i></div>
                        <p>No subscribers found for <strong>${utilityName}</strong>.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <table class="ms-table">
                        <thead>
                            <tr>
                                <th>Room_ID</th>
                                <th>Room_Number</th>
                                <th>Tenant Name</th>
                                <th>Usage_date</th>
                            </tr>
                        </thead>
                        <tbody id="subscriberTable">
                            <c:forEach var="s" items="${subscribers}" varStatus="loop">
                                <tr>
                                    <td>${s.utilityId}</td>
                                    <td>
                                        <span class="ms-room-badge">
                                            <i class="bi bi-door-open"></i>
                                            ${s.utilityName}
                                        </span>
                                    </td>
                                    <td>${s.unit}</td>
                                    <td>${s.status}</td> 
                                </tr>
                            </c:forEach>
                            <tr id="notFoundSub" style="display:none;">
                                <td colspan="3">No results found.</td>
                            </tr>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <script>
        const subSearchInput = document.getElementById('subSearchInput');
        if (subSearchInput) {
            subSearchInput.addEventListener('input', function () {
                const keyword = this.value.toLowerCase();
                const rows = document.querySelectorAll('#subscriberTable tr:not(#notFoundSub)');
                let hasResult = false;
                rows.forEach(row => {
                    const text = row.innerText.toLowerCase();
                    if (text.includes(keyword)) {
                        row.style.display = '';
                        hasResult = true;
                    } else {
                        row.style.display = 'none';
                    }
                });
                const notFound = document.getElementById('notFoundSub');
                if (notFound)
                    notFound.style.display = hasResult ? 'none' : '';
            });
        }
    </script>

</layout:layout>