document.addEventListener("DOMContentLoaded", function () {
  const card = document.getElementById("errorCard");
  const particlesWrap = document.getElementById("particles");
  const magneticButtons = document.querySelectorAll(".magnetic");

  // ===== 1. create floating particles =====
  function createParticles(total = 26) {
    if (!particlesWrap) return;

    for (let i = 0; i < total; i++) {
      const p = document.createElement("span");
      p.className = "particle";

      const size = Math.random() * 4 + 2;
      const left = Math.random() * 100;
      const delay = Math.random() * 10;
      const duration = Math.random() * 10 + 10;
      const opacity = Math.random() * 0.6 + 0.2;

      p.style.width = `${size}px`;
      p.style.height = `${size}px`;
      p.style.left = `${left}%`;
      p.style.bottom = `${Math.random() * 20 - 10}%`;
      p.style.opacity = opacity;
      p.style.animationDuration = `${duration}s`;
      p.style.animationDelay = `${delay}s`;

      particlesWrap.appendChild(p);
    }
  }

  createParticles();

  // ===== 2. tilt card with mouse =====
  if (card && window.innerWidth > 768) {
    const maxRotate = 8;

    window.addEventListener("mousemove", function (e) {
      const x = e.clientX / window.innerWidth;
      const y = e.clientY / window.innerHeight;

      const rotateY = (x - 0.5) * maxRotate * 2;
      const rotateX = (0.5 - y) * maxRotate * 2;

      card.style.transform = `
                perspective(1400px)
                rotateX(${rotateX}deg)
                rotateY(${rotateY}deg)
                translateZ(0)
            `;
    });

    window.addEventListener("mouseleave", function () {
      card.style.transform = `
                perspective(1400px)
                rotateX(0deg)
                rotateY(0deg)
                translateZ(0)
            `;
    });
  }

  // ===== 3. magnetic buttons =====
  magneticButtons.forEach((btn) => {
    btn.addEventListener("mousemove", function (e) {
      const rect = btn.getBoundingClientRect();
      const x = e.clientX - rect.left - rect.width / 2;
      const y = e.clientY - rect.top - rect.height / 2;

      btn.style.transform = `translate(${x * 0.12}px, ${y * 0.18}px) scale(1.03)`;
    });

    btn.addEventListener("mouseleave", function () {
      btn.style.transform = "";
    });
  });

  // ===== 4. random subtle glitch =====
  const code = document.querySelector(".code");

  function triggerGlitch() {
    if (!code) return;

    code.classList.add("glitch-boost");

    setTimeout(() => {
      code.classList.remove("glitch-boost");
    }, 280);

    const nextTime = Math.random() * 3000 + 1800;
    setTimeout(triggerGlitch, nextTime);
  }

  triggerGlitch();
});
