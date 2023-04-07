import java.util.zip.DataFormatException;

public class Product {
    private String name;
    private String store;
    private String description;
    private int quantityForPurchase;
    private double price;
    private int quantitySold;

    public Product(String name, String store, String description, int quantityForPurchase, double price, int quantitySold) {
        this.name = name;
        this.store = store;
        this.description = description;
        this.quantityForPurchase = quantityForPurchase;
        this.price = price;
        this.quantitySold = quantitySold;
    }

    public Product(String[] productDetails) throws DataFormatException{
        if (productDetails.length != 5) {
            throw new DataFormatException("Insufficient Details, please try again!");
        }
        this.name = productDetails[0];
        this.description = productDetails[1];

        try {
            this.quantityForPurchase = Integer.parseInt(productDetails[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Quantity for Purchase must be an Integer");
        }

        try {
            int tempPrice = Integer.parseInt(productDetails[3]);
            if (tempPrice < 0 ) {
                throw new IllegalArgumentException("Valid price required!");
            } else {
                this.price = tempPrice;
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Age must be an Integer");
        }

        try {
            this.quantityForPurchase = Integer.parseInt(productDetails[4]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Quantity Sold must be an Integer");
        }

    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
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
        return String.format("%s, %s, %d, %d, %d", name, description, quantityForPurchase, price, quantitySold);
    }
}
