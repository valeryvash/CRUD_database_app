package controller;

import model.Tag;
import repository.JdbcTagRepositoryImpl;
import service.TagService;
import util.EntitiesPrinter;

import java.util.Scanner;

public class TagViewController {

    private final TagService tagService = new TagService(new JdbcTagRepositoryImpl());

    private final Scanner sc = new Scanner(System.in);

    private final EntitiesPrinter ep = new EntitiesPrinter();

    public void printAllTags() {
        tagService.getAll().forEach(ep::print);
    }

    public void updateTag() {
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

    public void deleteTag() {
        Tag t = getTag();
        tagService.delete(t);
    }

    private Tag getTag() {
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
