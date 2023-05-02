import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
/**
 * The product class constructs a product object to be sold and bought in the marketplace. Each product also has a
 * unique ID that which allows its store of origin to be identified after being sold. Products also have a specific
 * name, price, description, quantity for purchase, and quantity sold. Methods in this class also allow for reading
 * and writing products to their associated files.
 *
 *
 * @author Roger, Somansh, Ethan, Vedant
 * @version June 13, 2022
 */

public class Product {
    private String name;

    public int getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(int uniqueID) {
        this.uniqueID = uniqueID;
    }

    private int uniqueID;
    private String description;
    private int quantityForPurchase;
    private double price;
    private int quantitySold;

    public ArrayList<Seller> readSellerDatabase() {
        BufferedReader bfr = null;
        String line;
        ArrayList<Seller> database = new ArrayList<Seller>();
        Seller seller;
        Store store;
        Product product;
        int sellerIndex = -1;
        int storeIndex = -1;

        try {
            bfr = new BufferedReader(new FileReader("./src/SellerDatabase.txt"));

            while (true) {
                line = bfr.readLine();

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
                    database.get(sellerIndex).addStore(storeIndex, store);
                } else {
                    try {
                        product = new Product(line.split(", "));
                        database.get(sellerIndex).getStores().get(storeIndex).addProduct(product);
                    } catch (DataFormatException e) {
                        System.out.println("Seller Database Malformed!");
                    }
                }

            }

            bfr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return database;

    }
    public ArrayList<Product> getProductDatabase() {
        ArrayList<Seller> database = readSellerDatabase();
        ArrayList<Product> productDatabase = new ArrayList<Product>();
        for (Seller seller: database) {
            for (Store store : seller.getStores()) {
                for (Product product : store.getProducts()) {
                    productDatabase.add(product);
                }
            }
        }
        return productDatabase;
    }
    public Product(String name, String description, int quantityForPurchase, double price, int quantitySold, int uniqueID) {
        this.name = name;
        this.description = description;
        this.quantityForPurchase = quantityForPurchase;
        this.price = price;
        this.quantitySold = quantitySold;
        this.uniqueID = uniqueID;
    }
    public Product(int uniqueID) {
        this.uniqueID = uniqueID;
        ArrayList<Product> productDatabase = getProductDatabase();
        for (Product product: productDatabase) {
            if (uniqueID == product.getUniqueID()) {
                this.description = product.getDescription();
                this.quantityForPurchase = product.getQuantityForPurchase();
                this.price = product.getPrice();
                this.quantitySold = product.getQuantitySold();
                this.name = product.getName();
            }
        }
    }

    public double getValueSold() {
        return getPrice() * getQuantitySold();
    }

    public Product(String[] productDetails) throws DataFormatException{
        if (productDetails.length != 6) {
            throw new DataFormatException("Insufficient Details, please try again!");
        }
        this.uniqueID = Integer.parseInt(productDetails[0]);
        this.name = productDetails[1];
        this.description = productDetails[2];

        try {
            this.quantityForPurchase = Integer.parseInt(productDetails[3]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Quantity for Purchase must be an Integer");
        }

        try {
            double tempPrice = Double.parseDouble(productDetails[4]);
            if (tempPrice < 0 ) {
                throw new IllegalArgumentException("Valid price required!");
            } else {
                this.price = tempPrice;
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Price must be a Double");
        }

        try {
            this.quantitySold = Integer.parseInt(productDetails[5]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Quantity Sold must be an Integer");
        }


    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantityForPurchase() {
        return quantityForPurchase;
    }

    public void setQuantityForPurchase(int quantityForPurchase) {
        this.quantityForPurchase = quantityForPurchase;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String toDatabase() {
        return String.format("%d, %s, %s, %d, %.2f, %d", uniqueID, name, description, quantityForPurchase, price, quantitySold);
    }

    public String marketplaceString() {
        return String.format("Name: %s Price: $%.2f", name, price);
    }

    public String productPage() {
        return String.format("Name: %s\nPrice: $%.2f\nDescription: %s\nQuantity Available: %d\nQuantity Sold: %d", name, price, description, quantityForPurchase, quantitySold);

    }

    public void addPurchase(int orderQ) {
        this.setQuantitySold(this.getQuantitySold() + orderQ);
    }
/*
    Comparator<Product> compareByPrice = (Product o1, Product o2) ->
            o1.getPrice().compareTo( o2.getPrice() );

    @Override
    public int compareTo(Product o) {
        double delta = o.getPrice() - this.getPrice();
        if (delta > 0) return 1;
        else if (delta < 0) return -1;
        else return 0;
    }

 */
}


