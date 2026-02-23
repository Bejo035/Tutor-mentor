package ge.batumi.tutormentor.security.filter;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * In-memory IP-based rate limiter for authentication endpoints.
 * Limits to 10 requests per minute per IP on /api/v1/auth/login and /api/v1/auth/refresh.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 10;
    private static final long WINDOW_MS = 60_000;

    private final Map<String, Deque<Long>> requestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();

        if (!path.equals("/api/v1/auth/login") && !path.equals("/api/v1/auth/refresh")) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);
        long now = System.currentTimeMillis();

        Deque<Long> timestamps = requestCounts.computeIfAbsent(clientIp, k -> new ConcurrentLinkedDeque<>());

        // Remove entries outside the time window
        Iterator<Long> iterator = timestamps.iterator();
        while (iterator.hasNext()) {
            if (now - iterator.next() > WINDOW_MS) {
                iterator.remove();
            } else {
                break;
            }
        }

        if (timestamps.size() >= MAX_REQUESTS) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Too many requests. Please try again later.\"}");
            return;
        }

        timestamps.addLast(now);
        filterChain.doFilter(request, response);

        // Periodic cleanup of stale entries
        if (requestCounts.size() > 1000) {
            requestCounts.entrySet().removeIf(entry -> {
                Deque<Long> deque = entry.getValue();
                return deque.isEmpty() || now - deque.peekLast() > WINDOW_MS;
            });
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
