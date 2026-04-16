let locacoes = JSON.parse(localStorage.getItem("locacoes")) || [];

function carregarSelects() {
  const clientes = JSON.parse(localStorage.getItem("clientes")) || [];
  const veiculos = JSON.parse(localStorage.getItem("veiculos")) || [];

  const selectCliente = document.getElementById("cliente");
  const selectVeiculo = document.getElementById("veiculo");

  selectCliente.innerHTML = "<option>Selecione Cliente</option>";
  selectVeiculo.innerHTML = "<option>Selecione Veículo</option>";

  clientes.forEach(c => {
    selectCliente.innerHTML += `<option>${c.nome}</option>`;
  });

  veiculos.forEach(v => {
    selectVeiculo.innerHTML += `<option>${v.modelo}</option>`;
  });
}

function salvarLocacao() {
  const cliente = document.getElementById("cliente").value;
  const veiculo = document.getElementById("veiculo").value;
  const inicio = document.getElementById("inicio").value;
  const fim = document.getElementById("fim").value;

  if (!cliente || !veiculo || !inicio || !fim) {
    alert("Preencha tudo!");
    return;
  }

  locacoes.push({ cliente, veiculo, inicio, fim });

  localStorage.setItem("locacoes", JSON.stringify(locacoes));

  listarLocacoes();
}

function listarLocacoes() {
  const lista = document.getElementById("listaLocacoes");
  lista.innerHTML = "";

  locacoes.forEach((l, i) => {
    lista.innerHTML += `
      <tr>
        <td>${l.cliente}</td>
        <td>${l.veiculo}</td>
        <td>${l.inicio}</td>
        <td>${l.fim}</td>
        <td>
          <button onclick="excluir(${i})">Excluir</button>
        </td>
      </tr>
    `;
  });
}

function excluir(login) {
  locacoes.splice(login, 1);
  localStorage.setItem("locacoes", JSON.stringify(locacoes));
  listarLocacoes();
}

window.onload = function () {
  carregarSelects();
  listarLocacoes();

  const user = localStorage.getItem("user");
  document.getElementById("userName").innerText = user;
};