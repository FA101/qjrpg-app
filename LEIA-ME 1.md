# Como usar este pacote

## 1. Gerar o projeto-base

Acesse https://start.spring.io e configure:
- Project: Maven
- Language: Java
- Spring Boot: a versao estavel mais recente da serie 3.x
- Group: com.qjrpg
- Artifact: api
- Java: 21
- Dependencies: Spring Web, Spring Data JPA, PostgreSQL Driver, Validation

Clique em Generate, baixe o zip e extraia — esse vira o seu projeto `qjrpg-api`.

## 2. Copiar estes arquivos

Copie o conteudo das pastas `src/main` e `src/test` deste pacote para dentro do projeto
gerado, mantendo os mesmos caminhos (a estrutura de pacotes `com.qjrpg.api.evento` e
`com.qjrpg.api.shared.exception` ja vem pronta aqui). O `application.yml` gerado pelo
Initializr pode ser substituido pelo daqui (ou mesclado, se preferir).

## 3. Criar o banco local

No pgAdmin4, crie um banco chamado `qjrpg` (usuario/senha padrao `postgres`/`postgres`,
ou ajuste esses valores no `application.yml`). Nao precisa criar tabelas — o Hibernate
cria a tabela `eventos` sozinho na primeira execucao (`ddl-auto: update`).

## 4. Rodar

No terminal, dentro da pasta do projeto:

```
mvnw spring-boot:run
```

(no Windows, sem o `./` na frente). A API sobe em `http://localhost:8080`.

## 5. Rodar os testes

```
mvnw test
```

Os 5 testes de `EventoServiceImplTest` devem passar sem precisar do banco rodando —
eles usam um repositorio mockado (Mockito), nao o Postgres real.

## 6. Testar manualmente (opcional)

Com a API rodando, teste no navegador ou em um cliente HTTP (ex: extensao REST Client do
VS Code):

```
GET  http://localhost:8080/api/eventos
POST http://localhost:8080/api/eventos
Content-Type: application/json

{
  "nome": "QJRPG Agosto",
  "local": "HUB Goias",
  "linkMapa": "https://maps.app.goo.gl/6ppn1PVJp9n5i8fR8",
  "status": "PLANEJADO"
}
```

## O que fazer depois

Este modulo (Evento) e o modelo a repetir para os proximos: Mesa, Candidatura, Produto,
Workshop, Tag etc. — mesma estrutura de pastas (entidade, repository, service + impl,
controller, dto, teste). Quando este estiver rodando e os testes passando, me avise que
seguimos para o proximo modulo ou para o esqueleto do app Flutter.
