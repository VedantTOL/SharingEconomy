import java.util.ArrayList;

public class Store {
    private String storeName;


    public int getStockRemaining() {
        int result = 0;
        for (Product product: products) {
            result += product.getQuantityForPurchase();
        }
        return result;
    }


    public double getTotalValueSold() {
        double result = 0;
        for (Product product: products) {
            result += product.getQuantitySold() * product.getPrice();
        }
        return result;
    }


    public int getTotalQuantitySold() {
        int result = 0;
        for (Product product: products) {
            result += product.getQuantitySold();
        }
        return result;
    }



    private ArrayList<Product> products;

    public Store(String storeName, ArrayList<Product> products) {
        this.storeName = storeName;
        this.products = products;
    }

    public Store(String storeName) {
        this.storeName = storeName;
        this.products = new ArrayList<Product>();
    }


    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    // to String method just to see what the store looks like
    public String toString() {
        return String.format("%s", storeName);
    }
}