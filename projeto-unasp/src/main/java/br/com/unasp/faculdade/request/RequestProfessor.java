package br.com.unasp.faculdade.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RequestProfessor(
        @NotBlank(message = "O nome do professor é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 100, message = "O email deve ter no máximo 100 caracteres")
        String email,

        @Size(max = 50, message = "O departamento deve ter no máximo 50 caracteres")
        String departamento
) {
}
