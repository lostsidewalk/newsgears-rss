package com.lostsidewalk.buffy.rss;


import com.google.gson.Gson;
import com.lostsidewalk.buffy.DataAccessException;
import com.lostsidewalk.buffy.RenderedFeedDao;
import com.lostsidewalk.buffy.model.RenderedATOMFeed;
import com.lostsidewalk.buffy.model.RenderedRSSFeed;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.rss.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

@Slf4j
@RestController
public class FeedController {

    private static final Gson GSON = new Gson();

    @Autowired
    RenderedFeedDao renderedFeedDao;

    /**
     * Fetches the RSS feed associated with the transport identifier, or null if DNE
     *
     * @param transportIdent UUID (or customized string) uniquely identifying the end-point for a feed
     */
    @GetMapping(path = "/feed/rss/{transportIdent}")
    public Channel rss(@PathVariable String transportIdent) throws DataAccessException {
        RenderedRSSFeed r = renderedFeedDao.findRSSChannelByTransportIdent(transportIdent);
        if (r == null) {
            log.debug("No RSS feed at transportIdent={}", transportIdent);
            return null;
        }

        return r.getChannel();
    }

    /**
     * Fetches the ATOM feed associated with the transport identifier, or null if DNE
     *
     * @param transportIdent UUID (or customized string) uniquely identifying the end-point for a feed
     */
    @GetMapping(path = "/feed/atom/{transportIdent}")
    public Feed atom(@PathVariable String transportIdent) throws DataAccessException {
        RenderedATOMFeed r = renderedFeedDao.findATOMFeedByTransportIdent(transportIdent);
        if (r == null) {
            log.debug("No ATOM feed at transportIdent={}", transportIdent);
            return null;
        }

        return r.getFeed();
    }

    /**
     * Fetches the JSON feed associated with the transport identifier, or null if DNE
     *
     * @param transportIdent UUID (or customized string) uniquely identifying the end-point for a feed
     */
    @GetMapping(path = "/feed/json/{transportIdent}")
    public Object json(@PathVariable String transportIdent) throws DataAccessException {
        Serializable s = renderedFeedDao.findJSONFeedByTransportIdent(transportIdent);
        if (s == null) {
            log.debug("No JSON feed at transportIdent={}", transportIdent);
            return null;
        }

        return GSON.fromJson(s.toString(), Object.class);
    }
}
