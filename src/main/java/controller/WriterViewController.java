package controller;

import model.Post;
import model.Writer;
import repository.jdbc.JdbcWriterRepositoryImpl;
import service.WriterService;
import util.EntitiesPrinter;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class WriterViewController {

    private final WriterService writerService = new WriterService(new JdbcWriterRepositoryImpl());

    private final Scanner sc = new Scanner(System.in);

    private final EntitiesPrinter ep = new EntitiesPrinter();

    public WriterViewController() {}

    public void writerCreate() {
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

    public Writer getWriter() {
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

    public void updateWriterName() {
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

    public void writerDelete() {
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

    public Stream<Post> getWriterPosts(boolean print) {
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

    public void getAllWriters() {
        writerService.getAll().forEach(ep::print);
    }

}
