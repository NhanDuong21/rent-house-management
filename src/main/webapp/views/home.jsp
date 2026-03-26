<%-- 
    Document   : home page (premium unified UI, keep old logic)
    Created on : 02/06/2026, 6:22:57 AM
    Author     : Duong Thien Nhan - CE190741
--%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<t:layout
    title="Home - RentHouse"
    active="home"
    cssFile="${pageContext.request.contextPath}/assets/css/views/home.css"
    jsFile="${pageContext.request.contextPath}/assets/js/pages/home.js">

    <c:set var="ctx" value="${pageContext.request.contextPath}" />

    <!-- defaults -->
    <c:set var="PRICE_MIN" value="0" />
    <c:set var="PRICE_MAX" value="10000000" />
    <c:set var="AREA_MIN" value="0" />
    <c:set var="AREA_MAX" value="50" />

    <c:set var="minPrice" value="${empty minPrice ? PRICE_MIN : minPrice}" />
    <c:set var="maxPrice" value="${empty maxPrice ? PRICE_MAX : maxPrice}" />
    <c:set var="minArea"  value="${empty minArea  ? AREA_MIN  : minArea}" />
    <c:set var="maxArea"  value="${empty maxArea  ? AREA_MAX  : maxArea}" />

    <%-- =========================================
         ROLE (scriptless) - fallback like layout.tag
         ========================================= --%>
    <c:set var="role" value="GUEST" />

    <c:choose>
        <c:when test="${empty sessionScope.auth}">
            <c:set var="role" value="GUEST" />
        </c:when>

        <c:when test="${not empty sessionScope.auth.role}">
            <c:set var="role" value="${sessionScope.auth.role}" />
        </c:when>

        <c:otherwise>
            <c:choose>
                <c:when test="${not empty sessionScope.auth.staff}">
                    <c:choose>
                        <c:when test="${not empty sessionScope.auth.staff.staffRole}">
                            <c:set var="role" value="${sessionScope.auth.staff.staffRole}" />
                        </c:when>
                        <c:otherwise>
                            <c:set var="role" value="STAFF" />
                        </c:otherwise>
                    </c:choose>
                </c:when>

                <c:when test="${not empty sessionScope.auth.tenant}">
                    <c:set var="role" value="TENANT" />
                </c:when>

                <c:otherwise>
                    <c:set var="role" value="GUEST" />
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>

    <div class="home-shell">
        <!-- HERO -->
        <section class="home-hero" id="homeHero" aria-label="Hero Banner">
            <div class="home-hero__overlay"></div>
            <div class="home-hero__mesh"></div>
            <div class="home-hero__shine"></div>

            <div class="hero-track">
                <div class="hero-slide is-active">
                    <img src="${ctx}/assets/images/banners/banner1.jpg" alt="Hero banner 1">
                </div>
                <div class="hero-slide">
                    <img src="${ctx}/assets/images/banners/banner2.jpg" alt="Hero banner 2">
                </div>
            </div>

            <div class="home-hero__content">
                <div class="home-hero__text hero-reveal">
                    <span class="home-badge">
                        <i class="bi bi-buildings-fill"></i>
                        Rental House Management
                    </span>

                    <c:choose>
                        <c:when test="${role == 'GUEST' || role == 'TENANT'}">
                            <h1>Find Your Perfect Room</h1>
                            <p>
                                Discover available rooms with transparent pricing, practical facilities,
                                and comfortable living spaces tailored to your needs.
                            </p>
                        </c:when>
                        <c:otherwise>
                            <h1>Manage All Rooms Efficiently</h1>
                            <p>
                                Track room availability, pricing, facilities, and room status
                                through a clean and organized management interface.
                            </p>
                        </c:otherwise>
                    </c:choose>

                    <div class="home-hero__actions">
                        <button class="home-hero-btn home-hero-btn--light" type="button" id="btnOpenFilterHero">
                            <i class="bi bi-funnel-fill"></i>
                            Filter Rooms
                        </button>
                        <a href="#roomListSection" class="home-hero-btn home-hero-btn--ghost">
                            <i class="bi bi-grid-1x2-fill"></i>
                            Browse Rooms
                        </a>
                    </div>
                </div>

                <div class="home-hero__stats hero-reveal hero-reveal--delay">
                    <div class="hero-stat hero-stat--main">
                        <div class="hero-stat__icon"><i class="bi bi-house-door-fill"></i></div>
                        <div class="hero-stat__value">${empty rooms ? 0 : totalItems}</div>
                        <div class="hero-stat__label">Rooms In System</div>
                    </div>

                    <div class="hero-stat-row">
                        <div class="hero-stat">
                            <div class="hero-stat__icon"><i class="bi bi-lightning-charge-fill"></i></div>
                            <div class="hero-stat__value">Fast</div>
                            <div class="hero-stat__label">Filtering</div>
                        </div>

                        <div class="hero-stat">
                            <div class="hero-stat__icon"><i class="bi bi-shield-check"></i></div>
                            <div class="hero-stat__value">Smart</div>
                            <div class="hero-stat__label">Management</div>
                        </div>
                    </div>
                </div>
            </div>

            <button class="hero-nav prev" type="button" aria-label="Previous slide">‹</button>
            <button class="hero-nav next" type="button" aria-label="Next slide">›</button>

            <div class="hero-dots" aria-label="Hero dots">
                <button type="button" class="dot is-active" data-index="0" aria-label="Slide 1"></button>
                <button type="button" class="dot" data-index="1" aria-label="Slide 2"></button>
            </div>

            <div class="hero-scroll-indicator">
                <span></span>
            </div>
        </section>

        <!-- HEAD -->
        <section class="home-head" id="roomListSection">
            <div>
                <c:choose>
                    <c:when test="${role == 'GUEST' || role == 'TENANT'}">
                        <h2 class="home-title">Available Rooms</h2>
                        <div class="home-sub">Browse our collection of available rooms and find your perfect home</div>
                    </c:when>
                    <c:otherwise>
                        <h2 class="home-title">All Rooms</h2>
                        <div class="home-sub">Manage and monitor all rooms in the system</div>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="home-actions">
                <div class="home-count">
                    <c:choose>
                        <c:when test="${empty rooms}">0 rooms</c:when>
                        <c:otherwise>${totalItems} rooms</c:otherwise>
                    </c:choose>
                </div>

                <button class="home-filter-btn" type="button" id="btnOpenFilter">
                    <i class="bi bi-sliders"></i>
                    <span>Filter</span>
                </button>
            </div>
        </section>

        <!-- ROOMS GRID -->
        <c:choose>
            <c:when test="${empty rooms}">
                <div class="home-empty">
                    <div class="home-empty__icon">
                        <i class="bi bi-house-x-fill"></i>
                    </div>
                    <div class="home-empty__title">No matching rooms found</div>
                    <div class="home-empty__desc">Try adjusting your filters to see more available rooms.</div>
                </div>
            </c:when>

            <c:otherwise>
                <div class="room-grid">
                    <c:forEach var="r" items="${rooms}">
                        <div class="room-card room-reveal">
                            <div class="room-card__glow"></div>
                            <div class="room-card__shine"></div>

                            <div class="room-img">
                                <c:choose>
                                    <c:when test="${not empty r.roomImage}">
                                        <img
                                            src="${ctx}/assets/images/rooms/${r.roomImage}"
                                            alt="Room"
                                            onerror="this.onerror=null; this.style.display='none'; this.parentElement.innerHTML='<div class=&quot;room-img-placeholder&quot;>No Image</div>';"/>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="room-img-placeholder">No Image</div>
                                    </c:otherwise>
                                </c:choose>

                                <div class="room-badge status-${fn:toLowerCase(r.status)}">
                                    ${r.status}
                                </div>
                            </div>

                            <div class="room-body">
                                <div class="room-topline">
                                    <div class="room-name">${r.roomNumber}</div>
                                    <div class="room-price">
                                        <fmt:formatNumber value="${r.price}" type="number" groupingUsed="true"/> đ/month
                                    </div>
                                </div>

                                <div class="room-meta-grid">
                                    <span><i class="bi bi-bounding-box"></i> ${r.area} m²</span>
                                    <c:if test="${not empty r.floor}">
                                        <span><i class="bi bi-layers-fill"></i> Floor ${r.floor}</span>
                                    </c:if>
                                    <span><i class="bi bi-people-fill"></i> Max ${r.maxTenants}</span>
                                    <c:if test="${not empty r.blockName}">
                                        <span><i class="bi bi-grid-1x2-fill"></i> ${r.blockName}</span>
                                    </c:if>
                                </div>

                                <div class="room-tags">
                                    <c:if test="${r.airConditioning}">
                                        <span class="tag tag--good">AC</span>
                                    </c:if>
                                    <c:if test="${r.mezzanine}">
                                        <span class="tag tag--info">Mezzanine</span>
                                    </c:if>
                                    <c:if test="${not r.airConditioning and not r.mezzanine}">
                                        <span class="tag">Standard</span>
                                    </c:if>
                                </div>

                                <c:if test="${not empty r.description}">
                                    <div class="room-desc">${r.description}</div>
                                </c:if>

                                <button class="room-btn js-room-detail" type="button" data-room-id="${r.roomId}">
                                    <span>View Details</span>
                                    <i class="bi bi-arrow-right-short"></i>
                                </button>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <!-- PAGINATION -->
                <c:if test="${totalPages > 1}">
                    <c:url value="/home" var="baseUrl">
                        <c:param name="minPrice" value="${param.minPrice}" />
                        <c:param name="maxPrice" value="${param.maxPrice}" />
                        <c:param name="minArea" value="${param.minArea}" />
                        <c:param name="maxArea" value="${param.maxArea}" />
                        <c:param name="hasAC" value="${param.hasAC}" />
                        <c:param name="hasMezzanine" value="${param.hasMezzanine}" />
                    </c:url>

                    <c:set var="window" value="2" />
                    <c:set var="start" value="${page - window}" />
                    <c:set var="end" value="${page + window}" />

                    <c:if test="${start < 2}">
                        <c:set var="start" value="2" />
                    </c:if>
                    <c:if test="${end > totalPages - 1}">
                        <c:set var="end" value="${totalPages - 1}" />
                    </c:if>

                    <div class="pagination">
                        <c:if test="${page > 1}">
                            <a href="${baseUrl}&page=${page-1}">Prev</a>
                        </c:if>

                        <c:choose>
                            <c:when test="${page == 1}">
                                <span class="active">1</span>
                            </c:when>
                            <c:otherwise>
                                <a href="${baseUrl}&page=1">1</a>
                            </c:otherwise>
                        </c:choose>

                        <c:if test="${start > 2}">
                            <span class="dots">…</span>
                        </c:if>

                        <c:forEach begin="${start}" end="${end}" var="i">
                            <c:if test="${i >= 2 && i <= totalPages-1}">
                                <c:choose>
                                    <c:when test="${i == page}">
                                        <span class="active">${i}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${baseUrl}&page=${i}">${i}</a>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                        </c:forEach>

                        <c:if test="${end < totalPages - 1}">
                            <span class="dots">…</span>
                        </c:if>

                        <c:if test="${totalPages > 1}">
                            <c:choose>
                                <c:when test="${page == totalPages}">
                                    <span class="active">${totalPages}</span>
                                </c:when>
                                <c:otherwise>
                                    <a href="${baseUrl}&page=${totalPages}">${totalPages}</a>
                                </c:otherwise>
                            </c:choose>
                        </c:if>

                        <c:if test="${page < totalPages}">
                            <a href="${baseUrl}&page=${page+1}">Next</a>
                        </c:if>
                    </div>
                </c:if>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- FILTER MODAL -->
    <div class="rh-modal" id="filterModal" aria-hidden="true">
        <div class="rh-modal-backdrop" id="filterBackdrop"></div>

        <div class="rh-modal-dialog" role="dialog" aria-modal="true">
            <div class="rh-modal-header">
                <div>
                    <div class="rh-modal-title">Filter Rooms</div>
                    <div class="rh-modal-sub">Set your preferences to find the perfect room</div>
                </div>
                <button class="rh-modal-close" type="button" id="btnCloseFilter">✕</button>
            </div>

            <form method="get" action="${ctx}/home" class="rh-modal-body">

                <div class="filter-block">
                    <div class="filter-row">
                        <div class="filter-label">Price Range</div>
                        <div class="filter-value">
                            <span id="priceMinText"></span> - <span id="priceMaxText"></span>
                        </div>
                    </div>

                    <div class="dual-range">
                        <input type="range" id="priceMin" min="${PRICE_MIN}" max="${PRICE_MAX}" step="50000" value="${minPrice}">
                        <input type="range" id="priceMax" min="${PRICE_MIN}" max="${PRICE_MAX}" step="50000" value="${maxPrice}">
                    </div>

                    <input type="hidden" name="minPrice" id="minPriceHidden" value="${minPrice}">
                    <input type="hidden" name="maxPrice" id="maxPriceHidden" value="${maxPrice}">
                </div>

                <div class="filter-block">
                    <div class="filter-row">
                        <div class="filter-label">Area (m²)</div>
                        <div class="filter-value">
                            <span id="areaMinText"></span> - <span id="areaMaxText"></span>
                        </div>
                    </div>

                    <div class="dual-range">
                        <input type="range" id="areaMin" min="${AREA_MIN}" max="${AREA_MAX}" step="1" value="${minArea}">
                        <input type="range" id="areaMax" min="${AREA_MIN}" max="${AREA_MAX}" step="1" value="${maxArea}">
                    </div>

                    <input type="hidden" name="minArea" id="minAreaHidden" value="${minArea}">
                    <input type="hidden" name="maxArea" id="maxAreaHidden" value="${maxArea}">
                </div>

                <div class="filter-block">
                    <div class="filter-label">Has Air Conditioning</div>
                    <div class="choice-group" data-target="hasAC">
                        <button type="button" class="choice" data-value="any">Any</button>
                        <button type="button" class="choice" data-value="yes">Yes</button>
                        <button type="button" class="choice" data-value="no">No</button>
                    </div>
                    <input type="hidden" name="hasAC" id="hasACHidden" value="${empty hasAC ? 'any' : hasAC}">
                </div>

                <div class="filter-block">
                    <div class="filter-label">Has Mezzanine</div>
                    <div class="choice-group" data-target="hasMezzanine">
                        <button type="button" class="choice" data-value="any">Any</button>
                        <button type="button" class="choice" data-value="yes">Yes</button>
                        <button type="button" class="choice" data-value="no">No</button>
                    </div>
                    <input type="hidden" name="hasMezzanine" id="hasMezzHidden" value="${empty hasMezzanine ? 'any' : hasMezzanine}">
                </div>

                <div class="rh-modal-footer">
                    <a class="btn-reset" href="${ctx}/home">Reset</a>
                    <button class="btn-apply" type="submit">Apply Filters</button>
                </div>
            </form>
        </div>
    </div>

    <!-- ROOM DETAIL MODAL -->
    <div class="rh-modal rh-modal--detail" id="roomDetailModal" aria-hidden="true">
        <div class="rh-modal-backdrop rh-modal-backdrop--detail" id="roomDetailBackdrop"></div>

        <div class="rh-modal-dialog room-detail-dialog room-detail-shell" role="dialog" aria-modal="true">
            <div class="room-detail-shell__topbar">
                <div class="room-detail-shell__label">
                    <i class="bi bi-stars"></i>
                    Premium Room Preview
                </div>

                <button class="rh-modal-close rh-modal-close-floating room-detail-shell__close" type="button" id="roomDetailClose">✕</button>
            </div>

            <div class="room-detail-shell__body">
                <div id="roomDetailBody" class="room-detail-body">
                    <div class="room-detail-loading room-detail-loading--fancy">
                        <div class="rd-loading-spinner"></div>
                        <div class="rd-loading-text">Loading room details...</div>
                        <div class="rd-loading-sub">Please wait a moment</div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- init for js -->
    <script>
        window.RH_INIT = {
            ctx: "${ctx}",
            hasAC: "${empty hasAC ? 'any' : hasAC}",
            hasMezzanine: "${empty hasMezzanine ? 'any' : hasMezzanine}"
        };
    </script>
    <script src="${pageContext.request.contextPath}/assets/js/pages/roomDetail.js"></script>
</t:layout>