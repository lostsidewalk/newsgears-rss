package com.lostsidewalk.buffy.rss.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class WebHealthIndicator implements HealthIndicator {

    @Override
    public final Health health() {
        return new Health.Builder().up().build();
    }
}
