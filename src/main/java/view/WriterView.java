package view;

import controller.WriterViewController;

import java.util.Scanner;

public class WriterView {

    private static final String writerViewMessage =
            """
                    Writers
                    Input the point for continue...
                    1. Create a new writer
                    2. Print writer info
                    3. Update writer name
                    4. Delete writer\s
                    5. Print writer posts
                    6. Print all writers info
                    \t'q' for quit
                    \t'p' for previous screen
                    """;

    private static final Scanner sc = new Scanner(System.in);

    private static final WriterViewController wvc = new WriterViewController();

    public static void run() {
        System.out.println(writerViewMessage);
        choice();
    }

    private static void choice() {
        switch (sc.nextLine().toLowerCase()) {
            case "1" -> wvc.writerCreate();
            case "2" -> wvc.getWriter();
            case "3" -> wvc.updateWriterName();
            case "4" -> wvc.writerDelete();
            case "5" -> wvc.getWriterPosts(true);
            case "6" -> wvc.getAllWriters();

            case "q" -> System.exit(0);
            case "p" -> StartView.run();
            default -> System.out.println("Wrong point. Try another");
        }
        question();
    }

    private static void question() {
        System.out.print("Show 'WriterView' again? 'y' for yes\t");
        switch (sc.nextLine().toLowerCase()) {
            case "y" -> WriterView.run();
            default -> System.exit(0);
        }
    }

}
