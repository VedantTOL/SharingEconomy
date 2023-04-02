import java.util.ArrayList;

public class Store {
    private String storeName;
    private String storeOwner;
    private ArrayList<Product> products;

    public Store(String storeName, String storeOwner, ArrayList<Product> products) {
        this.storeName = storeName;
        this.storeOwner = storeOwner;
        this.products = products;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreOwner() {
        return storeOwner;
    }

    public void setStoreOwner(String storeOwner) {
        this.storeOwner = storeOwner;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    // this method will calculate the sales made, so this method will only be called after product(s) have been sold
    public double calculateSales() {
        return 0;
    }

    // to String method just to see what the store looks like
    public String toString() {
        return storeName + storeOwner + products;
    }


}
