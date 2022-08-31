package com.lostsidewalk.buffy;


`import com.lostsidewalk.buffy.model.RenderedATOMFeed;
import com.lostsidewalk.buffy.model.RenderedRSSFeed;
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
    RenderedFeedDao renderedFeedDao;

    /**
     * build an RSS feed from a NG feed definition given by the transport identifier
     *
     * @param transportIdent UUID (or customized string) uniquely identifying the end-point for a feed
     * @return
     */
    @GetMapping(path = "/rss/{transportIdent}")
    public Channel rss(@PathVariable String transportIdent) {
        RenderedRSSFeed r = renderedFeedDao.findRSSChannelByTransportIdent(transportIdent);
        if (r == null) {
            log.debug("No RSS feed with transportIdent={}", transportIdent);
            return null;
        }

        return r.getChannel();
    }

    @GetMapping(path = "/atom/{transportIdent}")
    public Feed atom(@PathVariable String transportIdent) {
        RenderedATOMFeed r = renderedFeedDao.findATOMFeedByTransportIdent(transportIdent);
        if (r == null) {
            log.debug("No ATOM feed with transportIdent={}", transportIdent);
            return null;
        }

        return r.getFeed();
    }
}
