import javax.swing.*;
import java.io.IOException;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

public class Client extends JComponent implements Runnable {
    public ArrayList<User> getLoginDatabase() {
        return loginDatabase;
    }

    public void setLoginDatabase(ArrayList<User> loginDatabase) {
        this.loginDatabase = loginDatabase;
    }

    private ArrayList<User> loginDatabase;

    public int getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(int uniqueID) {
        this.uniqueID = uniqueID;
    }

    private int uniqueID;
    private static DataInputStream dis;
    private static DataOutputStream dos;
    private static Socket socket;

    private JButton customer;
    private JButton seller;
    private JButton exit;
    private JFrame frame;

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

    public String[] getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String[] emailPassword) {
        this.emailPassword = emailPassword;
    }

    private String[] emailPassword = new String[2];


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

    public static ArrayList<Seller> sellerServerRead(ArrayList<String> data) {
        ArrayList<Seller> database = new ArrayList<Seller>();

        //initializing iterating objects to use them outside the scope of try/catch;
        Seller seller;
        Store store;
        Product product;

        //used for indexing arraylists; incremented
        int sellerIndex = -1;
        int storeIndex = -1;
        for (String line : data) {
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

    public static ArrayList<Buyer> buyerServerRead(ArrayList<String> data) {
        ArrayList<Buyer> database = new ArrayList<Buyer>();
        //ArrayList<Product> productDatabase = getProductDatabase();

        //String line;
        Buyer buyer = null;

        for (String line : data) {
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

    public boolean sendServer(String option) throws IOException {
        BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
        String toServer = "";
        User user;

        if (option.equals("requestSellerDatabase")) {
            dos.writeUTF("sendSeller\n");
        } else if (option.equals("requestBuyerDatabase")) {
            dos.writeUTF("sendBuyer\n");
        } else if (option.equals("login")) {
            dos.writeUTF("sendLogin\n");
        } else if (option.equals("seller")) {
            dos.writeUTF("true\n");
        } else if (option.equals("buyer")) {
            dos.writeUTF("false\n");
        } else if (option.equals("updateBuyer")) {
            dos.writeUTF("writeBuyer\n");
        } else if (option.equals("updateSeller")) {
            dos.writeUTF("writeSeller\n");
        } else if (option.charAt(0) == '*') {
            dos.writeUTF("loginDatabase\n");
            bw.flush();
            dos.writeUTF(option.substring(1, option.length() - 1));
            dos.writeUTF("\n");
            bw.flush();
        } else if (option.charAt(0) == '-') {
            dos.writeUTF("changeAccount\n");
            bw.flush();
        } else if (option.charAt(0) == '+') {
            dos.writeUTF("deleteAccount\n");
            bw.flush();
        } else if (option.equals("addUser")) {
            dos.writeUTF("getUniqueInt\n");
            bw.flush();
            setUniqueID(Integer.parseInt(bfr.readLine()));
        } else if (option.equals("confirmUser")) {
            dos.writeUTF("confirmUser\n");
            bw.flush();
            dos.writeUTF(loginDetails.constructorString());
        }

        bw.flush();

        String action = bfr.readLine();

        if (action.equals("sellerDatabase")) {
            ArrayList<Seller> temp = this.sellerServerRead(parseServer(bfr));
            this.setSellerDatabase(temp);
        } else if (action.equals("buyerDatabase")) {
            this.setBuyerDatabase(this.buyerServerRead(parseServer(bfr)));
        } else if (action.equals("loginDatabase")) {
            this.setLoginDetails(this.getUserInfo(bfr.readLine()));
        } else if (action.equals("writeSeller")) {
            dos.writeUTF("sellerDatabase\n");
            bw.flush();
            for (Seller seller : this.getSellerDatabase()) {
                toServer = toServer.concat(seller.serverString());
            }
            dos.writeUTF(toServer);
            dos.writeUTF("end\n");
            bw.flush();
        } else if (action.equals("sendLogin")) {
            //send login information
            dos.writeUTF(emailPassword[0]);
            dos.writeUTF("\n");
            bw.flush();
            dos.writeUTF(emailPassword[1]);
            dos.writeUTF("\n");
            bw.flush();

            String loginConfirmation = bfr.readLine();
            if (loginConfirmation.equals("loginError")) {
                return false;
            } else {
                setLoginDetails(new User(loginConfirmation.split(", ")));
                return true;
            }

        } else if (action.equals("sendBuyer")) {
            //send buyer information
            dos.writeUTF("buyerDatabase\n");
            bw.flush();
            for (Buyer buyer : this.getBuyerDatabase()) {
                toServer = toServer.concat(buyer.serverString());
            }
            dos.writeUTF(toServer);
            dos.writeUTF("end\n");
            bw.flush();
        } else if (action.equals("changeAccount")) {
            dos.writeUTF(option.substring(1, option.length() - 1));
            bw.flush();
        } else if (action.equals("deleteAccount")) {
            dos.writeUTF(option.substring(1, option.length() - 1));
            bw.flush();
        }
        return false;
    }


    private ArrayList<Seller> sellerDatabase;
    private ArrayList<Buyer> buyerDatabase;
    private User loginDetails;
    User user = null;
    private Client client;

    public Client() throws IOException {
        this.sellerDatabase = new ArrayList<Seller>();
        this.buyerDatabase = new ArrayList<Buyer>();
        this.loginDetails = new User();
        this.client = this;
    }

    public static void main(String[] args) {
        try {
            SwingUtilities.invokeLater(new Client());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == customer) {
                // creates new JFrame for a customer to log in
                try {
                    dos.writeUTF("false\n");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                LoginOptionCustomer loginOptionCustomer = new LoginOptionCustomer();
                loginOptionCustomer.pack();
                loginOptionCustomer.setVisible(true);
                frame.dispose();
            } else if (e.getSource() == exit) {
                frame.dispose();
            } else if (e.getSource() == seller) {
                // creates new JFrame for a seller to log in
                try {
                    dos.writeUTF("false\n");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                LoginOptionSeller loginOptionSeller = new LoginOptionSeller();
                loginOptionSeller.pack();
                loginOptionSeller.setVisible(true);
                frame.dispose();
            }
        }

    };

    @Override
    public void run() {
        try {
            socket = new Socket("localhost", 4242);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream((socket.getOutputStream()));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Make sure the server is running before trying to connect!", "ERROR! Run Server!", JOptionPane.ERROR_MESSAGE);
            frame.dispose();
        }
        frame = new JFrame("Welcome! Please click the button according to your information!");
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        JPanel topPanel = topPanel();
        content.add(topPanel, BorderLayout.NORTH);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private JPanel topPanel() {
        customer = newButton("Buyer");
        seller = newButton("Seller");
        exit = newButton("Exit");
        JPanel top = new JPanel();
        top.add(customer);
        top.add(seller);
        top.add(exit);
        return top;
    }

    private JButton newButton(String text) {
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        return button;
    }

    private static class LoginOptionCustomer extends JFrame {
        private JButton createAccountButton;
        private JButton loginButton;

        public LoginOptionCustomer() {
            super("Login or Create Account");
            createAccountButton = new JButton("Create Account");
            loginButton = new JButton("Login");

            JPanel panel = new JPanel();
            panel.add(createAccountButton);
            panel.add(loginButton);
            add(panel);

            createAccountButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    BuyerCreateAccountCredentials loginCredentials = new BuyerCreateAccountCredentials();
                    loginCredentials.setVisible(true);
                    dispose();

                }
            });
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    BuyerLoginCredentials loginCredentials = new BuyerLoginCredentials();
                    loginCredentials.setVisible(true);
                    dispose();

                }
            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);
        }
    }

    private static class buyerEditAccount extends JFrame {
        private JButton editEmailButton;
        private JButton editPasswordButton;
        private JButton previousPageButton;
        private Buyer buyer;

        public buyerEditAccount(Buyer buyer) {
            super("Edit Account");
            this.buyer = buyer;
            editEmailButton = new JButton("Enter new email");
            editPasswordButton = new JButton("Enter new password");
            previousPageButton = new JButton("Go back");

            JPanel panel = new JPanel();
            panel.add(editEmailButton);
            panel.add(editPasswordButton);
            panel.add(previousPageButton);
            add(panel);

            editEmailButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    String newEmail = JOptionPane.showInputDialog("Enter your new email");
                    buyer.setEmail(newEmail);
                    dispose();

                }

                // maybe update database
            });

            editPasswordButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    String newPassword = JOptionPane.showInputDialog("Enter your new password");
                    buyer.setPassword(newPassword);
                    dispose();

                }

                // maybe update database
            });

        }
    }

    private static class BuyerGUI extends JFrame {
        private JButton marketPlaceButton;
        private JButton shopBySellerButton;
        private User user;

        private ArrayList<Seller> requestSellerDatabase() {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
            BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
            try {
                bw.write("sendSeller\n");
                bw.flush();
                return sellerServerRead(parseServer(bfr));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        private Buyer createBuyer(User user, double balance) {
            Buyer buyer = new Buyer(user.getUniqueIdentifier(), user.getEmail(), user.getPassword(), user.getName(),
                    user.getAge(), balance);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
            BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
            ArrayList<Buyer> db = null;
            try {
                bw.write("sendBuyer\n");
                bw.flush();
                db =  buyerServerRead(parseServer(bfr));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            for (Buyer k: db) {
                if (k.getUniqueIdentifier() == buyer.getUniqueIdentifier()) {
                    buyer.setPurchases(k.getPurchases());
                    buyer.setShoppingCart(k.getShoppingCart());
                }
            }

            return buyer;

        }

        public BuyerGUI(User user) {
            super("Would you like to view the whole marketplace or shop by seller?");
            marketPlaceButton = new JButton("View the whole marketplace");
            shopBySellerButton = new JButton("Shop by seller");

            this.user = user;
            JPanel panel = new JPanel();
            panel.add(marketPlaceButton);
            panel.add(shopBySellerButton);
            add(panel);

            marketPlaceButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // asking for balance before creating buyer object
                    String bal = JOptionPane.showInputDialog(null, "What is your budget?",
                            "Budget Information", JOptionPane.QUESTION_MESSAGE);

                    double balance = 0;
                    try {
                        balance = Double.parseDouble(bal);
                        Buyer buyer = createBuyer(user, balance);

                        // generate new marketplace JFrame here to show what is in the marketplace
                        marketPlace marketPlace = new marketPlace(buyer);
                        marketPlace.setVisible(true);
                        dispose();

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Please enter a valid number!",
                                "NumberError!", JOptionPane.ERROR_MESSAGE);
                    }
                }


            });

            shopBySellerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // asking for balance before creating buyer object
                    String bal = JOptionPane.showInputDialog(null, "What is your budget?",
                            "Budget Information", JOptionPane.QUESTION_MESSAGE);

                    double balance = Double.parseDouble(bal);

                    // creating buyer object from user data that was generated in the login frame
                    Buyer buyer = createBuyer(user, balance);

                    // generate list of sellers from database here:
                    ArrayList<Seller> sellers = requestSellerDatabase();

                    // generate new shop by seller Jframe here to allow the buyer to search for a seller
                    shopBySeller bySeller = new shopBySeller(sellers, buyer);
                    bySeller.setVisible(true);
                    dispose();
                }
            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);

        }
    }


    private static class marketPlace extends JFrame {
        private JButton viewAllProductsButton;
        private JButton searchForProductsButton;
        private JButton viewCartButton;
        private JButton viewPurchasesButton;
        private Buyer buyer;

        public marketPlace(Buyer buyer) {
            super("View all products or search for a specific product?");
            viewAllProductsButton = new JButton("View all products");
            searchForProductsButton = new JButton("Search for a specific product");
            viewCartButton = new JButton("View your cart");
            viewPurchasesButton = new JButton("View your purchases");
            this.buyer = buyer;
            JPanel panel = new JPanel();
            panel.add(viewCartButton);
            panel.add(viewPurchasesButton);
            panel.add(viewAllProductsButton);
            panel.add(searchForProductsButton);
            add(panel);

            viewAllProductsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // read database in to here:

                    ArrayList<Seller> database = requestSellerDatabase();

                    int choice = 1;

                    ArrayList<Product> productList = processProduct(buyer, choice, database);
                    if (productList == null) {
                        JOptionPane.showMessageDialog(null, "Sorry! Sellers have not yet posted anything to the marketplace.\n" +
                                        "Come back later when sellers have stocked their stores!\n" + "Logging you out...\n",
                                "Empty Marketplace!", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        addToCartOrPurchase addToCartOrPurchase = new addToCartOrPurchase(productList, buyer, database);
                        addToCartOrPurchase.setVisible(true);
                        dispose();
                    }

                }

                private ArrayList<Product> processProduct(Buyer buyer, int choice, ArrayList<Seller> database) {
                    return buyer.viewMarketPlace(choice, database);
                }

                private ArrayList<Seller> requestSellerDatabase() {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
                    BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
                    try {
                        bw.write("sendSeller\n");
                        bw.flush();
                        return sellerServerRead(parseServer(bfr));

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            });

            searchForProductsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {


                    //getting stuck here
                    ArrayList<Seller> database = requestSellerDatabase();


                    int choice = 2;

                    ArrayList<Product> productList = buyer.viewMarketPlace(choice, database);
                    if (productList == null) {
                        JOptionPane.showMessageDialog(null, "Sorry! Sellers have not yet posted anything to the marketplace.\n" +
                                        "Come back later when sellers have stocked their stores!\n" + "Logging you out...\n",
                                "Empty Marketplace!", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        addToCartOrPurchase addToCartOrPurchase = new addToCartOrPurchase(productList, buyer, database);
                        addToCartOrPurchase.setVisible(true);
                        dispose();
                    }

                }

                private ArrayList<Seller> requestSellerDatabase() {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
                    BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
                    try {
                        bw.write("sendSeller\n");
                        bw.flush();
                        return sellerServerRead(parseServer(bfr));

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            });

            viewCartButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ArrayList<Buyer> db = requestBuyerDatabase();

                    buyerCart buyerCart = new buyerCart(buyer, db);
                    buyerCart.setVisible(true);

                }

                private ArrayList<Buyer> requestBuyerDatabase() {

                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
                    BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
                    try {
                        bw.write("sendBuyer\n");
                        bw.flush();
                        return buyerServerRead(parseServer(bfr));

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            viewPurchasesButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String purchasesString = "";
                    for (ProductPurchase productPurchase : buyer.getPurchases()) {
                        purchasesString = purchasesString.concat(productPurchase.viewOrder());
                        purchasesString = purchasesString.concat("\n");

                    }

                    JOptionPane.showMessageDialog(null, purchasesString,
                            "Your Purchases", JOptionPane.INFORMATION_MESSAGE);

                }
            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);

        }
    }


    private static class shopBySeller extends JFrame {
        private JComboBox<String> sellerComboBox;
        private ArrayList<Seller> sellers;
        private JButton selectSellerButton;
        private JButton viewCartButton;
        private JButton viewPurchasesButton;
        private Buyer buyer;


        public shopBySeller(ArrayList<Seller> sellers, Buyer buyer) {
            super("Select a Seller");
            this.sellers = sellers;
            this.buyer = buyer;

            // Create GUI components
            sellerComboBox = new JComboBox<>();
            for (Seller seller : sellers) {
                sellerComboBox.addItem(seller.getName());
            }

            viewCartButton = new JButton("View your cart");
            viewPurchasesButton = new JButton("View your purchases");
            selectSellerButton = new JButton("Select seller");
            JPanel panel = new JPanel();
            panel.add(sellerComboBox);
            panel.add(selectSellerButton);
            panel.add(viewCartButton);
            panel.add(viewPurchasesButton);
            add(panel);


            selectSellerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedSeller = (String) sellerComboBox.getSelectedItem();

                    ArrayList<Product> productList = null;

                    ArrayList<Store> sellerStores = new ArrayList<>();
                    for (Seller seller : sellers) {
                        if (seller.getName().equals(selectedSeller)) {
                            productList = processProduct(seller);
                        }
                    }


                    addToCartOrPurchase addToCartOrPurchase = new addToCartOrPurchase(productList, buyer, sellers);
                    addToCartOrPurchase.setVisible(true);
                    dispose();

                }

                private ArrayList<Product> processProduct(Seller seller) {
                    ArrayList<Product> result = new ArrayList<>();

                    for (Store store : seller.getStores()) {
                        for (Product product : store.getProducts()) {
                            result.add(product);
                        }
                    }
                    return result;

                }


            });

            viewCartButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ArrayList<Buyer> db = requestBuyerDatabase();

                    buyerCart buyerCart = new buyerCart(buyer, db);
                    buyerCart.setVisible(true);

                }

                private ArrayList<Buyer> requestBuyerDatabase() {

                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
                    BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
                    try {
                        bw.write("sendBuyer\n");
                        bw.flush();
                        return buyerServerRead(parseServer(bfr));

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            viewPurchasesButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String purchasesString = "";
                    for (ProductPurchase productPurchase : buyer.getPurchases()) {
                        purchasesString = purchasesString + productPurchase.getName() + " " + productPurchase.getPrice() + "\n";
                    }

                    JOptionPane.showMessageDialog(null, purchasesString,
                            "Your Purchases", JOptionPane.INFORMATION_MESSAGE);

                }
            });

            // Set JFrame properties
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }

    private static class addToCartOrPurchase extends JFrame {
        private void coreProcess(Buyer buyer, ArrayList<Buyer> buyerDatabase) {
            for (Buyer c : buyerDatabase) {
                if (c.getUniqueIdentifier() == buyer.getUniqueIdentifier()) {
                    buyerDatabase.remove(c);
                    buyerDatabase.add(buyer);
                    break;
                }
            }

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
            BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
            String toServer = "";

            try {
                toServer = "";
                bw.write("writeBuyer\n");
                for (Buyer k : buyerDatabase) {
                    toServer = toServer.concat(k.serverString());
                    toServer = toServer.concat("\n");
                }
                bw.write(toServer);
                bw.write("end\n");
                bw.flush();
                toServer = "";
                bw.write("writeSeller\n");
                for (Seller m: sellers) {
                    toServer = toServer.concat(m.serverString());
                }
                bw.write(toServer);
                bw.write("end\n");
                bw.flush();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private ArrayList<Seller> requestSellerDatabase() {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
            BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
            try {
                bw.write("sendSeller\n");
                bw.flush();
                return sellerServerRead(parseServer(bfr));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        private ArrayList<Buyer> requestBuyerDatabase() {

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
            BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
            try {
                bw.write("sendBuyer\n");
                bw.flush();
                return buyerServerRead(parseServer(bfr));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        private JButton addToCartButton;
        private JButton purchaseNowButton;
        private JButton previousPageButton;
        private JComboBox<String> comboBox;
        private ArrayList<Seller> sellers;


        public addToCartOrPurchase(ArrayList<Product> productList, Buyer buyer, ArrayList<Seller> sellers) {
            super("Available Products");

            addToCartButton = new JButton("Add to cart");
            purchaseNowButton = new JButton("Purchase now");
            previousPageButton = new JButton("Previous page");
            comboBox = new JComboBox<>();
            this.sellers = sellers;

            for (Product product : productList) {
                comboBox.addItem(product.marketplaceString());
            }

            JPanel panel = new JPanel();
            panel.add(addToCartButton);
            panel.add(purchaseNowButton);
            panel.add(previousPageButton);
            panel.add(comboBox);
            add(panel);

            addToCartButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedItem = (String) comboBox.getSelectedItem();

                    Product product1 = null;
                    for (Product product : productList) {
                        if (selectedItem.equals(product.marketplaceString())) {
                            product1 = product;
                        }
                    }

                    Store store = buyer.viewStore(product1, sellers);

                    int quantity;
                    String quantityForCart;
                    do {
                        do {
                            quantityForCart = JOptionPane.showInputDialog(null,
                                    "How many of " + product1.getName() + " would you like to add?\n",
                                    "Add to Cart", JOptionPane.QUESTION_MESSAGE);
                            if (quantityForCart == null || quantityForCart.isEmpty()) {
                                JOptionPane.showMessageDialog(null, "Please enter a number before continuing!",
                                        "ERROR!", JOptionPane.ERROR_MESSAGE);
                            }
                        } while (quantityForCart == null || quantityForCart.isEmpty());

                        quantity = readInt(quantityForCart);
                    } while (quantity == -1);

                    ArrayList<Buyer> updateBuyer = requestBuyerDatabase();

                    buyer.addToShoppingCart(product1, store, quantity);

                    coreProcess(buyer, updateBuyer);

                    continueShoppingEtc continueShoppingEtc = new continueShoppingEtc(buyer);
                    continueShoppingEtc.setVisible(true);
                    dispose();
                }

            });


            purchaseNowButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed (ActionEvent e){
                    String selectedItem = (String) comboBox.getSelectedItem();


                    Product product1 = null;
                    for (Product product : productList) {
                        if (selectedItem.equals(product.marketplaceString())) {
                            product1 = product;
                        }
                    }

                    // read database from server here:
                    ArrayList<Seller> database;
                    database = requestSellerDatabase();

                    Store store = buyer.viewStore(product1, database);


                    int numProductsForPurchase;
                    String numProductsForPurchases;
                    do {
                        do {
                            numProductsForPurchases = JOptionPane.showInputDialog(null,
                                    "How many of " + product1.getName() + " would you like to purchase?\n",
                                    "Purchase now", JOptionPane.QUESTION_MESSAGE);
                            if (numProductsForPurchases == null || numProductsForPurchases.isEmpty()) {
                                JOptionPane.showMessageDialog(null, "Please enter an option before continuing!",
                                        "ERROR!", JOptionPane.ERROR_MESSAGE);
                            }
                        } while (numProductsForPurchases == null || numProductsForPurchases.isEmpty());

                        numProductsForPurchase = readInt(numProductsForPurchases);
                    } while (numProductsForPurchase == -1);

                    buyer.buyProduct(product1, numProductsForPurchase, store, database);

                    ArrayList<Buyer> buyerDatabase = requestBuyerDatabase();

//                    for (ProductPurchase k: buyer.getPurchases()) {
//                        System.out.println(k.viewOrder());
//                    }

                    coreProcess(buyer, buyerDatabase);

                    continueShoppingEtc continueShoppingEtc = new continueShoppingEtc(buyer);
                    continueShoppingEtc.setVisible(true);
                    dispose();

                }
            });

            previousPageButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed (ActionEvent e){

                    marketPlace marketPlace = new marketPlace(buyer);
                    marketPlace.setVisible(true);
                    dispose();


                }
            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            pack();

            setLocationRelativeTo(null);

        }
    }

    private static class continueShoppingEtc extends JFrame {
        private JButton continueShoppingButton;
        private JButton viewCartButton;
        private JButton viewPurchasesButton;
        private JButton logOutButton;


        public continueShoppingEtc(Buyer buyer) {
            super("Continue Shopping?");

            continueShoppingButton = new JButton("Continue shopping: Marketplace menu");
            viewCartButton = new JButton("View your cart");
            viewPurchasesButton = new JButton("View your purchases");
            logOutButton = new JButton("Log Out");

            JPanel panel = new JPanel();
            panel.add(continueShoppingButton);
            panel.add(viewCartButton);
            panel.add(viewPurchasesButton);
            panel.add(logOutButton);
            add(panel);

            continueShoppingButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    marketPlace marketPlace = new marketPlace(buyer);
                    marketPlace.setVisible(true);
                    dispose();
                }
            });

            viewCartButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ArrayList<Buyer> db = requestBuyerDatabase();

                    buyerCart buyerCart = new buyerCart(buyer, db);
                    buyerCart.setVisible(true);

                }

                private ArrayList<Buyer> requestBuyerDatabase() {

                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
                    BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
                    try {
                        bw.write("sendBuyer\n");
                        bw.flush();
                        return buyerServerRead(parseServer(bfr));

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            viewPurchasesButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String purchasesString = "";
                    for (ProductPurchase productPurchase : buyer.getPurchases()) {
                        purchasesString = purchasesString.concat(productPurchase.viewOrder());
                        purchasesString = purchasesString.concat("\n");
                    }

                    JOptionPane.showMessageDialog(null, purchasesString,
                            "Your Purchases", JOptionPane.INFORMATION_MESSAGE);

                }
            });

            logOutButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // maybe some kind of method here that writes all changes to the database (may not be needed)
                    JOptionPane.showMessageDialog(null, "Thank you, come again!",
                            "Seeya!", JOptionPane.INFORMATION_MESSAGE);

                    dispose();

                }

            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);

        }
    }

    private static class buyerCart extends JFrame {

        private void coreProcess(Buyer buyer, ArrayList<Buyer> buyerDatabase) {
            for (Buyer c : buyerDatabase) {
                if (c.getUniqueIdentifier() == buyer.getUniqueIdentifier()) {
                    buyerDatabase.remove(c);
                    buyerDatabase.add(buyer);
                    break;
                }
            }

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
            BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
            String toServer = "";

            try {
                bw.write("writeBuyer\n");
                for (Buyer k : buyerDatabase) {
                    toServer = toServer.concat(k.serverString());
                    toServer = toServer.concat("\n");
                }
                bw.write(toServer);
                bw.write("end\n");
                bw.flush();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private ArrayList<Seller> requestSellerDatabase() {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
            BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
            try {
                bw.write("sendSeller\n");
                bw.flush();
                return sellerServerRead(parseServer(bfr));


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        private ArrayList<Buyer> requestBuyerDatabase() {

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
            BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
            try {
                bw.write("sendBuyer\n");
                bw.flush();
                return buyerServerRead(parseServer(bfr));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private JButton removeItemButton;
        private JButton purchaseCartButton;
        private JButton previousPageButton;
        private JComboBox<String> comboBox;

        private ArrayList<Buyer> buyerDatabase;

        public buyerCart(Buyer buyer, ArrayList<Buyer> buyerDatabase) {
            super("Your Shopping Cart");
            removeItemButton = new JButton("Remove item");
            purchaseCartButton = new JButton("Purchase cart");
            previousPageButton = new JButton("Previous page");
            comboBox = new JComboBox<>();
            this.buyerDatabase = buyerDatabase;

            for (ProductPurchase product : buyer.getShoppingCart()) {
                comboBox.addItem(product.viewOrder());
            }

            JPanel panel = new JPanel();
            panel.add(removeItemButton);
            panel.add(purchaseCartButton);
            panel.add(previousPageButton);
            panel.add(comboBox);
            add(panel);

            removeItemButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedItem = (String) comboBox.getSelectedItem();

                    for (ProductPurchase product : buyer.getShoppingCart()) {
                        if (selectedItem.equals(product.toString())) {
                            buyer.removeFromShoppingCart(product);
                            comboBox.removeItem(selectedItem);
                        }
                    }
                    panel.updateUI();

                    coreProcess(buyer, buyerDatabase);

                }
            });

            purchaseCartButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // read database in to here:
                    ArrayList<Seller> database = requestSellerDatabase();
                    int result = 0;
                    do {
                        result = buyer.purchaseCart(database);
                    } while (result == 1);

                    comboBox.removeAll();
                    dispose();

                    coreProcess(buyer, buyerDatabase);

                }

            });

            previousPageButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);
        }
    }

    private static class LoginOptionSeller extends JFrame {
        private JButton createAccountButton;
        private JButton loginButton;

        public LoginOptionSeller() {
            super("Login or Create Account");
            createAccountButton = new JButton("Create Account");
            loginButton = new JButton("Login");

            JPanel panel = new JPanel();
            panel.add(createAccountButton);
            panel.add(loginButton);
            add(panel);

            createAccountButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SellerCreateAccountCredentials loginCredentials = new SellerCreateAccountCredentials();
                    loginCredentials.setVisible(true);
                    dispose();

                }
            });

            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sellerLoginCredentials loginCredentials = new sellerLoginCredentials();
                    loginCredentials.setVisible(true);
                    dispose();

                }
            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);
        }
    }

    private static class BuyerCreateAccountCredentials extends JFrame {
        private JTextField emailField;
        private JPasswordField passwordField;
        private JTextField nameField;
        private JTextField ageField;
        private JButton loginButton;
        private User user;

        public BuyerCreateAccountCredentials() {
            super("Enter New Login Credentials");
            emailField = new JTextField(20);
            passwordField = new JPasswordField(20);
            nameField = new JTextField(20);
            ageField = new JTextField(3);
            loginButton = new JButton("Enter");
            JPanel panel = new JPanel();
            panel.add(new JLabel("Email:"));
            panel.add(emailField);
            panel.add(new JLabel("Password:"));
            panel.add(passwordField);
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Age:"));
            panel.add(ageField);
            panel.add(loginButton);
            add(panel);

            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    User loginSuccess = null;
                    try {
                        char[] temp = passwordField.getPassword();
                        loginSuccess = processLogin(emailField.getText(), new String(temp), nameField.getText(), Integer.parseInt(ageField.getText()));
                        System.out.println("reached");
                        BuyerGUI buyerGUI = new BuyerGUI(loginSuccess);
                        buyerGUI.setVisible(true);
                        dispose();

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }

                private User processLogin(String email, String password, String name, int age) throws IOException {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
                    BufferedReader btemp = new BufferedReader(new InputStreamReader(dis));
                    bw.write("getUniqueInt\n");
                    bw.flush();
                    int uniqueID = Integer.parseInt(btemp.readLine());
                    System.out.println(uniqueID);
                    User user = new User(uniqueID, email, password, name, age);
                    bw.write("confirmUser\n");
                    bw.write(user.constructorString());
                    bw.write("\n");
                    bw.flush();
                    return user;
                }
            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);
        }
    }

    private static class BuyerLoginCredentials extends JFrame {
        private JTextField emailField;
        private JPasswordField passwordField;
        private JButton loginButton;

        public BuyerLoginCredentials() {
            super("Enter New Login Credentials");
            emailField = new JTextField(20);
            passwordField = new JPasswordField(20);

            loginButton = new JButton("Enter");
            JPanel panel = new JPanel();
            panel.add(new JLabel("Email:"));
            panel.add(emailField);
            panel.add(new JLabel("Password:"));
            panel.add(passwordField);
            panel.add(loginButton);
            add(panel);

            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        char[] temp = passwordField.getPassword();
                        User loginSuccess = processLogin(emailField.getText(), new String(temp));


                        if (loginSuccess != null) {
                            BuyerGUI buyerGUI = new BuyerGUI(loginSuccess);
                            buyerGUI.setVisible(true);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "Incorrect Email or Password. Please try again!", "LoginError!", JOptionPane.ERROR_MESSAGE);
                        }

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                private User processLogin(String email, String password) throws IOException {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
                    bw.write("sendLogin\n");
                    bw.write(email);
                    bw.write("\n");
                    bw.flush();
                    bw.write(password);
                    bw.write("\n");
                    bw.flush();
                    System.out.println(password);

                    BufferedReader btemp = new BufferedReader(new InputStreamReader(dis));
                    String loginConfirmation = btemp.readLine();
                    if (loginConfirmation.equals("loginError")) {
                        return null;
                    } else {
                        System.out.println(loginConfirmation);
                        return new User(loginConfirmation.split(", "));
                    }

                }
            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);
        }
    }


    private static class SellerCreateAccountCredentials extends JFrame {
        private JTextField emailField;
        private JPasswordField passwordField;
        private JTextField nameField;
        private JTextField ageField;
        private JButton loginButton;
        private User user;

        public SellerCreateAccountCredentials() {
            super("Enter New Login Credentials");
            emailField = new JTextField(20);
            passwordField = new JPasswordField(20);
            nameField = new JTextField(20);
            ageField = new JTextField(3);
            loginButton = new JButton("Enter");
            JPanel panel = new JPanel();
            panel.add(new JLabel("Email:"));
            panel.add(emailField);
            panel.add(new JLabel("Password:"));
            panel.add(passwordField);
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Age:"));
            panel.add(ageField);
            panel.add(loginButton);
            add(panel);

            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    User loginSuccess = null;
                    try {
                        char[] temp = passwordField.getPassword();
                        loginSuccess = processLogin(emailField.getText(), new String(temp), nameField.getText(), Integer.parseInt(ageField.getText()));

                        SellerGUI sellerGUI = new SellerGUI(loginSuccess);
                        sellerGUI.setVisible(true);
                        dispose();

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }

                private User processLogin(String email, String password, String name, int age) throws IOException {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
                    BufferedReader btemp = new BufferedReader(new InputStreamReader(dis));
                    bw.write("getUniqueInt\n");
                    bw.flush();
                    int uniqueID = Integer.parseInt(btemp.readLine());
                    System.out.println(uniqueID);
                    User user = new User(uniqueID, email, password, name, age);
                    bw.write("confirmUser\n");
                    bw.write(user.constructorString());
                    bw.write("\n");
                    bw.flush();
                    return user;
                }


            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);
        }
    }

    private static class sellerLoginCredentials extends JFrame {
        private JTextField emailField;
        private JPasswordField passwordField;
        private JButton loginButton;

        public sellerLoginCredentials() {
            super("Enter New Login Credentials");
            emailField = new JTextField(20);
            passwordField = new JPasswordField(20);

            loginButton = new JButton("Enter");
            JPanel panel = new JPanel();
            panel.add(new JLabel("Email:"));
            panel.add(emailField);
            panel.add(new JLabel("Password:"));
            panel.add(passwordField);
            panel.add(loginButton);
            add(panel);

            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    User user = null;
                    try {
                        char[] temp = passwordField.getPassword();
                        User loginSuccess = processLogin(emailField.getText(), new String(temp));

                        if (loginSuccess != null) {
                            SellerGUI sellerGUI = new SellerGUI(loginSuccess);
                            sellerGUI.setVisible(true);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "Incorrect Email or Password. Please try again!", "LoginError!", JOptionPane.ERROR_MESSAGE);
                        }
                        // maybe pass the User as an argument to the BuyerGUI class, so we can use it in marketplace

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                private User processLogin(String email, String password) throws IOException {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
                    bw.write("sendLogin\n");
                    bw.write(email);
                    bw.write("\n");
                    bw.flush();
                    bw.write(password);
                    bw.write("\n");
                    bw.flush();
                    System.out.println(password);

                    BufferedReader btemp = new BufferedReader(new InputStreamReader(dis));
                    String loginConfirmation = btemp.readLine();
                    if (loginConfirmation.equals("loginError")) {
                        return null;
                    } else {
                        System.out.println(loginConfirmation);
                        return new User(loginConfirmation.split(", "));
                    }

                }


            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);
        }
    }


    public static int readInt(String input) {
        int result;
        try {
            result = Integer.parseInt(input);
            return result;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid Integer!",
                    "ERROR!", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }


    private static class SellerGUI extends JFrame {

        private JButton addButton;
        private JButton deleteButton;
        private JButton editButton;
        private JButton statisticsButton;
        private JButton editAccountButton;
        private JButton deleteAccountButton;
        private JButton logoutButton;

        private Seller createSeller(User user) {
            Seller seller = new Seller(user.getUniqueIdentifier(), user.getEmail(), user.getPassword(), user.getName(),
                    user.getAge(), null);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
            BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
            ArrayList<Seller> db = null;
            try {
                bw.write("sendSeller\n");
                bw.flush();
                db =  sellerServerRead(parseServer(bfr));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            for (Seller k: db) {
                if (k.getUniqueIdentifier() == seller.getUniqueIdentifier()) {
                    seller.setStores(k.getStores());
                }
            }
            return seller;
        }

        public SellerGUI(User user) {
            super("Seller Menu: What actions would you like to take?");

            addButton = new JButton("Add Store");
            deleteButton = new JButton("Delete Store");
            editButton = new JButton("Edit Store");
            statisticsButton = new JButton("View Statistics");
            editAccountButton = new JButton("Edit Account");
            deleteAccountButton = new JButton("Delete Account");
            logoutButton = new JButton("Logout");

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(addButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(editButton);
            buttonPanel.add(statisticsButton);
            buttonPanel.add(editAccountButton);
            buttonPanel.add(deleteAccountButton);
            buttonPanel.add(logoutButton);
            add(buttonPanel);

            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {


                    // freezes when making a new account but not when logging
                    ArrayList<Seller> database = requestSellerDatabase();
                    Seller seller = createSeller(user);


                    // generate new frame for adding a store:
                    addStore addStore = new addStore(seller);
                    addStore.setVisible(true);

                }

                private Seller coreProcess(ArrayList<Seller> database, User user) {
                    ArrayList<Store> sellerStores = null;
                    for (Seller seller : database) {
                        if (seller.getUniqueIdentifier() == user.getUniqueIdentifier()) {
                            sellerStores = seller.getStores();
                            break;
                        }
                    }
                    // constructing seller object from info from server:
                    return new Seller(user.getUniqueIdentifier(), user.getEmail(), user.getPassword(), user.getName(),
                            user.getAge(), sellerStores);
                }

                private ArrayList<Seller> requestSellerDatabase() {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
                    BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
                    try {
                        bw.write("sendSeller\n");
                        bw.flush();
                        return sellerServerRead(parseServer(bfr));

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    // here for framework, but should read seller data from server here:

                    ArrayList<Seller> database = requestSellerDatabase();

                    Seller seller = createSeller(user);


                    if (seller.getStores().size() == 0 || seller.getStores() == null) {
                        JOptionPane.showMessageDialog(null, null,
                                "You have no stores, please add one.", JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        // new JFrame for deleting stores here:
                        deleteStore deleteStore = new deleteStore(seller);
                        deleteStore.setVisible(true);

                    }

                }

                private Seller coreProcess(ArrayList<Seller> database, User user) {
                    ArrayList<Store> sellerStores = null;
                    for (Seller seller : database) {
                        if (seller.getUniqueIdentifier() == user.getUniqueIdentifier()) {
                            sellerStores = seller.getStores();
                            break;
                        }
                    }
                    // constructing seller object from info from server:
                    return new Seller(user.getUniqueIdentifier(), user.getEmail(), user.getPassword(), user.getName(),
                            user.getAge(), sellerStores);
                }
                private ArrayList<Seller> requestSellerDatabase() {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
                    BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
                    try {
                        bw.write("sendSeller\n");
                        bw.flush();
                        return sellerServerRead(parseServer(bfr));

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {


                    ArrayList<Seller> database = requestSellerDatabase();


                    Seller seller = createSeller(user);

                    editStore editStore = new editStore(seller);
                    editStore.setVisible(true);

                }

                private Seller coreProcess(ArrayList<Seller> database, User user) {
                    ArrayList<Store> sellerStores = null;
                    for (Seller seller : database) {
                        if (seller.getUniqueIdentifier() == user.getUniqueIdentifier()) {
                            sellerStores = seller.getStores();
                            break;
                        }
                    }
                    // constructing seller object from info from server:
                    return new Seller(user.getUniqueIdentifier(), user.getEmail(), user.getPassword(), user.getName(),
                            user.getAge(), sellerStores);
                }

                private ArrayList<Seller> requestSellerDatabase() {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
                    BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
                    try {
                        bw.write("sendSeller\n");
                        bw.flush();
                        return sellerServerRead(parseServer(bfr));

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            });

            statisticsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ArrayList<Buyer> buyers = new ArrayList<Buyer>();
                    ViewStatisticsPanel viewStatisticsPanel = new ViewStatisticsPanel();
                    viewStatisticsPanel.setVisible(true);
                    dispose();
                }

            });
            editAccountButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {


                }
            });
            deleteAccountButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // stores can be empty since the account is going to either be deleted or seller will be created in another method
                    ArrayList <Store> stores = new ArrayList<>();

                    Seller seller = new Seller(user.getUniqueIdentifier(), user.getEmail(), user.getPassword(), user.getName(),
                            user.getAge(), stores);

                    int reply = JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to delete your account?", "Delete account",
                            JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        Window window = SwingUtilities.windowForComponent(deleteAccountButton);
                        window.dispose();
                        // TODO server for seller delete account

                    }

                }
            });
            logoutButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Handle the logout action
                    // ...
                }
            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);

        }
    }
    public static class ViewStatisticsPanel extends JFrame {
        private JButton byCustomer;
        private JButton byStore;
        private JButton allProducts;
        private JButton goBack;


        private ArrayList<Buyer> buyerDatabase;

        private ArrayList<Buyer> requestBuyerDatabase() {

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
            BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
            try {
                bw.write("sendBuyer\n");
                bw.flush();
                return buyerServerRead(parseServer(bfr));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        public ViewStatisticsPanel(Seller seller) {

            byCustomer = new JButton("View by Customer");
            byStore = new JButton("View by Store");
            allProducts = new JButton("View by All Products");
            goBack = new JButton("Return to Seller Menu");


            JPanel panel = new JPanel();
            panel.add(byCustomer);
            panel.add(byStore);
            panel.add(allProducts);
            panel.add(goBack);
            add(panel);



            byCustomer.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                }
            });
            byStore.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    StringBuilder messageBuilder = new StringBuilder();
                    for (Buyer buyer : buyerDatabase) {
                        messageBuilder.append("Buyer ID: ").append(buyer.getUniqueIdentifier()).append("\n");
                        messageBuilder.append("Shopping Cart:\n");
                        for (ProductPurchase productPurchase : buyer.getShoppingCart()) {
                            messageBuilder.append(productPurchase.getName())
                                    .append(" (Quantity: ").append(productPurchase.getQuantitySold()).append(")\n");
                        }
                        messageBuilder.append("Purchases:\n");
                        for (ProductPurchase productPurchase : buyer.getPurchases()) {
                            messageBuilder.append(productPurchase.getName())
                                    .append(" (Quantity: ").append(productPurchase.getQuantitySold()).append(")\n");
                        }
                        messageBuilder.append("\n");
                    }
                    JOptionPane.showMessageDialog(ViewStatisticsPanel.this, messageBuilder.toString());
                }
            });
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);

        }

    }

    private static class addStore extends JFrame {
        private JTextField storeName;
        private JTextField storeProducts;
        private JButton enterButton;
        private JButton backToMenuButton;
        private Seller seller;

        public addStore(Seller seller) {

            super("Add store");
            storeName = new JTextField(20);
            storeProducts = new JTextField(3);
            enterButton = new JButton("Next step");
            backToMenuButton = new JButton("Back to Seller Menu");
            this.seller = seller;


            JPanel panel = new JPanel();
            panel.add(enterButton);
            panel.add(new JLabel("What is the name of the store you want to add?"));
            panel.add(storeName);
            panel.add(new JLabel("How many products do you want to add?"));
            panel.add(storeProducts);
            add(panel);


            enterButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ArrayList<Product> products = null;

                    int numProducts = Integer.parseInt(storeProducts.getText());
                    String nameOfStore = storeName.getText();

                    ArrayList<Seller> database = requestSellerDatabase();

                    products = seller.addProducts(numProducts, database);


                    Store store = new Store(nameOfStore, products);
                    seller.addStore(-1, store);

                    JOptionPane.showMessageDialog(null, "Store successfully added!",
                            "Added New Store", JOptionPane.INFORMATION_MESSAGE);

                    //update database after this:
                    coreProcess(database, seller);

                    // close the frame (takes you back to seller menu):
                    dispose();
                }

                private ArrayList<Seller> requestSellerDatabase() {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
                    BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
                    try {
                        bw.write("sendSeller\n");
                        bw.flush();
                        return sellerServerRead(parseServer(bfr));

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                private void coreProcess(ArrayList<Seller> database, Seller seller) {
                    for (Seller x : database) {
                        if (x.getUniqueIdentifier() == seller.getUniqueIdentifier()) {
                            database.remove(x);
                            database.add(seller);
                        }
                    }

                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
                    BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
                    String toServer = "";

                    try {
                        bw.write("writeSeller\n");
                        for (Seller k : database) {
                            toServer.concat(k.serverString());
                        }
                        bw.write(toServer);
                        bw.write("\n");
                        bw.flush();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            backToMenuButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();


                }
            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);
        }
    }

    private static class deleteStore extends JFrame {
        private JComboBox<String> comboBox;
        private JButton deleteButton;
        private JButton backToMenuButton;
        private Seller seller;

        public deleteStore(Seller seller) {
            super("Delete A Store");
            comboBox = new JComboBox<>();
            this.seller = seller;

            for (Store store : seller.getStores()) {
                comboBox.addItem(store.getStoreName());
            }

            deleteButton = new JButton("Delete");
            backToMenuButton = new JButton("Back to Seller Menu");
            JPanel panel = new JPanel();
            panel.add(comboBox);
            panel.add(deleteButton);
            panel.add(backToMenuButton);
            add(panel);

            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // assigns selectedItem to the item currently selected in the drop-down box (comboBox)
                    String selectedItem = (String) comboBox.getSelectedItem();

                    // deletes the selected item from the seller's stores and the drop-down box
                    for (Store store : seller.getStores()) {
                        if (selectedItem.equals(store.getStoreName())) {
                            seller.getStores().remove(store);
                            comboBox.removeItem(selectedItem);
                        }
                    }

                    // update database after this:
                    ArrayList<Seller> database = requestSellerDatabase();
                    coreProcess(database, seller);

                    // close the frame (takes you back to seller menu):
                    // maybe not necessary if the user wants to delete multiple stores before closing the frame
                    dispose();


                }

                private ArrayList<Seller> requestSellerDatabase() {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
                    BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
                    try {
                        bw.write("sendSeller\n");
                        bw.flush();
                        return sellerServerRead(parseServer(bfr));

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                private void coreProcess(ArrayList<Seller> database, Seller seller) {
                    for (Seller x : database) {
                        if (x.getUniqueIdentifier() == seller.getUniqueIdentifier()) {
                            database.remove(x);
                            database.add(seller);
                        }
                    }

                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
                    BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
                    String toServer = "";

                    try {
                        bw.write("writeSeller\n");
                        for (Seller k : database) {
                            toServer.concat(k.serverString());
                        }
                        bw.write(toServer);
                        bw.write("\n");
                        bw.flush();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            });

            backToMenuButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();


                }
            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);

        }

    }


    private static class editStore extends JFrame {
        private JComboBox<String> comboBox;
        private JButton selectStoreButton;
        private JButton backToMenuButton;
        private Seller seller;

        public editStore(Seller seller) {
            super("Edit A Store");
            comboBox = new JComboBox<>();
            this.seller = seller;

            for (Store store : seller.getStores()) {
                comboBox.addItem(store.getStoreName());
            }
            selectStoreButton = new JButton("Select store");
            backToMenuButton = new JButton("Back to menu");

            JPanel panel = new JPanel();
            panel.add(comboBox);
            panel.add(selectStoreButton);
            panel.add(backToMenuButton);
            add(panel);

            selectStoreButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // assigns selectedItem to the item currently selected in the drop-down box (comboBox)
                    String selectedItem = (String) comboBox.getSelectedItem();

                    // deletes the selected item from the seller's stores and the drop-down box
                    Store storeForEdit = null;
                    for (Store store : seller.getStores()) {
                        if (selectedItem.equals(store.getStoreName())) {
                            storeForEdit = store;
                        }
                    }

                    // new JFrame to edit the products in the selected store (pass the store and the seller):
                    editStoreProducts editStoreProducts = new editStoreProducts(storeForEdit, seller);
                    editStoreProducts.setVisible(true);

                    // close the frame:
                    dispose();

                }

            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);
        }
    }

    private static class editStoreProducts extends JFrame {
        private JComboBox<String> comboBox;
        private JButton changeProductNameButton;
        private JTextField newProductName;
        private JButton changeProductDescription;
        private JTextField newProductDescription;
        private JButton changeProductQuantity;
        private JTextField newProductQuantity;
        private JButton deleteProductButton;
        private JButton createNewProductButton;

        private Store store;
        private Seller seller;

        public editStoreProducts(Store store, Seller seller) {
            super("Edit Store Contents");
            this.store = store;
            this.seller = seller;
            comboBox = new JComboBox<>();

            for (Product product : store.getProducts()) {
                comboBox.addItem(product.productPage());
            }

            changeProductNameButton = new JButton("Set new product name");
            newProductName = new JTextField(10);
            changeProductDescription = new JButton("Set new product description");
            newProductDescription = new JTextField(20);
            changeProductQuantity = new JButton("Set new product quantity");
            newProductQuantity = new JTextField(5);
            deleteProductButton = new JButton("Delete selected product");
            createNewProductButton = new JButton("Create new product");

            JPanel panel = new JPanel();
            panel.add(comboBox);
            panel.add(changeProductNameButton);
            panel.add(newProductName);
            panel.add(changeProductDescription);
            panel.add(newProductDescription);
            panel.add(changeProductQuantity);
            panel.add(newProductQuantity);
            panel.add(deleteProductButton);
            panel.add(createNewProductButton);

            changeProductNameButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedProduct = (String) comboBox.getSelectedItem();

                    Product editedProduct = null;
                    for (Product product : store.getProducts()) {
                        if (selectedProduct.equals(product.productPage())) {
                            editedProduct = product;

                            // replaces old product in combo box with new product
                            comboBox.removeItem(selectedProduct);
                            editedProduct.setName(newProductName.getText());
                            comboBox.addItem(editedProduct.productPage());

                            //replaces old product in the store with the new product
                            store.getProducts().set(store.getProducts().indexOf(product), editedProduct);

                        }
                    }
                }

                // probably need to write to seller database with updated store and seller object:

            });

            changeProductDescription.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedProduct = (String) comboBox.getSelectedItem();

                    Product editedProduct = null;
                    for (Product product : store.getProducts()) {
                        if (selectedProduct.equals(product.productPage())) {
                            editedProduct = product;

                            // replaces old product in combo box with new product
                            comboBox.removeItem(selectedProduct);
                            editedProduct.setDescription(newProductDescription.getText());
                            comboBox.addItem(editedProduct.productPage());

                            //replaces old product in the store with the new product
                            store.getProducts().set(store.getProducts().indexOf(product), editedProduct);

                        }
                    }

                }

                // probably need to write to seller database with updated store and seller object:

            });

            changeProductQuantity.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedProduct = (String) comboBox.getSelectedItem();

                    Product editedProduct = null;
                    for (Product product : store.getProducts()) {
                        if (selectedProduct.equals(product.productPage())) {
                            editedProduct = product;

                            // replaces old product in combo box with new product
                            comboBox.removeItem(selectedProduct);
                            editedProduct.setQuantityForPurchase(Integer.parseInt(newProductQuantity.getText()));
                            comboBox.addItem(editedProduct.productPage());

                            //replaces old product in the store with the new product
                            store.getProducts().set(store.getProducts().indexOf(product), editedProduct);

                        }
                    }

                }

                // probably need to write to seller database with updated store and seller object:

            });

            deleteProductButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedProduct = (String) comboBox.getSelectedItem();

                    for (Product product : store.getProducts()) {
                        if (selectedProduct.equals(product.productPage())) {
                            comboBox.removeItem(selectedProduct);
                            store.getProducts().remove(product);

                        }
                    }
                }

                // probably need to write to seller database with updated store and seller object:

            });

            createNewProductButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    // need seller database here:
                    ArrayList<Seller> database = new ArrayList<>();

                    String name = JOptionPane.showInputDialog(null, "What is the name?");

                    String description = JOptionPane.showInputDialog(null, "What is the description?");

                    String stockString = JOptionPane.showInputDialog(null, "How many items in stock?");
                    int stock = Integer.parseInt(stockString);

                    String priceString = JOptionPane.showInputDialog(null, "How much does this item cost?");
                    double price = Double.parseDouble(priceString);

//                    int uniqueID = getProductDatabase(database).size() + 1;

//                    Product product = new Product(name, description, stock, price, 0, uniqueID);
//                    store.getProducts().add(product);
//                    comboBox.addItem(product.productPage());

                }

                // then need to update database:

            });
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);

        }

    }
}

