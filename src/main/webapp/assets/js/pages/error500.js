document.addEventListener("DOMContentLoaded", function () {
  const shell = document.getElementById("errorShell");
  const card = shell ? shell.querySelector(".error-card") : null;
  const particles = document.getElementById("particles");
  const systemText = document.getElementById("systemText");
  const magneticButtons = document.querySelectorAll(".magnetic");

  initTiltCard(card, shell);
  initParticles(particles, 22);
  initSystemText(systemText);
  initMagneticButtons(magneticButtons);
  initAutoPulseReload();
});

/**
 * 3D tilt card effect
 */
function initTiltCard(card, shell) {
  if (!card || !shell) return;

  shell.addEventListener("mousemove", function (e) {
    const rect = shell.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;

    const centerX = rect.width / 2;
    const centerY = rect.height / 2;

    const rotateY = ((x - centerX) / centerX) * 5;
    const rotateX = ((centerY - y) / centerY) * 5;

    card.style.transform =
      "rotateX(" +
      rotateX +
      "deg) rotateY(" +
      rotateY +
      "deg) translateY(-4px)";
  });

  shell.addEventListener("mouseleave", function () {
    card.style.transform = "rotateX(0deg) rotateY(0deg) translateY(0)";
  });
}

/**
 * Floating particles
 */
function initParticles(container, count) {
  if (!container) return;

  for (let i = 0; i < count; i++) {
    spawnParticle(container);
  }

  setInterval(function () {
    spawnParticle(container);
  }, 380);
}

function spawnParticle(container) {
  const particle = document.createElement("span");
  particle.className = "particle";

  const size = random(4, 10);
  const left = random(0, window.innerWidth);
  const duration = random(2500, 5200);
  const delay = random(0, 400);

  particle.style.width = size + "px";
  particle.style.height = size + "px";
  particle.style.left = left + "px";
  particle.style.bottom = "-20px";
  particle.style.animationDuration = duration + "ms";
  particle.style.animationDelay = delay + "ms";

  container.appendChild(particle);

  setTimeout(
    function () {
      particle.remove();
    },
    duration + delay + 200,
  );
}

/**
 * Terminal typing effect
 */
function initSystemText(target) {
  if (!target) return;

  const lines = [
    "[OK] Detecting server anomaly...",
    "[OK] Collecting exception trace...",
    "[OK] Rolling UI fallback view...",
    "[WAIT] Restoring stable response state...",
    "[DONE] Please retry your request.",
  ];

  let lineIndex = 0;
  let charIndex = 0;
  let currentText = "";

  function typeNext() {
    if (lineIndex >= lines.length) {
      setTimeout(function () {
        lineIndex = 0;
        charIndex = 0;
        currentText = "";
        target.textContent = "";
        typeNext();
      }, 2200);
      return;
    }

    const currentLine = lines[lineIndex];

    if (charIndex < currentLine.length) {
      currentText += currentLine.charAt(charIndex);
      target.textContent = currentText;
      charIndex++;
      setTimeout(typeNext, 28);
    } else {
      currentText += "\n";
      target.textContent = currentText;
      lineIndex++;
      charIndex = 0;
      setTimeout(typeNext, 320);
    }
  }

  target.textContent = "";
  typeNext();
}

/**
 * Magnetic button hover
 */
function initMagneticButtons(buttons) {
  if (!buttons || !buttons.length) return;

  buttons.forEach(function (btn) {
    btn.addEventListener("mousemove", function (e) {
      const rect = btn.getBoundingClientRect();
      const relX = e.clientX - rect.left - rect.width / 2;
      const relY = e.clientY - rect.top - rect.height / 2;

      btn.style.transform =
        "translate(" + relX * 0.12 + "px," + relY * 0.18 + "px)";
    });

    btn.addEventListener("mouseleave", function () {
      btn.style.transform = "translate(0,0)";
    });
  });
}

/**
 * Slight attention pulse on reload button after a while
 */
function initAutoPulseReload() {
  const reloadBtn = document.querySelector(".btn-secondary");
  if (!reloadBtn) return;

  setInterval(function () {
    reloadBtn.animate(
      [
        { transform: "scale(1)" },
        { transform: "scale(1.05)" },
        { transform: "scale(1)" },
      ],
      {
        duration: 700,
        easing: "ease",
      },
    );
  }, 5000);
}

function random(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}
