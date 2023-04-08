import java.lang.reflect.Array;
import java.util.ArrayList;
import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;
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

    public ArrayList<Seller> readSellerDatabase() {
        BufferedReader bfr = null;
        String line;
        ArrayList<Seller> database = new ArrayList<Seller>();
        Seller seller;
        Store store;
        Product product;
        int sellerIndex = -1;
        int storeIndex = -1;

        try {
            bfr = new BufferedReader(new FileReader("./src/SellerDatabase.txt"));

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

            bfr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return database;

    }

    public void sellerMenu(Scanner scanner) {
        int decision;
        this.setStores(readSellerDatabase().get(getSellerIndex()).getStores());
        while (true) {
            System.out.println("What actions would you like to take?\n" +
                    "1. Add store\n" +
                    "2. Delete" +
                    " store\n" +
                    "3. Edit store\n +" +
                    "4. Get statistics");
            try {
                decision = scanner.nextInt();
                scanner.nextLine();
                if (decision != 1 && decision != 2 && decision != 3) {
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

                int uniqueID = 0;

                Product product = new Product(name, description, stock, price, 0, uniqueID);
                products.add(product);
            }

            Store store = new Store(storeName, products);
            this.addStore(store);
            writeToDatabase(false);
        } else if (decision == 2) {//Edit Store
            System.out.print("Enter the store name you want to delete: ");
            String storeNameToDelete = scanner.nextLine();

            File inputFile = new File("SellerDatabase.txt");
            ArrayList<String> updatedStores = new ArrayList<>();

            try {
                Scanner fileScanner = new Scanner(inputFile);
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    if (line.startsWith("(")) {
                        updatedStores.add(line.substring(1, line.length() - 1));
                    }
                    updatedStores.add(line);
                }
                fileScanner.close();
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + inputFile.getName());
            }

            boolean storeDeleted = false;
            for (int i = 0; i < updatedStores.size(); i++) {
                if (updatedStores.get(i).equals("(" + storeNameToDelete + ")")) {
                    updatedStores.remove(i);
                    while (i < updatedStores.size() && !updatedStores.get(i).startsWith("(")) {
                        updatedStores.remove(i);
                    }
                    storeDeleted = true;
                    break;
                }
            }

            if (storeDeleted) {
                try {
                    FileWriter fileWriter = new FileWriter(inputFile);
                    for (String deletedStores : updatedStores) {
                        fileWriter.write(deletedStores + "\n");
                    }
                    fileWriter.close();
                    System.out.println(storeNameToDelete + " has been deleted.");
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            } else {
                System.out.println(storeNameToDelete + " not found.");
            }

        } else if (decision == 3) {
            System.out.print("Enter the store name you want to edit: ");
            String storeNameToEdit = scanner.nextLine();

            File inputFile = new File("stores.txt");
            ArrayList<String> editedStores = new ArrayList<>();

            try {
                Scanner fileScanner = new Scanner(inputFile);
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    if (line.startsWith("(")) {
                        editedStores.add(line.substring(1, line.length() - 1));
                    }
                    editedStores.add(line);
                }
                fileScanner.close();
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + inputFile.getName());
            }

            int storeIndex = -1;
            for (int i = 0; i < stores.size(); i++) {
                if (editedStores.get(i).equals("(" + storeNameToEdit + ")")) {
                    storeIndex = i;
                    break;
                }
            }

            if (storeIndex != -1) {
                System.out.println("Enter the new details for " + storeNameToEdit + ":");
                System.out.print("Items and descriptions and price: ");
                String newDetails = scanner.nextLine();

                editedStores.set(storeIndex + 1, newDetails);

                try {
                    FileWriter fileWriter = new FileWriter(inputFile);
                    for (String shop : editedStores) {
                        fileWriter.write(shop + "\n");
                    }
                    fileWriter.close();
                    System.out.println(storeNameToEdit + " has been updated.");
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            } else {
                System.out.println(storeNameToEdit + " not found.");
            }
        }
    }

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
        return String.format(out + suffix + " item name.");
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


        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("./src/SellerDatabase.txt"));
            for (Seller seller : database) {
                String temp = String.format("* %d\n", seller.getSellerIndex());
                bw.write(temp);
                if (seller.getStores() != null) {
                    for (Store store : seller.getStores()) {
                        bw.write(String.format("+ %s\n", store.getStoreName()));
                        for (Product product : store.getProducts()) {
                            bw.write(product.toString());
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
                                buyer.getShoppingCart().add(new ProductPurchase(product.getUniqueID(), tempQuantity));
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
                                buyer.getPurchases().add(new ProductPurchase(product.getUniqueID(), tempQuantity));
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

}
