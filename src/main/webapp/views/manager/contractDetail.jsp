<%--
    Document   : contractDetail
    Author     : Duong Thien Nhan - CE190741
--%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<layout:layout title="Contract Detail"
               active="m_contracts"
               cssFile="${pageContext.request.contextPath}/assets/css/views/contractDetail.css?v=4"
               jsFile="${pageContext.request.contextPath}/assets/js/pages/viewCCCD.js">

    <div class="tcd-container">

        <!-- Page header -->
        <div class="tcd-pagehead">
            <div class="tcd-pagehead-left">
                <a href="${pageContext.request.contextPath}/manager/contracts" class="tcd-back">
                    <i class="bi bi-arrow-left"></i>
                    <span>Back to Contracts</span>
                </a>

                <div class="tcd-breadcrumb">
                    <span class="tcd-bc-item">
                        <i class="bi bi-speedometer2"></i>
                        Manager
                    </span>
                    <span class="tcd-bc-sep">/</span>
                    <span class="tcd-bc-item">
                        <i class="bi bi-file-earmark-text"></i>
                        Contracts
                    </span>
                    <span class="tcd-bc-sep">/</span>
                    <span class="tcd-bc-item active">
                        Detail
                    </span>
                </div>
            </div>

            <div class="tcd-pagehead-right">
                <div class="tcd-minihelp">
                    <i class="bi bi-shield-check"></i>
                    <span>Manager view</span>
                </div>
            </div>
        </div>

        <c:set var="c" value="${contract}"/>

        <!-- Alerts -->
        <c:if test="${param.terminated eq '1'}">
            <div class="tcd-alert tcd-alert-success">
                <div class="tcd-alert-ic"><i class="bi bi-check-circle-fill"></i></div>
                <div class="tcd-alert-tx">
                    <div class="tcd-alert-title">Success</div>
                    <div class="tcd-alert-sub">Terminate successfully.</div>
                </div>
            </div>
        </c:if>

        <c:if test="${param.err eq '1'}">
            <div class="tcd-alert tcd-alert-warning">
                <div class="tcd-alert-ic"><i class="bi bi-exclamation-triangle-fill"></i></div>
                <div class="tcd-alert-tx">
                    <div class="tcd-alert-title">Error</div>
                    <div class="tcd-alert-sub">
                        Error code: <b><c:out value="${param.code}"/></b>
                    </div>
                </div>
            </div>
        </c:if>

        <!-- Main Card -->
        <div class="tcd-card">
            <div class="tcd-card-body">

                <!-- TOP BAR -->
                <div class="tcd-top">
                    <div class="tcd-top-left">
                        <div class="tcd-title">
                            <i class="bi bi-file-earmark-text"></i>
                            Contract #<fmt:formatNumber value="${c.contractId}" pattern="000000"/>
                        </div>
                        <div class="tcd-sub">
                            Complete rental agreement with legal terms and conditions
                        </div>
                    </div>

                    <div class="tcd-top-right">
                        <div class="tcd-status">
                            <c:choose>
                                <c:when test="${c.status eq 'ACTIVE'}">
                                    <span class="tcd-badge active">
                                        <i class="bi bi-lightning-charge-fill"></i>
                                        ACTIVE
                                    </span>
                                </c:when>
                                <c:when test="${c.status eq 'PENDING'}">
                                    <span class="tcd-badge pending">
                                        <i class="bi bi-hourglass-split"></i>
                                        PENDING
                                    </span>
                                </c:when>
                                <c:when test="${c.status eq 'ENDED'}">
                                    <span class="tcd-badge ended">
                                        <i class="bi bi-flag-fill"></i>
                                        ENDED
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <span class="tcd-badge cancelled">
                                        <i class="bi bi-x-octagon-fill"></i>
                                        <c:out value="${c.status}"/>
                                    </span>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- ACTIONS (Manager) -->
                        <c:if test="${c.status eq 'ACTIVE' || c.status eq 'PENDING'}">
                            <div class="tcd-actions">

                                <c:if test="${c.status eq 'ACTIVE'}">
                                    <a class="tcd-btn tcd-btn-primary"
                                       href="${pageContext.request.contextPath}/manager/contracts/extend?contractId=${c.contractId}">
                                        <i class="bi bi-arrow-repeat"></i>
                                        Extend
                                    </a>
                                </c:if>

                                <form method="post"
                                      action="${pageContext.request.contextPath}/manager/contracts/terminate"
                                      class="tcd-inline-form">
                                    <input type="hidden" name="contractId" value="${c.contractId}" />

                                    <button type="submit"
                                            class="tcd-btn tcd-btn-danger"
                                            onclick="
                                                    return confirm(
                                                            'Bạn chắc chắn muốn terminate Contract #<fmt:formatNumber value="${c.contractId}" pattern="000000"/> ?\n\n'
                                                            + 'Lưu ý: Nếu contract ACTIVE và có hợp đồng gia hạn (PENDING) cùng room, hệ thống sẽ huỷ luôn hợp đồng đó.\n'
                                                            + 'Nếu contract có BANK payment đang PENDING, hệ thống sẽ chặn terminate.'
                                                            );
                                            ">
                                        <i class="bi bi-slash-circle"></i>
                                        Terminate
                                    </button>
                                </form>

                            </div>
                        </c:if>
                    </div>
                </div>

                <div class="tcd-divider"></div>

                <!-- DOCUMENT HEADER -->
                <div class="tcd-doc-head">
                    <div class="tcd-doc-left">
                        <div class="tcd-doc-title">
                            <i class="bi bi-journal-text"></i>
                            ROOM RENTAL AGREEMENT
                        </div>
                        <div class="tcd-doc-meta">
                            <span class="tcd-meta-item">
                                <i class="bi bi-hash"></i>
                                Contract No:
                                <b><fmt:formatNumber value="${c.contractId}" pattern="000000"/></b>
                            </span>
                            <span class="tcd-dot">•</span>
                            <span class="tcd-meta-item">
                                <i class="bi bi-calendar2-week"></i>
                                Dated:
                                <b><fmt:formatDate value="${c.startDate}" pattern="dd/MM/yyyy"/></b>
                            </span>
                        </div>
                    </div>
                </div>

                <div class="tcd-divider tcd-divider-soft"></div>

                <!-- PARTY A/B -->
                <div class="tcd-grid-2">

                    <div class="tcd-section">
                        <div class="tcd-section-title">
                            <i class="bi bi-person-badge"></i>
                            PARTY A (LANDLORD)
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label"><i class="bi bi-person"></i> Full Name</span>
                            <span class="tcd-value"><c:out value="${empty c.landlordFullName ? '-' : c.landlordFullName}"/></span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label"><i class="bi bi-cake2"></i> Date of Birth</span>
                            <span class="tcd-value">
                                <c:choose>
                                    <c:when test="${empty c.landlordDateOfBirth}">-</c:when>
                                    <c:otherwise><fmt:formatDate value="${c.landlordDateOfBirth}" pattern="dd/MM/yyyy"/></c:otherwise>
                                </c:choose>
                            </span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label"><i class="bi bi-credit-card-2-front"></i> Citizen ID</span>
                            <span class="tcd-value"><c:out value="${empty c.landlordIdentityCode ? '-' : c.landlordIdentityCode}"/></span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label"><i class="bi bi-telephone"></i> Phone</span>
                            <span class="tcd-value"><c:out value="${empty c.landlordPhoneNumber ? '-' : c.landlordPhoneNumber}"/></span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label"><i class="bi bi-envelope"></i> Email</span>
                            <span class="tcd-value"><c:out value="${empty c.landlordEmail ? '-' : c.landlordEmail}"/></span>
                        </div>
                    </div>

                    <div class="tcd-section">
                        <div class="tcd-section-title">
                            <i class="bi bi-person-check"></i>
                            PARTY B (TENANT)
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label"><i class="bi bi-person"></i> Full Name</span>
                            <span class="tcd-value"><c:out value="${empty c.tenantName ? '-' : c.tenantName}"/></span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label"><i class="bi bi-cake2"></i> Date of Birth</span>
                            <span class="tcd-value">
                                <c:choose>
                                    <c:when test="${empty c.tenantDateOfBirth}">-</c:when>
                                    <c:otherwise><fmt:formatDate value="${c.tenantDateOfBirth}" pattern="dd/MM/yyyy"/></c:otherwise>
                                </c:choose>
                            </span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label"><i class="bi bi-credit-card-2-front"></i> Citizen ID</span>
                            <span class="tcd-value"><c:out value="${empty c.tenantIdentityCode ? '-' : c.tenantIdentityCode}"/></span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label"><i class="bi bi-telephone"></i> Phone</span>
                            <span class="tcd-value"><c:out value="${empty c.tenantPhoneNumber ? '-' : c.tenantPhoneNumber}"/></span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label"><i class="bi bi-envelope"></i> Email</span>
                            <span class="tcd-value"><c:out value="${empty c.tenantEmail ? '-' : c.tenantEmail}"/></span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label"><i class="bi bi-geo-alt"></i> Address</span>
                            <span class="tcd-value"><c:out value="${empty c.tenantAddress ? '-' : c.tenantAddress}"/></span>
                        </div>
                    </div>

                </div>

                <div class="tcd-divider tcd-divider-soft"></div>

                <!-- OCCUPANTS -->
                <div class="tcd-article">
                    <div class="tcd-article-title">
                        <i class="bi bi-people me-2"></i>
                        ARTICLE 0: OCCUPANTS INFORMATION
                    </div>

                    <!-- PRIMARY TENANT -->
                    <div class="tcd-line">
                        <span class="tcd-label">
                            <i class="bi bi-person-circle me-1"></i>
                            Primary Tenant:
                        </span>
                        <span class="tcd-value">
                            <strong><c:out value="${empty contract.tenantName ? '-' : contract.tenantName}"/></strong>
                            -
                            PRIMARY
                            -
                            <c:out value="${empty contract.status ? '-' : contract.status}"/>
                        </span>
                    </div>

                    <div class="tcd-line">
                        <span class="tcd-label">
                            <i class="bi bi-credit-card me-1"></i>
                            Citizen ID:
                        </span>
                        <span class="tcd-value">
                            <c:out value="${empty contract.tenantIdentityCode ? '-' : contract.tenantIdentityCode}"/>
                        </span>
                    </div>

                    <div class="tcd-line">
                        <span class="tcd-label">
                            <i class="bi bi-telephone me-1"></i>
                            Phone:
                        </span>
                        <span class="tcd-value">
                            <c:out value="${empty contract.tenantPhoneNumber ? '-' : contract.tenantPhoneNumber}"/>
                        </span>
                    </div>

                    <div class="tcd-line">
                        <span class="tcd-label">
                            <i class="bi bi-envelope me-1"></i>
                            Email:
                        </span>
                        <span class="tcd-value">
                            <c:out value="${empty contract.tenantEmail ? '-' : contract.tenantEmail}"/>
                        </span>
                    </div>

                    <div class="tcd-line">
                        <span class="tcd-label">
                            <i class="bi bi-geo-alt me-1"></i>
                            Address:
                        </span>
                        <span class="tcd-value">
                            <c:out value="${empty contract.tenantAddress ? '-' : contract.tenantAddress}"/>
                        </span>
                    </div>

                    <div class="tcd-line">
                        <span class="tcd-label">
                            <i class="bi bi-image me-1"></i>
                            Primary CCCD Front:
                        </span>
                        <span class="tcd-value">
                            <c:choose>
                                <c:when test="${not empty tenantCccdFront}">
                                    <button type="button"
                                            class="tcd-link-btn js-image-popup"
                                            data-image-url="${pageContext.request.contextPath}${tenantCccdFront}"
                                            data-image-title="Primary CCCD Front">
                                        View image
                                    </button>
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </span>
                    </div>

                    <div class="tcd-line">
                        <span class="tcd-label">
                            <i class="bi bi-image me-1"></i>
                            Primary CCCD Back:
                        </span>
                        <span class="tcd-value">
                            <c:choose>
                                <c:when test="${not empty tenantCccdBack}">
                                    <button type="button"
                                            class="tcd-link-btn js-image-popup"
                                            data-image-url="${pageContext.request.contextPath}${tenantCccdBack}"
                                            data-image-title="Primary CCCD Back">
                                        View image
                                    </button>
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </span>
                    </div>

                    <div class="tcd-divider tcd-divider-soft"></div>

                    <!-- ROOMMATES -->
                    <c:choose>
                        <c:when test="${empty occupants}">
                            <div class="tcd-note">
                                <i class="bi bi-info-circle me-1"></i>
                                Chưa có dữ liệu người ở cùng.
                            </div>
                        </c:when>

                        <c:otherwise>
                            <c:forEach var="o" items="${occupants}" varStatus="loop">
                                <div class="tcd-line">
                                    <span class="tcd-label">
                                        <i class="bi bi-person me-1"></i>
                                        Occupant ${loop.index + 1}:
                                    </span>
                                    <span class="tcd-value">
                                        <strong><c:out value="${o.fullName}"/></strong>
                                        -
                                        ROOMMATE
                                        -
                                        <c:out value="${o.status}"/>
                                    </span>
                                </div>

                                <div class="tcd-line">
                                    <span class="tcd-label">
                                        <i class="bi bi-credit-card me-1"></i>
                                        Citizen ID:
                                    </span>
                                    <span class="tcd-value">
                                        <c:out value="${empty o.identityCode ? '-' : o.identityCode}"/>
                                    </span>
                                </div>

                                <div class="tcd-line">
                                    <span class="tcd-label">
                                        <i class="bi bi-telephone me-1"></i>
                                        Phone:
                                    </span>
                                    <span class="tcd-value">
                                        <c:out value="${empty o.phoneNumber ? '-' : o.phoneNumber}"/>
                                    </span>
                                </div>

                                <div class="tcd-line">
                                    <span class="tcd-label">
                                        <i class="bi bi-envelope me-1"></i>
                                        Email:
                                    </span>
                                    <span class="tcd-value">
                                        <c:out value="${empty o.email ? '-' : o.email}"/>
                                    </span>
                                </div>

                                <div class="tcd-line">
                                    <span class="tcd-label">
                                        <i class="bi bi-calendar me-1"></i>
                                        Date of Birth:
                                    </span>
                                    <span class="tcd-value">
                                        <c:choose>
                                            <c:when test="${empty o.dateOfBirth}">-</c:when>
                                            <c:otherwise>
                                                <fmt:formatDate value="${o.dateOfBirth}" pattern="dd/MM/yyyy"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>

                                <div class="tcd-line">
                                    <span class="tcd-label">
                                        <i class="bi bi-geo-alt me-1"></i>
                                        Address:
                                    </span>
                                    <span class="tcd-value">
                                        <c:out value="${empty o.address ? '-' : o.address}"/>
                                    </span>
                                </div>

                                <div class="tcd-line">
                                    <span class="tcd-label">
                                        <i class="bi bi-image me-1"></i>
                                        CCCD Front:
                                    </span>
                                    <span class="tcd-value">
                                        <c:choose>
                                            <c:when test="${not empty o.cccdFrontUrl}">
                                                <button type="button"
                                                        class="tcd-link-btn js-image-popup"
                                                        data-image-url="${pageContext.request.contextPath}${o.cccdFrontUrl}"
                                                        data-image-title="Occupant ${loop.index + 1} - CCCD Front">
                                                    View image
                                                </button>
                                            </c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>

                                <div class="tcd-line">
                                    <span class="tcd-label">
                                        <i class="bi bi-image me-1"></i>
                                        CCCD Back:
                                    </span>
                                    <span class="tcd-value">
                                        <c:choose>
                                            <c:when test="${not empty o.cccdBackUrl}">
                                                <button type="button"
                                                        class="tcd-link-btn js-image-popup"
                                                        data-image-url="${pageContext.request.contextPath}${o.cccdBackUrl}"
                                                        data-image-title="Occupant ${loop.index + 1} - CCCD Back">
                                                    View image
                                                </button>
                                            </c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>

                                <c:if test="${!loop.last}">
                                    <div class="tcd-divider tcd-divider-soft"></div>
                                </c:if>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="tcd-divider tcd-divider-soft"></div>

                <!-- ARTICLE 1 -->
                <div class="tcd-article">
                    <div class="tcd-article-title">
                        <i class="bi bi-house-door"></i>
                        ARTICLE 1: RENTAL PROPERTY
                    </div>

                    <div class="tcd-grid-2 tcd-grid-compact">
                        <div class="tcd-line">
                            <span class="tcd-label"><i class="bi bi-door-open"></i> Room Number</span>
                            <span class="tcd-value"><c:out value="${empty c.roomNumber ? '-' : c.roomNumber}"/></span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label"><i class="bi bi-building"></i> Block</span>
                            <span class="tcd-value"><c:out value="${empty c.blockName ? '-' : c.blockName}"/></span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label"><i class="bi bi-layers"></i> Floor</span>
                            <span class="tcd-value"><c:out value="${empty c.floor ? '-' : c.floor}"/></span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label"><i class="bi bi-aspect-ratio"></i> Area</span>
                            <span class="tcd-value">
                                <c:choose>
                                    <c:when test="${empty c.area}">-</c:when>
                                    <c:otherwise><c:out value="${c.area}"/> m²</c:otherwise>
                                </c:choose>
                            </span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label"><i class="bi bi-people"></i> Maximum Occupants</span>
                            <span class="tcd-value"><c:out value="${empty c.maxTenants ? '-' : c.maxTenants}"/></span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label"><i class="bi bi-stars"></i> Amenities</span>
                            <span class="tcd-value">
                                <c:choose>
                                    <c:when test="${c.hasAirConditioning}">Air Conditioning</c:when>
                                    <c:otherwise>-</c:otherwise>
                                </c:choose>
                                <c:if test="${c.isMezzanine}">, Mezzanine</c:if>
                            </span>
                        </div>
                    </div>
                </div>

                <div class="tcd-divider tcd-divider-soft"></div>

                <!-- ARTICLE 2 -->
                <div class="tcd-article">
                    <div class="tcd-article-title">
                        <i class="bi bi-calendar-range"></i>
                        ARTICLE 2: RENTAL PERIOD
                    </div>

                    <div class="tcd-grid-2 tcd-grid-compact">
                        <div class="tcd-line">
                            <span class="tcd-label"><i class="bi bi-calendar2-check"></i> Start Date</span>
                            <span class="tcd-value"><fmt:formatDate value="${c.startDate}" pattern="dd/MM/yyyy"/></span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label"><i class="bi bi-calendar2-x"></i> End Date</span>
                            <span class="tcd-value">
                                <c:choose>
                                    <c:when test="${empty c.endDate}">-</c:when>
                                    <c:otherwise><fmt:formatDate value="${c.endDate}" pattern="dd/MM/yyyy"/></c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </div>
                </div>

                <div class="tcd-divider tcd-divider-soft"></div>

                <!-- ARTICLE 3 -->
                <div class="tcd-article">
                    <div class="tcd-article-title">
                        <i class="bi bi-cash-coin"></i>
                        ARTICLE 3: RENTAL FEE AND PAYMENT
                    </div>

                    <div class="tcd-grid-2 tcd-grid-compact">
                        <div class="tcd-line">
                            <span class="tcd-label"><i class="bi bi-wallet2"></i> Monthly Rent</span>
                            <span class="tcd-value">
                                <fmt:formatNumber value="${c.monthlyRent}" type="number" groupingUsed="true"/> ₫
                            </span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label"><i class="bi bi-safe2"></i> Security Deposit</span>
                            <span class="tcd-value">
                                <fmt:formatNumber value="${c.deposit}" type="number" groupingUsed="true"/> ₫
                            </span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label"><i class="bi bi-credit-card"></i> Payment Method</span>
                            <span class="tcd-value">Bank transfer or cash</span>
                        </div>
                    </div>

                    <div class="tcd-note">
                        <i class="bi bi-info-circle"></i>
                        <span>Note: Electricity and water charges are calculated separately based on actual usage.</span>
                    </div>
                </div>

                <!-- latestPayment -->
                <c:if test="${not empty latestPayment}">
                    <div class="tcd-divider"></div>

                    <div class="tcd-alert tcd-alert-success tcd-alert-inline">
                        <div class="tcd-alert-ic"><i class="bi bi-bank2"></i></div>
                        <div class="tcd-alert-tx">
                            <div class="tcd-alert-title">Latest bank transfer</div>
                            <div class="tcd-alert-sub">
                                Status:
                                <span class="tcd-pill"><c:out value="${latestPayment.status}"/></span>
                                <span class="tcd-muted">
                                    • Amount:
                                    <fmt:formatNumber value="${latestPayment.amount}" type="number" groupingUsed="true"/> ₫
                                </span>
                            </div>
                        </div>
                    </div>
                </c:if>

                <!-- Payment QR -->
                <c:if test="${c.status eq 'PENDING'}">
                    <div class="tcd-divider"></div>

                    <div class="tcd-pay-wrap">
                        <div class="tcd-pay-card">

                            <div class="tcd-pay-title">
                                <i class="bi bi-qr-code-scan"></i>
                                Payment QR
                            </div>
                            <div class="tcd-pay-sub">Quét QR để tenant chuyển khoản tiền cọc (deposit).</div>

                            <c:choose>
                                <c:when test="${not empty c.paymentQrData}">
                                    <div class="tcd-qr-box">
                                        <img class="tcd-qr-img"
                                             src="${pageContext.request.contextPath}${c.paymentQrData}"
                                             alt="Payment QR">
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="tcd-alert tcd-alert-warning">
                                        <div class="tcd-alert-ic"><i class="bi bi-exclamation-triangle-fill"></i></div>
                                        <div class="tcd-alert-tx">
                                            <div class="tcd-alert-title">Missing QR</div>
                                            <div class="tcd-alert-sub">Contract chưa có QR. Vui lòng kiểm tra dữ liệu.</div>
                                        </div>
                                    </div>
                                </c:otherwise>
                            </c:choose>

                            <c:if test="${not empty latestPayment}">
                                <div class="tcd-pay-latest">
                                    <i class="bi bi-activity"></i>
                                    Latest transfer status:
                                    <span class="tcd-pill"><c:out value="${latestPayment.status}"/></span>
                                </div>
                            </c:if>

                        </div>

                        <div class="tcd-pay-card">
                            <div class="tcd-pay-title">
                                <i class="bi bi-clipboard-check"></i>
                                Manager note
                            </div>
                            <div class="tcd-pay-sub">
                                Chờ tenant confirm transfer và/hoặc kiểm tra sao kê để duyệt hợp đồng.
                            </div>

                            <div class="tcd-note tcd-note-soft" style="margin-top:8px;">
                                <i class="bi bi-lightbulb"></i>
                                <span>Tip: Sau khi tenant chuyển khoản, manager kiểm tra trạng thái payment trước khi confirm contract.</span>
                            </div>
                        </div>
                    </div>
                </c:if>

            </div>
        </div>

    </div>

    <!-- IMAGE PREVIEW MODAL -->
    <div class="modal fade" id="tcdImageModal" tabindex="-1" aria-labelledby="tcdImageModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered modal-xl">
            <div class="modal-content tcd-image-modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="tcdImageModalLabel">Image Preview</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>

                <div class="modal-body tcd-image-modal-body">
                    <div class="tcd-image-loading d-none" id="tcdImageLoading">
                        <div class="spinner-border" role="status" aria-hidden="true"></div>
                        <span>Loading image...</span>
                    </div>

                    <img id="tcdPreviewImage"
                         class="tcd-preview-img d-none"
                         src=""
                         alt="Preview image">

                    <div id="tcdPreviewFallback" class="tcd-preview-fallback d-none">
                        <i class="bi bi-image"></i>
                        <div>Không thể tải ảnh.</div>
                    </div>
                </div>

                <div class="modal-footer">
                    <a id="tcdOpenImageNewTab"
                       href="#"
                       target="_blank"
                       rel="noopener noreferrer"
                       class="btn btn-outline-primary">
                        <i class="bi bi-box-arrow-up-right me-1"></i>
                        Open in new tab
                    </a>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>

</layout:layout>