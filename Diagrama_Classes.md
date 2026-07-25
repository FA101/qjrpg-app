# Diagrama de Classes — App de Gestão de Eventos

Baseado nos requisitos do PRD v0.2. Papel (Admin/Moderador/Comum) é um **atributo** do
Usuário, não uma subclasse — evita hierarquia rígida e facilita promoção/demoção em tempo
de execução (RF22-25), mantendo aderência a SOLID (Open/Closed: novos papéis não exigem
nova classe).

```mermaid
classDiagram
    class Usuario {
        +UUID id
        +String nome
        +String telefone
        +Date dataNascimento
        +PapelUsuario papel
        +StatusConta status
        +String idiomaPreferido
        +boolean biometriaAtiva
        +boolean duasEtapasAtivo
        +boolean notificacoesPushAtivas
        +ehMenorDeIdade() boolean
    }

    class VinculoResponsavel {
        +UUID id
        +UUID menorId
        +UUID responsavelId
        +StatusVinculo status
        +DateTime dataConsentimento
        +String evidenciaConsentimento
        +confirmar()
        +revogar()
    }

    class Tag {
        +UUID id
        +String nome
        +String corHex
        +TipoTag tipo
    }

    class RegraDeTag {
        +UUID id
        +UUID tagId
        +String criterioAutomatico
        +avaliar(Usuario) boolean
    }

    class Evento {
        +UUID id
        +String nome
        +String local
        +String linkMapa
        +StatusEvento status
    }

    class DiaDeEvento {
        +UUID id
        +UUID eventoId
        +Date data
        +Time horaInicioJanela
        +Time horaFimJanela
    }

    class Mesa {
        +UUID id
        +UUID diaDeEventoId
        +UUID gameMasterId
        +String tipoJogo
        +Time horaInicio
        +Time horaFim
        +int vagas
        +StatusMesa status
        +validarDentroDaJanela() boolean
        +validarSemSobreposicao() boolean
    }

    class Candidatura {
        +UUID id
        +UUID mesaId
        +UUID usuarioId
        +StatusCandidatura status
        +boolean bloqueada
        +aceitar()
        +recusar()
        +remover()
    }

    class Produto {
        +UUID id
        +UUID usuarioId
        +UUID eventoId
        +TipoProduto tipo
        +String titulo
        +String descricao
        +String imagemUrl
        +String linkExterno
    }

    class Workshop {
        +UUID id
        +UUID usuarioId
        +UUID eventoId
        +String tema
        +String descricao
        +Time horarioDesejado
        +StatusWorkshop status
    }

    class Mensagem {
        +UUID id
        +UUID mesaId
        +UUID autorId
        +UUID respostaDeId
        +String conteudo
        +DateTime dataHora
    }

    class ConteudoInstitucional {
        +UUID id
        +String secao
        +String titulo
        +String corpo
        +UUID autorId
        +DateTime dataAtualizacao
    }

    class LinkUtil {
        +UUID id
        +String titulo
        +String url
        +String categoria
    }

    Usuario "1" --> "0..*" Mesa : oferta (Game Master)
    Usuario "1" --> "0..*" Candidatura : realiza
    Mesa "1" --> "0..*" Candidatura : recebe
    Usuario "1" --> "0..*" Produto : divulga
    Usuario "1" --> "0..*" Workshop : propõe
    Usuario "1" --> "0..*" Mensagem : envia
    Mesa "1" --> "0..*" Mensagem : contém
    Usuario "1" --> "0..*" ConteudoInstitucional : edita (Admin)
    Usuario "1" --> "0..*" LinkUtil : cadastra (Admin)
    Usuario "*" --> "*" Tag : possui
    Tag "1" --> "0..*" RegraDeTag : possui
    Evento "1" --> "1..*" DiaDeEvento : possui
    DiaDeEvento "1" --> "0..*" Mesa : oferece
    Evento "1" --> "0..*" Workshop : recebe
    Evento "1" --> "0..*" Produto : recebe no catálogo
    Usuario "0..1" --> "0..1" Usuario : responsável por (via VinculoResponsavel)
    VinculoResponsavel "1" --> "1" Usuario : menor
    VinculoResponsavel "1" --> "1" Usuario : responsável
```

## Notas de design (ligação com SOLID/Clean Code)

- **Single Responsibility:** cada classe representa uma única entidade de domínio; regras
  de validação de horário ficam em métodos da própria `Mesa` (`validarDentroDaJanela`,
  `validarSemSobreposicao`), não espalhadas em controllers.
- **Open/Closed:** `Tag` e `RegraDeTag` são extensíveis (Admin cria tags novas) sem alterar
  código-fonte existente — a lógica de aplicação de regra fica isolada em `RegraDeTag`.
- **Dependency Inversion:** no back-end (Spring Boot), cada uma dessas classes terá uma
  interface de repositório associada (`MesaRepository`, `CandidaturaRepository` etc.), para
  que o `Service` dependa da abstração, não da implementação JPA/PostgreSQL — isso é o que
  torna o CRUD testável (RNF02) com mocks.
- `VinculoResponsavel` é uma classe associativa própria (não um atributo simples em
  `Usuario`), porque carrega estado e regras próprias (RF34-36).

## Próximos passos

1. Revisar se este modelo de classes reflete corretamente o domínio.
2. Avançar para o modelo C4 (Contexto → Container → Componente → Código).
3. Só então configurar o ambiente de desenvolvimento.
