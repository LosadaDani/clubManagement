package com.managementClub.managementClub.security;

import com.managementClub.managementClub.model.entity.AppUser;
import com.managementClub.managementClub.model.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// Sin Spring, sin mocks y sin reloj: JwtService real construido con una clave de test y una expiración explícita.
class JwtServiceTest {

    // Claves de test: Base64 válido de 32 bytes (mínimo que exige HS256). La segunda es distinta, para la firma ajena
    private static final String SECRET = "Y2xhdmUtZGUtdGVzdC11bm8tMDEyMzQ1Njc4OS1hYmM=";
    private static final String OTHER_SECRET = "Y2xhdmUtZGUtdGVzdC1kb3MtOTg3NjU0MzIxMC14eXo=";
    private static final long ONE_HOUR = 3_600_000L;
    private static final long EXPIRED_ONE_SECOND_AGO = -1_000L;
    private static final String USERNAME = "ana";

    private final JwtService service = new JwtService(SECRET, ONE_HOUR);

    private AppUser appUserWithRole(Role role) {
        return new AppUser(USERNAME, "HASHED", role, null);
    }

    // Lee las claims verificando la firma con la clave de test, para comprobar el contenido del token
    private Claims claimsOf(String token) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    // Las dos operaciones que validan el token se prueban con los mismos casos
    static Stream<Arguments> tokenOperations() {
        BiFunction<JwtService, String, Object> extractUsername = JwtService::extractUsername;
        BiFunction<JwtService, String, Object> extractRole = JwtService::extractRole;
        return Stream.of(
                Arguments.of(Named.of("extractUsername", extractUsername)),
                Arguments.of(Named.of("extractRole", extractRole)));
    }

    //J1 generateToken + extractUsername -> el username del usuario
    @Test
    void extractUsername_whenTokenJustGenerated_returnsUsername() {
        String token = service.generateToken(appUserWithRole(Role.ROLE_USER));

        assertThat(service.extractUsername(token)).isEqualTo(USERNAME);
    }

    //J2 generateToken + extractRole -> el rol del usuario, para cada rol
    @ParameterizedTest
    @EnumSource(Role.class)
    void extractRole_whenTokenJustGenerated_returnsRole(Role role) {
        String token = service.generateToken(appUserWithRole(role));

        assertThat(service.extractRole(token)).isEqualTo(role);
    }

    //J3 Contenido del token -> claim "role" con el nombre del rol, fecha de emisión presente y expiración = emisión + la expiración configurada (con el redondeo a segundos de las fechas del token)
    @Test
    void generateToken_containsRoleClaimAndConfiguredExpiration() {
        String token = service.generateToken(appUserWithRole(Role.ROLE_ADMIN));

        Claims claims = claimsOf(token);

        assertThat(claims.getSubject()).isEqualTo(USERNAME);
        assertThat(claims.get("role", String.class)).isEqualTo("ROLE_ADMIN");
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();
        // Las fechas del token van en segundos: la diferencia es la expiración configurada, más como mucho un segundo de redondeo
        long lifetime = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();
        assertThat(lifetime).isBetween(ONE_HOUR, ONE_HOUR + 1_000L);
    }

    //J4 Token caducado (servicio con expiración negativa, sin esperar ni usar el reloj) -> ExpiredJwtException al extraer usuario y rol
    @ParameterizedTest(name = "{0}")
    @MethodSource("tokenOperations")
    void tokenOperations_whenTokenExpired_throwExpiredJwtException(BiFunction<JwtService, String, Object> operation) {
        JwtService expiredService = new JwtService(SECRET, EXPIRED_ONE_SECOND_AGO);
        String expiredToken = expiredService.generateToken(appUserWithRole(Role.ROLE_USER));

        assertThatThrownBy(() -> operation.apply(expiredService, expiredToken))
                .isInstanceOf(ExpiredJwtException.class);
    }

    //J5 Firma ajena (token firmado con otra clave) -> SignatureException al extraer usuario y rol
    @ParameterizedTest(name = "{0}")
    @MethodSource("tokenOperations")
    void tokenOperations_whenSignedWithOtherKey_throwSignatureException(BiFunction<JwtService, String, Object> operation) {
        String foreignToken = new JwtService(OTHER_SECRET, ONE_HOUR).generateToken(appUserWithRole(Role.ROLE_USER));

        assertThatThrownBy(() -> operation.apply(service, foreignToken))
                .isInstanceOf(SignatureException.class);
    }

    //J6 Token manipulado: payload con el rol cambiado a ROLE_ADMIN conservando la firma original -> JwtException (SignatureException); nadie escala privilegios editando el token
    @Test
    void extractRole_whenPayloadTamperedKeepingOriginalSignature_throwsJwtException() {
        String token = service.generateToken(appUserWithRole(Role.ROLE_USER));
        String[] parts = token.split("\\.");

        String originalPayload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        String tamperedPayload = originalPayload.replace("ROLE_USER", "ROLE_ADMIN");
        // Precondición: el payload realmente cambia y ahora dice ROLE_ADMIN (el test no es vacuo)
        assertThat(tamperedPayload).isNotEqualTo(originalPayload).contains("ROLE_ADMIN");

        String tamperedToken = parts[0] + "."
                + Base64.getUrlEncoder().withoutPadding().encodeToString(tamperedPayload.getBytes(StandardCharsets.UTF_8))
                + "." + parts[2];

        assertThatThrownBy(() -> service.extractRole(tamperedToken))
                .isInstanceOf(JwtException.class)
                .isInstanceOf(SignatureException.class);
        assertThatThrownBy(() -> service.extractUsername(tamperedToken))
                .isInstanceOf(SignatureException.class);
    }

    //J7 Cadenas que no son un JWT -> JwtException (el filtro solo captura JwtException)
    @ParameterizedTest
    @ValueSource(strings = {"abc", "a.b.c"})
    void tokenOperations_whenStringIsNotJwt_throwJwtException(String notAToken) {
        assertThatThrownBy(() -> service.extractUsername(notAToken)).isInstanceOf(JwtException.class);
        assertThatThrownBy(() -> service.extractRole(notAToken)).isInstanceOf(JwtException.class);
    }

    //J7b CARACTERIZACIÓN (ver D-J2): cadena vacía o en blanco -> IllegalArgumentException, que NO es JwtException (la librería valida el texto antes de parsear).
    // Es la excepción real que sale hoy; el filtro JwtAuthenticationFilter solo captura JwtException (discrepancia D-J2, pendiente de decisión).
    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void tokenOperations_whenStringEmptyOrBlank_throwIllegalArgumentExceptionNotJwtException(String emptyOrBlank) {
        assertThatThrownBy(() -> service.extractUsername(emptyOrBlank))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
        assertThatThrownBy(() -> service.extractRole(emptyOrBlank))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }
}
