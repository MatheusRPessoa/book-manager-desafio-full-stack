# Book Manager — Desafio Técnico Full-Stack

Aplicação full-stack para gerenciamento pessoal de livros com autenticação JWT, CRUD completo, paginação, filtros avançados e documentação interativa via Swagger.

## Deploy

| Serviço | URL |
|---|---|
| Frontend | https://book-manager.up.railway.app |
| Backend API | https://book-manager-api.up.railway.app |
| Swagger UI | https://book-manager-api.up.railway.app/swagger-ui/index.html |

---

## Stack

**Backend**
- Java 21 + Spring Boot 3.2
- Spring Security + JWT (HttpOnly cookie, `SameSite=None`)
- Spring Data JPA + Hibernate
- PostgreSQL
- SpringDoc OpenAPI (Swagger UI)
- Testcontainers (testes de integração com banco real)

**Frontend**
- Vue 3 + TypeScript
- Vite + Tailwind CSS
- Pinia (gerenciamento de estado)
- Vee-validate + Zod (validação de formulários)
- Axios

**Infraestrutura**
- Docker + Docker Compose (ambiente local completo)
- Railway (deploy backend + frontend + PostgreSQL)

---

## Diferenciais entregues

- **Dockerização completa** — backend, frontend e banco sobem com um único comando
- **Deploy funcional** — aplicação disponível publicamente no Railway
- **Paginação** — endpoint `/books` retorna `Page<BookResponse>` com suporte a `page`, `size` e `sort`
- **Filtros avançados** — filtragem por título, autor e intervalo de ano de publicação
- **Swagger/OpenAPI detalhado** — todos os endpoints, DTOs e modelos documentados com exemplos e constraints
- **Isolamento de dados** — cada usuário visualiza e gerencia apenas seus próprios livros
- **Testes de integração** — cobertura com banco real via Testcontainers (AuthControllerIT, BookControllerIT)
- **Tratamento de erros** — `GlobalExceptionHandler` + estados de erro no frontend
- **JWT via HttpOnly cookie** — mais seguro que localStorage; token não acessível por JavaScript

---

## Executar localmente

### Pré-requisitos
- Docker e Docker Compose instalados

### Subir tudo com Docker Compose

```bash
docker compose up --build
```

| Serviço | URL local |
|---|---|
| Frontend | http://localhost:3000 |
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| PostgreSQL | localhost:5432 |

### Executar backend sem Docker

```bash
# Sobe apenas o banco
make db

# Roda o backend com hot-reload
make backend
```

### Rodar os testes

```bash
make test
```

Os testes de integração sobem um container PostgreSQL via Testcontainers automaticamente — não é necessário banco externo.

---

## Estrutura do projeto

```
book-manager/
├── backend/
│   ├── src/
│   │   ├── main/java/com/bookmanager/
│   │   │   ├── config/          # SecurityConfig, SwaggerConfig
│   │   │   ├── controller/      # AuthController, BookController
│   │   │   ├── dto/             # Request/Response DTOs com @Schema
│   │   │   ├── entity/          # User, Book
│   │   │   ├── exception/       # GlobalExceptionHandler
│   │   │   ├── repository/      # BookRepository (JPQL com filtros)
│   │   │   ├── security/        # JwtAuthFilter, JwtService
│   │   │   └── service/         # AuthService, BookService
│   │   └── resources/
│   │       └── db/migration/schema.sql
│   ├── src/test/                # Testes de integração
│   └── Dockerfile
├── frontend/
│   ├── src/
│   │   ├── components/          # AppHeader, AppInput, BookCard, BookForm
│   │   ├── layouts/             # AuthLayout, DefaultLayout
│   │   ├── pages/               # LoginPage, RegisterPage, BooksPage, ...
│   │   ├── services/            # api.ts, auth.service.ts, books.service.ts
│   │   ├── stores/              # auth.ts, books.ts (Pinia)
│   │   └── types/               # index.ts
│   └── Dockerfile
├── docker-compose.yml
└── Makefile
```

---

## API — Endpoints

### Autenticação

| Método | Rota | Descrição |
|---|---|---|
| POST | `/auth/register` | Criar conta |
| POST | `/auth/login` | Login (retorna token JWT) |
| POST | `/auth/logout` | Logout (limpa cookie) |

### Livros (requer autenticação)

| Método | Rota | Descrição |
|---|---|---|
| GET | `/books` | Listar com filtros e paginação |
| GET | `/books/{id}` | Buscar por ID |
| POST | `/books` | Criar livro |
| PUT | `/books/{id}` | Atualizar livro |
| DELETE | `/books/{id}` | Excluir livro |

**Parâmetros de listagem:**

| Parâmetro | Tipo | Descrição |
|---|---|---|
| `title` | string | Filtro parcial no título |
| `author` | string | Filtro parcial no autor |
| `yearFrom` | integer | Ano mínimo de publicação |
| `yearTo` | integer | Ano máximo de publicação |
| `page` | integer | Página (0-based) |
| `size` | integer | Itens por página |
| `sort` | string | Ex: `title,asc` |

Documentação completa e interativa disponível no [Swagger UI](https://book-manager-api.up.railway.app/swagger-ui/index.html).

---

## Schema do banco

```sql
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    author VARCHAR(150) NOT NULL,
    year INTEGER,
    description TEXT,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## Variáveis de ambiente (backend)

| Variável | Descrição | Padrão |
|---|---|---|
| `SPRING_DATASOURCE_URL` | URL JDBC do PostgreSQL | `jdbc:postgresql://localhost:5432/bookmanager` |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco | `postgres` |
| `JWT_SECRET` | Chave secreta JWT (Base64) | valor de desenvolvimento |
| `JWT_EXPIRATION` | Expiração do token em ms | `86400000` (24h) |
