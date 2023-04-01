public class Product {
    private String name;
    private String store;
    private String description;
    private int quantityForPurchase;
    private double price;

    public Product(String name, String store, String description, int quantityForPurchase, double price) {
        this.name = name;
        this.store = store;
        this.description = description;
        this.quantityForPurchase = quantityForPurchase;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
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
}
