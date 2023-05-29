const container = document.getElementById("container");

const toggle = () => {
    container.classList.toggle("sign-in");
    container.classList.toggle("sign-up");
};

setTimeout(() => {
    container.classList.add("sign-in");
}, 200);

document.getElementById("registerForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const formData = new FormData(e.target);
    const data = Object.fromEntries(formData);

    const response = await fetch("/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    });

    if (response.ok) {
        const result = await response.json();
        if (result.result === "success") {
            window.location.href = "/"; // 회원가입 성공시 이동할 페이지
        } else {
            alert("회원가입에 실패했습니다.");
        }
    } else {
        alert("회원가입에 실패했습니다.");
    }
});

function saveUsernameToSessionStorage(username) {
    // 로그인 성공 후 sessionStorage에 사용자 이름 저장
    sessionStorage.setItem("username", username);

    // 세션 스토리지에 저장된 사용자 이름 값 확인
    console.log("Username stored in sessionStorage: ", sessionStorage.getItem("username"));

    // navbar 업데이트
    updateNavbar();
}

document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const formData = new FormData(e.target);
    const data = Object.fromEntries(formData);

    const response = await fetch("/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    });

    if (response.ok) {
        const result = await response.json();

        if (result.result === "success") {
            saveUsernameToSessionStorage(result.username);

            // 로그인 성공시 이동할 페이지
            window.location.href = "/";
        } else {
            alert("로그인에 실패했습니다.");
        }
    } else {
        alert("로그인에 실패했습니다.");
    }
});