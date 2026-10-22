package br.com.unasp.faculdade.controller;

import br.com.unasp.faculdade.response.DebugRequestDTO;
import br.com.unasp.faculdade.service.DebugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/debug")
@CrossOrigin(origins = "*")
public class DebugController {

    @Autowired
    private DebugService debugService;

    @GetMapping("/requests")
    public List<DebugRequestDTO> getRequests() {
        return debugService.getAll();
    }

    @DeleteMapping("/requests")
    public void clearRequests() {
        debugService.clear();
    }
}
