package br.com.unasp.faculdade.service;

import br.com.unasp.faculdade.response.DebugRequestDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class DebugService {

    private final ConcurrentLinkedDeque<DebugRequestDTO> requests = new ConcurrentLinkedDeque<>();
    private static final int MAX_REQUESTS = 100;

    public void add(DebugRequestDTO request) {
        requests.addFirst(request);
        while (requests.size() > MAX_REQUESTS) {
            requests.removeLast();
        }
    }

    public List<DebugRequestDTO> getAll() {
        return new ArrayList<>(requests);
    }

    public void clear() {
        requests.clear();
    }
}
