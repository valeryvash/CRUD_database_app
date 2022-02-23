package view;

import model.Post;
import model.Writer;
import repository.PostRepository;
import repository.TagRepository;
import repository.WriterRepository;
import repository.jdbc.JdbcPostRepositoryImpl;
import repository.jdbc.JdbcTagRepositoryImpl;
import repository.jdbc.JdbcWriterRepositoryImpl;
import service.WriterService;
import util.EntitiesPrinter;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class WriterView {

    private static final String writerViewMessage =
            """
                    :.:.:.: Writers :.:.:.:
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

    private static final WriterRepository wr = new JdbcWriterRepositoryImpl();
    private static final PostRepository pr = new JdbcPostRepositoryImpl();
    private static final TagRepository tr = new JdbcTagRepositoryImpl();

    private final static WriterService writerService = new WriterService(wr, pr, tr);

    private static final EntitiesPrinter ep = new EntitiesPrinter();

    public static void run() {
        System.out.println(writerViewMessage);
        choice();
    }

    private static void choice() {
        switch (sc.nextLine().toLowerCase()) {
            case "1" -> writerCreate();
            case "2" -> getWriter();
            case "3" -> updateWriterName();
            case "4" -> writerDelete();
            case "5" -> getWriterPosts(true);
            case "6" -> getAllWriters();

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

    private static void writerCreate() {
        System.out.println("Input new writer name \n 'q' for quit");
        Writer w = new Writer();
        do {
            String s = sc.nextLine();
            if (s.equalsIgnoreCase("q")) System.exit(0);
            if (!writerService.writerNameContains(s)) {
                w.setWriterName(s);
                writerService.add(w);
                System.out.println("New writer created");
                ep.print(w);
                break;
            } else {
                System.out.println("Writer name already exist! Try another");
            }
        } while (true);
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
                    if (writerService.containsId(id)){
                        w = writerService.getById(id);
                        ep.print(w);
                        return w;
                    }
                } catch (NumberFormatException ignored) {}
            } else {
                w = writerService.getByName(s);
                ep.print(w);
                return w;
            }
            System.out.println("Such writer doesn't exist! Try again");
        } while (true);
    }

    private static void updateWriterName() {
        Writer w = getWriter();
        System.out.println("Input new writer name \n 'q' for quit");
        do {
            String s = sc.nextLine();
            if (s.equalsIgnoreCase("q")) System.exit(0);
            if (!writerService.writerNameContains(s)) {
                w.setWriterName(s);
                writerService.update(w);
                System.out.println("Writer updated");
                ep.print(w);
                break;
            } else {
                System.out.println("Such writer name already exist! Try another ");
            }
        } while (true);
    }

    private static void writerDelete() {
        Writer w = getWriter();
        do{
            System.out.println("Are you sure want to delete this writer?\n" +
                    " All writer posts will be deleted\n " +
                    "\n 'y' for yes \n 'q' for quit");
            String s = sc.nextLine().toLowerCase();
            if (s.equalsIgnoreCase("q")) System.exit(0);
            if (s.equalsIgnoreCase("y")) {
                writerService.delete(w);
                System.out.println("Writer deleted.");
                break;
            }
        } while(true);
    }

    private static Stream<Post> getWriterPosts(boolean print) {
        Writer w = getWriter();
        long writerId = w.getId();
        List<Post> writerPosts = w.getWriterPosts();
        if (!writerPosts.isEmpty()){
            if (print) {
                writerPosts.forEach(ep::print);
                return writerPosts.stream();
            } else {
                return writerPosts.stream();
            }
        } else {
            System.out.println("Writer has no posts");
            return Stream.empty();
        }
    }

    private static void getAllWriters() {
        writerService.getAll().forEach(ep::print);
    }

}
