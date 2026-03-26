<%-- 
    Document   : detail room premium
    Created on : 02/06/2026, 6:22:57 AM
    Author     : Duong Thien Nhan - CE190741
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<t:layout-fragment cssFile="${pageContext.request.contextPath}/assets/css/views/roomDetail.css">
    <c:set var="r" value="${room}" />
    <c:set var="ctx" value="${pageContext.request.contextPath}" />

    <div class="rd-page">

        <!-- HEADER -->
        <div class="rd-head rd-reveal">
            <div class="rd-head__left">
                <span class="rd-badge-top">
                    <i class="bi bi-house-door-fill"></i>
                    Room Information
                </span>

                <h2 class="rd-title">Room Details - ${r.roomNumber}</h2>

                <div class="rd-sub">
                    Complete information about this room including features, amenities, images, and pricing.
                </div>

                <div class="rd-meta-line">
                    <span><i class="bi bi-hash"></i> ID: ${r.roomNumber}</span>
                    <span><i class="bi bi-grid-1x2-fill"></i> ${r.blockName}</span>
                    <span><i class="bi bi-bounding-box"></i> ${r.area} m²</span>
                </div>
            </div>

            <div class="rd-head__right">
                <div class="rd-status-chip status-${fn:toLowerCase(r.status)}">
                    <i class="bi bi-check-circle-fill"></i>
                    <span>${r.status}</span>
                </div>
            </div>
        </div>

        <!-- HERO -->
        <div class="rd-hero">
            <!-- LEFT: GALLERY -->
            <div class="rd-gallery-card rd-reveal">
                <div class="rd-image-wrap">
                    <div class="rd-image">
                        <div class="rd-fade rd-fade-left"></div>
                        <div class="rd-fade rd-fade-right"></div>
                        <div class="rd-image-orb rd-image-orb--one"></div>
                        <div class="rd-image-orb rd-image-orb--two"></div>

                        <c:choose>
                            <c:when test="${not empty r.images}">
                                <img id="mainImg"
                                     class="rd-main"
                                     src="${ctx}/assets/images/rooms/${r.images[0].imageUrl}"
                                     alt="Room image">

                                <div class="rd-badge status-${fn:toLowerCase(r.status)}">${r.status}</div>

                                <button type="button" class="rd-gallery-nav rd-gallery-prev" id="rdPrevBtn">
                                    <i class="bi bi-chevron-left"></i>
                                </button>

                                <button type="button" class="rd-gallery-nav rd-gallery-next" id="rdNextBtn">
                                    <i class="bi bi-chevron-right"></i>
                                </button>

                                <button type="button" class="rd-zoom-btn" id="rdZoomBtn" aria-label="Zoom image">
                                    <i class="bi bi-arrows-fullscreen"></i>
                                </button>

                                <div class="rd-image-bottom">
                                    <div class="rd-image-counter">
                                        <i class="bi bi-images"></i>
                                        <span id="rdImageCounter">1 / ${fn:length(r.images)}</span>
                                    </div>

                                    <div class="rd-thumbs" id="rdThumbs">
                                        <c:forEach var="img" items="${r.images}" varStatus="loop">
                                            <img class="rd-thumb ${loop.index == 0 ? 'is-active' : ''}"
                                                 src="${ctx}/assets/images/rooms/${img.imageUrl}"
                                                 alt="Room image ${loop.index + 1}"
                                                 data-index="${loop.index}">
                                        </c:forEach>
                                    </div>
                                </div>
                            </c:when>

                            <c:otherwise>
                                <c:choose>
                                    <c:when test="${not empty r.roomImage}">
                                        <img id="mainImg"
                                             class="rd-main"
                                             src="${ctx}/assets/images/rooms/${r.roomImage}"
                                             alt="Room image">

                                        <div class="rd-badge status-${fn:toLowerCase(r.status)}">${r.status}</div>

                                        <button type="button" class="rd-zoom-btn" id="rdZoomBtn" aria-label="Zoom image">
                                            <i class="bi bi-arrows-fullscreen"></i>
                                        </button>
                                    </c:when>

                                    <c:otherwise>
                                        <div class="rd-image-placeholder">No Image</div>
                                        <div class="rd-badge status-${fn:toLowerCase(r.status)}">${r.status}</div>
                                    </c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- RIGHT: INFO -->
            <div class="rd-side">
                <div class="rd-price-card rd-reveal">
                    <div class="rd-price-card__icon">
                        <i class="bi bi-cash-stack"></i>
                    </div>

                    <div class="rd-price-card__label">Monthly Price</div>

                    <div class="rd-price-value">
                        <fmt:formatNumber value="${r.price}" type="number" groupingUsed="true"/> đ/month
                    </div>

                    <div class="rd-price-sub">
                        Transparent room pricing for monthly rental.
                    </div>

                    <div class="rd-price-tags">
                        <span><i class="bi bi-shield-check"></i> Premium listing</span>
                        <span><i class="bi bi-lightning-charge"></i> Instant overview</span>
                    </div>
                </div>

                <div class="rd-quick-grid">
                    <div class="rd-quick-card rd-reveal">
                        <i class="bi bi-bounding-box"></i>
                        <span>Area</span>
                        <strong>${r.area} m²</strong>
                    </div>

                    <div class="rd-quick-card rd-reveal">
                        <i class="bi bi-people-fill"></i>
                        <span>Capacity</span>
                        <strong>${r.maxTenants}</strong>
                    </div>

                    <div class="rd-quick-card rd-reveal">
                        <i class="bi bi-layers-fill"></i>
                        <span>Floor</span>
                        <strong><c:out value="${r.floor}" /></strong>
                    </div>

                    <div class="rd-quick-card rd-reveal">
                        <i class="bi bi-grid-1x2-fill"></i>
                        <span>Block</span>
                        <strong>${r.blockName}</strong>
                    </div>
                </div>

                <div class="rd-action-card rd-reveal">
                    <div class="rd-action-card__icon">
                        <i class="bi bi-lightning-charge-fill"></i>
                    </div>

                    <h3>Quick Actions</h3>

                    <p>
                        Explore room information, check the rental details, and contact management
                        if you need more support or consultation.
                    </p>

                    <div class="rd-action-buttons">
                        <a href="${ctx}/contact" class="rd-action-btn rd-action-btn--primary">
                            <i class="bi bi-envelope-fill"></i>
                            Contact Manager
                        </a>

                        <button type="button"
                                class="rd-action-btn rd-action-btn--secondary"
                                id="rdCopyRoomBtn"
                                data-room-number="${r.roomNumber}">
                            <i class="bi bi-copy"></i>
                            Copy Room ID
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- INFO GRID -->
        <div class="rd-grid">
            <div class="rd-card rd-reveal">
                <div class="rd-card-title">
                    <i class="bi bi-info-circle-fill"></i>
                    <span>Basic Information</span>
                </div>

                <div class="rd-row">
                    <span>Room</span>
                    <b>${r.roomNumber}</b>
                </div>

                <div class="rd-row">
                    <span>Block</span>
                    <b>${r.blockName}</b>
                </div>

                <div class="rd-row">
                    <span>Area</span>
                    <b>${r.area} m²</b>
                </div>

                <div class="rd-row">
                    <span>Floor</span>
                    <b><c:out value="${r.floor}" /></b>
                </div>

                <div class="rd-row">
                    <span>Maximum occupancy</span>
                    <b><c:out value="${r.maxTenants}" /></b>
                </div>

                <div class="rd-row">
                    <span>Status</span>
                    <b>${r.status}</b>
                </div>
            </div>

            <div class="rd-card rd-reveal">
                <div class="rd-card-title">
                    <i class="bi bi-stars"></i>
                    <span>Room Features</span>
                </div>

                <div class="rd-row">
                    <span>Air Conditioner</span>
                    <b>
                        <c:if test="${r.airConditioning}">Yes</c:if>
                        <c:if test="${!r.airConditioning}">No</c:if>
                    </b>
                </div>

                <div class="rd-row">
                    <span>Mezzanine</span>
                    <b>
                        <c:if test="${r.mezzanine}">Yes</c:if>
                        <c:if test="${!r.mezzanine}">No</c:if>
                    </b>
                </div>

                <div class="rd-feature-pills">
                    <span class="rd-pill ${r.airConditioning ? 'is-on' : 'is-off'}">
                        <i class="bi bi-snow"></i>
                        AC
                    </span>

                    <span class="rd-pill ${r.mezzanine ? 'is-on' : 'is-off'}">
                        <i class="bi bi-layers-fill"></i>
                        Mezzanine
                    </span>
                </div>
            </div>
        </div>

        <!-- DESCRIPTION -->
        <c:if test="${not empty r.description}">
            <div class="rd-desc-wrap rd-reveal">
                <div class="rd-desc-title">
                    <i class="bi bi-file-earmark-text-fill"></i>
                    <span>Description</span>
                </div>
                <div class="rd-desc">${r.description}</div>
            </div>
        </c:if>
    </div>

    <!-- LIGHTBOX -->
    <div class="rd-lightbox" id="rdLightbox" aria-hidden="true">
        <div class="rd-lightbox__backdrop"></div>
        <div class="rd-lightbox__dialog">
            <button type="button" class="rd-lightbox__close" id="rdLightboxClose">✕</button>
            <img id="rdLightboxImg" src="" alt="Zoomed room image">
        </div>
    </div>

</t:layout-fragment>