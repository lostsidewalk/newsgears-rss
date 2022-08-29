package com.lostsidewalk.buffy;


import com.lostsidewalk.buffy.rss.ATOMFeedBuilder;
import com.lostsidewalk.buffy.rss.RSSChannelBuilder;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.rss.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class FeedController {

    @Autowired
    FeedDefinitionDao feedDefinitionDao;

    @Autowired
    StagingPostDao stagingPostDao;

    @Autowired
    RSSChannelBuilder rssChannelBuilder;

    @Autowired
    ATOMFeedBuilder atomFeedBuilder;

    /**
     * build an RSS feed from a NG feed definition given by the transport identifier
     *
     * @param transportIdent UUID (or customized string) uniquely identifying the end-point for a feed
     * @return
     */
    @GetMapping(path = "/rss/{transportIdent}")
    public Channel rss(@PathVariable String transportIdent) {

        FeedDefinition feedDefinition = this.feedDefinitionDao.findByTransportIdent(transportIdent);
        if (feedDefinition != null) {
            return this.rssChannelBuilder
                    .buildChannel(feedDefinition, stagingPostDao.findPublishedByFeed(feedDefinition.getIdent()));
        } else {
            log.debug("No RSS feed at end-point given by transportIdent={}", transportIdent);
        }

        return null;
    }

    @GetMapping(path = "/atom/{transportIdent}")
    public Feed atom(@PathVariable String transportIdent) {

        FeedDefinition feedDefinition = this.feedDefinitionDao.findByTransportIdent(transportIdent);
        if (feedDefinition != null) {
            return this.atomFeedBuilder
                    .buildFeed(feedDefinition, stagingPostDao.findPublishedByFeed(feedDefinition.getIdent()));
        } else {
            log.debug("No ATOM feed at end-point given by transportIdent={}", transportIdent);
        }

        return null;
    }
}
