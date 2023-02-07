package com.lostsidewalk.buffy.rss;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Slf4j
@Configuration
public class FeedConfig {

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> feedEtagFilter() {
        FilterRegistrationBean<ShallowEtagHeaderFilter> filterRegistrationBean
                = new FilterRegistrationBean<>( new ShallowEtagHeaderFilter());
        filterRegistrationBean.addUrlPatterns("/feed/rss/*", "/feed/atom/*", "/feed/json/*");
        filterRegistrationBean.setName("feedEtagFilter");
        return filterRegistrationBean;
    }
}
