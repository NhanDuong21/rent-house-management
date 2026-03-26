document.addEventListener("DOMContentLoaded", function () {
  const input = document.getElementById("searchInput");
  const tableBody = document.getElementById("utilityTable");
  const notFound = document.getElementById("notFoundUtility");

  function getRows() {
    return tableBody
      ? tableBody.querySelectorAll("tr:not(#notFoundUtility)")
      : [];
  }

  function filterUtilities() {
    if (!input) return;

    const searchKeyword = input.value.trim().toLowerCase();
    const rows = getRows();
    let count = 0;

    rows.forEach((row) => {
      const nameText = row.querySelector(".utilityName");
      if (!nameText) return;

      const name = nameText.textContent.toLowerCase();
      const matched = name.includes(searchKeyword);

      row.style.display = matched ? "" : "none";

      if (matched) {
        count++;
      }
    });

    if (notFound) {
      notFound.style.display = count === 0 ? "" : "none";
    }
  }

  if (input) {
    input.addEventListener("keyup", filterUtilities);
  }

  // Auto close alerts after a while
  const alerts = document.querySelectorAll(".mb-alert");
  alerts.forEach((alert, index) => {
    setTimeout(
      () => {
        alert.style.transition = "opacity 0.35s ease, transform 0.35s ease";
        alert.style.opacity = "0";
        alert.style.transform = "translateY(-10px)";
        setTimeout(() => alert.remove(), 350);
      },
      5000 + index * 800,
    );
  });

  // ESC close modal
  document.addEventListener("keydown", function (e) {
    if (e.key === "Escape") {
      closeModal("addModal");
      closeModal("editModal");
    }
  });
});

function openModal(modalId) {
  const modal = document.getElementById(modalId);
  if (!modal) return;

  modal.classList.add("open");
  document.body.style.overflow = "hidden";
}

function closeModal(modalId) {
  const modal = document.getElementById(modalId);
  if (!modal) return;

  modal.classList.remove("open");

  const anotherOpenModal = document.querySelector(".mb-modal-overlay.open");
  if (!anotherOpenModal) {
    document.body.style.overflow = "";
  }
}

function handleOverlayClick(event, modalId) {
  if (event.target.id === modalId) {
    closeModal(modalId);
  }
}
