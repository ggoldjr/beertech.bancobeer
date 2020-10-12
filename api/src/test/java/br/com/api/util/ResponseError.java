package br.com.api.util;


import br.com.api.exception.FieldErrorMessage;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseError {
    private Integer status;
    private String message;
    private List<FieldErrorMessage> errors;

    @JsonCreator
    public ResponseError(@JsonProperty("status") Integer status,
                         @JsonProperty("message") String message,
                         @JsonProperty("errors") List<FieldErrorMessage> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }
}
