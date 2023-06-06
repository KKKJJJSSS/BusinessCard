const container = document.getElementById("container");
document.getElementById("uploadForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const formData = new FormData(e.target);
    const data = Object.fromEntries(formData);

    const response = await fetch("/card/upload", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    });

    if (response.ok) {
        const result = await response.json();
        if (result.result === "success") {
            window.location.href = "/board";
        } else {
            alert("이름을 필수로 입력 해주세요.");
        }
    } else {
        alert("이름을 필수로 입력 해주세요.");
    }
});
