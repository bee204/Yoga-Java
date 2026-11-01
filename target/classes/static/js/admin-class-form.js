document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('adminClassForm');
    if (!form) return;
    const byId = id => document.getElementById(id);
    const fields = ['name','teacher_id','lich_hoc','location','start_time','end_time','start_date','end_date','quantity','price'];
    const formatDate = value => value ? new Intl.DateTimeFormat('vi-VN').format(new Date(`${value}T00:00:00`)) : '';
    const formatPrice = value => `${new Intl.NumberFormat('vi-VN').format(Number(value) || 0)}đ`;
    const set = (id, value) => { const element = byId(id); if (element) element.textContent = value; };
    function updateTeacherProfile(option) {
        const profile = byId('selectedTeacherProfile'), avatar = byId('selectedTeacherAvatar'), initial = byId('selectedTeacherInitial');
        if (!option?.value) { profile.hidden = true; return; }
        set('selectedTeacherCode', option.dataset.code || '#000');
        set('selectedTeacherName', option.dataset.name || 'Giáo viên');
        set('selectedTeacherExperience', option.dataset.experience || 'Chưa cập nhật kinh nghiệm');
        set('selectedTeacherDescription', option.dataset.description || 'Chưa có thông tin giới thiệu.');
        set('selectedTeacherEmail', option.dataset.email || 'Chưa có email');
        set('selectedTeacherPhone', option.dataset.phone || 'Chưa có số điện thoại');
        set('selectedTeacherInitial', option.dataset.initial || 'G');
        if (option.dataset.avatar) { avatar.src = option.dataset.avatar; avatar.alt = `Ảnh giáo viên ${option.dataset.name || ''}`; avatar.hidden = false; initial.hidden = true; }
        else { avatar.hidden = true; initial.hidden = false; }
        profile.hidden = false;
    }
    function updatePreview() {
        const teacher = byId('teacher_id');
        const teacherOption = teacher.options[teacher.selectedIndex];
        set('previewClassName', byId('name').value.trim() || 'Tên lớp Yoga');
        set('previewTeacher', teacherOption?.value ? `${teacherOption.dataset.name} · ${teacherOption.dataset.experience}` : 'Chưa chọn giáo viên');
        updateTeacherProfile(teacherOption);
        set('previewSchedule', byId('lich_hoc').value.trim() || 'Chưa thiết lập');
        set('previewTime', `${byId('start_time').value || '--:--'} – ${byId('end_time').value || '--:--'}`);
        set('previewLocation', byId('location').value.trim() || 'Chưa cập nhật');
        const start = formatDate(byId('start_date').value), end = formatDate(byId('end_date').value);
        set('previewDates', start || end ? `${start || '—'} – ${end || '—'}` : 'Chưa cập nhật');
        set('previewQuantity', `${byId('quantity').value || 0} học viên`);
        set('previewPrice', formatPrice(byId('price').value));
        set('classDescriptionCounter', `${byId('description').value.length}/255`);
        byId('end_date').min = byId('start_date').value || '';
    }
    fields.forEach(id => { byId(id)?.addEventListener('input', updatePreview); byId(id)?.addEventListener('change', updatePreview); });
    byId('description').addEventListener('input', updatePreview);
    updatePreview();
});
