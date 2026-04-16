function cadastrar() {
  const nome = document.getElementById("nome").value;
  const senha = document.getElementById("senha").value;

  localStorage.setItem("user", nome);
  localStorage.setItem("pass", senha);

  alert("Cadastro realizado!");
  window.location.href = "login.html";
}