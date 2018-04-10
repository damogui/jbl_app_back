/**
 * Copyright (c) 2016. Hand Enterprise Solution Company. All right reserved. Project
 * Name:hstaffParent Package Name:hstaff.core.token Date:2016/7/9 0009 Create
 * By:zongyun.zhou@hand-china.com
 */
package hmap.core.token;

import hmap.core.hms.system.domain.HmsSystemConfig;
import hmap.core.hms.system.service.IHmsSystemConfigService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.*;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class CustomTokenServices implements AuthorizationServerTokenServices,
        ResourceServerTokenServices, ConsumerTokenServices, InitializingBean {
    private int refreshTokenValiditySeconds = 7200;
    private int accessTokenValiditySeconds = ' ';
    private boolean supportRefreshToken = false;
    private boolean reuseRefreshToken = true;
    private TokenStore tokenStore;
    private ClientDetailsService clientDetailsService;
    private TokenEnhancer accessTokenEnhancer;
    private AuthenticationManager authenticationManager;

    @Autowired
    IHmsSystemConfigService hmsSystemConfigService;
    @Autowired
    IHmsSystemConfigService systemConfigService;

    public CustomTokenServices() {
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.tokenStore, "tokenStore must be set");
    }

    @Transactional
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication)
            throws AuthenticationException {
        OAuth2AccessToken existingAccessToken = this.tokenStore.getAccessToken(authentication);
        OAuth2RefreshToken refreshToken = null;

        if (existingAccessToken != null) {
            HmsSystemConfig config = hmsSystemConfigService.selectByConfigKey("hmap.singleUser");
            String singleUser = null;
            if (config != null) {
                singleUser = hmsSystemConfigService.selectByConfigKey("hmap.singleUser").getConfigValue();
            }
            //每次请求token都产生一个新的token，并删除旧的token
            if ("N".equals(singleUser)) {
                if (!existingAccessToken.isExpired()) {
                    this.tokenStore.storeAccessToken(existingAccessToken, authentication);
                    return existingAccessToken;
                }
            }

            if (existingAccessToken.getRefreshToken() != null) {
                refreshToken = existingAccessToken.getRefreshToken();
                this.tokenStore.removeRefreshToken(refreshToken);
            }
            this.tokenStore.removeAccessToken(existingAccessToken);
        }

        if (refreshToken == null) {
            refreshToken = this.createRefreshToken(authentication);
        } else if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
            ExpiringOAuth2RefreshToken accessToken = (ExpiringOAuth2RefreshToken) refreshToken;
            if (System.currentTimeMillis() > accessToken.getExpiration().getTime()) {
                refreshToken = this.createRefreshToken(authentication);
            }
        }

        OAuth2AccessToken accessToken1 = this.createAccessToken(authentication, refreshToken);
        this.tokenStore.storeAccessToken(accessToken1, authentication);
        refreshToken = accessToken1.getRefreshToken();
        if (refreshToken != null) {
            this.tokenStore.storeRefreshToken(refreshToken, authentication);
        }

        return accessToken1;
    }

    @Transactional(noRollbackFor = {InvalidTokenException.class, InvalidGrantException.class})
    public OAuth2AccessToken refreshAccessToken(String refreshTokenValue, TokenRequest tokenRequest)
            throws AuthenticationException {
        if (!this.supportRefreshToken) {
            throw new InvalidGrantException("Invalid refresh token: " + refreshTokenValue);
        } else {
            OAuth2RefreshToken refreshToken = this.tokenStore.readRefreshToken(refreshTokenValue);
            if (refreshToken == null) {
                throw new InvalidGrantException("Invalid refresh token: " + refreshTokenValue);
            } else {
                OAuth2Authentication authentication =
                        this.tokenStore.readAuthenticationForRefreshToken(refreshToken);
                if (this.authenticationManager != null && !authentication.isClientOnly()) {
                    PreAuthenticatedAuthenticationToken clientId = new PreAuthenticatedAuthenticationToken(
                            authentication.getUserAuthentication(), "", authentication.getAuthorities());
                    Authentication clientId1 = this.authenticationManager.authenticate(clientId);
                    Object accessToken = authentication.getDetails();
                    authentication = new OAuth2Authentication(authentication.getOAuth2Request(), clientId1);
                    authentication.setDetails(accessToken);
                }

                OAuth2AccessToken existingAccessToken = this.tokenStore.getAccessToken(authentication);
                String ifDelay = systemConfigService.selectByConfigKey("hmap.accessTokenExpireCanbeRefresh").getConfigValue();

                String clientId2 = authentication.getOAuth2Request().getClientId();
                if (clientId2 != null && clientId2.equals(tokenRequest.getClientId())) {
                    this.tokenStore.removeAccessTokenUsingRefreshToken(refreshToken);
                    if (this.isExpired(refreshToken)) {
                        this.tokenStore.removeRefreshToken(refreshToken);
                        throw new InvalidTokenException("Invalid refresh token (expired): " + refreshToken);
                    } else {

                        OAuth2AccessToken accessToken1 = null;
                        if (!existingAccessToken.isExpired() && "Y".equals(ifDelay)) {

                            DefaultOAuth2AccessToken defaultOAuth2AccessToken = new DefaultOAuth2AccessToken(existingAccessToken);
                            int validitySeconds = this.getAccessTokenValiditySeconds(authentication.getOAuth2Request());
                            defaultOAuth2AccessToken.setExpiration(new Date(System.currentTimeMillis() + (long) validitySeconds * 1000L));
                            defaultOAuth2AccessToken.setRefreshToken(refreshToken);
                            defaultOAuth2AccessToken.setScope(authentication.getOAuth2Request().getScope());

                            if (this.accessTokenEnhancer != null) {
                                accessToken1 = (OAuth2AccessToken) this.accessTokenEnhancer.enhance(defaultOAuth2AccessToken, authentication);
                            } else {
                                accessToken1 = (OAuth2AccessToken) defaultOAuth2AccessToken;
                            }
                            this.tokenStore.storeAccessToken(accessToken1, authentication);

                        } else {

                            authentication = this.createRefreshedAuthentication(authentication, tokenRequest);
                            if (!this.reuseRefreshToken) {
                                this.tokenStore.removeRefreshToken(refreshToken);
                                refreshToken = this.createRefreshToken(authentication);
                            }

                            accessToken1 = this.createAccessToken(authentication, refreshToken);
                            this.tokenStore.storeAccessToken(accessToken1, authentication);
                            if (!this.reuseRefreshToken) {
                                this.tokenStore.storeRefreshToken(refreshToken, authentication);
                            }

                        }
                        return accessToken1;
                    }
                } else {
                    throw new InvalidGrantException(
                            "Wrong client for this refresh token: " + refreshTokenValue);
                }
            }
        }
    }

    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        return this.tokenStore.getAccessToken(authentication);
    }

    private OAuth2Authentication createRefreshedAuthentication(OAuth2Authentication authentication,
                                                               TokenRequest request) {
        Set scope = request.getScope();
        OAuth2Request clientAuth = authentication.getOAuth2Request().refresh(request);
        if (scope != null && !scope.isEmpty()) {
            Set originalScope = clientAuth.getScope();
            if (originalScope == null || !originalScope.containsAll(scope)) {
                throw new InvalidScopeException(
                        "Unable to narrow the scope of the client authentication to " + scope + ".",
                        originalScope);
            }

            clientAuth = clientAuth.narrowScope(scope);
        }

        OAuth2Authentication narrowed =
                new OAuth2Authentication(clientAuth, authentication.getUserAuthentication());
        return narrowed;
    }

    protected boolean isExpired(OAuth2RefreshToken refreshToken) {
        if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
            ExpiringOAuth2RefreshToken expiringToken = (ExpiringOAuth2RefreshToken) refreshToken;
            return expiringToken.getExpiration() == null
                    || System.currentTimeMillis() > expiringToken.getExpiration().getTime();
        } else {
            return false;
        }
    }

    public OAuth2AccessToken readAccessToken(String accessToken) {
        return this.tokenStore.readAccessToken(accessToken);
    }

    public OAuth2Authentication loadAuthentication(String accessTokenValue)
            throws AuthenticationException, InvalidTokenException {
        OAuth2AccessToken accessToken = this.tokenStore.readAccessToken(accessTokenValue);
        if (accessToken == null) {
            throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
        } else if (accessToken.isExpired()) {
            this.tokenStore.removeAccessToken(accessToken);
            throw new InvalidTokenException("Access token expired: " + accessTokenValue);
        } else {
            OAuth2Authentication result = this.tokenStore.readAuthentication(accessToken);
            if (this.clientDetailsService != null) {
                String clientId = result.getOAuth2Request().getClientId();

                try {
                    this.clientDetailsService.loadClientByClientId(clientId);
                } catch (ClientRegistrationException var6) {
                    throw new InvalidTokenException("Client not valid: " + clientId, var6);
                }
            }else{
                this.tokenStore.removeAccessToken(accessToken);
                throw new InvalidTokenException("Access token expired: " + accessTokenValue);
            }

            return result;
        }
    }

    public String getClientId(String tokenValue) {
        OAuth2Authentication authentication = this.tokenStore.readAuthentication(tokenValue);
        if (authentication == null) {
            throw new InvalidTokenException("Invalid access token: " + tokenValue);
        } else {
            OAuth2Request clientAuth = authentication.getOAuth2Request();
            if (clientAuth == null) {
                throw new InvalidTokenException("Invalid access token (no client id): " + tokenValue);
            } else {
                return clientAuth.getClientId();
            }
        }
    }

    public boolean revokeToken(String tokenValue) {
        OAuth2AccessToken accessToken = this.tokenStore.readAccessToken(tokenValue);
        if (accessToken == null) {
            return false;
        } else {
            if (accessToken.getRefreshToken() != null) {
                this.tokenStore.removeRefreshToken(accessToken.getRefreshToken());
            }

            this.tokenStore.removeAccessToken(accessToken);
            return true;
        }
    }

    private OAuth2RefreshToken createRefreshToken(OAuth2Authentication authentication) {
        if (!this.isSupportRefreshToken(authentication.getOAuth2Request())) {
            return null;
        } else {
            int validitySeconds = this.getRefreshTokenValiditySeconds(authentication.getOAuth2Request());
            String value = UUID.randomUUID().toString();
            return (OAuth2RefreshToken) (validitySeconds > 0
                    ? new DefaultExpiringOAuth2RefreshToken(value,
                    new Date(System.currentTimeMillis() + (long) validitySeconds * 1000L))
                    : new DefaultOAuth2RefreshToken(value));
        }
    }

    private OAuth2AccessToken createAccessToken(OAuth2Authentication authentication,
                                                OAuth2RefreshToken refreshToken) {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(UUID.randomUUID().toString());
        int validitySeconds = this.getAccessTokenValiditySeconds(authentication.getOAuth2Request());
        if (validitySeconds > 0) {
            token.setExpiration(new Date(System.currentTimeMillis() + (long) validitySeconds * 1000L));
        }

        token.setRefreshToken(refreshToken);
        token.setScope(authentication.getOAuth2Request().getScope());
        return (OAuth2AccessToken) (this.accessTokenEnhancer != null
                ? this.accessTokenEnhancer.enhance(token, authentication) : token);
    }

    protected int getAccessTokenValiditySeconds(OAuth2Request clientAuth) {
        if (this.clientDetailsService != null) {
            ClientDetails client =
                    this.clientDetailsService.loadClientByClientId(clientAuth.getClientId());
            Integer validity = client.getAccessTokenValiditySeconds();
            if (validity != null) {
                return validity.intValue();
            }
        }

        return this.accessTokenValiditySeconds;
    }

    protected int getRefreshTokenValiditySeconds(OAuth2Request clientAuth) {
        if (this.clientDetailsService != null) {
            ClientDetails client =
                    this.clientDetailsService.loadClientByClientId(clientAuth.getClientId());
            Integer validity = client.getRefreshTokenValiditySeconds();
            if (validity != null) {
                return validity.intValue();
            }
        }

        return this.refreshTokenValiditySeconds;
    }

    protected boolean isSupportRefreshToken(OAuth2Request clientAuth) {
        if (this.clientDetailsService != null) {
            ClientDetails client =
                    this.clientDetailsService.loadClientByClientId(clientAuth.getClientId());
            return client.getAuthorizedGrantTypes().contains("refresh_token");
        } else {
            return this.supportRefreshToken;
        }
    }

    public void setTokenEnhancer(TokenEnhancer accessTokenEnhancer) {
        this.accessTokenEnhancer = accessTokenEnhancer;
    }

    public void setRefreshTokenValiditySeconds(int refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    public void setAccessTokenValiditySeconds(int accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    public void setSupportRefreshToken(boolean supportRefreshToken) {
        this.supportRefreshToken = supportRefreshToken;
    }

    public void setReuseRefreshToken(boolean reuseRefreshToken) {
        this.reuseRefreshToken = reuseRefreshToken;
    }

    public void setTokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setClientDetailsService(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }
}
