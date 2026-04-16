let clientes = JSON.parse(localStorage.getItem("clientes")) || [];

function salvarCliente() {
  const nome = document.getElementById("nome").value;
  const email = document.getElementById("email").value;
  const telefone = document.getElementById("telefone").value;

  if (!nome || !email || !telefone) {
    alert("Preencha todos os campos!");
    return;
  }

  clientes.push({ nome, email, telefone });

  localStorage.setItem("clientes", JSON.stringify(clientes));

  limparCampos();
  listarClientes();
}

function listarClientes() {
  const lista = document.getElementById("listaClientes");
  lista.innerHTML = "";

  clientes.forEach((c, i) => {
    lista.innerHTML += `
      <tr>
        <td>${c.nome}</td>
        <td>${c.email}</td>
        <td>${c.telefone}</td>
        <td>
          <button onclick="excluirCliente(${i})">Excluir</button>
        </td>
      </tr>
    `;
  });
}

function excluirCliente(login) {
  clientes.splice(login, 1);
  localStorage.setItem("clientes", JSON.stringify(clientes));
  listarClientes();
}

function limparCampos() {
  document.getElementById("nome").value = "";
  document.getElementById("email").value = "";
  document.getElementById("telefone").value = "";
}

window.onload = function () {
  listarClientes();

  const user = localStorage.getItem("user");
  document.getElementById("userName").innerText = user;
};