# Rodada 3 - Modulos Flutter (Tag, Produto, Workshop, Mensagem, Link, Regras do Evento, Mesa, Candidatura)

## Como integrar

1. Copie o conteudo de `frontend/lib/features` para dentro de `app\lib\features`
   (mantendo as 8 pastas: tag, produto, workshop, mensagem, link, conteudo, mesa, candidatura).
2. Nao precisa mexer em `core/` nem `main.dart` - so estao usando o DioClient que ja existe.
3. Rode `flutter pub get` (nao precisa, pois nenhuma dependencia nova foi adicionada - so
   reaproveita dio e flutter_riverpod que ja estao no pubspec).
4. `flutter run -d web-server --web-port=8090` de novo (o mesmo comando que ja funcionou).

## Padrao seguido (identico ao modulo Evento)

Cada modulo tem: `domain/` (entidade + interface de repositorio), `data/` (dto +
implementacao Dio), `presentation/` (providers Riverpod + tela).

## Como navegar ate as novas telas

Este pacote so criou as *paginas*, mas nao adicionou botoes de navegacao no `main.dart`
ainda (isso depende de como voce quer organizar o menu do app - vale conversarmos sobre
isso separadamente, ja que envolve decisao de design de navegacao, nao so codigo).
Para testar cada uma agora, a forma mais rapida e trocar temporariamente a linha `home:`
em `main.dart`, por exemplo:

```dart
home: const TagListPage(), // ao inves de EventoListPage()
```

(nao esqueca do import correspondente no topo do arquivo)

## Modulos que dependem de parametro (eventoId ou mesaId)

Produto, Workshop e Mesa precisam de um `eventoId` (do evento que voce ja criou via API).
Mensagem e Candidatura precisam de um `mesaId` (de uma mesa que voce crie via API primeiro,
com `POST /api/mesas`). Passe como parametro ao construir a tela, ex:
`ProdutoListPage(eventoId: 'cole-o-id-aqui')`.

## O que ainda falta (proposital, fora desta rodada)

- Formularios de criacao (POST) nas telas - por enquanto so leitura (GET), para
  manter esta entrega enxuta. O repository ja tem os metodos `criar`/`ofertar`/`propor`
  prontos, so falta a UI de formulario chamando eles.
- Menu de navegacao entre as telas.
- Autenticacao (JWT) - fica para a fase propria, como combinado.
