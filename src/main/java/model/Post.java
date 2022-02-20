package model;

import java.util.ArrayList;
import java.util.List;

public class Post implements Entity<Long>{

    private Long id;
    private String postContent;
    private PostStatus postStatus;
    private List<Tag> postTags;
    private Writer writer;

    public Post() {
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public List<Tag> getPostTags() {
        return postTags;
    }

    public void setPostTags(List<Tag> postTags) {
        this.postTags = postTags;
    }

    public PostStatus getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(PostStatus postStatus) {
        this.postStatus = postStatus;
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }
}
