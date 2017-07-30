package org.pac4j.oidc.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.auth.*;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpUtils;
import org.pac4j.core.util.InitializableWebObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * OpenID Connect configuration.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class OidcConfiguration extends InitializableWebObject {

    public static final String SCOPE = "scope";
    public static final String RESPONSE_TYPE = "response_type";
    public static final String RESPONSE_MODE = "response_mode";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String CLIENT_ID = "client_id";
    public static final String STATE = "state";
    public static final String NONCE = "nonce";

    /* state attribute name in session */
    public static final String STATE_SESSION_ATTRIBUTE = "oidcStateAttribute";

    /* nonce attribute name in session */
    public static final String NONCE_SESSION_ATTRIBUTE = "oidcNonceAttribute";

    /* default max clock skew */
    public static final int DEFAULT_MAX_CLOCK_SKEW = 30;

    /* OpenID client identifier */
    private String clientId;

    /* OpenID secret */
    private String secret;

    /* discovery URI for fetching OP metadata (http://openid.net/specs/openid-connect-discovery-1_0.html) */
    private String discoveryURI;

    /* Scope */
    private String scope;

    /* Map containing user defined parameters */
    private Map<String, String> customParams = new HashMap<>();

    /* client authentication method used at token End Point */
    private ClientAuthenticationMethod clientAuthenticationMethod;

    /* use nonce? */
    private boolean useNonce;

    /* Preferred JWS algorithm */
    private JWSAlgorithm preferredJwsAlgorithm;

    /* max clock skew in seconds */
    private int maxClockSkew = DEFAULT_MAX_CLOCK_SKEW;

    private ResourceRetriever resourceRetriever;

    private OIDCProviderMetadata providerMetadata;

    private String callbackUrl;

    private String responseType;

    private String responseMode;

    private String logoutUrl;

    @Override
    protected void internalInit(final WebContext context) {
        // checks
        CommonHelper.assertNotBlank("clientId", getClientId());
        CommonHelper.assertNotBlank("secret", getSecret());
        if (this.getDiscoveryURI() == null && this.getProviderMetadata() == null) {
            throw new TechnicalException("You must define either the discovery URL or directly the provider metadata");
        }

        // default value
        if (getResourceRetriever() == null) {
            setResourceRetriever(new DefaultResourceRetriever(getConnectTimeout(),getReadTimeout()));
        }
        if (this.getProviderMetadata() == null) {
            CommonHelper.assertNotBlank("discoveryURI", getDiscoveryURI());
            try {
                // Download OIDC metadata
                this.setProviderMetadata(OIDCProviderMetadata.parse(getResourceRetriever().retrieveResource(
                        new URL(this.getDiscoveryURI())).getContent()));
            } catch (final IOException | ParseException e) {
                throw new TechnicalException(e);
            }
        }
    }

    public OIDCProviderMetadata getProviderMetadata() {
        return this.providerMetadata;
    }

    public void setProviderMetadata(final OIDCProviderMetadata providerMetadata) {
        this.providerMetadata = providerMetadata;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(final String secret) {
        this.secret = secret;
    }

    public String getDiscoveryURI() {
        return discoveryURI;
    }

    public void defaultDiscoveryURI(final String discoveryURI) {
        if (this.discoveryURI == null) {
            this.discoveryURI = discoveryURI;
        }
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(final String scope) {
        this.scope = scope;
    }

    public Map<String, String> getCustomParams() {
        return customParams;
    }

    public String getCustomParam(String name) {
        return customParams.get(name);
    }

    public void setCustomParams(final Map<String, String> customParams) {
        CommonHelper.assertNotNull("customParams", customParams);
        this.customParams = customParams;
    }

    public void addCustomParam(final String key, final String value) {
        this.customParams.put(key, value);
    }

    public ClientAuthenticationMethod getClientAuthenticationMethod() {
        return clientAuthenticationMethod;
    }

    public void setClientAuthenticationMethod(final ClientAuthenticationMethod clientAuthenticationMethod) {
        this.clientAuthenticationMethod = clientAuthenticationMethod;
    }

    public void setClientAuthenticationMethodAsString(String auth) {
        this.clientAuthenticationMethod = ClientAuthenticationMethod.parse(auth);
    }

    public boolean isUseNonce() {
        return useNonce;
    }

    public void setUseNonce(final boolean useNonce) {
        this.useNonce = useNonce;
    }

    public JWSAlgorithm getPreferredJwsAlgorithm() {
        return preferredJwsAlgorithm;
    }

    public void setPreferredJwsAlgorithm(final JWSAlgorithm preferredJwsAlgorithm) {
        this.preferredJwsAlgorithm = preferredJwsAlgorithm;
    }

    public int getMaxClockSkew() {
        return maxClockSkew;
    }

    public void setMaxClockSkew(final int maxClockSkew) {
        this.maxClockSkew = maxClockSkew;
    }

    @Deprecated
    public int getConnectTimeout() {
        return HttpUtils.getConnectTimeout();
    }

    @Deprecated
    public void setConnectTimeout(final int connectTimeout) {
        HttpUtils.setConnectTimeout(connectTimeout);
    }

    @Deprecated
    public int getReadTimeout() {
        return HttpUtils.getReadTimeout();
    }

    @Deprecated
    public void setReadTimeout(final int readTimeout) {
        HttpUtils.setReadTimeout(readTimeout);
    }

    public ResourceRetriever getResourceRetriever() {
        return resourceRetriever;
    }

    public void defaultResourceRetriever(final ResourceRetriever resourceRetriever) {
        if (this.resourceRetriever == null) {
            this.resourceRetriever = resourceRetriever;
        }
    }

    public void setDiscoveryURI(final String discoveryURI) {
        this.discoveryURI = discoveryURI;
    }

    public void setResourceRetriever(final ResourceRetriever resourceRetriever) {
        this.resourceRetriever = resourceRetriever;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(final String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(final String responseType) {
        this.responseType = responseType;
    }

    public String getResponseMode() {
        return responseMode;
    }

    public void setResponseMode(final String responseMode) {
        this.responseMode = responseMode;
    }

    public String getLogoutUrl() {
        if(logoutUrl == null && getProviderMetadata().getEndSessionEndpointURI() != null) {
            return getProviderMetadata().getEndSessionEndpointURI().toString();
        }
        return logoutUrl;
    }

    public void setLogoutUrl(final String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "clientId", clientId, "secret", "[protected]", "discoveryURI", discoveryURI, 
                "scope", scope, "customParams", customParams, "clientAuthenticationMethod", clientAuthenticationMethod, 
                "useNonce", useNonce, "preferredJwsAlgorithm", preferredJwsAlgorithm, "maxClockSkew", maxClockSkew, 
                "connectTimeout", HttpUtils.getConnectTimeout(), "readTimeout", HttpUtils.getReadTimeout(), 
                "resourceRetriever", resourceRetriever, "callbackUrl", callbackUrl, "responseType", responseType, 
                "responseMode", responseMode, "logoutUrl", logoutUrl);
    }
}
