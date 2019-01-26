package br.com.leonardoferreira.poc.restspringsecurity.security;

import br.com.leonardoferreira.poc.restspringsecurity.domain.Account;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class TokenAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RsaVerifier rsaVerifier;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            Jwt jwt = JwtHelper.decodeAndVerify((String) authentication.getPrincipal(), rsaVerifier);
            Account account = objectMapper.readValue(jwt.getClaims(), Account.class);
            return new PreAuthenticatedAuthenticationToken(account, account.getPassword(), account.getAuthorities());
        } catch (Exception e) {
            throw new PreAuthenticatedCredentialsNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(PreAuthenticatedAuthenticationToken.class);
    }

}
