package br.com.unasp.faculdade.response;

public record ResponseProfessor(
        Integer id,
        String nome,
        String email,
        String departamento
) {
}
