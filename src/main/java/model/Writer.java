package model;

import java.util.ArrayList;
import java.util.List;

public class Writer implements Entity<Long>{

    private Long id;
    private String writerName;
    private List<Post> writerPosts;

    public Writer() {
        this.id = -1L;
        this.writerName = "";
        this.writerPosts = new ArrayList<>();
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWriterName() {
        return writerName;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }

    public List<Post> getWriterPosts() {
        return writerPosts;
    }

    public void setWriterPosts(List<Post> writerPosts) {
        this.writerPosts = writerPosts;
    }
}
