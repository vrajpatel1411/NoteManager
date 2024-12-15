package org.vrajpatel.notemanager.config;

import jakarta.servlet.*;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class RateLimitingFilter implements Filter {
    private static final int MAX_REQUESTS_PER_IP = 5;
    private static final long ONE_MINUTE_IN_MILLIS = 60 * 1000;
    private static final long RESET_INTERVAL_IN_MINUTES = 1; // Interval after which to reset request counts
    private final Map<String, Queue<Long>> requestTimestampsPerIpAddress = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final long THROTTLE_DELAY_IN_MILLIS = 500;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        scheduler.scheduleAtFixedRate(this::resetRequestCounts, RESET_INTERVAL_IN_MINUTES, RESET_INTERVAL_IN_MINUTES, TimeUnit.MINUTES);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        String ipAddress = httpServletRequest.getRemoteAddr();
        long currentTimeMillis = System.currentTimeMillis();
        requestTimestampsPerIpAddress.putIfAbsent(ipAddress, new ConcurrentLinkedQueue<>());
        Queue<Long> timestamps = requestTimestampsPerIpAddress.get(ipAddress);
        while (!timestamps.isEmpty() && currentTimeMillis - timestamps.peek() > ONE_MINUTE_IN_MILLIS) {
            timestamps.poll();
        }
        timestamps.offer(currentTimeMillis);
        if (timestamps.size() > MAX_REQUESTS_PER_IP) {

            try {
                TimeUnit.MILLISECONDS.sleep(THROTTLE_DELAY_IN_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        // Clean up the scheduler when the filter is destroyed
        scheduler.shutdown();
    }

    // Periodic task to reset request counts for all IPs
    private void resetRequestCounts() {
        requestTimestampsPerIpAddress.clear();
    }





}
