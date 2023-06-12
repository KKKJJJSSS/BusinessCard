//체크박스, 검색, 스크롤
$(document).ready(function () {
    const searchInput = document.getElementById('search-input');
    const searchButton = document.getElementById('search-button');

    // 검색 버튼 이벤트 리스너
    searchButton.addEventListener('click', (event) => {
        event.preventDefault();
        updateDisplay(searchInput.value);
        applyToggleCheck();
        displayFirstCardBox();
    });

    // 검색 입력 중 Enter 키 이벤트 리스너
    searchInput.addEventListener('keypress', (event) => {
        if (event.key === 'Enter') {
            event.preventDefault();
            updateDisplay(searchInput.value);
            applyToggleCheck();
            displayFirstCardBox();
        }
    });

    // 체크박스 변경 이벤트 리스너 적용
    function applyToggleCheck() {
        $(".wrap-right").hide();
        $(".wrap-right:first").show();
        $(".toggle-check").off().click(function () {
            if (!$(this).is(":checked")) {
                $(this).prop("checked", true);
            } else {
                $(".toggle-check").not(this).prop("checked", false);
                $(this).parents(".main-row").siblings().find(".wrap-right").hide();
                $(this).parents(".main-row").find(".wrap-right").show();
                displaySelectedInfo(this);
            }
        });
    }

    // 첫 번째 card-box만 표시
    function displayFirstCardBox() {
        $(".wrap-right .card-box").hide();
        $(".wrap-right:first .card-box").show();
    }

    // 체크박스 변경 이벤트 리스너 적용 초기화
    applyToggleCheck();
});

let currentKeyword = "";

// 검색된 키워드를 사용하여 업데이트
function updateDisplay(keyword) {
    currentKeyword = keyword;
    updateLeft();
}

// 키워드를 사용하여 wrap-left 내부의 card-box 표시 업데이트
function updateLeft() {
    const wrapLefts = document.querySelectorAll('.wrap-left');
    wrapLefts.forEach(wrapLeft => {
        const cards = wrapLeft.querySelectorAll('.card-box');
        // 키워드가 있는 card만 가져오기
        const filteredCards = cards.length && currentKeyword
            ? Array.from(cards).filter(card => card.textContent.includes(currentKeyword))
            : cards;

        // 모든 card 숨기고 키워드와 일치하는 card만 표시
        cards.forEach((card) => {
            card.style.display = 'none';
        });

        filteredCards.forEach((card) => {
            card.style.display = 'block';
        });
    });
}


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

    let displayImage = document.getElementById('display-image');
    let imageURL = "/uploads/" + data.image;

    if (data.image === '' || data.image === undefined) {
        imageURL = '/static/image/card.png';
    }

    displayImage.src = imageURL;
}

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
        const imagePath = checkedItem.getAttribute('data-image');
        const deleteResponse = await fetch(`/card/delete/${id}`, {method: 'DELETE'});

        if (deleteResponse.ok) {
            // 삭제가 성공적으로 완료된 경우, 체크된 item이 포함된 card-box를 삭제합니다.
            try {
                await deleteItemImage(imagePath); // 이미지 파일 삭제
            } catch (error) {
                console.error(`이미지 삭제를 실패 했습니다.`);
            }

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

async function deleteItemImage(imagePath) {
    const response = await fetch(`/card/file/delete?imagePath=${imagePath}`, {method: 'DELETE'});
    if (!response.ok) {
        throw new Error('이미지 삭제를 실패 했습니다.');
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

