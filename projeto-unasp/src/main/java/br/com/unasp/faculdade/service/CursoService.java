package br.com.unasp.faculdade.service;

import br.com.unasp.faculdade.entity.Curso;
import br.com.unasp.faculdade.entity.Professor;
import br.com.unasp.faculdade.repository.CursoRepository;
import br.com.unasp.faculdade.repository.ProfessorRepository;
import br.com.unasp.faculdade.request.RequestCurso;
import br.com.unasp.faculdade.response.ResponseCurso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class CursoService {

    @Autowired
    private CursoRepository repository;

    @Autowired
    private ProfessorRepository professorRepository;

    public ResponseCurso save(RequestCurso request) {
        Professor professor = professorRepository.findById(request.professorId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Professor não encontrado com ID: " + request.professorId()
                ));

        Curso curso = new Curso();
        curso.setNome(request.nome());
        curso.setProfessor(professor);

        Curso salvo = repository.save(curso);
        return toResponse(salvo);
    }

    public List<ResponseCurso> findAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ResponseCurso findById(Integer id) {
        Curso curso = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado com ID: " + id));
        return toResponse(curso);
    }

    public Curso getEntityById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado com ID: " + id));
    }

    private ResponseCurso toResponse(Curso curso) {
        return new ResponseCurso(
                curso.getId(),
                curso.getNome(),
                curso.getProfessor().getId(),
                curso.getProfessor().getNome()
        );
    }
}
