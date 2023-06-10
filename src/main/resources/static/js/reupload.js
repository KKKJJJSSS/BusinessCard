const reuploadForm = document.getElementById("reuploadForm");
reuploadForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const formData = new FormData(e.target);

    const response = await fetch("/card/reupload", {
        method: "POST",
        body: formData,
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
