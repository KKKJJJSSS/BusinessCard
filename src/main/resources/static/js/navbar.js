document.addEventListener("DOMContentLoaded", () => {
    updateNavbar();
});

function updateNavbar() {
    // 함수 정의 시작 부분에 로그 출력
    console.log("[DEBUG] Inside updateNavbar function");

    const loggedSection = document.getElementById("logged-section");
    const usernameLbl = document.getElementById("usernameLbl");
    const loginSection = document.getElementById("login-section");

    const username = sessionStorage.getItem("username");

    // 기존의 로그 위치
    console.log("Username on updateNavbar: ", username);

    if (username) {
        loggedSection.style.display = "block";
        usernameLbl.textContent = username + "님";
        loginSection.style.display = "none";
    } else {
        loggedSection.style.display = "none";
        loginSection.style.display = "block";
    }
}

// 로그아웃 함수
function logout() {
    sessionStorage.removeItem("username");
    updateNavbar();
    window.location.href = "/";
}