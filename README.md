# 🏘️ Real State AI Backend

> Uma API moderna de gestão de imóveis com busca semântica alimentada por IA

Olá! 👋 Seja muito bem-vindo(a) ao **Real State AI Backend**, um projeto criado com muito carinho para explorar as melhores práticas do ecossistema Spring Boot, enquanto constrói uma solução real e funcional para o mercado imobiliário.

---

## 📖 Sobre o Projeto

Este projeto nasceu da vontade de aprofundar conhecimentos em **Spring Boot** e suas capacidades avançadas, criando uma API robusta e moderna para gestão de imóveis (vendas e aluguéis). O diferencial? Uma integração inteligente com **IA (Gemini)** que permite buscas semânticas naturais!

### 🎯 Objetivos de Aprendizado

- **JPA Specifications**: Implementação de filtros complexos e dinâmicos com type-safe queries
- **Arquitetura em Camadas**: Separação clara de responsabilidades (Controllers, UseCases, Services, Repositories)
- **Respostas Padronizadas**: API responses consistentes com `ApiResponse<T>` e tratamento de erros
- **Error Handling Centralizado**: `@ControllerAdvice` com exception handlers globais
- **Paginação**: Implementação de paginação eficiente com metadados completos
- **Integração com IA**: Busca semântica usando Google Gemini para interpretação de linguagem natural
- **Autenticação JWT**: Security com OAuth2 Resource Server e tokens JWT
- **RBAC (Role-Based Access Control)**: Sistema de permissões baseado em roles
- **Migrations**: Controle de versão do banco de dados com Flyway
- **Docker Compose**: Containerização do ambiente de desenvolvimento

---

## ✨ Principais Funcionalidades

### 🔐 Autenticação e Autorização

- Sistema completo de registro e login com JWT
- Tokens RSA assimétricos (chaves pública/privada)
- RBAC com roles `USER` e `ADMIN`
- Proteção de endpoints com `@PreAuthorize`

### 🏠 Gestão de Imóveis

- **CRUD completo** de propriedades (Create, Read, Update, Delete)
- Suporte para imóveis de **venda** e **aluguel**
- Tipos de imóveis: Casa, Apartamento, Terreno, Comercial, etc.
- Status: Disponível, Alugado, Vendido, Manutenção

### 🔍 Busca Avançada

- **Filtros dinâmicos** com JPA Specifications:
  - Por tipo de imóvel
  - Por status
  - Por tipo de anúncio (venda/aluguel)
  - Por faixa de preço
  - Por número de quartos/banheiros
  - Por área
  - Por localização (cidade, estado, país)
  
- **Busca Semântica com IA**:

  ```
  "Quero um apartamento com 3 quartos em São Paulo por até 500 mil"
  → A IA converte para filtros estruturados automaticamente!
  ```

### 📄 Paginação

- Sistema robusto de paginação com metadados:
  - Número da página atual
  - Tamanho da página
  - Total de elementos
  - Total de páginas
  - Indicador de próxima página

### 🛡️ Tratamento de Erros

- Exception handling centralizado
- Mensagens de erro padronizadas
- HTTP status codes apropriados
- Validações com Bean Validation

---

## 🚀 Tecnologias Utilizadas

| Tecnologia | Versão    | Finalidade |
|-----------|-----------|-----------|
| Java | 21        | Linguagem principal |
| Spring Boot | 3.4.1     | Framework base |
| Spring Data JPA | -         | Camada de persistência |
| Spring Security | -         | Autenticação e autorização |
| PostgreSQL | latest    | Banco de dados relacional |
| Flyway | -         | Migrations e versionamento do BD |
| Google Gemini | 2.5-flash | Modelo de IA para busca semântica |
| JWT (OAuth2) | -         | Tokens de autenticação |
| Lombok | -         | Redução de boilerplate |
| Maven | -         | Gerenciamento de dependências |
| Docker Compose | -         | Orquestração de containers |

---

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas bem definida:

```
┌─────────────────────────────────────────┐
│         Controllers (API Layer)          │  ← Recebe requisições HTTP
├─────────────────────────────────────────┤
│      Use Cases (Business Logic)          │  ← Orquestra regras de negócio
├─────────────────────────────────────────┤
│       Services (Domain Services)         │  ← Lógica de domínio
├─────────────────────────────────────────┤
│    Repositories (Data Access Layer)      │  ← Acesso ao banco de dados
├─────────────────────────────────────────┤
│         Database (PostgreSQL)            │  ← Persistência
└─────────────────────────────────────────┘
```

### Padrões Implementados

- **Use Case Pattern**: Cada operação de negócio é um UseCase independente
- **Repository Pattern**: Abstração da camada de dados
- **DTO Pattern**: Objetos de transferência separados das entidades
- **Mapper Pattern**: Conversão entre entidades e DTOs
- **Specification Pattern**: Queries dinâmicas type-safe

---

## 📋 Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- ☕ **Java 21** ou superior ([Download](https://adoptium.net/))
- 🐳 **Docker** e **Docker Compose** ([Download](https://www.docker.com/))
- 🔧 **Maven** 3.8+ (ou use o wrapper incluído `./mvnw`)
- 🔑 **API Key do Google Gemini** ([Obtenha aqui](https://makersuite.google.com/app/apikey))

---

## 🎮 Como Rodar o Projeto

### 1️⃣ Clone o Repositório

```bash
git clone https://github.com/seu-usuario/real_state_ai_backend.git
cd real_state_ai_backend
```

### 2️⃣ Configure as Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto ou exporte a variável:

```bash
export GEMINI_API_KEY=sua_chave_api_aqui
```

Ou no Windows (PowerShell):

```powershell
$env:GEMINI_API_KEY="sua_chave_api_aqui"
```

### 3️⃣ Suba o Banco de Dados

O projeto usa Docker Compose para gerenciar o PostgreSQL. Basta rodar:

```bash
docker-compose up -d
```

Isso iniciará um container PostgreSQL na porta **5434** com:

- Database: `real_state_db`
- User: `postgres`
- Password: `postgres`

### 4️⃣ Gere as Chaves RSA (Se necessário)

O projeto já inclui chaves de exemplo em `src/main/resources/`

```bash
# Gerar chave privada
openssl genrsa -out src/main/resources/app.key 2048

# Gerar chave pública
openssl rsa -in src/main/resources/app.key -pubout -out src/main/resources/app.pub
```

### 5️⃣ Execute a Aplicação

Com Maven wrapper (recomendado):

```bash
./mvnw spring-boot:run
```

Ou se você tem Maven instalado:

```bash
mvn spring-boot:run
```

### 6️⃣ Acesse a API

A aplicação estará disponível em: **<http://localhost:8080>**

---

## 📡 Endpoints da API

### 🔐 Autenticação

#### Registrar Usuário

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "senha123"
}
```

#### Login

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "johndoe",
  "password": "senha123"
}
```

**Resposta:**

```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJSUzI1NiIs...",
    "expiresIn": 3600
  },
  "message": "Login successful",
  "timestamp": "2024-12-17T10:30:00"
}
```

### 🏠 Propriedades

#### Listar Propriedades (com filtros e paginação)

```http
GET /api/v1/properties?page=0&size=15&propertyType=APARTMENT&minPrice=100000&maxPrice=500000
```

#### Buscar Propriedade por ID

```http
GET /api/v1/properties/{id}
```

#### Busca Semântica com IA ✨

```http
POST /api/v1/properties/search
Content-Type: application/json

{
  "text": "Preciso de uma casa com 4 quartos em Campinas para comprar até 800 mil reais"
}
```

**Resposta:** A IA converte sua busca natural em filtros estruturados!

```json
{
  "success": true,
  "data": {
    "propertyType": "HOUSE",
    "listingType": "SALE",
    "minBedrooms": 4,
    "maxPrice": 800000,
    "city": "Campinas"
  },
  "message": "AI search completed successfully"
}
```

#### Criar Propriedade para Venda 🔒

```http
POST /api/v1/properties/sale
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Casa Moderna no Centro",
  "description": "Linda casa com 3 quartos...",
  "price": 450000,
  "propertyType": "HOUSE",
  "bedrooms": 3,
  "bathrooms": 2,
  "area": 150.5,
  "address": {
    "street": "Rua das Flores",
    "number": "123",
    "city": "São Paulo",
    "state": "SP",
    "country": "Brasil",
    "zipCode": "01234-567"
  },
  "images": [
    { "url": "https://...", "description": "Fachada" }
  ]
}
```

#### Criar Propriedade para Aluguel 🔒

```http
POST /api/v1/properties/rental
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Apartamento Mobiliado",
  "description": "Apto 2 quartos mobiliado...",
  "monthlyRent": 2500,
  "propertyType": "APARTMENT",
  "bedrooms": 2,
  "bathrooms": 1,
  "area": 65.0,
  "address": { ... }
}
```

#### Atualizar Propriedade 🔒

```http
PUT /api/v1/properties/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Novo título",
  "price": 480000,
  "status": "AVAILABLE"
}
```

#### Deletar Propriedade 🔒

```http
DELETE /api/v1/properties/{id}
Authorization: Bearer {token}
```

### 📝 Formato Padrão das Respostas

Todas as respostas seguem o formato `ApiResponse<T>`:

```json
{
  "success": true,
  "data": { ... },
  "message": "Operation completed successfully",
  "timestamp": "2024-12-17T10:30:00"
}
```

Para paginação, o formato é `PagedResponse<T>`:

```json
{
  "success": true,
  "data": {
    "content": [ ... ],
    "currentPage": 0,
    "pageSize": 15,
    "totalElements": 100,
    "totalPages": 7,
    "hasNext": true
  },
  "message": "Properties retrieved successfully"
}
```

---

## 🗂️ Estrutura do Projeto

```
src/main/java/com/yuricosta/real_state_ai_backend/
│
├── properties/                    # Módulo de Propriedades
│   ├── ai/                       # Integração com IA
│   │   ├── GeminiConfig.java
│   │   ├── GeminiPrompt.java
│   │   └── SemanticSearchService.java
│   ├── controllers/              # Endpoints REST
│   ├── dtos/                     # Data Transfer Objects
│   ├── enums/                    # Enums de domínio
│   ├── mappers/                  # Conversão Entity ↔ DTO
│   ├── repositories/             # Acesso a dados
│   ├── specifications/           # JPA Specifications
│   ├── useCases/                 # Regras de negócio
│   ├── Property.java             # Entidade principal
│   └── Address.java              # Entidade de endereço
│
├── security/                      # Módulo de Segurança
│   ├── controllers/
│   ├── dtos/
│   ├── AuthService.java
│   ├── SecurityConfig.java
│   └── UserDetailsService.java
│
├── users/                         # Módulo de Usuários
│   ├── User.java
│   ├── UserRepository.java
│   └── UserService.java
│
├── roles/                         # Módulo de Roles
│   ├── Role.java
│   └── RoleRepository.java
│
└── shared/                        # Utilitários compartilhados
    ├── errors/
    │   ├── GlobalExceptionHandler.java
    │   └── NotFoundException.java
    ├── ApiResponse.java
    ├── PagedResponse.java
    └── BaseEntity.java
```

---

## 🎨 Tipos de Propriedades

O sistema suporta diversos tipos de imóveis:

- `HOUSE` - Casa
- `APARTMENT` - Apartamento
- `CONDO` - Condomínio
- `LAND` - Terreno
- `COMMERCIAL` - Comercial
- `FARM` - Fazenda/Sítio

## 📊 Status das Propriedades

- `DRAFTED` - Em rascunho
- `PUBLISHED` - Publicado
- `PAUSED` - Pausado

## 📝 Tipos de Anúncio

- `SALE` - Venda
- `RENTAL` - Aluguel

---

## 🔍 Exemplos de Filtros Avançados

### Busca por Faixa de Preço e Tipo

```
GET /api/v1/properties?propertyType=APARTMENT&minPrice=200000&maxPrice=400000
```

### Busca por Localização e Características

```
GET /api/v1/properties?city=São Paulo&minBedrooms=2&minBathrooms=1&minArea=50
```

### Busca por Status e Tipo de Anúncio

```
GET /api/v1/properties?status=AVAILABLE&listingType=RENTAL
```

### Busca Semântica (Linguagem Natural)

```
POST /api/v1/properties/search
{
  "text": "Apartamento para alugar com 2 quartos em Campinas até 3000 reais"
}
```

---

## 🐛 Troubleshooting

### Problema: "Port 5434 already in use"

**Solução:** Outra aplicação está usando a porta. Altere a porta em `compose.yaml` ou pare o serviço conflitante.

### Problema: "GEMINI_API_KEY not found"

**Solução:** Certifique-se de exportar a variável de ambiente antes de iniciar a aplicação.

### Problema: Erro de Flyway migration

**Solução:** Se necessário, limpe o banco e execute novamente:

```bash
docker-compose down -v
docker-compose up -d
./mvnw spring-boot:run
```

### Problema: JWT Token inválido

**Solução:** Verifique se as chaves `app.key` e `app.pub` existem em `src/main/resources/`.

---

## 🎓 Aprendizados e Conceitos Aplicados

### JPA Specifications

- Queries dinâmicas e compostas
- Type-safe criteria API
- Filtros reutilizáveis e combináveis

### Spring Security

- OAuth2 Resource Server
- JWT com chaves RSA
- Method-level security com `@PreAuthorize`

### API Design

- RESTful best practices
- Versionamento de API (`/api/v1`)
- Respostas consistentes
- HTTP status codes corretos

### Integração com IA

- Prompt engineering
- Parsing de respostas de LLMs
- Fallback e error handling

---

## 🚧 Melhorias Futuras (TODOs)

- [ ] Adicionar Swagger/OpenAPI documentation
- [ ] Implementar logging estruturado (Logback/SLF4J)
- [ ] Adicionar testes de integração
- [ ] Implementar cache com Redis
- [ ] Adicionar upload de imagens real (S3/Cloudinary)
- [ ] Validação de propriedade por usuário (ownership)
- [ ] Rate limiting
- [ ] Métricas e monitoring (Prometheus/Grafana)
- [ ] CI/CD pipeline

---

## 📄 Licença

Este projeto é de código aberto e está disponível para fins educacionais.

---

## 👨‍💻 Autor

Desenvolvido por **Yuri Costa**

---

## 🎉 Considerações Finais

Obrigado por conferir este projeto! Espero que ele possa ser útil tanto para aprendizado quanto como referência para suas próprias implementações.

Se você encontrou algum bug, tem sugestões de melhorias ou quer discutir alguma decisão arquitetural, fique à vontade para abrir uma issue ou enviar um PR. Toda contribuição é muito bem-vinda! 🚀

**Happy coding!** 🎈✨

---

<div align="center">
  <sub>Feito com dedicação para a comunidade dev 💙</sub>
</div>
