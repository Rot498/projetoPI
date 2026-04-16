window.onload = function () {
  const user = localStorage.getItem("user");
  document.getElementById("userName").innerText = "👤 " + user;

  atualizarDashboard();
  carregarFrota();
  carregarDevolucoes();
};

function atualizarDashboard() {
  const veiculos = JSON.parse(localStorage.getItem("veiculos")) || [];
  const locacoes = JSON.parse(localStorage.getItem("locacoes")) || [];
  const reservas = JSON.parse(localStorage.getItem("reservas")) || [];

  document.getElementById("totalVeiculos").innerText = veiculos.length;
  document.getElementById("totalLocacoes").innerText = locacoes.length;

  const pendentes = reservas.filter(r => r.status === "Pendente");
  document.getElementById("totalReservas").innerText = pendentes.length;

  const faturamento = locacoes.length * 150;
  document.getElementById("faturamentoTotal").innerText = "R$ " + faturamento.toFixed(2);
}

function carregarFrota() {
  const veiculos = JSON.parse(localStorage.getItem("veiculos")) || [];
  const locacoes = JSON.parse(localStorage.getItem("locacoes")) || [];

  const tabela = document.getElementById("tabelaFrota");
  tabela.innerHTML = "";

  veiculos.forEach(v => {
    const locado = locacoes.find(l => l.veiculo === v.modelo);

    tabela.innerHTML += `
      <tr>
        <td>${v.modelo}</td>
        <td>${v.placa}</td>
        <td>${v.ano}</td>
        <td>${locado ? locado.cliente : "-"}</td>
        <td>
          <span class="badge ${locado ? 'verde' : 'laranja'}">
            ${locado ? 'Alugado' : 'Disponível'}
          </span>
        </td>
      </tr>
    `;
  });
}

function carregarDevolucoes() {
  const locacoes = JSON.parse(localStorage.getItem("locacoes")) || [];

  const tabela = document.getElementById("tabelaDevolucoes");
  tabela.innerHTML = "";

  locacoes.forEach(l => {
    tabela.innerHTML += `
      <tr>
        <td>${l.cliente}</td>
        <td>${l.veiculo}</td>
        <td>${l.fim}</td>
        <td><span class="badge verde">Confirmado</span></td>
      </tr>
    `;
  });
}