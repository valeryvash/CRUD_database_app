package view;

import model.Tag;
import repository.jdbc.JdbcTagRepositoryImpl;
import service.TagService;
import util.EntitiesPrinter;

import java.util.Scanner;

public class TagView {

    private static final String tagViewMessage =
            """
                    :.:.:.: Tags :.:.:.:
                    Input the point for continue...
                    1. Print all tags
                    2. Update tag
                    3. Delete tag
                    
                    \t'q' for quit
                    \t'p' for previous screen
                    """;

    private static final Scanner sc = new Scanner(System.in);

    private final static EntitiesPrinter ep = new EntitiesPrinter();

    private final static TagService tagService = new TagService(new JdbcTagRepositoryImpl());

    public static void run() {
        System.out.println(tagViewMessage);
        choice();
    }

    private static void choice() {
        switch (sc.nextLine().toLowerCase()) {
            case "1" -> printAllTags();
            case "2" -> updateTag();
            case "3" -> deleteTag();

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

    private static void printAllTags() {
        tagService.getAll().forEach(ep::print);
    }

    private static void updateTag() {
        Tag t = getTag();
        do {
            System.out.println("Input new tag name");
            String s = sc.nextLine();
            if (s.equalsIgnoreCase("q")) System.exit(0);

            if (!tagService.tagNameContains(s)) {
                t.setTagName(s);
                break;
            }
            System.out.println("Such tag name already exist! Try again");
        } while (true);
        tagService.update(t);
        System.out.println("Tag successfully updated");
    }

    private static void deleteTag() {
        Tag t = getTag();
        tagService.delete(t);
    }

    private static Tag getTag() {
        System.out.println("Input exist tag id or name \n 'q' for quit");
        do {
            String s = sc.nextLine();
            if (s.equalsIgnoreCase("q")) System.exit(0);
            if (!tagService.tagNameContains(s)) {
                try {
                    Long id = Long.valueOf(s);
                    if (tagService.containsId(id)){
                        Tag t = tagService.getById(id);
                        ep.print(t);
                        return t;
                    }
                } catch (NumberFormatException ignored) {}
            } else {
                Tag t = tagService.getByName(s);
                ep.print(t);
                return t;
            }
            System.out.println("Such tag doesn't exist! Try again");
        } while (true);
    }

}
