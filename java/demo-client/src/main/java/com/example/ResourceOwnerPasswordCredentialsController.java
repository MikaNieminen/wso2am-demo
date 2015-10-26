package com.example;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Created by ttapanai on 07/10/15.
 */

@Controller
@ConfigurationProperties(prefix = "clientapp")
public class ResourceOwnerPasswordCredentialsController {

    private String tokenUrl;
    private String serviceUrl;
    private String consumerId;
    private String consumerSecret;

    private static final String ACCESS_TOKEN_KEY = "ACCESS_TOKEN";

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
        OAuthClientRequest oauthRequest = null;
        try {
            oauthRequest = OAuthClientRequest
                    .tokenLocation(tokenUrl)
                    .setGrantType(GrantType.PASSWORD)
                    .setClientId(consumerId)
                    .setClientSecret(consumerSecret)
                    .setUsername(username)
                    .setPassword(password)
                    .buildQueryMessage();

            OAuthClient client = new OAuthClient(new URLConnectionClient());
            OAuthAccessTokenResponse tokenResponse = client.accessToken(oauthRequest, OAuthJSONAccessTokenResponse.class);
            request.getSession().setAttribute(ACCESS_TOKEN_KEY, tokenResponse.getAccessToken());
            System.out.println("access token: " + tokenResponse.getAccessToken());
            return "redirect:service-invoker.html";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/service", method = RequestMethod.POST)
    @ResponseBody
    public String callService(@RequestParam int count, HttpSession session) {

        try {
            OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(serviceUrl)
                    .setAccessToken((String) session.getAttribute(ACCESS_TOKEN_KEY)).buildHeaderMessage();

            bearerClientRequest.setHeader(OAuth.HeaderType.CONTENT_TYPE, "application/json");

            OAuthResourceResponse resourceResponse = null;
            OAuthClient client = new OAuthClient(new URLConnectionClient());
            for (int i = 1; i <= count; ++i) {
                resourceResponse = callService(bearerClientRequest, client, i);
            }

            return resourceResponse.getBody();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    private OAuthResourceResponse callService(OAuthClientRequest bearerClientRequest, OAuthClient client, int i) throws OAuthSystemException, OAuthProblemException {
        OAuthResourceResponse resourceResponse;
        System.out.println("call " + i);
        resourceResponse = client.resource(bearerClientRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
        return resourceResponse;
    }

    @RequestMapping("/")
    public String home() {
        return "login-form.html";
    }

    static {
        //for localhost testing only
        HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier(){

                    public boolean verify(String hostname,
                                          javax.net.ssl.SSLSession sslSession) {
                        if (hostname.equals("192.168.99.100")) {
                            return true;
                        }
                        return false;
                    }
                });

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                try {
                    InputStream inStream = new FileInputStream("certificate.der");
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    X509Certificate cert = (X509Certificate)cf.generateCertificate(inStream);
                    inStream.close();
                    return new X509Certificate[] {cert};
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (GeneralSecurityException e) {
            System.out.println(e.getStackTrace());
            throw new RuntimeException(e);
        }
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }
}
