import javax.swing.*;
import java.io.IOException;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.DataFormatException;

public class Client extends JComponent implements Runnable {


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

    public void sendServer(String option) throws IOException {
        BufferedReader bfr = new BufferedReader(new InputStreamReader(dis));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
        String toServer = "";

        if (option.equals("requestSellerDatabase")) {
            bw.write("sendSeller\n");
        } else if (option.equals("requestBuyerDatabase")) {
            bw.write("sendBuyer\n");
        } else if (option.equals("login")) {
            bw.write("sendLogin\n");
        } else if (option.equals("seller")) {
            bw.write("true\n");
        } else if (option.equals("buyer")) {
            bw.write("false\n");
        } else if (option.equals("updateBuyer")) {
            bw.write("writeBuyer\n");
        } else if (option.equals("updateSeller")) {
            bw.write("writeSeller\n");
        }

        String action = bfr.readLine();

        if (action.equals("sellerDatabase")) {
            ArrayList<Seller> temp = this.sellerServerRead(parseServer(bfr));
            this.setSellerDatabase(temp);
        } else if (action.equals("buyerDatabase")) {
            this.setBuyerDatabase(this.buyerServerRead(parseServer(bfr)));
        } else if (action.equals("loginDatabase")) {
            this.setLoginDetails(this.getUserInfo(bfr.readLine()));
        } else if (action.equals ("writeSeller")) {
            bw.write("sellerDatabase\n");
            bw.flush();
            for (Seller seller : this.getSellerDatabase()) {
                toServer = toServer.concat(seller.serverString());
            }
            bw.write(toServer);
            bw.write("end\n");
            bw.flush();
        } else if (action.equals("sendLogin")) {
            //send login information
            bw.write(this.getLoginDetails().constructorString());

        } else if (action.equals("sendBuyer")) {
            //send buyer information
            bw.write("buyerDatabase\n");
            bw.flush();
            for (Buyer buyer : this.getBuyerDatabase()) {
                toServer = toServer.concat(buyer.serverString());
            }
            bw.write(toServer);
            bw.write("end\n");
            bw.flush();
        }
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



    public static void main(String[] args) throws IOException {
        SwingUtilities.invokeLater(new Client());
    }

    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == customer) {
                // creates new JFrame for a customer to log in
                try {
                    client.sendServer("buyer");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                LoginOptionCustomer loginOptionCustomer = new LoginOptionCustomer(client);
                loginOptionCustomer.pack();
                loginOptionCustomer.setVisible(true);
                frame.dispose();
            } else if (e.getSource() == exit) {
                frame.dispose();
            } else if (e.getSource() == seller) {
                // creates new JFrame for a seller to log in
                try {
                    client.sendServer("seller");
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
        private Client client;
        private JButton createAccountButton;
        private JButton loginButton;

        public LoginOptionCustomer(Client client) {
            super("Login or Create Account");
            this.client = client;
            createAccountButton = new JButton("Create Account");
            loginButton = new JButton("Login");

            JPanel panel = new JPanel();
            panel.add(createAccountButton);
            panel.add(loginButton);
            add(panel);

            createAccountButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    BuyerLoginCredentials loginCredentials = new BuyerLoginCredentials(client);
                    loginCredentials.setVisible(true);
                    dispose();

                }
            });
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    BuyerLoginCredentials loginCredentials = new BuyerLoginCredentials(client);
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

        public BuyerGUI(User user, Client client) {
            super("Would you like to view the whole marketplace or shop by seller?");
            marketPlaceButton = new JButton("View the whole marketplace");
            shopBySellerButton = new JButton("Shop by seller");
            this.user = user;
            this.client = client;
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
                    marketPlace marketPlace = new marketPlace(buyer, client);
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



                    // generate new shop by seller Jframe here to allow the buyer to search for a seller

                }
            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);

        }
    }

    private static class marketPlace extends JFrame {
        private Client client;
        private JButton viewAllProductsButton;
        private JButton searchForProductsButton;
        private Buyer buyer;

        public marketPlace(Buyer buyer, Client client) {
            super("View all products or search for a specific product?");
            this.client = client;
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
                    ArrayList<Seller> database = null;
                    try {
                        // this is here for now just to be able to construct the framework but replace with reading database from server later
                        client.sendServer("requestSellerDatabase");
                        database = client.getSellerDatabase();
                    //} //catch (NoSellers ex) {
                        //JOptionPane.showMessageDialog(null, "No Sellers Exist Yet; You will be unable to shop!",
                                //"No Sellers!", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    int choice = 1;

                    ArrayList<Product> productList = buyer.viewMarketPlace(choice, new Scanner(System.in), database);
                    if (productList == null) {
                        JOptionPane.showMessageDialog(null, "Sorry! Sellers have not yet posted anything to the marketplace.\n" +
                                        "Come back later when sellers have stocked their stores!\n" + "Logging you out...\n",
                                "Empty Marketplace!", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        addToCartOrPurchase addToCartOrPurchase = new addToCartOrPurchase(productList, buyer, client);
                        addToCartOrPurchase.setVisible(true);
                        dispose();
                    }

                }
            });

            searchForProductsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // read database in to here:

                    ArrayList<Seller> database;
                    try {
                        // this is here for now just to be able to construct the framework but replace with reading database from server later
                        client.sendServer("requestSellerDatabase");
                        database = client.getSellerDatabase();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    int choice = 2;

                    ArrayList<Product> productList = buyer.viewMarketPlace(choice,new Scanner(System.in), database);
                    if (productList == null) {
                        JOptionPane.showMessageDialog(null, "Sorry! Sellers have not yet posted anything to the marketplace.\n" +
                                        "Come back later when sellers have stocked their stores!\n" + "Logging you out...\n",
                                "Empty Marketplace!", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        addToCartOrPurchase addToCartOrPurchase = new addToCartOrPurchase(productList, buyer, client);
                        addToCartOrPurchase.setVisible(true);
                        dispose();
                    }

                }
            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);

        }
    }

    private static class addToCartOrPurchase extends JFrame {
        private Client client;
        private JButton addToCartButton;
        private JButton purchaseNowButton;
        private JButton previousPageButton;
        private JComboBox<String> comboBox;

        public addToCartOrPurchase(ArrayList<Product> productList, Buyer buyer, Client client) {
            super("Available Products");
            this.client = client;
            addToCartButton = new JButton("Add to cart");
            purchaseNowButton = new JButton("Purchase now");
            previousPageButton = new JButton("Previous page");
            comboBox = new JComboBox<>();

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

                    // read database from server here:
                    ArrayList<Seller> database;
                    try {
                        client.sendServer("requestSellerDatabase");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    database = client.getSellerDatabase();
                    Store store = buyer.viewStore(product1, database);

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

                    try {
                        client.sendServer("requestBuyerDatabase");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    ArrayList<Buyer> buyerDatabase = client.getBuyerDatabase();

                    buyer.addToShoppingCart(product1, store, quantity);

                    for (Buyer c: buyerDatabase) {
                        if (c.getUniqueIdentifier() == buyer.getUniqueIdentifier()) {
                            buyerDatabase.remove(c);
                            buyerDatabase.add(buyer);
                            break;
                        }
                    }
                    client.setBuyerDatabase(buyerDatabase);
                    try {
                        client.sendServer("updateBuyer");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    continueShoppingEtc continueShoppingEtc = new continueShoppingEtc(buyer, client);
                    continueShoppingEtc.setVisible(true);
                    dispose();

                }
            });

            purchaseNowButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedItem = (String) comboBox.getSelectedItem();

                    Product product1 = null;
                    for (Product product : productList) {
                        if (selectedItem.equals(product.marketplaceString())) {
                            product1 = product;
                        }
                    }

                    // read database from server here:
                    ArrayList<Seller> database;
                    try {
                        client.sendServer("requestSellerDatabase");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    database = client.getSellerDatabase();
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

                    buyer.buyProduct(product1, numProductsForPurchase, store, null, database);

                    try {
                        client.sendServer("requestBuyerDatabase");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    ArrayList<Buyer> buyerDatabase = client.getBuyerDatabase();

                    for (Buyer c: buyerDatabase) {
                        if (c.getUniqueIdentifier() == buyer.getUniqueIdentifier()) {
                            buyerDatabase.remove(c);
                            buyerDatabase.add(buyer);
                            break;
                        }
                    }
                    client.setBuyerDatabase(buyerDatabase);
                    try {
                        client.sendServer("updateBuyer");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    continueShoppingEtc continueShoppingEtc = new continueShoppingEtc(buyer,client);
                    continueShoppingEtc.setVisible(true);
                    dispose();

                }
            });

            previousPageButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    marketPlace marketPlace = new marketPlace(buyer, client);
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


        public continueShoppingEtc(Buyer buyer, Client client) {
            super("Continue Shopping?");
            this.client = client;
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
                    marketPlace marketPlace = new marketPlace(buyer, client);
                    marketPlace.setVisible(true);
                    dispose();
                }
            });

            viewCartButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        client.sendServer("requestBuyerDatabase");
                    } catch (IOException k) {
                        k.printStackTrace();
                    }

                    buyerCart buyerCart = new buyerCart(buyer, client, client.getBuyerDatabase());
                    buyerCart.setVisible(true);

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
                    try {
                        client.sendServer("updateBuyer");
                        client.sendServer("updateSeller");
                    } catch (IOException l) {
                        l.printStackTrace();
                    }
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
        private Client client;
        private JButton removeItemButton;
        private JButton purchaseCartButton;
        private JButton previousPageButton;
        private JComboBox<String> comboBox;

        private ArrayList<Buyer> buyerDatabase;

        public buyerCart(Buyer buyer, Client client, ArrayList<Buyer> buyerDatabase) {
            super("Your Shopping Cart");
            this.client = client;
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

                    for (Buyer c: buyerDatabase) {
                        if (c.getUniqueIdentifier() == buyer.getUniqueIdentifier()) {
                            buyerDatabase.remove(c);
                            buyerDatabase.add(buyer);
                            break;
                        }
                    }

                    try {
                        client.sendServer("updateBuyer");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }


                }
            });

            purchaseCartButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // read database in to here:
                    ArrayList<Seller> database = null;
                    try {
                        // this is here for now just to be able to construct the framework but replace with reading database from server later
                        database = buyer.readSellerDatabase();
                    } catch (NoSellers ex) {
                        JOptionPane.showMessageDialog(null, "There is nothing in your cart!",
                                "Nothing in cart!", JOptionPane.INFORMATION_MESSAGE);
                    }
                    int result = 0;
                    do {
                        result = buyer.purchaseCart(database);
                    } while (result == 1);

                    comboBox.removeAll();
                    dispose();

                    for (Buyer c: buyerDatabase) {
                        if (c.getUniqueIdentifier() == buyer.getUniqueIdentifier()) {
                            buyerDatabase.remove(c);
                            buyerDatabase.add(buyer);
                            break;
                        }
                    }

                    try {
                        client.sendServer("updateBuyer");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
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
                    SellerLoginCredentials loginCredentials = new SellerLoginCredentials();
                    loginCredentials.setVisible(true);
                    dispose();

                }
            });

            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SellerLoginCredentials loginCredentials = new SellerLoginCredentials();
                    loginCredentials.setVisible(true);
                    dispose();

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
        private JTextField nameField;
        private JTextField ageField;
        private JButton loginButton;

        public BuyerLoginCredentials(Client client) {
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
                    User user = null;
                    try {
                        dos.writeUTF("sendLogin\n");
                        // write email here
                        dos.writeUTF(emailField.getText());

                        // User current = dis.read(); //read user
                        user = new User();
                        // maybe pass the User as an argument to the BuyerGUI class, so we can use it in marketplace

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    BuyerGUI buyerGUI = new BuyerGUI(user, client);
                    buyerGUI.setVisible(true);
                    dispose();


                }
            });

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);
        }
    }

    private static class SellerLoginCredentials extends JFrame {
        private JTextField emailField;
        private JPasswordField passwordField;
        private JTextField nameField;
        private JTextField ageField;
        private JButton loginButton;

        public SellerLoginCredentials() {
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
                    try {
                        dos.writeUTF("sendLogin\n");
                        // write email here
                        dos.writeUTF(emailField.getText());

                        // User current = dis.read(); //read user

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    // make this for seller:

//                    BuyerGUI buyerGUI = new BuyerGUI();
//                    buyerGUI.setVisible(true);
//                    dispose();


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

}