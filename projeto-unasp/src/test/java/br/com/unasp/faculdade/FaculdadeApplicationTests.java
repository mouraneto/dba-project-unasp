package br.com.unasp.faculdade;

import br.com.unasp.faculdade.request.RequestAluno;
import br.com.unasp.faculdade.request.RequestCurso;
import br.com.unasp.faculdade.request.RequestProfessor;
import br.com.unasp.faculdade.response.ResponseAluno;
import br.com.unasp.faculdade.response.ResponseCurso;
import br.com.unasp.faculdade.response.ResponseProfessor;
import br.com.unasp.faculdade.service.AlunoService;
import br.com.unasp.faculdade.service.CursoService;
import br.com.unasp.faculdade.service.ProfessorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FaculdadeApplicationTests {

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private CursoService cursoService;

    @Autowired
    private AlunoService alunoService;

    @Test
    void contextLoads() {
    }

    @Test
    @DisplayName("Deve cadastrar Professor, Curso e Aluno com sucesso no fluxo relacional completo")
    void testFluxoCompletoRelacional() {
        // 1. Cadastrar Professor
        RequestProfessor reqProf = new RequestProfessor(
                "Carlos Silva",
                "carlos.silva@unasp.br",
                "Ciencia da Computacao"
        );
        ResponseProfessor profSalvo = professorService.save(reqProf);
        assertNotNull(profSalvo.id());
        assertEquals("Carlos Silva", profSalvo.nome());
        assertEquals("carlos.silva@unasp.br", profSalvo.email());

        // 2. Cadastrar Curso vinculado ao Professor
        RequestCurso reqCurso = new RequestCurso(
                "Engenharia de Software",
                profSalvo.id()
        );
        ResponseCurso cursoSalvo = cursoService.save(reqCurso);
        assertNotNull(cursoSalvo.id());
        assertEquals("Engenharia de Software", cursoSalvo.nome());
        assertEquals(profSalvo.id(), cursoSalvo.professorId());
        assertEquals("Carlos Silva", cursoSalvo.professorNome());

        // 3. Cadastrar Aluno vinculado ao Curso
        RequestAluno reqAluno = new RequestAluno(
                "Lucas Neto",
                "lucas.neto@aluno.unasp.br",
                cursoSalvo.id()
        );
        ResponseAluno alunoSalvo = alunoService.save(reqAluno);
        assertNotNull(alunoSalvo.id());
        assertEquals("Lucas Neto", alunoSalvo.nome());
        assertEquals(cursoSalvo.id(), alunoSalvo.cursoId());
        assertEquals("Engenharia de Software", alunoSalvo.cursoNome());

        // 4. Listar e validar integridade
        List<ResponseAluno> alunos = alunoService.findAll();
        assertFalse(alunos.isEmpty());
        assertTrue(alunos.stream().anyMatch(a -> a.nome().equals("Lucas Neto")));

        List<ResponseCurso> cursos = cursoService.findAll();
        assertFalse(cursos.isEmpty());

        List<ResponseProfessor> professores = professorService.findAll();
        assertFalse(professores.isEmpty());
    }

    @Test
    @DisplayName("Deve falhar ao tentar cadastrar Curso com professorId inexistente")
    void testCursoComProfessorInexistente() {
        RequestCurso reqCurso = new RequestCurso("Curso Fantasma", 9999);
        assertThrows(ResponseStatusException.class, () -> cursoService.save(reqCurso));
    }

    @Test
    @DisplayName("Deve falhar ao tentar cadastrar Aluno com cursoId inexistente")
    void testAlunoComCursoInexistente() {
        RequestAluno reqAluno = new RequestAluno("Aluno Sem Curso", "fantasma@unasp.br", 9999);
        assertThrows(ResponseStatusException.class, () -> alunoService.save(reqAluno));
    }
}
