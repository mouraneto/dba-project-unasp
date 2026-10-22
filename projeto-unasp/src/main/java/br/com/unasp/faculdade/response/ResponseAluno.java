package br.com.unasp.faculdade.response;

public record ResponseAluno(
        Integer id,
        String nome,
        String email,
        Integer cursoId,
        String cursoNome
) {
}
