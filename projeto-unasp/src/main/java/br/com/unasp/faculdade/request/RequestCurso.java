package br.com.unasp.faculdade.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RequestCurso(
        @NotBlank(message = "O nome do curso é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @NotNull(message = "O id do professor é obrigatório")
        Integer professorId
) {
}
