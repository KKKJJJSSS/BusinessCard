// 체크 박스 설정
$(document).ready(function () {
    $(".wrap-right").hide();
    $(".wrap-right:first").show();
    $(".toggle-check:first").prop("checked", true);

    $(".toggle-check").click(function () {
        if (!$(this).is(":checked")) {
            $(this).prop("checked", true);
        } else {
            $(".toggle-check").not(this).prop("checked", false);
            $(this).parents(".main-row").siblings().find(".wrap-right").hide();
            $(this).parents(".main-row").find(".wrap-right").show();
        }
    });
});

function displaySelectedInfo(element) {
    const data = element.dataset;

    document.getElementById('display-name').textContent = data.name;
    if (data.position && data.department) {
        document.getElementById('display-position-department').textContent = data.position + ' / ' + data.department;
    } else if (data.position) {
        document.getElementById('display-position-department').textContent = data.position;
    } else if (data.department) {
        document.getElementById('display-position-department').textContent = data.department;
    } else {
        document.getElementById('display-position-department').textContent = '';
    }
    document.getElementById('display-company-name').textContent = data.companyName;
    document.getElementById('display-email').textContent = data.email;
    document.getElementById('display-phone-number').textContent = data.phoneNumber;
    document.getElementById('display-number').textContent = data.number;
    document.getElementById('display-fax').textContent = data.fax;
    document.getElementById('display-address').textContent = data.address;
    document.getElementById('display-created-at').textContent = data.createdAt;
    document.getElementById('display-memo').textContent = data.memo;
}

document.addEventListener('DOMContentLoaded', () => {
    const checkboxes = document.querySelectorAll('.toggle-check');
    for (const checkbox of checkboxes) {
        checkbox.addEventListener('change', function () {
            displaySelectedInfo(this);
        });
    }

    const firstCheckbox = document.querySelector('.toggle-check');
    if (firstCheckbox) {
        firstCheckbox.checked = true;
        displaySelectedInfo(firstCheckbox);
    }
});

// 페이지 기능
document.addEventListener('DOMContentLoaded', () => {
    let currentPage = 1;
    const numPerPage = 3;

    const initPagination = () => {
        const wrapLefts = document.querySelectorAll('.wrap-left');
        wrapLefts.forEach(wrapLeft => {
            const cards = wrapLeft.querySelectorAll('.card-box');
            cards.forEach((card, index) => {
                if (index < numPerPage) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });
        });
    };

    const updateLeft = () => {
        const wrapLefts = document.querySelectorAll('.wrap-left');
        wrapLefts.forEach(wrapLeft => {
            const cards = wrapLeft.querySelectorAll('.card-box');
            const start = (currentPage - 1) * numPerPage;
            const end = start + numPerPage;
            cards.forEach((card, index) => {
                if (index >= start && index < end) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });
        });

        updatePageNumber();
    };

    const updatePageNumber = () => {
        const pageNumberElement = document.getElementById('page-number');
        pageNumberElement.textContent = currentPage;
    };

    document.getElementById('previous-page').addEventListener('click', () => {
        currentPage--;
        if (currentPage < 1) {
            currentPage = 1;
        }
        updateLeft();
    });

    document.getElementById('next-page').addEventListener('click', () => {
        currentPage++;
        const totalRows = document.querySelectorAll('.wrap-left .card-box').length;
        if (currentPage > Math.ceil(totalRows / numPerPage)) {
            currentPage = Math.ceil(totalRows / numPerPage);
        }
        updateLeft();
    });

    // Pagination 초기화
    initPagination();

    // 페이지 로드 시 페이지 번호 업데이트
    updatePageNumber();
});

// 체크 박스 삭제
const deleteSelectedBtn = document.querySelector('#delete-selected-btn');
deleteSelectedBtn.addEventListener('click', async function () {
    const checkedItems = document.querySelectorAll('.toggle-check:checked');
    if (checkedItems.length === 0) {
        alert('명함을 체크해주세요.');
        return;
    }

    for (const checkedItem of checkedItems) {
        const id = checkedItem.getAttribute('data-id');
        const deleteResponse = await fetch(`/card/delete/${id}`, {method: 'DELETE'});

        if (deleteResponse.ok) {
            // 삭제가 성공적으로 완료된 경우, 체크된 item이 포함된 card-box를 삭제합니다.
            deleteItem(id);
            location.reload(); // 페이지 새로고침
        } else {
            console.error(`삭제를 실패 했습니다.`);
        }
    }
});

function deleteItem(id) {
    const item = document.querySelector(`.card-box[data-id="${id}"]`);
    if (item) {
        item.remove();
    }
}

// 체크 박스 편집
const updateSelectedBtn = document.querySelector('#update-selected-btn');
updateSelectedBtn.addEventListener('click', async function () {
    const checkedItems = document.querySelectorAll('.toggle-check:checked');
    if (checkedItems.length === 0) {
        alert('명함을 체크해 주세요.');
        return;
    }

    for (const checkedItem of checkedItems) {
        const id = checkedItem.getAttribute('data-id');

        if (id != null) {
            location.href="/reupload?id=" + id;
        } else {
            console.error(`명함을 체크해 주세요.`);
        }
    }
});
