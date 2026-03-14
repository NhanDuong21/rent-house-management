/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


document.addEventListener("DOMContentLoaded", function () {

    const form = document.querySelector(".profile-form");

    form.addEventListener("submit", function (e) {

        const password = form.password.value;

        if (password.length < 6) {
            alert("Password must be at least 6 characters.");
            e.preventDefault();
        }

    });
    
});
