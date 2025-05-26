# 🛒 Sistema de Pedidos com Pagamento e Notificação

[![Java](https://img.shields.io/badge/Java-17-blue)]()
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.0-green)]()
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-orange)]()

## Tecnologias Principais
- **Backend**: Spring Boot 3 + Spring Security + JWT
- **Banco de Dados**: PostgreSQL
- **Cache**: Redis
- **Mensageria**: RabbitMQ
- **Ferramentas**: Docker, Lombok, MapStruct

## Como Executar

1. Clone o projeto:
```bash
git clone https://github.com/seu-usuario/ecommerce-pedidos-spring.git
```

2. Inicie os containers:
```bash
docker-compose up -d
```

3. Acesse:
```
http://localhost:8080
```

## Variáveis de Ambiente
Crie um arquivo `.env` na raiz com:
```ini
JWT_SECRET=sua_chave_secreta_aqui
DB_PASSWORD=senha_do_banco
```

## Desenvolvimento
```bash
./gradlew bootRun
```

## Testes
```bash
./gradlew test
```

> **Dica**: Configure seu IDE para usar Java 17 e Spring Boot 3.5+
