//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package hmap.core.service.impl;

import com.github.pagehelper.StringUtil;
import hmap.core.beans.TransferDataMapper;
import hmap.core.hms.api.command.RestApiCommand;
import hmap.core.hms.api.domain.HmsOauthToken;
import hmap.core.hms.api.dto.HeaderAndLineDTO;
import hmap.core.hms.api.service.IApiService;
import hmap.core.hms.api.service.IHmsOauthTokenService;
import hmap.core.hms.system.domain.HmsThirdpartyApp;
import hmap.core.hms.system.service.IHmsThirdpartyAppService;
import hmap.core.security.SecurityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.codehaus.plexus.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class JbRestApiServiceImpl implements IApiService {
    private final Logger logger = LoggerFactory.getLogger(JbRestApiServiceImpl.class);
    @Autowired
    IHmsOauthTokenService hmsOauthTokenService;
    @Autowired
    IHmsThirdpartyAppService hmsThirdpartyAppService;
    @Resource(
            name = "restTemplate"
    )
    RestTemplate restTemplate;
    @Autowired
    RandomValueAuthorizationCodeServices randomValueAuthorizationCodeServices;

    public JbRestApiServiceImpl() {
    }

    HttpHeaders setHeaders(HeaderAndLineDTO headerAndLineDTO) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Charset","utf-8");
        if(headerAndLineDTO.getRequestContenttype() != null) {
            httpHeaders.set("Content-Type", headerAndLineDTO.getRequestContenttype());
        } else {
            httpHeaders.set("Content-Type", "application/json");
        }

        if(headerAndLineDTO.getRequestAccept() != null) {
            httpHeaders.set("Accept", headerAndLineDTO.getRequestAccept());
        }

        if(headerAndLineDTO.getAuthType()!=null && headerAndLineDTO.getAuthType().equals("BASIC_AUTH")) {
            String e1 = headerAndLineDTO.getAuthUsername() + ":" + headerAndLineDTO.getAuthPassword();
            String basicBase64 = new String(Base64.encodeBase64(e1.getBytes()));
            httpHeaders.set("Authorization", "Basic " + basicBase64);
        }

        if(headerAndLineDTO.getAuthType()!=null &&  headerAndLineDTO.getAuthType().equals("OAUTH2")) {
            HmsThirdpartyApp thirdpartyApp;
            HashMap oauthConfig;
            if(headerAndLineDTO.getGrantType().equals("CLIENT_CREDENTIALS")) {
                oauthConfig = new HashMap();
                thirdpartyApp = this.hmsThirdpartyAppService.selectById(headerAndLineDTO.getThirdpartyId());
                oauthConfig.put("clientId", thirdpartyApp.getAppKey());
                oauthConfig.put("clientSecret", thirdpartyApp.getAppSecret());
                oauthConfig.put("grantType", headerAndLineDTO.getGrantType());
                oauthConfig.put("authenticationServerUrl", headerAndLineDTO.getDomainUrl().concat(headerAndLineDTO.getAccessTokenUrl()));
                HmsOauthToken hmsOauthToken = this.hmsOauthTokenService.getAccessToken(oauthConfig, "N");
                this.logger.info("使用了oauth2验证，获得token为:{}", hmsOauthToken.getAccessToken());
                httpHeaders.set("ACCESS_TOKEN", hmsOauthToken.getAccessToken());
            }

            if(headerAndLineDTO.getGrantType().equals("PASSWORD")) {
                oauthConfig = new HashMap();
                thirdpartyApp = this.hmsThirdpartyAppService.selectById(headerAndLineDTO.getThirdpartyId());
                oauthConfig.put("clientId", thirdpartyApp.getAppKey());
                oauthConfig.put("clientSecret", thirdpartyApp.getAppSecret());
                oauthConfig.put("grantType", headerAndLineDTO.getGrantType());
                oauthConfig.put("userName", SecurityUtils.getCurrentUserLogin());
                SecurityContext securityContext = SecurityContextHolder.getContext();
                Authentication authentication = securityContext.getAuthentication();
                if(authentication instanceof OAuth2Authentication) {
                    OAuth2Authentication oAuth2Authentication = (OAuth2Authentication)authentication;
                    oauthConfig.put("password", this.randomValueAuthorizationCodeServices.createAuthorizationCode(oAuth2Authentication));
                }

                oauthConfig.put("authenticationServerUrl", headerAndLineDTO.getDomainUrl().concat(headerAndLineDTO.getAccessTokenUrl()));
                HmsOauthToken hmsOauthToken = this.hmsOauthTokenService.getAccessToken(oauthConfig, "N");
                this.logger.info("使用了oauth2验证，获得token为:{}", hmsOauthToken.getAccessToken());
                httpHeaders.set("ACCESS_TOKEN", hmsOauthToken.getAccessToken());
            }
        }

        return httpHeaders;
    }

    public String get(String url, HeaderAndLineDTO headerAndLineDTO, JSONObject params) {
        String resultData = "";
        if(params != null && params.size() > 0) {
            Iterator iterator = params.keys();

            String key;
            for(url = url + "?"; iterator.hasNext(); url = url + key + "=" + params.get(key) + "&") {
                key = (String)iterator.next();
            }

            url = url.substring(0, url.length() - 1);
        }

        this.logger.info("request url:{}", url);
        HttpHeaders headers = this.setHeaders(headerAndLineDTO);
        HttpEntity httpEntity = new HttpEntity(headers);
        System.setProperty("archaius.dynamicProperty.disableDefaultConfig", "true");
        RestApiCommand restApiCommand = new RestApiCommand(url, headerAndLineDTO, httpEntity, this.restTemplate, "GET");

        try {
            resultData = (String)restApiCommand.execute();
            if(restApiCommand.isResponseFromFallback()) {
                this.logger.error("返回结果由断路器提供，依赖系统开始熔断");
            }
        } catch (RestClientException var9) {
            this.logger.info("connect failed：{}", var9.getMessage());
            var9.printStackTrace();
        }

        this.logger.info("responseData:{}", resultData);
        return resultData;
    }

    public String post(String url, HeaderAndLineDTO headerAndLineDTO, String params) {
        /*String resultData = "";
        HttpHeaders headers = this.setHeaders(headerAndLineDTO);
        HttpEntity httpEntity = new HttpEntity(params, headers);
        System.setProperty("archaius.dynamicProperty.disableDefaultConfig", "true");
        RestApiCommand restApiCommand = new RestApiCommand(url, headerAndLineDTO, httpEntity, this.restTemplate, "POST");

        try {
            resultData = (String)restApiCommand.execute();
            if(restApiCommand.isResponseFromFallback()) {
                this.logger.error("返回结果由断路器提供，依赖系统开始熔断");
            }
        } catch (RestClientException var9) {
            this.logger.info("connect failed：{}", var9.getMessage());
            var9.printStackTrace();
        }

        this.logger.info("responseData:{}", resultData);
        return resultData;*/

        String resultData = "";
        try {
            URL myURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) myURL.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(60000);
            connection.setRequestProperty("Charset","utf-8");
            if (!params.isEmpty()) {
                connection.setDoOutput(true);
            }

            if (headerAndLineDTO.getRequestContenttype() != null) {
                connection.setRequestProperty("Content-Type", headerAndLineDTO.getRequestContenttype());
            } else {
                connection.setRequestProperty("Content-Type", "application/json");
            }

            /*String basicBase64;
            if (headerAndLineDTO.getAuthFlag().equals("Y")) {
                String e = headerAndLineDTO.getAuthUsername() + ":" + headerAndLineDTO.getAuthPassword();
                basicBase64 = new String(Base64.encodeBase64(e.getBytes()));
                connection.setRequestProperty("Authorization", "Basic " + basicBase64);
            }*/
            connection.connect();

            if (!params.isEmpty()) {
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(params.getBytes());
                outputStream.flush();
                outputStream.close();
            }
            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("HTTP GET Request Failed with Error code : " + connection.getResponseCode());
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
            StringBuilder results = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                results.append(line);
            }
            reader.close();
            connection.disconnect();
            resultData = results.toString();

        } catch (MalformedURLException var7) {
            var7.printStackTrace();
            logger.error(var7.getMessage());
        } catch (IOException var8) {
            var8.printStackTrace();
            logger.error(var8.getMessage());
        }
        return resultData;


    }


    public String invoke(HeaderAndLineDTO headerAndLineDTO, Object jsonElement) {
        {
            String url = headerAndLineDTO.getDomainUrl() + headerAndLineDTO.getIftUrl();
            this.logger.info("request url:{}", url);
            String data = null;
            String json = null;
            String inboundParam = "";
            if(headerAndLineDTO.getRequestMethod().equals("POST")) {
//                inboundParam = inbound.toString();
                if(jsonElement instanceof List) {
                    inboundParam = JSONArray.fromObject(jsonElement).toString();
                } else if(jsonElement instanceof Map) {
                    inboundParam = JSONObject.fromObject(jsonElement).toString();
                }
                if(StringUtil.isNotEmpty(headerAndLineDTO.getMapperClass())) {
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    TransferDataMapper mapper = null;

                    try {
                        Class c = cl.loadClass(headerAndLineDTO.getMapperClass());
                        mapper = (TransferDataMapper)c.newInstance();
                        JSONObject inbound = JSONObject.fromObject(jsonElement);
                        inboundParam = mapper.requestDataMap(inbound);
                    } catch (ClassNotFoundException var10) {
                        this.logger.error("ClassNotFoundException:" + var10.getMessage());
                        var10.printStackTrace();
                    } catch (InstantiationException var11) {
                        this.logger.error("InstantiationException:" + var11.getMessage());
                        var11.printStackTrace();
                    } catch (IllegalAccessException var12) {
                        this.logger.error("IllegalAccessException:" + var12.getMessage());
                        var12.printStackTrace();
                    }

                    this.logger.info("params Xml :{}", inboundParam.toString());
                    data = this.post(url, headerAndLineDTO, inboundParam);
                    data = mapper.responseDataMap(data);
                } else {
                    data = this.post(url, headerAndLineDTO, inboundParam);
                }
            } else if(headerAndLineDTO.getRequestMethod().equals("GET")) {
                JSONObject inbound = JSONObject.fromObject(jsonElement);
                data = this.get(url, headerAndLineDTO, JSONObject.fromObject(inbound));
            }

            return data;
        }
    }

    /*@Override
    public String invoke(HeaderAndLineDTO headerAndLineDTO, Object o) {
        return null;
    }*/
}
