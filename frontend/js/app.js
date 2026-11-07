(() => {
  'use strict';

  const configuredOrigin = window.FINAOBRA_CONFIG?.API_ORIGIN;
  const isLocalDevelopment = ['localhost', '127.0.0.1'].includes(location.hostname);
  const API_ORIGIN = new URL(
    configuredOrigin || 'https://finaobra.onrender.com'
  );;

  if (!['http:', 'https:'].includes(API_ORIGIN.protocol) || (location.protocol === 'https:' && API_ORIGIN.protocol !== 'https:')) {
    throw new Error('Configuração insegura da API: use HTTPS quando o frontend estiver em HTTPS.');
  }

  const API_BASE_URL = `${API_ORIGIN.origin}/api/v1`;
  const REQUEST_TIMEOUT_MS = 15000;
  const TOKEN_KEY = 'finaobra-access-token';

  const $ = id => document.getElementById(id);
  const money = value => new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(value) || 0);
  const moneyInputFormatter = new Intl.NumberFormat('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2, useGrouping: true });

  function formatMoneyInput(input) {
    const digits = input.value.replace(/\D/g, '').slice(0, 11);
    input.value = digits ? moneyInputFormatter.format(Number(digits) / 100) : '';
  }

  function setMoneyInputValue(input, numericValue) {
    if (numericValue == null || isNaN(numericValue)) {
      input.value = '';
      return;
    }
    input.value = moneyInputFormatter.format(Number(numericValue));
  }

  function parseMoneyInput(input) {
    const digits = input.value.replace(/\D/g, '');
    return digits ? Number(digits) / 100 : NaN;
  }

  const dateBR = value => {
    if (!value) return '—';
    const [y, m, d] = String(value).slice(0, 10).split('-');
    if (y && m && d) {
      if (y.length === 4) return `${d}/${m}/${y}`; // yyyy-mm-dd
      if (d.length === 4) return `${y}/${m}/${d}`; // dd-mm-yyyy fallback
    }
    return value;
  };

  const safe = value => String(value ?? '').replace(/[&<>"']/g, ch => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
  }[ch]));

  const todayISO = () => {
    const d = new Date();
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
  };

  let apiOnline = false;
  let categories = [];
  let expenses = [];

  // Paginação e controle de dados
  const PAGE_SIZE = 15;
  let expensesPage = 0;
  let expensesTotalPages = 1;
  let categoriesPage = 0;
  let categoriesTotalPages = 1;

  const toast = bootstrap.Toast.getOrCreateInstance($('app-toast'));
  const expenseModal = bootstrap.Modal.getOrCreateInstance($('expense-modal'));
  const categoryModal = bootstrap.Modal.getOrCreateInstance($('category-modal'));
  const confirmModalEl = $('confirm-modal');
  const confirmModal = bootstrap.Modal.getOrCreateInstance(confirmModalEl);

  function notify(message, error = false) {
    const iconClass = error ? 'bi-exclamation-triangle text-danger' : 'bi-check-circle text-success';
    $('toast-message').innerHTML = `<i class="bi ${iconClass} me-2 fs-5"></i>${safe(message)}`;
    toast.show();
  }

  function setApiStatus(online, label) {
    $('api-dot').classList.toggle('online', online);
    $('api-status').textContent = label;
  }

  function confirmDialog(title, message) {
    return new Promise(resolve => {
      $('confirm-modal-title').textContent = title;
      $('confirm-modal-message').textContent = message;

      const confirmBtn = $('confirm-modal-yes');
      const onConfirm = () => {
        cleanup();
        confirmModal.hide();
        resolve(true);
      };

      const onDismiss = () => {
        cleanup();
        resolve(false);
      };

      function cleanup() {
        confirmBtn.removeEventListener('click', onConfirm);
        confirmModalEl.removeEventListener('hidden.bs.modal', onDismiss);
      }

      confirmBtn.addEventListener('click', onConfirm, { once: true });
      confirmModalEl.addEventListener('hidden.bs.modal', onDismiss, { once: true });
      confirmModal.show();
    });
  }

  function applyTheme(theme) {
    document.documentElement.dataset.theme = theme;
    $('theme-icon').className = theme === 'dark' ? 'bi bi-sun' : 'bi bi-moon-stars';
    $('theme-toggle').setAttribute('aria-label', theme === 'dark' ? 'Ativar tema claro' : 'Ativar tema escuro');
    $('theme-color-meta').content = theme === 'dark' ? '#090d16' : '#f8fafc';
    try { localStorage.setItem('finaobra-theme', theme); } catch { }
  }

  function getTheme() {
    try { const saved = localStorage.getItem('finaobra-theme'); if (saved) return saved; } catch { }
    return matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
  }

  function navigate(view) {
    document.querySelectorAll('.view').forEach(el => el.classList.toggle('d-none', el.id !== `view-${view}`));
    document.querySelectorAll('#main-nav .nav-link').forEach(el => el.classList.toggle('active', el.dataset.view === view));

    $('app-sidebar')?.classList.remove('show');

    if (view === 'dashboard') renderDashboard();
    if (view === 'lancamentos') renderExpenses();
    if (view === 'orcamentos') renderCategories();
    history.replaceState(null, '', `#${view}`);
  }

  function categoryIdOf(expense) { return expense.categoria_id ?? expense.idCategoria ?? expense.categoriaId; }
  function categoryName(id) { return categories.find(c => Number(c.id) === Number(id))?.nome || 'Sem categoria'; }
  function categoryBudget(category) { return Number(category.valor_planejado ?? category.orcamento ?? 0); }

  function renderDashboard() {
    if (apiOnline) return;
    const planned = categories.reduce((sum, c) => sum + categoryBudget(c), 0);
    const spent = expenses.reduce((sum, e) => sum + Number(e.valor || 0), 0);
    const balance = planned - spent;

    $('total-planejado').textContent = money(planned);
    $('total-realizado').textContent = money(spent);
    $('saldo-restante').textContent = money(balance);
    $('saldo-restante').classList.toggle('text-danger', balance < 0);
    $('saldo-restante').classList.toggle('text-success', balance >= 0);

    $('category-progress').innerHTML = categories.length ? categories.map(c => {
      const spentCat = expenses.filter(e => Number(categoryIdOf(e)) === Number(c.id)).reduce((s, e) => s + Number(e.valor || 0), 0);
      const budget = categoryBudget(c);
      const pct = budget > 0 ? (spentCat / budget) * 100 : 0;
      const barColor = pct > 100 ? 'var(--color-danger)' : pct >= 85 ? 'var(--color-warning)' : 'var(--color-primary)';

      return `
        <div class="progress-item">
          <div class="progress-meta-row">
            <span class="progress-category-name">${safe(c.nome)}</span>
            <div class="d-flex align-items-center gap-2">
              <span class="progress-values">${money(spentCat)} <span class="text-muted">/</span> ${money(budget)}</span>
              <span class="badge ${pct > 100 ? 'bg-danger' : 'bg-light text-muted border'}">${pct.toFixed(1)}%</span>
            </div>
          </div>
          <div class="progress-bar-custom">
            <div class="progress-bar-inner" style="width: ${Math.min(Math.max(pct, 0), 100)}%; background-color: ${barColor}"></div>
          </div>
        </div>`;
    }).join('') : `
      <div class="empty-state">
        <div class="empty-state-icon"><i class="bi bi-pie-chart"></i></div>
        <div class="empty-state-title">Nenhum orçamento cadastrado</div>
        <div class="empty-state-text">Crie categorias com limites de gastos para ver o progresso da obra aqui.</div>
        <button class="btn btn-outline-primary btn-sm" data-bs-toggle="modal" data-bs-target="#category-modal">
          <i class="bi bi-folder-plus me-1"></i>Criar Categoria
        </button>
      </div>`;
  }

  function getToken() {
    try { return sessionStorage.getItem(TOKEN_KEY); } catch { return null; }
  }

  function saveToken(token) {
    if (!token || typeof token !== 'string') throw new Error('A API não retornou o token de autenticação esperado.');
    token = token.replace(/^Bearer\s+/i, '').trim();
    if (!token || /[\r\n]/.test(token)) throw new Error('A API retornou um token inválido.');
    try { sessionStorage.setItem(TOKEN_KEY, token); }
    catch { throw new Error('Não foi possível guardar o token nesta sessão do navegador.'); }
  }

  function showAuthScreen(message = '') {
    $('auth-screen').classList.remove('d-none');
    document.body.style.overflow = 'hidden';
    setAuthError(message);
  }

  function hideAuthScreen() {
    $('auth-screen').classList.add('d-none');
    document.body.style.overflow = '';
    setAuthError('');
  }

  function setAuthError(message) {
    const box = $('auth-error');
    box.textContent = message;
    box.style.display = message ? 'block' : 'none';
  }

  function authMode(mode) {
    const registering = mode === 'register';
    $('login-form').classList.toggle('d-none', registering);
    $('register-form').classList.toggle('d-none', !registering);
    $('show-login').classList.toggle('active', !registering);
    $('show-register').classList.toggle('active', registering);
    $('auth-title').textContent = registering ? 'Criar Nova Conta' : 'Bem-vindo ao FinaObra';
    $('auth-subtitle').textContent = registering ? 'Cadastre seus dados para começar a gerenciar sua obra.' : 'Acesse para acompanhar despesas e o orçamento da sua construção.';
    setAuthError('');
  }

  function httpMessage(status) {
    if (status === 400) return 'Os dados enviados são inválidos. Revise os campos.';
    if (status === 401) return 'Usuário/senha inválidos ou sessão expirada.';
    if (status === 403) return 'Você não tem permissão para realizar esta operação.';
    if (status === 404) return 'O recurso solicitado não foi encontrado.';
    if (status === 409) return 'Já existe um registro com esses dados.';
    if (status === 429) return 'Muitas tentativas. Aguarde um instante e tente novamente.';
    if (status >= 500) return 'O servidor encontrou um problema interno.';
    return 'Não foi possível concluir a solicitação.';
  }

  async function readResponse(response) {
    const raw = await response.text();
    let data = null;
    if (raw) { try { data = JSON.parse(raw); } catch { data = null; } }
    if (!response.ok) {
      const error = new Error(data?.detail || httpMessage(response.status));
      error.status = response.status;
      throw error;
    }
    if (response.status === 204 || !raw) return null;
    if (!data) throw new Error('A API retornou uma resposta em formato inesperado.');
    return data;
  }

  async function fetchWithTimeout(url, options) {
    const controller = new AbortController();
    const timeoutId = window.setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS);
    try {
      return await fetch(url, { ...options, signal: controller.signal, credentials: 'omit', cache: 'no-store', redirect: 'error' });
    } catch (error) {
      if (error.name === 'AbortError') throw new Error('A solicitação demorou demais. Verifique a conexão com o servidor.');
      if (error.name === 'TypeError') throw new Error('Não foi possível conectar à API. Verifique a rede ou CORS.');
      throw error;
    } finally { window.clearTimeout(timeoutId); }
  }

  async function authRequest(path, payload) {
    if (!['/auth/login', '/auth/register'].includes(path)) throw new Error('Rota de autenticação não permitida.');
    const response = await fetchWithTimeout(`${API_ORIGIN.origin}${path}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
      body: JSON.stringify(payload)
    });
    return readResponse(response);
  }

  async function request(path, options = {}) {
    const token = getToken();
    if (!token) {
      showAuthScreen('Sua sessão terminou. Entre novamente para continuar.');
      throw new Error('Sua sessão terminou. Entre novamente.');
    }
    if (typeof path !== 'string' || !path.startsWith('/') || path.startsWith('//') || path.includes('\\')) {
      throw new Error('Caminho de API inválido.');
    }
    const response = await fetchWithTimeout(`${API_BASE_URL}${path}`, {
      ...options,
      headers: {
        'Accept': 'application/json',
        ...(options.body ? { 'Content-Type': 'application/json' } : {}),
        ...(options.headers || {}),
        'Authorization': `Bearer ${token}`
      }
    });
    if (response.status === 401) {
      try { sessionStorage.removeItem(TOKEN_KEY); } catch { }
      showAuthScreen('Sua autenticação expirou. Entre novamente.');
    }
    return readResponse(response);
  }

  async function completeLogin(token) {
    saveToken(token);
    hideAuthScreen();
    setApiStatus(false, 'Conectando...');
    await refreshApiData();
    if (apiOnline) notify('Autenticação realizada com sucesso.');
  }

  $('show-login').addEventListener('click', () => authMode('login'));
  $('show-register').addEventListener('click', () => authMode('register'));

  $('login-form').addEventListener('submit', async event => {
    event.preventDefault();
    const button = $('login-submit');
    button.disabled = true;
    button.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Entrando...';
    try {
      const userName = $('login-username').value.trim();
      const password = $('login-password').value;
      if (!userName || !password) throw new Error('Informe usuário e senha.');
      const result = await authRequest('/auth/login', { userName, password });
      if (!result?.token) throw new Error('Resposta de login inválida.');
      await completeLogin(result.token);
    } catch (error) {
      setAuthError(error.message || 'Falha ao autenticar.');
    } finally {
      button.disabled = false;
      button.innerHTML = '<i class="bi bi-box-arrow-in-right me-2"></i>Entrar no Sistema';
      $('login-form').reset();
    }
  });

  $('register-form').addEventListener('submit', async event => {
    event.preventDefault();
    const button = $('register-submit');
    button.disabled = true;
    button.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Criando conta...';
    try {
      const payload = {
        nome: $('register-name').value.trim(),
        email: $('register-email').value.trim(),
        username: $('register-username').value.trim(),
        password: $('register-password').value
      };
      if (!payload.nome || !payload.email || !payload.username || !payload.password) {
        throw new Error('Preencha todos os campos.');
      }
      if (!$('register-email').checkValidity()) {
        throw new Error('Informe um e-mail válido.');
      }
      const result = await authRequest('/auth/register', payload);
      if (result?.token) {
        await completeLogin(result.token);
      } else {
        $('login-username').value = payload.username;
        authMode('login');
        setAuthError('Conta criada! Informe sua senha para entrar.');
      }
    } catch (error) {
      setAuthError(error.message || 'Falha ao criar conta.');
    } finally {
      button.disabled = false;
      button.innerHTML = '<i class="bi bi-person-plus me-2"></i>Criar Minha Conta';
      $('register-form').reset();
    }
  });

  $('logout-btn').addEventListener('click', () => {
    try { sessionStorage.removeItem(TOKEN_KEY); } catch { }
    apiOnline = false;
    categories = [];
    expenses = [];
    setApiStatus(false, 'Sessão encerrada');
    renderCategories();
    renderExpenses();
    showAuthScreen('Sessão encerrada com sucesso.');
    authMode('login');
    $('login-password').value = '';

  });

  async function loadCategoriesFromApi(page = 0) {
    const data = await request(`/categorias?page=${page}&size=${PAGE_SIZE}`);
    if (!data || !Array.isArray(data.content)) throw new Error('Contrato de categorias inesperado.');
    categories = data.content.map(c => ({
      id: c.id,
      nome: c.nome,
      valor_planejado: Number(c.valor_planejado ?? 0)
    }));
    categoriesPage = data.number ?? page;
    categoriesTotalPages = data.totalPages ?? 1;
    updateCategoriesPaginationControls(data.totalElements ?? categories.length);
  }

  async function loadExpensesFromApi(page = 0) {
    const data = await request(`/despesas?page=${page}&size=${PAGE_SIZE}`);
    const items = Array.isArray(data) ? data : data?.content;
    if (!Array.isArray(items)) throw new Error('Contrato de despesas inesperado.');
    expenses = items.map(d => ({
      id: d.id,
      descricao: d.descricao,
      categoria_id: d.idCategoria ?? d.categoria_id ?? d.categoriaId,
      categoriaNome: categoryName(d.idCategoria ?? d.categoria_id ?? d.categoriaId),
      fornecedor: d.fornecedor,
      valor: Number(d.valor ?? 0),
      data: d.data
    }));
    expensesPage = data.number ?? page;
    expensesTotalPages = data.totalPages ?? 1;
    updateExpensesPaginationControls(data.totalElements ?? expenses.length);
  }

  function updateExpensesPaginationControls(totalElements) {
    const info = $('expenses-page-info');
    const prevBtn = $('expenses-prev-page');
    const nextBtn = $('expenses-next-page');
    if (!info || !prevBtn || !nextBtn) return;

    info.textContent = `Página ${expensesPage + 1} de ${Math.max(1, expensesTotalPages)} (${totalElements} registros)`;
    prevBtn.disabled = expensesPage <= 0;
    nextBtn.disabled = expensesPage >= expensesTotalPages - 1;
  }

  function updateCategoriesPaginationControls(totalElements) {
    const info = $('categories-page-info');
    const prevBtn = $('categories-prev-page');
    const nextBtn = $('categories-next-page');
    if (!info || !prevBtn || !nextBtn) return;

    info.textContent = `Página ${categoriesPage + 1} de ${Math.max(1, categoriesTotalPages)} (${totalElements} registros)`;
    prevBtn.disabled = categoriesPage <= 0;
    nextBtn.disabled = categoriesPage >= categoriesTotalPages - 1;
  }

  async function loadDashboardFromApi() {
    const data = await request('/dashboard/resumo');
    $('total-planejado').textContent = money(data.totalPlanejado);
    $('total-realizado').textContent = money(data.totalRealizado);
    $('saldo-restante').textContent = money(data.saldoRestante);

    const saldoNum = Number(data.saldoRestante);
    $('saldo-restante').classList.toggle('text-danger', saldoNum < 0);
    $('saldo-restante').classList.toggle('text-success', saldoNum >= 0);

    const rows = Array.isArray(data.progressoCategorias) ? data.progressoCategorias : [];
    $('category-progress').innerHTML = rows.length ? rows.map(c => {
      const pct = Number(c.percentualGasto ?? (Number(c.valorPlanejado) > 0 ? (Number(c.valorGasto) / Number(c.valorPlanejado)) * 100 : 0));
      const barColor = pct > 100 ? 'var(--color-danger)' : pct >= 85 ? 'var(--color-warning)' : 'var(--color-primary)';

      return `
        <div class="progress-item">
          <div class="progress-meta-row">
            <span class="progress-category-name">${safe(c.categoriaNome || 'Sem categoria')}</span>
            <div class="d-flex align-items-center gap-2">
              <span class="progress-values">${money(c.valorGasto)} <span class="text-muted">/</span> ${money(c.valorPlanejado)}</span>
              <span class="badge ${pct > 100 ? 'bg-danger' : 'bg-light text-muted border'}">${pct.toFixed(1)}%</span>
            </div>
          </div>
          <div class="progress-bar-custom">
            <div class="progress-bar-inner" style="width: ${Math.min(Math.max(pct, 0), 100)}%; background-color: ${barColor}"></div>
          </div>
        </div>`;
    }).join('') : `
      <div class="empty-state">
        <div class="empty-state-icon"><i class="bi bi-pie-chart"></i></div>
        <div class="empty-state-title">Nenhuma categoria cadastrada</div>
        <div class="empty-state-text">Comece adicionando uma categoria para planejar e controlar os custos.</div>
        <button class="btn btn-outline-primary btn-sm" data-bs-toggle="modal" data-bs-target="#category-modal">
          <i class="bi bi-folder-plus me-1"></i>Criar Categoria
        </button>
      </div>`;
  }

  async function refreshApiData() {
    try {
      await loadDashboardFromApi();
      await loadCategoriesFromApi(categoriesPage);
      await loadExpensesFromApi(expensesPage);
      apiOnline = true;
      setApiStatus(true, 'Conectado');
      renderCategories();
      renderExpenses();
    } catch (err) {
      apiOnline = false;
      categories = [];
      expenses = [];
      setApiStatus(false, 'Offline / Indisponível');
      renderCategories();
      renderExpenses();
      if (getToken() && err.status !== 401) notify(err.message || 'Falha ao sincronizar com a API.', true);
    }
  }

  function populateFilterCategorySelect() {
    const filterCat = $('filter-category');
    if (!filterCat) return;
    const currentVal = filterCat.value;
    filterCat.innerHTML = '<option value="">Todas as categorias</option>' +
      categories.map(c => `<option value="${safe(c.id)}">${safe(c.nome)}</option>`).join('');
    if (currentVal) filterCat.value = currentVal;
  }

  function renderExpenses() {
    const query = $('expense-search').value.trim().toLocaleLowerCase('pt-BR');
    const selectedCat = $('filter-category')?.value;
    const dateStart = $('filter-date-start')?.value;
    const dateEnd = $('filter-date-end')?.value;

    const hasActiveFilters = Boolean(query || selectedCat || dateStart || dateEnd);
    $('clear-filters-btn')?.classList.toggle('d-none', !hasActiveFilters);

    const list = expenses.filter(e => {
      // Busca texto
      const textMatch = (e.descricao + ' ' + (e.categoriaNome || categoryName(categoryIdOf(e))) + ' ' + (e.fornecedor || '')).toLocaleLowerCase('pt-BR').includes(query);
      if (!textMatch) return false;

      // Filtro categoria
      if (selectedCat && Number(categoryIdOf(e)) !== Number(selectedCat)) {
        return false;
      }

      // Filtro data
      if (dateStart && e.data && e.data < dateStart) return false;
      if (dateEnd && e.data && e.data > dateEnd) return false;

      return true;
    });

    $('expenses-count').textContent = `${list.length} ${list.length === 1 ? 'despesa' : 'despesas'}`;

    $('expenses-table').innerHTML = list.length ? list.map(e => {
      const id = safe(e.id);
      return `
        <tr>
          <td class="ps-4 fw-bold">${safe(e.descricao)}</td>
          <td><span class="badge-tag">${safe(e.categoriaNome || categoryName(categoryIdOf(e)))}</span></td>
          <td class="#c4b39d small">${safe(e.fornecedor || '—')}</td>
          <td class="#c4b39d small">${dateBR(e.data)}</td>
          <td class="#c4b39d fw-bold font-monospace">${money(e.valor)}</td>
          <td class="text-center">
            <div class="d-inline-flex gap-1">
              <button class="btn-action-icon" data-edit-expense="${id}" aria-label="Editar despesa" title="Editar">
                <i class="bi bi-pencil text-primary"></i>
              </button>
              <button class="btn-action-icon" data-delete-expense="${id}" aria-label="Excluir despesa" title="Excluir">
                <i class="bi bi-trash3 text-danger"></i>
              </button>
            </div>
          </td>
        </tr>`;
    }).join('') : `
      <tr>
        <td colspan="6" class="p-0">
          <div class="empty-state">
            <div class="empty-state-icon"><i class="bi bi-receipt"></i></div>
            <div class="empty-state-title">Nenhum lançamento encontrado</div>
            <div class="empty-state-text">${hasActiveFilters ? 'Nenhum lançamento coincide com os filtros aplicados.' : 'Comece registrando os materiais e serviços adquiridos.'}</div>
            ${!hasActiveFilters ? `<button class="btn btn-primary btn-sm" id="empty-state-new-expense"><i class="bi bi-plus-lg me-1"></i>Novo Lançamento</button>` : ''}
          </div>
        </td>
      </tr>`;

    $('expense-category').innerHTML = '<option value="">Selecione uma categoria...</option>' +
      categories.map(c => `<option value="${safe(c.id)}">${safe(c.nome)}</option>`).join('');

    populateFilterCategorySelect();
  }

  function renderCategories() {
    $('categories-table').innerHTML = categories.length ? categories.map(c => `
      <tr>
        <td class="ps-4 #c4b39d small font-monospace">#${safe(c.id)}</td>
        <td class="fw-bold">${safe(c.nome)}</td>
        <td class="text-end fw-bold font-monospace">${money(categoryBudget(c))}</td>
        <td class="text-center">
          <div class="d-inline-flex gap-1">
            <button class="btn-action-icon" data-edit-category="${safe(c.id)}" aria-label="Editar categoria" title="Editar">
              <i class="bi bi-pencil text-primary"></i>
            </button>
            <button class="btn-action-icon" data-delete-category="${safe(c.id)}" aria-label="Excluir categoria" title="Excluir">
              <i class="bi bi-trash3 text-danger"></i>
            </button>
          </div>
        </td>
      </tr>`).join('') : `
      <tr>
        <td colspan="4" class="p-0">
          <div class="empty-state">
            <div class="empty-state-icon"><i class="bi bi-folder-plus"></i></div>
            <div class="empty-state-title">Nenhuma categoria cadastrada</div>
            <div class="empty-state-text">Defina as fases da sua obra para estabelecer limites orçamentários.</div>
            <button class="btn btn-primary btn-sm" id="empty-state-new-category"><i class="bi bi-plus-lg me-1"></i>Criar Categoria</button>
          </div>
        </td>
      </tr>`;
  }

  document.querySelectorAll('#main-nav [data-view]').forEach(link => link.addEventListener('click', e => {
    e.preventDefault();
    navigate(link.dataset.view);
  }));

  $('theme-toggle').addEventListener('click', () => applyTheme(document.documentElement.dataset.theme === 'dark' ? 'light' : 'dark'));

  $('refresh-dashboard').addEventListener('click', async () => {
    const btn = $('refresh-dashboard');
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Atualizando...';
    await refreshApiData();
    btn.disabled = false;
    btn.innerHTML = '<i class="bi bi-arrow-clockwise me-1"></i>Atualizar Dados';
    if (apiOnline) notify('Painel sincronizado com sucesso.');
  });

  // Filtros avançados
  $('expense-search').addEventListener('input', renderExpenses);
  $('filter-category')?.addEventListener('change', renderExpenses);
  $('filter-date-start')?.addEventListener('change', renderExpenses);
  $('filter-date-end')?.addEventListener('change', renderExpenses);
  $('clear-filters-btn')?.addEventListener('click', () => {
    $('expense-search').value = '';
    if ($('filter-category')) $('filter-category').value = '';
    if ($('filter-date-start')) $('filter-date-start').value = '';
    if ($('filter-date-end')) $('filter-date-end').value = '';
    renderExpenses();
  });

  // Paginação - Despesas
  $('expenses-prev-page')?.addEventListener('click', async () => {
    if (expensesPage > 0) {
      await loadExpensesFromApi(expensesPage - 1);
      renderExpenses();
    }
  });
  $('expenses-next-page')?.addEventListener('click', async () => {
    if (expensesPage < expensesTotalPages - 1) {
      await loadExpensesFromApi(expensesPage + 1);
      renderExpenses();
    }
  });

  // Paginação - Categorias
  $('categories-prev-page')?.addEventListener('click', async () => {
    if (categoriesPage > 0) {
      await loadCategoriesFromApi(categoriesPage - 1);
      renderCategories();
    }
  });
  $('categories-next-page')?.addEventListener('click', async () => {
    if (categoriesPage < categoriesTotalPages - 1) {
      await loadCategoriesFromApi(categoriesPage + 1);
      renderCategories();
    }
  });

  document.querySelectorAll('.money-input').forEach(input => {
    input.addEventListener('input', () => formatMoneyInput(input));
    input.addEventListener('paste', () => requestAnimationFrame(() => formatMoneyInput(input)));
  });

  // EDIÇÃO E EXCLUSÃO DE DESPESA
  $('expenses-table').addEventListener('click', async e => {
    // Editar
    const editBtn = e.target.closest('[data-edit-expense]');
    if (editBtn) {
      const id = editBtn.dataset.editExpense;
      const despesa = expenses.find(x => String(x.id) === String(id));
      if (!despesa) return;

      $('expense-id').value = despesa.id;
      $('expense-title').innerHTML = '<i class="bi bi-pencil text-primary me-2"></i>Editar Lançamento';
      $('expense-description').value = despesa.descricao;
      $('expense-supplier').value = despesa.fornecedor || '';
      $('expense-category').value = String(categoryIdOf(despesa));
      setMoneyInputValue($('expense-value'), despesa.valor);
      $('expense-date').value = despesa.data;
      $('expense-submit-btn').textContent = 'Salvar Alterações';

      expenseModal.show();
      return;
    }

    // Excluir
    const deleteBtn = e.target.closest('[data-delete-expense]');
    if (deleteBtn) {
      const id = deleteBtn.dataset.deleteExpense;
      const confirmed = await confirmDialog('Excluir Lançamento?', 'O registro será removido permanentemente da base de dados.');
      if (!confirmed) return;

      if (!apiOnline) { notify('A API não está disponível no momento.', true); return; }

      try {
        await request(`/despesas/${encodeURIComponent(id)}`, { method: 'DELETE' });
        await loadExpensesFromApi(expensesPage);
        await loadDashboardFromApi().catch(() => { });
        renderExpenses();
        notify('Lançamento removido com sucesso.');
      } catch (err) {
        notify('Não foi possível excluir a despesa na API.', true);
      }
    }
  });

  // EDIÇÃO E EXCLUSÃO DE CATEGORIA
  $('categories-table').addEventListener('click', async e => {
    // Editar
    const editBtn = e.target.closest('[data-edit-category]');
    if (editBtn) {
      const id = Number(editBtn.dataset.editCategory);
      const cat = categories.find(c => Number(c.id) === id);
      if (!cat) return;

      $('category-id').value = cat.id;
      $('category-title').innerHTML = '<i class="bi bi-pencil text-primary me-2"></i>Editar Categoria';
      $('category-name').value = cat.nome;
      setMoneyInputValue($('category-budget'), categoryBudget(cat));
      $('category-submit-btn').textContent = 'Salvar Alterações';

      categoryModal.show();
      return;
    }

    // Excluir
    const deleteBtn = e.target.closest('[data-delete-category]');
    if (deleteBtn) {
      const id = Number(deleteBtn.dataset.deleteCategory);

      if (expenses.some(x => Number(categoryIdOf(x)) === id)) {
        notify('Remova ou reclassifique os lançamentos vinculados a esta categoria antes de excluí-la.', true);
        return;
      }

      const confirmed = await confirmDialog('Excluir Categoria?', 'A categoria e sua meta orçamentária serão removidas.');
      if (!confirmed) return;

      if (!apiOnline) { notify('A API não está conectada.', true); return; }

      try {
        await request(`/categorias/${encodeURIComponent(id)}`, { method: 'DELETE' });
        await loadCategoriesFromApi(categoriesPage);
        renderCategories();
        renderExpenses();
        await refreshApiData();
        notify('Categoria removida.');
      } catch (err) {
        notify('Não foi possível excluir a categoria na API.', true);
      }
    }
  });

  // SUBMIT DESPESA (CRIAR OU ATUALIZAR)
  $('expense-form').addEventListener('submit', async e => {
    e.preventDefault();
    const btn = $('expense-submit-btn');
    const expenseId = $('expense-id').value;
    const isEdit = Boolean(expenseId);

    const categoriaId = Number($('expense-category').value);
    const categoria = categories.find(c => Number(c.id) === categoriaId);
    const descricao = $('expense-description').value.trim();
    const fornecedor = $('expense-supplier').value.trim();
    const valor = parseMoneyInput($('expense-value'));
    const data = $('expense-date').value;

    if (!categoria || !descricao || !fornecedor || !Number.isFinite(valor) || valor < 2 || valor > 999999.99 || !data) {
      notify('Preencha os campos corretamente. O valor mínimo é R$ 2,00.', true);
      return;
    }

    btn.disabled = true;
    btn.innerHTML = `<span class="spinner-border spinner-border-sm me-2"></span>${isEdit ? 'Salvando...' : 'Cadastrando...'}`;

    const payload = { descricao, categoria_id: categoriaId, fornecedor, valor, data };

    try {
      if (isEdit) {
        await request(`/despesas/${encodeURIComponent(expenseId)}`, { method: 'PUT', body: JSON.stringify(payload) });
        notify('Lançamento atualizado com sucesso.');
      } else {
        await request('/despesas', { method: 'POST', body: JSON.stringify(payload) });
        notify('Lançamento registrado com sucesso.');
      }

      expenseModal.hide();
      e.target.reset();
      $('expense-id').value = '';
      $('expense-date').value = todayISO();
      await loadExpensesFromApi(expensesPage);
      renderExpenses();
      await loadDashboardFromApi().catch(() => { });
    } catch (err) {
      notify(err.message || 'Erro ao salvar despesa. Verifique as informações.', true);
    } finally {
      btn.disabled = false;
      btn.textContent = isEdit ? 'Salvar Alterações' : 'Salvar Lançamento';
    }
  });

  // SUBMIT CATEGORIA (CRIAR OU ATUALIZAR)
  $('category-form').addEventListener('submit', async e => {
    e.preventDefault();
    const btn = $('category-submit-btn');
    const catId = $('category-id').value;
    const isEdit = Boolean(catId);

    const nome = $('category-name').value.trim();
    const valor_planejado = parseMoneyInput($('category-budget'));

    if (!nome || !Number.isFinite(valor_planejado) || valor_planejado < 2 || valor_planejado > 999999.99) {
      notify('Informe o nome e um orçamento entre R$ 2,00 e R$ 999.999,99.', true);
      return;
    }

    btn.disabled = true;
    btn.innerHTML = `<span class="spinner-border spinner-border-sm me-2"></span>${isEdit ? 'Salvando...' : 'Cadastrando...'}`;

    const payload = { nome, valor_planejado };

    try {
      if (isEdit) {
        await request(`/categorias/${encodeURIComponent(catId)}`, { method: 'PUT', body: JSON.stringify(payload) });
        notify('Categoria atualizada com sucesso.');
      } else {
        await request('/categorias', { method: 'POST', body: JSON.stringify(payload) });
        notify('Categoria criada com sucesso.');
      }

      categoryModal.hide();
      e.target.reset();
      $('category-id').value = '';
      await loadCategoriesFromApi(categoriesPage);
      renderCategories();
      renderExpenses();
      await refreshApiData();
    } catch (err) {
      notify(err.message || 'Erro ao salvar categoria.', true);
    } finally {
      btn.disabled = false;
      btn.textContent = isEdit ? 'Salvar Alterações' : 'Cadastrar Categoria';
    }
  });

  // RESET MODAL HANDLERS
  $('expense-modal').addEventListener('hidden.bs.modal', () => {
    $('expense-form').reset();
    $('expense-id').value = '';
    $('expense-title').innerHTML = '<i class="bi bi-receipt text-primary me-2"></i>Novo Lançamento';
    $('expense-submit-btn').textContent = 'Salvar Lançamento';
  });

  $('category-modal').addEventListener('hidden.bs.modal', () => {
    $('category-form').reset();
    $('category-id').value = '';
    $('category-title').innerHTML = '<i class="bi bi-folder-plus text-primary me-2"></i>Nova Categoria';
    $('category-submit-btn').textContent = 'Cadastrar Categoria';
  });

  // Empty states CTA delegation
  document.body.addEventListener('click', e => {
    if (e.target.closest('#empty-state-new-expense')) {
      expenseModal.show();
    }
    if (e.target.closest('#empty-state-new-category')) {
      categoryModal.show();
    }
  });

  // SIDEBAR MOBILE TOGGLE
  $('sidebar-toggle')?.addEventListener('click', () => {
    $('app-sidebar')?.classList.toggle('show');
  });

  $('about-btn').addEventListener('click', () => bootstrap.Modal.getOrCreateInstance($('about-modal')).show());
  $('expense-modal').addEventListener('show.bs.modal', () => {
    renderExpenses();
    if (!$('expense-date').value) $('expense-date').value = todayISO();
  });

  applyTheme(getTheme());
  $('expense-date').value = todayISO();
  const initial = location.hash.replace('#', '');
  navigate(['dashboard', 'lancamentos', 'orcamentos'].includes(initial) ? initial : 'dashboard');

  if (getToken()) {
    hideAuthScreen();
    refreshApiData();
  } else {
    showAuthScreen();
    authMode('login');
    setApiStatus(false, 'Não autenticado');
  }

  // PWA Prompt
  let installPrompt = null;
  window.addEventListener('beforeinstallprompt', e => {
    e.preventDefault();
    installPrompt = e;
    $('install-banner')?.classList.remove('d-none');
    $('install-banner')?.classList.add('d-flex');
  });

  $('install-btn')?.addEventListener('click', async () => {
    if (!installPrompt) return;
    installPrompt.prompt();
    await installPrompt.userChoice;
    installPrompt = null;
    $('install-banner')?.classList.add('d-none');
  });

  $('dismiss-install')?.addEventListener('click', () => $('install-banner')?.classList.add('d-none'));

  if ('serviceWorker' in navigator && (location.protocol === 'https:' || location.hostname === 'localhost')) {
    navigator.serviceWorker.register('./sw.js').catch(() => { });
  }
})();
