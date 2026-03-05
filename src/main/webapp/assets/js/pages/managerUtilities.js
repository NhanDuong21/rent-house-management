/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

document.addEventListener("DOMContentLoaded", function () {
    const input = document.getElementById("searchInput");
    const rows = document.querySelectorAll("#utilityTable tr:not(#notFoundUtility)");
    const notFound = document.getElementById("notFoundUtility");

    if (!input) return;

    function filterUtilities() {
        const searchKeyword = input.value.toLowerCase();
        let count = 0;

        rows.forEach(row => {
            const nameText = row.querySelector(".utilityName");
            if (!nameText) return;

            const name = nameText.textContent.toLowerCase();

            if (name.includes(searchKeyword)) {
                row.style.display = "";
                count++;
            } else {
                row.style.display = "none";
            }
        });

        notFound.style.display = count === 0 ? "" : "none";
    }

    input.addEventListener("keyup", filterUtilities);
});