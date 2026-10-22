# Projeto Faculdade UNASP - Spring Boot & Maven

Projeto desenvolvido para a disciplina de Programação / Engenharia de Software da UNASP, implementando a modelagem relacional de banco de dados e API REST com chaves estrangeiras utilizando Spring Boot, Spring Data JPA, Maven e H2 Database.

---

## 🏛️ Modelagem do Banco de Dados

- **Professor** (`id_professor`, `nome`, `email`, `departamento`)
- **Curso** (`id_curso`, `nome`, `id_professor` FK -> `Professor`)
- **Aluno** (`id_aluno`, `nome`, `email`, `id_curso` FK -> `Curso`)

---

## 🚀 Como Executar o Projeto

Abra o PowerShell na pasta do projeto (`projeto-unasp`):

```powershell
# 1. Executar a aplicação via Maven Wrapper
.\mvnw.cmd spring-boot:run

# OU executar diretamente o arquivo JAR já gerado
java -jar target\faculdade-0.0.1-SNAPSHOT.jar
```

A aplicação subirá em: `http://localhost:8080`

---

## 🗄️ Console do Banco de Dados H2

Para visualizar as tabelas e dados diretamente no navegador:
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:faculdadedb`
- **User Name**: `sa`
- **Password**: *(em branco)*

---

## 🧪 Como Rodar os Testes Automatizados

```powershell
.\mvnw.cmd test
```

---

## 📬 Testando com o Postman

Importe no Postman o arquivo:
`C:\Users\Neto\Documents\UNASP\UNASP_Faculdade.postman_collection.json`

Requisições incluídas na coleção:
1. `POST /professor`: Cadastra um professor
2. `GET /professor`: Lista todos os professores
3. `POST /curso`: Cadastra um curso informando o `professorId`
4. `GET /curso`: Lista todos os cursos com o nome do professor vinculado
5. `POST /aluno`: Cadastra um aluno informando o `cursoId`
6. `GET /aluno`: Lista todos os alunos com o curso vinculado
