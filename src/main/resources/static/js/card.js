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
    document.getElementById('display-position-department').textContent = data.position + ' / ' + data.department;
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
        checkbox.addEventListener('change', function() {
            displaySelectedInfo(this);
        });
    }

    const firstCheckbox = document.querySelector('.toggle-check');
    if (firstCheckbox) {
        firstCheckbox.checked = true;
        displaySelectedInfo(firstCheckbox);
    }
});
