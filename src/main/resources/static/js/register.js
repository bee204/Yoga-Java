document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('registerForm');
    const classSelect = document.getElementById('className');

    if (!form || !classSelect) return;

    const packageInputs = Array.from(form.querySelectorAll('input[name="package_months"]'));
    const submitButton = document.getElementById('registerSubmit');
    const summaryFacts = document.getElementById('summaryFacts');
    const summaryStatus = document.getElementById('summaryStatus');
    const discountRates = { 1: 0, 3: 5, 6: 10, 12: 15 };

    const output = {
        className: document.getElementById('summaryClassName'),
        teacher: document.getElementById('summaryTeacher'),
        schedule: document.getElementById('summarySchedule'),
        time: document.getElementById('summaryTime'),
        location: document.getElementById('summaryLocation'),
        dates: document.getElementById('summaryDates'),
        originalPrice: document.getElementById('originalPrice'),
        discountAmount: document.getElementById('discountAmount'),
        finalPrice: document.getElementById('finalPrice'),
    };

    const formatPrice = (price) => `${new Intl.NumberFormat('vi-VN').format(Math.round(price))}₫`;

    function selectedPackageMonths() {
        const selectedPackage = packageInputs.find((input) => input.checked);
        return selectedPackage ? Number(selectedPackage.value) : 0;
    }

    function renderSummary() {
        const selectedClass = classSelect.options[classSelect.selectedIndex];
        const hasClass = Boolean(selectedClass?.value);
        const packageMonths = selectedPackageMonths();

        if (!hasClass) {
            output.className.textContent = 'Chưa chọn lớp';
            output.teacher.textContent = 'Chọn một lớp Yoga để xem thông tin.';
            output.originalPrice.textContent = '—';
            output.discountAmount.textContent = '—';
            output.finalPrice.textContent = '—';
            summaryFacts.hidden = true;
            summaryStatus.innerHTML = '<i class="fa-solid fa-circle-info" aria-hidden="true"></i> Vui lòng chọn lớp học.';
            submitButton.disabled = true;
            return;
        }

        output.className.textContent = selectedClass.textContent.trim();
        output.teacher.textContent = `Giáo viên: ${selectedClass.dataset.teacher}`;
        output.schedule.textContent = selectedClass.dataset.schedule;
        output.time.textContent = selectedClass.dataset.time;
        output.location.textContent = selectedClass.dataset.location;
        output.dates.textContent = selectedClass.dataset.dates;
        summaryFacts.hidden = false;

        const monthlyPrice = Number(selectedClass.dataset.price || 0);
        const totalPrice = monthlyPrice * packageMonths;
        const discountRate = discountRates[packageMonths] || 0;
        const discountAmount = totalPrice * discountRate / 100;
        const finalPrice = totalPrice - discountAmount;

        output.originalPrice.textContent = formatPrice(totalPrice);
        output.discountAmount.textContent = discountAmount > 0 ? `−${formatPrice(discountAmount)}` : '0₫';
        output.finalPrice.textContent = formatPrice(finalPrice);

        const availableSlots = Number(selectedClass.dataset.slots || 0);
        summaryStatus.innerHTML = `<i class="fa-solid fa-circle-check" aria-hidden="true"></i> Còn ${availableSlots} chỗ · Gói ${packageMonths} tháng`;
        submitButton.disabled = packageMonths === 0;
    }

    classSelect.addEventListener('change', renderSummary);
    packageInputs.forEach((input) => input.addEventListener('change', renderSummary));

    form.addEventListener('submit', () => {
        if (!form.checkValidity()) return;
        submitButton.disabled = true;
        submitButton.innerHTML = '<i class="fa-solid fa-circle-notch fa-spin" aria-hidden="true"></i> Đang gửi đăng ký...';
    });

    renderSummary();
});
