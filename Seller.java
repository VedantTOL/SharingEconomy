import javax.swing.*;
import java.util.ArrayList;
import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.zip.DataFormatException;
import java.util.*;
/**
 * The seller class allows a seller to perform all the necessary actions to post stores and products within the
 * marketplace. Methods within the class allow the seller to post a number of stores of their choosing and a number of
 * products of their choosing. Methods in this class also allow the seller to add stores, edit stores, and delete stores.
 * Methods in this class also read and write to the associated text files, allowing for data to be preserved between
 * logging in and logging out.
 *
 *
 * @author Roger, Somansh, Ethan, Vedant
 * @version June 13, 2022
 */

public class Seller extends User {
    private ArrayList<Store> stores;
    private boolean isSeller(int sellerIndex) {
        return (sellerIndex != -1);
    }
    public Seller(int uniqueIdentifier, String email, String password, String name, int age, ArrayList<Store> stores) {
        super(uniqueIdentifier, email, password, name, age);
        this.stores = stores;
    }
    public Seller(int uniqueIdentifier){
        super(uniqueIdentifier, true);
        this.stores = new ArrayList<Store>();
    }
    public Seller (String[] userDetails) throws UserDatabaseFormatError {
        super(userDetails);
    }
    public Seller() {
        this.stores = null;
    }
    public int getNewIndex(boolean newSeller) {
        try {
            ArrayList<Seller> database = readSellerDatabase();
            if (newSeller) {
                return database.size() + 1;
            } else {
                return 0;
            }
        } catch (NoSellers e) {
            return 0;
        }
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
                                buyer.getShoppingCart().add(new ProductPurchase(tempID, tempQuantity));
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
                            int tempID = Integer.parseInt(productID.split(":")[0]);
                            int tempQuantity = Integer.parseInt(productID.split(":")[1]);
                            buyer.addPurchase(new ProductPurchase(tempID, tempQuantity));

                        /*
                        for (Product product: productDatabase) {
                            if (tempID == product.getUniqueID()){

                        }

                         */
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
    public void addStore(int storeIndex, Store store) {

        try {
            this.stores.add(storeIndex, store);
        } catch (IndexOutOfBoundsException e) {
            this.stores.add(store);
        }


    }
    public ArrayList<Store> getStores() {
        return stores;
    }
    public ArrayList<Product> getStore(int index) {
        return stores.get(index).getProducts();
    }
    public void setStores(ArrayList<Store> stores) {
        this.stores = stores;
    }

//    public void sellerMenu(Scanner scanner, boolean newSeller) throws DataFormatException, IOException {
//        int decision;
//        ArrayList<Seller> updateDatabase = null;
//        try {
//             updateDatabase = readSellerDatabase();
//        } catch (NoSellers e) {
//            newSeller = true;
//        }
//
//        if (updateDatabase == null) {
//            this.setStores(new ArrayList<Store>());
//        } else {
//            try {
//                this.setStores(updateDatabase.get(getSellerIndex()).getStores());
//            } catch (IndexOutOfBoundsException e) {
//                this.setStores(new ArrayList<Store>());
//            }
//        }
//        boolean quit = false;
//
//        while (!quit) {
//            while (true) {
//                //writeToDatabase(true, updateDatabase);
//                System.out.println("What actions would you like to take?\n" +
//                        "1. Add store\n" +
//                        "2. Delete" +
//                        " store\n" +
//                        "3. Edit store\n" +
//                        "4. View statistics\n" +
//                        "5. Edit account\n" +
//                        "6. Delete account\n7. Logout");
//                try {
//                    decision = scanner.nextInt();
//                    scanner.nextLine();
//                    if (decision != 1 && decision != 2 && decision != 3 && decision != 4 && decision != 5
//                            && decision != 6 && decision != 7) {
//                        System.out.println("Please enter a valid option!");
//                    } else {
//                        break;
//                    }
//                } catch (InputMismatchException e) {
//                    System.out.println("Please enter a valid option!");
//                }
//            }
//
//            if (decision == 1) { //Add store
//                ArrayList<Product> products = null;
//                System.out.println("What is the name of the store you want to add?");
//                String storeName = scanner.nextLine();
//
//                System.out.println("How many products do you want to add?");
//                int items = scanner.nextInt();
//                scanner.nextLine();
//
//                products = addProducts(items);
//
//                Store store = new Store(storeName, products);
//                this.addStore(-1, store);
//
//                for (Iterator<Seller> it = updateDatabase.iterator(); it.hasNext(); ) {
//                    Seller seller = it.next();
//                    if (seller.getUniqueIdentifier() == this.getUniqueIdentifier()) {
//                        it.remove();
//                    }
//                }
//                updateDatabase.add(this);
//                writeToDatabase(false, updateDatabase); //
//            } else if (decision == 2) {//Delete Store
//                int i = 1;
//
//                if(this.getStores().size()==0) {
//                    System.out.println("You have no stores, please add one!");
//                }
//                else {
//
//                    for (Store store : this.getStores()) {
//                        System.out.printf("%d: %s\n", i, store.getStoreName());
//                        i++;
//                    }
//
//
//                    while (true) {
//                        System.out.println("Enter the store index you want to delete: ");
//                        String storeNameToDelete = scanner.nextLine();
//                        int toDelete = readInt(storeNameToDelete);
//                        if (toDelete != -1) {
//                            this.getStores().remove(toDelete - 1);
//                            System.out.println("Store successfully deleted!");
//                            break;
//                        }
//                    }
//                }
//                updateDatabase.set(this.getSellerIndex(), this);
//                writeToDatabase(false, updateDatabase);
//            } else if (decision == 3) { // Edit Store
//                Store edit = null;
//                int i = 1;
//
//                if(this.getStores().size()==0) {
//                    System.out.println("You have no stores, please add one!");
//                }
//
//                else {
//
//                    for (Store store : this.getStores()) {
//                        System.out.printf("%d: %s\n", i, store.getStoreName());
//                        i++;
//                    }
//
//                    while (true) {
//                        System.out.println("Enter the store index you want to edit: ");
//                        String storeNameToDelete = scanner.nextLine();
//                        int toEdit = readInt(storeNameToDelete);
//                        if (toEdit != -1) {
//                            edit = this.getStores().get(toEdit - 1);
//                            this.getStores().remove(toEdit - 1);
//                            break;
//                        }
//                    }
//                }
//
//                while (true) {
//                    System.out.printf("What would you like to change about %s\n", edit.getStoreName());
//                    System.out.println("1. Store Name");
//                    System.out.println("2. Add Products");
//                    System.out.println("3. Edit Products");
//                    System.out.println("4. Delete Products");
//
//                    String option = scanner.nextLine();
//                    int toEdit = readInt(option);
//                    if (toEdit != -1) {
//                        if (toEdit == 1) { //Store Name
//                            while (true) {
//                                System.out.println("Enter the new name of the Store: ");
//                                String newName = scanner.nextLine();
//                                System.out.println("New store name printer successfully.");
//                                if (newName == null) {
//                                    System.out.println("Please enter a valid String (cannot be empty!)");
//                                } else {
//                                    edit.setStoreName(newName);
//                                    break;
//                                }
//                            }
//                            this.getStores().add(edit);
//                        } else if (toEdit == 3) { // Edit Products
//                            Product productEdit = null;
//                            int k = 1;
//                            for (Product product : edit.getProducts()) {
//                                System.out.printf("%d: %s\n", k, product.getName());
//                                k++;
//                            }
//                            while (true) {
//                                System.out.println("Enter the product index you want to edit: ");
//                                String productToEdit = scanner.nextLine();
//                                int x = readInt(productToEdit);
//                                if (x != -1) {
//                                    productEdit = edit.getProducts().get(x - 1);
//                                    edit.getProducts().remove(x - 1);
//                                    break;
//                                }
//                            }
//                            boolean exitCondition = true;
//                            do {
//                                System.out.printf("What would you like to edit about %s\n", productEdit.getName());
//                                System.out.printf("1. Name\n2. Description\n3. Price\n4. Quantity For Purchase\n");
//                                int productChoice = readInt(scanner.nextLine());
//                                if (productChoice != -1) {
//                                    if (productChoice == 1) {
//                                        while (true) {
//                                            System.out.println("Enter the new name of the Product: ");
//                                            String newName = scanner.nextLine();
//                                            if (newName == null) {
//                                                System.out.println("Please enter a valid String (cannot be empty!)");
//                                            } else {
//                                                productEdit.setName(newName);
//                                                System.out.println("Product name updated successfully!");
//                                                break;
//                                            }
//                                        }
//                                    } else if (productChoice == 2) {
//                                        while (true) {
//                                            System.out.println("Enter the new description of the product: ");
//                                            String newName = scanner.nextLine();
//                                            if (newName == null) {
//                                                System.out.println("Please enter a valid String (cannot be empty!)");
//                                            } else {
//                                                productEdit.setDescription(newName);
//                                                System.out.println("Product description updated successfully!");
//                                                break;
//                                            }
//                                        }
//                                    } else if (productChoice == 3) {
//                                        while (true) {
//                                            System.out.println("Enter the new price of the Product: ");
//                                            double newPrice = readDouble(scanner.nextLine());
//                                            if (newPrice != -1) {
//                                                if (newPrice < 0) {
//                                                    System.out.println("Please enter a valid Price (cannot be less than 0!)");
//                                                } else {
//                                                    productEdit.setPrice(newPrice);
//                                                    System.out.println("Price was updated successfully!");
//                                                    break;
//                                                }
//                                            }
//                                        }
//                                    } else if (productChoice == 4) {
//                                        while (true) {
//                                            System.out.println("How much stock is available?");
//                                            int newStock = readInt(scanner.nextLine());
//                                            if (newStock < 0) {
//                                                System.out.println("Please enter a number greater than 0!");
//                                            } else {
//                                                productEdit.setQuantityForPurchase(newStock);
//                                                break;
//                                            }
//                                        }
//
//                                    } else {
//                                        System.out.println("Please enter a valid choice!");
//                                        exitCondition = false;
//                                    }
//
//                                }
//                            } while (!exitCondition);
//                            edit.getProducts().add(productEdit);
//                            this.getStores().add(edit);
//                        } else if (toEdit == 2) {
//                            System.out.println("How many products do you want to add?");
//                            int items = scanner.nextInt();
//                            scanner.nextLine();
//                            edit.setProducts(addProducts(items));
//                            this.getStores().add(edit);
//                        } else if (toEdit == 4) {
//                            Product productDelete = null;
//                            int k = 1;
//                            for (Product product : edit.getProducts()) {
//                                System.out.printf("%d: %s\n", k, product.getName());
//                                k++;
//                            }
//                            while (true) {
//                                System.out.println("Enter the product index you want to delete: ");
//                                String productToDelete = scanner.nextLine();
//                                int x = readInt(productToDelete);
//                                if (x != -1) {
//                                    edit.getProducts().remove(x - 1);
//                                    break;
//                                }
//                            }
//                            this.getStores().add(edit);
//                        }
//                    }
//                    break;
//                }
//                updateDatabase.set(this.getSellerIndex(), this);
//                writeToDatabase(false, updateDatabase);
//            } else if (decision == 4) {
//                getSellerStatistics();
//            } else if (decision == 5) {
//                this.changeAccount(scanner, true);
//            } else if (decision == 6) {
//                boolean x = this.deleteAccount(scanner, true);
//                if (x) {
//                    System.out.println("Logging you out...");
//                    return;
//                }
//            } else if (decision == 7) {
//                System.out.println("Thank you for shopping with us!");
//                return;
//            }
//        }
//    }

    public String productName(int items) {
        String out = String.format("Enter the ");
        String suffix = null;

        int num = items + 1;
        out.concat(String.valueOf(num));
        String numCode = Integer.toString(num);
        String lastChar = numCode.substring(numCode.length() - 1); // the last character of the string
        if (numCode.length() > 1) {
            numCode = numCode.substring(numCode.length() - 2);
            if (numCode.charAt(0) == '1') {
                suffix = "th";
            } else {
                if (lastChar.equals("1")) {
                    suffix = "st";
                } else if (lastChar.equals("2")) {
                    suffix = "nd";
                } else if (lastChar.equals("3")) {
                    suffix = "rd";
                } else {
                    suffix = "th";
                }

            }
        } else {
            if (lastChar.equals("1")) {
                suffix = "st";
            } else if (lastChar.equals("2")) {
                suffix = "nd";
            } else if (lastChar.equals("3")) {
                suffix = "rd";
            } else {
                suffix = "th";
            }

        }
        return String.format(out + num + suffix + " item name.");
    }

    public void writeToDatabase(boolean newSeller, ArrayList<Seller> updateDatabase) {
        //ArrayList<Seller> database = readSellerDatabase();
        if (updateDatabase == null) {
            return;
        } else if (updateDatabase.size() != 0) {
            if (this.getUniqueIdentifier() != -1) {
                if (!newSeller) {
                    int toReplace = 0;
                    for (int i = 0; i < updateDatabase.size(); i++) {
                        if (updateDatabase.get(i).getUniqueIdentifier() == this.getUniqueIdentifier()) {
                            toReplace = i;
                        }
                    }
                    updateDatabase.set(toReplace, this);
                } else {
                    try {
                        updateDatabase.set(this.getSellerIndex(), this);
                    } catch (IndexOutOfBoundsException e) {
                        updateDatabase.add(this);
                    }
                }
            }
        }
        /*
        ArrayList<Seller> removeDuplicates = null;
        for (Seller seller: database) {
            removeDuplicates.set(seller.getSellerIndex(), seller);
        }
         */
        //ArrayList<Seller> removeDuplicates = new ArrayList<Seller>(new LinkedHashSet<Seller>(database));
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("./src/SellerDatabase.txt"));
            for (Seller seller : updateDatabase) {
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
            JOptionPane.showMessageDialog(null, "Database Malformed");
            //System.out.println("Database Malformed");
        }
    }

    public static int readInt(String input) {
        int result;
        try {
            result = Integer.parseInt(input);
            return result;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid Integer!");

            //System.out.println("Please enter a valid Integer!");
            return -1;
        }
    }

    public void getSellerStatistics() throws DataFormatException, IOException {
        ArrayList<Buyer> buyerDatabase = null;
        try {
            //ArrayList<Seller> sellerDatabase = readSellerDatabase();
            buyerDatabase = readBuyerDatabase();
            //ArrayList<Product> productDatabase = getProductDatabase();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "There are no statistics to view yet.");
            //System.out.println("There are no statistics to view yet.");
        }


        //list of customers with items purchase, list of products with sales
        ArrayList<Product> sellerProducts = new ArrayList<Product>();

        for (Store store : this.getStores()) {
            for (Product product : store.getProducts()) {
                sellerProducts.add(product);
            }
        }


        boolean exitCondition = true;
        do {
            String[] options = {"By Customer", "By Store", "All Products", "Return to main menu"};
            int choice = JOptionPane.showOptionDialog(null, "How would you like to view the statistics?", "View Statistics",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            //choice = readInt(scanner.nextLine());
            if (choice != -1) {
                if (choice == 1) {
                    int itemsPurchased;
                    double totalSpent;
                    int itemsInCart;
                    double potentialSpending;
                    ArrayList<Buyer> customers = new ArrayList<Buyer>();
                    for (Buyer buyer : buyerDatabase) {
                        itemsPurchased = 0;
                        totalSpent = 0;
                        itemsInCart = 0;
                        potentialSpending = 0;
                        for (ProductPurchase purchase : buyer.getPurchases()) {
                            for (Product product : sellerProducts) {
                                if (product.getUniqueID() == purchase.getUniqueID()) {
                                    customers.add(buyer);
                                    itemsPurchased += purchase.getOrderQuantity();
                                    totalSpent += purchase.getOrderQuantity() * purchase.getPrice();
                                    break;
                                }
                            }
                        }
                        for (ProductPurchase purchase : buyer.getShoppingCart()) {
                            for (Product product : sellerProducts) {
                                if (product.getUniqueID() == purchase.getUniqueID()) {
                                    customers.add(buyer);
                                    itemsInCart += purchase.getOrderQuantity();
                                    potentialSpending += purchase.getOrderQuantity() * purchase.getPrice();
                                    break;
                                }
                            }
                        }
                        if (customers.contains(buyer)) {
                            if (customers.contains(buyer)) {
                                String message = String.format("Customer Name: %s\n\tItems Purchased: %d\n\tTotal Value (with current prices): %.2f\n"
                                                + "\tItems In Cart: %d\n\tPotential Revenue: %.2f\n",
                                        buyer.getName(), itemsPurchased, totalSpent, itemsInCart, potentialSpending);
                                JOptionPane.showMessageDialog(null, message);
                            }

                        }
                        if (customers.size() == 0) {
                            //System.out.println("No one has purchased your products yet!");
                            JOptionPane.showMessageDialog(null, "No one has purchased your products yet!");
                        }
                    }

                } else if (choice == 2) {
                    ArrayList<Store> storeStat = this.getStores();
                    do {
                        String[] options1 = {"Quantity Sold", "Total Revenue", "Stock Remaining"};
                        int decision = JOptionPane.showOptionDialog(null, "How would you like to sort?",
                                "Sort Options", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options1, options1[0]);
                        if (decision != -1) {
                            if (decision == 1) {
                                Collections.sort(storeStat, Comparator.comparingInt(Store::getTotalQuantitySold).reversed());
                                StringBuilder message1 = new StringBuilder();
                                int i = 1;
                                for (Store store : storeStat) {
                                    message1.append(String.format("%d. %s\n", i, store.getStoreName()));
                                    message1.append(String.format("\tQuantity Sold: %d\n", store.getTotalQuantitySold()));
                                    message1.append(String.format("\tTotal Revenue: %.2f\n", store.getTotalValueSold()));
                                    message1.append(String.format("\tStock Remaining: %d\n", store.getStockRemaining()));
                                    i++;
                                }
                                JOptionPane.showMessageDialog(null, message1.toString());
                                break;
                            } else if (decision == 2) {
                                Collections.sort(storeStat, Comparator.comparingDouble(Store::getTotalValueSold).reversed());
                                StringBuilder message2 = new StringBuilder();
                                int i = 1;
                                for (Store store : storeStat) {
                                    message2.append(String.format("%d. %s\n", i, store.getStoreName()));
                                    message2.append(String.format("\tQuantity Sold: %d\n", store.getTotalQuantitySold()));
                                    message2.append(String.format("\tTotal Revenue: %.2f\n", store.getTotalValueSold()));
                                    message2.append(String.format("\tStock Remaining: %d\n", store.getStockRemaining()));
                                    i++;
                                }
                                JOptionPane.showMessageDialog(null, message2.toString());
                                break;

                            } else if (decision == 3) {
                                Collections.sort(storeStat, Comparator.comparingInt(Store::getStockRemaining).reversed());
                                StringBuilder message3 = new StringBuilder();
                                int i = 1;
                                for (Store store : storeStat) {
                                    message3.append(String.format("%d. %s\n", i, store.getStoreName()));
                                    message3.append(String.format("\tQuantity Sold: %d\n", store.getTotalQuantitySold()));
                                    message3.append(String.format("\tTotal Revenue: %.2f\n", store.getTotalValueSold()));
                                    message3.append(String.format("\tStock Remaining: %d\n", store.getStockRemaining()));
                                    i++;
                                }
                                JOptionPane.showMessageDialog(null, message3.toString());
                                break;
                            }
                        }


                    } while (!exitCondition);

                } else if (choice == 3) {

                    ArrayList<Product> productStat = sellerProducts;
                    do {
                        exitCondition = true;
                        String[] options2 = {"Quantity Sold", "Total Revenue", "Stock Remaining"};
                        int decision = JOptionPane.showOptionDialog(null, "How would you like to sort?",
                                "Sort Options", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options2, options2[0]);

                        if (decision != -1) {
                            if (decision == 1) {
                                StringBuilder messaged1 = new StringBuilder();
                                Collections.sort(productStat, Comparator.comparingInt(Product::getQuantitySold).reversed());
                                int i = 1;
                                for (Product product : productStat) {
                                    messaged1.append(String.format("%d. %s\n", i, product.getName()));
                                    messaged1.append(String.format("\tQuantity Sold: %d\n", product.getQuantitySold()));
                                    messaged1.append(String.format("\tTotal Revenue: %.2f\n", product.getQuantitySold() * product.getPrice()));
                                    messaged1.append(String.format("\tStock Remaining: %d\n", product.getQuantityForPurchase()));
                                    i++;
                                }
                                JOptionPane.showMessageDialog(null, messaged1.toString());
                                break;
                            } else if (decision == 2) {
                                Collections.sort(productStat, Comparator.comparingDouble(Product::getValueSold).reversed());
                                StringBuilder messaged2 = new StringBuilder();
                                int i = 1;
                                for (Product product : productStat) {
                                    messaged2.append(String.format("%d. %s\n", i, product.getName()));
                                    messaged2.append(String.format("\tQuantity Sold: %d\n", product.getQuantitySold()));
                                    messaged2.append(String.format("\tTotal Revenue: %.2f\n", product.getQuantitySold() * product.getPrice()));
                                    messaged2.append(String.format("\tStock Remaining: %d\n", product.getQuantityForPurchase()));
                                    i++;
                                }
                                JOptionPane.showMessageDialog(null, messaged2.toString());
                                break;
                            } else if (decision == 3) {
                                Collections.sort(productStat, Comparator.comparingInt(Product::getQuantityForPurchase).reversed());
                                StringBuilder messaged3 = new StringBuilder();
                                int i = 1;
                                for (Product product : productStat) {
                                    messaged3.append(String.format("%d. %s\n", i, product.getName()));
                                    messaged3.append(String.format("\tQuantity Sold: %d\n", product.getQuantitySold()));
                                    messaged3.append(String.format("\tTotal Revenue: %.2f\n", product.getQuantitySold() * product.getPrice()));
                                    messaged3.append(String.format("\tStock Remaining: %d\n", product.getQuantityForPurchase()));
                                    i++;
                                }
                                JOptionPane.showMessageDialog(null, messaged3.toString());
                                break;
                            } else {
                                JOptionPane.showMessageDialog(null, "Please enter valid menu choice!");
                                //System.out.println("Please enter valid menu choice!");
                                exitCondition = false;
                            }
                        }
                        while (!exitCondition) ;
                    } while (!exitCondition);
                } else if (choice == 4) {
                    exitCondition = true;
                    //System.out.println("Goodbye!");
                    break;
                }
            } else {
                exitCondition = false;
            }

        } while (!exitCondition) ;
    }

    public static double readDouble(String input) {
        double result;
        try {
            result = Double.parseDouble(input);
            return result;
        } catch (NumberFormatException e) {
            System.out.println("Please enter a number!");
            return -1;
        }
    }

    public ArrayList<Product> addProducts(int count) {
        ArrayList<Product> products = new ArrayList<Product>();
        for (int i = 0; i < count; i++) {

            String name = JOptionPane.showInputDialog(null, productName(i));

            //System.out.println(productName(i));
            //String name = scanner.nextLine();

            String description = JOptionPane.showInputDialog(null, "What is the description?");

            String stockString = JOptionPane.showInputDialog(null, "How many items in stock?");
            int stock = Integer.parseInt(stockString);

            String priceString = JOptionPane.showInputDialog(null, "How much does this item cost?");
            double price = Double.parseDouble(priceString);

            int uniqueID = getProductDatabase().size() + 1 + i;

            Product product = new Product(name, description, stock, price, 0, uniqueID);
            products.add(product);
        }
        return products;
    }

    public String serverString() {
        String id = String.format("* %d\n", this.getUniqueIdentifier());
        String storeName;
        String productName;
        for (Store store: this.getStores()){
            storeName = String.format("+ %s\n", store.getStoreName());
            for (Product product: store.getProducts()) {
                productName = product.toDatabase();
                storeName = storeName.concat(productName);
                storeName = storeName.concat("\n");
            }
            id = id.concat(storeName);
        }
        return id;
    }

}
