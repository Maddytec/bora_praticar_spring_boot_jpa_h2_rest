package br.com.maddytec.cliente.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Cliente implements Serializable {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        @Column(name = "nome", nullable = false)
        private String nome;

        @Column(name = "email")
        private String email;

        @Column(name = "cpf")
        private String cpf;

}
