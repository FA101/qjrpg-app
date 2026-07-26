# Rodada 2 - Modulos de back-end + esqueleto Flutter

## Back-end (pasta backend/)

Contem arquivos NOVOS e ATUALIZADOS. Copie o conteudo de backend/src para dentro do seu
projeto `api` (mesmo processo da rodada 1: mesclar mantendo os caminhos de pacote).

**Atencao a 3 arquivos que foram ALTERADOS (nao so adicionados) e devem SOBRESCREVER os
que ja existem no seu projeto:**
- `com/qjrpg/api/evento/Evento.java` (ganhou horaInicioJanela/horaFimJanela)
- `com/qjrpg/api/evento/EventoServiceImpl.java`, `EventoController.java`, `dto/EventoRequest.java`, `dto/EventoResponse.java`
- `com/qjrpg/api/shared/exception/GlobalExceptionHandler.java` (agora generico para todos os modulos)

**Um arquivo deve ser APAGADO** do seu projeto (foi substituido pela excecao generica):
`com/qjrpg/api/shared/exception/EventoNaoEncontradoException.java`

Depois de copiar tudo, rode `mvnw.cmd test` de dentro da pasta `api`. Devem passar
bem mais testes agora (Evento + Tag + Produto + Workshop + Mensagem + Link + Conteudo +
Mesa + Candidatura).

### Modulos novos, endpoints principais
- Tag: `/api/tags` (cor exclusiva validada)
- Produto: `/api/produtos?eventoId=`
- Workshop: `/api/workshops?eventoId=`, `PATCH /{id}/status`
- Mensagem: `/api/mensagens?mesaId=`
- Link Util: `/api/links-uteis`
- Conteudo Institucional: `/api/conteudos`, `GET /api/conteudos/{secao}`
- Mesa: `/api/mesas?eventoId=`, `POST` valida sobreposicao de horario e janela do evento,
  `PATCH /{id}/status`
- Candidatura: `/api/candidaturas?mesaId=`, `PATCH /{id}/aceitar` (bloqueia conflitantes
  automaticamente), `PATCH /{id}/recusar`, `DELETE /{id}` (desbloqueia conflitantes)

### O que ficou de fora de proposito
Autenticacao/JWT, papeis (Admin/Moderador) e o modulo Usuario - por enquanto os campos
`usuarioId`/`gameMasterId`/`autorId` sao UUIDs "soltos" (sem validar se o usuario existe
de verdade). Isso e resolvido quando fizermos a fase de autenticacao.

## Front-end (pasta frontend/)

1. Rode `flutter create app` numa pasta separada (ou dentro do mesmo repo, numa pasta
   `app/`) para gerar o projeto base.
2. Copie o conteudo de `frontend/lib` para dentro do `lib` gerado (substitua o
   `main.dart` padrao).
3. Abra o `pubspec.yaml` gerado e adicione as dependencias listadas em
   `frontend/pubspec_dependencias.yaml`.
4. Rode `flutter pub get`.
5. Confirme o endereco da API em `lib/core/network/dio_client.dart`:
   - Rodando no emulador Android: `http://10.0.2.2:8080/api` (ja esta assim)
   - Rodando no Chrome (`flutter run -d chrome`): troque para `http://localhost:8080/api`
6. Com a API rodando (`mvnw.cmd spring-boot:run`), rode `flutter run -d chrome` e veja a
   tela listar os eventos que voce criou via API.

## Padrao para os proximos modulos do Flutter (Mesa, Candidatura etc.)

Repita a estrutura de `features/evento/`: `domain/` (entidade + interface de repositorio),
`data/` (dto + implementacao Dio), `presentation/` (providers Riverpod + tela).
