package com.lostsidewalk.buffy.rss;

import com.lostsidewalk.buffy.FeedDefinition;
import com.lostsidewalk.buffy.StagingPost;
import com.rometools.rome.feed.rss.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class RSSChannelBuilder {

    private final FeedConfigProps configProps;

    public RSSChannelBuilder(FeedConfigProps configProps) {
        this.configProps = configProps;
    }

    public Channel buildChannel(FeedDefinition feedDefinition, List<StagingPost> stagingPosts) {

        Channel channel = new Channel();

        channel.setFeedType(this.configProps.getRssFeedType());

        channel.setTitle(getTitle(feedDefinition));

        channel.setDescription(getDescription(feedDefinition));

        channel.setLink(getLink(feedDefinition));

        channel.setUri(getChannelUri(feedDefinition));

        channel.setGenerator(getGenerator(feedDefinition));

        channel.setImage(getChannelImage(feedDefinition));

        channel.setPubDate(getPubDate());

        channel.setItems(getItems(stagingPosts));

        return channel;
    }

    private static String getTitle(FeedDefinition feedDefinition) {
        return feedDefinition.getTitle();
    }

    private static String getDescription(FeedDefinition feedDefinition) {
        return feedDefinition.getDescription();
    }

    private String getLink(FeedDefinition feedDefinition) {
        return String.format(configProps.getChannelLinkTemplate(), feedDefinition.getId());
    }

    private String getChannelUri(FeedDefinition feedDefinition) {
        return String.format(configProps.getChannelUriTemplate(), feedDefinition.getId());
    }

    private String getGenerator(FeedDefinition feedDefinition) {
        return feedDefinition.getGenerator();
    }

    private Image getChannelImage(FeedDefinition feedDefinition) {
        Image image = new Image();
        image.setUrl(getImageUrl(feedDefinition));
        image.setTitle(getImageTitle(feedDefinition));
        image.setHeight(configProps.getChannelImageHeight());
        image.setWidth(configProps.getChannelImageWidth());

        return image;
    }

    private String getImageUrl(FeedDefinition feedDefinition) {
        return String.format(configProps.getChannelImageUrlTemplate(), feedDefinition.getId());
    }

    private static String getImageTitle(FeedDefinition feedDefinition) {
        return feedDefinition.getTitle();
    }

    private static Date getPubDate() {
        return new Date();
    }

    private static List<Item> getItems(List<StagingPost> stagingPosts) {
        return stagingPosts.stream()
                .map(RSSChannelBuilder::toItem)
                .collect(toList());
    }

    private static Item toItem(StagingPost stagingPost) {

        Item item = new Item();

        setAuthor(stagingPost, item);

        setLink(stagingPost, item);

        setTitle(stagingPost, item);

        setUri(stagingPost, item);

        setComments(stagingPost, item);

        setCategory(stagingPost, item);

        setDescription(stagingPost, item);

        setPubDate(stagingPost, item);

        return item;
    }

    private static void setPubDate(@SuppressWarnings("unused") StagingPost stagingPost, Item item) {
        item.setPubDate(null); // TODO: see if we can get this from staging post
    }

    private static void setAuthor(@SuppressWarnings("unused") StagingPost stagingPost, Item item) {
        item.setAuthor(null); // TODO: see if we can get this from staging post
    }

    private static void setLink(StagingPost stagingPost, Item item) {
        item.setLink(stagingPost.getPostUrl());
    }

    private static void setTitle(StagingPost stagingPost, Item item) {
        item.setTitle(stagingPost.getPostTitle());
    }

    private static void setUri(StagingPost stagingPost, Item item) {
        item.setUri(stagingPost.getPostUrl());
    }

    private static void setComments(@SuppressWarnings("unused") StagingPost stagingPost, Item item) {
        item.setComments(null);
    }

    private static void setCategory(@SuppressWarnings("unused") StagingPost stagingPost, Item item) {
        Category category = new Category();
        category.setValue(null); // TODO: see if we can get this from staging post
        item.setCategories(Collections.singletonList(category));
    }

    private static void setDescription(StagingPost stagingPost, Item item) {
        Description descr = new Description();

        descr.setValue(stagingPost.getPostDesc());

        item.setDescription(descr);
    }
}
