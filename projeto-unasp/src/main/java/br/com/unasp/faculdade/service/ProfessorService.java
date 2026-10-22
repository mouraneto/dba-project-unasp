package br.com.unasp.faculdade.service;

import br.com.unasp.faculdade.entity.Professor;
import br.com.unasp.faculdade.repository.ProfessorRepository;
import br.com.unasp.faculdade.request.RequestProfessor;
import br.com.unasp.faculdade.response.ResponseProfessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class ProfessorService {

    @Autowired
    private ProfessorRepository repository;

    public ResponseProfessor save(RequestProfessor request) {
        if (repository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado: " + request.email());
        }

        Professor professor = new Professor();
        professor.setNome(request.nome());
        professor.setEmail(request.email());
        professor.setDepartamento(request.departamento());

        Professor salvo = repository.save(professor);
        return toResponse(salvo);
    }

    public List<ResponseProfessor> findAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ResponseProfessor findById(Integer id) {
        Professor professor = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado com ID: " + id));
        return toResponse(professor);
    }

    public Professor getEntityById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado com ID: " + id));
    }

    private ResponseProfessor toResponse(Professor professor) {
        return new ResponseProfessor(
                professor.getId(),
                professor.getNome(),
                professor.getEmail(),
                professor.getDepartamento()
        );
    }
}
