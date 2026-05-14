# 🚗 StreetCar — Sistema de Locação de Veículos

## Estrutura do Projeto

```
streetcar/
├── backend/          ← Spring Boot + H2 (Java 17)
│   ├── pom.xml
│   └── src/main/java/com/streetcar/
│       ├── StreetCarApplication.java
│       ├── controller/
│       │   ├── AuthController.java      ← POST /api/auth/login e /cadastro
│       │   ├── VeiculoController.java   ← CRUD /api/veiculos
│       │   ├── UsuarioController.java   ← CRUD /api/usuarios
│       │   ├── LocacaoController.java   ← CRUD /api/locacoes
│       │   └── ReservaController.java   ← CRUD /api/reservas
│       ├── model/
│       │   ├── Usuario.java
│       │   ├── Veiculo.java
│       │   ├── Locacao.java
│       │   └── Reserva.java
│       ├── repository/
│       │   ├── UsuarioRepository.java
│       │   ├── VeiculoRepository.java
│       │   ├── LocacaoRepository.java
│       │   └── ReservaRepository.java
│       └── service/
│           └── DataSeeder.java          ← Popula dados iniciais no H2
└── frontend/
    ├── login.html          ← Redireciona ADM → dashboard | CLIENTE → minha-area
    ├── cadastro.html
    ├── dashboard.html      ← Área ADM (visão geral)
    ├── minha-area.html     ← Área CLIENTE (reservas, locações)
    ├── veiculos.html       ← CRUD de veículos (ADM)
    ├── clientes.html       ← CRUD de usuários (ADM)
    ├── locacoes.html       ← CRUD de locações (ADM)
    ├── reservas.html       ← Gerenciar reservas (ADM)
    ├── js/
    │   └── api.js          ← Toda comunicação com o backend
    └── css/
        └── style.css
```

---

## ▶️ Como Rodar

### 1. Backend (Spring Boot)

**Pré-requisito:** Java 17+ e Maven instalados.

```bash
cd backend
./mvnw spring-boot:run
```

Ou com Maven instalado:
```bash
cd backend
mvn spring-boot:run
```

O servidor sobe em: **http://localhost:8080**

### 2. Frontend

Abra os arquivos HTML diretamente no navegador.
Para evitar problemas de CORS, use uma extensão como **Live Server** no VS Code:
- Clique com botão direito em `login.html` → "Open with Live Server"

---

## 🔑 Usuários Padrão (criados automaticamente)

| Perfil | E-mail | Senha |
|--------|--------|-------|
| **ADMIN** | admin@streetcar.com | admin123 |
| **CLIENTE** | cliente@teste.com | 1234 |

---

## 🔀 Lógica de Login (ADM vs Cliente)

```
login.html
  └─ POST /api/auth/login
       ├─ perfil = "ADMIN"   → redireciona para dashboard.html
       └─ perfil = "CLIENTE" → redireciona para minha-area.html
```

**Cada página protege seu acesso:** se um CLIENTE tentar acessar dashboard.html,
é redirecionado para minha-area.html (e vice-versa).

---

## 🗄️ Banco H2

Console disponível em: **http://localhost:8080/h2-console**

Configurações:
- **JDBC URL:** `jdbc:h2:mem:streetcardb`
- **User:** `sa`
- **Password:** *(vazio)*

---

## 🌐 API REST — Endpoints

### Auth
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | /api/auth/login | Login (`email`, `senha`) |
| POST | /api/auth/cadastro | Cadastro de novo cliente |

### Veículos
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /api/veiculos | Lista todos |
| GET | /api/veiculos/disponiveis | Só disponíveis |
| POST | /api/veiculos | Criar |
| PUT | /api/veiculos/{id} | Editar |
| DELETE | /api/veiculos/{id} | Excluir |

### Usuários
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /api/usuarios | Lista todos |
| POST | /api/usuarios | Criar |
| PUT | /api/usuarios/{id} | Editar |
| DELETE | /api/usuarios/{id} | Excluir |

### Locações
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /api/locacoes | Lista todas |
| GET | /api/locacoes/cliente/{id} | Por cliente |
| POST | /api/locacoes | Criar (marca veículo como "Alugado") |
| PUT | /api/locacoes/{id} | Atualizar |
| DELETE | /api/locacoes/{id} | Excluir |

### Reservas
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /api/reservas | Lista todas |
| GET | /api/reservas/cliente/{id} | Por cliente |
| POST | /api/reservas | Criar |
| PUT | /api/reservas/{id} | Atualizar (confirmar/cancelar) |
| DELETE | /api/reservas/{id} | Excluir |
