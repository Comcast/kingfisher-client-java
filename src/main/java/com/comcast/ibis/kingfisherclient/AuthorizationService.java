package com.comcast.ibis.kingfisherclient;

import com.comcast.ibis.kingfisherclient.common.Constants;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Date;


/**
 * The type Authorization service.
 */
public class AuthorizationService {
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private ObjectMapper mapper;
    private String apiKey;
    private String host;

    /**
     * Instantiates a new Authorization service.
     *
     * @param apiKey the api key
     */

    AuthorizationService(String apiKey, String host) {
        this.mapper = new ObjectMapper();
        this.apiKey = apiKey;
        this.host = host;
    }

    /**
     * Gets current user org.
     *
     * @return the current user org
     * @throws IOException the io exception
     */
    public String getCurrentUserOrg() throws  IOException{

        HttpGet request = new HttpGet(Constants.AUTHORITY_SERVICE + "/users/current");
        request.addHeader("Authorization", "apikey " + apiKey);
        CloseableHttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        Header headers = entity.getContentType();

        if (entity == null) {
            throw new Error("no response body found");
        }

        JsonNode jsonNode = mapper.readTree(EntityUtils.toString(entity));

        if(jsonNode.get("error") != null) {
            throw new IllegalArgumentException(jsonNode.get("error").toString());
        }

        CurrentUser user = this.mapper.readValue(jsonNode.get("result").toString(), CurrentUser.class);
        return user.getOrg();
    }

    /**
     * The type Current user.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrentUser {
        private String org;
        private String id;
        private String name;
        private String email;
        private Team[] teams;
        private Date created;
        private Date updated;
    }

    /**
     * The type Team.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Team {
        private String org;
        private String id;
    }
}