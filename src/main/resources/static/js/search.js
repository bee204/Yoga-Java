function normalizeClassSearchText(value) {
    return String(value || '')
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .toLocaleLowerCase('vi')
        .trim();
}

function searchClassFunc() {
    const input = document.getElementById('searchClass');
    const classGrid = document.getElementById('classGrid');

    if (!input || !classGrid) return;

    const query = normalizeClassSearchText(input.value);
    const classCards = Array.from(classGrid.querySelectorAll('[data-class-card]'));
    const noResults = document.getElementById('noResultsMessage');
    const resultCount = document.getElementById('classResultCount');
    const pagination = document.getElementById('classesPagination');
    let visibleCount = 0;

    classCards.forEach((card) => {
        const searchableText = normalizeClassSearchText(card.dataset.search || card.textContent);
        const isVisible = !query || searchableText.includes(query);
        card.hidden = !isVisible;
        if (isVisible) visibleCount += 1;
    });

    if (resultCount) {
        resultCount.textContent = query
            ? `Tìm thấy ${visibleCount} lớp trong trang này`
            : `Đang hiển thị ${classCards.length} lớp`;
    }

    if (noResults) noResults.hidden = !query || visibleCount > 0;
    if (pagination) pagination.hidden = Boolean(query);
}

document.addEventListener('DOMContentLoaded', () => {
    const input = document.getElementById('searchClass');
    if (!input) return;

    input.addEventListener('input', searchClassFunc);

    document.getElementById('clearClassSearch')?.addEventListener('click', () => {
        input.value = '';
        searchClassFunc();
        input.focus();
    });

    document.addEventListener('keydown', (event) => {
        if ((event.metaKey || event.ctrlKey) && event.key.toLocaleLowerCase() === 'k') {
            event.preventDefault();
            input.focus();
        }
    });
});
