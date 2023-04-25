import java.io.IOException;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

public class Client extends User {

    private ArrayList<Seller> sellerDatabase;
    private ArrayList<Buyer> buyerDatabase;
    private User loginDetails;

    User user = null;

    public Client() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 4242);
        OutputStream w = socket.getOutputStream();
        InputStream input = socket.getInputStream();
        BufferedReader bfr = new BufferedReader(new InputStreamReader(input));

        w.write(0b1100);
        w.flush();
        //w.close();

        String action;
        int lineCount;
        String[] data;

        while (true) {
            action = bfr.readLine();
            System.out.println(action);

            if (action.equals("sellerDatabase")) {
                ArrayList<Seller> sellerDatabase = sellerServerRead(parseServer(bfr));
                for (Seller seller: sellerDatabase) {
                    System.out.println(seller.serverString());
                }
            } else if (action.equals("buyerDatabase")) {
                //ArrayList<Buyer> buyerDatabase = buyerServerRead(parseServer(bfr));
            } else if (action.equals("loginDatabase")) {
                String password = bfr.readLine();
                if (password == null) {
                    //account does not exist
                }

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

    public static ArrayList<Seller> sellerServerRead (ArrayList<String> data) {
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
}
