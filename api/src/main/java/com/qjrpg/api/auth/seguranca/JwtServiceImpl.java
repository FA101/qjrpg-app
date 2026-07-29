package com.qjrpg.api.auth.seguranca;

import com.qjrpg.api.usuario.PapelUsuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtServiceImpl implements JwtService {

    private final SecretKey chave;
    private final long diasExpiracao;

    public JwtServiceImpl(
            @Value("${app.jwt.secret}") String segredo,
            @Value("${app.jwt.expiracao-dias:30}") long diasExpiracao) {
        this.chave = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
        this.diasExpiracao = diasExpiracao;
    }

    @Override
    public String gerarToken(UUID usuarioId, PapelUsuario papel) {
        Instant agora = Instant.now();
        return Jwts.builder()
                .subject(usuarioId.toString())
                .claim("papel", papel.name())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(diasExpiracao, ChronoUnit.DAYS)))
                .signWith(chave)
                .compact();
    }

    @Override
    public UUID extrairUsuarioId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return UUID.fromString(claims.getSubject());
    }
}
