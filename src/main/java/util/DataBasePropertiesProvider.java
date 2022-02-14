package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataBasePropertiesProvider {

    private static String DB_URL;
    private static String USER;
    private static String PASS;

    static {
        init();
    }

    private static void init() {
        try (FileReader applicationProperties = new FileReader("src/main/resources/application.properties");
                BufferedReader lineReader =new BufferedReader(applicationProperties)) {

            List<String> params = new ArrayList<>();

            for (int i = 0; i < 3; i++) {
                String temp = lineReader.readLine().trim();
                int equalSign = temp.indexOf('=') + 1;
                params.add(temp.substring(equalSign));
            }

            DB_URL = params.get(0);
            USER = params.get(1);
            PASS = params.get(2);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DataBasePropertiesProvider() {}

    public static String getDB_URL() {
        if (DB_URL == null) {
            init();
        }
        return DB_URL;
    }

    public static String getUSER() {
        if (USER == null) {
            init();
        }
        return USER;
    }

    public static String getPASS() {
        if (PASS == null) {
            init();
        }
        return PASS;
    }
}
