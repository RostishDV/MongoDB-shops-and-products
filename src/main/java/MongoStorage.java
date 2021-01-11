import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;

import org.bson.Document;
import org.bson.conversions.Bson;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MongoStorage {
    //записи имеют формат
    //  для магазинов
    //  {
    //      "title" : shopTitle,
    //      "products" : [{"name" : productName1}, {"name" : productName2}]
    //  }
    //  для товаров
    //  {
    //      "name" : name,
    //      "price" : price
    //  }

    private MongoCollection<Document> shopsCollection;
    private MongoCollection<Document> productsCollection;
//    private final static Class<? extends List> docClazz = ArrayList.class;

    public MongoStorage(){
        String dbName = "local";
        String shopsCollectionName = "shops";
        String productsCollectionName = "products";
        String host = "127.0.0.1";
        int port = 27017;
        MongoClient client = new MongoClient(host, port);
        MongoDatabase database = client.getDatabase(dbName);
        shopsCollection = database.getCollection(shopsCollectionName);
        productsCollection = database.getCollection(productsCollectionName);
    }

    public void addShop(String title){
        if (shopsCollection
                .find(Filters.eq("title", title))
                .first() == null) {
            Document shop = new Document().append("title", title).append("products", new ArrayList<String>());
            shopsCollection.insertOne(shop);
        } else {
            System.out.println("Can't add, that's already exist");
        }
    }

    public void addProduct(String productName, int price){
        if (productsCollection
                .find(Filters.eq("name", productName))
                .first() == null) {
            Document product = new Document().append("name", productName).append("price", price);
            productsCollection.insertOne(product);
        } else {
            System.out.println("Can't add, that's already exist");
        }
    }

    public void placeAtShowcase(String productName, String shopTitle){
        if (productsCollection
                .find(Filters.eq("name", productName))
                .first() != null) {
            ArrayList<String> products;
            Object o = shopsCollection.find().
                    filter(Filters.eq("title", shopTitle)).first().get("products");
            if (o != null) {
                products = (ArrayList<String>) o;
                products.add(productName);
                shopsCollection.updateOne(new Document().append("title", shopTitle), new Document().append("$set", new Document().append("products", products)));
            } else {
                System.out.println("Error in update");
            }
        } else {
            System.out.println("product " + productName + " not found");
        }
    }

    public void showStatistics(){
        HashMap<String,HashMap<String,String>> statistics = new HashMap<>();
// example
//        db.products.aggregate([
//          {"$lookup":{
//              "from":"shops",
//              "localField":"name",
//              "foreignField":"products",
//              "as":"shops_list"}
//          },{
//              "$unwind":{
//                  "path":"$shops_list"
//                  }
//          },{
//              "$group":{
//                  "_id":"$shops_list.title",
//                  "avg":{$avg:"$price"},
//                  "min":{"$min":"$price"},
//                  "max":{"$max":"$price"},
//                  "count":{"$sum":1}
//              }
//           }])
        List<BsonField> groupQuery = Arrays.asList(
                Accumulators.avg("avg","$price"),
                Accumulators.min("min","$price"),
                Accumulators.max("max","$price"),
                Accumulators.sum("count",1)
        );

        Bson lookup = Aggregates.lookup("shops",
                "name",
                "products",
                "shops_list");

        Bson unwind = Aggregates.unwind("$shops_list");

        Bson group = Aggregates.group("$shops_list.title", groupQuery);

        for (Document doc : productsCollection.aggregate(Arrays.asList(
                lookup,
                unwind,
                group
        ))){
            String title =  (String) doc.get("_id");
            HashMap<String,String> currentShopStatistic = new HashMap<>();
            currentShopStatistic.put("averagePrice", Double.
                    toString((Double) doc.get("avg")));
            currentShopStatistic.put("minPrice", Integer.
                    toString((Integer) doc.get("min")));
            currentShopStatistic.put("maxPrice", Integer.
                    toString((Integer) doc.get("max")));
            currentShopStatistic.put("totalProductsCount",Integer.
                    toString((Integer) doc.get("count")));
            statistics.put(title,currentShopStatistic);
        }

        Bson match = Aggregates.match(Filters.lt("price",100));

        group = Aggregates.group("$shops_list.title",
                Accumulators.sum("count",1));

        for (Document doc : productsCollection.aggregate(Arrays.asList(
            match, lookup, unwind, group
        ))){
            String title = (String) doc.get("_id");
            HashMap<String,String> currentStatistic = statistics.get(title);
            currentStatistic.put("cheaper100products",Integer.
                    toString((Integer) doc.get("count")));
            statistics.put(title,currentStatistic);
        }


        for (String title : statistics.keySet()){
            System.out.println("in " + title + " shop:");
            HashMap<String,String> shopStatistic = statistics.get(title);
            System.out.println("\ttotal products amount: " + shopStatistic.get("totalProductsCount"));
            System.out.println("\taverage products price: " + shopStatistic.get("averagePrice"));
            System.out.println("\tminimal products price: " + shopStatistic.get("minPrice"));
            System.out.println("\tmaximal products price: " + shopStatistic.get("maxPrice"));
            System.out.println("\tproducts cheaper then 100: " + shopStatistic.get("cheaper100products"));
        }
    }
}
