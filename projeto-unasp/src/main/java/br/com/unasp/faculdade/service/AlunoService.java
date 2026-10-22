package br.com.unasp.faculdade.service;

import br.com.unasp.faculdade.entity.Aluno;
import br.com.unasp.faculdade.entity.Curso;
import br.com.unasp.faculdade.repository.AlunoRepository;
import br.com.unasp.faculdade.repository.CursoRepository;
import br.com.unasp.faculdade.request.RequestAluno;
import br.com.unasp.faculdade.response.ResponseAluno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class AlunoService {

    @Autowired
    private AlunoRepository repository;

    @Autowired
    private CursoRepository cursoRepository;

    public ResponseAluno save(RequestAluno request) {
        if (repository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado para outro aluno: " + request.email());
        }

        Curso curso = cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Curso não encontrado com ID: " + request.cursoId()
                ));

        Aluno aluno = new Aluno();
        aluno.setNome(request.nome());
        aluno.setEmail(request.email());
        aluno.setCurso(curso);

        Aluno salvo = repository.save(aluno);
        return toResponse(salvo);
    }

    public List<ResponseAluno> findAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ResponseAluno findById(Integer id) {
        Aluno aluno = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado com ID: " + id));
        return toResponse(aluno);
    }

    private ResponseAluno toResponse(Aluno aluno) {
        return new ResponseAluno(
                aluno.getId(),
                aluno.getNome(),
                aluno.getEmail(),
                aluno.getCurso().getId(),
                aluno.getCurso().getNome()
        );
    }
}
