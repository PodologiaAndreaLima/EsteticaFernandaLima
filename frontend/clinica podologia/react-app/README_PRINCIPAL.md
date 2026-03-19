# 📱 Clínica Podologia - Estética Fernanda Lima

Sistema de gerenciamento para clínica de estética e podologia.

---

## 🚀 Quick Start

### Backend
```bash
cd backend
mvn clean compile
mvn clean package -DskipTests
java -jar target/clinica-1.0.0.jar
```

### Frontend
```bash
cd frontend/clinica\ podologia/react-app
npm install
npm run dev
```

**URL:** http://localhost:5173

---

## 🔧 Tecnologias

- **Backend:** Spring Boot 3.x, Spring Security 6, JPA/Hibernate
- **Frontend:** React 18, Vite, Axios
- **Database:** MySQL/PostgreSQL
- **Auth:** JWT Tokens

---

## 📋 Funcionalidades Principais

- ✅ Autenticação com JWT
- ✅ Cadastro de funcionários
- ✅ Gerenciamento de usuários
- ✅ Alter de senha (admin + usuário)
- ✅ Sincronismo bidirecional Funcionário ↔ Usuário
- ✅ Controle de acesso por role (ADMIN, USER)

---

## 🗄️ Estrutura de Pastas

```
projeto/
├── backend/
│   ├── src/
│   │   └── main/java/lima/fernanda/esteticaFernandaLima/
│   │       ├── model/          (Entidades JPA)
│   │       ├── service/        (Lógica de negócio)
│   │       ├── controller/     (Endpoints REST)
│   │       ├── repository/     (Data Access)
│   │       └── dto/            (DTOs de transferência)
│   └── pom.xml
│
└── frontend/clinica podologia/react-app/
    ├── src/
    │   ├── components/      (Componentes React)
    │   ├── pages/          (Páginas)
    │   ├── services/       (Serviços API)
    │   └── hooks/          (Custom Hooks)
    └── package.json
```

---

## 🔑 Credenciais de Teste

```
Email: admin@clinica.com
Senha: Admin@123
Role: ADMIN
```

---

## 📝 Features Recentes

### ✅ Sincronização Bidirecional (v1.0)
- Quando edita funcionário → sincroniza em usuário
- Quando altera senha funcionário → sincroniza em usuário
- Quando edita usuário → sincroniza em funcionário

**Tabelas sincronizadas:**
- Funcionarios ↔ Usuarios

---

## 🧪 Testes

### Teste: Editar Funcionário
1. Login como Admin
2. Vá em Funcionários
3. Clique "Editar"
4. Altere dados (ex: telefone)
5. Clique "Salvar"
6. Verifique no BD se ambas as tabelas atualizaram ✅

### Teste: Alterar Senha
1. Login como Admin
2. Vá em Funcionários
3. Clique "Alterar Senha"
4. Digite nova senha (8+ chars, maiúscula, minúscula, número, especial)
5. Clique "Confirmar"
6. Verifique no BD se ambas as tabelas atualizaram ✅

---

## 🗄️ Database Setup

### Criar coluna de sincronização (se colocou agora)
```sql
ALTER TABLE funcionarios ADD COLUMN usuario_id BIGINT UNIQUE;
ALTER TABLE funcionarios 
ADD CONSTRAINT fk_funcionarios_usuario 
FOREIGN KEY (usuario_id) REFERENCES usuario(id);

-- Popular dados existentes
UPDATE funcionarios f 
SET usuario_id = (SELECT id FROM usuario u WHERE u.email = f.email LIMIT 1)
WHERE usuario_id IS NULL;
```

---

## 🔐 Segurança

- Senhas com BCryptPasswordEncoder (10+ rounds)
- JWT tokens com expiração 24h
- @PreAuthorize no backend para controle de acesso
- Validação de força de senha:
  ```
  Mínimo: 8 caracteres
  ✓ Letra maiúscula (A-Z)
  ✓ Letra minúscula (a-z)
  ✓ Número (0-9)
  ✓ Caractere especial (@$!%*?&)
  
  Exemplo: Test@123
  ```

---

## 🚀 Deployment

### Staging
```bash
# Backend
mvn clean package
scp target/clinica-1.0.0.jar user@staging:/app/

# Frontend
npm run build
scp -r dist/* user@staging:/var/www/clinica/
```

### Produção
```bash
# Mesmo do Staging, apenas mudar host
scp target/clinica-1.0.0.jar user@prod:/app/
scp -r dist/* user@prod:/var/www/clinica/
```

---

## 🐛 Troubleshooting

| Problema | Solução |
|----------|---------|
| Backend não inicia | Verificar BD e variáveis de ambiente |
| Frontend branco | Verificar console (F12) para erros |
| Login falha 401 | Token JWT expirado, fazer novo login |
| Sincronização não funciona | Verificar coluna usuario_id no BD |

---

## 📞 API Endpoints Principais

### Autenticação
- `POST /usuarios/login` - Login
- `POST /usuarios/{id}/alterar-senha` - Change password

### Funcionários
- `GET /funcionarios` - Listar todos
- `GET /funcionarios/{id}` - Buscar por ID
- `POST /funcionarios` - Criar
- `PUT /funcionarios/{id}` - Editar
- `DELETE /funcionarios/{id}` - Deletar
- `POST /funcionarios/{id}/alterar-senha` - Alterar senha (Admin)

### Usuários
- `GET /usuarios` - Listar (Admin only)
- `PUT /usuarios/{id}` - Editar perfil

---

## 📚 Documentação Adicional

Para informações detalhadas sobre:
- Setup completo
- Migração de dados
- Troubleshooting avançado

Execute os testes e consulte os logs.

---

## 📅 Changelog

### v1.0.0 (19/03/2026)
- ✅ Sincronização bidirecional Funcionário ↔ Usuário
- ✅ Alterar senha funcionário (admin)
- ✅ Validação de força de senha
- ✅ JWT Authentication
- ✅ Role-based access control

---

## 👥 Autores

Time de desenvolvimento - Estética Fernanda Lima

---

## 📄 Licença

Proprietary - Estética Fernanda Lima

---

**Última atualização:** 19/03/2026  
**Status:** ✅ Production Ready
