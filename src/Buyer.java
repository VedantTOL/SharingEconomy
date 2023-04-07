import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.zip.DataFormatException;

public class Buyer extends User {
    private double balance;
    private ArrayList<Product> shoppingCart;
    private ArrayList<Product> purchases;

    public Buyer(int uniqueIdentifier, String email, String password, String name, int age, double balance) {
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

    public ArrayList<Product> getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ArrayList<Product> shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public ArrayList<Product> getPurchases() {
        return purchases;
    }

    public void setPurchases(ArrayList<Product> purchases) {
        this.purchases = purchases;
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

    public String viewMarketPlace(int choice, Scanner scanner) {
        if (choice == 1) {

            ArrayList<Seller> sortProducts = readSellerDatabase();
            if (sortProducts == null) {
                return null;
            }

            System.out.println("How would you like to sort the marketplace?\n 1.Price \n 2.Quantity\n " +
                    "3.Name\n");

            int sort = scanner.nextInt();
            do {
                if (sort == 1) {
                    ArrayList<Double> productPrices= new ArrayList<>();
                    for (Seller seller: sortProducts) {
                        for (Store store : seller.getStores()) {
                            for (Product product : store.getProducts()) {
                                productPrices.add(product.getPrice());
                            }
                        }
                    }
                    Collections.sort(productPrices);

                    String productPricesList = "";
                    for (int i = 0; i < productPrices.size(); i++) {
                        productPricesList = productPricesList + (i+1) + ". $" + productPrices.get(i) + "\n";
                    }

                    return productPricesList;

                } else if (sort == 2) {
                    ArrayList<Integer> productQuantities = new ArrayList<>();
                    for (Seller seller: sortProducts) {
                        for (Store store : seller.getStores()) {
                            for (Product product : store.getProducts()) {
                                productQuantities.add(product.getQuantityForPurchase());
                            }
                        }
                    }
                    Collections.sort(productQuantities);

                    String productQuantityList = "";
                    for (int i = 0; i < productQuantities.size(); i++) {
                        productQuantityList = productQuantityList + (i+1) + ". " + productQuantities.get(i) + "\n";
                    }

                    return productQuantityList;

                } else if (sort == 3) {
                    ArrayList<String> productNames = new ArrayList<>();
                    for (Seller seller: sortProducts) {
                        for (Store store : seller.getStores()) {
                            for (Product product : store.getProducts()) {
                                productNames.add(product.getName());
                            }
                        }
                    }
                    Collections.sort(productNames);

                    String productNameList = "";
                    for (int i = 0; i < productNames.size(); i++) {
                        productNameList = productNameList + (i+1) + ". " + productNames.get(i) + "\n";
                    }

                    return productNameList;

                } else {
                    System.out.println("Enter a valid number to sort the marketplace!");
                }
            } while (sort != 1 && sort != 2 && sort != 3);

        } else if (choice == 2) {
            ArrayList<Seller> searchProducts = readSellerDatabase();
            System.out.println("What would you like to search for?\n1.Name \n2. Store\n3. Description");
            int search = scanner.nextInt();
            scanner.nextLine();

            do{
                if (search == 1) {
                    System.out.println("Enter the name of the product you want to buy.");
                    String nameProd = scanner.nextLine();

                    ArrayList<String> nameProduct = new ArrayList<>();

                    for (Seller seller : searchProducts) {
                        for (Store store : seller.getStores()) {
                            for (Product product : store.getProducts()) {
                                if (product.getName().equalsIgnoreCase(nameProd)) {
                                    nameProduct.add(product.getName());
                                }
                            }
                        }
                        Collections.sort(nameProduct);

                        String nameProductList = "";
                        for (int i = 0; i < nameProduct.size(); i++) {
                            nameProductList = nameProductList + (i + 1) + ". " + nameProduct.get(i) + "\n";
                        }

                        return nameProductList;
                    }
                }

                if (search == 2) { // Store
                    System.out.println("Enter the name of the store you want to buy from.");
                    String storeProd = scanner.nextLine();

                    ArrayList<Product> storeName = new ArrayList<>(); //is it okay is i put

                    for (Seller seller : searchProducts) {
                        for (Store store : seller.getStores()) {
                            if (store.getStoreName().equals(storeProd)) {
                                storeName = store.getProducts();

                                break; // No need to continue searching after finding the store
                            }
                        }
                    }

                    String storeProductList = "";
                    for (int i = 0; i < storeName.size(); i++) {
                        storeProductList = storeProductList + (i + 1) + ". " + storeName.get(i).getName() + "\n";
                    }
                    return storeProductList;
                }
                if (search == 3) { // description

                    System.out.println("Enter the description of the product you want to buy.");
                    String prodDescription = scanner.nextLine();

                    ArrayList<String> descriptionProd = new ArrayList<>();

                    for (Seller seller : searchProducts) {
                        for (Store store : seller.getStores()) {
                            for (Product product : store.getProducts()) {
                                if (product.getDescription().equalsIgnoreCase(prodDescription)) {
                                    //descriptionProd.add(product); // should i just do product or product.getName()
                                    descriptionProd.add(product.getName());

                                }
                            }
                        }
                    }
                    Collections.sort(descriptionProd);

                    String descriptionProductList = "";
                    for (int i = 0; i < descriptionProd.size(); i++) {
                        descriptionProductList = descriptionProductList + (i + 1) + ". " + descriptionProd.get(i) + "\n";
                    }
                    return descriptionProductList;
                }
            } while(search != 1 && search != 2 && search != 3);
        }
        return null;
    }

    public Product viewProduct(String productList, int productNum) {
        String[] productStringArray = productList.split("\n");
        String productName = "";
        for (int i = 0; i < productStringArray.length; i++) {
            if (productNum == i+1) {
                productName = productStringArray[i].split("[. ]")[1];
            }

            // I think this works
            ArrayList<Seller> fetchProduct = readSellerDatabase();
            for (Seller seller: fetchProduct) {
                for (Store store : seller.getStores()) {
                    for (Product product : store.getProducts()) {
                        if (product.getName().equals(productName)) {
                            return product;
                        }
                    }
                }
            }
        }
        return null;
    }

    public Store viewStore(Product product) {
        ArrayList<Seller> fetchStore = readSellerDatabase();
        for (Seller seller: fetchStore) {
            for (Store store : seller.getStores()) {
                if (store.getStoreName().equals(product.getStore())) {
                    return store;
                }
            }
        }
        return null;
    }

    public void addToShoppingCart(Product product, Store store) {
        try {
            for (int i = 0; i < store.getProducts().size(); i++) {
                if (store.getProducts().get(i).equals(product) && store.getProducts().get(i).getQuantityForPurchase() > 0) {
                    shoppingCart.add(product);
                } else {
                    System.out.println("This product does not exist in our store!");
                }
            }
        } catch (NullPointerException e) {
            System.out.println("This store has no toy car models left!");
        }
    }

    public void removeFromShoppingCart(Product product) {
        if (shoppingCart.contains(product)) {
            shoppingCart.remove(product);
            System.out.printf("%s has been removed from the shopping cart!\n", product.getName());
        } else {
            System.out.printf("%s is not in your shopping cart!\n", product.getName());
        }
    }

    public void buyProduct(Product product, int numProductsForPurchase, Store store, Scanner scanner) {
        double total = product.getPrice() * numProductsForPurchase;
        if (product.getQuantityForPurchase() >= numProductsForPurchase) {
            if (balance >= total) {
                balance -= total;
                product.setQuantityForPurchase(product.getQuantityForPurchase() - numProductsForPurchase);
                store.getProducts().get(store.getProducts().indexOf(product)).setQuantityForPurchase(
                        product.getQuantityForPurchase());
                System.out.printf("%d of %s have been bought for $%.2f.\n", numProductsForPurchase, product.getName(), total);
                purchases.add(product);
            } else {
                System.out.println(super.getName() + " cannot afford " + product.getName());
            }

        } else if (product.getQuantityForPurchase() == numProductsForPurchase){
            if (balance >= total) {
                balance -= total;
                product.setQuantityForPurchase(0);
                store.getProducts().get(store.getProducts().indexOf(product)).setQuantityForPurchase(0);
                store.getProducts().remove(product);
                System.out.printf("You got the last %d %ss available!\n", product.getQuantityForPurchase(), product.getName());
                purchases.add(product);
            } else {
                System.out.println(super.getName() + " cannot afford " + product.getName());
            }

        } else if (product.getQuantityForPurchase() < numProductsForPurchase) {
            System.out.printf("There are only %d %ss left in this store\n", product.getQuantityForPurchase(), product.getName());
            System.out.println("Would you like to purchase the remaining number?");

            String yesOrNo = scanner.nextLine();
            if (yesOrNo.equalsIgnoreCase("Yes")) {
                if (balance >= total) {
                    balance -= total;
                    product.setQuantityForPurchase(0);
                    store.getProducts().get(store.getProducts().indexOf(product)).setQuantityForPurchase(0);
                    store.getProducts().remove(product);
                    System.out.printf("You got the last %d %ss available!\n", product.getQuantityForPurchase(), product.getName());
                    purchases.add(product);
                } else {
                    System.out.println(super.getName() + " cannot afford " + product.getName());
                }
            }
        }
    }

    public void purchaseCart() {
        // reads the shopping cart file into an the arrayList and purchases every product
        ArrayList<Product> shoppingCart = new ArrayList<>(); // read file into this
        for (int i = 0; i < shoppingCart.size(); i++) {

        }
    }

    public void writeShoppingCart(ArrayList<Product> shoppingCart) {

    }

    public void writePurchases(ArrayList<Product> purchases) {

    }

    public ArrayList<Product> viewCart() {
        // reads the shopping cart file and returns an arraylist of products that the specific customer has in their cart
        // I'll print out the arraylist in the main so that the user can see every product
        // 1.
        // 2.
        // 3.
    }

    public String viewPurchases() {
        // reads the purchases file and returns a string of the specific buyer's purchases
    }


}
