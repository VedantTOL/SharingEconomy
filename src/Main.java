import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.zip.DataFormatException;
import java.util.List;
import java.util.Arrays;
/**
 * The main method that which is the menu for the buyer to interact with the marketplace. The user first logs in,
 * and then as a buyer can navigate the marketplace, purchase products, add products to their cart, view their cart
 * and past purchases, and purchase their whole cart. After performing these actions, the user can log out if they c
 * choose, or they can continue shopping.
 *
 *
 * @author Roger, Somansh, Ethan, Vedant
 * @version June 13, 2022
 */


public class Main {
    public static final String EXIT = "Thank you! Please come again!"; //Displayed when user exits the program.
    public static final String INVALID_OPTION = "Please select a valid menu option!";
    public static void main(String[] args) throws DataFormatException, IOException, UserDatabaseFormatError, NoSellers {
        User loginAccess = new User();
        User user = null;
        boolean isSeller = false;
        boolean newSeller = false;

        Scanner scanner = new Scanner(System.in);

        int decisionA = 0;
        List<Integer> optionsA = Arrays.asList(1, 2, 3);
        while (true) {
            System.out.println("Are you a Customer or Seller?");
            System.out.print("1. Customer\n2. Seller\n3. Exit\n");
            decisionA = readInt(scanner.nextLine());
            boolean x = optionsA.contains(decisionA);
            if (x) {
                break;
            }
            else {
                System.out.println(INVALID_OPTION);
            }
        }

        if(decisionA == 1) isSeller = false;
        else if(decisionA == 2) isSeller = true;
        else if(decisionA == 3) {
            System.out.println(EXIT);
            return;
        }

        while (true) {
            System.out.print("Create new account or login?\n1. Create an account\n2. Login\n3. Exit\n");
            decisionA = readInt(scanner.nextLine());
            if (optionsA.contains(decisionA)) break;
            else System.out.println(INVALID_OPTION);
        }
        if (decisionA == 1) {

            user = loginAccess.addUser(scanner, isSeller);
            newSeller = true;

        } else if (decisionA == 2) {
            do {
                try {
                    user = loginAccess.login(scanner, isSeller);
                } catch (NoAccountError e) {
                    e.printStackTrace();
                    int decisionB = 0;

                    List<Integer> optionsB = Arrays.asList(1, 2, 3);
                    while (true) {
                        System.out.println("Would you like to create an account?");
                        System.out.println("1. Yes");
                        System.out.println("2. Exit");

                        decisionB = readInt(scanner.nextLine());
                        if (optionsB.contains(decisionB)) break;
                        else System.out.println(INVALID_OPTION);
                    }
                    if (decisionB == 1) {
                        user = loginAccess.addUser(scanner, isSeller);
                        newSeller = true;
                    } else if (decisionB == 2) {
                        System.out.println(EXIT);
                        return;
                    }

                } catch (AccountTypeError e) {
                    e.printStackTrace();
                    String alternate;

                    if (isSeller) {
                        alternate = "Customer";

                    } else {
                        alternate = "Seller";

                    }
                    System.out.printf("Attempting to log you in as %s. Please re-enter email and password...\n", alternate);

                    try {
                        user = loginAccess.login(scanner, !isSeller);
                        isSeller = !isSeller;
                    } catch (Exception k) {
                        System.out.println("Account does not exist!");
                        int decisionB = 0;
                        List<Integer> optionsB = Arrays.asList(1, 2, 3);
                        while (true) {
                            System.out.println("Would you like to create an account?");
                            System.out.println("1. Yes");
                            System.out.println("2. Exit");

                            decisionB = readInt(scanner.nextLine());
                            if (optionsB.contains(decisionB)) break;
                            else System.out.println(INVALID_OPTION);
                        }
                        if (decisionB == 1) {
                            user = loginAccess.addUser(scanner, isSeller);
                            newSeller = true;
                        } else if (decisionB == 2) {
                            System.out.println(EXIT);
                            return;
                        }
                    }

                } catch (IllegalAccessError e) {
                    e.printStackTrace();
                    System.out.println("Please re-enter email and password");
                    user = null;
                }
            } while (user == null);
        } else if (decisionA == 3) {
            System.out.println(EXIT);
            return;
        }


        if (!isSeller) {
            double balance = 0;
            do {
                System.out.println("What is your balance?");
                String bal = scanner.nextLine();
                balance = readDouble(bal);
            } while (balance == -1);

            Buyer buyer = new Buyer(user.getUniqueIdentifier(), user.getEmail(), user.getPassword(), user.getName(),
                    user.getAge(), balance);

            // if the buyer exists already do this:
            ArrayList<ProductPurchase> shoppingCart = buyer.viewCart();
            buyer.setShoppingCart(shoppingCart);

            if (buyer.getShoppingCart() == null) {
                buyer.setShoppingCart(new ArrayList<ProductPurchase>());
            }

            ArrayList<ProductPurchase> purchases = buyer.viewPurchases();
            buyer.setPurchases(purchases);

            if (buyer.getPurchases() == null) {
                buyer.setPurchases(new ArrayList<ProductPurchase>());
            }
            ArrayList<Seller> database = null;

            int continueShopping = 0;
            boolean leave = false;
            while (!leave) {
                do {
                    try {
                        database = buyer.readSellerDatabase();
                    } catch (NoSellers e) {
                        System.out.println("No Sellers Exist Yet; You will be unable to shop!");
                    }
                    int shopBy = 0;
                    do {
                        System.out.println("Do you want to view the whole marketplace or shop by Seller?\n1. Marketplace\n2. Seller\n3. Change Account Details\n4. Delete Account");
                        String shop = scanner.nextLine();
                        shopBy = readInt(shop);
                    } while (shopBy == -1);

                    int choice;
                    if (shopBy == 1) {

                        do {
                            System.out.println("Do you want to view all products or do you want to search for a specific product?\n" +
                                    "1. View all products\n2. Search");
                            String choices = scanner.nextLine();
                            choice = readInt(choices);
                        } while (choice == -1);

                        ArrayList<Product> productList = buyer.viewMarketPlace(choice, scanner, database);

                        if (productList == null) {
                            System.out.println("Sorry! Sellers have not yet posted anything to the marketplace.");
                            System.out.println("Come back later when sellers have stocked their stores!");
                            System.out.println("Logging you out...");
                            leave = true;
                        } else {
                            do {
                                int i = 1;
                                for (Product product: productList) {
                                    System.out.printf("%d. %s\n", i, product.marketplaceString());
                                    i++;
                                }

                                int productNum;
                                do {
                                    System.out.println("Enter the number that corresponds to the product you would like to view.");
                                    String productNums = scanner.nextLine();
                                    productNum = readInt(productNums);
                                } while (productNum == -1);

                                Product product = buyer.viewProduct(productList, productNum);

                                do {
                                    System.out.println("Would you like to buy this product now, add it to your cart, or go back to the " +
                                            "previous page?\n1. Buy now\n" + "2. Add to cart\n" + "3. Previous page");
                                    String choices = scanner.nextLine();
                                    choice = readInt(choices);
                                } while (!optionsA.contains(choice));

                                if (choice == 1) {

                                    Store store = buyer.viewStore(product, database);

                                    int numProductsForPurchase;
                                    do {
                                        System.out.printf("How many of %s would you like to purchase?\n", product.getName());
                                        String numProductsForPurchases = scanner.nextLine();
                                        numProductsForPurchase = readInt(numProductsForPurchases);
                                    } while (numProductsForPurchase == -1);

                                    buyer.buyProduct(product, numProductsForPurchase, store, scanner, database);

                                } else if (choice == 2) {
                                    Store store = buyer.viewStore(product, database);

                                    int quantity;
                                    do {
                                        System.out.printf("How many of %s would you like to add?\n", product.getName());
                                        String quantityForCart = scanner.nextLine();
                                        quantity = readInt(quantityForCart);
                                    } while (quantity == -1);

                                    buyer.addToShoppingCart(product, store, quantity);

                                } else if (choice == 3) {
                                    System.out.println("Taking you back to the product list...");
                                }

                            } while (choice == 3);

                            do {
                                System.out.println("Would you like to continue shopping, view your cart, view your " +
                                        "purchases, or log out?\n1. Continue shopping\n2. View cart\n3. View purchases" +
                                        "\n4. Log out");
                                String continueShoppings = scanner.nextLine();
                                continueShopping = readInt(continueShoppings);
                            } while (continueShopping == -1);

                            if (continueShopping == 1) {
                                System.out.println("Taking you back to the marketplace menu...");

                            } else if (continueShopping == 2) {
                                int removeFromCart = 0;
                                do {
                                    for (ProductPurchase productPurchase : buyer.getShoppingCart()) {
                                        System.out.printf("Name: %s, Quantity ordered: %d, Price: %.2f\n", productPurchase.getName(),
                                                productPurchase.getOrderQuantity(), productPurchase.getPrice());
                                    }

                                    int purchaseCart;
                                    do {
                                        System.out.println("Would you like to remove a product from your cart, purchase your cart," +
                                                " or continue shopping?\n1. Remove item from cart.\n2. Purchase cart.\n3. Continue.");
                                        String cartPurchase = scanner.nextLine();
                                        purchaseCart = readInt(cartPurchase);
                                    } while (purchaseCart == -1);

                                    if (purchaseCart == 1) {
                                        System.out.println("Which product would you like to remove from your cart? Please" +
                                                "type the name of the product.");
                                        String productName = scanner.nextLine();

                                        for (ProductPurchase productPurchase : buyer.getShoppingCart()) {
                                            if (productName.equalsIgnoreCase(productPurchase.getName())) {
                                                buyer.removeFromShoppingCart(productPurchase);
                                            }
                                        }
                                        removeFromCart = 1;

                                    } else if (purchaseCart == 2) {
                                        removeFromCart = buyer.purchaseCart(database);
                                        //buyer.writeToDatabase(false);

                                    }
                                } while (removeFromCart == 1);

                            } else if (continueShopping == 3) {
                                for (ProductPurchase productPurchase : buyer.getPurchases()) {
                                    System.out.printf("Name: %s, Quantity ordered: %d, Price: %.2f\n", productPurchase.getName(),
                                            productPurchase.getOrderQuantity(), productPurchase.getPrice());
                                }

                            } else if (continueShopping == 4) {
                                System.out.println("Thank you for shopping with us!");
                                try {
                                    buyer.writeToBuyer();
                                } catch (IOException | DataFormatException e) {
                                    System.out.println("Something");
                                }
                                leave = true;

                            }

                            if (continueShopping != 1 && continueShopping != 4) {
                                int logOut;
                                do {
                                    System.out.println("Would you like to continue shopping or log out?\n 1. Continue shopping\n" +
                                            "2. Log Out\n");

                                    String outLog = scanner.nextLine();
                                    logOut = readInt(outLog);
                                } while (logOut == -1);

                                if (logOut == 1) {
                                    continueShopping = 1;

                                } else if (logOut == 2) {
                                    try {
                                        buyer.writeToBuyer();
                                    } catch (IOException | DataFormatException e) {
                                        System.out.println("Something");
                                    }
                                    leave = true;
                                }
                            }
                        }

                    } else if (shopBy == 2) {
                        // methods related to viewing by isSeller
                        ArrayList<Product> productList = null;
                        Seller seller = null;
                        do {
                            seller = buyer.shopBySeller(scanner, database);
                            if (seller == null) {
                                System.out.println("There is no seller name that matches this name");
                            } else {
                                productList = buyer.shopByStore(seller, scanner);
                            }

                            if (productList == null) {
                                System.out.println("There is no store that matches this name.");
                            }
                        } while (seller == null);

                        if (productList == null) {
                            System.out.println("Sorry! Sellers have not yet posted anything to the marketplace.");
                            System.out.println("Come back later when sellers have stocked their stores!");
                            System.out.println("Logging you out...");
                            leave = true;
                        } else {
                            do {
                                int i = 1;
                                for (Product product: productList) {
                                    System.out.printf("%d. %s\n", i, product.marketplaceString());
                                    i++;
                                }

                                int productNum;
                                do {
                                    System.out.println("Enter the number that corresponds to the product you would like to view.");
                                    String productNums = scanner.nextLine();
                                    productNum = readInt(productNums);
                                } while (productNum == -1);

                                Product product = buyer.viewProduct(productList, productNum);

                                do {
                                    System.out.println("Would you like to buy this product now, add it to your cart, or go back to the " +
                                            "previous page?\n1. Buy now\n" + "2. Add to cart\n" + "3. Previous page\n");
                                    String choices = scanner.nextLine();
                                    choice = readInt(choices);
                                } while (!optionsA.contains(choice));

                                if (choice == 1) {

                                    Store store = buyer.viewStore(product, database);

                                    int numProductsForPurchase;
                                    do {
                                        System.out.printf("How many of %s would you like to purchase?\n", product.getName());
                                        String numProductsForPurchases = scanner.nextLine();
                                        numProductsForPurchase = readInt(numProductsForPurchases);
                                    } while (numProductsForPurchase == -1);

                                    buyer.buyProduct(product, numProductsForPurchase, store, scanner,database);

                                } else if (choice == 2) {
                                    Store store = buyer.viewStore(product, database);

                                    int quantity;
                                    do {
                                        System.out.printf("How many of %s would you like to add?\n", product.getName());
                                        String quantityForCart = scanner.nextLine();
                                        quantity = readInt(quantityForCart);
                                    } while (quantity == -1);

                                    buyer.addToShoppingCart(product, store, quantity);

                                } else if (choice == 3) {
                                    System.out.println("Taking you back to the product list...");
                                }

                            } while (choice == 3);

                            do {
                                System.out.println("Would you like to continue shopping, view your cart, view your " +
                                        "purchases, or log out?\n1. Continue shopping\n2. View cart\n3. View purchases" +
                                        "\n4. Log out");
                                String continueShoppings = scanner.nextLine();
                                continueShopping = readInt(continueShoppings);
                                scanner.nextLine();
                            } while (continueShopping == -1);

                            if (continueShopping == 1) {
                                System.out.println("Taking you back to the marketplace menu...");

                            } else if (continueShopping == 2) {
                                int removeFromCart = 0;
                                do {
                                    for (ProductPurchase productPurchase : buyer.getShoppingCart()) {
                                        System.out.printf("Name: %s, Quantity ordered: %d, Price: %.2f\n", productPurchase.getName(),
                                                productPurchase.getOrderQuantity(), productPurchase.getPrice());
                                    }

                                    int purchaseCart;
                                    do {
                                        System.out.println("Would you like to remove a product from your cart, purchase your cart," +
                                                " or continue shopping?\n1. Remove item from cart.\n2. Purchase cart.\n3. Continue.");
                                        String cartPurchase = scanner.nextLine();
                                        purchaseCart = readInt(cartPurchase);
                                    } while (purchaseCart == -1);

                                    if (purchaseCart == 1) {
                                        System.out.println("Which product would you like to remove from your cart? Please" +
                                                "type the name of the product.");
                                        String productName = scanner.nextLine();

                                        for (ProductPurchase productPurchase : buyer.getShoppingCart()) {
                                            if (productName.equalsIgnoreCase(productPurchase.getName())) {
                                                buyer.removeFromShoppingCart(productPurchase);
                                            }
                                        }
                                        removeFromCart = 1;

                                    } else if (purchaseCart == 2) {
                                        removeFromCart = buyer.purchaseCart(database);
                                        //buyer.writeToDatabase(false);

                                    }
                                } while (removeFromCart == 1);

                            } else if (continueShopping == 3) {
                                for (ProductPurchase productPurchase : buyer.getPurchases()) {
                                    System.out.printf("Name: %s, Quantity ordered: %d, Price: %.2f\n", productPurchase.getName(),
                                            productPurchase.getOrderQuantity(), productPurchase.getPrice());
                                }

                            } else if (continueShopping == 4) {
                                System.out.println("Thank you for shopping with us!");
                                try {
                                    buyer.writeToBuyer();
                                } catch (IOException | DataFormatException e) {
                                    //System.out.println("Something");
                                }
                                leave = true;

                            }

                            if (continueShopping != 1 && continueShopping != 4) {
                                int logOut;
                                do {
                                    System.out.println("Would you like to continue shopping or log out?\n 1. Continue shopping\n" +
                                            "2. Log Out\n");

                                    String outLog = scanner.nextLine();
                                    logOut = readInt(outLog);
                                } while (logOut == -1);

                                if (logOut == 1) {
                                    continueShopping = 1;

                                } else if (logOut == 2) {
                                    leave = true;
                                }
                            }
                        }
                    } else if (shopBy == 3) {
                        buyer.changeAccount(scanner, false);
                        continueShopping = 1;
                    } else if (shopBy == 4) {
                        buyer.deleteAccount(scanner, false);
                        System.out.println(EXIT);
                        System.out.println("Logging you out!");
                        leave = true;
                        break;
                    }
                    buyer.writeToBuyer();
                } while (continueShopping == 1);
            }
        } else {
            Seller seller = new Seller(user.getUniqueIdentifier());
            seller.sellerMenu(scanner, newSeller);
        }
    }
    public static int readInt(String input) {
        int result;
        try {
            result = Integer.parseInt(input);
            return result;
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid Integer!");
            return -1;
        }
    }

    public static double readDouble (String input) {
        double result;
        try {
            result = Double.parseDouble(input);
            return result;
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid balance!");
            return -1;
        }
    }
}