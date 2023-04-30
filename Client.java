import javax.swing.*;
import java.io.IOException;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

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


                    // maybe send prompt to server for the database and then read the database in:
//                    dos.writeUTF();
//                    dis.readUTF();

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

                    // maybe send prompt to server for the database and then read the database in:
//                    dos.writeUTF();
//                    dis.readUTF();

                    // generate new shop by seller Jframe here to allow the buyer to search for a seller
                    shopBySeller bySeller = new shopBySeller(buyer);
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

    private static class shopBySeller extends JFrame {

        private JButton viewProducts;
        private JComboBox<String> sellerComboBox;
        private JComboBox<String> productComboBox;
        private ArrayList<Seller> shopSeller;
        private JButton selectSellerButton;
        private Seller selectedSeller;
        private Buyer buyer;


        public shopBySeller(ArrayList<Seller> sellers, Buyer buyer) {
            super("Marketplace");
            this.shopSeller = shopSeller;
            this.buyer = buyer;

            // Create GUI components
            JLabel sellerLabel = new JLabel("Select a seller:");
            sellerComboBox = new JComboBox<String>();
            for (Seller seller : sellers) {
                sellerComboBox.addItem(seller.getName());
            }

            JLabel productLabel = new JLabel("Select a product:");
            productComboBox = new JComboBox<String>();

            JButton viewButton = new JButton("View Product");
            viewButton.addActionListener((ActionListener) this);

            // Add components to JFrame
            setLayout(new GridLayout(4, 1));
            add(sellerLabel);
            add(sellerComboBox);
            add(productLabel);
            add(productComboBox);
            add(viewButton);

            selectSellerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedSeller = (String) sellerComboBox.getSelectedItem();
                    Seller seller = null;
                    for (Seller s : shopSeller) {
                        if (s.getName().equals(selectedSeller)) {
                            seller = s;
                            break;
                        }
                    }
                    if (seller != null) {
                        ArrayList<Product> products = seller.readSellerDatabase();
                        productComboBox.removeAllItems();
                        for (Product product : products) {
                            productComboBox.addItem(product.getName());
                        }
                    }
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
                    // JFrame for cart here:

                }
            });

            viewPurchasesButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // JFrame or JOptionPane for purchases here

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
                        JOptionPane.showMessageDialog(null, "No Sellers Exist Yet; You will be unable to shop!",
                                "No Sellers!", JOptionPane.INFORMATION_MESSAGE);
                    }

                }
            });

            previousPageButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    dispose();

                }
            });


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

