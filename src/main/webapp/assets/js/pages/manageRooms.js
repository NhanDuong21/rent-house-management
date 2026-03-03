document.addEventListener("DOMContentLoaded", function () {
  const input = document.querySelector(".searchRoom");
  const tbody = document.getElementById("roomTable");
  if (!input || !tbody) return;

  function normalize(s) {
    return (s || "").toString().trim().toLowerCase();
  }

  input.addEventListener("input", function () {
    const keyword = normalize(input.value);

    // Re-query rows each time in case pagination/DOM changes
    const rows = tbody.querySelectorAll("tr");

    rows.forEach((row) => {
      const roomCell = row.querySelector(".roomNumber");
      if (!roomCell) return;

      const roomNumber = normalize(roomCell.textContent);
      row.style.display = roomNumber.includes(keyword) ? "" : "none";
    });
  });
});

