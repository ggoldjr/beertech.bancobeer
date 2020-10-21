package br.com.api.dto;

import br.com.api.model.Usuario;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContaDto {

    Long id;
    String hash;
    String usuario;

}
