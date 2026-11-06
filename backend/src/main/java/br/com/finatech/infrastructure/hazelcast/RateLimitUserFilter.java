package br.com.finatech.infrastructure.hazelcast;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.grid.hazelcast.HazelcastProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitUserFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitUserFilter.class);

    private final HazelcastProxyManager<String> proxyManager;

    // 300 req/min para usuários autenticados; 30 req/min para anônimos (IPs)
    private static final int LIMITE_LOGADO  = 300;
    private static final int LIMITE_ANONIMO = 30;

    public RateLimitUserFilter(HazelcastProxyManager<String> proxyManager) {
        this.proxyManager = proxyManager;
    }

    private BucketConfiguration criarConfiguracaoDeBalde(long limiteMaximo) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(limiteMaximo)
                .refillIntervally(limiteMaximo, Duration.ofMinutes(1))
                .build();
        return BucketConfiguration.builder()
                .addLimit(limit)
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path != null && !path.contains(".") && !path.equals("/")) {
            String identificadorDoCliente;
            int limiteAtual;

            Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();

            if (autenticacao != null && autenticacao.isAuthenticated()
                    && !"anonymousUser".equals(autenticacao.getName())) {
                identificadorDoCliente = "USER:" + autenticacao.getName();
                limiteAtual = LIMITE_LOGADO;
            } else {
                identificadorDoCliente = "IP:" + request.getRemoteAddr();
                limiteAtual = LIMITE_ANONIMO;
            }

            var baldeDistribuido = proxyManager.builder()
                    .build(identificadorDoCliente, () -> criarConfiguracaoDeBalde(limiteAtual));

            if (!baldeDistribuido.tryConsume(1)) {
                log.warn("Rate limit atingido para: {} — limite: {} req/min", identificadorDoCliente, limiteAtual);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(
                    String.format("{\"status\":429,\"title\":\"Too Many Requests\",\"detail\":\"Limite de %d requisições por minuto atingido. Aguarde e tente novamente.\"}", limiteAtual));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
