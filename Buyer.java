import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.zip.DataFormatException;

public class Buyer extends User {
    private double balance;
    private ArrayList<ProductPurchase> shoppingCart;
    private ArrayList<ProductPurchase> purchases;

    public Buyer(int uniqueIdentifier, String email, String password, String name, int age, double balance) {
        super(uniqueIdentifier, email, password, name, age, -1);
        this.balance = balance;
        this.shoppingCart = null;
        this.purchases = null;
    }

    public Buyer(int uniqueIdentifier) {
        super(uniqueIdentifier);
        this.shoppingCart = new ArrayList<ProductPurchase>();
        this.purchases = new ArrayList<ProductPurchase>();
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public ArrayList<ProductPurchase> getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ArrayList<ProductPurchase> shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public ArrayList<ProductPurchase> getPurchases() {
        return purchases;
    }

    public void setPurchases(ArrayList<ProductPurchase> purchases) {
        this.purchases = purchases;
    }

    public ArrayList<Buyer> readBuyerDatabase() throws DataFormatException, IOException {
        ArrayList<Buyer> database = new ArrayList<Buyer>();
        ArrayList<Product> productDatabase = getProductDatabase();
        String line;
        Buyer buyer = null;

        BufferedReader bfr = null;
        try {
            bfr = new BufferedReader(new FileReader("./src/BuyerDatabase.txt"));
            while (true) {
                line = bfr.readLine();
                if (line == null) {
                    break;
                }
                char identifier = line.charAt(0);

                if (identifier == 42) {
                    buyer = new Buyer(Integer.parseInt(line.split(" ")[1]));
                    database.add(buyer);
                } else if (identifier == 43) {
                    line = line.substring(1);
                    String[] cartList = line.split(", ");
                    for (String productID: cartList) {
                        int tempID = Integer.parseInt(productID.split(":")[0]);
                        int tempQuantity = Integer.parseInt(productID.split(":")[1]);
                        for (Product product: productDatabase) {
                            if (tempID == product.getUniqueID()){
                                buyer.shoppingCart.add(new ProductPurchase(product.getUniqueID(), tempQuantity));
                            }
                        }

                    }

                } else if (identifier == '+') {
                    line = line.substring(1);
                    String[] purchasedList = line.split(", ");
                    for (String productID: purchasedList) {
                        int tempID = Integer.parseInt(productID.split(":")[0]);
                        int tempQuantity = Integer.parseInt(productID.split(":")[1]);
                        for (Product product: productDatabase) {
                            if (tempID == product.getUniqueID()){
                                buyer.purchases.add(new ProductPurchase(product.getUniqueID(), tempQuantity));
                            }
                        }
                    }
                }

            }


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return database;

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

    public ArrayList<Product> getProductDatabase() {
        ArrayList<Seller> database= readSellerDatabase();
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

    public ArrayList<Product> viewMarketPlace(int choice, Scanner scanner) {
        if (choice == 1) {
            ArrayList<Seller> sortProducts = readSellerDatabase();
            if (sortProducts == null) {
                return null;
            }

            int sort;
            do {
                System.out.println("How would you like to sort the marketplace?\n1. Price \n2. Quantity\n " +
                        "3. Name\n");
                String sorting = scanner.nextLine();
                sort = readInt(sorting);
            } while (sort == -1);

            do {
                if (sort == 1) {
                    ArrayList<Product> productPrices= new ArrayList<Product>();
                    for (Seller seller: sortProducts) {
                        for (Store store : seller.getStores()) {
                            for (Product product : store.getProducts()) {
                                productPrices.add(product);
                            }
                        }
                    }
                    Collections.sort(productPrices, Comparator.comparingDouble(Product::getPrice));

                    return productPrices;

                } else if (sort == 2) {
                    ArrayList<Product> productQuantities = new ArrayList<>();
                    for (Seller seller: sortProducts) {
                        for (Store store : seller.getStores()) {
                            for (Product product : store.getProducts()) {
                                productQuantities.add(product);
                            }
                        }
                    }
                    Collections.sort(productQuantities, Comparator.comparingInt(Product::getQuantityForPurchase) );

                    return productQuantities;

                } else if (sort == 3) {
                    ArrayList<Product> productNames = new ArrayList<Product>();
                    for (Seller seller: sortProducts) {
                        for (Store store : seller.getStores()) {
                            for (Product product : store.getProducts()) {
                                productNames.add(product);
                            }
                        }
                    }
                    Collections.sort(productNames, Comparator.comparing(Product::getName));

                    return productNames;

                } else {
                    System.out.println("Enter a valid number to sort the marketplace!");
                }
            } while (sort != 1 && sort != 2 && sort != 3);

        } else if (choice == 2) {
            ArrayList<Seller> searchProducts = readSellerDatabase();

            int search;
            do {
                System.out.println("What would you like to search for?\n1.Name \n2. Store\n3. Description");
                String searching = scanner.nextLine();
                search = readInt(searching);
            } while (search == -1);

            do{
                if (search == 1) {
                    System.out.println("Enter the name of the product you want to buy.");
                    String nameProd = scanner.nextLine();

                    ArrayList<Product> nameProduct = new ArrayList<>();

                    for (Seller seller : searchProducts) {
                        for (Store store : seller.getStores()) {
                            for (Product product : store.getProducts()) {
                                if (product.getName().contains(nameProd)) {
                                    nameProduct.add(product);
                                }
                            }
                        }
                        Collections.sort(nameProduct,Comparator.comparing(Product::getName));
                        return nameProduct;
                    }
                }

                if (search == 2) { // Store
                    System.out.println("Enter the name of the store you want to buy from.");
                    String storeProd = scanner.nextLine();

                    Store storeName = null; //is it okay is i put

                    for (Seller seller : searchProducts) {
                        for (Store store : seller.getStores()) {
                            if (store.getStoreName().equals(storeProd)) {
                                storeName = store;
                            }
                        }
                    }

                    return storeName.getProducts();
                }
                if (search == 3) { // description

                    System.out.println("Enter the description of the product you want to buy.");
                    String prodDescription = scanner.nextLine();

                    ArrayList<Product> descriptionProd = new ArrayList<Product>();

                    for (Seller seller : searchProducts) {
                        for (Store store : seller.getStores()) {
                            for (Product product : store.getProducts()) {
                                if (product.getDescription().contains(prodDescription)) {
                                    //descriptionProd.add(product); // should i just do product or product.getName()
                                    descriptionProd.add(product);
                                }
                            }
                        }
                    }
                    Collections.sort(descriptionProd, Comparator.comparing(Product::getName));


                    return descriptionProd;
                }
            } while(search != 1 && search != 2 && search != 3);
        }
        return null;
    }

    public void writeToBuyer() throws DataFormatException, IOException {
        ArrayList<Buyer> buyerDatabase = readBuyerDatabase();
        buyerDatabase.remove(this.getUniqueIdentifier());
        buyerDatabase.add(this.getUniqueIdentifier(), this);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("./src/BuyerDatabase.txt"));
            for (Buyer buyer: buyerDatabase) {
                String temp = String.format("* %d\n", buyer.getUniqueIdentifier());
                bw.write(temp);
                if (buyer.getShoppingCart() != null) {
                    bw.write("+ ");
                    for (ProductPurchase productPurchase : buyer.shoppingCart) {
                        bw.write(productPurchase.toString());
                        bw.write(", ");
                        bw.flush();
                    }
                    bw.write("\n");
                }
                if (buyer.getPurchases() != null) {
                    bw.write("- ");
                    for (ProductPurchase productPurchase : buyer.purchases) {
                        bw.write(productPurchase.toString());
                        bw.write(", ");
                        bw.flush();
                    }
                    bw.write("\n");
                }
                bw.flush();
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("Database Malformed");
        }
    }

    public Product viewProduct(ArrayList<Product> productList, int productNum) {
        Product selected = productList.get(productNum);
        System.out.println(selected.productPage());
        return selected;
    }

    public Store viewStore(Product product) {
        ArrayList<Seller> fetchStore = readSellerDatabase();
        for (Seller seller: fetchStore) {
            for (Store store : seller.getStores()) {
                for (Product products : store.getProducts()) {
                    if (product.equals(products)) {
                        return store;
                    }
                }
            }
        }
        return null;
    }

    public void addToShoppingCart(Product product, Store store, int quantity) {
        try {
            for (int i = 0; i < store.getProducts().size(); i++) {
                if (store.getProducts().get(i).equals(product) && store.getProducts().get(i).getQuantityForPurchase() > 0) {
                    shoppingCart.add(new ProductPurchase(product.getUniqueID(), quantity));
                } else {
                    System.out.println("This product does not exist in our store!");
                }
            }
        } catch (NullPointerException e) {
            System.out.println("This store has no toy car models left!");
        }
    }

    public void removeFromShoppingCart(Product product) {
        int index = 0;
        for (int i = 0; i < shoppingCart.size(); i++) {
            if (shoppingCart.get(i).getUniqueID() == product.getUniqueID()) {
                shoppingCart.remove(i);
                System.out.printf("%s has been removed from the shopping cart!\n", product.getName());
                index++;
            }
        }
        if (index == 0) {
            System.out.printf("%s is not in your cart.\n", product.getName());
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
                purchases.add(new ProductPurchase(product.getUniqueID(), numProductsForPurchase));
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
                purchases.add(new ProductPurchase(product.getUniqueID(), numProductsForPurchase));
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
                    purchases.add(new ProductPurchase(product.getUniqueID(), numProductsForPurchase));
                } else {
                    System.out.println(super.getName() + " cannot afford " + product.getName());
                }
            }
        }
    }

    public int purchaseCart() {

        ArrayList<ProductPurchase> shoppingCart = viewCart();
        double totalSum = 0;
        for (ProductPurchase productPurchase : shoppingCart) {
            totalSum = totalSum + productPurchase.getPrice();
        }

        if (totalSum <= balance) {
            purchases.addAll(shoppingCart);
            shoppingCart.removeAll(purchases);
            balance = balance - totalSum;
            System.out.println("Thank you for your purchases!");
            return 0;

        } else if (totalSum > balance) {
            System.out.println("ERROR! Transaction denied. Your balance is less than the total price of your cart.");
            System.out.println("You will need to remove products until the total price is less than or equal to your balance");
            System.out.printf("Total Price: %.2f\n", totalSum);
            System.out.printf("Balance: %.2f\n", balance);
            return 1;

        }
        return 0;
    }


    public ArrayList<ProductPurchase> viewCart() {
        try {
            ArrayList<Buyer> buyers = readBuyerDatabase();
            for (Buyer buyer : buyers) {
                if (buyer.getUniqueIdentifier() == getUniqueIdentifier()) {
                    return buyer.getShoppingCart();
                }
            }

        } catch (IOException | DataFormatException e) {
            throw new RuntimeException();
        }
        return null;
    }
    public ArrayList<ProductPurchase> viewPurchases() {
        try {
            ArrayList<Buyer> buyers = readBuyerDatabase();
            for (Buyer buyer : buyers) {
                if (buyer.getUniqueIdentifier() == getUniqueIdentifier()) {
                    return buyer.getPurchases();
                }
            }

        } catch (IOException | DataFormatException e) {
            throw new RuntimeException();
        }
        return null;
    }

    public Seller shopBySeller(Scanner scanner) {

        // add a do while to take into account "No seller found with the name: "

        ArrayList<Seller> shopSeller = readSellerDatabase();

        System.out.println("What is the name of the seller you want to buy from?");
        String nameSeller = scanner.nextLine();


        // Find the seller object that matches the entered name
        Seller seller = null;
        for (Seller s : shopSeller) {
            if (s.getName().equalsIgnoreCase(nameSeller)) {
                seller = s;
                return seller;
            }
        }

        if (seller == null) {
            System.out.println("No seller found with the name: " + nameSeller);
        }

        return null;
    }


    public ArrayList<Product> shopByStore(Seller seller, Scanner scanner) {

        ArrayList<Store> shopSeller = seller.getStores();

        System.out.println("Enter the name of the shop you want to see products for:");
        String shopName = scanner.nextLine();

        // Find the store object that matches the entered name
        for (Store store : shopSeller) {
            if (shopName.equalsIgnoreCase(store.getStoreName())) {
                return store.getProducts();
            }
        }

        return null;
    }

    public static int readInt(String input) {
        int result;
        try {
            result = Integer.parseInt(input);
            return result;
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid Integer!");
            return -1;
        }
    }

}
