package br.com.unasp.faculdade.controller;

import br.com.unasp.faculdade.request.RequestCurso;
import br.com.unasp.faculdade.response.ResponseCurso;
import br.com.unasp.faculdade.service.CursoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/curso")
@CrossOrigin(origins = "*")
public class CursoController {

    @Autowired
    private CursoService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseCurso cadastrarCurso(@Valid @RequestBody RequestCurso request) {
        return service.save(request);
    }

    @GetMapping
    public List<ResponseCurso> listarCursos() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseCurso buscarCursoPorId(@PathVariable Integer id) {
        return service.findById(id);
    }
}
