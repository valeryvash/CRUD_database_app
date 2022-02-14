package util;

import model.Entity;
import model.Post;
import model.Tag;
import model.Writer;

import java.util.List;


/**
 * Regulates print actions for the entities
 */
public class EntitiesPrinter {

    public void print(Entity entity) {
        if (entity instanceof Writer) {
            this.writerPrint((Writer) entity);
        } else if (entity instanceof Post) {
            this.postPrint((Post) entity);
        } else if (entity instanceof Tag) {
            this.tagPrint((Tag) entity);
        } else {
            throw new IllegalArgumentException("Unknown entity type");
        }
    }

    private void writerPrint(Writer writer) {
        System.out.printf(
                "Writer | id : %011d | name : %25.25s |\n",
                writer.getId(),
                writer.getWriterName());
        List<Post> writerPosts = writer.getWriterPosts();
        if (!writerPosts.isEmpty()) {
            writerPosts.forEach(this::postPrint);
        } else {
            System.out.println("Writer has no posts");
        }
    }

    private void postPrint(Post post) {
        System.out.printf(
                "Post | id : %011d |\n" +
                "content : \n" +
                "%20s \n" +
                "PostStatus : %10.10s\n",
                post.getId(),
                post.getPostContent(),
                post.getPostStatus().toString());

        List<Tag> postTags = post.getPostTags();
        if (postTags.isEmpty()){
            System.out.println("Post has no tags");
        } else {
            postTags.forEach(this::tagPrint);
        }
        System.out.println();
    }

    private void tagPrint(Tag tag) {
        System.out.printf(
                "Tag | id : %011d | name : %25.25s |\n",
                tag.getId(),
                tag.getTagName());
    }

}
