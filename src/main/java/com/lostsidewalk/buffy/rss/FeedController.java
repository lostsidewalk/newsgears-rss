package com.lostsidewalk.buffy.rss;


import com.google.gson.Gson;
import com.lostsidewalk.buffy.DataAccessException;
import com.lostsidewalk.buffy.feed.FeedCredentialsDao;
import com.lostsidewalk.buffy.feed.FeedDefinitionDao;
import com.lostsidewalk.buffy.model.RenderedATOMFeed;
import com.lostsidewalk.buffy.model.RenderedFeedDao;
import com.lostsidewalk.buffy.model.RenderedRSSFeed;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.tomcat.util.codec.binary.Base64.decodeBase64;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
public class FeedController {

    private static final Gson GSON = new Gson();

    @Autowired
    RenderedFeedDao renderedFeedDao;

    @Autowired
    FeedDefinitionDao feedDefinitionDao;

    @Autowired
    FeedCredentialsDao feedCredentialsDao;

    /**
     * Fetches the RSS feed associated with the transport identifier, or null if DNE
     *
     * @param transportIdent UUID (or customized string) uniquely identifying the end-point for a feed
     */
    @GetMapping(path = "/feed/rss/{transportIdent}")
    public ResponseEntity<?> rss(@PathVariable String transportIdent, HttpServletRequest httpServletRequest) throws DataAccessException {
        RenderedRSSFeed r = renderedFeedDao.findRSSChannelByTransportIdent(transportIdent);
        if (r == null) {
            log.debug("No RSS feed at transportIdent={}", transportIdent);
            return notFound().build();
        } else if (requiresAuthentication(transportIdent) && !isAuthenticated(transportIdent, httpServletRequest)) {
            log.info("Authentication failed for RSS feed at transportIdent={}", transportIdent);
            return ResponseEntity.of(ProblemDetail.forStatus(403)).build();
        }

        return ok(r.getChannel());
    }

    /**
     * Fetches the ATOM feed associated with the transport identifier, or null if DNE
     *
     * @param transportIdent UUID (or customized string) uniquely identifying the end-point for a feed
     */
    @GetMapping(path = "/feed/atom/{transportIdent}")
    public ResponseEntity<?> atom(@PathVariable String transportIdent, HttpServletRequest httpServletRequest) throws DataAccessException {
        RenderedATOMFeed r = renderedFeedDao.findATOMFeedByTransportIdent(transportIdent);
        if (r == null) {
            log.debug("No ATOM feed at transportIdent={}", transportIdent);
            return notFound().build();
        } else if (requiresAuthentication(transportIdent) && !isAuthenticated(transportIdent, httpServletRequest)) {
            log.info("Authentication failed for ATOM feed at transportIdent={}", transportIdent);
            return ResponseEntity.of(ProblemDetail.forStatus(403)).build();
        }

        return ok(r.getFeed());
    }

    /**
     * Fetches the JSON feed associated with the transport identifier, or null if DNE
     *
     * @param transportIdent UUID (or customized string) uniquely identifying the end-point for a feed
     */
    @GetMapping(path = "/feed/json/{transportIdent}")
    public ResponseEntity<?> json(@PathVariable String transportIdent, HttpServletRequest httpServletRequest) throws DataAccessException {
        Serializable s = renderedFeedDao.findJSONFeedByTransportIdent(transportIdent);
        if (s == null) {
            log.debug("No JSON feed at transportIdent={}", transportIdent);
            return notFound().build();
        } else if (requiresAuthentication(transportIdent) && !isAuthenticated(transportIdent, httpServletRequest)) {
            log.info("Authentication failed for JSON feed at transportIdent={}", transportIdent);
            return ResponseEntity.of(ProblemDetail.forStatus(403)).build();
        }

        return ok(GSON.fromJson(s.toString(), Object.class));
    }

    private boolean requiresAuthentication(String transportIdent) throws DataAccessException {
        return isTrue(feedDefinitionDao.requiresAuthentication(transportIdent));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isAuthenticated(String transportIdent, HttpServletRequest httpServletRequest) throws DataAccessException {
        boolean isAuthenticated = false;
        final String authorization = httpServletRequest.getHeader("Authorization");
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            String base64Credentials = authorization.substring("basic".length()).trim();
            if (isNotBlank(base64Credentials)) {
                String[] values = new String(decodeBase64(base64Credentials), UTF_8).split(":", 2);
                if (values.length == 2) {
                    String remoteUsername = values[0];
                    String remotePassword = values[1];
                    if (isNoneBlank(remoteUsername, remotePassword)) {
                        String feedPassword = feedCredentialsDao.findByTransportIdent(remoteUsername, transportIdent);
                        isAuthenticated = StringUtils.equals(remotePassword, feedPassword);
                    }
                }
            }
        }

        return isAuthenticated;
    }
}
