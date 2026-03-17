<%-- 
    Document   : contractDetail
    Author     : Duong Thien Nhan - CE190741
--%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<layout:layout title="Contract Detail"
               active="tenant_contract"
               cssFile="${pageContext.request.contextPath}/assets/css/views/tenantContractDetail.css">

    <div class="tcd-wrap">

        <a href="${pageContext.request.contextPath}/tenant/contract" class="tcd-back">
            <i class="bi bi-arrow-left me-2"></i> Back
        </a>

        <c:set var="c" value="${contract}"/>

        <!-- Add occupant alerts -->
        <c:if test="${param.added == '1'}">
            <div class="tcd-alert tcd-alert-success">
                <i class="bi bi-check-circle me-2"></i>
                Thêm người ở cùng thành công.
            </div>
        </c:if>

        <c:if test="${param.err == 'max'}">
            <div class="tcd-alert tcd-alert-warning">
                <i class="bi bi-exclamation-triangle me-2"></i>
                Phòng đã đạt số lượng người tối đa.
            </div>
        </c:if>

        <c:if test="${param.err == 'document'}">
            <div class="tcd-alert tcd-alert-danger">
                <i class="bi bi-x-circle me-2"></i>
                Lỗi lưu CCCD của người ở cùng.
            </div>
        </c:if>

        <c:if test="${param.err == 'occupant'}">
            <div class="tcd-alert tcd-alert-danger">
                <i class="bi bi-x-circle me-2"></i>
                Không tạo được người ở cùng.
            </div>
        </c:if>

        <c:if test="${param.err == 'notpending'}">
            <div class="tcd-alert tcd-alert-warning">
                <i class="bi bi-exclamation-triangle me-2"></i>
                Chỉ được thêm người ở cùng khi hợp đồng đang PENDING.
            </div>
        </c:if>

        <c:if test="${param.err == 'contractNotFound'}">
            <div class="tcd-alert tcd-alert-danger">
                <i class="bi bi-x-circle me-2"></i>
                Không tìm thấy hợp đồng.
            </div>
        </c:if>

        <c:if test="${param.err == 'missingContractId'}">
            <div class="tcd-alert tcd-alert-danger">
                <i class="bi bi-x-circle me-2"></i>
                Thiếu mã hợp đồng.
            </div>
        </c:if>

        <c:if test="${param.err == 'badContractId'}">
            <div class="tcd-alert tcd-alert-danger">
                <i class="bi bi-x-circle me-2"></i>
                Mã hợp đồng không hợp lệ.
            </div>
        </c:if>

        <c:if test="${param.err == '1' and not empty param.msg}">
            <div class="tcd-alert tcd-alert-danger">
                <i class="bi bi-x-circle me-2"></i>
                Có lỗi xảy ra:
                <c:out value="${param.msg}"/>
            </div>
        </c:if>

        <div class="tcd-card">
            <div class="tcd-card-body">

                <!-- Top -->
                <div class="tcd-top">
                    <div>
                        <div class="tcd-contract-id">
                            <i class="bi bi-file-earmark-text me-2"></i>
                            Contract #<fmt:formatNumber value="${c.contractId}" pattern="000000"/>
                        </div>
                        <div class="tcd-sub">
                            <i class="bi bi-info-circle me-1"></i>
                            Complete rental agreement with legal terms and conditions
                        </div>
                    </div>

                    <c:choose>
                        <c:when test="${c.status eq 'ACTIVE'}">
                            <span class="tcd-badge tcd-badge-active">
                                <i class="bi bi-check-circle me-1"></i> ACTIVE
                            </span>
                        </c:when>
                        <c:otherwise>
                            <span class="tcd-badge tcd-badge-pending">
                                <i class="bi bi-clock-history me-1"></i> <c:out value="${c.status}"/>
                            </span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="tcd-divider"></div>

                <!-- Document header -->
                <div class="tcd-doc-title">
                    <i class="bi bi-journal-text me-2"></i>
                    ROOM RENTAL AGREEMENT
                </div>

                <div class="tcd-doc-meta">
                    <i class="bi bi-hash me-1"></i>
                    Contract No: <fmt:formatNumber value="${c.contractId}" pattern="000000"/>
                </div>

                <div class="tcd-doc-meta tcd-doc-meta-2">
                    <i class="bi bi-calendar-event me-1"></i>
                    Dated:
                    <c:choose>
                        <c:when test="${empty c.startDate}">-</c:when>
                        <c:otherwise>
                            <fmt:formatDate value="${c.startDate}" pattern="dd/MM/yyyy"/>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="tcd-divider tcd-divider-soft"></div>

                <!-- PARTY A / PARTY B -->
                <div class="tcd-grid-2">

                    <!-- PARTY A -->
                    <div>
                        <div class="tcd-section-title">
                            <i class="bi bi-person-badge me-2"></i>
                            PARTY A (LANDLORD)
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label">
                                <i class="bi bi-person me-1"></i> Full Name:
                            </span>
                            <span class="tcd-value">
                                <c:out value="${empty c.landlordFullName ? '-' : c.landlordFullName}"/>
                            </span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label">
                                <i class="bi bi-calendar me-1"></i> Date of Birth:
                            </span>
                            <span class="tcd-value">
                                <c:choose>
                                    <c:when test="${empty c.landlordDateOfBirth}">-</c:when>
                                    <c:otherwise>
                                        <fmt:formatDate value="${c.landlordDateOfBirth}" pattern="dd/MM/yyyy"/>
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label">
                                <i class="bi bi-credit-card me-1"></i> Citizen ID:
                            </span>
                            <span class="tcd-value">
                                <c:out value="${empty c.landlordIdentityCode ? '-' : c.landlordIdentityCode}"/>
                            </span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label">
                                <i class="bi bi-telephone me-1"></i> Phone:
                            </span>
                            <span class="tcd-value">
                                <c:out value="${empty c.landlordPhoneNumber ? '-' : c.landlordPhoneNumber}"/>
                            </span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label">
                                <i class="bi bi-envelope me-1"></i> Email:
                            </span>
                            <span class="tcd-value">
                                <c:out value="${empty c.landlordEmail ? '-' : c.landlordEmail}"/>
                            </span>
                        </div>
                    </div>

                    <!-- PARTY B -->
                    <div>
                        <div class="tcd-section-title">
                            <i class="bi bi-person-circle me-2"></i>
                            PARTY B (TENANT)
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label">
                                <i class="bi bi-person me-1"></i> Full Name:
                            </span>
                            <span class="tcd-value">
                                <c:out value="${empty c.tenantName ? '-' : c.tenantName}"/>
                            </span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label">
                                <i class="bi bi-calendar me-1"></i> Date of Birth:
                            </span>
                            <span class="tcd-value">
                                <c:choose>
                                    <c:when test="${empty c.tenantDateOfBirth}">-</c:when>
                                    <c:otherwise>
                                        <fmt:formatDate value="${c.tenantDateOfBirth}" pattern="dd/MM/yyyy"/>
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label">
                                <i class="bi bi-credit-card me-1"></i> Citizen ID:
                            </span>
                            <span class="tcd-value">
                                <c:out value="${empty c.tenantIdentityCode ? '-' : c.tenantIdentityCode}"/>
                            </span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label">
                                <i class="bi bi-telephone me-1"></i> Phone:
                            </span>
                            <span class="tcd-value">
                                <c:out value="${empty c.tenantPhoneNumber ? '-' : c.tenantPhoneNumber}"/>
                            </span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label">
                                <i class="bi bi-envelope me-1"></i> Email:
                            </span>
                            <span class="tcd-value">
                                <c:out value="${empty c.tenantEmail ? '-' : c.tenantEmail}"/>
                            </span>
                        </div>

                        <div class="tcd-line">
                            <span class="tcd-label">
                                <i class="bi bi-geo-alt me-1"></i> Address:
                            </span>
                            <span class="tcd-value">
                                <c:out value="${empty c.tenantAddress ? '-' : c.tenantAddress}"/>
                            </span>
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
                                                <a href="${pageContext.request.contextPath}${o.cccdFrontUrl}" target="_blank">
                                                    View image
                                                </a>
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
                                                <a href="${pageContext.request.contextPath}${o.cccdBackUrl}" target="_blank">
                                                    View image
                                                </a>
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

                <!-- Add roommate -->
                <c:if test="${c.status eq 'PENDING'}">
                    <div class="tcd-divider"></div>

                    <div class="tcd-article">
                        <div class="tcd-article-title">
                            <i class="bi bi-person-plus me-2"></i>
                            Add Roommate
                        </div>

                        <form method="post"
                              action="${pageContext.request.contextPath}/tenant/add-occupant?contractId=${c.contractId}"
                              enctype="multipart/form-data">

                            <input type="hidden" name="contractId" value="${c.contractId}"/>

                            <div class="tcd-line">
                                <span class="tcd-label">Full Name</span>
                                <input type="text" name="fullName" required/>
                            </div>

                            <div class="tcd-line">
                                <span class="tcd-label">Citizen ID</span>
                                <input type="text" name="identityCode" required/>
                            </div>

                            <div class="tcd-line">
                                <span class="tcd-label">Phone</span>
                                <input type="text" name="phone" required/>
                            </div>

                            <div class="tcd-line">
                                <span class="tcd-label">Email</span>
                                <input type="email" name="email" required/>
                            </div>

                            <div class="tcd-line">
                                <span class="tcd-label">Address</span>
                                <input type="text" name="address" required/>
                            </div>

                            <div class="tcd-line">
                                <span class="tcd-label">Date of Birth</span>
                                <input type="date" name="dob" required/>
                            </div>

                            <div class="tcd-line">
                                <span class="tcd-label">Gender</span>
                                <select name="gender" required>
                                    <option value="">-- Select --</option>
                                    <option value="1">Male</option>
                                    <option value="0">Female</option>
                                </select>
                            </div>

                            <div class="tcd-line">
                                <span class="tcd-label">CCCD Front</span>
                                <input type="file" name="cccdFront" accept=".jpg,.jpeg,.png,.webp,image/*" required/>
                            </div>

                            <div class="tcd-line">
                                <span class="tcd-label">CCCD Back</span>
                                <input type="file" name="cccdBack" accept=".jpg,.jpeg,.png,.webp,image/*" required/>
                            </div>

                            <button class="tcd-btn" type="submit">
                                <i class="bi bi-plus-circle"></i>
                                Add Occupant
                            </button>
                        </form>
                    </div>
                </c:if>

                <div class="tcd-divider tcd-divider-soft"></div>

                <!-- ARTICLE 1 -->
                <div class="tcd-article">
                    <div class="tcd-article-title">
                        <i class="bi bi-house-door me-2"></i>
                        ARTICLE 1: RENTAL PROPERTY
                    </div>

                    <div class="tcd-line">
                        <span class="tcd-label">
                            <i class="bi bi-door-open me-1"></i> Room Number:
                        </span>
                        <span class="tcd-value">
                            <c:out value="${empty c.roomNumber ? '-' : c.roomNumber}"/>
                        </span>
                    </div>

                    <div class="tcd-line">
                        <span class="tcd-label">
                            <i class="bi bi-building me-1"></i> Block:
                        </span>
                        <span class="tcd-value">
                            <c:out value="${empty c.blockName ? '-' : c.blockName}"/>
                        </span>
                    </div>

                    <div class="tcd-line">
                        <span class="tcd-label">
                            <i class="bi bi-layers me-1"></i> Floor:
                        </span>
                        <span class="tcd-value">
                            <c:out value="${empty c.floor ? '-' : c.floor}"/>
                        </span>
                    </div>

                    <div class="tcd-line">
                        <span class="tcd-label">
                            <i class="bi bi-aspect-ratio me-1"></i> Area:
                        </span>
                        <span class="tcd-value">
                            <c:choose>
                                <c:when test="${empty c.area}">-</c:when>
                                <c:otherwise><c:out value="${c.area}"/> m²</c:otherwise>
                            </c:choose>
                        </span>
                    </div>

                    <div class="tcd-line">
                        <span class="tcd-label">
                            <i class="bi bi-people me-1"></i> Maximum Occupants:
                        </span>
                        <span class="tcd-value">
                            <c:out value="${empty c.maxTenants ? '-' : c.maxTenants}"/>
                        </span>
                    </div>

                    <div class="tcd-line">
                        <span class="tcd-label">
                            <i class="bi bi-stars me-1"></i> Amenities:
                        </span>
                        <span class="tcd-value">
                            <c:choose>
                                <c:when test="${c.hasAirConditioning}">Air Conditioning</c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                            <c:if test="${c.isMezzanine}">, Mezzanine</c:if>
                        </span>
                    </div>
                </div>

                <div class="tcd-divider tcd-divider-soft"></div>

                <!-- ARTICLE 2 -->
                <div class="tcd-article">
                    <div class="tcd-article-title">
                        <i class="bi bi-calendar-range me-2"></i>
                        ARTICLE 2: RENTAL PERIOD
                    </div>

                    <div class="tcd-line">
                        <span class="tcd-label">
                            <i class="bi bi-calendar-check me-1"></i> Start Date:
                        </span>
                        <span class="tcd-value">
                            <c:choose>
                                <c:when test="${empty c.startDate}">-</c:when>
                                <c:otherwise>
                                    <fmt:formatDate value="${c.startDate}" pattern="dd/MM/yyyy"/>
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </div>

                    <div class="tcd-line">
                        <span class="tcd-label">
                            <i class="bi bi-calendar-x me-1"></i> End Date:
                        </span>
                        <span class="tcd-value">
                            <c:choose>
                                <c:when test="${empty c.endDate}">-</c:when>
                                <c:otherwise>
                                    <fmt:formatDate value="${c.endDate}" pattern="dd/MM/yyyy"/>
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                </div>

                <div class="tcd-divider tcd-divider-soft"></div>

                <!-- ARTICLE 3 -->
                <div class="tcd-article">
                    <div class="tcd-article-title">
                        <i class="bi bi-cash-coin me-2"></i>
                        ARTICLE 3: RENTAL FEE AND PAYMENT
                    </div>

                    <div class="tcd-line">
                        <span class="tcd-label">
                            <i class="bi bi-wallet2 me-1"></i> Monthly Rent:
                        </span>
                        <span class="tcd-value">
                            <fmt:formatNumber value="${c.monthlyRent}" type="number" groupingUsed="true"/> ₫
                        </span>
                    </div>

                    <div class="tcd-line">
                        <span class="tcd-label">
                            <i class="bi bi-safe2 me-1"></i> Security Deposit:
                        </span>
                        <span class="tcd-value">
                            <fmt:formatNumber value="${c.deposit}" type="number" groupingUsed="true"/> ₫
                        </span>
                    </div>

                    <div class="tcd-line">
                        <span class="tcd-label">
                            <i class="bi bi-bank me-1"></i> Payment Method:
                        </span>
                        <span class="tcd-value">Bank transfer or cash</span>
                    </div>

                    <div class="tcd-note">
                        <i class="bi bi-exclamation-circle me-1"></i>
                        Note: Electricity and water charges are calculated separately based on actual usage.
                    </div>
                </div>

                <!-- Payment alerts -->
                <c:if test="${param.sent == '1'}">
                    <div class="tcd-alert tcd-alert-success">
                        <i class="bi bi-check-circle me-2"></i>
                        Đã gửi xác nhận chuyển khoản. Vui lòng chờ manager duyệt.
                    </div>
                </c:if>

                <c:if test="${param.already == '1'}">
                    <div class="tcd-alert tcd-alert-warning">
                        <i class="bi bi-exclamation-triangle me-2"></i>
                        Bạn đã xác nhận chuyển khoản trước đó rồi.
                    </div>
                </c:if>

                <c:if test="${param.err == '1' and empty param.msg}">
                    <div class="tcd-alert tcd-alert-danger">
                        <i class="bi bi-x-circle me-2"></i>
                        Lỗi khi xác nhận chuyển khoản. Thử lại sau.
                    </div>
                </c:if>

                <!-- Payment QR + confirm transfer -->
                <c:if test="${c.status eq 'PENDING'}">
                    <div class="tcd-divider"></div>

                    <div class="tcd-pay-wrap">
                        <div>
                            <div class="tcd-pay-title">
                                <i class="bi bi-qr-code me-2"></i>
                                Payment QR
                            </div>

                            <div class="tcd-pay-sub">
                                Quét QR để chuyển khoản tiền cọc (deposit).
                            </div>

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
                                        <i class="bi bi-exclamation-triangle me-2"></i>
                                        Contract chưa có QR. Vui lòng liên hệ manager.
                                    </div>
                                </c:otherwise>
                            </c:choose>

                            <c:if test="${not empty latestPayment}">
                                <div class="tcd-pay-latest">
                                    <i class="bi bi-arrow-repeat me-2"></i>
                                    Latest transfer status:
                                    <span class="tcd-pill"><c:out value="${latestPayment.status}"/></span>
                                </div>
                            </c:if>
                        </div>

                        <div>
                            <div class="tcd-pay-title">
                                <i class="bi bi-shield-check me-2"></i>
                                Confirm transfer
                            </div>

                            <div class="tcd-pay-sub">
                                Sau khi chuyển khoản, bấm xác nhận để manager kiểm tra và duyệt hợp đồng.
                            </div>

                            <form class="tcd-form"
                                  method="post"
                                  action="${pageContext.request.contextPath}/tenant/contract/confirm-transfer">

                                <input type="hidden" name="contractId" value="${c.contractId}"/>

                                <button class="tcd-btn"
                                        type="submit"
                                        onclick="return confirm('Bạn chắc chắn đã chuyển khoản tiền cọc chưa?');">
                                    <i class="bi bi-check2-circle me-2"></i>
                                    Tôi đã chuyển khoản
                                </button>

                                <div class="tcd-hint">
                                    <i class="bi bi-info-circle me-1"></i>
                                    Lưu ý: bấm nhiều lần sẽ không tạo thêm payment mới.
                                </div>
                            </form>
                        </div>
                    </div>
                </c:if>

            </div>
        </div>
    </div>

</layout:layout>