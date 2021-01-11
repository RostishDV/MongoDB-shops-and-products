import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        MongoStorage storage = new MongoStorage();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        for (;;) {
            try {
                String command = reader.readLine();
                String[] commandArray = command.split(" ");
                if (commandArray.length == 3) {
                    if (commandArray[0].equals("ДОБАВИТЬ_ТОВАР")) {
                        storage.addProduct(commandArray[1], Integer.parseInt(commandArray[2]));
                    } else if (commandArray[0].equals("ВЫСТАВИТЬ_ТОВАР")) {
                        storage.placeAtShowcase(commandArray[1], commandArray[2]);
                    }
                } else if (commandArray.length == 2 &&
                        commandArray[0].equals("ДОБАВИТЬ_МАГАЗИН")) {
                    storage.addShop(commandArray[1]);
                } else if (commandArray[0].equals("СТАТИСТИКА_ТОВАРОВ")){
                    storage.showStatistics();
                } else {
                    System.out.println("wrong command, try again");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
