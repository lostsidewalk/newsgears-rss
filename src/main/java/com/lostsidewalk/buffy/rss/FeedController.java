package com.lostsidewalk.buffy.rss;


import com.google.gson.Gson;
import com.lostsidewalk.buffy.DataAccessException;
import com.lostsidewalk.buffy.model.RenderedATOMFeed;
import com.lostsidewalk.buffy.model.RenderedFeedDao;
import com.lostsidewalk.buffy.model.RenderedRSSFeed;
import com.lostsidewalk.buffy.queue.QueueCredential;
import com.lostsidewalk.buffy.queue.QueueCredentialDao;
import com.lostsidewalk.buffy.queue.QueueDefinitionDao;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.ENGLISH;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.tomcat.util.codec.binary.Base64.decodeBase64;
import static org.springframework.http.ProblemDetail.forStatus;
import static org.springframework.http.ResponseEntity.*;

@SuppressWarnings("DesignForExtension")
@Slf4j
@RestController
public class FeedController {

    private static final Gson GSON = new Gson();

    @Autowired
    RenderedFeedDao renderedFeedDao;

    @Autowired
    QueueDefinitionDao queueDefinitionDao;

    @Autowired
    QueueCredentialDao queueCredentialDao;

    /**
     * Fetches the RSS feed associated with the transport identifier, or null if DNE
     *
     * @param transportIdent UUID (or customized string) uniquely identifying the end-point for a feed
     */
    @GetMapping(path = "/feed/rss/{transportIdent}")
    public ResponseEntity<?> rssByTransportIdent(@PathVariable String transportIdent, HttpServletRequest httpServletRequest) throws DataAccessException {
        RenderedRSSFeed r = renderedFeedDao.findRSSChannelByTransportIdent(transportIdent);
        if (r == null) {
            log.debug("No RSS feed at transportIdent={}", transportIdent);
            return notFound().build();
        } else if (requiresAuthentication(transportIdent) && !isAuthenticated(queueDefinitionDao.findByTransportIdent(transportIdent).getId(), httpServletRequest)) {
            log.info("Authentication failed for RSS feed at transportIdent={}", transportIdent);
            return of(forStatus(403)).build();
        }

        return ok(r.getChannel());
    }

    @GetMapping(path = "/feed/rss/{username}/{queueIdent}")
    public ResponseEntity<?> rssByQueueIdent(@PathVariable String username, @PathVariable String queueIdent, HttpServletRequest httpServletRequest) throws DataAccessException {
        String transportIdent = queueDefinitionDao.resolveTransportIdent(username, queueIdent);
        return rssByTransportIdent(transportIdent, httpServletRequest);
    }

    /**
     * Fetches the ATOM feed associated with the transport identifier, or null if DNE
     *
     * @param transportIdent UUID (or customized string) uniquely identifying the end-point for a feed
     */
    @GetMapping(path = "/feed/atom/{transportIdent}")
    public ResponseEntity<?> atomByTransportIdent(@PathVariable String transportIdent, HttpServletRequest httpServletRequest) throws DataAccessException {
        RenderedATOMFeed r = renderedFeedDao.findATOMFeedByTransportIdent(transportIdent);
        if (r == null) {
            log.debug("No ATOM feed at transportIdent={}", transportIdent);
            return notFound().build();
        } else if (requiresAuthentication(transportIdent) && !isAuthenticated(queueDefinitionDao.findByTransportIdent(transportIdent).getId(), httpServletRequest)) {
            log.info("Authentication failed for ATOM feed at transportIdent={}", transportIdent);
            return of(forStatus(403)).build();
        }

        return ok(r.getFeed());
    }

    @GetMapping(path = "/feed/atom/{username}/{queueIdent}")
    public ResponseEntity<?> atomByQueueIdent(@PathVariable String username, @PathVariable String queueIdent, HttpServletRequest httpServletRequest) throws DataAccessException {
        String transportIdent = queueDefinitionDao.resolveTransportIdent(username, queueIdent);
        return atomByTransportIdent(transportIdent, httpServletRequest);
    }

    /**
     * Fetches the JSON feed associated with the transport identifier, or null if DNE
     *
     * @param transportIdent UUID (or customized string) uniquely identifying the end-point for a feed
     */
    @GetMapping(path = "/feed/json/{transportIdent}")
    public ResponseEntity<?> jsonByTransportIdent(@PathVariable String transportIdent, HttpServletRequest httpServletRequest) throws DataAccessException {
        Serializable serializable = renderedFeedDao.findJSONFeedByTransportIdent(transportIdent);
        if (serializable == null) {
            log.debug("No JSON feed at transportIdent={}", transportIdent);
            return notFound().build();
        } else if (requiresAuthentication(transportIdent) && !isAuthenticated(queueDefinitionDao.findByTransportIdent(transportIdent).getId(), httpServletRequest)) {
            log.info("Authentication failed for JSON feed at transportIdent={}", transportIdent);
            return of(forStatus(403)).build();
        }

        return ok(GSON.fromJson(serializable.toString(), Object.class));
    }

    @GetMapping(path = "/feed/json/{username}/{queueIdent}")
    public ResponseEntity<?> jsonByQueueIdent(@PathVariable String username, @PathVariable String queueIdent, HttpServletRequest httpServletRequest) throws DataAccessException {
        String transportIdent = queueDefinitionDao.resolveTransportIdent(username, queueIdent);
        return jsonByTransportIdent(transportIdent, httpServletRequest);
    }

    private boolean requiresAuthentication(String transportIdent) throws DataAccessException {
        return isTrue(queueDefinitionDao.requiresAuthentication(transportIdent));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isAuthenticated(Long queueId, HttpServletRequest httpServletRequest) throws DataAccessException {
        boolean isAuthenticated = false;
        String authorization = httpServletRequest.getHeader("Authorization");
        if (authorization != null && authorization.toLowerCase(ENGLISH).startsWith("basic")) {
            String base64Credentials = authorization.substring("basic".length()).trim();
            if (isNotBlank(base64Credentials)) {
                String[] values = new String(decodeBase64(base64Credentials), UTF_8).split(":", 2);
                if (values.length == 2) {
                    String remoteUsername = values[0];
                    String remotePassword = values[1];
                    if (isNoneBlank(remoteUsername, remotePassword)) {
                        QueueCredential userCredential = queueCredentialDao.findByRemoteUsername(queueId, remoteUsername);
                        if (userCredential != null) {
                            isAuthenticated = userCredential.getBasicPassword().equals(remotePassword);
                        }
                    }
                }
            }
        }

        return isAuthenticated;
    }

    @Override
    public String toString() {
        return "FeedController{" +
                "renderedFeedDao=" + renderedFeedDao +
                ", queueDefinitionDao=" + queueDefinitionDao +
                ", queueCredentialDao=" + queueCredentialDao +
                '}';
    }
}
