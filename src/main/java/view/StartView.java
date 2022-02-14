package view;

import java.util.Scanner;

public class StartView {
    private static String startMessage =
            " :.: Welcome to the 'ConsoleCRUD app v2! :.: \n" +
                    "Input the point for continue...\n" +
                    "1. WriterView\n" +
                    "2. PostView\n" +
                    "3. TagView\n" +
                    "\t 'q' for quit";

    static Scanner sc = new Scanner(System.in);

    public static void run() {
        System.out.println(startMessage);
        choice();
    }

    private static void choice() {
        switch (sc.nextLine().toLowerCase()) {
            case "1" -> WriterView.run();
            case "2" -> PostView.run();
            case "3" -> TagView.run();

            case "q" -> System.out.println("Bye!");
            default -> System.out.println("Try another one");
        }
        question();
    }

    private static void question() {
        System.out.print("Show 'StartView' again? 'y' for yes\t");
        if ("y".equals(sc.nextLine().toLowerCase())) {
            StartView.run();
        } else {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        StartView.run();
    }
}
