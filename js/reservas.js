let reservas = JSON.parse(localStorage.getItem("reservas")) || [];

function carregarSelects() {
  const clientes = JSON.parse(localStorage.getItem("clientes")) || [];
  const veiculos = JSON.parse(localStorage.getItem("veiculos")) || [];

  const selectCliente = document.getElementById("cliente");
  const selectVeiculo = document.getElementById("veiculo");

  selectCliente.innerHTML = "<option>Cliente</option>";
  selectVeiculo.innerHTML = "<option>Veículo</option>";

  clientes.forEach(c => {
    selectCliente.innerHTML += `<option>${c.nome}</option>`;
  });

  veiculos.forEach(v => {
    selectVeiculo.innerHTML += `<option>${v.modelo}</option>`;
  });
}

function salvarReserva() {
  const cliente = document.getElementById("cliente").value;
  const veiculo = document.getElementById("veiculo").value;
  const inicio = document.getElementById("inicio").value;
  const fim = document.getElementById("fim").value;

  if (!cliente || !veiculo || !inicio || !fim) {
    alert("Preencha tudo!");
    return;
  }

  reservas.push({ cliente, veiculo, inicio, fim, status: "Pendente" });

  localStorage.setItem("reservas", JSON.stringify(reservas));

  listarReservas();
}

function listarReservas() {
  const lista = document.getElementById("listaReservas");
  lista.innerHTML = "";

  reservas.forEach((r, i) => {
    lista.innerHTML += `
  <tr>
    <td>${r.cliente}</td>
    <td>${r.veiculo}</td>
    <td>${r.inicio}</td>
    <td>${r.fim}</td>
    <td>
      <span class="badge ${r.status === 'Ativo' ? 'verde' : 'laranja'}">
        ${r.status}
      </span>
    </td>
    <td>
      <div class="acoes-botoes">
        <div class="linha-botoes">
          <button onclick="checkin(${i})">Check-in</button>
          <button onclick="checkout(${i})">Check-out</button>
        </div>

        <button class="btn-excluir" onclick="excluirReserva(${i})">
          Excluir
        </button>
      </div>
    </td>
  </tr>
`;
  });
}

function excluirReserva(login) {
  const confirmar = confirm("Deseja excluir essa reserva?");

  if (!confirmar) return;

  reservas.splice(login, 1);

  localStorage.setItem("reservas", JSON.stringify(reservas));

  listarReservas();
}

function checkin(login) {
  reservas[login].status = "Ativo";
  salvarNoLocalStorage();
}

function checkout(login) {
  reservas[login].status = "Finalizado";
  salvarNoLocalStorage();
}

function salvarNoLocalStorage() {
  localStorage.setItem("reservas", JSON.stringify(reservas));
  listarReservas();
}

window.onload = function () {
  carregarSelects();
  listarReservas();

  const user = localStorage.getItem("user");
  document.getElementById("userName").innerText = user;
};