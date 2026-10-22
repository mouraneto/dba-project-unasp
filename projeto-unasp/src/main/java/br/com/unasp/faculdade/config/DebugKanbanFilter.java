package br.com.unasp.faculdade.config;

import br.com.unasp.faculdade.response.DebugRequestDTO;
import br.com.unasp.faculdade.service.DebugService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DebugKanbanFilter extends OncePerRequestFilter {

    @Autowired
    private DebugService debugService;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Nao logar chamadas internas de debug, h2-console ou assets estaticos para nao poluir o kanban
        if (path.startsWith("/api/debug") || path.startsWith("/h2-console") || path.endsWith(".js") || path.endsWith(".css") || path.endsWith(".html") || path.endsWith(".ico")) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = responseWrapper.getStatus();
            String statusText = resolveStatusText(status);

            String reqBody = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8).trim();
            String resBody = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8).trim();

            String column;
            if (status >= 200 && status < 300) {
                column = "success";
            } else if (status >= 400 && status < 500) {
                column = "client_error";
            } else {
                column = "server_error";
            }

            DebugRequestDTO dto = new DebugRequestDTO(
                    UUID.randomUUID().toString(),
                    LocalTime.now().format(TIME_FORMATTER),
                    request.getMethod(),
                    path,
                    status,
                    statusText,
                    duration,
                    reqBody.isEmpty() ? null : reqBody,
                    resBody.isEmpty() ? null : resBody,
                    column
            );

            debugService.add(dto);

            // Importante: descarregar o conteudo de volta na resposta
            responseWrapper.copyBodyToResponse();
        }
    }

    private String resolveStatusText(int status) {
        try {
            return HttpStatus.valueOf(status).getReasonPhrase();
        } catch (Exception e) {
            return "Status " + status;
        }
    }
}
