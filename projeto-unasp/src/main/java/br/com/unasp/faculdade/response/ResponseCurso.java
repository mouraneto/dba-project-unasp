package br.com.unasp.faculdade.response;

public record ResponseCurso(
        Integer id,
        String nome,
        Integer professorId,
        String professorNome
) {
}
