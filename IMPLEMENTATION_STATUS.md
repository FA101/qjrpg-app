# IMPLEMENTATION_STATUS — QJRPG

Última atualização: 2026-10-13 · Fonte de verdade de requisitos: `PRD_App_Eventos_RPG.md` (v0.2)

## Stack (confirmada no código)
- **API:** Spring Boot 4.1.0 · Java 21 · Spring Data JPA · PostgreSQL · Spring Security + JWT (jjwt 0.12.6) · TOTP (`dev.samstevens.totp` 1.7.1) · testes JUnit 5 + Mockito.
- **App:** Flutter (Dart ^3.12) · Riverpod · Dio · shared_preferences · alvo Android nativo + PWA (web).

## Como rodar e verificar
```powershell
# API (variáveis opcionais; sem elas valem os defaults de desenvolvimento)
cd api
$env:QJRPG_DB_PASSWORD="sua_senha"; $env:QJRPG_ADMIN_EMAILS="seu@email.com"
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run

# App
cd app
flutter pub get
flutter analyze
flutter test
flutter run -d web-server --web-port=8090
```
Variáveis de ambiente da API: `QJRPG_DB_URL`, `QJRPG_DB_USER`, `QJRPG_DB_PASSWORD`, `QJRPG_JWT_SECRET` (obrigatória em produção, ≥ 32 caracteres), `QJRPG_JWT_EXPIRACAO_DIAS`, `QJRPG_ADMIN_EMAILS`, `QJRPG_EXPOR_CODIGO` (`true` só em dev; em produção `false`), `QJRPG_SHOW_SQL`, `PORT`.

## Rastreabilidade de requisitos
Legenda: ✅ implementado · 🟡 parcial · ⬜ pendente

| Req. | Situação | Observação |
|---|---|---|
| RF01, RF29 | 🟡 | Login sem senha por código de e-mail; **envio real (SMTP) pendente** — em dev o código volta na resposta (`QJRPG_EXPOR_CODIGO`). |
| RF02 | ✅ | Papéis ADMIN/MODERADOR/COMUM; ações restritas → 403 (`@PreAuthorize`, `AcessoNegadoException`). |
| RF03 | 🟡 | Apelido e preferência de nome; foto e celular no perfil pendentes. |
| RF04, RF05 | 🟡 | CRUD de eventos (Admin). Calendário anual ordenado e exceções pendentes de revisão. |
| RF06, RF49 | 🟡 | Janela de horário por evento; RF06 (quantidade de mesas por dia) pendente. |
| RF07, RF48 | ✅ | Oferta valida janela, sobreposição e início < fim; GM = usuário autenticado. |
| RF08 | ✅ | Aprovar/recusar/ajuste (Admin/Moderador) com notificação ao GM. |
| RF09, RF15, RF19, RF21, RF26 | ⬜ | Atribuição automática/manual de tags a usuários ainda não existe (Tag não tem vínculo com Usuario). |
| RF10, RF44 | ✅ | Só em mesa aprovada, com vaga, sem duplicidade, GM não se candidata à própria mesa. |
| RF11 | ✅ | GM (ou Admin/Mod) aceita/recusa; candidato é notificado. |
| RF12, RF13 | ✅ | Mensagens persistentes; autor = usuário autenticado; resposta encadeada só na mesma mesa. |
| RF14, RF16 | ✅ | Catálogo por evento; dono = usuário autenticado; edição/exclusão só dono ou Admin/Mod. |
| RF17, RF18 | ✅ | Workshops com aprovação e notificação. |
| RF20, RF37 | 🟡 | `NotificacaoService` registra em log e respeita opt-out; **push real (FCM) pendente** (requer projeto Firebase do autor). |
| RF22–RF25 | 🟡 | Promoção/demoção via API (`/api/usuarios/{id}/papel`, só Admin); falta tela e bloqueio de remover o último Admin. |
| RF27, RF28 | ✅ | Tags com cor exclusiva (validação no serviço). Regra automática customizada pendente. |
| RF30 | ✅ | TOTP com **desafio de sessão** (corrige bypass do 1º fator) e limite de 5 tentativas. |
| RF31, RF32 | ⬜ | Biometria — só faz sentido no build Android nativo (`local_auth`). |
| RF33 | ⬜ | Recuperação de conta (contato de confiança + códigos de backup + espera). |
| RF34, RF35, RF36b | 🟡 | Menor exige e-mail do responsável; confirmação/revogação pelo responsável. Falta bloqueio de acesso enquanto pendente (RF35) e evidência de consentimento (RF35b). |
| RF35b, RF36, RF36c | ⬜ | Termo + evidência (data/hora/dispositivo), troca de responsável com revisão, painel do responsável. |
| RF38, RF39 | ✅ | Suspensão ≤ 90 dias reversível; exclusão por anonimização (inclui segredo TOTP). |
| RF40, RF51 | ⬜ | Confirmação dupla configurável; perguntar sobre conteúdo associado na suspensão. |
| RF41, RF42 | 🟡 | Relatório de mesas com filtros (evento, GM, tipo, mês, ano, faixa etária, sexo). Faltam consolidado 12 meses, vagas, frequência. |
| RF43 | 🟡 | `.ics` (RFC 5545, com escape de texto) no **web/PWA**; no Android o botão fica oculto até haver compartilhamento de arquivo. |
| RF45, RF46, RF47 | ✅ | Conflito só no mesmo evento; notifica usuário + Admin/Mod ao surgir; aceitar bloqueia; bloqueada não é aceita; remoção (candidato, GM, Mod, Admin) desbloqueia o que não conflita com outra aceita. |
| RF50 | ⬜ | Eventos multi-dia (`DiaDeEvento`) — exige mudança de modelo. |
| RF52–RF56 | 🟡 | Regras e links públicos (GET sem login), edição só Admin. Auditoria de edição (RF54) a revisar. |
| RNF06 | 🟡 | Segredos fora do código (env). Token no app em `shared_preferences` (avaliar `flutter_secure_storage` no Android). |
| RNF11 | ⬜ | i18n (ARB pt-BR/en-US/es-ES/fr-FR) — strings ainda fixas. |
| RNF12 | ⬜ | Tema centralizado/alto contraste e revisão de semântica. |

## Decisões técnicas
- **Base de integração:** `qjrpg-PROJETO-COMPLETO.zip` (rodadas 8–11) mesclado arquivo a arquivo sobre o código local, preservando edições locais (ex.: `TagServiceImpl` null-safe, `SecurityConfig`/`JwtAuthFilter` locais). O zip nunca havia sido compilado.
- **Identidade vem do token, nunca do corpo:** `gameMasterId`, `usuarioId`, `autorId` nos requests são ignorados (mantidos só por compatibilidade).
- **2FA:** a 1ª etapa devolve `desafio2fa` (aleatório, 5 min); `/api/auth/2fa/validar` exige e-mail + desafio + código.
- **Colunas novas com `DEFAULT`** (`columnDefinition`) para o `ddl-auto: update` não falhar em tabelas existentes.
- **`palavras-proibidas.txt` local preservado** (o zip trazia uma lista de exemplo).

## Testes
- Backend: testes unitários de serviço (Mockito) para Evento, Mesa, Candidatura (12 cenários de RF10/44–47), Auth (2FA/desafio/tentativas), Produto, Workshop, Mensagem, Tag, Link, Conteúdo, Denúncia, JWT, TOTP. `ApiApplicationTests` exige PostgreSQL rodando.
- App: `ics_export_test.dart` (formato RFC 5545) e `widget_test.dart` (abre no login sem sessão).
- **Execução:** ver seção "Histórico de verificação".

## Histórico de verificação
- 2026-10-13: mescla feita num ambiente na nuvem **sem acesso ao Maven Central / pub.dev** (bloqueados pela rede) → **não foi possível compilar nem rodar testes lá**. Pendente rodar no PC: `mvnw.cmd test`, `flutter pub get`, `flutter analyze`, `flutter test`.

## Bloqueios e dependências externas
- SMTP para envio de código (sem ele, produção não tem login).
- Projeto Firebase (FCM) para push real.
- Banco: as novas colunas usam default; se o Hibernate reclamar, recriar o banco de desenvolvimento.

## Próxima tarefa recomendada
1. Rodar a verificação no PC e corrigir o que falhar.
2. Tags automáticas (RF09/15/19/21/26) + RF35 (bloqueio de conta de menor pendente).
3. i18n (RNF11) e tema/acessibilidade (RNF12).
4. Envio de e-mail (SMTP) e RF33.
