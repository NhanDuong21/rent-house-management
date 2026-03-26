(function () {
  function initRoomDetail(root) {
    if (!root) return;

    const reduceMotion = window.matchMedia(
      "(prefers-reduced-motion: reduce)",
    ).matches;

    const mainImg = root.querySelector("#mainImg");
    const imageWrap = root.querySelector(".rd-image");
    const thumbs = Array.from(root.querySelectorAll(".rd-thumb"));
    const prevBtn = root.querySelector("#rdPrevBtn");
    const nextBtn = root.querySelector("#rdNextBtn");
    const zoomBtn = root.querySelector("#rdZoomBtn");
    const copyBtn = root.querySelector("#rdCopyRoomBtn");
    const lightbox = root.querySelector("#rdLightbox");
    const lightboxImg = root.querySelector("#rdLightboxImg");
    const lightboxClose = root.querySelector("#rdLightboxClose");
    const lightboxBackdrop = root.querySelector(".rd-lightbox__backdrop");
    const counter = root.querySelector("#rdImageCounter");

    const cleanupFns = [];
    let currentIndex = 0;
    let isSwitching = false;
    let touchStartX = null;
    let touchStartY = null;

    function on(el, event, handler, options) {
      if (!el) return;
      el.addEventListener(event, handler, options);
      cleanupFns.push(() => el.removeEventListener(event, handler, options));
    }

    function setBodyLocked(locked) {
      document.body.style.overflow = locked ? "hidden" : "";
    }

    function updateCounter() {
      if (counter && thumbs.length > 0) {
        counter.textContent = currentIndex + 1 + " / " + thumbs.length;
      }
    }

    function setActiveThumb(index) {
      thumbs.forEach(function (thumb, i) {
        thumb.classList.toggle("is-active", i === index);
      });
    }

    function pulseThumb(index) {
      const thumb = thumbs[index];
      if (!thumb || reduceMotion) return;

      thumb.animate(
        [
          { transform: "translateY(0) scale(1)" },
          { transform: "translateY(-3px) scale(1.06)" },
          { transform: "translateY(0) scale(1)" },
        ],
        {
          duration: 360,
          easing: "cubic-bezier(0.22, 1, 0.36, 1)",
        },
      );
    }

    function preloadAround(index) {
      if (!thumbs.length) return;

      const nextIndex = (index + 1) % thumbs.length;
      const prevIndex = (index - 1 + thumbs.length) % thumbs.length;

      [nextIndex, prevIndex].forEach(function (i) {
        const src = thumbs[i] && thumbs[i].src;
        if (!src) return;
        const img = new Image();
        img.src = src;
      });
    }

    function animateMainImageIn() {
      if (!mainImg || reduceMotion) return;

      mainImg.animate(
        [
          { opacity: 0.72, transform: "scale(1.02)" },
          { opacity: 1, transform: "scale(1)" },
        ],
        {
          duration: 360,
          easing: "cubic-bezier(0.22, 1, 0.36, 1)",
        },
      );
    }

    function showImage(index) {
      if (!mainImg || thumbs.length === 0 || isSwitching) return;

      const safeIndex = (index + thumbs.length) % thumbs.length;
      const selectedThumb = thumbs[safeIndex];
      if (!selectedThumb || !selectedThumb.src) return;

      isSwitching = true;
      mainImg.classList.add("is-switching");

      const tempImg = new Image();

      tempImg.onload = function () {
        mainImg.src = selectedThumb.src;
        currentIndex = safeIndex;
        setActiveThumb(currentIndex);
        updateCounter();
        pulseThumb(currentIndex);
        preloadAround(currentIndex);

        requestAnimationFrame(function () {
          mainImg.classList.remove("is-switching");
          animateMainImageIn();

          setTimeout(function () {
            isSwitching = false;
          }, 120);
        });
      };

      tempImg.onerror = function () {
        mainImg.classList.remove("is-switching");
        isSwitching = false;
      };

      tempImg.src = selectedThumb.src;
    }

    function openLightbox() {
      if (!lightbox || !lightboxImg || !mainImg) return;

      lightbox.classList.add("show");
      lightbox.setAttribute("aria-hidden", "false");
      lightboxImg.src = mainImg.src;
      setBodyLocked(true);

      if (!reduceMotion) {
        lightboxImg.animate(
          [
            { opacity: 0, transform: "scale(0.96)" },
            { opacity: 1, transform: "scale(1)" },
          ],
          {
            duration: 280,
            easing: "cubic-bezier(0.22, 1, 0.36, 1)",
          },
        );
      }
    }

    function closeLightbox() {
      if (!lightbox) return;
      lightbox.classList.remove("show");
      lightbox.setAttribute("aria-hidden", "true");
      setBodyLocked(false);
    }

    function copyTextFallback(text) {
      try {
        const input = document.createElement("textarea");
        input.value = text;
        input.setAttribute("readonly", "");
        input.style.position = "absolute";
        input.style.left = "-9999px";
        document.body.appendChild(input);
        input.select();
        document.execCommand("copy");
        document.body.removeChild(input);
        return true;
      } catch (e) {
        return false;
      }
    }

    async function handleCopyRoomId() {
      if (!copyBtn) return;

      const roomNumber = copyBtn.dataset.roomNumber || "";
      const original = copyBtn.innerHTML;
      if (!roomNumber) return;

      try {
        if (navigator.clipboard && navigator.clipboard.writeText) {
          await navigator.clipboard.writeText(roomNumber);
        } else {
          const ok = copyTextFallback(roomNumber);
          if (!ok) throw new Error("Clipboard not supported");
        }

        copyBtn.innerHTML = '<i class="bi bi-check2-circle"></i> Copied';
        copyBtn.classList.add("is-copied");

        setTimeout(function () {
          copyBtn.innerHTML = original;
          copyBtn.classList.remove("is-copied");
        }, 1600);
      } catch (err) {
        console.error("Copy failed:", err);
        copyBtn.innerHTML = '<i class="bi bi-x-circle"></i> Copy failed';

        setTimeout(function () {
          copyBtn.innerHTML = original;
        }, 1400);
      }
    }

    function initGallery() {
      if (mainImg && thumbs.length > 0) {
        setActiveThumb(0);
        updateCounter();
        preloadAround(0);

        thumbs.forEach(function (thumb, index) {
          on(thumb, "click", function () {
            showImage(index);
          });
        });
      }

      on(prevBtn, "click", function (e) {
        e.preventDefault();
        e.stopPropagation();
        showImage(currentIndex - 1);
      });

      on(nextBtn, "click", function (e) {
        e.preventDefault();
        e.stopPropagation();
        showImage(currentIndex + 1);
      });

      on(zoomBtn, "click", openLightbox);

      on(mainImg, "dblclick", openLightbox);

      on(lightboxClose, "click", closeLightbox);
      on(lightboxBackdrop, "click", closeLightbox);
    }

    function initSwipe() {
      if (!imageWrap || thumbs.length <= 1) return;

      on(
        imageWrap,
        "touchstart",
        function (e) {
          const touch = e.touches && e.touches[0];
          if (!touch) return;
          touchStartX = touch.clientX;
          touchStartY = touch.clientY;
        },
        { passive: true },
      );

      on(imageWrap, "touchend", function (e) {
        const touch = e.changedTouches && e.changedTouches[0];
        if (!touch || touchStartX == null || touchStartY == null) return;

        const dx = touch.clientX - touchStartX;
        const dy = touch.clientY - touchStartY;

        if (Math.abs(dx) > 40 && Math.abs(dx) > Math.abs(dy)) {
          if (dx < 0) {
            showImage(currentIndex + 1);
          } else {
            showImage(currentIndex - 1);
          }
        }

        touchStartX = null;
        touchStartY = null;
      });
    }

    function initTiltEffect() {
      if (!imageWrap || reduceMotion) return;

      function handleMove(e) {
        const rect = imageWrap.getBoundingClientRect();
        const px = (e.clientX - rect.left) / rect.width;
        const py = (e.clientY - rect.top) / rect.height;

        const rotateY = (px - 0.5) * 4;
        const rotateX = (0.5 - py) * 4;

        imageWrap.style.transform =
          "perspective(1200px) rotateX(" +
          rotateX +
          "deg) rotateY(" +
          rotateY +
          "deg)";
      }

      function resetTilt() {
        imageWrap.style.transform = "";
      }

      on(imageWrap, "mousemove", handleMove);
      on(imageWrap, "mouseleave", resetTilt);
    }

    function initReveal() {
      const revealItems = root.querySelectorAll(".rd-reveal");
      if (!revealItems.length) return;

      if (reduceMotion) {
        revealItems.forEach(function (item) {
          item.style.opacity = "1";
          item.style.transform = "none";
        });
        return;
      }

      revealItems.forEach(function (item, index) {
        item.style.opacity = "0";
        item.style.transform = "translateY(20px) scale(0.985)";
        item.style.transition =
          "opacity 0.62s cubic-bezier(0.22,1,0.36,1), transform 0.62s cubic-bezier(0.22,1,0.36,1)";
        item.style.transitionDelay = Math.min(index * 0.06, 0.28) + "s";
      });

      const observer = new IntersectionObserver(
        function (entries) {
          entries.forEach(function (entry) {
            if (!entry.isIntersecting) return;
            entry.target.style.opacity = "1";
            entry.target.style.transform = "translateY(0) scale(1)";
            observer.unobserve(entry.target);
          });
        },
        {
          threshold: 0.08,
        },
      );

      revealItems.forEach(function (item) {
        observer.observe(item);
      });

      cleanupFns.push(function () {
        observer.disconnect();
      });
    }

    function keyHandler(e) {
      const detailModal = document.querySelector("#roomDetailModal");
      if (!detailModal || !detailModal.classList.contains("show")) return;

      if (e.key === "Escape") {
        if (lightbox && lightbox.classList.contains("show")) {
          closeLightbox();
        }
        return;
      }

      if (lightbox && lightbox.classList.contains("show")) {
        if (!thumbs.length) return;

        if (e.key === "ArrowLeft") {
          showImage(currentIndex - 1);
        } else if (e.key === "ArrowRight") {
          showImage(currentIndex + 1);
        }
        return;
      }

      if (!thumbs.length) return;

      if (e.key === "ArrowLeft") {
        showImage(currentIndex - 1);
      } else if (e.key === "ArrowRight") {
        showImage(currentIndex + 1);
      } else if (e.key === "Enter" && document.activeElement === mainImg) {
        openLightbox();
      }
    }

    function initAccessibility() {
      if (mainImg) {
        mainImg.setAttribute("tabindex", "0");
      }

      thumbs.forEach(function (thumb, index) {
        thumb.setAttribute("tabindex", "0");
        on(thumb, "keydown", function (e) {
          if (e.key === "Enter" || e.key === " ") {
            e.preventDefault();
            showImage(index);
          }
        });
      });
    }

    initGallery();
    initSwipe();
    initTiltEffect();
    initReveal();
    initAccessibility();

    on(copyBtn, "click", handleCopyRoomId);
    on(document, "keydown", keyHandler);

    root.__roomDetailCleanup = function () {
      cleanupFns.forEach(function (fn) {
        try {
          fn();
        } catch (e) {
          console.error("Cleanup error:", e);
        }
      });
    };
  }

  window.initRoomDetail = initRoomDetail;
})();
