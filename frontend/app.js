// API Base URL:
// - Em Docker (porta 3000 via Nginx proxy): usa caminho relativo, o nginx repassa para o backend
// - Abrindo diretamente no browser (porta 3000 via Node ou file://): aponta direto para API
const API_BASE = (window.location.port === '80')
  ? ''
  : 'http://localhost:8080';

// Estado global
let professoresCache = [];
let cursosCache = [];
let debugRequestsCache = [];
let kanbanInterval = null;

// Inicialização
document.addEventListener('DOMContentLoaded', () => {
  verificarStatusAPI();
  carregarProfessores();
  carregarCursos();
  carregarAlunos();
  
  // Iniciar monitoramento do Kanban em segundo plano
  atualizarKanban();
  kanbanInterval = setInterval(atualizarKanban, 1500);
});

// ==========================================
// NAVEGAÇÃO DE ABAS
// ==========================================
function switchTab(tabName) {
  document.querySelectorAll('.tab-content').forEach(el => el.classList.add('hidden'));
  document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.classList.remove('border-amber-400', 'text-white', 'bg-indigo-800');
    btn.classList.add('border-transparent', 'text-indigo-200');
  });

  const content = document.getElementById(`content-${tabName}`);
  const btn = document.getElementById(`tab-${tabName}`);
  if (content && btn) {
    content.classList.remove('hidden');
    btn.classList.add('border-amber-400', 'text-white', 'bg-indigo-800');
    btn.classList.remove('border-transparent', 'text-indigo-200');
  }

  if (tabName === 'cursos') carregarProfessores();
  if (tabName === 'alunos') carregarCursos();
}

// ==========================================
// STATUS DA API
// ==========================================
async function verificarStatusAPI() {
  const badge = document.getElementById('api-status-badge');
  const text = document.getElementById('api-status-text');
  try {
    const res = await fetch(`${API_BASE}/professor`);
    if (res.ok) {
      text.innerText = 'API Online (Porta 8080)';
      badge.classList.remove('border-rose-700');
      badge.classList.add('border-emerald-700');
    } else {
      throw new Error();
    }
  } catch (err) {
    text.innerText = 'API Indisponível';
    badge.classList.remove('border-emerald-700');
    badge.classList.add('border-rose-700');
  }
}

// ==========================================
// PROFESSORES (CRUD)
// ==========================================
async function carregarProfessores() {
  const tbody = document.getElementById('lista-professores');
  const select = document.getElementById('curso-prof-id');
  try {
    const res = await fetch(`${API_BASE}/professor`);
    const data = await res.json();
    professoresCache = data;

    if (data.length === 0) {
      tbody.innerHTML = `<tr><td colspan="4" class="py-4 text-center text-slate-400">Nenhum professor cadastrado ainda.</td></tr>`;
    } else {
      tbody.innerHTML = data.map(p => `
        <tr class="hover:bg-slate-50 transition">
          <td class="py-3 px-4 font-mono font-bold text-indigo-700">#${p.id}</td>
          <td class="py-3 px-4 font-semibold text-slate-800">${escapeHtml(p.nome)}</td>
          <td class="py-3 px-4 text-slate-600">${escapeHtml(p.email)}</td>
          <td class="py-3 px-4"><span class="bg-indigo-50 text-indigo-700 text-xs px-2.5 py-1 rounded-md font-medium">${escapeHtml(p.departamento || 'N/A')}</span></td>
        </tr>
      `).join('');
    }

    if (select) {
      select.innerHTML = '<option value="">Selecione um professor...</option>' + 
        data.map(p => `<option value="${p.id}">#${p.id} - ${escapeHtml(p.nome)} (${escapeHtml(p.departamento || 'Sem Dep')})</option>`).join('');
    }
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="4" class="py-4 text-center text-rose-500">Erro ao conectar com a API.</td></tr>`;
  }
}

async function handleCadastrarProfessor(e) {
  e.preventDefault();
  const nome = document.getElementById('prof-nome').value.trim();
  const email = document.getElementById('prof-email').value.trim();
  const departamento = document.getElementById('prof-departamento').value.trim();

  try {
    const res = await fetch(`${API_BASE}/professor`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ nome, email, departamento })
    });

    if (res.status === 201) {
      showToast('Professor cadastrado com sucesso!', 'success');
      document.getElementById('form-professor').reset();
      carregarProfessores();
    } else {
      const err = await res.json();
      showToast(err.error || 'Erro ao cadastrar professor.', 'error');
    }
  } catch (err) {
    showToast('Falha na comunicação com a API.', 'error');
  }
}

// ==========================================
// CURSOS (CRUD)
// ==========================================
async function carregarCursos() {
  const tbody = document.getElementById('lista-cursos');
  const select = document.getElementById('aluno-curso-id');
  try {
    const res = await fetch(`${API_BASE}/curso`);
    const data = await res.json();
    cursosCache = data;

    if (data.length === 0) {
      tbody.innerHTML = `<tr><td colspan="3" class="py-4 text-center text-slate-400">Nenhum curso cadastrado ainda.</td></tr>`;
    } else {
      tbody.innerHTML = data.map(c => `
        <tr class="hover:bg-slate-50 transition">
          <td class="py-3 px-4 font-mono font-bold text-indigo-700">#${c.id}</td>
          <td class="py-3 px-4 font-semibold text-slate-800">${escapeHtml(c.nome)}</td>
          <td class="py-3 px-4">
            <span class="bg-amber-50 text-amber-800 text-xs px-2.5 py-1 rounded-md font-medium border border-amber-200">
              <i class="fa-solid fa-chalkboard-user mr-1"></i>${escapeHtml(c.professorNome)} (ID: ${c.professorId})
            </span>
          </td>
        </tr>
      `).join('');
    }

    if (select) {
      select.innerHTML = '<option value="">Selecione um curso...</option>' + 
        data.map(c => `<option value="${c.id}">#${c.id} - ${escapeHtml(c.nome)}</option>`).join('');
    }
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="3" class="py-4 text-center text-rose-500">Erro ao carregar cursos.</td></tr>`;
  }
}

async function handleCadastrarCurso(e) {
  e.preventDefault();
  const nome = document.getElementById('curso-nome').value.trim();
  const professorId = parseInt(document.getElementById('curso-prof-id').value);

  if (!professorId) {
    showToast('Selecione um professor válido.', 'warning');
    return;
  }

  try {
    const res = await fetch(`${API_BASE}/curso`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ nome, professorId })
    });

    if (res.status === 201) {
      showToast('Curso cadastrado com sucesso!', 'success');
      document.getElementById('form-curso').reset();
      carregarCursos();
    } else {
      const err = await res.json();
      showToast(err.error || 'Erro ao cadastrar curso.', 'error');
    }
  } catch (err) {
    showToast('Falha na comunicação com a API.', 'error');
  }
}

// ==========================================
// ALUNOS (CRUD)
// ==========================================
async function carregarAlunos() {
  const tbody = document.getElementById('lista-alunos');
  try {
    const res = await fetch(`${API_BASE}/aluno`);
    const data = await res.json();

    if (data.length === 0) {
      tbody.innerHTML = `<tr><td colspan="4" class="py-4 text-center text-slate-400">Nenhum aluno matriculado ainda.</td></tr>`;
    } else {
      tbody.innerHTML = data.map(a => `
        <tr class="hover:bg-slate-50 transition">
          <td class="py-3 px-4 font-mono font-bold text-indigo-700">#${a.id}</td>
          <td class="py-3 px-4 font-semibold text-slate-800">${escapeHtml(a.nome)}</td>
          <td class="py-3 px-4 text-slate-600">${escapeHtml(a.email)}</td>
          <td class="py-3 px-4">
            <span class="bg-emerald-50 text-emerald-800 text-xs px-2.5 py-1 rounded-md font-medium border border-emerald-200">
              <i class="fa-solid fa-graduation-cap mr-1"></i>${escapeHtml(a.cursoNome)} (ID: ${a.cursoId})
            </span>
          </td>
        </tr>
      `).join('');
    }
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="4" class="py-4 text-center text-rose-500">Erro ao carregar alunos.</td></tr>`;
  }
}

async function handleCadastrarAluno(e) {
  e.preventDefault();
  const nome = document.getElementById('aluno-nome').value.trim();
  const email = document.getElementById('aluno-email').value.trim();
  const cursoId = parseInt(document.getElementById('aluno-curso-id').value);

  if (!cursoId) {
    showToast('Selecione um curso válido.', 'warning');
    return;
  }

  try {
    const res = await fetch(`${API_BASE}/aluno`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ nome, email, cursoId })
    });

    if (res.status === 201) {
      showToast('Aluno matriculado com sucesso!', 'success');
      document.getElementById('form-aluno').reset();
      carregarAlunos();
    } else {
      const err = await res.json();
      showToast(err.error || 'Erro ao cadastrar aluno.', 'error');
    }
  } catch (err) {
    showToast('Falha na comunicação com a API.', 'error');
  }
}

// ==========================================
// DEBUG KANBAN (POSTMAN MONITOR)
// ==========================================
async function atualizarKanban() {
  try {
    const res = await fetch(`${API_BASE}/api/debug/requests`);
    if (!res.ok) return;
    const requests = await res.json();
    debugRequestsCache = requests;

    document.getElementById('kanban-badge-count').innerText = requests.length;

    const colSuccess = document.getElementById('col-success');
    const colClient = document.getElementById('col-client_error');
    const colServer = document.getElementById('col-server_error');

    const successReqs = requests.filter(r => r.column === 'success');
    const clientReqs = requests.filter(r => r.column === 'client_error');
    const serverReqs = requests.filter(r => r.column === 'server_error');

    document.getElementById('count-col-success').innerText = successReqs.length;
    document.getElementById('count-col-client').innerText = clientReqs.length;
    document.getElementById('count-col-server').innerText = serverReqs.length;

    renderKanbanColumn(colSuccess, successReqs, 'emerald');
    renderKanbanColumn(colClient, clientReqs, 'amber');
    renderKanbanColumn(colServer, serverReqs, 'rose');

  } catch (err) {
    // API pode estar reiniciando
  }
}

function renderKanbanColumn(container, items, color) {
  if (items.length === 0) {
    container.innerHTML = `<p class="text-center text-xs text-slate-400 py-10">Nenhuma requisição nesta coluna.</p>`;
    return;
  }

  container.innerHTML = items.map(req => {
    const methodBadge = req.method === 'POST' ? 'bg-indigo-600 text-white' :
                        req.method === 'GET' ? 'bg-emerald-600 text-white' :
                        req.method === 'DELETE' ? 'bg-rose-600 text-white' : 'bg-slate-600 text-white';

    return `
      <div class="bg-white p-3.5 rounded-lg shadow-sm border border-slate-200 hover:shadow-md transition">
        <div class="flex items-center justify-between mb-2">
          <div class="flex items-center space-x-1.5">
            <span class="text-[10px] font-black uppercase px-2 py-0.5 rounded ${methodBadge}">${req.method}</span>
            <span class="text-xs font-bold text-slate-800 font-mono">${escapeHtml(req.path)}</span>
          </div>
          <span class="text-[11px] font-mono font-bold text-slate-500">${req.timestamp}</span>
        </div>

        <div class="flex items-center justify-between text-xs text-slate-600 mb-2.5">
          <span class="font-bold text-${color}-600">
            ${req.status} ${escapeHtml(req.statusText || '')}
          </span>
          <span class="text-[11px] text-slate-400">
            <i class="fa-regular fa-clock mr-1"></i>${req.durationMs}ms
          </span>
        </div>

        <div class="border-t border-slate-100 pt-2 flex items-center justify-between">
          <span class="text-[10px] text-slate-400">Origem: Postman / HTTP</span>
          <button onclick="abrirModalDetalhes('${req.id}')" class="text-xs text-indigo-600 hover:text-indigo-800 font-semibold flex items-center">
            <i class="fa-solid fa-code mr-1"></i>Ver Payload
          </button>
        </div>
      </div>
    `;
  }).join('');
}

function abrirModalDetalhes(requestId) {
  const req = debugRequestsCache.find(r => r.id === requestId);
  if (!req) return;

  document.getElementById('modal-title').innerText = `${req.method} ${req.path} (${req.status} ${req.statusText})`;
  
  let reqPretty = 'Nenhum payload enviado';
  if (req.requestBody) {
    try {
      reqPretty = JSON.stringify(JSON.parse(req.requestBody), null, 2);
    } catch {
      reqPretty = req.requestBody;
    }
  }

  let resPretty = 'Nenhum corpo de resposta';
  if (req.responseBody) {
    try {
      resPretty = JSON.stringify(JSON.parse(req.responseBody), null, 2);
    } catch {
      resPretty = req.responseBody;
    }
  }

  document.getElementById('modal-req-body').innerText = reqPretty;
  document.getElementById('modal-res-body').innerText = resPretty;
  document.getElementById('json-modal').classList.remove('hidden');
}

function fecharModal() {
  document.getElementById('json-modal').classList.add('hidden');
}

async function limparHistoricoKanban() {
  try {
    await fetch(`${API_BASE}/api/debug/requests`, { method: 'DELETE' });
    atualizarKanban();
    showToast('Histórico do Kanban limpo!', 'info');
  } catch (err) {
    showToast('Erro ao limpar histórico.', 'error');
  }
}

// ==========================================
// UTILITÁRIOS
// ==========================================
function escapeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

function showToast(message, type = 'info') {
  const container = document.getElementById('toast-container');
  const toast = document.createElement('div');
  const colors = {
    success: 'bg-emerald-600 text-white',
    error: 'bg-rose-600 text-white',
    warning: 'bg-amber-500 text-white',
    info: 'bg-indigo-600 text-white'
  };

  toast.className = `${colors[type] || colors.info} px-4 py-2.5 rounded-lg shadow-lg text-xs font-semibold flex items-center space-x-2 transition transform duration-300 translate-y-2 opacity-0`;
  toast.innerHTML = `<span>${escapeHtml(message)}</span>`;
  container.appendChild(toast);

  setTimeout(() => {
    toast.classList.remove('translate-y-2', 'opacity-0');
  }, 10);

  setTimeout(() => {
    toast.classList.add('opacity-0', 'translate-y-2');
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}
