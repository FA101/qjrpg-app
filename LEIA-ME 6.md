# Rodada 5 - Autenticacao (login sem senha, por codigo de e-mail) + JWT

## Backend

### 1. Adicionar dependencias no pom.xml
Dentro de `<dependencies>`, adicione:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

### 2. Adicionar no application.yml (no final do arquivo, mesma indentacao raiz que `spring:` e `server:`)
```yaml
app:
  jwt:
    secret: "troque-este-segredo-antes-de-qualquer-uso-real-0123456789"
    expiracao-dias: 30
  admin-emails: "SEU_EMAIL_AQUI@exemplo.com"
```
Troque `SEU_EMAIL_AQUI@exemplo.com` pelo seu proprio e-mail - e assim que voce vira Admin
automaticamente no primeiro login (bootstrap do primeiro administrador).

### 3. Copiar arquivos NOVOS
Copie `backend/src` deste zip para dentro de `api`, mesclando (igual das rodadas
anteriores). Isso adiciona os pacotes `usuario` e `auth` inteiros.

### 4. Arquivos que devem SOBRESCREVER os existentes
- `com/qjrpg/api/conteudo/ConteudoInstitucionalController.java` (ganhou `@PreAuthorize`)
- `com/qjrpg/api/link/LinkUtilController.java` (ganhou `@PreAuthorize`)

### 5. Rodar os testes
`mvnw.cmd test` - preste atencao especial em `JwtServiceImplTest`: se ele falhar, o
problema e a versao/API da biblioteca jjwt (avisei que essa era a parte de maior risco).
Me manda o erro que eu ajusto.

## Frontend

### Arquivos NOVOS
- `lib/core/auth/auth_session.dart`
- `lib/features/auth/` (pasta inteira: domain, data, presentation)

### Arquivos que devem SOBRESCREVER os existentes
- `lib/core/network/dio_client.dart` (agora envia o token JWT em toda requisicao)
- `lib/main.dart` (decide entre LoginPage e o app)
- `lib/core/widgets/app_drawer.dart` (ganhou botao Sair)

Copie `frontend/lib` inteiro por cima de `app/lib`, aceitando sobrescrever.

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
Abra `http://localhost:8090` manualmente no navegador.

**Terminal 3 - livre (git, testes avulsos)**

## Como testar o login

Na tela que abrir, digite o e-mail que voce configurou em `app.admin-emails`. Clique
"Enviar codigo" - o codigo aparece na propria tela (laranja, "modo desenvolvimento").
Copie o codigo, preencha nome e celular (primeiro acesso), clique "Confirmar e entrar".
Voce deve cair na tela de Eventos, agora autenticado como Admin.

## O que ficou de fora (proposital)

- Envio real de e-mail (SMTP) - o codigo aparece na tela por enquanto.
- 2FA, biometria, recuperacao de conta, menores de idade - vem depois, conforme o PRD.
- Restricao de papel (Admin/Moderador) nos outros 9 modulos (Evento, Mesa, etc.) - por
  enquanto so exigem "estar logado", nao um papel especifico. Mapear isso modulo a modulo
  fica para uma proxima rodada de "hardening".
- Persistir a sessao entre recarregamentos de pagina (hoje, F5 desloga).
