package br.com.unasp.faculdade.controller;

import br.com.unasp.faculdade.request.RequestProfessor;
import br.com.unasp.faculdade.response.ResponseProfessor;
import br.com.unasp.faculdade.service.ProfessorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/professor")
@CrossOrigin(origins = "*")
public class ProfessorController {

    @Autowired
    private ProfessorService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseProfessor cadastrarProfessor(@Valid @RequestBody RequestProfessor request) {
        return service.save(request);
    }

    @GetMapping
    public List<ResponseProfessor> listarProfessores() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseProfessor buscarProfessorPorId(@PathVariable Integer id) {
        return service.findById(id);
    }
}
