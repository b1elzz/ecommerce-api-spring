# 🛍️ E-commerce API - Spring Boot

[![CI/CD](https://github.com/b1elzz/ecommerce-api-spring/actions/workflows/ci.yml/badge.svg)](https://github.com/b1elzz/ecommerce-api-spring/actions)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17-007396?logo=java)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?logo=spring)](https://spring.io/projects/spring-boot)

API completa para gerenciamento de e-commerce com arquitetura escalável e boas práticas de desenvolvimento.

## ✅ Funcionalidades Implementadas

- 🔐 **Autenticação JWT**  
  - Registro e login com tokens  
  - Roles (USER/ADMIN)  
  - Proteção de endpoints com Spring Security

- 🛒 **Gestão de Produtos**  
  - CRUD completo com validações  
  - Controle de estoque  
  - Associação com categorias

- 📦 **Fluxo de Pedidos**  
  - Criação com cálculo automático de total  
  - Validação de estoque em tempo real  
  - Integração com RabbitMQ para notificações  
  - Dead Letter Queue para tratamento de falhas

- ⚡ **Performance**  
  - Cache com Redis para consultas frequentes  
  - Paginação e filtros

## 🛠️ Stack Tecnológica Confirmada

| Camada          | Tecnologias Implementadas                                                                 |
|-----------------|------------------------------------------------------------------------------------------|
| **Backend**     | Java 17 • Spring Boot 3.5 • Spring Data JPA • Hibernate Validator                        |
| **Segurança**   | Spring Security • JWT • BCrypt                                                           |
| **Banco**       | PostgreSQL • Redis                                                                       |
| **Mensageria**  | RabbitMQ (com DLQ)                                                                       |
| **DevOps**      | Docker • GitHub Actions • PMD                                                            |
| **Produtividade**| Lombok • MapStruct • Gradle                                                             |

## ⚠️ Pendências (Por Implementar)

- [ ] Documentação Swagger/OpenAPI
- [ ] Testes unitários abrangentes
- [ ] Integração contínua com SonarCloud
- [ ] Sistema de refresh token

## ⚙️ Primeiros Passos

### 🐳 Execução com Docker
```bash
docker-compose up -d --build
```
Acesse:
- API: http://localhost:8080
- RabbitMQ: http://localhost:15672 (admin/admin123!)

### 📚 Endpoints Principais

```http
### Autenticação
POST /auth/registrar
POST /auth/login

### Produtos (Admin)
GET /produtos
POST /produtos
PUT /produtos/{id}

### Pedidos (User)
POST /pedidos
GET /pedidos/{id}
```

## 🏗️ Estrutura do Código (Atual)

```
src/
├── main/
│   ├── java/
│   │   ├── application/       # DTOs e serviços
│   │   ├── domain/            # Entidades e repositories
│   │   ├── infrastructure/    # Configurações técnicas
│   │   └── presentation/      # Controllers
└── docker-compose.yml         # Containers essenciais
```

## 📄 Licença
MIT License - [LICENSE](LICENSE)
