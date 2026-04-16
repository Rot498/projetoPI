const user = localStorage.getItem("user");
document.getElementById("userName").innerText = user;

let veiculos = JSON.parse(localStorage.getItem("veiculos")) || [];

function salvarVeiculo() {
  const modelo = document.getElementById("modelo").value;
  const placa = document.getElementById("placa").value;
  const ano = document.getElementById("ano").value;

  if (!modelo || !placa || !ano) {
    alert("Preencha todos os campos!");
    return;
  }

  veiculos.push({ modelo, placa, ano });

  localStorage.setItem("veiculos", JSON.stringify(veiculos));

  limparCampos();
  listarVeiculos();
}

function listarVeiculos() {
  const lista = document.getElementById("listaVeiculos");
  lista.innerHTML = "";

  veiculos.forEach((v, i) => {
    lista.innerHTML += `
      <tr>
        <td>${v.modelo}</td>
        <td>${v.placa}</td>
        <td>${v.ano}</td>
        <td>
          <button onclick="excluir(${i})">Excluir</button>
        </td>
      </tr>
    `;
  });
}

function excluir(login) {
  veiculos.splice(login, 1);
  localStorage.setItem("veiculos", JSON.stringify(veiculos));
  listarVeiculos();
}

function limparCampos() {
  document.getElementById("modelo").value = "";
  document.getElementById("placa").value = "";
  document.getElementById("ano").value = "";
}

window.onload = listarVeiculos;