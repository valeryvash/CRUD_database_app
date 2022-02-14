package view;

import controller.PostViewController;

import java.util.Scanner;

public class PostView {

    private static final String postViewMessage =
            """
                     :.:.:.: Posts :.:.:.: \s
                    Input the point for continue...
                    1. Create a new post for writer\s
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

    private static final PostViewController pvc = new PostViewController();

    public static void run() {
        System.out.println(postViewMessage);
        choice();
    }

    private static void choice() {
        switch (sc.nextLine().toLowerCase()) {
            case "1" -> pvc.postCreate();
            case "2" -> pvc.getAllPosts();
            case "3" -> pvc.getPostsByStatus();
            case "4" -> pvc.getPostById();
            case "5" -> pvc.getPostsByTags();//23.11.2021
            case "6" -> pvc.updatePostContentById();//23.11.2021
            case "7" -> pvc.updatePostTagsById();//23.11.2021
            case "8" -> pvc.changePostStatusById();//23.11.2021
            case "9" -> pvc.deletePostById();//23.11.2021
            case "10" -> pvc.deletePostsByStatus();//23.11.2021

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

}
