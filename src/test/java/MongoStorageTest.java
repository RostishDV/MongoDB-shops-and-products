import java.util.Arrays;
import java.util.HashMap;

public class MongoStorageTest {
    private static MongoStorage mongoStorage;

    public static void main(String[] args) {
        mongoStorage = new MongoStorage();
        addShops();
        addProducts();
        placingAtShowcase();
        mongoStorage.showStatistics();
    }

    private static void addShops(){
        String[] titles = {"5ochka", "magnit", "all4home"};
        Arrays.stream(titles).forEach(mongoStorage::addShop);
    }

    private static void addProducts(){
        HashMap<String, Integer> productsMap = new HashMap<>();
        productsMap.put("hammer", 54);
        productsMap.put("nails", 101);
        productsMap.put("scissors", 68);
        productsMap.put("milk", 64);
        productsMap.put("cheese", 120);
        productsMap.put("chocolate", 80);
        productsMap.put("candy's", 40);
        productsMap.put("sausage", 80);
        productsMap.keySet().forEach(k ->
                mongoStorage.addProduct(k, productsMap.get(k)));
    }

    private static void placingAtShowcase(){
        mongoStorage.placeAtShowcase("hammer","all4home");
        mongoStorage.placeAtShowcase("nails","all4home");
        mongoStorage.placeAtShowcase("scissors","all4home");
        mongoStorage.placeAtShowcase("milk","5ochka");
        mongoStorage.placeAtShowcase("cheese","5ochka");
        mongoStorage.placeAtShowcase("sausage","5ochka");
        mongoStorage.placeAtShowcase("chocolate","magnit");
        mongoStorage.placeAtShowcase("candy's","magnit");
    }
}
