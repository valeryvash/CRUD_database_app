package view;

import model.Post;
import model.PostStatus;
import model.Tag;
import model.Writer;
import repository.PostRepository;
import repository.TagRepository;
import repository.WriterRepository;
import repository.jdbc.JdbcPostRepositoryImpl;
import repository.jdbc.JdbcTagRepositoryImpl;
import repository.jdbc.JdbcWriterRepositoryImpl;
import service.PostService;
import service.TagService;
import service.WriterService;
import util.EntitiesPrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PostView {

    private static final String postViewMessage =
            """
                     :.:.:.: Posts :.:.:.:
                    Input the point for continue...
                    1. Create a new post for writer
                    2. Show all posts
                    3. Show all post by status
                    4. Show post by id
                    5. Show posts by tags
                    6. Update post content by post id
                    7. Update post tags by post id
                    8. Change post status by post id
                    9. Delete post by id
                    10. Delete posts by status
                    \t'q' for quit
                    \t'p' for previous screen
                    """;

    private static final Scanner sc = new Scanner(System.in);
    private final static EntitiesPrinter ep = new EntitiesPrinter();

    private static WriterRepository wr = new JdbcWriterRepositoryImpl();
    private static PostRepository pr = new JdbcPostRepositoryImpl();
    private static TagRepository tr = new JdbcTagRepositoryImpl();

    private final static PostService postService = new PostService(pr, wr, tr);
    private final static TagService tagService = new TagService(tr);
    private final static WriterService writerService = new WriterService(wr, pr, tr);

    public static void run() {
        System.out.println(postViewMessage);
        choice();
    }

    private static void choice() {
        switch (sc.nextLine().toLowerCase()) {
            case "1" -> postCreate();
            case "2" -> getAllPosts();
            case "3" -> getPostsByStatus();
            case "4" -> getPostById();
            case "5" -> getPostsByTags();
            case "6" -> updatePostContentById();
            case "7" -> updatePostTagsById();
            case "8" -> changePostStatusById();
            case "9" -> deletePostById();
            case "10" -> deletePostsByStatus();

            case "q" -> System.exit(0);
            case "p" -> StartView.run();
            default -> System.out.println("Wrong point. Try another");
        }
        question();
    }

    private static void question() {
        System.out.print("Show 'PostView' again? 'y' for yes\t");
        switch (sc.nextLine().toLowerCase()) {
            case "y" -> PostView.run();
            default -> System.exit(0);
        }
    }

    private static Writer getWriter() {
        Writer w = new Writer();
        System.out.println("Input exist writer id or name \n 'q' for quit");
        do {
            String s = sc.nextLine();
            if (s.equalsIgnoreCase("q")) System.exit(0);
            if (!writerService.writerNameContains(s)) {
                try {
                    Long id = Long.valueOf(s);
                    if (writerService.containsId(id)) {
                        w = writerService.getById(id);
                        ep.print(w);
                        return w;
                    }
                } catch (NumberFormatException ignored) {
                }
            } else {
                w = writerService.getByName(s);
                ep.print(w);
                return w;
            }
            System.out.println("Such writer doesn't exist! Try again");
        } while (true);
    }

    private static void postCreate() {
        Writer w = getWriter();
        long writerId = w.getId();
        Post p = new Post();
        System.out.println("Input new post contain \n 'q' for quit");

        String s = sc.nextLine();
        if (s.equalsIgnoreCase("q")) System.exit(0);
        p.setPostContent(s);
        p.setWriter(w);
        p.setPostTags(getTagsList());
        postService.add(p);
        System.out.println("Post created");
        ep.print(w);
        ep.print(p);
    }

    private static List<Tag> getTagsList() {
        List<Tag> tagsToBeStreamed = new ArrayList<>();
        System.out.println("Input tags. 's' for skip, 'q' for quit");
        do {
            String s = sc.nextLine();

            if (s.equalsIgnoreCase("q")) System.exit(0);
            if (s.equalsIgnoreCase("s")) break;

            if (tagService.tagNameContains(s)) {
                Tag t = tagService.getByName(s);
                tagsToBeStreamed.add(t);
            } else {
                Tag t = new Tag();
                t.setTagName(s);
                tagService.add(t);

                t = tagService.getByName(s);
                tagsToBeStreamed.add(t);
            }
        } while (true);
        return tagsToBeStreamed;
    }

    private static void getAllPosts() {
        postService.getAll().forEach(ep::print);
    }

    private static void getPostsByStatus() {
        PostStatus ps = getPostStatus();
        postService.getAll()
                .stream()
                .filter(p -> p.getPostStatus() == ps)
                .forEach(ep::print);
    }

    private static  PostStatus getPostStatus() {
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

    private static  Post getPostById() {
        Post p = new Post();
        System.out.println("Input exist post id \n 'q' for quit");
        do {
            String s = sc.nextLine();
            if (s.equalsIgnoreCase("q")) System.exit(0);
            try {
                Long id = Long.valueOf(s);
                if (postService.containsId(id)) {
                    p = postService.getById(id);
                    ep.print(p);
                    return p;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Such post doesn't exist! Try again");
        } while (true);
    }

    private static void getPostsByTags() {
        List<Tag> tagsList = getTagsList();
        if (tagsList.isEmpty()) {
            postService.getAll().stream()
                    .filter(p -> p.getPostTags().stream().findAny().isEmpty())
                    .forEach(ep::print);
        } else {
            List<String> tagNames = tagsList.stream().map(Tag::getTagName).collect(Collectors.toList());
            postService.getAll().stream()
                    .filter(p -> p.getPostTags().stream().anyMatch(
                            tag -> {
                                String tagName = tag.getTagName();
                                return tagNames.stream().anyMatch(tagName::equalsIgnoreCase);
                            }
                    ))
                    .forEach(ep::print);
        }
    }

    private static void updatePostContentById() {
        Post p = getPostById();

        System.out.println("Input new post contain \n 'q' for quit");

        String s = sc.nextLine();
        if (s.equalsIgnoreCase("q")) System.exit(0);
        p.setPostContent(s);

        postService.update(p);
        System.out.println("Post content updated");
        ep.print(p);
    }


    private static void updatePostTagsById() {
        Post p = getPostById();
        Stream<Tag> tagStream = getTagsList().stream();
        p.setPostTags(tagStream.collect(Collectors.toList()));
        postService.update(p);
        System.out.println("Post tags updated");
        ep.print(p);
    }

    private static void changePostStatusById() {
        Post p = getPostById();
        PostStatus ps = getPostStatus();
        p.setPostStatus(ps);
        postService.update(p);
        ep.print(p);

    }

    private static void deletePostById() {
        Post p = getPostById();
        postService.delete(p);
        System.out.println("Post deleted");
    }

    private static void deletePostsByStatus() {
        PostStatus ps = getPostStatus();
        postService.deleteByStatus(ps);
        System.out.println("Posts deleted");
    }


}
