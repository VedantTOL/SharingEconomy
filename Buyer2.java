import java.util.ArrayList;

public class Buyer2 extends User {
    private double balance;
    private double price;
    private ArrayList<Product> shoppingCart;
    private ArrayList<Product> purchases;

    public Buyer2(int uniqueIdentifier, String email, String password, String name, int age, double balance) {
        super(uniqueIdentifier, email, password, name, age);
        this.balance = balance;
        this.shoppingCart = null;
        this.purchases = null;
    }


    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void addToShoppingCart(Product product, int numProductsForPurchase, Store store) {
        try {
            for (int i = 0; i < store.getProducts().size(); i++) {
                if (store.getProducts().get(i).equals(product)) {
                    if (product.getQuantityForPurchase() > numProductsForPurchase) {
                        System.out.printf("%d of %s have been added to your cart!\n", numProductsForPurchase, product.getName());
                        // have to modify the product object and its copy that is in the array
                        store.getProducts().get(i).setQuantityForPurchase(product.getQuantityForPurchase() - numProductsForPurchase);
                        product.setQuantityForPurchase(product.getQuantityForPurchase() - numProductsForPurchase);
                        shoppingCart.add(product);

                    } else if (product.getQuantityForPurchase() < numProductsForPurchase) {
                        System.out.printf("There are only %d %s's left in the store. How many would you like to add to " +
                                "your cart?\n", product.getQuantityForPurchase(), product.getName());
                        // at this point we call this method again with the new number the buyer enters

                    } else if (product.getQuantityForPurchase() == numProductsForPurchase) {
                        System.out.printf("You got the last %d of %s!\n", product.getQuantityForPurchase()
                                , product.getName());
                        product.setQuantityForPurchase(0);
                        store.getProducts().remove(product);
                        shoppingCart.add(product);
                    }
                } else {
                    System.out.println("This product does not exist in our store!");
                }
            }
        } catch (NullPointerException e) {
            System.out.println("This store has no products left!");
        }
    }

    public void removeFromShoppingCart() {

    }

    // TODO make sure to make it so that the user can buy one or more, or even all of the products in their cart
    // we can use the purchases field I made and update that, or this method can return an array of the purchases
    public void buyProduct(Product product, int numProductsForPurchase, Store store) {
        if (balance >= product.getPrice()) {
            balance -= product.getPrice();
            System.out.println(super.getName() + " bought " + product.getName() + " for $" + product.getPrice());
        } else {
            System.out.println(super.getName()+ " cannot afford " + product.getName());
        }
    }
}

