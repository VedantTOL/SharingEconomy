import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

public class Server {
    //has all reading and writing function: remove those from other user Classes


    public static void main(String[] args) throws IOException, DataFormatException {
        boolean newWrite = false;
        ArrayList<Seller> sellerDatabase = readSellerDatabase();
        //ArrayList<Buyer> buyerDatabase = readBuyerDatabase();
        ArrayList<Product> productDatabase = getProductDatabase(sellerDatabase);
        ArrayList<User> loginDatabase;

        ServerSocket serverSocket = new ServerSocket(4242);
        Socket clientSocket = serverSocket.accept();
        System.out.println("Connected");
        //not required.
        User databaseReader = new User();
        InputStream input = clientSocket.getInputStream();
        BufferedReader bfr = new BufferedReader(new InputStreamReader(input));

        OutputStream output = clientSocket.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(output));
        byte action;
        String data = "";
        while (true) {
            action = (byte) input.read();

            if (action == 0b0) {
                //end thread
            } else if (action > 0b1000 ) {
                //request data
                if (action == 0b1100) {
                    //send all seller data
                    bw.write("sellerDatabase\n");
                    bw.flush();

                    if (newWrite) {
                        sellerDatabase = readSellerDatabase();
                    }

                    for (Seller seller: sellerDatabase) {
                        data = data.concat(seller.serverString());
                    }

                    bw.write(data);
                    bw.write("end\n");
                    bw.flush();
                } else if (action == 0b1110) {
                    //send login information
                    boolean isSeller = Boolean.parseBoolean(bfr.readLine());
                    loginDatabase = getInformation(isSeller);
                    bw.write("loginDatabase\n");
                    bw.flush();

                    String email = bfr.readLine();
                    String password = null;
                    for (User user: loginDatabase) {
                        if (user.getEmail().equals(email)) {
                            password = user.getPassword();
                            break;
                        }
                    }
                    bw.write(password);
                    bw.flush();


                }

            } else {
                //update data
                System.out.println("success");
                break;
            }

        }
        clientSocket.close();


    }

    public Server() throws IOException, NoSellers, DataFormatException {
    }
    public static ArrayList<Product> getProductDatabase(ArrayList<Seller> database) {

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

    public static ArrayList<Seller> readSellerDatabase() {
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
                        sellerIndex = seller.getUniqueIdentifier();
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
    public static ArrayList<Buyer> readBuyerDatabase() throws DataFormatException, IOException {

        ArrayList<Buyer> database = new ArrayList<Buyer>();
        //ArrayList<Product> productDatabase = getProductDatabase();

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

    public static ArrayList<User> getInformation(boolean seller) {
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
}
