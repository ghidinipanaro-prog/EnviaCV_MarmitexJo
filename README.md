# EnviaCV

Sistema web para recebimento de candidaturas e currículos.
Projeto da disciplina de Fábrica de Software, Ciência da Computação, Universidade Guarulhos (UNG).

O candidato preenche um formulário, anexa o currículo em PDF e envia. O sistema valida os dados
no servidor, grava a candidatura no MySQL, guarda o PDF em diretório restrito e envia o currículo
por e-mail ao RH, com confirmação ao candidato. Se o e-mail falhar, o envio entra em uma fila de
reenvio automático.

## Tecnologias

- Java 21, Spring Boot 4 (Web, Data JPA, Validation, Security, Mail)
- MySQL (XAMPP) com Hibernate
- HTML, CSS e JavaScript (formulário em `src/main/resources/static/index.html`)
- Maven (wrapper incluído)

## Como executar

1. Instale o **JDK 21 ou superior** e inicie o **MySQL** (por exemplo, pelo XAMPP, porta 3306).
   O banco `EnviaCV` é criado automaticamente na primeira execução.
2. Na raiz do projeto, rode:

   ```
   mvnw.cmd spring-boot:run        (Windows)
   ./mvnw spring-boot:run          (Linux/macOS)
   ```

3. Abra **http://localhost:8081** no navegador.

Também é possível importar o projeto no Eclipse (File > Import > Existing Maven Projects) e
executar a classe `EnviaCvApplication`.

## Variáveis de ambiente

Todas são opcionais: sem elas, o sistema usa os valores padrão de desenvolvimento.

| Variável | Padrão | Uso |
|---|---|---|
| `DB_HOST`, `DB_PORT`, `DB_NAME` | `localhost`, `3306`, `EnviaCV` | conexão com o MySQL |
| `DB_USER`, `DB_PASSWORD` | `root`, (vazio) | usuário e senha do MySQL |
| `UPLOAD_DIR` | `uploads` | pasta onde os PDFs são gravados |
| `ADMIN_USER`, `ADMIN_PASSWORD` | `admin`, `admin123` | login da consulta restrita |
| `MAIL_HOST`, `MAIL_PORT` | `smtp.gmail.com`, `587` | servidor SMTP |
| `MAIL_USER`, `MAIL_PASSWORD` | (vazio) | credenciais do SMTP |
| `RH_EMAIL` | (vazio) | e-mail que recebe as candidaturas |

Sem as variáveis de e-mail, o envio de candidaturas continua funcionando: os e-mails ficam na
fila de reenvio (tabela `fila_email`) até que o SMTP seja configurado. Para testar e-mails sem
enviá-los de verdade, use uma caixa de testes como o Mailtrap.

> Os valores `admin`/`admin123` são apenas para desenvolvimento. Troque-os em qualquer uso real.

## Endpoints

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| POST | `/api/candidaturas` | público | recebe o formulário (`multipart/form-data`) |
| GET | `/api/candidaturas` | restrito (Basic Auth) | lista as candidaturas |
| GET | `/api/candidaturas/{id}` | restrito (Basic Auth) | consulta uma candidatura |

Campos do POST: `nomeCompleto`, `email`, `telefone`, `vagaPretendida`, `linkedin` (opcional),
`apresentacao` (opcional) e `curriculo` (PDF de até 5 MB).

Respostas: `201` em sucesso, `400` para dados inválidos, `401` sem login, `404` para id
inexistente e `500` para falhas do servidor.

## Estrutura

```
com.enviacv
├── config       segurança (Spring Security)
├── controller   endpoints REST
├── service      regras de negócio, upload, e-mail e fila de reenvio
├── repository   acesso ao banco
├── model        entidades Candidatura e FilaEmail
├── dto          objetos de entrada e saída
└── exception    tratamento global de erros
```

## Documentação

A documentação técnica, o fluxograma e os prints dos testes estão na pasta `docs/`.

## Privacidade (LGPD)

O formulário coleta apenas os dados necessários para a seleção de pessoal e exige consentimento
do candidato. Os currículos ficam em diretório do servidor fora do acesso público, e a consulta
aos dados exige autenticação. A pasta `uploads/` não é versionada.

## Autora

Natalia Ghidini Panaro. Professor orientador: Anselmo.