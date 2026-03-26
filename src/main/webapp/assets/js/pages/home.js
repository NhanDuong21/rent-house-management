(function () {
  const init = window.RH_INIT || {};
  const ctx = init.ctx || "";
  const reduceMotion = window.matchMedia(
    "(prefers-reduced-motion: reduce)",
  ).matches;

  function qs(selector, root = document) {
    return root.querySelector(selector);
  }

  function qsa(selector, root = document) {
    return Array.from(root.querySelectorAll(selector));
  }

  function clampPair(minEl, maxEl) {
    const minV = Number(minEl.value);
    const maxV = Number(maxEl.value);
    if (minV > maxV) {
      minEl.value = maxV;
    }
  }

  function formatVND(n) {
    try {
      return Number(n).toLocaleString("vi-VN") + " đ";
    } catch (e) {
      return n + " đ";
    }
  }

  /* =========================
   * FILTER MODAL
   * ========================= */
  const filterModal = qs("#filterModal");
  const openBtn = qs("#btnOpenFilter");
  const openBtnHero = qs("#btnOpenFilterHero");
  const closeBtn = qs("#btnCloseFilter");
  const backdrop = qs("#filterBackdrop");

  function openModal() {
    if (!filterModal) return;
    filterModal.classList.add("show");
    filterModal.setAttribute("aria-hidden", "false");
    document.body.style.overflow = "hidden";
  }

  function closeModal() {
    if (!filterModal) return;
    filterModal.classList.remove("show");
    filterModal.setAttribute("aria-hidden", "true");
    document.body.style.overflow = "";
  }

  if (openBtn) openBtn.addEventListener("click", openModal);
  if (openBtnHero) openBtnHero.addEventListener("click", openModal);
  if (closeBtn) closeBtn.addEventListener("click", closeModal);
  if (backdrop) backdrop.addEventListener("click", closeModal);

  /* =========================
   * DUAL RANGE - PRICE
   * ========================= */
  const priceMin = qs("#priceMin");
  const priceMax = qs("#priceMax");
  const priceMinText = qs("#priceMinText");
  const priceMaxText = qs("#priceMaxText");
  const minPriceHidden = qs("#minPriceHidden");
  const maxPriceHidden = qs("#maxPriceHidden");

  function syncPrice() {
    if (!priceMin || !priceMax) return;
    clampPair(priceMin, priceMax);

    if (priceMinText) priceMinText.textContent = formatVND(priceMin.value);
    if (priceMaxText) priceMaxText.textContent = formatVND(priceMax.value);
    if (minPriceHidden) minPriceHidden.value = priceMin.value;
    if (maxPriceHidden) maxPriceHidden.value = priceMax.value;
  }

  if (priceMin) priceMin.addEventListener("input", syncPrice);
  if (priceMax) priceMax.addEventListener("input", syncPrice);
  syncPrice();

  /* =========================
   * DUAL RANGE - AREA
   * ========================= */
  const areaMin = qs("#areaMin");
  const areaMax = qs("#areaMax");
  const areaMinText = qs("#areaMinText");
  const areaMaxText = qs("#areaMaxText");
  const minAreaHidden = qs("#minAreaHidden");
  const maxAreaHidden = qs("#maxAreaHidden");

  function syncArea() {
    if (!areaMin || !areaMax) return;
    clampPair(areaMin, areaMax);

    if (areaMinText) areaMinText.textContent = areaMin.value + " m²";
    if (areaMaxText) areaMaxText.textContent = areaMax.value + " m²";
    if (minAreaHidden) minAreaHidden.value = areaMin.value;
    if (maxAreaHidden) maxAreaHidden.value = areaMax.value;
  }

  if (areaMin) areaMin.addEventListener("input", syncArea);
  if (areaMax) areaMax.addEventListener("input", syncArea);
  syncArea();

  /* =========================
   * CHOICE GROUPS
   * ========================= */
  function initChoiceGroup(groupSelector, hiddenId, initValue) {
    const group = qs(groupSelector);
    const hidden = qs("#" + hiddenId);
    if (!group || !hidden) return;

    function setActive(val) {
      hidden.value = val;
      qsa(".choice", group).forEach((btn) => {
        btn.classList.toggle("active", btn.dataset.value === val);
      });
    }

    qsa(".choice", group).forEach((btn) => {
      btn.addEventListener("click", () => setActive(btn.dataset.value));
    });

    setActive(initValue || hidden.value || "any");
  }

  initChoiceGroup(
    '.choice-group[data-target="hasAC"]',
    "hasACHidden",
    init.hasAC,
  );

  initChoiceGroup(
    '.choice-group[data-target="hasMezzanine"]',
    "hasMezzHidden",
    init.hasMezzanine,
  );

  /* =========================
   * ROOM DETAIL MODAL
   * ========================= */
  const detailModal = qs("#roomDetailModal");
  const detailBackdrop = qs("#roomDetailBackdrop");
  const detailClose = qs("#roomDetailClose");
  const detailBody = qs("#roomDetailBody");

  function fancyLoadingHtml() {
    return `
      <div class="room-detail-loading room-detail-loading--fancy">
        <div class="rd-loading-spinner"></div>
        <div class="rd-loading-text">Loading room details...</div>
        <div class="rd-loading-sub">Please wait a moment</div>
      </div>
    `;
  }

  function openRoomModal() {
    if (!detailModal) return;
    detailModal.classList.add("show");
    detailModal.setAttribute("aria-hidden", "false");
    document.body.style.overflow = "hidden";
  }

  function closeRoomModal() {
    if (!detailModal) return;
    detailModal.classList.remove("show");
    detailModal.setAttribute("aria-hidden", "true");
    document.body.style.overflow = "";
  }

  if (detailBackdrop) detailBackdrop.addEventListener("click", closeRoomModal);
  if (detailClose) detailClose.addEventListener("click", closeRoomModal);

  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") {
      closeModal();
      closeRoomModal();
    }
  });

  document.addEventListener("click", async (e) => {
    const btn = e.target.closest(".js-room-detail");
    if (!btn) return;

    const roomId = btn.dataset.roomId;
    openRoomModal();

    if (detailBody) {
      detailBody.innerHTML = fancyLoadingHtml();
      detailBody.scrollTop = 0;
    }

    try {
      const url = `${ctx}/room-detail?id=${encodeURIComponent(roomId)}`;
      const res = await fetch(url, {
        headers: { "X-Requested-With": "XMLHttpRequest" },
      });

      if (!res.ok) throw new Error(res.status);

      const html = await res.text();

      if (detailBody) {
        if (typeof detailBody.__roomDetailCleanup === "function") {
          detailBody.__roomDetailCleanup();
          detailBody.__roomDetailCleanup = null;
        }

        detailBody.innerHTML = html;

        if (typeof window.initRoomDetail === "function") {
          window.initRoomDetail(detailBody);
        }

        injectRoomDetailReveal(detailBody);
      }
    } catch (err) {
      if (detailBody) {
        detailBody.innerHTML = `
          <div class="room-detail-error">
            Không load được chi tiết phòng.
          </div>
        `;
      }
      console.error("detail error:", err);
    }
  });

  function injectRoomDetailReveal(root) {
    if (!root || reduceMotion) return;
    const items = qsa(".rd-reveal", root);

    items.forEach((el, index) => {
      el.style.opacity = "0";
      el.style.transform = "translateY(18px) scale(0.985)";
      el.style.transition =
        "opacity 0.62s cubic-bezier(0.22,1,0.36,1), transform 0.62s cubic-bezier(0.22,1,0.36,1)";
      el.style.transitionDelay = `${Math.min(index * 0.06, 0.3)}s`;
    });

    requestAnimationFrame(() => {
      items.forEach((el) => {
        el.style.opacity = "1";
        el.style.transform = "translateY(0) scale(1)";
      });
    });
  }

  /* =========================
   * HERO SLIDER
   * ========================= */
  (function initHeroSlider() {
    const hero = qs("#homeHero");
    if (!hero) return;

    const slides = qsa(".hero-slide", hero);
    const dots = qsa(".dot", hero);
    const prevBtn = qs(".hero-nav.prev", hero);
    const nextBtn = qs(".hero-nav.next", hero);

    if (slides.length <= 1) return;

    let idx = 0;
    let timer = null;
    const intervalMs = 4200;

    function render() {
      slides.forEach((s, i) => s.classList.toggle("is-active", i === idx));
      dots.forEach((d, i) => d.classList.toggle("is-active", i === idx));
    }

    function go(n) {
      idx = (n + slides.length) % slides.length;
      render();
    }

    function next() {
      go(idx + 1);
    }

    function prev() {
      go(idx - 1);
    }

    function start() {
      if (reduceMotion) return;
      stop();
      timer = setInterval(next, intervalMs);
    }

    function stop() {
      if (timer) clearInterval(timer);
      timer = null;
    }

    if (nextBtn) {
      nextBtn.addEventListener("click", () => {
        next();
        start();
      });
    }

    if (prevBtn) {
      prevBtn.addEventListener("click", () => {
        prev();
        start();
      });
    }

    dots.forEach((d) => {
      d.addEventListener("click", () => {
        const n = Number(d.dataset.index || 0);
        go(n);
        start();
      });
    });

    hero.addEventListener("mouseenter", stop);
    hero.addEventListener("mouseleave", start);

    let x0 = null;
    hero.addEventListener(
      "touchstart",
      (e) => {
        x0 = e.touches && e.touches[0] ? e.touches[0].clientX : null;
      },
      { passive: true },
    );

    hero.addEventListener("touchend", (e) => {
      if (x0 == null) return;
      const x1 =
        e.changedTouches && e.changedTouches[0]
          ? e.changedTouches[0].clientX
          : null;
      if (x1 == null) return;

      const dx = x1 - x0;
      if (Math.abs(dx) > 40) {
        if (dx < 0) next();
        else prev();
        start();
      }
      x0 = null;
    });

    render();
    start();
  })();

  /* =========================
   * CARD 3D HOVER
   * ========================= */
  (function initCardTilt() {
    if (reduceMotion) return;

    const cards = qsa(".room-card");

    cards.forEach((card) => {
      function reset() {
        card.style.transform = "";
      }

      card.addEventListener("mousemove", (e) => {
        const rect = card.getBoundingClientRect();
        const px = (e.clientX - rect.left) / rect.width;
        const py = (e.clientY - rect.top) / rect.height;

        const rotateY = (px - 0.5) * 8;
        const rotateX = (0.5 - py) * 8;

        card.style.transform = `translateY(-10px) rotateX(${rotateX}deg) rotateY(${rotateY}deg)`;
      });

      card.addEventListener("mouseleave", reset);
      card.addEventListener("blur", reset, true);
    });
  })();

  /* =========================
   * REVEAL ANIMATION
   * ========================= */
  const revealTargets = [
    ...qsa(".home-head"),
    ...qsa(".room-card"),
    ...qsa(".hero-stat"),
    ...qsa(".pagination a, .pagination span"),
    ...qsa(".home-empty"),
  ];

  if (!reduceMotion) {
    revealTargets.forEach((el, index) => {
      el.style.opacity = "0";
      el.style.transform = "translateY(30px) scale(0.985)";
      el.style.transition =
        "opacity 0.8s cubic-bezier(0.22,1,0.36,1), transform 0.8s cubic-bezier(0.22,1,0.36,1)";
      el.style.transitionDelay = `${Math.min(index * 0.035, 0.28)}s`;
    });

    const revealObserver = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (!entry.isIntersecting) return;
          entry.target.style.opacity = "1";
          entry.target.style.transform = "translateY(0) scale(1)";
          revealObserver.unobserve(entry.target);
        });
      },
      { threshold: 0.12 },
    );

    revealTargets.forEach((el) => revealObserver.observe(el));
  }

  /* =========================
   * HERO PARALLAX
   * ========================= */
  (function initHeroParallax() {
    if (reduceMotion) return;

    const hero = qs("#homeHero");
    const content = qs(".home-hero__content", hero);
    if (!hero || !content) return;

    hero.addEventListener("mousemove", (e) => {
      const rect = hero.getBoundingClientRect();
      const x = (e.clientX - rect.left) / rect.width - 0.5;
      const y = (e.clientY - rect.top) / rect.height - 0.5;

      content.style.transform = `translate3d(${x * 10}px, ${y * 10}px, 0)`;
    });

    hero.addEventListener("mouseleave", () => {
      content.style.transform = "";
    });
  })();
})();
