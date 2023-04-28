import java.io.IOException;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

public class Client extends User {

    private byte action;

    public ArrayList<Seller> getSellerDatabase() {
        return sellerDatabase;
    }

    public void setSellerDatabase(ArrayList<Seller> sellerDatabase) {
        this.sellerDatabase = sellerDatabase;
    }

    public ArrayList<Buyer> getBuyerDatabase() {
        return buyerDatabase;
    }

    public void setBuyerDatabase(ArrayList<Buyer> buyerDatabase) {
        this.buyerDatabase = buyerDatabase;
    }

    public User getLoginDetails() {
        return loginDetails;
    }

    public void setLoginDetails(User loginDetails) {
        this.loginDetails = loginDetails;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private ArrayList<Seller> sellerDatabase;
    private ArrayList<Buyer> buyerDatabase;
    private User loginDetails;
    User user = null;
    public Client() throws IOException {
        this.sellerDatabase = new ArrayList<Seller>();
        this.buyerDatabase = new ArrayList<Buyer>();
        this.loginDetails = new User();
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        Socket socket = new Socket("localhost", 4242);
        OutputStream w = socket.getOutputStream();
        InputStream input = socket.getInputStream();
        BufferedReader bfr = new BufferedReader(new InputStreamReader(input));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(w));

        w.write(0b1100);
        w.flush();
        //w.close();
        String toServer = "";

        String action;
        int lineCount;
        String[] data;

        while (true) {
            toServer = "";
            action = bfr.readLine();
            System.out.println(action);
            if (action.equals("sellerDatabase")) {
                ArrayList<Seller> temp = client.sellerServerRead(parseServer(bfr));
                client.setSellerDatabase(temp);
            } else if (action.equals("buyerDatabase")) {
                client.setBuyerDatabase(client.buyerServerRead(parseServer(bfr)));
            } else if (action.equals("loginDatabase")) {
                client.setLoginDetails(client.getUserInfo(bfr.readLine()));
            } else if (action.equals ("writeSeller")) {
                bw.write("sellerDatabase\n");

                bw.flush();


                for (Seller seller : client.getSellerDatabase()) {
                    toServer = toServer.concat(seller.serverString());
                }

                bw.write(toServer);
                bw.write("end\n");
                bw.flush();
            } else if (action.equals("sendLogin")) {
                //send login information
                bw.write(client.getLoginDetails().constructorString());

            } else if (action.equals("sendBuyer")) {
                //send buyer information

                bw.write("buyerDatabase\n");
                bw.flush();
                for (Buyer buyer : client.getBuyerDatabase()) {
                    toServer = toServer.concat(buyer.serverString());
                }
                bw.write(toServer);
                bw.write("end\n");
                bw.flush();

            }
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

    public byte requestData(int option) {
        byte action = 0b1000;
        if (option == 1) { //requestSellerDatabase
            action = 0b1100;
        } else if (option == 2) {// requestBuyerDatabase {
            action = 0b1110;
        } else if (option == 3) {
            action = 0b1111;
        }
        return action;
    }
}
