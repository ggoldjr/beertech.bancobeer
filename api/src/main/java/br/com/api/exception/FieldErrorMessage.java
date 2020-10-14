package br.com.api.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldErrorMessage {

    private String name;
    private String message;

    @JsonCreator
    public FieldErrorMessage(@JsonProperty("name") String name,
                             @JsonProperty("message") String message) {
        this.name = name;
        this.message = message;
    }
}
