import java.util.Comparator;
import java.util.zip.DataFormatException;
import java.util.Collections;

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

    public Product(String name, String description, int quantityForPurchase, double price, int quantitySold, int uniqueID) {
        this.name = name;
        this.description = description;
        this.quantityForPurchase = quantityForPurchase;
        this.price = price;
        this.quantitySold = quantitySold;
        this.uniqueID = uniqueID;
    }
    public Product(int uniqueID) {


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
            this.quantityForPurchase = Integer.parseInt(productDetails[5]);
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
    
    public String toString() {
        return String.format("%d, %s, %s, %d, %.2f, %d", uniqueID, name, description, quantityForPurchase, price, quantitySold);
    }

    public String marketplaceString() {
        return String.format("Name: %s Price: $%.2f", name, price);
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
