package br.com.maddytec.cliente.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("cliente")
public class Cliente implements Serializable {

        @Id
        private Long id;

        private String nome;

        private String email;

        private String cpf;

}
