# 🏛️ UNASP - Sistema de Gestão Acadêmica & Debug Kanban

Projeto desenvolvido para a disciplina de Banco de Dados / Engenharia de Software da **UNASP**, implementando a modelagem relacional de banco de dados e API REST com chaves estrangeiras utilizando **Spring Boot**, **Spring Data JPA**, **MySQL**, **H2 Database**, **Docker Compose** e **Frontend Web com Monitor Kanban**.

---

## 📋 Modelagem Relacional do Banco de Dados

Conforme as instruções do projeto:
- **Professor** (`id_professor`, `nome`, `email`, `departamento`)
- **Curso** (`id_curso`, `nome`, chave estrangeira `id_professor` referenciando `Professor`)
- **Aluno** (`id_aluno`, `nome`, `email`, chave estrangeira `id_curso` referenciando `Curso`)

---

## ⚡ Como Rodar o Projeto (Portátil - "Apenas Baixar e Funcionar")

O ecossistema completo é 100% conteinerizado e portátil. Basta ter o Docker instalado e rodar um único comando:

```bash
docker compose up --build
```

Isso inicializará automaticamente:
1. 🗄️ **Banco MySQL 8.0**: na porta `3306` com healthcheck e banco criado.
2. ☕ **Backend Spring Boot 3.3.4**: na porta `8080`.
3. 💻 **Frontend Web Nginx**: na porta `3000`.

### 🌐 Endereços de Acesso:
- **Interface Web (CRUD + Kanban)**: [http://localhost:3000](http://localhost:3000)
- **API REST**: [http://localhost:8080](http://localhost:8080)
- **Console H2 (em modo standalone)**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

---

## 🌟 Funcionalidades Especiais

### 1. CRUD Completo com Chaves Estrangeiras Simplificadas
- **Professores**: Cadastro e listagem.
- **Cursos**: Cadastro vinculando o professor via `select` dinâmico (o front envia o `professorId` automaticamente).
- **Alunos**: Cadastro vinculando o curso via `select` dinâmico (o front envia o `cursoId` automaticamente).

### 2. 📊 Aba Debug Kanban (Monitor de Requisições do Postman ao Vivo)
- O backend possui um interceptador em tempo real (`DebugKanbanFilter`).
- Qualquer requisição feita pelo **Postman**, **curl** ou navegador cai **instantaneamente** no quadro Kanban da aba Debug:
  - 🟢 **Coluna Sucesso (2xx)** (ex: POST `/professor` 201 Created)
  - 🟡 **Coluna Erros de Validação (4xx)** (ex: POST `/curso` 404 Professor Inexistente)
  - 🔴 **Coluna Falhas de Servidor (5xx)**
- Cada card exibe método, rota, horário, status, duração em ms e botão para ver o JSON do Request e do Response recebidos!

---

## 📬 Coleção do Postman

O repositório já inclui a coleção completa e automatizada:
`UNASP_Faculdade.postman_collection.json`

### Como importar e rodar:
1. Abra o Postman e clique em **Import**.
2. Selecione o arquivo `UNASP_Faculdade.postman_collection.json`.
3. Você pode rodar as requisições em ordem ou clicar em **"Run Collection"** para executar toda a bateria de testes com relatórios automatizados (`pm.test`).

---

## 🛠️ Execução Local sem Docker (Desenvolvimento)

Caso queira executar diretamente com o Maven Wrapper local:

```powershell
cd projeto-unasp
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Para abrir o frontend estático: basta abrir o arquivo `frontend/index.html` em qualquer navegador!
