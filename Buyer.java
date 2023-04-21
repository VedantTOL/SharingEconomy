import javax.lang.model.element.ModuleElement;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.zip.DataFormatException;

/**
 * The buyer class that allows the buyer to perform all the necessary actions within the marketplace. Methods within
 * this class allow the buyer to purchase products, add them to their cart, remove them from their cart, purchase their
 * whole cart, view their cart, and view their purchases. Methods within this class also write to the corresponding files
 * so that data is preserved between logging out and logging in.
 *
 * @author Roger, Somansh, Ethan, Vedant
 * @version April 10, 2023
 */

public class Buyer extends User {
    private double balance;
    private ArrayList<ProductPurchase> shoppingCart;
    private ArrayList<ProductPurchase> purchases;

    public Buyer(int uniqueIdentifier, String email, String password, String name, int age, double balance) {
        super(uniqueIdentifier, email, password, name, age, -1);
        this.balance = balance;
        this.shoppingCart = new ArrayList<ProductPurchase>();
        this.purchases = new ArrayList<ProductPurchase>();
    }

    public Buyer(int uniqueIdentifier) throws NoAccountError {
        super(uniqueIdentifier);
        this.purchases = new ArrayList<ProductPurchase>();
        this.shoppingCart = new ArrayList<ProductPurchase>();
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

    public void addPurchase(ProductPurchase purchase) {
        this.purchases.add(purchase);
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
                if (line == null || line == "") {
                    break;
                }
                char identifier = line.charAt(0);

                if (identifier == '*') {
                    try {
                        buyer = new Buyer(Integer.parseInt(line.split(" ")[1]));
                        database.add(buyer);
                    } catch (NoAccountError e) {
                        return null;
                    }

                } else if (identifier == '+') {
                    try {
                        line = line.substring(2);
                    } catch (StringIndexOutOfBoundsException e) {
                        buyer.setShoppingCart(new ArrayList<ProductPurchase>());
                    }
                    if (line != "") {
                        String[] cartList = line.split(", ");
                        for (String productID : cartList) {
                            try {
                                int tempID = Integer.parseInt(productID.split(":")[0]);
                                int tempQuantity = Integer.parseInt(productID.split(":")[1]);
                                buyer.shoppingCart.add(new ProductPurchase(tempID, tempQuantity));
                            } catch (NumberFormatException e) {
                            }
                        }
                    } else {
                        buyer.setShoppingCart(new ArrayList<ProductPurchase>());
                    }

                } else if (identifier == '-') {
                    try {
                        line = line.substring(2);
                    } catch (StringIndexOutOfBoundsException e) {
                        buyer.setPurchases(new ArrayList<ProductPurchase>());
                    }
                    if (line != "") {
                        String[] purchasedList = line.split(", ");
                        for (String productID : purchasedList) {
                            try {
                                int tempID = Integer.parseInt(productID.split(":")[0]);
                                int tempQuantity = Integer.parseInt(productID.split(":")[1]);
                                buyer.purchases.add(new ProductPurchase(tempID, tempQuantity));
                            } catch (NumberFormatException e) {
                            }
                        }
                    } else {
                        buyer.setPurchases(new ArrayList<ProductPurchase>());
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
/*
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
                    storeIndex = -1;
                    try {
                        seller = new Seller(Integer.parseInt(line.split(" ")[1]));
                        if (seller.getSellerIndex() != -1) {
                            database.add(seller);
                        }
                    } catch (NoAccountError e) {
                        return null;
                    }
                } else if (identifier == 43) {
                    storeIndex++;
                    store = new Store(line.split(" ")[1]);
                    database.get(sellerIndex).addStore(storeIndex, store);
                } else {
                    try {
                        product = new Product(line.split(", "));
                        //System.out.println(storeIndex);
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
 */

    public ArrayList<Product> getProductDatabase() {
        ArrayList<Seller> database = null;
        try {
            database = readSellerDatabase();
        } catch (NoSellers e) {
            return null;
        }
        ArrayList<Product> productDatabase = new ArrayList<Product>();
        if (database == null) {
            return null;
        }
        for (Seller seller : database) {
            for (Store store : seller.getStores()) {
                for (Product product : store.getProducts()) {
                    productDatabase.add(product);
                }
            }
        }
        return productDatabase;
    }

    public ArrayList<Product> viewMarketPlace(int choice, Scanner scanner, ArrayList<Seller> database) {
        if (choice == 1) {

            if (database == null) {
                return null;
            }

            int sort;
            String sorting;
            do {
                do {
                    sorting = JOptionPane.showInputDialog(null, "How would you like to sort the marketplace?\n1. Price \n2. Quantity\n" +
                                    "3. Name",
                            "How would you like to sort? (Please enter the number corresponding to the option)", JOptionPane.QUESTION_MESSAGE);
                    if (sorting == null || sorting.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please enter an option before continuing!",
                                "ERROR!", JOptionPane.ERROR_MESSAGE);
                    }
                } while (sorting == null || sorting.isEmpty());

                sort = readInt(sorting);
            } while (sort == -1);

            do {
                if (sort == 1) {
                    ArrayList<Product> productPrices = new ArrayList<Product>();
                    for (Seller seller : database) {
                        for (Store store : seller.getStores()) {
                            for (Product product : store.getProducts()) {
                                productPrices.add(product);
                            }
                        }
                    }
                    Collections.sort(productPrices, Comparator.comparingDouble(Product::getPrice));
                    if (productPrices.size() == 0) {
                        break;
                    }
                    return productPrices;

                } else if (sort == 2) {
                    ArrayList<Product> productQuantities = new ArrayList<>();
                    for (Seller seller : database) {
                        for (Store store : seller.getStores()) {
                            for (Product product : store.getProducts()) {
                                productQuantities.add(product);
                            }
                        }
                    }
                    if (productQuantities.size() == 0) {
                        break;
                    }

                    Collections.sort(productQuantities, Comparator.comparingInt(Product::getQuantityForPurchase));

                    return productQuantities;

                } else if (sort == 3) {
                    ArrayList<Product> productNames = new ArrayList<Product>();
                    for (Seller seller : database) {
                        for (Store store : seller.getStores()) {
                            for (Product product : store.getProducts()) {
                                productNames.add(product);
                            }
                        }
                    }
                    if (productNames.size() == 0) {
                        break;
                    }

                    Collections.sort(productNames, Comparator.comparing(Product::getName));

                    return productNames;

                } else {
                    JOptionPane.showMessageDialog(null, "Enter a valid number to sort the marketplace!",
                            "ERROR!", JOptionPane.ERROR_MESSAGE);
                }
            } while (sort != 1 && sort != 2 && sort != 3);

        } else if (choice == 2) {
            ArrayList<Seller> searchProducts = database;

            int search;
            String searching;
            do {
                do {
                    searching = JOptionPane.showInputDialog(null, "What would you like to search for?\n1.Name \n2. Store\n3. Description",
                            "How Would you Like to Search? (Please enter the number corresponding to the option)", JOptionPane.QUESTION_MESSAGE);
                    if (searching == null || searching.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please enter an option before continuing!",
                                "ERROR!", JOptionPane.ERROR_MESSAGE);
                    }
                } while (searching == null || searching.isEmpty());

                search = readInt(searching);
            } while (search == -1);

            do {
                if (search == 1) {
                    String nameProd;
                    do {
                        nameProd = JOptionPane.showInputDialog(null, "Enter the name of the product you want to buy.",
                                "Search for Product", JOptionPane.QUESTION_MESSAGE);
                        if (nameProd == null || nameProd.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Please enter a name before continuing!",
                                    "ERROR!", JOptionPane.ERROR_MESSAGE);
                        }
                    } while (nameProd == null || nameProd.isEmpty());

                    ArrayList<Product> nameProduct = new ArrayList<>();

                    for (Seller seller : searchProducts) {
                        for (Store store : seller.getStores()) {
                            for (Product product : store.getProducts()) {
                                if (product.getName().contains(nameProd)) {
                                    nameProduct.add(product);
                                }
                            }
                        }
                        Collections.sort(nameProduct, Comparator.comparing(Product::getName));
                        return nameProduct;
                    }
                }

                if (search == 2) { // Store
                    String storeProd;
                    do {
                        storeProd = JOptionPane.showInputDialog(null, "Enter the name of the store you want to buy from.",
                                "Search for Store", JOptionPane.QUESTION_MESSAGE);
                        if (storeProd == null || storeProd.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Please enter a name before continuing!",
                                    "ERROR!", JOptionPane.ERROR_MESSAGE);
                        }
                    } while (storeProd == null || storeProd.isEmpty());

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
                    String prodDescription;
                    do {
                        prodDescription = JOptionPane.showInputDialog(null, "Enter the description of the product you want to buy.",
                                "Search for Product Description", JOptionPane.QUESTION_MESSAGE);
                        if (prodDescription == null || prodDescription.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Please enter a description before continuing!",
                                    "ERROR!", JOptionPane.ERROR_MESSAGE);
                        }
                    } while (prodDescription == null || prodDescription.isEmpty());

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
            } while (search != 1 && search != 2 && search != 3);
        }
        return null;
    }

    public void writeToBuyer() throws DataFormatException, IOException {

        ArrayList<Buyer> buyerDatabase = readBuyerDatabase();

        if (buyerDatabase.size() != 0) {
            int toReplace = 0;
            for (int i = 0; i < buyerDatabase.size(); i++) {
                if (buyerDatabase.get(i).getUniqueIdentifier() == this.getUniqueIdentifier()) {
                    toReplace = i;
                }
            }
            buyerDatabase.set(toReplace, this);
        } else {
            buyerDatabase.add(this);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("./src/BuyerDatabase.txt"));
            for (Buyer buyer : buyerDatabase) {
                String temp = String.format("* %d\n", buyer.getUniqueIdentifier());
                bw.write(temp);
                if (buyer.getShoppingCart() != null) {
                    bw.write("+ ");
                    StringBuilder tempLine = new StringBuilder();
                    for (ProductPurchase productPurchase : buyer.shoppingCart) {
                        tempLine.append(String.format("%s, ", productPurchase.toString()));
                    }
                    if (tempLine.length() > 2) {
                        tempLine.substring(0, tempLine.length() - 2);
                    }
                    bw.write(String.valueOf(tempLine));
                    bw.write("\n");
                    bw.flush();
                }
                if (buyer.getPurchases() != null && !buyer.getPurchases().isEmpty()) {
                    bw.write("- ");
                    StringBuilder tempLine = new StringBuilder();
                    for (ProductPurchase productPurchase : buyer.purchases) {
                        tempLine.append(String.format("%s, ", productPurchase.toString()));
                    }
                    if (tempLine.length() > 2) {
                        tempLine.substring(0, tempLine.length() - 2);
                    }
                    bw.write(String.valueOf(tempLine));
                    bw.write("\n");
                    bw.flush();
                } else {
                    //System.out.println("You have no purchases!"); //this was added later.
                    JOptionPane.showMessageDialog(null, "You have no purchases!",
                            "ERROR!", JOptionPane.ERROR_MESSAGE);

                }
                bw.flush();
            }
            bw.close();
        } catch (IOException e) {
            //System.out.println("Database Malformed");
            JOptionPane.showMessageDialog(null, "Database Malformed",
                    "ERROR!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Product viewProduct(ArrayList<Product> productList, int productNum) {
        Product selected = productList.get(productNum - 1);
        System.out.println(selected.productPage());
        return selected;
    }

    public Store viewStore(Product product, ArrayList<Seller> fetchStore) {
        for (Seller seller : fetchStore) {
            for (Store store : seller.getStores()) {
                for (Product products : store.getProducts()) {
                    if (product.getUniqueID() == products.getUniqueID()) {
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
                if (store.getProducts().get(i).getUniqueID() == product.getUniqueID() && product.getQuantityForPurchase() > 0) {
                    shoppingCart.add(new ProductPurchase(product.getUniqueID(), quantity));
                } else {
                    //System.out.println("This product does not exist in our store!");
                    JOptionPane.showMessageDialog(null, "This product does not exist in our store!",
                            "ERROR!", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NullPointerException e) {
            //System.out.println("This store has no products left!");
            JOptionPane.showMessageDialog(null, "This store has no products left!",
                    "Out of Stock!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void removeFromShoppingCart(Product product) {
        int index = 0;
        for (int i = 0; i < shoppingCart.size(); i++) {
            if (shoppingCart.get(i).getUniqueID() == product.getUniqueID()) {
                shoppingCart.remove(i);
                //System.out.printf("%s has been removed from the shopping cart!\n", product.getName());
                JOptionPane.showMessageDialog(null, product.getName() + " has been removed from the shopping cart!",
                        "Removed!", JOptionPane.INFORMATION_MESSAGE);
                index++;
            }
        }
        if (index == 0) {
            //System.out.printf("%s is not in your cart.\n", product.getName());
            JOptionPane.showMessageDialog(null, product.getName() + " is not in your cart.",
                    "ERROR!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void writeToDatabase(boolean newSeller, ArrayList<Seller> database) {
        //ArrayList<Seller> database = readSellerDatabase();
        if (database == null) {
            return;
        }

        ArrayList<Seller> removeDuplicates = new ArrayList<Seller>();
        for (Seller seller : database) {
            if (seller.getSellerIndex() != -1) {
                removeDuplicates.add(seller.getSellerIndex(), seller);
            }
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("./src/SellerDatabase.txt"));
            for (Seller seller : removeDuplicates) {
                String temp = String.format("* %d\n", seller.getSellerIndex());
                bw.write(temp);
                if (seller.getStores() != null) {
                    for (Store store : seller.getStores()) {
                        bw.write(String.format("+ %s\n", store.getStoreName()));
                        for (Product product : store.getProducts()) {
                            bw.write(product.toDatabase());
                            bw.write("\n");
                        }
                    }
                }
                bw.flush();
            }
            bw.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Database Malformed",
                    "ERROR!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void buyProduct(Product product, int numProductsForPurchase, Store store, Scanner scanner, ArrayList<Seller> database) {
        boolean success = false;
        int index = 0;
        for (int i = 0; i < store.getProducts().size(); i++) {
            if (store.getProducts().get(i).getUniqueID() == product.getUniqueID()) {
                index = store.getProducts().indexOf(store.getProducts().get(i));
            }
        }
        if (product.getQuantityForPurchase() > 0) {
            double total = product.getPrice() * numProductsForPurchase;
            if (product.getQuantityForPurchase() > numProductsForPurchase) {
                if (balance >= total) {
                    balance -= total;
                    product.setQuantityForPurchase(product.getQuantityForPurchase() - numProductsForPurchase);
                    store.getProducts().get(index).setQuantityForPurchase(product.getQuantityForPurchase());
                    //System.out.printf("%d of %s have been bought for $%.2f.\n", numProductsForPurchase, product.getName(), total);
                    JOptionPane.showMessageDialog(null, numProductsForPurchase + " of " + product.getName() +
                            " have been bought for " + total + ".",
                            "Yay!", JOptionPane.INFORMATION_MESSAGE);
                    product.setQuantitySold(product.getQuantitySold() + numProductsForPurchase);
                    purchases.add(new ProductPurchase(product.getUniqueID(), numProductsForPurchase));

                    success = true;
                } else {
                    //System.out.println(super.getName() + " cannot afford " + product.getName());
                    JOptionPane.showMessageDialog(null, super.getName() + " cannot afford " + product.getName(),
                            "Oh no! :(", JOptionPane.ERROR_MESSAGE);
                }

            } else if (product.getQuantityForPurchase() == numProductsForPurchase) {
                if (balance >= total) {
                    balance -= total;
                    product.setQuantityForPurchase(0);
                    store.getProducts().get(index).setQuantityForPurchase(0);
                    store.getProducts().remove(product);
                   // System.out.printf("You got the last %d %ss available!\n", numProductsForPurchase, product.getName());
                    JOptionPane.showMessageDialog(null, "You got the last " + numProductsForPurchase +
                            " " + product.getName() + "s" + "available!", "Yay!", JOptionPane.INFORMATION_MESSAGE);
                    purchases.add(new ProductPurchase(product.getUniqueID(), numProductsForPurchase));
                    success = true;
                } else {
                    //System.out.println(super.getName() + " cannot afford " + product.getName());
                    JOptionPane.showMessageDialog(null, super.getName() + " cannot afford " + product.getName(),
                            "Oh no! :(", JOptionPane.ERROR_MESSAGE);
                }

            } else if (product.getQuantityForPurchase() < numProductsForPurchase) {
                int productsavail = product.getQuantityForPurchase() + 1;
                if (product.getQuantityForPurchase() == 1) {
                    //System.out.println("There is only " + productsavail + " " + product.getName() + "left in this store!");
                    JOptionPane.showMessageDialog(null, "There is only " + productsavail + " " +
                            product.getName() + "left in this store!", "Low Stock!", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    //System.out.println("There are only " + productsavail + " " + product.getName() + "s left in this store!");
                    JOptionPane.showMessageDialog(null, "There are only " + productsavail + " " +
                            product.getName() + "left in this store!", "Low Stock!", JOptionPane.INFORMATION_MESSAGE);
                }
                String yesOrNo;
                do {
                    yesOrNo = JOptionPane.showInputDialog(null, "Would you like to purchase the remaining number?",
                            "Purchase Remaining Number", JOptionPane.QUESTION_MESSAGE);
                } while (yesOrNo == null || yesOrNo.isEmpty());

                if (yesOrNo.equalsIgnoreCase("Yes")) {
                    if (balance >= total) {
                        balance -= total;
                        product.setQuantityForPurchase(0);
                        store.getProducts().get(index).setQuantityForPurchase(0);
                        store.getProducts().remove(product);
                        //System.out.printf("You got the last %d %ss available!\n", numProductsForPurchase, product.getName());
                        JOptionPane.showMessageDialog(null, "You got the last " + numProductsForPurchase +
                                " " + product.getName() + "s" + "available!", "Yay!", JOptionPane.INFORMATION_MESSAGE);
                        purchases.add(new ProductPurchase(product.getUniqueID(), numProductsForPurchase));
                        success = true;
                    } else {
                        JOptionPane.showMessageDialog(null, super.getName() + " cannot afford " + product.getName(),
                                "Oh no! :(", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            //System.out.printf("%s is out of stock!\n", product.getName());
            JOptionPane.showMessageDialog(null, product.getName() + " is out of stock!",
                    "Out of Stock!", JOptionPane.ERROR_MESSAGE);
        }
        if (success) {

            Seller toEdit = null;
            boolean completed = false;
            for (Seller seller : database) {
                for (Store storeX : seller.getStores()) {
                    for (Product productX : storeX.getProducts()) {
                        if (productX.getUniqueID() == product.getUniqueID()) {
                            toEdit = seller;
                            ArrayList<Store> tempStores = toEdit.getStores();
                            Store tempStore = storeX;

                            ArrayList<Product> tempProduct = tempStore.getProducts();
                            Product tempBuy = productX;
                            tempProduct.remove(tempBuy);

                            tempProduct.add(product);
                            tempStore.setProducts(tempProduct);

                            tempStores.remove(tempStore);
                            tempStores.add(tempStore);
                            toEdit.setStores(tempStores);
                            completed = true;
                            break;
                        }
                        if (completed) break;
                    }
                    if (completed) break;
                }
                if (completed) break;
            }
            database.remove(toEdit.getSellerIndex());
            database.add(toEdit.getSellerIndex(), toEdit);
            this.writeToDatabase(false, database);
        }
    }

    public int purchaseCart(ArrayList<Seller> updated) {
        ArrayList<ProductPurchase> shoppingCartFile = viewCart();
        if (shoppingCartFile == null) {
            shoppingCartFile = new ArrayList<ProductPurchase>();
        }

        shoppingCart.addAll(shoppingCartFile);

        double totalSum = 0;
        for (ProductPurchase productPurchase : shoppingCart) {
            totalSum = totalSum + (productPurchase.getPrice() * productPurchase.getOrderQuantity());
        }

        if (totalSum <= balance) {
            for (ProductPurchase productPurchase : shoppingCart) {
                if (productPurchase.getOrderQuantity() <= productPurchase.getQuantityForPurchase()) {
                    productPurchase.setQuantityForPurchase(productPurchase.getQuantityForPurchase() - productPurchase.getOrderQuantity());
                }
            }


            Seller toEdit = null;
            boolean completed = false;
            for (ProductPurchase productPurchase : shoppingCart) {
                completed = false;
                for (Seller seller : updated) {
                    for (Store store : seller.getStores()) {

                        for (Product product : store.getProducts()) {
                            if (productPurchase.getUniqueID() == product.getUniqueID()) {
                                toEdit = seller;
                                ArrayList<Store> tempStores = toEdit.getStores();
                                Store tempStore = store;

                                ArrayList<Product> tempProduct = tempStore.getProducts();
                                Product tempBuy = productPurchase;
                                tempProduct.remove(product);

                                tempProduct.add(tempBuy);
                                tempStore.setProducts(tempProduct);

                                tempStores.remove(store);
                                tempStores.add(tempStore);
                                seller.setStores(tempStores);
                                completed = true;
                                break;
                            }
                            if (completed) break;
                        }
                        if (completed) break;
                    }
                    if (completed) break;
                }

            }
            updated.remove(toEdit.getSellerIndex());
            updated.add(toEdit.getSellerIndex(), toEdit);

            purchases.addAll(shoppingCart);
            shoppingCart.removeAll(purchases);
            balance = balance - totalSum;
            //System.out.println("Thank you for your purchases!");
            JOptionPane.showMessageDialog(null, "Thank you for your purchases!", "Yay!", JOptionPane.INFORMATION_MESSAGE);

            writeToDatabase(false, updated);
            return 0;

        } else if (totalSum > balance) {
//            System.out.println("ERROR! Transaction denied. Your balance is less than the total price of your cart.");
//            System.out.println("You will need to remove products until the total price is less than or equal to your balance");
//            System.out.printf("Total Price: %.2f\n", totalSum);
//            System.out.printf("Balance: %.2f\n", balance);
            JOptionPane.showMessageDialog(null, "ERROR! Transaction denied.\n Your balance is less than the total price of your cart.\n" +
                    "You will need to remove products until the total price is less than or equal to your balance\n" +
                    "Total Price: " + totalSum + "\n" + "Balance: " + balance + "\n", "Yay!", JOptionPane.INFORMATION_MESSAGE);

            return 1;
        }

        return 0;
    }


    public ArrayList<ProductPurchase> viewCart() {
        ArrayList<ProductPurchase> shoppingCart = null;

        try {
            ArrayList<Buyer> buyers = readBuyerDatabase();
            for (Buyer buyer : buyers) {
                if (buyer.getUniqueIdentifier() == getUniqueIdentifier()) {
                    shoppingCart = buyer.getShoppingCart();
                }
            }

        } catch (IOException | DataFormatException e) {
            throw new RuntimeException();
        }
        return shoppingCart;
    }

    public ArrayList<ProductPurchase> viewPurchases() {
        ArrayList<ProductPurchase> purchases = null;

        try {
            ArrayList<Buyer> buyers = readBuyerDatabase();
            for (Buyer buyer : buyers) {
                if (buyer.getUniqueIdentifier() == getUniqueIdentifier()) {
                    purchases = buyer.getPurchases();
                }
            }

        } catch (IOException | DataFormatException e) {
            throw new RuntimeException();
        }
        return purchases;
    }

    public Seller shopBySeller(Scanner scanner, ArrayList<Seller> databaseSeller) {

        // add a do while to take into account "No seller found with the name: "


        //System.out.println("What is the name of the seller you want to buy from?");
        String nameSeller = JOptionPane.showInputDialog(null,
                "What is the name of the seller you want to buy from?",
                "Search for Seller Name", JOptionPane.QUESTION_MESSAGE);


        // Find the seller object that matches the entered name
        Seller seller = null;
        for (Seller s : databaseSeller) {
            if (s.getName().equalsIgnoreCase(nameSeller)) {
                seller = s;
                return seller;
            }
        }

        if (seller == null) {
            //System.out.println("No seller found with the name: " + nameSeller);
            JOptionPane.showMessageDialog(null, "No seller found with the name: " + nameSeller,
                    "No Sellers found!", JOptionPane.ERROR_MESSAGE);
        }

        return null;
    }


    public ArrayList<Product> shopByStore(Seller seller, Scanner scanner) {

        ArrayList<Store> shopSeller = seller.getStores();

        //System.out.println("Enter the name of the shop you want to see products for:");
        String shopName = JOptionPane.showInputDialog(null,
                "Enter the name of the shop you want to see products for",
                "Search for Store Name", JOptionPane.QUESTION_MESSAGE);

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
            //System.out.println("Please enter a valid Integer!");
            JOptionPane.showMessageDialog(null, "Please enter a valid Integer!",
                    "ERROR!", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }

}
