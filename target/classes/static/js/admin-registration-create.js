document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('adminRegistrationForm');
    if (!form) return;

    const classSelect = document.getElementById('class_id');
    const packageInputs = [...form.querySelectorAll('input[name="package_months"]')];
    const emailInput = document.getElementById('email');
    const phoneInput = document.getElementById('phone');
    const nameInput = document.getElementById('fullname');
    const notesInput = document.getElementById('notes');
    const customerNotice = document.getElementById('existingCustomerNotice');
    const classPreview = document.getElementById('selectedClassDetails');
    const pricePlaceholder = document.getElementById('pricePlaceholder');
    const priceContent = document.getElementById('priceContent');
    const discountRates = { 1: 0, 3: 5, 6: 10, 12: 15 };
    let customerLookupTimer;
    let lastLookup = '';

    const formatPrice = value => `${new Intl.NumberFormat('vi-VN').format(Math.round(value || 0))}đ`;
    const setText = (id, value) => {
        const element = document.getElementById(id);
        if (element) element.textContent = value;
    };

    function selectedPackageMonths() {
        return Number(packageInputs.find(input => input.checked)?.value || 1);
    }

    function updateSummary() {
        const option = classSelect.options[classSelect.selectedIndex];
        const hasClass = Boolean(option?.value);
        const months = selectedPackageMonths();

        packageInputs.forEach(input => {
            input.closest('.registration-package-option')?.classList.toggle('is-selected', input.checked);
        });

        if (!hasClass) {
            classPreview.hidden = true;
            priceContent.hidden = true;
            pricePlaceholder.hidden = false;
            return;
        }

        const monthlyPrice = Number(option.dataset.price || 0);
        const originalPrice = monthlyPrice * months;
        const discountAmount = originalPrice * (discountRates[months] || 0) / 100;

        setText('classSchedule', option.dataset.schedule || 'Chưa cập nhật');
        setText('classTime', option.dataset.time || 'Chưa cập nhật');
        setText('classLocation', option.dataset.location || 'Chưa cập nhật');
        setText('classTeacher', option.dataset.teacher || 'Chưa cập nhật');
        setText('classSlots', `${option.dataset.slots || 0} học viên`);
        setText('summaryClassName', option.textContent.split('·')[0].trim());
        setText('monthlyPrice', formatPrice(monthlyPrice));
        setText('summaryPackage', `${months} tháng`);
        setText('originalPrice', formatPrice(originalPrice));
        setText('discountAmount', `− ${formatPrice(discountAmount)}`);
        setText('finalPrice', formatPrice(originalPrice - discountAmount));

        classPreview.hidden = false;
        pricePlaceholder.hidden = true;
        priceContent.hidden = false;
    }

    async function lookupCustomer() {
        if (!form.dataset.customerSearchUrl || !customerNotice) return;

        const email = emailInput.value.trim();
        const phone = phoneInput.value.trim();
        const lookupKey = `${email}|${phone}`;

        if ((!email && !phone) || lookupKey === lastLookup) return;
        lastLookup = lookupKey;

        try {
            const response = await fetch(form.dataset.customerSearchUrl, {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]')?.content || '',
                },
                body: JSON.stringify({ email, phone }),
            });
            if (!response.ok) return;

            const data = await response.json();
            if (!data.customer) {
                customerNotice.hidden = true;
                return;
            }

            nameInput.value = data.customer.name || nameInput.value;
            emailInput.value = data.customer.email || emailInput.value;
            phoneInput.value = data.customer.phone || phoneInput.value;
            customerNotice.hidden = false;
        } catch (_) {
            customerNotice.hidden = true;
        }
    }

    function scheduleCustomerLookup() {
        window.clearTimeout(customerLookupTimer);
        customerLookupTimer = window.setTimeout(lookupCustomer, 350);
    }

    function updateNoteCounter() {
        setText('noteCounter', `${notesInput.value.length}/255`);
    }

    classSelect.addEventListener('change', updateSummary);
    packageInputs.forEach(input => input.addEventListener('change', updateSummary));
    if (form.dataset.customerSearchUrl) {
        emailInput.addEventListener('blur', lookupCustomer);
        phoneInput.addEventListener('blur', lookupCustomer);
        emailInput.addEventListener('input', scheduleCustomerLookup);
        phoneInput.addEventListener('input', scheduleCustomerLookup);
    }
    notesInput.addEventListener('input', updateNoteCounter);

    updateSummary();
    updateNoteCounter();
});
