const byId = (id) => document.getElementById(id);
const resultsBody = byId('resultsBody');
const rowCount = byId('rowCount');
const searchMessage = byId('searchMessage');

async function api(path, options = {}) {
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 60000);
    try {
        const response = await fetch(path, {
            ...options,
            signal: controller.signal,
            headers: { 'Content-Type': 'application/json', ...(options.headers || {}) }
        });
        const data = await response.json().catch(() => ({}));
        if (!response.ok) throw new Error(`HTTP ${response.status}: ${data.message || response.statusText}`);
        return data;
    } finally {
        clearTimeout(timeout);
    }
}

function escapeHtml(value) {
    return String(value ?? '').replace(/[&<>'"]/g, (symbol) => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;'
    })[symbol]);
}

function formatDate(value) {
    if (!value) return '—';
    return new Intl.DateTimeFormat('ru-RU', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(value));
}

function renderRows(rows) {
    rowCount.textContent = `${rows.length} ${rows.length === 1 ? 'запись' : 'записей'}`;
    if (!rows.length) {
        resultsBody.innerHTML = '<tr><td colspan="7" class="empty">Ничего не найдено</td></tr>';
        return;
    }
    resultsBody.innerHTML = rows.map((row) => `
        <tr data-ticket="${escapeHtml(row.ticketNumber)}">
            <td><strong>${escapeHtml(row.ticketNumber)}</strong><br><span class="subtle">сегмент ${row.serialNumber}</span></td>
            <td>${escapeHtml(row.passengerSurname)} ${escapeHtml(row.passengerName)}</td>
            <td>${escapeHtml(row.documentNumber)}</td>
            <td><strong>${escapeHtml(row.airlineCode)} ${row.flightNumber}</strong><br><span class="subtle">PNR ${escapeHtml(row.pnrId)}</span></td>
            <td>${escapeHtml(row.departPlace)} → ${escapeHtml(row.arrivePlace)}</td>
            <td>${formatDate(row.departDatetime)}</td>
            <td><span class="badge ${row.refunded ? 'refunded' : ''}">${row.refunded ? 'ВОЗВРАЩЕН' : 'ПРОДАН'}</span></td>
        </tr>`).join('');
    resultsBody.querySelectorAll('tr[data-ticket]').forEach((row) => row.addEventListener('click', () => {
        byId('refundTicket').value = row.dataset.ticket;
    }));
}

async function loadRows(query = '') {
    searchMessage.textContent = 'Выполняется запрос…';
    try {
        const rows = await api(`/api/segments${query}`);
        renderRows(rows);
        searchMessage.textContent = rows.length ? 'Данные получены из PostgreSQL' : 'Записи не найдены';
    } catch (error) {
        searchMessage.textContent = error.name === 'AbortError'
            ? 'Запрос отменен по превышению допустимого времени выполнения'
            : `База данных или backend недоступны: ${error.message}`;
        renderRows([]);
    }
}

async function checkStatus() {
    const node = byId('systemStatus');
    try {
        await api('/api/info');
        node.className = 'status up';
        node.innerHTML = '<span></span>Все сервисы работают';
    } catch (_) {
        node.className = 'status down';
        node.innerHTML = '<span></span>Сервис недоступен';
    }
}

byId('documentForm').addEventListener('submit', (event) => {
    event.preventDefault();
    const value = byId('documentNumber').value.trim();
    if (!value) return;
    byId('ticketNumber').value = '';
    loadRows(`?documentNumber=${encodeURIComponent(value)}`);
});

byId('ticketForm').addEventListener('submit', (event) => {
    event.preventDefault();
    const value = byId('ticketNumber').value.trim();
    if (!/^\d{13}$/.test(value)) {
        searchMessage.textContent = 'Номер билета должен содержать 13 цифр';
        return;
    }
    byId('documentNumber').value = '';
    const all = byId('allPassengerTickets').checked;
    loadRows(`?ticketNumber=${encodeURIComponent(value)}&allPassengerTickets=${all}`);
});

byId('showAllButton').addEventListener('click', () => loadRows());

byId('saleButton').addEventListener('click', async (event) => {
    const button = event.currentTarget;
    button.disabled = true;
    const ticket = String(Date.now()).slice(-13);
    const body = {
        operation_type: 'sale', operation_time: new Date().toISOString(), operation_place: 'Web demo',
        passenger: {
            name: 'Анна', surname: 'Смирнова', patronymic: 'Игоревна', doc_type: '00', doc_number: '4500123456',
            birthdate: '2002-04-18', gender: 'F', passenger_type: 'student', ticket_number: ticket, ticket_type: 1
        },
        routes: [
            { airline_code: 'SU', flight_num: 100, depart_place: 'SVO', depart_datetime: '2026-12-01T09:00:00+03:00', arrive_place: 'KZN', arrive_datetime: '2026-12-01T10:35:00+03:00', pnr_id: 'DOCKER' },
            { airline_code: 'SU', flight_num: 101, depart_place: 'KZN', depart_datetime: '2026-12-05T18:00:00+03:00', arrive_place: 'SVO', arrive_datetime: '2026-12-05T19:35:00+03:00', pnr_id: 'DOCKER' }
        ]
    };
    try {
        const result = await api('/api/process/sale', { method: 'POST', body: JSON.stringify(body) });
        byId('saleResult').textContent = JSON.stringify(result, null, 2);
        byId('refundTicket').value = ticket;
        await loadRows(`?ticketNumber=${ticket}`);
    } catch (error) {
        byId('saleResult').textContent = error.message;
    } finally {
        button.disabled = false;
    }
});

byId('refundButton').addEventListener('click', async (event) => {
    const button = event.currentTarget;
    const ticket = byId('refundTicket').value.trim();
    if (!/^\d{13}$/.test(ticket)) {
        byId('refundResult').textContent = 'Введите номер из 13 цифр';
        return;
    }
    button.disabled = true;
    const body = { operation_type: 'refund', operation_time: new Date().toISOString(), operation_place: 'Web demo', ticket_number: ticket };
    try {
        const result = await api('/api/process/refund', { method: 'POST', body: JSON.stringify(body) });
        byId('refundResult').textContent = JSON.stringify(result, null, 2);
        await loadRows(`?ticketNumber=${ticket}`);
    } catch (error) {
        byId('refundResult').textContent = error.message;
    } finally {
        button.disabled = false;
    }
});

checkStatus();
loadRows();

