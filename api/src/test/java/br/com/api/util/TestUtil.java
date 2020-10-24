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

    public HttpEntity<String> getHttpEntity(Object body) throws JsonProcessingException {
        return new HttpEntity<>(mapper.writeValueAsString(body), getHttpHeader());
    }
    public HttpEntity<String> getHttpEntity() {
        return new HttpEntity<>(getHttpHeader());
    }

    public <T> T parseSuccessfulResponse(ResponseEntity<String> responseEntity, Class<T> tclass) throws JsonProcessingException {
        return mapper.readValue(responseEntity.getBody(), tclass);
    }

    public <T> List<T> parseSuccessfulResponseList(ResponseEntity<String> responseEntity, Class<T> tclass) throws JsonProcessingException {
        return mapper.readValue(responseEntity.getBody(), new TypeReference<>() {});
    }

    public ResponseError parseResponseError(ResponseEntity<String> responseEntity) throws JsonProcessingException {
        return mapper.readValue(responseEntity.getBody(), ResponseError.class);
    }
}