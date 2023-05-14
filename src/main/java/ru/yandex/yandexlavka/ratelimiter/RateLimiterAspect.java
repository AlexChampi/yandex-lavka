package ru.yandex.yandexlavka.ratelimiter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.exception.RequestLimitPerEndpointException;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
@Aspect
public class RateLimiterAspect {
    private final Map<Method, Bucket> methodBucketMap;

    public RateLimiterAspect() {
        this.methodBucketMap = new HashMap<>();
    }

    @Before("@annotation(RateLimiter)")
    public void rateLimiter(JoinPoint jp) {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();
        if (!methodBucketMap.containsKey(method)) {
            RateLimiter rateLimiter = signature.getMethod().getAnnotation(RateLimiter.class);
            Duration duration = Duration.ZERO;
            switch (rateLimiter.timeUnit()) {
                case SECONDS -> duration = Duration.ofSeconds(rateLimiter.timeLimit());
                case MINUTES -> duration = Duration.ofMinutes(rateLimiter.timeLimit());
            }
            Bandwidth limit = Bandwidth.classic(rateLimiter.tokenPerPeriod(), Refill.intervally(rateLimiter.tokenPerPeriod(), duration));
            Bucket bucket = Bucket.builder()
                    .addLimit(limit)
                    .build();
            methodBucketMap.put(method, bucket);

        }
        Bucket bucket = methodBucketMap.get(method);
        if (!bucket.tryConsume(1)) {
            throw new RequestLimitPerEndpointException();
        }
    }
}
