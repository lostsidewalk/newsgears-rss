package com.lostsidewalk.buffy.rss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeedConfig {

    @Autowired
    FeedConfigProps feedConfigProps;

    @Bean
    RSSChannelBuilder rssChannelBuilder() {
        return new RSSChannelBuilder(feedConfigProps);
    }

    @Bean
    ATOMFeedBuilder atomFeedBuilder() {
        return new ATOMFeedBuilder(feedConfigProps);
    }
}
