document.addEventListener('DOMContentLoaded', () => {
    const input = document.getElementById('teacherSearch');
    const grid = document.getElementById('teachersGrid');

    if (!input || !grid) return;

    const cards = Array.from(grid.querySelectorAll('[data-teacher-card]'));
    const resultCount = document.getElementById('teacherResultCount');
    const noResults = document.getElementById('teacherNoResults');
    const pagination = document.getElementById('teachersPagination');

    const normalize = (value) => String(value || '')
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .toLocaleLowerCase('vi')
        .trim();

    function searchTeachers() {
        const query = normalize(input.value);
        let visibleCount = 0;

        cards.forEach((card) => {
            const isVisible = !query || normalize(card.dataset.search || card.textContent).includes(query);
            card.hidden = !isVisible;
            if (isVisible) visibleCount += 1;
        });

        if (resultCount) {
            resultCount.textContent = query
                ? `Tìm thấy ${visibleCount} giáo viên trong trang này`
                : `Đang hiển thị ${cards.length} giáo viên`;
        }

        if (noResults) noResults.hidden = !query || visibleCount > 0;
        if (pagination) pagination.hidden = Boolean(query);
    }

    input.addEventListener('input', searchTeachers);
    document.getElementById('clearTeacherSearch')?.addEventListener('click', () => {
        input.value = '';
        searchTeachers();
        input.focus();
    });

    document.addEventListener('keydown', (event) => {
        if ((event.ctrlKey || event.metaKey) && event.key.toLocaleLowerCase() === 'k') {
            event.preventDefault();
            input.focus();
        }
    });
});
