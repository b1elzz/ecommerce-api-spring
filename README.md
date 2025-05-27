# 🛍️ E-commerce API - Spring Boot

[![CI/CD](https://github.com/b1elzz/ecommerce-api-spring/actions/workflows/ci.yml/badge.svg)](https://github.com/b1elzz/ecommerce-api-spring/actions)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0-85ea2d?logo=openapi)](https://localhost:8080/swagger-ui.html)

API completa para gerenciamento de e-commerce com processamento de pedidos, autenticação JWT e mensageria.

## ✨ Funcionalidades Principais

- ✅ Autenticação JWT com roles de usuário
- 🛒 CRUD de produtos (apenas admin)
- 📦 Fluxo completo de pedidos:
    - Validação de estoque
    - Cálculo automático de total
    - Atualização de status
    - Notificação via RabbitMQ
- 🔒 Spring Security com proteção de endpoints
- 📊 Cache com Redis para melhor performance
- 📄 Documentação OpenAPI 3.0

## 🚀 Tecnologias Utilizadas

| Camada          | Tecnologias                                                                               |
|-----------------|------------------------------------------------------------------------------------------|
| **Core**        | Java 17 • Spring Boot 3.5 • Lombok • MapStruct • Hibernate                                |
| **Segurança**   | Spring Security • JWT • BCrypt                                                      |
| **Banco**       | PostgreSQL • Redis • Spring Data JPA                                                      |
| **Mensageria**  | RabbitMQ • Spring AMQP                                                                    |
| **Qualidade**   | PMD • SonarCloud • GitHub Actions                                                         |

## ⚙️ Configuração do Ambiente

### Pré-requisitos
- Java 17+
- Docker 20.10+
- Gradle 7.6+

### 🐳 Execução com Docker
```bash
# 1. Clone o repositório
git clone https://github.com/b1elzz/ecommerce-api-spring.git
cd ecommerce-api-spring

# 2. Inicie os containers
docker-compose up -d --build

# 3. Acesse a API
http://localhost:8080
```

### 🔧 Configuração Manual
1. Crie o arquivo `.env` na raiz:
```ini
JWT_SECRET="sua_chave_secreta_aqui"
DB_PASSWORD=postgres
```

2. Execute o projeto:
```bash
./gradlew bootRun
```

## 📚 Documentação da API (Ainda não implementado)

Acesse a documentação interativa após iniciar a aplicação:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI Spec: `http://localhost:8080/v3/api-docs`

Exemplo de endpoints principais:
```http
POST /auth/registrar
POST /auth/login
GET /produtos
POST /pedidos
PATCH /pedidos/{id}/status
```

## 🔍 Estrutura do Projeto
```
projeto-raiz
├── src/main/java
│   ├── application       # DTOs, serviços e mapeamentos
│   ├── domain            # Entidades e repositories
│   ├── infrastructure    # Configurações técnicas
│   └── presentation      # Controllers e tratamento de erros
└── docker-compose.yml    # Ambiente de desenvolvimento
```

## 🧪 Testes & Qualidade
```bash
# Executar testes unitários (Ainda não implementado)
./gradlew test

# Verificar qualidade do código
./gradlew pmdMain sonarqube

# Gerar relatório de cobertura
./gradlew jacocoTestReport
```

---