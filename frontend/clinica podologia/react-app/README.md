# Clínica de Podologia - Sistema de Autenticação em React

Este projeto é uma conversão do sistema de autenticação HTML/CSS/JS original para React, mantendo a mesma aparência visual e adicionando funcionalidades modernas de React.

## Estrutura do Projeto

```
react-app/
├── public/
│   └── index.html
├── src/
│   ├── assets/        # Imagens e recursos estáticos
│   ├── components/    # Componentes reutilizáveis (Header)
│   ├── contexts/      # Contextos do React (AuthContext)
│   ├── pages/         # Páginas/Views da aplicação
│   │   ├── Login.js
│   │   └── Register.js
│   ├── styles/        # Arquivos CSS globais
│   ├── App.js         # Configuração de rotas
│   └── index.js       # Ponto de entrada da aplicação
└── package.json
```

## Funcionalidades

- Tela de Login com validação
- Tela de Cadastro com validação de formulário
- Navegação entre as páginas usando React Router
- Autenticação simulada usando Context API
- Layout responsivo

## Como Executar o Projeto

### Pré-requisitos

- Node.js (versão 14 ou superior)
- npm ou yarn

### Instalação

1. Clone o repositório ou copie os arquivos para seu ambiente local
2. Navegue até a pasta do projeto:
   ```
   cd react-app
   ```
3. Instale as dependências:
   ```
   npm install
   # ou
   yarn install
   ```
4. Inicie o servidor de desenvolvimento:
   ```
   npm start
   # ou
   yarn start
   ```
5. Acesse `http://localhost:3000` no seu navegador

## Credenciais de Teste

Para testar o login, use:

- Email: `teste@exemplo.com`
- Senha: `123456`

## Próximos Passos

- Integração com backend real
- Implementação de validação avançada nos formulários
- Persistência de autenticação
- Recuperação de senha
- Perfil de usuário
