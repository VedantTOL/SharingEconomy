import java.util.*;
import java.io.*;
import java.util.zip.DataFormatException;

public class Seller extends User {
    private ArrayList<Store> stores;



    public Seller(int uniqueIdentifier, String email, String password, String name, int age, int sellerIndex, ArrayList<Store> stores) {
        super(uniqueIdentifier, email, password, name, age, sellerIndex);
        this.stores = stores;
    }

    public Seller(int uniqueIdentifier) {
        super(uniqueIdentifier);
        this.stores = new ArrayList<Store>();
    }

    public Seller() {
        this.stores = null;
    }


    public int getNewIndex(boolean newSeller) {
        ArrayList<Seller> database = readSellerDatabase();
        if (newSeller) {
            return database.size() + 1;
        } else {
            return 0;
        }
}

    public void addStore(Store store) {
        stores.add(store);
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



    public void sellerMenu(Scanner scanner) throws DataFormatException, IOException {
        int decision;
        this.setStores(readSellerDatabase().get(getSellerIndex()).getStores());

        while (true) {
            System.out.println("What actions would you like to take?\n" +
                    "1. Add store\n" +
                    "2. Delete" +
                    " store\n" +
                    "3. Edit store");
            System.out.println("4. View Statistics");
            try {
                decision = scanner.nextInt();
                scanner.nextLine();
                if (decision != 1 && decision != 2 && decision != 3 && decision != 4) {
                    System.out.println("Please enter a valid option!");
                } else {
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid option!");
            }
        }
        if (decision == 1) { //Add store
            ArrayList<Product> products = null;
            System.out.println("What is the name of the store you want to add?");
            String storeName = scanner.nextLine();

            System.out.println("How many products do you want to add?");
            int items = scanner.nextInt();
            scanner.nextLine();

            products = new ArrayList<Product>();
            for (int i = 0; i < items; i++) {
                System.out.println(productName(i));
                String name = scanner.nextLine();

                System.out.println("What is the description?");
                String description = scanner.nextLine();

                System.out.println("How many items in stock?");
                int stock = scanner.nextInt();

                System.out.println("How much does this item cost?");
                double price = scanner.nextDouble();
                scanner.nextLine();

                int uniqueID = getProductDatabase().size() + 1 + i;

                Product product = new Product(name, description, stock, price, 0, uniqueID );
                products.add(product);

            }

            Store store = new Store(storeName, products);
            this.addStore(store);
            writeToDatabase(false);
        } else if (decision == 2) {//Delete Store

            int i = 1;
            if (this.getStores().size() == 0) {
                System.out.println("You have no stores, please add one!");
            } else {

                for (Store store : this.getStores()) {
                    System.out.printf("%d: %s\n", i, store.getStoreName());
                    i++;
                }


                while (true) {
                    System.out.println("Enter the store index you want to delete: ");
                    String storeNameToDelete = scanner.nextLine();
                    int toDelete = readInt(storeNameToDelete);
                    if (toDelete != -1) {
                        this.getStores().remove(toDelete - 1);
                        break;
                    }
                }
                writeToDatabase(false);
            }
        } else if (decision == 3) { //edit
            Store edit = null;
            int i = 1;
            if (this.getStores().size() == 0) {
                System.out.println("You have no stores, please add one!");

            } else {

                for (Store store : this.getStores()) {
                    System.out.printf("%d: %s\n", i, store.getStoreName());
                    i++;
                }


                while (true) {
                    System.out.println("Enter the store index you want to edit: ");
                    String storeNameToDelete = scanner.nextLine();
                    int toEdit = readInt(storeNameToDelete);
                    if (toEdit != -1) {
                        edit = this.getStores().get(toEdit - 1);
                        this.getStores().remove(toEdit - 1);
                        break;
                    }
                }

                while (true) {
                    System.out.printf("What would you like to change about %s\n", edit.getStoreName());
                    System.out.println("1. Store Name");
                    System.out.println("2. Add Products");
                    System.out.println("3. Edit Products");
                    System.out.println("4. Delete Products");

                    String option = scanner.nextLine();
                    int toEdit = readInt(option);
                    if (toEdit != -1) {
                        if (toEdit == 1) { //Store Name
                            while (true) {
                                System.out.println("Enter the new name of the Store: ");
                                String newName = scanner.nextLine();
                                if (newName == null) {
                                    System.out.println("Please enter a valid String (cannot be empty!)");
                                } else {
                                    edit.setStoreName(newName);
                                    break;
                                }
                            }
                            this.getStores().add(edit);
                        } else if (toEdit == 3) { // Edit Products
                            Product productEdit = null;
                            int k = 1;
                            for (Product product : edit.getProducts()) {
                                System.out.printf("%d: %s\n", k, product.getName());
                                k++;
                            }
                            while (true) {
                                System.out.println("Enter the product index you want to edit: ");
                                String productToEdit = scanner.nextLine();
                                int x = readInt(productToEdit);
                                if (x != -1) {
                                    productEdit = edit.getProducts().get(x - 1);
                                    edit.getProducts().remove(x - 1);
                                    break;
                                }
                            }
                            boolean exitCondition = true;
                            do {
                                System.out.printf("What would you like to edit about %s\n", productEdit.getName());
                                System.out.print("1. Name\n2. Description\n3. Price\n4. Quantity For Purchase\n");
                                int productChoice = readInt(scanner.nextLine());
                                if (productChoice != -1) {
                                    if (productChoice == 1) {
                                        while (true) {
                                            System.out.println("Enter the new name of the Product: ");
                                            String newName = scanner.nextLine();
                                            if (newName == null) {
                                                System.out.println("Please enter a valid String (cannot be empty!)");
                                            } else {
                                                productEdit.setName(newName);
                                                break;
                                            }
                                        }
                                    } else if (productChoice == 2) {
                                        while (true) {
                                            System.out.println("Enter the new description of the product: ");
                                            String newName = scanner.nextLine();
                                            if (newName == null) {
                                                System.out.println("Please enter a valid String (cannot be empty!)");
                                            } else {
                                                productEdit.setDescription(newName);
                                                break;
                                            }
                                        }
                                    } else if (productChoice == 3) {
                                        while (true) {
                                            System.out.println("Enter the new price of the Product: ");
                                            double newPrice = readDouble(scanner.nextLine());
                                            if (newPrice != -1) {
                                                if (newPrice < 0) {
                                                    System.out.println("Please enter a valid Price (cannot be less than 0!)");
                                                } else {
                                                    productEdit.setPrice(newPrice);
                                                    break;
                                                }
                                            }
                                        }
                                    } else if (productChoice == 4) {
                                        while (true) {
                                            System.out.println("How much stock is available?");
                                            int newStock = readInt(scanner.nextLine());
                                            if (newStock < 0) {
                                                System.out.println("Please enter a number greater than 0!");
                                            } else {
                                                productEdit.setQuantityForPurchase(newStock);
                                                break;
                                            }
                                        }

                                    } else {
                                        System.out.println("Please enter a valid choice!");
                                        exitCondition = false;
                                    }

                                }
                            } while (!exitCondition);
                            edit.getProducts().add(productEdit);
                            this.getStores().add(edit);
                        } else if (toEdit == 2) {
                            System.out.println("How many products do you want to add?");
                            int items = scanner.nextInt();
                            scanner.nextLine();

                            for (int j = 0; j < items; j++) {
                                System.out.println(productName(j));
                                String name = scanner.nextLine();

                                System.out.println("What is the description?");
                                String description = scanner.nextLine();

                                System.out.println("How many items in stock?");
                                int stock = scanner.nextInt();

                                System.out.println("How much does this item cost?");
                                double price = scanner.nextDouble();
                                scanner.nextLine();

                                int uniqueID = getProductDatabase().size() + 1;

                                Product product = new Product(name, description, stock, price, 0, uniqueID);
                                edit.getProducts().add(product);
                            }
                            this.getStores().add(edit);
                        } else if (toEdit == 4) {
                            Product productDelete = null;
                            int k = 1;
                            for (Product product : edit.getProducts()) {
                                System.out.printf("%d: %s\n", k, product.getName());
                                k++;
                            }
                            while (true) {
                                System.out.println("Enter the product index you want to delete: ");
                                String productToDelete = scanner.nextLine();
                                int x = readInt(productToDelete);
                                if (x != -1) {
                                    edit.getProducts().remove(x - 1);
                                    break;
                                }
                            }
                            this.getStores().add(edit);
                        }
                    }

                    break;
                }
                writeToDatabase(false);

            }
        } else if (decision == 4) {
            getSellerStatistics(scanner);
        }
        writeToDatabase(false);
    }

    public String productName(int items) {
        String out = "Enter the ";
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

    public void writeToDatabase(boolean newSeller) {
        ArrayList<Seller> database = readSellerDatabase();

        if (database.size() != 0) {
            if (!newSeller) {
                database.remove(this.getSellerIndex());
                database.add(this.getSellerIndex(), this);
            } else {
                database.add(this);
            }
        }

        if (this.getUniqueIdentifier() == -1) {
            return;
        }


        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("./src/SellerDatabase.txt"));
            for (Seller seller: database) {
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
            System.out.println("Database Malformed");
        }
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

    public void getSellerStatistics(Scanner scanner) throws DataFormatException, IOException {
        ArrayList<Seller> sellerDatabase = readSellerDatabase();
        ArrayList<Buyer> buyerDatabase = readBuyerDatabase();
        ArrayList<Product> productDatabase = getProductDatabase();

        //list of customers with items purchase, list of products with sales
        ArrayList<Product> sellerProducts = new ArrayList<Product>();
        for (Store store: this.getStores()) {
            for (Product product: store.getProducts()) {
                sellerProducts.add(product);
            }
        }




        boolean exitCondition = true;
        do {
            System.out.println("How would you like to view the statistics?");
            System.out.println("1. By Customer");
            System.out.println("2. By Store");
            System.out.println("3. All Products");
            System.out.println("4. Exit");

            int choice = readInt(scanner.nextLine());
            if (choice != -1) {
                if (choice == 1) {
                    int itemsPurchased;
                    double totalSpent;
                    int itemsInCart;
                    double potentialSpending;
                    ArrayList<Buyer> customers = new ArrayList<Buyer>();
                    for (Buyer buyer: buyerDatabase) {
                        itemsPurchased = 0;
                        totalSpent = 0;
                        itemsInCart = 0;
                        potentialSpending = 0;
                        for (ProductPurchase purchase: buyer.getPurchases()) {
                            for (Product product: sellerProducts) {
                                if (product.getUniqueID() == purchase.getUniqueID()) {
                                    customers.add(buyer);
                                    itemsPurchased += purchase.getOrderQuantity();
                                    totalSpent += purchase.getOrderQuantity() * purchase.getPrice();
                                    break;
                                }
                            }
                        }
                        for (ProductPurchase purchase: buyer.getShoppingCart()) {
                            for (Product product: sellerProducts) {
                                if (product.getUniqueID() == purchase.getUniqueID()) {
                                    customers.add(buyer);
                                    itemsInCart += purchase.getOrderQuantity();
                                    potentialSpending += purchase.getOrderQuantity() * purchase.getPrice();
                                    break;
                                }
                            }
                        }
                        if (customers.contains(buyer)) {
                            System.out.printf("Customer Name: %s\n\tItems Purchased: %d\n\tTotal Value (with current prices): %.2f\n",
                                    buyer.getName(), itemsPurchased, totalSpent);
                            System.out.printf("\tItems In Cart: %d\n\tPotential Revenue: %.2f\n", itemsInCart, potentialSpending);
                        }
                    }

                } else if (choice == 2) {
                    ArrayList<Store> storeStat = this.getStores();
                    do {
                        System.out.println("How would you like to sort?");
                        System.out.print("1. Quantity Sold\n2. Total Revenue\n3. Stock Remaining\n");
                        int decision = readInt(scanner.nextLine());
                        if (decision != -1) {
                            if (decision == 1) {
                                Collections.sort(storeStat, Comparator.comparingInt(Store::getTotalQuantitySold).reversed());
                                int i = 1;
                                for (Store store : storeStat) {
                                    System.out.printf("%d. %s\n", i, store.getStoreName());
                                    System.out.printf("\tQuantity Sold: %d\n", store.getTotalQuantitySold());
                                    System.out.printf("\tTotal Revenue: %.2f\n", store.getTotalValueSold());
                                    System.out.printf("\tStock Remaining: %d\n", store.getStockRemaining());
                                    i++;
                                }
                                break;
                            } else if (decision == 2) {
                                Collections.sort(storeStat, Comparator.comparingDouble(Store::getTotalValueSold).reversed());
                                int i = 1;
                                for (Store store : storeStat) {
                                    System.out.printf("%d. %s\n", i, store.getStoreName());
                                    System.out.printf("\tQuantity Sold: %d\n", store.getTotalQuantitySold());
                                    System.out.printf("\tTotal Revenue: %.2f\n", store.getTotalValueSold());
                                    System.out.printf("\tStock Remaining: %d\n", store.getStockRemaining());
                                    i++;
                                }
                                break;

                            } else if (decision == 3) {
                                Collections.sort(storeStat, Comparator.comparingInt(Store::getStockRemaining).reversed());
                                int i = 1;
                                for (Store store : storeStat) {
                                    System.out.printf("%d. %s\n", i, store.getStoreName());
                                    System.out.printf("\tQuantity Sold: %d\n", store.getTotalQuantitySold());
                                    System.out.printf("\tTotal Revenue: %.2f\n", store.getTotalValueSold());
                                    System.out.printf("\tStock Remaining: %d\n", store.getStockRemaining());
                                    i++;
                                }
                                break;
                            }
                        }


                    } while (!exitCondition);

                } else if (choice == 3) {

                    ArrayList<Product> productStat = sellerProducts;
                    do {
                        exitCondition = true;
                        System.out.println("How would you like to sort?");
                        System.out.print("1. Quantity Sold\n2. Total Revenue\n3. Stock Remaining\n");
                        int decision = readInt(scanner.nextLine());
                        if (decision != -1) {
                            if (decision == 1) {
                                Collections.sort(productStat, Comparator.comparingInt(Product::getQuantitySold).reversed());
                                int i = 1;
                                for (Product product : productStat) {
                                    System.out.printf("%d. %s\n", i, product.getName());
                                    System.out.printf("\tQuantity Sold: %d\n", product.getQuantitySold());
                                    System.out.printf("\tTotal Revenue: %.2f\n", product.getQuantitySold() * product.getPrice());
                                    System.out.printf("\tStock Remaining: %d\n", product.getQuantityForPurchase());
                                    i++;
                                }
                                break;
                            } else if (decision == 2) {
                                Collections.sort(productStat, Comparator.comparingDouble(Product::getValueSold).reversed());
                                int i = 1;
                                for (Product product : productStat) {
                                    System.out.printf("%d. %s\n", i, product.getName());
                                    System.out.printf("\tQuantity Sold: %d\n", product.getQuantitySold());
                                    System.out.printf("\tTotal Revenue: %.2f\n", product.getQuantitySold() * product.getPrice());
                                    System.out.printf("\tStock Remaining: %d\n", product.getQuantityForPurchase());
                                    i++;
                                }
                                break;

                            } else if (decision == 3) {
                                Collections.sort(productStat, Comparator.comparingInt(Product::getQuantityForPurchase).reversed());
                                int i = 1;
                                for (Product product : productStat) {
                                    System.out.printf("%d. %s\n", i, product.getName());
                                    System.out.printf("\tQuantity Sold: %d\n", product.getQuantitySold());
                                    System.out.printf("\tTotal Revenue: %.2f\n", product.getQuantitySold() * product.getPrice());
                                    System.out.printf("\tStock Remaining: %d\n", product.getQuantityForPurchase());
                                    i++;
                                }
                                break;
                            } else {
                                System.out.println("Please enter valid menu choice!");
                                exitCondition = false;
                            }
                        } while (!exitCondition);
                    } while (!exitCondition);
                }

            } else {
                exitCondition = false;
            }

        } while (!exitCondition);


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
}


