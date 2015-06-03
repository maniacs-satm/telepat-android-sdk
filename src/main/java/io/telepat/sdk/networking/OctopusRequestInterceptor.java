package io.telepat.sdk.networking;

import io.telepat.sdk.Telepat;
import io.telepat.sdk.utilities.FacebookTokenException;
import io.telepat.sdk.utilities.TelepatConstants;
import io.telepat.sdk.utilities.TelepatUtilities;

import java.security.MessageDigest;

import retrofit.RequestInterceptor;

/**
 * Created by Andrei Marinescu on 02.06.2015.
 * Retrofit RequestInterceptor implementation for adding required HTTP headers
 */
public class OctopusRequestInterceptor implements RequestInterceptor{
    private String appId;
    private String apiKey;
    private String apiKeyHash;

    private String udid;

    private String authorizationToken;

    public OctopusRequestInterceptor(String apiKey, String appId) {
        this.appId = appId;
        this.apiKey = apiKey;
        if(apiKey != null) {
            this.apiKeyHash = TelepatUtilities.sha256(apiKey);
        }
        this.udid = (String) Telepat.getInstance()
                .getDBInstance()
                .getOperationsData(TelepatConstants.UDID_KEY, "", String.class);
        Long authTokenTs = (Long) Telepat.getInstance()
                .getDBInstance()
                .getOperationsData(TelepatConstants.JWT_TIMESTAMP_KEY, 0, Long.class);
        if(System.currentTimeMillis() - authTokenTs < TelepatConstants.JWT_MAX_AGE) {
            this.authorizationToken = (String) Telepat.getInstance()
                    .getDBInstance()
                    .getOperationsData(TelepatConstants.UDID_KEY, null, String.class);
        }

    }

    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("Content-Type", "application/json");
        if(apiKeyHash!=null) { request.addHeader("X-BLGREQ-SIGN", apiKeyHash); }
        if(appId!=null) { request.addHeader("X-BLGREQ-APPID", appId); }
        if(udid!=null) { request.addHeader("X-BLGREQ-UDID", udid); }
        else { request.addHeader("X-BLGREQ-UDID",""); }
        if(authorizationToken!=null) {
            request.addHeader("Authorization","Bearer "+authorizationToken);
        }
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }
}
