function login() {
  const user = document.getElementById("user").value;
  const pass = document.getElementById("pass").value;

  const savedUser = localStorage.getItem("user");
  const savedPass = localStorage.getItem("pass");

  if (user === savedUser && pass === savedPass) {
    window.location.href = "dashboard.html";
  } else {
    alert("Login inválido!");
  }
}