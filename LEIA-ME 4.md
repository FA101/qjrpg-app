# Rodada 4 - Navegacao entre telas

## Arquivos NOVOS (adicionar)
- `lib/core/widgets/app_drawer.dart`
- `lib/features/evento/presentation/evento_detail_page.dart`
- `lib/features/mesa/presentation/mesa_detail_page.dart`

## Arquivos que devem SOBRESCREVER os existentes (mesmo caminho, conteudo mudou)
- `lib/features/evento/presentation/evento_list_page.dart` (ganhou Drawer + navegacao ao tocar num evento)
- `lib/features/mesa/presentation/mesa_list_page.dart` (agora e "MesaListBody", sem Scaffold proprio, usado dentro de aba)
- `lib/features/produto/presentation/produto_list_page.dart` (idem, "ProdutoListBody")
- `lib/features/workshop/presentation/workshop_list_page.dart` (idem, "WorkshopListBody")
- `lib/features/candidatura/presentation/candidatura_list_page.dart` (idem, "CandidaturaListBody")
- `lib/features/mensagem/presentation/mensagem_list_page.dart` (idem, "MensagemListBody")
- `lib/features/tag/presentation/tag_list_page.dart` (ganhou Drawer)
- `lib/features/link/presentation/link_util_list_page.dart` (ganhou Drawer)
- `lib/features/conteudo/presentation/regras_evento_page.dart` (ganhou Drawer)

Copie a pasta `frontend/lib` inteira por cima da sua `app/lib`, aceitando sobrescrever
tudo que pedir - e mais simples e seguro que copiar arquivo por arquivo.

## Como a navegacao ficou

```
EventoListPage (com menu lateral: Eventos | Tags | Links uteis | Regras)
  -> toca num evento -> EventoDetailPage (abas: Mesas | Catalogo | Workshops)
       -> toca numa mesa -> MesaDetailPage (abas: Candidaturas | Mensagens)
```

## Testar

`flutter run -d web-server --web-port=8090` (mesmo comando de sempre). Crie um evento e
uma mesa vinculada a ele via API (POST /api/eventos, depois POST /api/mesas com o
eventoId retornado) se ainda nao tiver nenhum, para ver a navegacao completa funcionando.

## O que ainda falta (proximas rodadas)
Formularios de criacao (POST) nas telas, e a fase de Autenticacao/JWT.
