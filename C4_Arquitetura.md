# Arquitetura C4 — App de Gestão de Eventos

Baseado no PRD v0.2 e no Diagrama de Classes. Diagramas em Mermaid `flowchart` (mais
compatível entre GitHub/VS Code do que a sintaxe nativa `C4Context`, que nem todo visualizador
suporta ainda).

## ✅ Decisão tomada: verificação por e-mail

Celular é o identificador da conta; verificação (cadastro, login, 2FA e recuperação) é
feita por **e-mail** (SMTP transacional, gratuito) — evita o custo por mensagem de
provedores de SMS. Diagramas abaixo já refletem essa decisão.

## Nível 1 — Contexto

```mermaid
flowchart TB
    Usuario(("Usuário / Game Master / Vendas / Apoiador"))
    Admin(("Administrador"))
    Mod(("Moderador"))
    Responsavel(("Responsável legal"))

    subgraph Sistema["App de Gestão de Eventos (QJRPG)"]
        App["Sistema do evento:<br/>calendário, mesas, candidaturas,<br/>catálogo, workshops, mensagens"]
    end

    ServicoVerificacao["Serviço de verificação por e-mail<br/>(SMTP transacional, gratuito)"]
    ServicoPush["Serviço de notificação push<br/>(Firebase Cloud Messaging)"]
    CalendarioDispositivo["Calendário nativo do celular<br/>(.ics)"]
    GooglePlay["Google Play"]

    Usuario -->|usa| App
    Admin -->|administra| App
    Mod -->|modera| App
    Responsavel -->|autoriza vínculo| App
    App -->|verifica identidade| ServicoVerificacao
    App -->|envia notificações| ServicoPush
    App -->|exporta evento| CalendarioDispositivo
    App -.publicado via.-> GooglePlay
```

## Nível 2 — Container

```mermaid
flowchart TB
    subgraph Cliente["Dispositivos do usuário"]
        AppAndroid["App Android nativo<br/>(Flutter)"]
        AppPWA["PWA — iOS e Web<br/>(Flutter Web)"]
    end

    subgraph Backend["Back-end"]
        API["API REST<br/>(Spring Boot / Java)"]
        DB[("PostgreSQL")]
        Storage["Armazenamento de mídia<br/>(imagens de produtos/perfil)"]
    end

    Verificacao["Serviço de e-mail transacional<br/>(SMTP, gratuito)"]
    Push["Firebase Cloud Messaging<br/>(push, gratuito)"]

    AppAndroid -->|HTTPS/JSON| API
    AppPWA -->|HTTPS/JSON| API
    API -->|JDBC| DB
    API -->|upload/leitura| Storage
    API -->|solicita código| Verificacao
    API -->|dispara notificação| Push
    Push -.entrega.-> AppAndroid
    Push -.entrega.-> AppPWA
```

**Hospedagem gratuita candidata (validar disponibilidade atual antes de decidir):**
API Spring Boot → Render/Railway/Fly.io (free tier); PostgreSQL → Neon/Supabase/Railway
(free tier); App Web/PWA → Netlify/Vercel (free tier, estático); Storage → Supabase
Storage/Cloudinary (free tier).

## Nível 3 — Componentes (dentro da API)

```mermaid
flowchart TB
    subgraph API["API REST (Spring Boot)"]
        subgraph Seguranca["Segurança"]
            Filtro["Filtro JWT + Autorização por papel"]
        end

        subgraph Controllers["Controllers"]
            C1["AutenticacaoController"]
            C2["UsuarioController"]
            C3["EventoController"]
            C4["MesaController"]
            C5["CandidaturaController"]
            C6["TagController"]
            C7["ProdutoController"]
            C8["WorkshopController"]
            C9["MensagemController"]
            C10["RelatorioController"]
            C11["ConteudoInstitucionalController"]
        end

        subgraph Services["Services (regra de negócio)"]
            S1["AutenticacaoService"]
            S2["UsuarioService"]
            S3["EventoService"]
            S4["MesaService"]
            S5["CandidaturaService"]
            S6["TagService"]
            S7["ProdutoService"]
            S8["WorkshopService"]
            S9["MensagemService"]
            S10["RelatorioService"]
            S11["NotificacaoService"]
        end

        subgraph Repositories["Repositories (interfaces)"]
            R["Interfaces Spring Data JPA<br/>uma por entidade"]
        end

        subgraph Jobs["Tarefas agendadas"]
            J1["Verificador de conflito de horário"]
            J2["Expirador de suspensão (90 dias)"]
        end
    end

    Filtro --> Controllers
    Controllers --> Services
    Services --> R
    S5 --> S11
    J1 --> S11
    J2 --> S2
```

Cada `Service` depende apenas da **interface** do `Repository` correspondente (Dependency
Inversion) — isso é o que permite mockar o acesso a dados nos testes automatizados (RNF02),
sem precisar de um banco real em cada teste unitário.

## Nível 4 — Código (exemplo: aceitar candidatura e bloquear conflitos)

Ilustra RF45-RF47 (conflito de horário entre candidaturas do mesmo usuário).

```mermaid
sequenceDiagram
    participant GM as Game Master
    participant C as CandidaturaController
    participant S as CandidaturaService
    participant R as CandidaturaRepository
    participant N as NotificacaoService

    GM->>C: aceitar(candidaturaId)
    C->>S: aceitarCandidatura(candidaturaId)
    S->>R: buscarConflitantes(usuarioId, horario)
    R-->>S: lista de candidaturas conflitantes
    S->>R: marcarComoAceita(candidaturaId)
    S->>R: bloquearConflitantes(lista)
    S->>N: notificar(usuario, admins, moderadores)
    N-->>GM: confirmação
```

Esqueleto de código correspondente (Java/Spring, ilustrativo):

```java
public interface CandidaturaRepository {
    List<Candidatura> buscarConflitantes(UUID usuarioId, Horario horario);
    void marcarComoAceita(UUID candidaturaId);
    void bloquearConflitantes(List<Candidatura> conflitantes);
}

@Service
public class CandidaturaService {

    private final CandidaturaRepository repository;
    private final NotificacaoService notificacaoService;

    // injeção via construtor -> Dependency Inversion, facilita mock em teste
    public CandidaturaService(CandidaturaRepository repository,
                               NotificacaoService notificacaoService) {
        this.repository = repository;
        this.notificacaoService = notificacaoService;
    }

    public void aceitarCandidatura(UUID candidaturaId, UUID usuarioId, Horario horario) {
        List<Candidatura> conflitantes = repository.buscarConflitantes(usuarioId, horario);
        repository.marcarComoAceita(candidaturaId);
        if (!conflitantes.isEmpty()) {
            repository.bloquearConflitantes(conflitantes);
            notificacaoService.notificarConflito(usuarioId, conflitantes);
        }
    }
}
```

Esse desenho já é testável: um teste unitário injeta um `CandidaturaRepository` e um
`NotificacaoService` falsos (mock) e verifica que `bloquearConflitantes` e
`notificarConflito` são chamados corretamente — sem precisar de banco de dados real.

## Decisão em aberto antes do ambiente de desenvolvimento

1. Confirmar provedores de hospedagem gratuita no momento da implementação (as ofertas de
   free tier mudam com frequência).

## Próximo passo

Seguimos para a configuração do ambiente: JDK, Flutter SDK e estrutura inicial dos dois
projetos (API e app).
