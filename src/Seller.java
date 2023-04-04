import java.util.ArrayList;
import java.io.*;
import java.util.zip.DataFormatException;

public class Seller extends User {
    private ArrayList<Store> stores;

    public Seller(int uniqueIdentifier, String email, String password, String name, int age, ArrayList<Store> stores) {
        super(uniqueIdentifier, email, password, name, age);
        this.stores = stores;
    }
    public Seller (User user) {
        super();
    }

    public Seller () {
        this.stores = null;
    }

    public Seller(int uniqueIdentifier) {
        super(uniqueIdentifier);
        this.stores = new ArrayList<Store>();
    }
    public void addStore(Store store) {
        stores.add(store);
    }

    public ArrayList<Store> getStores() {
        return stores;
    }

    public ArrayList<Product> getStore(int index) {
        return stores.get(index).getProducts();
    }

    public void setStores(ArrayList<Store> stores) {
        this.stores = stores;
    }

    public ArrayList<Seller> readSellerDatabase() {
        File f;
        FileReader fr;
        BufferedReader bfr;
        String line;
        ArrayList<Seller> database= new ArrayList<Seller>();
        Seller seller;
        Store store;
        Product product;
        int sellerIndex = -1;
        int storeIndex = -1;

        try {
            bfr = new BufferedReader(new FileReader(new File("./src/SellerDatabase.txt")));

            while (true) {
                line = bfr.readLine();
                //System.out.println();
                if (line == null) {
                    break;
                }
                char identifier = line.charAt(0);
                if (identifier == 42) {
                    sellerIndex++;
                    storeIndex = -1;
                    seller = new Seller(Integer.parseInt(line.split(" ")[1]));
                    database.add(seller);
                } else if (identifier == 43) {
                    storeIndex++;
                    store = new Store(line.split(" ")[1]);
                    database.get(sellerIndex).addStore(store);
                } else {
                    try {
                        product = new Product(line.split(", "));
                        System.out.println(storeIndex);
                        database.get(sellerIndex).getStores().get(storeIndex).addProduct(product);
                    } catch (DataFormatException e) {
                        System.out.println("Seller Database Malformed!");
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return database;

    }
}
