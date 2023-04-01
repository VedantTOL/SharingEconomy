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

    // the customer wil enter an input that we can convert to a boolean, stating that they want to buy a specific product
    // this method can be used in conjunction with the shopping cart selection potentially
    public void buyProduct(boolean buyProduct, Product product) {
        if (buyProduct) {
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).equals(product)) {
                    if (inStock(product)) {
                        product.setQuantityForPurchase(product.getQuantityForPurchase() - 1);
                        System.out.println("Thank you for your purchase!");
                        // I think we want to put something here that adds the product to the user's purchases

                    } else if (!inStock(product)) {
                        System.out.println("This product(car model) is out of stock!");
                    } else if (inStock(product) && product.getQuantityForPurchase() == 1) {
                        product.setQuantityForPurchase(product.getQuantityForPurchase() - 1);
                        products.remove(product);
                        System.out.println("That was our last one!");
                    }
                }
            }
        } else {
            System.out.println("Thank you for your interest in our product(car)!");
        }
        // I might need to add an exception where if the user tries to buy a product that doesn't exist in the store,
        // an error message is thrown
    }

    // this method could be used to determine if a certain product in the store object is in/out of stock
    public boolean inStock(Product product) {
        return product.getQuantityForPurchase() > 0;
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
