document.getElementById("searchForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const formData = new FormData(e.target);
    const data = Object.fromEntries(formData);
    let number = document.getElementById("number").value;

    if (number.trim() === '') {
        alert("번호를 입력해 주세요.");
    } else {
        const response = await fetch("/auth/search", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(data),
        });

        if (response.ok) {
            const result = await response.json();

            if (result.result === "success") {
                const usernameElement = document.getElementById("username");
                usernameElement.textContent = "아이디: " + result.username;
            } else {
                alert("번호와 일치하는 계정이 없습니다.");
            }
        } else {
            alert("아이디 찾기에 실패했습니다.");
        }
    }
});