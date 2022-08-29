package com.lostsidewalk.buffy.rss;

import com.lostsidewalk.buffy.FeedDefinition;
import com.lostsidewalk.buffy.StagingPost;
import com.rometools.rome.feed.atom.*;
import com.rometools.rome.feed.synd.SyndPerson;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ATOMFeedBuilder {

    private final FeedConfigProps configProps;

    public ATOMFeedBuilder(FeedConfigProps configProps) {
        this.configProps = configProps;
    }

    public Feed buildFeed(FeedDefinition feedDefinition, List<StagingPost> stagingPosts) {

        Feed feed = new Feed();

        feed.setFeedType(this.configProps.getAtomFeedType());

        feed.setTitle(getTitle(feedDefinition));

        feed.setId(getChannelUri(feedDefinition));

        feed.setSubtitle(getSubtitle(feedDefinition));

        feed.setUpdated(getPubDate());

        feed.setEntries(getEntries(stagingPosts));

        return feed;
    }

    private static String getTitle(FeedDefinition feedDefinition) {
        return feedDefinition.getTitle();
    }

    private static Content getSubtitle(FeedDefinition feedDefinition) {
        Content subtitle = new Content();
        subtitle.setType("text/plain");
        subtitle.setValue(feedDefinition.getDescription());
        return subtitle;
    }

    private String getChannelUri(FeedDefinition feedDefinition) {
        return String.format(configProps.getChannelUriTemplate(), feedDefinition.getId());
    }

    private static Date getPubDate() {
        return new Date();
    }

    private static List<Entry> getEntries(List<StagingPost> stagingPosts) {
        return stagingPosts.stream()
                .map(ATOMFeedBuilder::toEntry)
                .collect(toList());
    }

    private static Entry toEntry(StagingPost stagingPost) {

        Entry entry = new Entry();

        setAlternateLinks(stagingPost, entry);

        setAuthors(stagingPost, entry);

        setCreated(stagingPost, entry);

        setPublished(stagingPost, entry);

        setUpdated(stagingPost, entry);

        setId(stagingPost, entry);

        setTitle(stagingPost, entry);

        setCategories(stagingPost, entry);

        setSummary(stagingPost, entry);

        return entry;
    }

    private static void setAlternateLinks(StagingPost stagingPost, Entry entry) {
        Link link = new Link();
        link.setHref(stagingPost.getPostUrl());
        entry.setAlternateLinks(Collections.singletonList(link));
    }

    private static void setAuthors(StagingPost stagingPost, Entry entry) {
        SyndPerson author = new Person();
        author.setName(null); // TODO: see if we can get this from staging post
    }

    private static void setCreated(StagingPost stagingPost, Entry entry) {
        entry.setCreated(getPubDate()); // TODO: see if we can get this from staging post
    }

    private static void setPublished(StagingPost stagingPost, Entry entry) {
        entry.setPublished(getPubDate()); // TODO: see if we can get this from staging post
    }

    private static void setUpdated(StagingPost stagingPost, Entry entry) {
        entry.setUpdated(stagingPost.getImportTimestamp());
    }

    private static void setId(StagingPost stagingPost, Entry entry) {
        entry.setId(stagingPost.getPostDesc());
    }

    private static void setTitle(StagingPost stagingPost, Entry entry) {
        entry.setTitle(stagingPost.getPostTitle());
    }

    private static void setCategories(StagingPost stagingPost, Entry entry) {
        Category category = new Category();
        category.setTerm(null); // TODO: see if we can get this from staging post
        entry.setCategories(Collections.singletonList(category));
    }

    private static void setSummary(StagingPost stagingPost, Entry entry) {
        Content summary = new Content();
        summary.setType("text/plain");
        summary.setValue(stagingPost.getPostDesc());
        entry.setSummary(summary);
    }
}
