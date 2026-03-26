document.addEventListener("DOMContentLoaded", function () {
  const card = document.querySelector(".tbd-card");
  const totalEl = document.querySelector(".tbd-total-amount");
  const confirmBtn = document.getElementById("confirmPaymentBtn");
  const confirmForm = document.querySelector(".tbd-confirm-form");
  const qrBox = document.querySelector(".tbd-qr-box");
  const breakdownRows = document.querySelectorAll(".breakdown-row");

  // 3D tilt card
  if (card) {
    card.addEventListener("mousemove", function (e) {
      const rect = card.getBoundingClientRect();
      const x = e.clientX - rect.left;
      const y = e.clientY - rect.top;

      const rotateY = (x / rect.width - 0.5) * 2.5;
      const rotateX = (y / rect.height - 0.5) * -2;

      card.style.transform = `perspective(1400px) rotateX(${rotateX}deg) rotateY(${rotateY}deg)`;
    });

    card.addEventListener("mouseleave", function () {
      card.style.transform = "";
    });
  }

  // QR subtle tilt
  if (qrBox) {
    qrBox.addEventListener("mousemove", function (e) {
      const rect = qrBox.getBoundingClientRect();
      const x = e.clientX - rect.left;
      const y = e.clientY - rect.top;

      const rotateY = (x / rect.width - 0.5) * 6;
      const rotateX = (y / rect.height - 0.5) * -6;

      qrBox.style.transform = `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) translateY(-4px) scale(1.01)`;
    });

    qrBox.addEventListener("mouseleave", function () {
      qrBox.style.transform = "";
    });
  }

  // Re-trigger stagger on hover once
  breakdownRows.forEach((row) => {
    row.addEventListener("mouseenter", function () {
      row.style.willChange = "transform";
    });

    row.addEventListener("mouseleave", function () {
      row.style.willChange = "auto";
    });
  });

  // Count up total
  if (totalEl) {
    const rawValue = Number(totalEl.dataset.total || 0);

    if (!isNaN(rawValue) && rawValue > 0) {
      const duration = 1200;
      const start = 0;
      const startTime = performance.now();

      const formatter = new Intl.NumberFormat("vi-VN");

      function animate(now) {
        const progress = Math.min((now - startTime) / duration, 1);
        const eased = 1 - Math.pow(1 - progress, 3);
        const current = Math.round(start + (rawValue - start) * eased);

        totalEl.textContent = formatter.format(current) + " ₫";

        if (progress < 1) {
          requestAnimationFrame(animate);
        } else {
          totalEl.textContent = formatter.format(rawValue) + " ₫";
        }
      }

      requestAnimationFrame(animate);
    }
  }

  // Confirm button loading state
  if (confirmBtn && confirmForm) {
    confirmForm.addEventListener("submit", function () {
      confirmBtn.classList.add("loading");
      confirmBtn.disabled = true;

      const icon = confirmBtn.querySelector("i");
      const text = confirmBtn.querySelector("span");

      if (icon) {
        icon.className = "bi bi-hourglass-split";
      }

      if (text) {
        text.textContent = "Processing Confirmation...";
      }
    });
  }
});
