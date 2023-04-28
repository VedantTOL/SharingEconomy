import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
/**
 * The user class create a user object and allows a buyer or seller to be identifiable with a uniqueID after being
 * written to the file. The user class and its methods allow users to create an account, login with an existing account,
 * and edit/delete accounts. This class's functionality is integral to the function of buyer and seller classes.
 *
 *
 * @author Roger, Somansh, Ethan, Vedant
 * @version June 13, 2022
 */

public class User {
    private int uniqueIdentifier;
    private String email;
    private String password;
    private String name;
    private int age;

    public int getSellerIndex() {
        return sellerIndex;
    }

    public void setSellerIndex(int sellerIndex) {
        this.sellerIndex = sellerIndex;
    }

    private int sellerIndex;

    public User() {
        this.uniqueIdentifier = -1;
        this.email = null;
        this.password = null;
        this.name = null;
        this.age = 0;
        this.sellerIndex = -2;
    }

    public User(int uniqueIdentifier, String email, String password, String name, int age) {
        this.uniqueIdentifier = uniqueIdentifier;
        this.email = email;
        this.password = password;
        this.name = name;
        this.age = age;
    }
    public User(int uniqueIdentifier, boolean isSeller){
        this.uniqueIdentifier = uniqueIdentifier;
        try {
            ArrayList<User> database = getInformation(isSeller);
            this.email = database.get(uniqueIdentifier).getEmail();
            this.password = database.get(uniqueIdentifier).getPassword();
            this.name = database.get(uniqueIdentifier).getName();
            this.age = database.get(uniqueIdentifier).getAge();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public User(String[] userDetails) {
        try {
            this.uniqueIdentifier = Integer.parseInt(userDetails[0]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("UniqueIdentifier must be an Integer");
        }

        if (!checkEmailFormat(userDetails[1])) {
            throw new IllegalArgumentException("Valid email required!");
        } else {
            this.email = userDetails[1];
        }

        this.password = userDetails[2];
        this.name = userDetails[3];

        try {
            int tempAge = Integer.parseInt(userDetails[4]);
            if (tempAge < 0 ) {
                throw new IllegalArgumentException("Valid age required!");
            } else {
                this.age = tempAge;
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Age must be an Integer");
        }
    }
    public int getUniqueIdentifier() {
        return uniqueIdentifier;
    }
    public void setUniqueIdentifier(int uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }


    public User addUser(Scanner scanner, boolean seller) throws UserDatabaseFormatError {
        String email;
        String password;
        String name;
        int age;
        int uniqueId;

        do {
            System.out.println("Enter your email: ");
            email = scanner.nextLine();

        } while (!checkEmailFormat(email));

        System.out.println("Enter a password: ");
        password = scanner.nextLine();

        //insert method to check password strength

        System.out.println("What's your name?");
        name = scanner.nextLine();

        System.out.println("What's your age?");
        age = scanner.nextInt();
        scanner.nextLine();
        ArrayList<User> database = getInformation(seller);

        if (database.size() != 0) {
            uniqueId = database.get(database.size() - 1).getUniqueIdentifier() + 1;
        } else {
            uniqueId = 0;
        }

        User user = new User(uniqueId, email, password, name, age);
        database.add(user);
        updateLoginDatabase(seller, database);
        return user;
    }

    public void updateLoginDatabase(boolean seller, ArrayList<User> database) {
        BufferedWriter bw;
        String filename;

        if (seller) {
            filename = "./src/SellerLogin.txt";
        } else {
            filename = "./src/BuyerLogin.txt";
        }

        try {
            bw = new BufferedWriter(new FileWriter(filename));
            for (User k: database) {
                bw.write(k.constructorString());
                bw.write("\n");
            }
            bw.close();

        } catch (IOException e) {
            return;
        }
    }

    public boolean checkEmailFormat(String email) {
        String[] emailChecker = email.split("@");
        if (emailChecker.length == 2) {
            return true;
        } else {
            System.out.println("Please enter a valid email!");
            return false;
        }
    }

    public String constructorString() {
        return String.format("%d, %s, %s, %s, %d", this.getUniqueIdentifier(), this.getEmail(), this.getPassword(), this.getName(), this.getAge());
    }

    public String toString() {
        return String.format("ID = <%d>\nEmail = <%s>\nPassword = <%s>\nName = <%s>\nAge <%d>", this.getUniqueIdentifier(), this.getEmail(), this.getPassword(), this.getName(), this.getAge());
    }
    public User login(Scanner scanner, boolean seller) throws AccountTypeError, NoAccountError, IllegalAccessError, UserDatabaseFormatError {
        ArrayList<User> database = getInformation(seller);

        System.out.println("Email: ");
        String emailCheck = scanner.nextLine();
        System.out.println("Password: ");
        String passwordCheck = scanner.nextLine();

        for (User user: database) {
            if (user.getEmail().equals(emailCheck)) {
                if(user.getPassword().equals(passwordCheck)) {
                    return user;
                } else {
                    throw new IllegalAccessError("Incorrect Password");
                }
            }
        }
        throw new NoAccountError("Email does not exist in records.");
    }

    public User changeAccount(Scanner scanner, boolean isSeller) {
        System.out.println("Here are your details: ");
        System.out.println(this);
        while (true) {
            System.out.println("What would you like to change?");
            System.out.println("1. Email\n2. Password\n3. Name\n4. Age");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> {
                    while (true) {
                        System.out.println("Enter new email: ");
                        String email = scanner.nextLine();
                        if (checkEmailFormat(email)) {
                            setEmail(email);
                            break;
                        } else {
                            System.out.println("Please enter a valid email!");
                        }
                    }
                }
                case 2 -> {
                    while (true) {
                        System.out.println("Please enter your old password: ");
                        String checkPassword = scanner.nextLine();
                        if (checkPassword.equals(this.getPassword())) {
                            System.out.println("Please enter your new password: ");
                            setPassword(scanner.nextLine());
                            break;
                        } else {
                            System.out.println("Incorrect password, please try again.");
                        }
                    }
                }
                case 3 -> {
                    System.out.println("Please enter a new name");
                    setName(scanner.nextLine());
                }
                case 4 -> {
                    int age;
                    System.out.println("Please enter a new age");
                    while (true) {
                        age = scanner.nextInt();
                        if (age > 0) {
                            setAge(age);
                            break;
                        } else {
                            System.out.println("Please enter a valid age!");
                        }
                    }
                }
                default -> {
                    System.out.println("Please select a valid menu option!");
                    continue;
                }
            }
            break;
        }

        ArrayList<User> database = getInformation(isSeller);
        database.remove(this.uniqueIdentifier);
        database.add(this.uniqueIdentifier, this);
        updateLoginDatabase(isSeller, database);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("./src/UserDatabase.txt")));
            for (User k : database) {
                bw.write(k.constructorString());
                bw.write("\n");
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("An error occurred please try again!");
            return null;
        }
        return this;
    }

    public boolean deleteAccount(Scanner scanner, boolean isSeller) {
        System.out.println("Would you like to delete your account? This cannot be undone.");
        System.out.println("1.Yes\n2.No");
        int choice = scanner.nextInt();
        scanner.nextLine();
        while (true) {
            if (choice == 1) {
                ArrayList<User> database = getInformation(isSeller);
                for (User user: database) {
                    if (this.uniqueIdentifier == user.getUniqueIdentifier()) {
                        database.remove(user);
                        break;
                    }
                }
                updateLoginDatabase(isSeller, database);
                return true;
            } else if (choice == 2) {
                return false;
            } else {
                System.out.println("Please select a valid menu option!");
            }
        }
    }

    public ArrayList<Seller> readSellerDatabase() throws NoSellers {

        //initialize variables;
        BufferedReader bfr = null;
        String line;
        ArrayList<Seller> database = new ArrayList<Seller>();

        //initializing iterating objects to use them outside the scope of try/catch;
        Seller seller;
        Store store;
        Product product;

        //used for indexing arraylists; incremented
        int sellerIndex = -1;
        int storeIndex = -1;

        try {
            bfr = new BufferedReader(new FileReader("./src/SellerDatabase.txt"));
            while (true) {
                line = bfr.readLine();

                if (line == null || line == "") {
                    break;
                }

                char identifier = line.charAt(0); //data processing

                if (identifier == 42) {
                    storeIndex = -1;

                    seller = new Seller(Integer.parseInt(line.split(" ")[1]));
                    if (seller.getSellerIndex() != -1) {
                        database.add(seller);
                        sellerIndex = seller.getSellerIndex();
                    }
                } else if (identifier == 43) {
                    storeIndex++;
                    store = new Store(line.split(" ")[1]);
                    database.get(sellerIndex).addStore(storeIndex, store);
                } else {
                    try {
                        product = new Product(line.split(", "));
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
    public ArrayList<Product> getProductDatabase() {
        ArrayList<Seller> database = null;
        try {
            database = readSellerDatabase();
        } catch (NoSellers e) {
            return null;
        }
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
    /*
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

                if (identifier == '*') {
                    try {
                        buyer = new Buyer(Integer.parseInt(line.split(" ")[1]));
                        database.add(buyer);
                    } catch (NoAccountError e) {
                        return null;
                    }
                } else if (identifier == '-') {
                    line = line.substring(2);
                    String[] cartList = line.split(", ");
                    for (String productID: cartList) {
                        try {
                            int tempID = Integer.parseInt(productID.split(":")[0]);
                            int tempQuantity = Integer.parseInt(productID.split(":")[1]);
                            buyer.getShoppingCart().add(new ProductPurchase(tempID, tempQuantity));
                        } catch (NumberFormatException e) {
                        }
                        /*
                        for (Product product: productDatabase) {
                            if (tempID == product.getUniqueID()){
                                buyer.shoppingCart.add(new ProductPurchase(product.getUniqueID(), tempQuantity));
                            }
                        }


                    }

                } else if (identifier == '+') {
                    line = line.substring(2);

                    String[] purchasedList = line.split(", ");
                    for (String productID: purchasedList) {
                        int tempID = Integer.parseInt(productID.split(":")[0]);

                        int tempQuantity = Integer.parseInt(productID.split(":")[1]);
                        buyer.getPurchases().add(new ProductPurchase(tempID, tempQuantity));

                        /*
                        for (Product product: productDatabase) {
                            if (tempID == product.getUniqueID()){

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

     */
    public ArrayList<User> getInformation (boolean seller) {
        BufferedReader bfr;
        ArrayList<User> result = new ArrayList<User>();
        User user;
        String line;
        try {
            if (seller) {
                bfr = new BufferedReader(new FileReader("./src/SellerLogin.txt"));
            } else {
                bfr = new BufferedReader(new FileReader("./src/BuyerLogin.txt"));
            }
            while (true) {


                line = bfr.readLine();
                if (line == null) {
                    break;
                }
                user = new User(line.split(", "));
                result.add(user);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (result.size() != 0) {
            return result;
        } else {
            return null;
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
                line = bfr.readLine(); // string to be sent from server
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


}