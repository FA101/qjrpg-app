# Testes de integração com apoio de GenAI — QJRPG

Atividade da Unidade III (testes de integração apoiados por LLM).
LLM utilizada: Claude (Anthropic), dentro do ambiente de desenvolvimento do projeto.

## 1. Cenário de integração escolhido

Fluxo **Mesa → Candidatura** da API REST (Spring Boot), consumido pelo app Flutter:

```
App Flutter ──HTTP/JSON──> SecurityFilterChain (JWT) ──> MesaController / CandidaturaController
                                                             │
                                  MesaService · CandidaturaService · TagService · NotificacaoService
                                                             │
                                                     JPA ──> Banco (H2 nos testes, PostgreSQL em produção)
```

Regras do PRD envolvidas: RF07–RF11 (oferta, aprovação, candidatura e aceite), RF44–RF47
(conflito de horário, bloqueio e desbloqueio), RF09 (tag Game Master), RF02/RF25 (permissões),
RF35 (menor de idade sem autorização) e o contrato do login sem senha (RF29).

Por que é integração e não unidade: cada caso atravessa filtro JWT, autorização por papel,
validação de entrada, controller, serviço, repositório e banco real em memória. Nada é mockado.

## 2. Tipos de prompt utilizados

### Tipo 1 — Prompt de geração de cenários a partir de regras de negócio

**Versão 1 (inicial, sem contexto):**

> Gere casos de teste de integração para uma API de candidaturas a mesas de RPG.

**Resultado da v1 (resumo do que a LLM devolveu):**
- 8 cenários genéricos de CRUD ("criar candidatura", "listar candidaturas", "excluir candidatura").
- Endpoints inventados (`POST /api/mesas/{id}/candidaturas`, `PUT /api/candidaturas/{id}`).
- Supôs `409 Conflict` para conflito de horário e `usuarioId` no corpo da requisição.
- Nenhum cenário para: horários encostados, eventos diferentes no mesmo horário,
  desbloqueio após remoção, mesa lotada, mesa ainda não aprovada.

**Versão 2 (ajustada):**

> Você é um engenheiro de QA especializado em testes de integração de APIs REST.
> Contexto: API Spring Boot com autenticação JWT. Endpoints reais:
> `POST /api/mesas`, `PATCH /api/mesas/{id}/status`, `POST /api/candidaturas {mesaId}`,
> `GET /api/candidaturas?mesaId=`, `PATCH /api/candidaturas/{id}/aceitar`, `DELETE /api/candidaturas/{id}`.
> O candidato é sempre o usuário do token. Erros de regra devolvem 422 com `{"erro": "..."}`.
> Regras (PRD): [colei RF07–RF11, RF44–RF47 e a seção 13 do PRD].
> Tarefa: liste cenários de integração de ponta a ponta. Para cada um informe:
> ID, pré-condições (usuários, papéis, evento, mesas e horários), sequência de chamadas HTTP,
> resultado esperado (status e campos do JSON) e a regra do PRD coberta.
> Inclua obrigatoriamente casos de fronteira de horário (encostados, sobrepostos, eventos diferentes).
> Não invente endpoints fora da lista. Responda em tabela.

**Resultado da v2:** cenários CT-01 a CT-06 da seção 3, já com pré-condições, sequência de
chamadas e status esperado. Um cenário sugerido (verificar a notificação de conflito na resposta HTTP)
foi deixado de fora porque hoje a notificação só é registrada em log; ele está coberto no teste unitário.

### Tipo 2 — Prompt de contrato da API e casos de erro (negativos e de segurança)

**Versão 1 (inicial):**

> Quais testes de erro devo fazer na minha API de eventos?

**Resultado da v1 (resumo):**
- Lista genérica (SQL injection, payload grande, timeout) sem relação com as regras do sistema.
- Não diferenciou 400 (validação), 403 (permissão), 404 (inexistente) e 422 (regra de negócio).
- Não considerou os papéis ADMIN, MODERADOR e COMUM nem o acesso restrito de menores.

**Versão 2 (ajustada, com contrato explícito e técnica de partição):**

> Atue como testador de contrato entre um app Flutter e uma API REST.
> Contrato de respostas da API:
> - 400 → `{campo: mensagem}` (Bean Validation)
> - 403 → sem permissão (papel insuficiente ou dono diferente), corpo `{"erro": ...}` quando a regra é do domínio
> - 404 → `{"erro": ...}`
> - 422 → `{"erro": ...}` (regra de negócio)
> Papéis: ADMIN, MODERADOR, COMUM; papel restrito para menor sem autorização do responsável.
> Rotas públicas: `GET /api/conteudos/**`, `GET /api/links-uteis`, `/api/auth/**`.
> Fluxo de login: `POST /api/auth/solicitar-codigo {email}` → `POST /api/auth/confirmar-codigo {email, codigo, apelido}` → token JWT.
> Tarefa: aplique partição de equivalência por status HTTP e por papel e gere casos negativos
> e de segurança, incluindo tentativa de agir em nome de outro usuário (campo `usuarioId` forjado).
> Para cada caso: ID, requisição, papel do chamador, status esperado e campo do corpo a verificar.
> Inclua 1 caso positivo do contrato de login ponta a ponta.

**Resultado da v2:** casos CE-01 a CE-14 da seção 3. Sugestões de infraestrutura (limite de requisições
e CORS do domínio de produção) ficaram fora, porque ainda não fazem parte do escopo do projeto.

## 3. Casos registrados e onde estão no código

| ID | Tipo de prompt | Caso | Regra | Classe |
|---|---|---|---|---|
| CT-01 | 1 | Fluxo feliz: ofertar, aprovar, candidatar, aceitar; vagas caem e GM ganha tag | RF07–RF11, RF09 | `CandidaturaFluxoIntegracaoTest` |
| CT-02 | 1 | Aceitar bloqueia a conflitante; bloqueada não pode ser aceita | RF45, RF46 | idem |
| CT-03 | 1 | Horários encostados (13h/13h) não conflitam | Seção 13 | idem |
| CT-04 | 1 | Mesmo horário em eventos diferentes não conflita | Seção 13 | idem |
| CT-05 | 1 | Remover a aceita desbloqueia a conflitante | RF47 | idem |
| CT-06 | 1 | Mesa lotada recusa nova candidatura | RF10 | idem |
| CE-01 | 2 | Rota protegida sem token | RF02 | `ContratoEErrosApiIntegracaoTest` |
| CE-02 | 2 | Token adulterado | RNF06 | idem |
| CE-03 | 2 | Regras e links públicos sem login | RF52, RF56 | idem |
| CE-04 | 2 | 400 com nomes dos campos inválidos | Contrato | idem |
| CE-05 | 2 | 422 mesa fora da janela | RF49 | idem |
| CE-06 | 2 | 422 oferta sobreposta do mesmo GM | RF48 | idem |
| CE-07 | 2 | 404 com `{erro}` | Contrato | idem |
| CE-08 | 2 | Comum não aprova mesa nem cria evento (403) | RF04, RF08 | idem |
| CE-09 | 2 | Quem não é GM não aceita candidatura (403) | RF11 | idem |
| CE-10 | 2 | `usuarioId` forjado é ignorado | Segurança | idem |
| CE-11 | 2 | Candidatura em mesa não aprovada (422) | RF10 | idem |
| CE-12 | 2 | Menor pendente só acessa o próprio perfil | RF35 | idem |
| CE-13 | 2 | Contrato do login sem senha de ponta a ponta | RF29 | idem |
| CE-14 | 2 | Código errado não emite token | RF29 | idem |

Código: `api/src/test/java/com/qjrpg/api/integracao/`

## 4. Ajustes de prompt que fizeram diferença

1. **Contexto real em vez de domínio genérico**: listar os endpoints existentes eliminou endpoints inventados.
2. **Colar as regras do PRD**: os cenários passaram a citar RF e a cobrir fronteiras (horário encostado, evento diferente).
3. **Formato de saída fixo** (ID, pré-condição, chamadas, esperado, regra): facilitou a conversão direta em código.
4. **Restrição explícita** ("não invente endpoints", "responda em tabela"): reduziu ruído.
5. **Técnica nomeada** (partição de equivalência por status e por papel): aumentou a diversidade dos casos negativos.
6. **Papel do modelo** (QA de integração, testador de contrato): mudou o foco de "CRUD funciona" para "o acordo entre os componentes é respeitado".

## 5. Revisão humana

Todo caso gerado foi conferido contra o PRD e contra o código antes de virar teste. Pontos conferidos à mão:
- Códigos de status: o projeto usa `422` para regra de negócio (não `409`), e `403` para falta de permissão.
- No CE-08 o corpo enviado precisa ser válido; senão a validação (400) responde antes da checagem de permissão (403).
- A notificação de conflito só é registrada em log; por isso ela é verificada no teste unitário
  (`CandidaturaServiceImplTest`), e não nos testes de integração.

## 6. Como executar

```powershell
cd api
.\mvnw.cmd test -Dtest="*IntegracaoTest"
```

Os testes usam H2 em memória (modo PostgreSQL), então não dependem do banco local.
