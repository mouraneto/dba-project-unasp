package br.com.unasp.faculdade.controller;

import br.com.unasp.faculdade.request.RequestAluno;
import br.com.unasp.faculdade.response.ResponseAluno;
import br.com.unasp.faculdade.service.AlunoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aluno")
@CrossOrigin(origins = "*")
public class AlunoController {

    @Autowired
    private AlunoService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseAluno cadastrarAluno(@Valid @RequestBody RequestAluno request) {
        return service.save(request);
    }

    @GetMapping
    public List<ResponseAluno> listarAlunos() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseAluno buscarAlunoPorId(@PathVariable Integer id) {
        return service.findById(id);
    }
}
