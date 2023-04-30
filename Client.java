import com.sun.tools.jconsole.JConsoleContext;

import javax.swing.*;
import java.io.IOException;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Client extends JComponent implements Runnable {

    private static DataInputStream dis;
    private static DataOutputStream dos;
    private static Socket socket;

    private JButton customer;
    private JButton seller;
    private JButton exit;
    private JFrame frame;



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Client());
    }

    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == customer) {
                // creates new JFrame for a customer to log in
                LoginOptionCustomer loginOptionCustomer = new LoginOptionCustomer();
                loginOptionCustomer.pack();
                loginOptionCustomer.setVisible(true);
                frame.dispose();
            } else if (e.getSource() == exit) {
                frame.dispose();
            } else if (e.getSource() == seller) {
                // creates new JFrame for a seller to log in
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
                    BuyerLoginCredentials loginCredentials = new BuyerLoginCredentials();
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



                    // generate new shop by seller Jframe here to allow the buyer to search for a seller

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
                    ArrayList<Seller> database = null;
                    try {
                        // this is here for now just to be able to construct the framework but replace with reading database from server later
                        database = buyer.readSellerDatabase();
                    } catch (NoSellers ex) {
                        JOptionPane.showMessageDialog(null, "No Sellers Exist Yet; You will be unable to shop!",
                                "No Sellers!", JOptionPane.INFORMATION_MESSAGE);
                    }

                    int choice = 1;

                    ArrayList<Product> productList = buyer.viewMarketPlace(choice, database);
                    if (productList == null) {
                        JOptionPane.showMessageDialog(null, "Sorry! Sellers have not yet posted anything to the marketplace.\n" +
                                        "Come back later when sellers have stocked their stores!\n" + "Logging you out...\n",
                                "Empty Marketplace!", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        addToCartOrPurchase addToCartOrPurchase = new addToCartOrPurchase(productList, buyer);
                        addToCartOrPurchase.setVisible(true);
                        dispose();
                    }

                }
            });

            searchForProductsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // read database in to here:
                    ArrayList<Seller> database = null;
                    try {
                        // this is here for now just to be able to construct the framework but replace with reading database from server later
                        database = buyer.readSellerDatabase();
                    } catch (NoSellers ex) {
                        JOptionPane.showMessageDialog(null, "No Sellers Exist Yet; You will be unable to shop!",
                                "No Sellers!", JOptionPane.INFORMATION_MESSAGE);
                    }

                    int choice = 2;

                    ArrayList<Product> productList = buyer.viewMarketPlace(choice, database);
                    if (productList == null) {
                        JOptionPane.showMessageDialog(null, "Sorry! Sellers have not yet posted anything to the marketplace.\n" +
                                        "Come back later when sellers have stocked their stores!\n" + "Logging you out...\n",
                                "Empty Marketplace!", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        addToCartOrPurchase addToCartOrPurchase = new addToCartOrPurchase(productList, buyer);
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
        private JButton addToCartButton;
        private JButton purchaseNowButton;
        private JButton previousPageButton;
        private JComboBox<String> comboBox;

        public addToCartOrPurchase(ArrayList<Product> productList, Buyer buyer) {
            super("Available Products");
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
                    // Store store = buyer.viewStore(product1, database);

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

                    //buyer.addToShoppingCart(product1, store, quantity);

                    continueShoppingEtc continueShoppingEtc = new continueShoppingEtc(buyer);
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
                    // Store store = buyer.viewStore(product1, database);

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

                    //buyer.buyProduct(product1, numProductsForPurchase, store, database);

                    continueShoppingEtc continueShoppingEtc = new continueShoppingEtc(buyer);
                    continueShoppingEtc.setVisible(true);
                    dispose();

                }
            });

            previousPageButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

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
                    buyerCart buyerCart = new buyerCart(buyer);
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

        public buyerCart(Buyer buyer) {
            super("Your Shopping Cart");
            removeItemButton = new JButton("Remove item");
            purchaseCartButton = new JButton("Purchase cart");
            previousPageButton = new JButton("Previous page");
            comboBox = new JComboBox<>();

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

                    // update database here:

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
        private JTextField emailField;
        private JPasswordField passwordField;
        private JTextField nameField;
        private JTextField ageField;
        private JButton loginButton;

        public BuyerLoginCredentials() {
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
                    User user = null;

                    //writer user info from server into user object here
                    try {
                        dos.writeUTF("sendLogin\n");
                        // write email here and other login info to server here
                        dos.writeUTF(emailField.getText());

                        user = new User();
                        // maybe pass the User as an argument to the BuyerGUI class, so we can use it in marketplace

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    BuyerGUI buyerGUI = new BuyerGUI(user);
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
                    User user = null;

                    //writer user info from server into user object here
                    try {
                        dos.writeUTF("sendLogin\n");
                        // write email here and other login info to server here
                        dos.writeUTF(emailField.getText());

                        user = new User();



                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    // same thing here as buyerGUI but for sellerGUI
                    SellerGUI sellerGUI = new SellerGUI(user);
                    sellerGUI.setVisible(true);
                    dispose();


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
                    ArrayList<Store> sellerStores = new ArrayList<>();

                    // constructing seller object from info from server:
                    Seller seller = new Seller(user.getUniqueIdentifier(), user.getEmail(), user.getPassword(), user.getName(),
                            user.getAge(), sellerStores);

                    // generate new frame for adding a store:
                    addStore addStore = new addStore(seller);
                    addStore.setVisible(true);

                }
            });

            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {


                    // here for framework, but should read seller data from server here:
                    ArrayList<Store> sellerStores = new ArrayList<>();

                    // constructing seller object from info from server:
                    Seller seller = new Seller(user.getUniqueIdentifier(), user.getEmail(), user.getPassword(), user.getName(),
                            user.getAge(), sellerStores);


                    if (seller.getStores().size() == 0) {
                        JOptionPane.showMessageDialog(null, null,
                                "You have no stores, please add one.", JOptionPane.INFORMATION_MESSAGE);
                        // then close the JFrame
                        dispose();
                    } else {
                        // new JFrame for deleting stores here:

                    }

                }
            });

            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                   editStore editStore = new editStore(seller);

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

                    // no scanner here because there will be JOptionPanes after Somansh replaces them
                    products = seller.addProducts(numProducts);

                    Store store = new Store(nameOfStore, products);
                    seller.addStore(-1, store);

                    JOptionPane.showMessageDialog(null, "Store successfully added!",
                            "Added New Store", JOptionPane.INFORMATION_MESSAGE);

                    //update database after this:

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

                    // close the frame (takes you back to seller menu):
                    // maybe not necessary if the user wants to delete multiple stores before closing the frame
                    dispose();


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

        public editStore(Seller seller) {

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

                    try {
                        dos.writeUTF(storeIndex.getText());
                        // write it to the server

                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    editStore1 editStore1 = new editStore1();

                }
            });
        }
    }

    private static class editStore1 extends JFrame {

        public editStore1(Seller seller) {
            super("What would you like to change about this store?");

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

                    editStore1 store = new editStore1();

                }
            });

            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    String input = JOptionPane.showInputDialog(null, "How many products do you want to add?");
                    int items = Integer.parseInt(input);
                    edit.setProducts(addProducts(items, scanner));
                    this.getStores().add(edit);

                }
            });

            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    Client.editStore1.editProduct1 editProduct1 = new Client.editStore1.editProduct1();

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

                }
            });
        }

        private static class storeName extends JFrame {
            private JTextField storeQuestion;

            public storeName(Seller seller) {

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

                    }
                });
            }
        }

        private static class editProduct1 extends JFrame {
            private JTextField productIndex;

            public editProduct1(Seller seller) {

                productIndex = new JTextField(3);
                JPanel panel = new JPanel();
                panel.add(productIndex);
                panel.add(new JLabel("Enter the product index you want to edit: "));
                add(panel);

                productIndex.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            dos.writeUTF(productIndex.getText());
                            Client.editStore1.editProduct1.editProduct2 editProduct2 = new Client.editStore1.editProduct1.editProduct2();

                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });
            }

            private static class editProduct2 extends JFrame {
                public editProduct2(Seller seller) {

                    super("What would you like to edit about this product?");
                    Product productEdit = null;

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

    public viewStatistics(Seller seller, User user) {

        super("How would you like to view the statistics?");

        JButton customerStat = new JButton("1. By Customer");
        JButton storeStat = new JButton("2. By Store");
        JButton productStat = new JButton("3. All Products");
        JButton mainMenu = new JButton("4. Return to main menu");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(customerStat);
        buttonPanel.add(storeStat);
        buttonPanel.add(productStat);
        buttonPanel.add(mainMenu);

        customerStat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                ArrayList<Buyer> customers = new ArrayList<Buyer>();
                Buyer buyer = new Buyer(user.getUniqueIdentifier(), user.getEmail(), user.getPassword(), user.getName(),
                        user.getAge(), balance);

                if (customers.contains(buyer)) {
                    String message = String.format("Customer Name: %s\nItems Purchased: %d\nTotal Value (with current prices): %.2f\n\nItems In Cart: %d\nPotential Revenue: %.2f",
                            buyer.getName(), itemsPurchased, totalSpent, itemsInCart, potentialSpending);
                    JOptionPane.showMessageDialog(null, message);
                } else if (customers.size() == 0) {
                    JOptionPane.showMessageDialog(null, "No one has purchased your products yet!");
                }
            }
        });


        storeStat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                Client.viewStatistics.storeStat storeStat = new Client.viewStatistics.storeStat();
            }
        });


        productStat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                Client.viewStatistics.productStat productStat = new Client.viewStatistics.productStat();

            }
        });


        mainMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JOptionPane.showMessageDialog("Goodbye");

            }
        });

    }

    private static class storeStat extends JFrame {

        public storeStat(Seller seller) {

            super("How would you like to sort??");
            Product productEdit = null;

            JButton qtyButton = new JButton("1. Qauntity Sold");
            JButton revButton = new JButton("2. Total Revenue");
            JButton stockButton = new JButton("3. Stock remaining");

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(qtyButton);
            buttonPanel.add(revButton);
            buttonPanel.add(stockButton);

            qtyButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    //collections part needs to be fixed

                    Collections.sort(storeStat, Comparator.comparingInt(Store::getTotalQuantitySold).reversed());
                    int i = 1;
                    for (Store store : storeStat) {
                        System.out.printf("%d. %s\n", i, store.getStoreName());
                        System.out.printf("\tQuantity Sold: %d\n", store.getTotalQuantitySold());
                        System.out.printf("\tTotal Revenue: %.2f\n", store.getTotalValueSold());
                        System.out.printf("\tStock Remaining: %d\n", store.getStockRemaining());
                        i++;
                    }

                }
            });

            revButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    //collections part needs to be fixed

                    Collections.sort(storeStat, Comparator.comparingDouble(Store::getTotalValueSold).reversed());
                    int i = 1;
                    for (Store store : storeStat) {
                        System.out.printf("%d. %s\n", i, store.getStoreName());
                        System.out.printf("\tQuantity Sold: %d\n", store.getTotalQuantitySold());
                        System.out.printf("\tTotal Revenue: %.2f\n", store.getTotalValueSold());
                        System.out.printf("\tStock Remaining: %d\n", store.getStockRemaining());
                        i++;
                    }

                }
            });


            stockButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    //collections part needs to be fixed

                    Collections.sort(storeStat, Comparator.comparingInt(Store::getStockRemaining).reversed());
                    int i = 1;
                    for (Store store : storeStat) {
                        System.out.printf("%d. %s\n", i, store.getStoreName());
                        System.out.printf("\tQuantity Sold: %d\n", store.getTotalQuantitySold());
                        System.out.printf("\tTotal Revenue: %.2f\n", store.getTotalValueSold());
                        System.out.printf("\tStock Remaining: %d\n", store.getStockRemaining());
                        i++;
                    }

                }
            });
        }
    }


    private static class productStat extends JFrame {

        public productStat(Seller seller) {

            super("How would you like to sort??");
            Product productEdit = null;

            JButton qtyButton = new JButton("1. Qauntity Sold");
            JButton revButton = new JButton("2. Total Revenue");
            JButton stockButton = new JButton("3. Stock remaining");

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(qtyButton);
            buttonPanel.add(revButton);
            buttonPanel.add(stockButton);

            qtyButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    //collections part needs to be fixed

                    Collections.sort(productStat, Comparator.comparingInt(Product::getQuantitySold).reversed());
                    int i = 1;
                    for (Store store : storeStat) {
                        System.out.printf("%d. %s\n", i, store.getStoreName());
                        System.out.printf("\tQuantity Sold: %d\n", store.getTotalQuantitySold());
                        System.out.printf("\tTotal Revenue: %.2f\n", store.getTotalValueSold());
                        System.out.printf("\tStock Remaining: %d\n", store.getStockRemaining());
                        i++;
                    }

                }
            });

            revButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    //collections part needs to be fixed

                    Collections.sort(productStat, Comparator.comparingDouble(Product::getValueSold).reversed());
                    int i = 1;
                    for (Store store : storeStat) {
                        System.out.printf("%d. %s\n", i, store.getStoreName());
                        System.out.printf("\tQuantity Sold: %d\n", store.getTotalQuantitySold());
                        System.out.printf("\tTotal Revenue: %.2f\n", store.getTotalValueSold());
                        System.out.printf("\tStock Remaining: %d\n", store.getStockRemaining());
                        i++;
                    }

                }
            });


            stockButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    //collections part needs to be fixed

                    Collections.sort(productStat, Comparator.comparingInt(Product::getQuantityForPurchase).reversed());
                    int i = 1;
                    for (Store store : storeStat) {
                        System.out.printf("%d. %s\n", i, store.getStoreName());
                        System.out.printf("\tQuantity Sold: %d\n", store.getTotalQuantitySold());
                        System.out.printf("\tTotal Revenue: %.2f\n", store.getTotalValueSold());
                        System.out.printf("\tStock Remaining: %d\n", store.getStockRemaining());
                        i++;
                    }

                }
            });
        }


    }
}







