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

    private static class BuyerGUI extends JFrame {
        private Client client;
        private JButton marketPlaceButton;
        private JButton shopBySellerButton;
        private User user;

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

                    double balance = Double.parseDouble(bal);

                    // creating buyer object from user data that was generated in the login frame
                    Buyer buyer = new Buyer(user.getUniqueIdentifier(), user.getEmail(), user.getPassword(), user.getName(),
                            user.getAge(), balance);


                    // generate new marketplace JFrame here to show what is in the marketplace
                    marketPlace marketPlace = new marketPlace(buyer);
                    marketPlace.setVisible(true);
                    dispose();


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
                    Buyer buyer = new Buyer(user.getUniqueIdentifier(), user.getEmail(), user.getPassword(), user.getName(),
                            user.getAge(), balance);

                    // generate list of sellers from database here:
                    ArrayList<Seller> sellers = new ArrayList<>();


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
        private Buyer buyer;

        public marketPlace(Buyer buyer) {
            super("View all products or search for a specific product?");
            viewAllProductsButton = new JButton("View all products");
            searchForProductsButton = new JButton("Search for a specific product");
            this.buyer = buyer;
            JPanel panel = new JPanel();
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
                    // read database in to here:

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
                    BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
                    try {
                        dos.writeUTF("sendSeller\n");
                        return sellerServerRead(parseServer(bfr));

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

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
        private Buyer buyer;
        private Client client;


        public shopBySeller(ArrayList<Seller> sellers, Buyer buyer) {
            super("Select a Seller");
            this.sellers = sellers;
            this.buyer = buyer;
            this.client = client;

            // Create GUI components
            sellerComboBox = new JComboBox<>();
            for (Seller seller : sellers) {
                sellerComboBox.addItem(seller.getName());
            }

            selectSellerButton = new JButton("Select");
            JPanel panel = new JPanel();
            panel.add(sellerComboBox);
            panel.add(selectSellerButton);
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

            // Set JFrame properties
            setSize(400, 200);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }

    private static class addToCartOrPurchase extends JFrame {
        private Client client;
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
                            toServer.concat(k.serverString());
                        }
                        bw.write(toServer);
                        bw.write("\n");
                        bw.flush();

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

                        coreProcess(buyer, buyerDatabase);

                        continueShoppingEtc continueShoppingEtc = new continueShoppingEtc(buyer);
                        continueShoppingEtc.setVisible(true);
                        dispose();

                    }

                private ArrayList<Seller> requestSellerDatabase() {
                    BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
                    try {
                        dos.writeUTF("sendSeller\n");
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
            private Client client;
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
                            purchasesString = purchasesString + productPurchase.getName() + " " + productPurchase.getPrice() + "\n";
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
                    comboBox.addItem(product.toString());
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

                        coreProcess(buyer, buyerDatabase);


                    }

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

                        coreProcess(buyer,buyerDatabase);


                    }

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
                                toServer.concat(k.serverString());
                            }
                            bw.write(toServer);
                            bw.write("\n");
                            bw.flush();

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }

                    private ArrayList<Seller> requestSellerDatabase() {
                        BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
                        try {
                            dos.writeUTF("sendSeller\n");
                            return sellerServerRead(parseServer(bfr));

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
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
            private Client client;
            private JTextField emailField;
            private JPasswordField passwordField;
            private JTextField nameField;
            private JTextField ageField;
            private JButton loginButton;
            private User user;

            public BuyerCreateAccountCredentials() {
                super("Enter New Login Credentials");
                this.client = client;
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
            private Client client;
            private JTextField emailField;
            private JPasswordField passwordField;
            private JButton loginButton;

            public BuyerLoginCredentials() {
                super("Enter New Login Credentials");
                this.client = client;
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
                        int uniqueID = Integer.parseInt(btemp.readLine());
                        User user = new User(uniqueID, password, email, name, age);
                        bw.write("confirmUser");
                        bw.write(user.constructorString());
                        bw.write("\n");
                        bw.flush();


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

        private static class sellerLoginCredentials extends JFrame {
            private Client client;
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
                                SellerGUI sellerGUI = new SellerGUI(user);
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
            private Seller sellerX;

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

                        // here for framework, but should read seller data from server here:

                        ArrayList<Seller> database = requestSellerDatabase();

                        sellerX = coreProcess(database, user);

                        // generate new frame for adding a store:
                        addStore addStore = new addStore(sellerX);
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
                        BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
                        try {
                            dos.writeUTF("sendSeller\n");
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

                        sellerX = coreProcess(database, user);



                        if (sellerX.getStores().size() == 0) {
                            JOptionPane.showMessageDialog(null, null,
                                    "You have no stores, please add one.", JOptionPane.INFORMATION_MESSAGE);

                        } else {
                            // new JFrame for deleting stores here:
                            deleteStore deleteStore = new deleteStore(sellerX);
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
                        BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
                        try {
                            dos.writeUTF("sendSeller\n");
                            return sellerServerRead(parseServer(bfr));

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                editButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        //editStore editStore = new editStore(sellerX);
                        //editStore.setVisible(true);

                    }
                });

                statisticsButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

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

        private static class addStore extends JFrame {
            private Client client;
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
                this.client = client;


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

                        // no scanner here because there will be JOptionPanes after Somansh replaces them
                        products = seller.addProducts(numProducts, client.getSellerDatabase());

                        Store store = new Store(nameOfStore, products);
                        seller.addStore(-1, store);

                        JOptionPane.showMessageDialog(null, "Store successfully added!",
                                "Added New Store", JOptionPane.INFORMATION_MESSAGE);

                        //update database after this:
                        try {
                            client.sendServer("requestSellerDatabase");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        ArrayList<Seller> database = client.getSellerDatabase();

                        for (Seller x : database) {
                            if (x.getUniqueIdentifier() == seller.getUniqueIdentifier()) {
                                database.remove(x);
                                database.add(seller);
                            }
                        }

                        client.setSellerDatabase(database);
                        try {
                            client.sendServer("updateSeller");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        // close the frame (takes you back to seller menu):
                        // maybe not necessary if the user wants to add multiple stores before closing the frame
                        dispose();


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
                        BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
                        try {
                            dos.writeUTF("sendSeller\n");
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
            }

        }

        private static class editStore extends JFrame {
            private JTextField storeIndex;
            private Client client;

            public editStore(Seller seller, Client client) {
                this.client = client;
                storeIndex = new JTextField(3);
                JPanel panel = new JPanel();
                panel.add(storeIndex);
                panel.add(new JLabel("Enter the store index you want to edit: "));
                add(panel);

                storeIndex.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        Store edit = null;
                        //int i = 1;

                        if (seller.getStores().size() == 0) {
                            JOptionPane.showMessageDialog(null, "You have no stores, please add one!");
                        } else {
                            int i = 1;
                            String message = "";
                            //int i = 1;
                            for (Store store : seller.getStores()) {
                                message += i + ": " + store.getStoreName() + "\n";
                                i++;
                            }
                            JOptionPane.showMessageDialog(null, message);
                        }
                        Store editStore = seller.getStores().get(Integer.parseInt(storeIndex.getText()));


                        try {
                            client.sendServer("requestSellerDatabase");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        ArrayList<Seller> database = client.getSellerDatabase();

                        for (Seller x : database) {
                            if (x.getUniqueIdentifier() == seller.getUniqueIdentifier()) {
                                database.remove(x);
                                database.add(seller);
                            }
                        }

                        client.setSellerDatabase(database);
                        try {
                            client.sendServer("updateSeller");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        editStore1 editStore1 = new editStore1(seller, client, editStore);
                        editStore1.setVisible(true);

                    }
                });
            }
        }

        private static class editStore1 extends JFrame {
            private Client client;
            private Store edit;

            public editStore1(Seller seller, Client client, Store edit) {
                super("What would you like to change about this store?");
                this.client = client;
                this.edit = edit;
                JButton storeButton = new JButton("1. Store Name");
                JButton addButton = new JButton("2. Add Products");
                JButton editButton = new JButton("3. Edit Products");
                JButton deleteButton = new JButton("4. Delete Products");

                JPanel buttonPanel = new JPanel();
                buttonPanel.add(storeButton);
                buttonPanel.add(addButton);
                buttonPanel.add(editButton);
                buttonPanel.add(deleteButton);

                storeButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        editStore1 store = new editStore1(seller, client, edit);

                    }
                });

                addButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        String input = JOptionPane.showInputDialog(null, "How many products do you want to add?");
                        int items = Integer.parseInt(input);
                        edit.setProducts(seller.addProducts(items, client.getSellerDatabase()));
                        seller.getStores().add(edit);

                        try {
                            client.sendServer("requestSellerDatabase");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        ArrayList<Seller> database = client.getSellerDatabase();

                        for (Seller x : database) {
                            if (x.getUniqueIdentifier() == seller.getUniqueIdentifier()) {
                                database.remove(x);
                                database.add(seller);
                            }
                        }

                        client.setSellerDatabase(database);
                        try {
                            client.sendServer("updateSeller");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }


                    }
                });

                editButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        Client.editStore1.editProduct1 editProduct1 = new Client.editStore1.editProduct1(seller, client);

                    }
                });

                deleteButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        Product productDelete = null;
                        int k = 1;
                        StringBuilder productList = new StringBuilder();
                        for (Product product : edit.getProducts()) {
                            productList.append(k).append(": ").append(product.getName()).append("\n");
                            k++;
                        }
                        JOptionPane.showMessageDialog(null, productList.toString(), "Products List", JOptionPane.INFORMATION_MESSAGE);

                        while (true) {
                            String productToDelete = JOptionPane.showInputDialog(null, "Enter the product index you want to delete:");
                            int x = readInt(productToDelete);
                            if (x != -1) {
                                edit.getProducts().remove(x - 1);
                                break;
                            }
                        }
                        seller.getStores().add(edit);

                        try {
                            client.sendServer("requestSellerDatabase");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        ArrayList<Seller> database = client.getSellerDatabase();

                        for (Seller x : database) {
                            if (x.getUniqueIdentifier() == seller.getUniqueIdentifier()) {
                                database.remove(x);
                                database.add(seller);
                            }
                        }

                        client.setSellerDatabase(database);
                        try {
                            client.sendServer("updateSeller");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                    }
                });
            }

            private static class storeName extends JFrame {
                private JTextField storeQuestion;
                private Client client;

                public storeName(Seller seller, Client client) {

                    storeQuestion = new JTextField(20);
                    JPanel panel = new JPanel();
                    panel.add(storeQuestion);
                    panel.add(new JLabel("Enter the  name of the Store: "));
                    add(panel);

                    storeQuestion.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Store edit = null;

                            while (true) {
                                String newName = JOptionPane.showInputDialog(null, "Enter the new name of the Store:");
                                if (newName == null) {
                                    JOptionPane.showMessageDialog(null, "Please enter a valid String (cannot be empty!)");
                                } else {
                                    JOptionPane.showMessageDialog(null, "New store name printed successfully.");
                                    edit.setStoreName(newName);
                                    break;
                                }
                            }

                            seller.getStores().add(edit);

                            try {
                                client.sendServer("requestSellerDatabase");
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            ArrayList<Seller> database = client.getSellerDatabase();

                            for (Seller x : database) {
                                if (x.getUniqueIdentifier() == seller.getUniqueIdentifier()) {
                                    database.remove(x);
                                    database.add(seller);
                                }
                            }

                            client.setSellerDatabase(database);
                            try {
                                client.sendServer("updateSeller");
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }

                        }
                    });
                }
            }

            private static class editProduct1 extends JFrame {
                private JTextField productIndex;

                private Seller seller;
                private Client client;

                public editProduct1(Seller seller, Client client) {
                    this.seller = seller;
                    this.client = client;

                    productIndex = new JTextField(3);
                    JPanel panel = new JPanel();
                    panel.add(productIndex);
                    panel.add(new JLabel("Enter the product index you want to edit: "));
                    add(panel);

                    productIndex.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {

                            Client.editStore1.editProduct1.editProduct2 editProduct2 = new Client.editStore1.editProduct1.editProduct2(seller, client);
//??
                        }
                    });
                }

                private static class editProduct2 extends JFrame {
                    private Client client;
                    private Seller seller;

                    public editProduct2(Seller seller, Client client) {

                        super("What would you like to edit about this product?");
                        Product productEdit = null;

                        this.seller = seller;
                        JButton nameButton = new JButton("1. Name");
                        JButton descriptionButton = new JButton("2. Description");
                        JButton priceButton = new JButton("3. Price");
                        JButton qtyButton = new JButton("4. Quantity For Purchase");

                        JPanel buttonPanel = new JPanel();
                        buttonPanel.add(nameButton);
                        buttonPanel.add(descriptionButton);
                        buttonPanel.add(priceButton);
                        buttonPanel.add(qtyButton);

                        nameButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {

                                while (true) {
                                    String newName = JOptionPane.showInputDialog(null, "Enter the new name of the Product:");
                                    if (newName == null) {
                                        JOptionPane.showMessageDialog(null, "Please enter a valid String (cannot be empty!)");
                                    } else {
                                        productEdit.setName(newName);
                                        JOptionPane.showMessageDialog(null, "Product name updated successfully!");
                                        break;
                                    }
                                }

                            }
                        });

                        descriptionButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {

                                while (true) {
                                    String newDescription = JOptionPane.showInputDialog(null, "Enter the new description of the product:");
                                    if (newDescription == null) {
                                        JOptionPane.showMessageDialog(null, "Please enter a valid String (cannot be empty!)");
                                    } else {
                                        productEdit.setDescription(newDescription);
                                        JOptionPane.showMessageDialog(null, "Product description updated successfully!");
                                        break;
                                    }
                                }

                            }
                        });

                        priceButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                while (true) {
                                    String input = JOptionPane.showInputDialog(null, "Enter the new price of the Product:");
                                    double newPrice = Double.parseDouble(input);
                                    if (newPrice != -1) {
                                        if (newPrice < 0) {
                                            JOptionPane.showMessageDialog(null, "Please enter a valid Price (cannot be less than 0!)");
                                        } else {
                                            productEdit.setPrice(newPrice);
                                            JOptionPane.showMessageDialog(null, "Price was updated successfully!");
                                            break;
                                        }
                                    }
                                }

                            }
                        });


                        qtyButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {

                                while (true) {
                                    String input = JOptionPane.showInputDialog(null, "How much stock is available?");
                                    try {
                                        int newStock = Integer.parseInt(input);
                                        if (newStock < 0) {
                                            JOptionPane.showMessageDialog(null, "Please enter a number greater than 0!");
                                        } else {
                                            productEdit.setQuantityForPurchase(newStock);
                                            break;
                                        }
                                    } catch (NumberFormatException f) {
                                        JOptionPane.showMessageDialog(null, "Please enter a valid integer!");
                                    }
                                }

                            }
                        });

                        try {
                            client.sendServer("requestSellerDatabase");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        ArrayList<Seller> database = client.getSellerDatabase();

                        for (Seller x : database) {
                            if (x.getUniqueIdentifier() == seller.getUniqueIdentifier()) {
                                database.remove(x);
                                database.add(seller);
                            }
                        }

                        client.setSellerDatabase(database);
                        try {
                            client.sendServer("updateSeller");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }

                private static class deleteStore extends JFrame {
                    private JTextField newQtyBox;

                    public deleteStore() {

                        newQtyBox = new JTextField(20);
                        JPanel panel = new JPanel();
                        panel.add(newQtyBox);
                        panel.add(new JLabel("Enter the index of the store you want to delete."));
                        add(panel);

                        newQtyBox.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                try {
                                    dos.writeUTF(newQtyBox.getText());
                                    JOptionPane.showMessageDialog(null, "Store deleted successfully.");

                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        });
                    }
                }
            }

        }
    }


