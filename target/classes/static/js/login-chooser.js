document.addEventListener('DOMContentLoaded', () => {
    const modal = document.getElementById('loginChooserModal');
    const openButton = document.querySelector('[data-login-chooser-open]');
    const dialog = modal?.querySelector('.login-chooser__dialog');
    const firstOption = modal?.querySelector('.login-option');
    let previousFocus = null;

    if (!modal || !openButton || !dialog) return;

    function openModal() {
        previousFocus = document.activeElement;
        modal.hidden = false;
        modal.setAttribute('aria-hidden', 'false');
        openButton.setAttribute('aria-expanded', 'true');
        document.body.classList.add('login-chooser-open');
        requestAnimationFrame(() => {
            modal.classList.add('is-open');
            firstOption?.focus();
        });
    }

    function closeModal() {
        if (modal.hidden) return;
        modal.classList.remove('is-open');
        modal.hidden = true;
        modal.setAttribute('aria-hidden', 'true');
        openButton.setAttribute('aria-expanded', 'false');
        document.body.classList.remove('login-chooser-open');
        previousFocus?.focus();
        previousFocus = null;
    }

    openButton.addEventListener('click', openModal);
    modal.querySelectorAll('[data-login-chooser-close]').forEach((element) => element.addEventListener('click', closeModal));

    document.addEventListener('keydown', (event) => {
        if (modal.hidden) return;
        if (event.key === 'Escape') {
            closeModal();
            return;
        }
        if (event.key !== 'Tab') return;

        const focusable = [...dialog.querySelectorAll('a[href], button:not([disabled]), [tabindex]:not([tabindex="-1"])')];
        if (!focusable.length) return;
        const first = focusable[0];
        const last = focusable[focusable.length - 1];
        if (event.shiftKey && document.activeElement === first) {
            event.preventDefault();
            last.focus();
        } else if (!event.shiftKey && document.activeElement === last) {
            event.preventDefault();
            first.focus();
        }
    });
});
