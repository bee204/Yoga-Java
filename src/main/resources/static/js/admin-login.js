document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('adminLoginForm');
    const password = document.getElementById('adminPassword');
    const passwordToggle = document.getElementById('adminPasswordToggle');
    const submitButton = document.getElementById('adminLoginSubmit');

    passwordToggle?.addEventListener('click', () => {
        const shouldShow = password.type === 'password';
        password.type = shouldShow ? 'text' : 'password';
        passwordToggle.setAttribute('aria-pressed', String(shouldShow));
        passwordToggle.setAttribute('aria-label', shouldShow ? 'Ẩn mật khẩu' : 'Hiện mật khẩu');
        passwordToggle.querySelector('i')?.classList.toggle('fa-eye', !shouldShow);
        passwordToggle.querySelector('i')?.classList.toggle('fa-eye-slash', shouldShow);
        password.focus();
    });

    form?.addEventListener('submit', () => {
        if (!form.checkValidity()) return;
        submitButton.disabled = true;
        submitButton.innerHTML = '<i class="fa-solid fa-circle-notch fa-spin" aria-hidden="true"></i> Đang xác thực...';
    });
});
