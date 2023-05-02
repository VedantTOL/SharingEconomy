import javax.swing.*;
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

//    public User addUser(boolean seller) {
//        String email;
//        String password;
//        String name;
//        int age;
//        int uniqueId;
//
//        do {
//            email = JOptionPane.showInputDialog(null, "Enter your email: ");
//
//        } while (!checkEmailFormat(email));
//
//        password = JOptionPane.showInputDialog(null, "Enter a password: ");
//        //System.out.println("Enter a password: ");
//        //password = scanner.nextLine();
//
//        name = JOptionPane.showInputDialog(null, "What's your name?");
//        //System.out.println("What's your name?");
//        //name = scanner.nextLine();
//
//        String ageInput = JOptionPane.showInputDialog(null, "What's your age?");
//        age = Integer.parseInt(ageInput);
//
//        //System.out.println("What's your age?");
//        //age = scanner.nextInt();
//        //scanner.nextLine();
//
//
//        ArrayList<User> database = readUserDatabase("./src/UserDatabase.txt");
//        if (database.size() != 0) {
//            uniqueId = database.get(database.size() - 1).getUniqueIdentifier() + 1;
//        } else {
//            uniqueId = 0;
//        }
//
//        int sellerIndex;
//        if (!seller) {
//            sellerIndex = -1;
//        } else {
//            sellerIndex = 0;
//            for (int i = database.size(); i > 0; i--) {
//                int topIndex = database.get(i - 1).getSellerIndex();
//                if (topIndex != -1) {
//                    sellerIndex = topIndex + 1;
//                    break;
//                }
//            }
//        }
//        User user = new User(uniqueId, email, password, name, age);
//        database.add(user);
//
//        File f;
//        FileWriter fw;
//        BufferedWriter bw;
//
//        try {
//            bw = new BufferedWriter(new FileWriter(new File("./src/UserDatabase.txt")));
//            for (User k : database) {
//                bw.write(k.constructorString());
//                bw.write("\n");
//            }
//            bw.close();
//
//            if (seller) {
//                bw = new BufferedWriter(new FileWriter("./src/SellerDatabase.txt", true));
//                bw.write(String.format("* %d\n", sellerIndex));
//                bw.close();
//
//            }
//            return user;
//        } catch (IOException e) {
//            return null;
//        }
//    }
//
    public boolean checkEmailFormat(String email) {
        String[] emailChecker = email.split("@");
        if (emailChecker.length == 2) {
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Please enter a valid email!", "Invalid Email", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
//
   public String constructorString() {
       return String.format("%d, %s, %s, %s, %d, %d", this.getUniqueIdentifier(), this.getEmail(), this.getPassword(), this.getName(), this.getAge(), this.getSellerIndex());
  }
//
    public String toString() {
       return String.format("ID = <%d>\nEmail = <%s>\nPassword = <%s>\nName = <%s>\nAge <%d>", this.getUniqueIdentifier(), this.getEmail(), this.getPassword(), this.getName(), this.getAge());
    }

//    public User login(boolean seller) throws AccountTypeError, NoAccountError, IllegalAccessError {
//        ArrayList<User> database = readUserDatabase("./src/UserDatabase.txt");
//        String emailCheck = JOptionPane.showInputDialog(null, "Email:");
//        String passwordCheck = JOptionPane.showInputDialog(null, "Password:");
//
//        for (User user : database) {
//            if (user.getEmail().equals(emailCheck)) {
//                if (user.getPassword().equals(passwordCheck)) {
//                    if (seller) {
//                        if (user.getSellerIndex() != -1) {
//                            JOptionPane.showMessageDialog(null, "Login Successful!");
//                            return user;
//                        } else {
//                            throw new AccountTypeError("Wrong Account Type!");
//                        }
//                    } else {
//                        if (user.getSellerIndex() == -1) {
//                            JOptionPane.showMessageDialog(null, "Login Successful!");
//                            //System.out.println("Login Successful!");
//                            return user;
//                        } else {
//                            throw new AccountTypeError("Wrong Account Type!");
//                        }
//                    }
//                } else {
//                    throw new IllegalAccessError("IncorrectPassword");
//                }
//            }
//        }
//        throw new NoAccountError("Email does not exist in records");
//    }

//    public User changeAccount() {
//        JOptionPane.showMessageDialog(null, "Here are your details: " + this.toString());
//        //System.out.println("Here are your details: ");
//        //System.out.println(this);
//        while (true) {
//            int choice = Integer.parseInt(JOptionPane.showInputDialog(null,
//                    "What would you like to change?\n1. Email\n2. Password\n3. Name\n4. Age"));
//            switch (choice) {
//                case 1 -> {
//                    while (true) {
//                        String email = JOptionPane.showInputDialog(null, "Enter new email:");
//                        if (checkEmailFormat(email)) {
//                            setEmail(email);
//                            break;
//                        } else {
//                            JOptionPane.showMessageDialog(null, "Please enter a valid email!", "Invalid Email", JOptionPane.ERROR_MESSAGE);
//                        }
//                    }
//                }
//                case 2 -> {
//                    while (true) {
//                        String checkPassword = JOptionPane.showInputDialog(null,
//                                "Please enter your old password:", "Change Password", JOptionPane.PLAIN_MESSAGE);
//                        if (checkPassword.equals(this.getPassword())) {
//                            String newPassword = JOptionPane.showInputDialog(null,
//                                    "Please enter your new password:", "Change Password", JOptionPane.PLAIN_MESSAGE);
//                            //setPassword(scanner.nextLine());
//                            break;
//                        } else {
//                            JOptionPane.showMessageDialog(null, "Incorrect password, please try again.",
//                                    "Change Password", JOptionPane.ERROR_MESSAGE);
//                        }
//                    }
//                }
//                case 3 -> {
//                    String newName = JOptionPane.showInputDialog(null,
//                            "Please enter a new name:", "Change Name", JOptionPane.PLAIN_MESSAGE);
//                }
//                case 4 -> {
//                    int age;
//                    String input = JOptionPane.showInputDialog("Please enter a new age");
//                    age = Integer.parseInt(input);
//                        if (age > 0) {
//                            setAge(age);
//                            break;
//                        } else {
//                            JOptionPane.showMessageDialog(null, "Please enter a valid age!");
//                            //System.out.println("Please enter a valid age!");
//                        }
//                    }
//
//                default -> {
//                    JOptionPane.showMessageDialog(null, "Please select a valid menu option!");
//                    //System.out.println("Please select a valid menu option!");
//                    continue;
//                }
//            }
//            break;
//        }
//        ArrayList<User> database = getInformation("./src/UserDatabase.txt");
//        database.remove(this.uniqueIdentifier);
//        database.add(this.uniqueIdentifier, this);
//        try {
//            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("./src/UserDatabase.txt")));
//            for (User k : database) {
//                bw.write(k.constructorString());
//                bw.write("\n");
//            }
//            bw.close();
//        } catch (IOException e) {
//            JOptionPane.showMessageDialog(null, "An error occurred please try again!");
//            return null;
//        }
//        return this;
//    }
//
//    public boolean deleteAccount(Client client) {
//        int choice = JOptionPane.showOptionDialog(null,
//                "Would you like to delete your account? This cannot be undone.", "Delete Account",
//                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Yes", "No"}, "No");
//
//        while (true) {
//            if (choice == 1) {
//                ArrayList<User> database = readUserDatabase("./src/UserDatabase.txt");
//                database.remove(this.uniqueIdentifier);
//                try {
//                    BufferedWriter bw = new BufferedWriter(new FileWriter(new File("./src/UserDatabase.txt")));
//                    for (User k : database) {
//                        bw.write(k.constructorString());
//                        bw.write("\n");
//                    }
//                    bw.close();
//                    return true;
//                } catch (IOException e) {
//                    JOptionPane.showMessageDialog(null, "An error occurred please try again!");
//                    //System.out.println("An error occurred please try again!");
//                    return false;
//                }
//            } else if (choice == 2) {
//                return false;
//            } else {
//                JOptionPane.showMessageDialog(null, "Please select a valid menu option!");
//                //System.out.println("Please select a valid menu option!");
//            }
//        }
//    }
//
//    public ArrayList<Seller> readSellerDatabase() throws NoSellers {
//
//        if (getSellerCount() == 0) { //exits when no sellers exist.
//            throw new NoSellers("No Sellers Exist!");
//        }
//
//        //initialize variables;
//        BufferedReader bfr = null;
//        String line;
//        ArrayList<Seller> database = new ArrayList<Seller>();
//
//        //initializing iterating objects to use them outside the scope of try/catch;
//        Seller seller;
//        Store store;
//        Product product;
//
//        //used for indexing arraylists; incremented
//        int sellerIndex = -1;
//        int storeIndex = -1;
//
//        try {
//            bfr = new BufferedReader(new FileReader("./src/SellerDatabase.txt"));
//            while (true) {
//                line = bfr.readLine();
//
//                if (line == null || line == "") {
//                    break;
//                }
//
//                char identifier = line.charAt(0); //data processing
//
//                if (identifier == 42) {
//                    storeIndex = -1;
//                    try {
//                        int iD = Integer.parseInt(line.split(" ")[1]);
//                        seller = new Seller(iD);
//                        seller = seller;
//                        if (seller.getSellerIndex() != -1) {
//                            database.add(seller);
//                            sellerIndex = seller.getSellerIndex();
//                        }
//                    } catch (NoAccountError e) {
//                        return null;
//                    }
//                } else if (identifier == 43) {
//                    storeIndex++;
//                    store = new Store(line.split(" ")[1]);
//                    database.get(sellerIndex).addStore(storeIndex, store);
//                } else {
//                    try {
//                        product = new Product(line.split(", "));
//                        database.get(sellerIndex).getStores().get(storeIndex).addProduct(product);
//                    } catch (DataFormatException e) {
//                        JOptionPane.showMessageDialog(null, "Seller Database Malformed!");
//                        //System.out.println("Seller Database Malformed!");
//                    }
//                }
//
//            }
//
//            bfr.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return database;
//
//    }
//
//    public ArrayList<Product> getProductDatabase() {
//        ArrayList<Seller> database = null;
//        try {
//            database = readSellerDatabase();
//        } catch (NoSellers e) {
//            return null;
//        }
//        ArrayList<Product> productDatabase = new ArrayList<Product>();
//        for (Seller seller : database) {
//            for (Store store : seller.getStores()) {
//                for (Product product : store.getProducts()) {
//                    productDatabase.add(product);
//                }
//            }
//        }
//        return productDatabase;
//    }
//
//    public ArrayList<User> getInformation (boolean seller) {
//        BufferedReader bfr;
//        ArrayList<User> result = new ArrayList<User>();
//        User user;
//        String line;
//        try {
//            if (seller) {
//                bfr = new BufferedReader(new FileReader("./src/SellerLogin.txt"));
//            } else {
//                bfr = new BufferedReader(new FileReader("./src/BuyerLogin.txt"));
//            }
//            while (true) {
//
//
//                line = bfr.readLine();
//                if (line == null) {
//                    break;
//                }
//                user = new User(line.split(", "));
//                result.add(user);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (result.size() != 0) {
//            return result;
//        } else {
//            return null;
//        }
//    }
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


}
