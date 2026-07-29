# Rodada 7 - Apelido/moderacao, campos reais de Mesa, data do Evento, nomes de exibicao

Nenhuma dependencia nova no pom.xml nem no pubspec.yaml nesta rodada.

## Backend

### 1. Copiar arquivos NOVOS
Mescle `backend/src` para dentro de `api` (mesmo processo de sempre). Isso adiciona:
- `com/qjrpg/api/moderacao/` inteiro (filtro de palavras, Denuncia)
- `com/qjrpg/api/usuario/dto/`, `UsuarioController.java`
- `src/main/resources/moderacao/palavras-proibidas.txt`

### 2. Popular a lista de palavras proibidas
Abra `api/src/main/resources/moderacao/palavras-proibidas.txt` e cole sua lista (voce
mencionou ja ter 5 idiomas prontos, incluindo italiano - perfeito, um termo por linha,
comentarios com #). Nao precisa mudar nenhum codigo, o filtro le o arquivo automaticamente.

### 3. Arquivos que devem SOBRESCREVER os existentes
- `com/qjrpg/api/usuario/Usuario.java`, `UsuarioRepository.java`
- `com/qjrpg/api/auth/AuthService.java`, `AuthServiceImpl.java`, `AuthController.java`,
  `dto/ConfirmarCodigoRequest.java`, `dto/AuthResponse.java`
- `com/qjrpg/api/evento/Evento.java`, `EventoServiceImpl.java`,
  `dto/EventoRequest.java`, `dto/EventoResponse.java`
- `com/qjrpg/api/mesa/Mesa.java`, `MesaRepository.java`, `MesaServiceImpl.java`,
  `MesaController.java`, `dto/MesaRequest.java`, `dto/MesaResponse.java`
- `com/qjrpg/api/candidatura/CandidaturaRepository.java`, `CandidaturaController.java`,
  `dto/CandidaturaResponse.java`
- `com/qjrpg/api/mensagem/MensagemController.java`, `dto/MensagemResponse.java`

### 4. Nota sobre o banco (migracao)
Os campos novos (data do evento, numero/sistema/sinopse da mesa, apelido do usuario etc.)
foram feitos **opcionais no banco** (nullable), justamente para nao quebrar linhas ja
existentes no seu Postgres. Mas como e so dado de teste, o mais simples e limpo:
apague as tabelas antigas e deixe o Hibernate recriar. No pgAdmin4, dentro do banco
`qjrpg` > Schemas > public > Tables, apague `eventos`, `mesas`, `candidaturas`,
`mensagens` (nessa ordem, por causa de dependencias) - ou mais facil: apague e recrie o
banco `qjrpg` inteiro. Suba a API de novo e o Hibernate recria tudo do zero.

### 5. Rodar os testes
`mvnw.cmd test`.

## Frontend
Copie `frontend/lib` inteiro por cima de `app/lib`, aceitando sobrescrever.

## O que mudou
- **Cadastro**: agora pede um apelido publico e unico no primeiro acesso (nome real fica
  privado por padrao - o usuario escolhe se quer mostrar, em "Meu perfil").
- **Meu perfil** (novo item no menu): trocar apelido, ligar/desligar exibicao do nome real.
- **Denunciar** (icone de bandeira): disponivel em candidaturas e mensagens. Denuncia
  procedente (via `PATCH /api/denuncias/{id}/status`, so Admin/Moderador) forca o usuario
  a escolher um apelido novo.
- **Evento**: agora tem campo de data.
- **Mesa**: numero (sugerido automaticamente), sistema do jogo, titulo da aventura,
  sinopse, palavras-chave, observacoes, classificacao etaria, vagas totais/reservadas
  (vagas disponiveis sao calculadas automaticamente, descontando candidaturas aceitas).
- **Candidaturas e mensagens**: mostram o apelido/nome de exibicao, nao mais o UUID.
- **Enter no teclado** agora confirma: login, criar evento, ofertar mesa, enviar mensagem,
  criar tag, criar link.

## Terminais necessarios para testar

**Terminal 1 - API**
```powershell
cd C:\Users\fbuth\Desktop\qjrpg_app\api
.\mvnw.cmd spring-boot:run
```

**Terminal 2 - App Flutter**
```powershell
cd C:\Users\fbuth\Desktop\qjrpg_app\app
flutter analyze
flutter run -d web-server --web-port=8090
```

**Terminal 3 - livre**

## O que ficou de fora (proposital)
- Tela de administracao para revisar denuncias pendentes (por enquanto, teste via
  `Invoke-RestMethod -Uri http://localhost:8080/api/denuncias -Headers @{Authorization="Bearer SEU_TOKEN"}`).
- Enter-to-submit em Produto/Workshop (nao mencionados no seu feedback, deixei para nao
  aumentar o escopo sem necessidade - avise se quiser).
