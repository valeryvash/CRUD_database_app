package view;

import controller.TagViewController;

import java.util.Scanner;

public class TagView {

    private static final String tagViewMessage =
            """
                        Tags
                    Input the point for continue...
                    1. Print all tags
                    2. Update tag
                    3. Delete tag
                    
                    \t'q' for quit
                    \t'p' for previous screen
                    """;

    private static final Scanner sc = new Scanner(System.in);

    private static final TagViewController tvc = new TagViewController();

    public static void run() {
        System.out.println(tagViewMessage);
        choice();
    }

    private static void choice() {
        switch (sc.nextLine().toLowerCase()) {
            case "1" -> tvc.printAllTags();
            case "2" -> tvc.updateTag();
            case "3" -> tvc.deleteTag();

            case "q" -> System.exit(0);
            case "p" -> StartView.run();
            default -> System.out.println("Wrong point. Try another");
        }
        question();
    }

    private static void question() {
        System.out.print("Show 'TagView' again? 'y' for yes\t");
        switch (sc.nextLine().toLowerCase()) {
            case "y" -> TagView.run();
            default -> System.exit(0);
        }
    }

}
