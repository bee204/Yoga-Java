document.addEventListener('DOMContentLoaded', () => {
    const accountToggle = document.getElementById('adminAccountToggle');
    const accountMenu = document.getElementById('dropdown-menu');
    const navToggle = document.getElementById('adminNavToggle');
    const navMenu = document.getElementById('adminNavMenu');
    const deleteModal = document.getElementById('adminDeleteModal');
    const deleteModalDialog = deleteModal?.querySelector('.admin-confirm-modal__dialog');
    const deleteModalTitle = document.getElementById('adminDeleteModalTitle');
    const deleteModalDescription = document.getElementById('adminDeleteModalDescription');
    const deleteConfirmButton = document.getElementById('adminDeleteConfirmButton');
    const deleteCancelButton = deleteModal?.querySelector('.admin-confirm-modal__cancel');
    let pendingDeleteForm = null;
    let deleteTrigger = null;

    function closeAccountMenu() {
        accountMenu?.classList.remove('is-open');
        accountToggle?.setAttribute('aria-expanded', 'false');
    }

    function closeNavigation() {
        navMenu?.classList.remove('is-open');
        navToggle?.setAttribute('aria-expanded', 'false');
    }

    function closeDeleteModal() {
        if (!deleteModal || deleteModal.hidden) return;

        deleteModal.classList.remove('is-open');
        deleteModal.setAttribute('aria-hidden', 'true');
        deleteModal.hidden = true;
        document.body.classList.remove('admin-modal-open');
        pendingDeleteForm = null;
        deleteConfirmButton?.removeAttribute('disabled');
        if (deleteConfirmButton) deleteConfirmButton.innerHTML = '<i class="fa-regular fa-trash-can" aria-hidden="true"></i> Xóa dữ liệu';
        deleteTrigger?.focus();
        deleteTrigger = null;
    }

    function openDeleteModal(form, trigger) {
        if (!deleteModal) return;

        pendingDeleteForm = form;
        deleteTrigger = trigger;
        deleteModalTitle.textContent = form.dataset.confirmTitle || 'Xác nhận xóa';
        deleteModalDescription.textContent = form.dataset.confirmMessage || 'Bạn có chắc chắn muốn xóa dữ liệu này?';
        deleteModal.hidden = false;
        deleteModal.setAttribute('aria-hidden', 'false');
        document.body.classList.add('admin-modal-open');
        requestAnimationFrame(() => {
            deleteModal.classList.add('is-open');
            deleteCancelButton?.focus();
        });
    }

    document.querySelectorAll('form').forEach((form) => {
        const methodField = form.querySelector('input[name="_method"]');
        if (methodField?.value.toUpperCase() !== 'DELETE') return;

        form.addEventListener('submit', (event) => {
            if (form.dataset.deleteConfirmed === 'true') return;
            event.preventDefault();
            openDeleteModal(form, event.submitter || document.activeElement);
        });
    });

    deleteModal?.querySelectorAll('[data-delete-modal-close]').forEach((button) => {
        button.addEventListener('click', closeDeleteModal);
    });

    deleteConfirmButton?.addEventListener('click', () => {
        if (!pendingDeleteForm) return;

        const form = pendingDeleteForm;
        form.dataset.deleteConfirmed = 'true';
        deleteConfirmButton.disabled = true;
        deleteConfirmButton.innerHTML = '<i class="fa-solid fa-circle-notch fa-spin" aria-hidden="true"></i> Đang xóa...';
        form.requestSubmit();
    });

    accountToggle?.addEventListener('click', (event) => {
        event.stopPropagation();
        const willOpen = !accountMenu.classList.contains('is-open');
        closeNavigation();
        accountMenu.classList.toggle('is-open', willOpen);
        accountToggle.setAttribute('aria-expanded', String(willOpen));
    });

    navToggle?.addEventListener('click', (event) => {
        event.stopPropagation();
        const willOpen = !navMenu.classList.contains('is-open');
        closeAccountMenu();
        navMenu.classList.toggle('is-open', willOpen);
        navToggle.setAttribute('aria-expanded', String(willOpen));
    });

    document.addEventListener('click', (event) => {
        if (accountMenu && !accountMenu.contains(event.target)) closeAccountMenu();
        if (navMenu && !navMenu.contains(event.target) && event.target !== navToggle) closeNavigation();
    });

    document.addEventListener('keydown', (event) => {
        if (event.key === 'Escape') {
            if (deleteModal && !deleteModal.hidden) {
                closeDeleteModal();
                return;
            }
            closeAccountMenu();
            closeNavigation();
            accountToggle?.focus();
            return;
        }

        if (event.key !== 'Tab' || !deleteModal || deleteModal.hidden || !deleteModalDialog) return;
        const focusable = [...deleteModalDialog.querySelectorAll('button:not([disabled]), [href], input:not([disabled]), select:not([disabled]), textarea:not([disabled]), [tabindex]:not([tabindex="-1"])')];
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

    window.addEventListener('resize', () => {
        if (window.innerWidth > 780) closeNavigation();
    });
});
