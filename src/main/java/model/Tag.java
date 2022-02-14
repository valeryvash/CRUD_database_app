package model;

public class Tag implements Entity<Long>{

    private Long id;
    private String tagName;

    public Tag() {
        this.id = -1L;
        this.tagName = "";
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
