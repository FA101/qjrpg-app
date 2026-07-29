# Rodada 6 - Formularios de criacao (POST)

## Como integrar
Copie `frontend/lib` inteiro por cima de `app/lib`, aceitando sobrescrever tudo.
(Todos os arquivos aqui ja existiam - esta rodada so adiciona formularios aos que ja tinham
apenas leitura, mais 1 arquivo novo: `core/utils/erro_utils.dart`.)

## O que mudou por tela
- **Eventos**: botao (+) cria evento, incluindo a janela de horario (necessaria pra Mesa funcionar).
- **Tags**: botao (+) cria tag.
- **Links uteis**: botao (+) cria link.
- **Mesas** (dentro do evento): botao "Ofertar mesa" - usa seu usuario logado como Game Master.
  Se violar regra de negocio (sobreposicao de horario, fora da janela), aparece a mensagem
  de erro exata que o back-end devolveu.
- **Catalogo** (dentro do evento): botao "Divulgar produto".
- **Workshops** (dentro do evento): botao "Propor workshop".
- **Candidaturas** (dentro da mesa): botao "Candidatar-se" (unico por usuario/mesa).
- **Mensagens** (dentro da mesa): campo de texto + enviar, no rodape.

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
Abra `http://localhost:8090` manualmente. Faca login antes de testar qualquer formulario
(sem login, `AuthSession.usuarioId` fica nulo e os formularios de Mesa/Produto/Workshop/
Candidatura/Mensagem vao dar erro).

**Terminal 3 - livre**

## Teste sugerido (fluxo completo)
1. Login.
2. Criar um evento com janela 09:00-22:00.
3. Entrar no evento, aba Mesas, ofertar uma mesa 09:00-13:00.
4. Tentar ofertar outra mesa 12:00-15:00 (mesmo usuario) - deve dar erro de sobreposicao.
5. Tocar na mesa criada, aba Mensagens, enviar uma mensagem.
6. Aba Candidaturas, clicar Candidatar-se (com o mesmo usuario logado - na vida real seria
   outro usuario, mas serve para validar o fluxo).
