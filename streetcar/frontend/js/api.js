// Endereço base da API Spring Boot
const API = "http://localhost:8080/api";

// ===================== AUTH =====================
async function apiLogin(email, senha) {
  const res = await fetch(`${API}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, senha })
  });
  if (!res.ok) throw new Error("Login inválido");
  return res.json();
}

async function apiCadastro(dados) {
  const res = await fetch(`${API}/auth/cadastro`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(dados)
  });
  if (!res.ok) {
    const err = await res.json();
    throw new Error(err.erro || "Erro no cadastro");
  }
  return res.json();
}

// ===================== VEÍCULOS =====================
async function apiGetVeiculos() {
  const res = await fetch(`${API}/veiculos`);
  return res.json();
}
async function apiGetVeiculosDisponiveis() {
  const res = await fetch(`${API}/veiculos/disponiveis`);
  return res.json();
}
async function apiCriarVeiculo(dados) {
  const res = await fetch(`${API}/veiculos`, {
    method: "POST", headers: { "Content-Type": "application/json" },
    body: JSON.stringify(dados)
  });
  if (!res.ok) throw new Error("Erro ao criar veículo");
  return res.json();
}
async function apiEditarVeiculo(id, dados) {
  const res = await fetch(`${API}/veiculos/${id}`, {
    method: "PUT", headers: { "Content-Type": "application/json" },
    body: JSON.stringify(dados)
  });
  if (!res.ok) throw new Error("Erro ao editar veículo");
  return res.json();
}
async function apiDeletarVeiculo(id) {
  const res = await fetch(`${API}/veiculos/${id}`, { method: "DELETE" });
  if (!res.ok) throw new Error("Erro ao deletar veículo");
}

// ===================== USUÁRIOS =====================
async function apiGetUsuarios() {
  const res = await fetch(`${API}/usuarios`);
  return res.json();
}
async function apiCriarUsuario(dados) {
  const res = await fetch(`${API}/usuarios`, {
    method: "POST", headers: { "Content-Type": "application/json" },
    body: JSON.stringify(dados)
  });
  if (!res.ok) throw new Error("Erro ao criar usuário");
  return res.json();
}
async function apiEditarUsuario(id, dados) {
  const res = await fetch(`${API}/usuarios/${id}`, {
    method: "PUT", headers: { "Content-Type": "application/json" },
    body: JSON.stringify(dados)
  });
  if (!res.ok) throw new Error("Erro ao editar usuário");
  return res.json();
}
async function apiDeletarUsuario(id) {
  const res = await fetch(`${API}/usuarios/${id}`, { method: "DELETE" });
  if (!res.ok) throw new Error("Erro ao deletar usuário");
}

// ===================== LOCAÇÕES =====================
async function apiGetLocacoes() {
  const res = await fetch(`${API}/locacoes`);
  return res.json();
}
async function apiGetLocacoesCliente(clienteId) {
  const res = await fetch(`${API}/locacoes/cliente/${clienteId}`);
  return res.json();
}
async function apiCriarLocacao(dados) {
  const res = await fetch(`${API}/locacoes`, {
    method: "POST", headers: { "Content-Type": "application/json" },
    body: JSON.stringify(dados)
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.erro || "Erro ao criar locação");
  }
  return res.json();
}
async function apiEncerrarLocacao(id) {
  const loc = await (await fetch(`${API}/locacoes/${id}`)).json();
  loc.status = "Encerrada";
  const res = await fetch(`${API}/locacoes/${id}`, {
    method: "PUT", headers: { "Content-Type": "application/json" },
    body: JSON.stringify(loc)
  });
  if (!res.ok) throw new Error("Erro ao encerrar locação");
}
async function apiDeletarLocacao(id) {
  await fetch(`${API}/locacoes/${id}`, { method: "DELETE" });
}

// ===================== RESERVAS =====================
async function apiGetReservas() {
  const res = await fetch(`${API}/reservas`);
  return res.json();
}
async function apiGetReservasCliente(clienteId) {
  const res = await fetch(`${API}/reservas/cliente/${clienteId}`);
  return res.json();
}
async function apiCriarReserva(dados) {
  const res = await fetch(`${API}/reservas`, {
    method: "POST", headers: { "Content-Type": "application/json" },
    body: JSON.stringify(dados)
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.erro || "Erro ao criar reserva");
  }
  return res.json();
}
async function apiAtualizarReserva(id, dados) {
  const res = await fetch(`${API}/reservas/${id}`, {
    method: "PUT", headers: { "Content-Type": "application/json" },
    body: JSON.stringify(dados)
  });
  if (!res.ok) throw new Error("Erro ao atualizar reserva");
}
async function apiDeletarReserva(id) {
  await fetch(`${API}/reservas/${id}`, { method: "DELETE" });
}

// ===================== SESSÃO =====================
function getSessao() {
  const dados = sessionStorage.getItem("sessao");
  return dados ? JSON.parse(dados) : null;
}
function setSessao(usuario) {
  sessionStorage.setItem("sessao", JSON.stringify(usuario));
}
function limparSessao() {
  sessionStorage.removeItem("sessao");
}

/**
 * Verifica se há sessão ativa e se o perfil bate com o requerido.
 * Se perfilRequerido for null, qualquer perfil logado é aceito.
 */
function verificarLogin(perfilRequerido = null) {
  const sessao = getSessao();
  if (!sessao) {
    window.location.href = "login.html";
    return null;
  }
  if (perfilRequerido && sessao.perfil !== perfilRequerido) {
    // Redireciona para a área correta do perfil que está logado
    window.location.href = sessao.perfil === "ADMIN" ? "dashboard.html" : "minha-area.html";
    return null;
  }
  return sessao;
}

function logout() {
  limparSessao();
  window.location.href = "login.html";
}
function ir(pagina) {
  window.location.href = pagina;
}
