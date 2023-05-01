import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.zip.DataFormatException;


public class Server {



    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4242);
        serverSocket.setReuseAddress(true);

        while (true) {

            Socket socket = null;

            try {
                socket = serverSocket.accept();
                System.out.println("Client connected");

                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                System.out.println("Now assigning a new thread to the client");

                Thread t = new ClientHandler(dis, dos, socket);

                t.start();
                System.out.println("Thread created");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

class ClientHandler extends Thread {
    final DataInputStream input;
    final DataOutputStream output;
    final Socket socket;


    ClientHandler(DataInputStream input, DataOutputStream output, Socket socket) throws IOException {
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
        this.socket = socket;
    }

    ClientHandler() {
        this.input = null;
        this.output = null;
        this.socket = null;
    }

    @Override
    public void run() {
        ClientHandler server = new ClientHandler();

        try {

            boolean newWrite = false;
            ArrayList<Seller> sellerDatabase = readSellerDatabase();
            ArrayList<Buyer> buyerDatabase = readBuyerDatabase();
            ArrayList<Product> productDatabase = getProductDatabase(sellerDatabase);
            ArrayList<User> loginDatabase;

            BufferedReader bfr = new BufferedReader(new InputStreamReader(input));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(output));
            String action;
            String data = "";

            boolean isSeller = Boolean.parseBoolean(bfr.readLine());
            System.out.println(isSeller);


            while (true) {
                data = "";
                action = bfr.readLine();

                if (action.equals("EXIT")) {
                    //end thread
                } else if (action.equals("sendSeller")) {
                    //send all seller data

                    if (newWrite) {
                        sellerDatabase = readSellerDatabase();
                    }

                    for (Seller seller : sellerDatabase) {
                        data = data.concat(seller.serverString());
                    }

                    bw.write(data);
                    bw.write("end\n");
                    bw.flush();
                } else if (action.equals("sendLogin")) {
                    //send login information
                    loginDatabase = getInformation(isSeller);

                    String email = bfr.readLine();
                    String password = bfr.readLine();
                    System.out.println(email);
                    User user = null;

                    for (User parse : loginDatabase) {
                        if (parse.getEmail().equals(email)) {
                            user = parse;
                            break;
                        }
                    }
                    if (user == null) {
                        bw.write("loginError\n");
                        bw.flush();
                    } else {
                        if (user.getPassword().equals(password)) {
                            bw.write(user.constructorString());
                            bw.write("\n");
                            bw.flush();
                        } else {
                            bw.write("loginError\n");
                        }
                    }
                    bw.flush();


                } else if (action.equals("sendBuyer")) {
                    //send buyer information
                    if (newWrite) {
                        buyerDatabase = readBuyerDatabase();
                    }
                    bw.write("buyerDatabase\n");
                    bw.flush();
                    for (Buyer buyer : buyerDatabase) {
                        data = data.concat(buyer.serverString());
                    }
                    bw.write(data);
                    bw.write("end\n");
                    bw.flush();
                } else if (action.equals("writeSeller")) {
                    ArrayList<Seller> temp = server.sellerServerRead(parseServer(bfr));
                    server.setSellerDatabase(temp);
                    newWrite = true;
                } else if (action.equals("writeBuyer")) {
                    server.setBuyerDatabase(server.buyerServerRead(parseServer(bfr)));
                    newWrite = true;
                } else if (action.equals("loginDatabase")) {
                    server.setLoginDetails(server.getUserInfo(bfr.readLine()), isSeller);
                    newWrite = true;
                } else if (action.equals("changeAccount")) {
                    bw.write("changeAccount\n");
                    bw.flush();
                    String user = bfr.readLine();
                    User change = new User(user.split(", "));
                    ArrayList<User> userDB = getInformation(isSeller);
                    for (User x: userDB) {
                        if (change.getUniqueIdentifier() == x.getUniqueIdentifier()) {
                            userDB.remove(x);
                            userDB.add(change);
                        }
                    }
                    updateLoginDatabase(userDB, isSeller);
                } else if (action.equals("deleteAccount")) {
                    bw.write("deleteAccount\n");
                    bw.flush();
                    String user = bfr.readLine();
                    User delete = new User(user.split(", "));
                    ArrayList<User> userDB = getInformation(isSeller);
                    for (User x: userDB) {
                        if (delete.getUniqueIdentifier() == x.getUniqueIdentifier()) {
                            userDB.remove(x);
                        }
                    }
                    updateLoginDatabase(userDB, isSeller);
                } else if (action.equals("getUniqueInt")) {
                    ArrayList<User> userDB = getInformation(isSeller);

                    bw.write(String.valueOf(userDB.size()));
                    bw.write("\n");
                    bw.flush();
                } else if (action.equals("confirmUser")) {
                    String nex = bfr.readLine();

                    User newUser = new User(nex.split(", "));
                    ArrayList<User> userDB = getInformation(isSeller);
                    userDB.add(newUser);
                    updateLoginDatabase(userDB, isSeller);
                }
            }

        } catch (IOException | DataFormatException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> parseServer(BufferedReader bfr) throws IOException {
        ArrayList<String> data = new ArrayList<String>();
        String line;
        do {
            line = bfr.readLine();
            if (line == "" || line == null || line.equals("end")) {
                break;
            }
            data.add(line);
        } while (true);
        return data;
    }

    public ArrayList<Seller> sellerServerRead (ArrayList<String> data) {
        ArrayList<Seller> database = new ArrayList<Seller>();

        //initializing iterating objects to use them outside the scope of try/catch;
        Seller seller;
        Store store;
        Product product;

        //used for indexing arraylists; incremented
        int sellerIndex = -1;
        int storeIndex = -1;
        for (String line: data) {
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
        return database;
    }
    public ArrayList<Buyer> buyerServerRead(ArrayList<String> data) {
        ArrayList<Buyer> database = new ArrayList<Buyer>();
        //ArrayList<Product> productDatabase = getProductDatabase();

        //String line;
        Buyer buyer = null;

        for (String line: data) {
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


        return database;
    }
    public User getUserInfo(String data) {
        return new User(data.split(", "));
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

    //complete
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

    public ArrayList<Seller> getSellerDatabase() {
        return sellerDatabase;
    }

    public void setSellerDatabase(ArrayList<Seller> sellerDatabase) {
        try {
            BufferedWriter toFile = new BufferedWriter(new FileWriter("./src/SellerDatabase.txt"));
            for (Seller seller : sellerDatabase) {
                toFile.write(seller.serverString());
                toFile.flush();
            }
            toFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.sellerDatabase = sellerDatabase;
    }

    public ArrayList<Buyer> getBuyerDatabase() {
        return buyerDatabase;
    }

    public void setBuyerDatabase(ArrayList<Buyer> buyerDatabase) {
        try {
            BufferedWriter toFile = new BufferedWriter(new FileWriter("./src/BuyerDatabase.txt"));
            for (Buyer buyer : buyerDatabase) {
                toFile.write(buyer.serverString());
                toFile.flush();
            }
            toFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.buyerDatabase = buyerDatabase;
    }

    public User getLoginDetails() {
        return loginDetails;
    }

    public void setLoginDetails(User loginDetails, boolean seller) {
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

        if (result.size() == 0) {
            return;
        }

        BufferedWriter toFile;
        String filename;

        if (seller) {
            filename = "./src/SellerLogin.txt";
        } else {
            filename = "./src/BuyerLogin.txt";
        }

        try {
            boolean newUser = false;
            toFile = new BufferedWriter(new FileWriter(filename));
            for (User k: result) {
                toFile.write(k.constructorString());
                toFile.write("\n");
            }
            toFile.close();

        } catch (IOException e) {
            return;
        }
        this.loginDetails = loginDetails;
    }

    public void updateLoginDatabase(ArrayList<User> userDB, boolean isSeller) {
        BufferedWriter toFile;
        String filename;

        if (isSeller) {
            filename = "./src/SellerLogin.txt";
        } else {
            filename = "./src/BuyerLogin.txt";
        }

        try {
            toFile = new BufferedWriter(new FileWriter(filename));
            for (User k: userDB) {
                toFile.write(k.constructorString());
                toFile.write("\n");
            }
            toFile.close();

        } catch (IOException e) {
            return;
        }

    }

    private ArrayList<Seller> sellerDatabase;
    private ArrayList<Buyer> buyerDatabase;
    private User loginDetails;

}

