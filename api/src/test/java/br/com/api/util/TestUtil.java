package br.com.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class TestUtil {

    public ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    public final TestRestTemplate restTemplate;
    private String authorization;

    public TestUtil(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public void login(int port, String login) {
        ResponseEntity<String> responseEntity = restTemplate.withBasicAuth(login, "senha")
                .getForEntity(String.format("http://localhost:%s/login", port), String.class);
        authorization = String.format("Bearer %s", responseEntity.getBody());
    }


    private HttpHeaders getHttpHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", authorization);
        return headers;
    }

    public HttpEntity<String> getHttpEntity(Object body)  {
        try {
            return new HttpEntity<>(mapper.writeValueAsString(body), getHttpHeader());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpEntity<String> getHttpEntityWithoutHeaders(Object body)  {
        try {
            return new HttpEntity<>(mapper.writeValueAsString(body));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public HttpEntity<String> getHttpEntity() {
        return new HttpEntity<>(getHttpHeader());
    }

    public <T> T parseSuccessfulResponse(ResponseEntity<String> responseEntity, Class<T> tclass) {
        try {
            return mapper.readValue(responseEntity.getBody(), tclass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> parseSuccessfulResponseList(ResponseEntity<String> responseEntity, Class<T> tclass) {
        try {
            return mapper.readValue(responseEntity.getBody(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseError parseResponseError(ResponseEntity<String> responseEntity) {
        try {
            return mapper.readValue(responseEntity.getBody(), ResponseError.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}