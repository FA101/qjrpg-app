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

## Terminais necessarios para testar

Voce precisa de **3 janelas/abas do PowerShell** abertas ao mesmo tempo:

**Terminal 1 - API (deixar rodando)**
```powershell
cd C:\Users\fbuth\Desktop\qjrpg_app\api
.\mvnw.cmd spring-boot:run
```
Espere aparecer `Started ApiApplication`. Nao feche esta janela.

**Terminal 2 - App Flutter (deixar rodando)**
```powershell
cd C:\Users\fbuth\Desktop\qjrpg_app\app
flutter analyze
flutter run -d web-server --web-port=8090
```
Espere aparecer `is being served at http://localhost:8090`. Nao feche esta janela.
Depois abra `http://localhost:8090` manualmente no navegador.

**Terminal 3 - Comandos avulsos (criar dados de teste, git, etc.)**
```powershell
cd C:\Users\fbuth\Desktop\qjrpg_app
```
Use esta janela livre para os `Invoke-RestMethod` de teste, `git add/commit/push`, ou
qualquer outro comando pontual - sem interromper a API nem o app, que continuam rodando
nas outras duas.

## Testar

Se ainda nao tiver evento/mesa cadastrados, crie pelo Terminal 3:
```powershell
Invoke-RestMethod -Uri http://localhost:8080/api/eventos -Method POST -ContentType 'application/json' -Body '{"nome":"QJRPG Agosto","local":"HUB Goias","linkMapa":null,"status":"PLANEJADO","horaInicioJanela":"09:00:00","horaFimJanela":"22:00:00"}'
```
Copie o `id` que voltar na resposta e use no proximo comando (troque COLE_O_ID_AQUI):
```powershell
Invoke-RestMethod -Uri http://localhost:8080/api/mesas -Method POST -ContentType 'application/json' -Body '{"eventoId":"COLE_O_ID_AQUI","gameMasterId":"11111111-1111-1111-1111-111111111111","tipoJogo":"RPG","horaInicio":"09:00:00","horaFim":"13:00:00","vagas":4}'
```
Depois va no navegador (`localhost:8090`), toque no evento, depois na mesa, e confira as
abas Mesas/Catalogo/Workshops e Candidaturas/Mensagens.

## O que ainda falta (proximas rodadas)
Formularios de criacao (POST) nas telas, e a fase de Autenticacao/JWT.
