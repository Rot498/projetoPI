function verificarLogin() {
  const user = localStorage.getItem("user");

  if (!user) {
    alert("Você precisa fazer login!");
    window.location.href = "login.html";
  }
}

function logout() {
  localStorage.removeItem("user");
  localStorage.removeItem("pass");
  window.location.href = "login.html";
}