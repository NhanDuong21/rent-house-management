document.addEventListener("DOMContentLoaded", function () {
    const form = document.querySelector(".profile-form");
    const passwordInput = form.password;
    const profileCard = document.querySelector(".profile-card");
    const submitBtn = document.getElementById("submitBtn");
    const togglePassword = document.getElementById("togglePassword");
    const passwordStrength = document.getElementById("passwordStrength");
    const staggerItems = document.querySelectorAll(".stagger-item");
    const magneticButtons = document.querySelectorAll(".magnetic-btn");

    // Stagger delay bằng JS để dễ scale thêm field sau này
    staggerItems.forEach((item, index) => {
        item.style.animationDelay = `${0.08 * (index + 1)}s`;
    });

    document.querySelectorAll(".form-group").forEach((el) => {
        el.style.opacity = "1";
        el.style.transform = "translateY(0)";
    });

    // Toggle password
    if (togglePassword && passwordInput) {
        togglePassword.addEventListener("click", function () {
            const icon = this.querySelector("i");
            const isPassword = passwordInput.type === "password";

            passwordInput.type = isPassword ? "text" : "password";
            icon.className = isPassword ? "bi bi-eye-slash-fill" : "bi bi-eye-fill";
        });
    }

    // Password strength visual
    function updatePasswordStrength(value) {
        passwordStrength.classList.remove("weak", "medium", "strong");

        if (!value)
            return;

        if (value.length < 6) {
            passwordStrength.classList.add("weak");
        } else if (value.length < 10) {
            passwordStrength.classList.add("medium");
        } else {
            passwordStrength.classList.add("strong");
        }
    }

    passwordInput.addEventListener("input", function () {
        updatePasswordStrength(this.value);
    });

    // Input focus animation helper
    document
            .querySelectorAll(".input-shell input, .input-shell select")
            .forEach((el) => {
                el.addEventListener("focus", () => {
                    const parent = el.closest(".form-group");
                    if (parent) {
                        parent.style.transform = "translateY(-2px)";
                    }
                });

                el.addEventListener("blur", () => {
                    const parent = el.closest(".form-group");
                    if (parent) {
                        parent.style.transform = "";
                    }
                });
            });

    // Ripple effect for button
    function createRipple(event, button) {
        const ripple = document.createElement("span");
        ripple.classList.add("ripple");

        const rect = button.getBoundingClientRect();
        const size = Math.max(rect.width, rect.height);

        ripple.style.width = ripple.style.height = `${size}px`;
        ripple.style.left = `${event.clientX - rect.left - size / 2}px`;
        ripple.style.top = `${event.clientY - rect.top - size / 2}px`;

        button.appendChild(ripple);

        setTimeout(() => {
            ripple.remove();
        }, 650);
    }

    [submitBtn, ...magneticButtons].forEach((btn) => {
        if (!btn)
            return;
        btn.addEventListener("click", function (e) {
            createRipple(e, this);
        });
    });

    // Magnetic hover effect
    magneticButtons.forEach((btn) => {
        btn.addEventListener("mousemove", function (e) {
            const rect = this.getBoundingClientRect();
            const x = e.clientX - rect.left - rect.width / 2;
            const y = e.clientY - rect.top - rect.height / 2;

            this.style.transform = `translate(${x * 0.08}px, ${y * 0.08}px)`;
        });

        btn.addEventListener("mouseleave", function () {
            this.style.transform = "";
        });
    });

    // Card tilt effect
    if (profileCard) {
        profileCard.addEventListener("mousemove", function (e) {
            const rect = profileCard.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;

            const rotateY = (x / rect.width - 0.5) * 4;
            const rotateX = (y / rect.height - 0.5) * -4;

            profileCard.style.transform = `perspective(1200px) rotateX(${rotateX}deg) rotateY(${rotateY}deg)`;
        });

        profileCard.addEventListener("mouseleave", function () {
            profileCard.style.transform = "";
        });
    }

    // Submit validation - giữ nguyên logic chính
    form.addEventListener("submit", function (e) {
        const password = passwordInput.value.trim();

        if (password.length < 6) {
            e.preventDefault();
            alert("Password must be at least 6 characters.");

            const passwordShell = passwordInput.closest(".input-shell");
            if (passwordShell) {
                passwordShell.classList.remove("shake");
                void passwordShell.offsetWidth;
                passwordShell.classList.add("shake");
            }

            passwordInput.focus();
            return;
        }

        submitBtn.classList.add("loading");
        submitBtn.disabled = true;
    });
});
