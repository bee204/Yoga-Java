document.addEventListener('DOMContentLoaded', () => {
    const root = document.getElementById('dashboardAnalytics');
    if (!root) return;

    const endpoint = root.dataset.endpoint;
    const periodSelect = document.getElementById('analyticsPeriod');
    const revenuePeriodSelect = document.getElementById('revenuePeriod');
    const notice = document.getElementById('analyticsNotice');
    const grid = root.querySelector('.control-analytics__grid');
    let activeRequest = null;

    const numberFormatter = new Intl.NumberFormat('vi-VN');
    const currencyFormatter = new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND',
        maximumFractionDigits: 0,
    });

    function element(tag, className, text) {
        const node = document.createElement(tag);
        if (className) node.className = className;
        if (text !== undefined) node.textContent = text;
        return node;
    }

    function setLoading(isLoading) {
        grid?.setAttribute('aria-busy', String(isLoading));
        periodSelect.disabled = isLoading;
        revenuePeriodSelect.disabled = isLoading;
        root.classList.toggle('is-loading', isLoading);
        if (isLoading) {
            notice.hidden = false;
            notice.className = 'control-analytics__notice';
            notice.innerHTML = '<i class="fa-solid fa-circle-notch fa-spin" aria-hidden="true"></i> Đang tổng hợp dữ liệu...';
        }
    }

    function renderDonut(segments, centerValue, centerLabel, accessibleLabel) {
        const wrapper = element('div', 'control-donut');
        const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        svg.setAttribute('viewBox', '0 0 120 120');
        svg.setAttribute('role', 'img');
        svg.setAttribute('aria-label', accessibleLabel);

        const track = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
        track.setAttribute('class', 'control-donut__track');
        track.setAttribute('cx', '60');
        track.setAttribute('cy', '60');
        track.setAttribute('r', '46');
        svg.appendChild(track);

        const total = Math.max(segments.reduce((sum, item) => sum + item.value, 0), 1);
        const circumference = 2 * Math.PI * 46;
        let offset = 0;
        segments.forEach((segment) => {
            if (segment.value <= 0) return;
            const ratio = segment.value / total;
            const circle = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
            circle.setAttribute('class', 'control-donut__segment');
            circle.setAttribute('cx', '60');
            circle.setAttribute('cy', '60');
            circle.setAttribute('r', '46');
            circle.setAttribute('stroke', segment.color);
            circle.setAttribute('stroke-dasharray', `${ratio * circumference} ${circumference}`);
            circle.setAttribute('stroke-dashoffset', String(-offset * circumference));
            svg.appendChild(circle);
            offset += ratio;
        });

        const center = element('div', 'control-donut__center');
        center.append(element('strong', '', centerValue), element('small', '', centerLabel));
        wrapper.append(svg, center);
        return wrapper;
    }

    function renderGrowthMetric(targetId, title, metric, color, icon) {
        const target = document.getElementById(targetId);
        if (!target) return;
        target.replaceChildren();

        const head = element('div', 'control-growth-metric__head');
        const iconNode = element('span');
        iconNode.innerHTML = `<i class="fa-solid ${icon}" aria-hidden="true"></i>`;
        const heading = element('div');
        heading.append(element('small', '', 'Tỷ lệ mới trong tổng số'), element('h4', '', title));
        head.append(iconNode, heading);

        const content = element('div', 'control-growth-metric__content');
        const share = Math.min(100, Math.max(0, metric.share));
        content.appendChild(renderDonut([
            { value: share, color },
            { value: Math.max(0, 100 - share), color: '#e6eeea' },
        ], `${numberFormatter.format(share)}%`, 'hồ sơ mới', `${title}: ${share}% là mới trong kỳ`));

        const details = element('div', 'control-growth-metric__details');
        const newRow = element('p');
        newRow.append(element('strong', '', numberFormatter.format(metric.new)), document.createTextNode(` mới / ${numberFormatter.format(metric.total)} tổng`));
        const trendClass = metric.growth_rate > 0 ? 'is-up' : (metric.growth_rate < 0 ? 'is-down' : 'is-flat');
        const trend = element('span', `control-growth-trend ${trendClass}`);
        const trendIcon = metric.growth_rate > 0 ? 'fa-arrow-trend-up' : (metric.growth_rate < 0 ? 'fa-arrow-trend-down' : 'fa-minus');
        trend.innerHTML = `<i class="fa-solid ${trendIcon}" aria-hidden="true"></i> ${Math.abs(metric.growth_rate)}% so với kỳ trước`;
        details.append(newRow, trend, element('small', '', `Kỳ trước có ${numberFormatter.format(metric.previous)} hồ sơ mới`));
        content.appendChild(details);
        target.append(head, content);
    }

    function renderLifecycle(data) {
        const target = document.getElementById('classLifecycleChart');
        if (!target) return;
        target.replaceChildren();
        const segments = [
            { label: 'Đang hoạt động', value: data.lifecycle.active, color: '#2f8a70' },
            { label: 'Sắp khai giảng', value: data.lifecycle.upcoming, color: '#b3ce54' },
            { label: 'Đã kết thúc', value: data.lifecycle.ended, color: '#aab8b2' },
        ];
        const chart = renderDonut(segments, numberFormatter.format(data.lifecycle.active), 'đang hoạt động', `Có ${data.lifecycle.active} lớp đang hoạt động trên tổng ${data.total} lớp`);
        const legend = element('div', 'control-chart-legend');
        segments.forEach((segment) => {
            const row = element('div');
            const label = element('span');
            const dot = element('i');
            dot.style.backgroundColor = segment.color;
            label.append(dot, document.createTextNode(segment.label));
            row.append(label, element('strong', '', numberFormatter.format(segment.value)));
            legend.appendChild(row);
        });
        target.append(chart, legend);
    }

    function renderColumns(targetId, items, options = {}) {
        const target = document.getElementById(targetId);
        if (!target) return;
        target.replaceChildren();
        if (!items.length || !items.some((item) => item.value > 0)) {
            const empty = element('div', 'control-chart-empty');
            empty.innerHTML = '<i class="fa-regular fa-chart-bar" aria-hidden="true"></i><strong>Chưa có dữ liệu trong kỳ</strong><span>Hãy thử chọn khoảng thời gian dài hơn.</span>';
            target.appendChild(empty);
            return;
        }

        const max = Math.max(...items.map((item) => item.value), 1);
        items.forEach((item) => {
            const column = element('div', 'control-column');
            column.title = options.tooltip ? options.tooltip(item) : `${item.name || item.label}: ${item.value}`;
            const value = element('strong', '', options.valueLabel ? options.valueLabel(item) : numberFormatter.format(item.value));
            const barArea = element('div', 'control-column__area');
            const bar = element('span', options.barClass || '');
            bar.style.height = `${Math.max(item.value > 0 ? 5 : 0, item.value / max * 100)}%`;
            barArea.appendChild(bar);
            const label = element('small', '', item.name || item.label);
            column.append(value, barArea, label);
            target.appendChild(column);
        });
    }

    function render(data) {
        const forecast = data.revenue_forecast;
        document.getElementById('forecastRevenue').textContent = currencyFormatter.format(forecast.forecast);
        document.getElementById('currentMonthRevenue').textContent = currencyFormatter.format(forecast.current_month);
        document.getElementById('previousMonthRevenue').textContent = currencyFormatter.format(forecast.previous_month);
        document.getElementById('previousMonthLabel').textContent = `Thực tế ${forecast.previous_month_label.toLowerCase()}`;
        document.getElementById('forecastProgress').textContent = `${forecast.elapsed_days}/${forecast.days_in_month} ngày đã qua`;
        document.getElementById('forecastMethod').textContent = `${forecast.month_label} · Ước tính theo tốc độ doanh số đã xác nhận`;

        const forecastChange = document.getElementById('forecastChange');
        const changeClass = forecast.change_rate > 0 ? 'is-up' : (forecast.change_rate < 0 ? 'is-down' : 'is-flat');
        const changeIcon = forecast.change_rate > 0 ? 'fa-arrow-trend-up' : (forecast.change_rate < 0 ? 'fa-arrow-trend-down' : 'fa-minus');
        forecastChange.className = changeClass;
        forecastChange.innerHTML = `<i class="fa-solid ${changeIcon}" aria-hidden="true"></i> ${Math.abs(forecast.change_rate)}%`;

        document.getElementById('growthPeriodLabel').textContent = data.meta.period_label;
        renderGrowthMetric('customerGrowthChart', 'Học viên mới', data.growth.customers, '#2f8a70', 'fa-user-plus');
        renderGrowthMetric('classGrowthChart', 'Lớp Yoga mới', data.growth.classes, '#88a93d', 'fa-spa');
        renderLifecycle(data.classes);

        document.getElementById('teacherAssignmentTotal').textContent = `${numberFormatter.format(data.teachers.total_assignments)} lượt phân công`;
        renderColumns('teacherChart', data.teachers.items, {
            valueLabel: (item) => `${numberFormatter.format(item.rate)}%`,
            tooltip: (item) => `${item.name}: ${item.value} lớp (${item.rate}%)`,
        });

        document.getElementById('revenueTotal').textContent = currencyFormatter.format(data.revenue.total);
        document.getElementById('revenueAverage').textContent = currencyFormatter.format(data.revenue.average);
        renderColumns('revenueChart', data.revenue.items, {
            barClass: 'is-revenue',
            valueLabel: (item) => item.value >= 1000000 ? `${numberFormatter.format(Math.round(item.value / 100000) / 10)}tr` : `${numberFormatter.format(Math.round(item.value / 1000))}k`,
            tooltip: (item) => `${item.label}: ${currencyFormatter.format(item.value)}`,
        });
    }

    async function loadAnalytics() {
        activeRequest?.abort();
        const requestController = new AbortController();
        activeRequest = requestController;
        setLoading(true);

        const url = new URL(endpoint, window.location.origin);
        url.searchParams.set('period', periodSelect.value);
        url.searchParams.set('revenue_months', revenuePeriodSelect.value);

        try {
            const response = await fetch(url, {
                headers: { Accept: 'application/json' },
                signal: requestController.signal,
            });
            if (!response.ok) throw new Error('Không thể tải dữ liệu dashboard.');
            const data = await response.json();
            render(data);
            notice.hidden = true;
        } catch (error) {
            if (error.name === 'AbortError') return;
            notice.hidden = false;
            notice.className = 'control-analytics__notice is-error';
            notice.innerHTML = '<i class="fa-solid fa-circle-exclamation" aria-hidden="true"></i> Không thể tải dữ liệu biểu đồ. Vui lòng thử lại.';
        } finally {
            if (activeRequest === requestController && !requestController.signal.aborted) setLoading(false);
        }
    }

    periodSelect.addEventListener('change', loadAnalytics);
    revenuePeriodSelect.addEventListener('change', loadAnalytics);
    loadAnalytics();
});
