package br.com.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Builder
@AllArgsConstructor
public class Transferencia implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conta_hash")
    private Conta contaOrigem;
    
    private String contaDestinoHash;

    private Double valor;

    public Transferencia() {}
}