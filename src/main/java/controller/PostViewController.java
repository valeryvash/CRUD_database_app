package controller;

import model.Post;
import model.PostStatus;
import model.Tag;
import model.Writer;
import repository.JdbcPostRepositoryImpl;
import repository.JdbcTagRepositoryImpl;
import service.PostService;
import service.TagService;
import util.EntitiesPrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PostViewController {

    private final PostService ps = new PostService(new JdbcPostRepositoryImpl());
    private final TagService ts = new TagService(new JdbcTagRepositoryImpl());

    private final Scanner sc = new Scanner(System.in);
    private final EntitiesPrinter ep = new EntitiesPrinter();

    private final WriterViewController wvc = new WriterViewController();

    public void postCreate() {
        Writer w = wvc.getWriter();
        long writerId = w.getId();
        Post p = new Post();
        System.out.println("Input new post contain \n 'q' for quit");

        String s = sc.nextLine();
        if (s.equalsIgnoreCase("q")) System.exit(0);
        p.setPostContent(s);
        p.setFkWriterId(writerId);
        p.setPostTags(getTagsList());
        ps.add(p);
        System.out.println("Post created");
        ep.print(w);
        ep.print(p);
    }

    private List<Tag> getTagsList() {
        List<Tag> tagsToBeStreamed = new ArrayList<>();
        System.out.println("Input tags. 's' for skip, 'q' for quit");
        do {
            String s = sc.nextLine();

            if (s.equalsIgnoreCase("q")) System.exit(0);
            if (s.equalsIgnoreCase("s")) break;

            if (ts.tagNameContains(s)){
                Tag t = ts.getByName(s);
                tagsToBeStreamed.add(t);
            } else {
                Tag t = new Tag();
                t.setTagName(s);
                ts.add(t);

                t = ts.getByName(s);
                tagsToBeStreamed.add(t);
            }
        } while (true);
        return tagsToBeStreamed;
    }

    public void getAllPosts() {
        ps.getAll().forEach(ep::print);
    }

    public void getPostsByStatus() {
        PostStatus ps = getPostStatus();
        this.ps.getAll()
                .stream()
                .filter(p -> p.getPostStatus() == ps)
                .forEach(ep::print);
    }

    private PostStatus getPostStatus() {
        System.out.println("Choose post status which you prefer\n" +
                "1. ACTIVE\n" +
                "2. DELETED\n" +
                " 'q' for quit");
        do {
            String s = sc.nextLine().toLowerCase();
            if (s.equals("q")) System.exit(0);
            switch (s) {
                case "1" -> {
                    return PostStatus.ACTIVE;
                }
                case "2" -> {
                    return PostStatus.DELETED;
                }
                default -> System.out.println("Wrong point. Input other");
            }
        } while (true);
    }

    public Post getPostById() {
        Post p = new Post();
        System.out.println("Input exist post id \n 'q' for quit");
        do {
            String s = sc.nextLine();
            if (s.equalsIgnoreCase("q")) System.exit(0);
                try {
                    Long id = Long.valueOf(s);
                    if (ps.containsId(id)) {
                        p = ps.getById(id);
                        ep.print(p);
                        return p;
                    }
                } catch (NumberFormatException ignored) {}
            System.out.println("Such post doesn't exist! Try again");
        } while (true);
    }

    public void getPostsByTags() {
        List<Tag> tagsList = getTagsList();
        if (tagsList.isEmpty()) {
            ps.getAll().stream()
                    .filter(p ->  p.getPostTags().stream().findAny().isEmpty())
                    .forEach(ep::print);
        } else {
            List<String> tagNames = tagsList.stream().map(Tag::getTagName).collect(Collectors.toList());
            ps.getAll().stream()
                    .filter(p -> p.getPostTags().stream().anyMatch(
                            tag -> {
                                String tagName = tag.getTagName();
                                return tagNames.stream().anyMatch(tagName::equalsIgnoreCase);
                            }
                    ))
                    .forEach(ep::print);
        }
    }

    public void updatePostContentById() {
        Post p = getPostById();

        System.out.println("Input new post contain \n 'q' for quit");

        String s = sc.nextLine();
        if (s.equalsIgnoreCase("q")) System.exit(0);
        p.setPostContent(s);

        ps.update(p);
        System.out.println("Post content updated");
        ep.print(p);
    }


    public void updatePostTagsById() {
        Post p = getPostById();
        Stream<Tag> tagStream = getTagsList().stream();
        p.setPostTags(tagStream.collect(Collectors.toList()));
        ps.update(p);
        System.out.println("Post tags updated");
        ep.print(p);
    }

    public void changePostStatusById() {
        Post p = getPostById();
        PostStatus ps = getPostStatus();
        p.setPostStatus(ps);
        this.ps.update(p);
        ep.print(p);

    }

    public void deletePostById() {
        Post p = getPostById();
        ps.delete(p);
        System.out.println("Post deleted");
    }

    public void deletePostsByStatus() {
        PostStatus ps = getPostStatus();
        this.ps.deleteByStatus(ps);
        System.out.println("Posts deleted");
    }
}
