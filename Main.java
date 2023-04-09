import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.zip.DataFormatException;

public class Main {
    public static final String EXIT = "Thank you! Please come again!"; //Displayed when user exits the program.
    public static final String INVALID_OPTION = "Please select a valid menu option!";
    public static void main(String[] args) {
        User loginAccess = new User();
        User user = null;
        boolean isSeller = false;

        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("Are you a Customer or Seller?");
            System.out.printf("1. Customer\n2. Seller\n3. Exit\n");
            String userChoice = scanner.nextLine();
            int x = readInt(userChoice); //checks if integer, returns -1 when not an integer.
            if (x == 1) {
                isSeller = false;
            } else if (x == 2) {
                isSeller = true;
            } else if (x == 3) {
                System.out.println(EXIT);
                return;
            } else {
                System.out.println(INVALID_OPTION);
            }

            do {
                System.out.printf("Create new account or login?\n1. Create an account\n2. Login\n3. Exit\n");
                String choice = scanner.nextLine();
                int option = readInt(choice);
                if (option == 1) {
                    //create new account
                    user = loginAccess.addUser(scanner, isSeller);
                    break;
                } else if (option == 2) {
                    boolean exitCondition;
                    do {
                        exitCondition = true;
                        user = loginAccess.login(scanner, isSeller);
                        if (user == null) {
                            do  {
                                System.out.println("Would you like to create a new account?");
                                System.out.printf("1. Yes\n2. No\n3. Exit\n");
                                String choiceX = scanner.nextLine();
                                int optionX = readInt(choiceX);
                                if (optionX == 1) {
                                    user = loginAccess.addUser(scanner, isSeller);
                                    break;
                                } else if (optionX == 2) {
                                    System.out.println("You will now be redirected to the Main Menu. Select the correct account type!");
                                    exitCondition = false;
                                    break;
                                } else if (optionX == 3) {
                                    System.out.println(EXIT);
                                    return;
                                } else {
                                    System.out.println(INVALID_OPTION);
                                }
                            } while (exitCondition);
                        }
                    } while (user == null && !exitCondition);

                } else if (option == 3 ) {
                    System.out.println(EXIT);
                }
            } while (user == null);
        } while (user == null);



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

            ArrayList<ProductPurchase> purchases = buyer.viewPurchases();
            buyer.setPurchases(purchases);

            int continueShopping = 0;
            boolean leave = false;
            while (!leave) {
                do {
                    int shopBy = 0;
                    do {
                        System.out.println("Do you want to view the whole marketplace or shop by Seller?\n1. Marketplace\n2. Seller");
                        String shop = scanner.nextLine();
                        shopBy = readInt(shop);
                    } while (shopBy == -1);

                    int choice;
                    if (shopBy == 1) {

                        do {
                            System.out.println("Do you want to view all products or do you want to search for a specific product?\n" +
                                    "1. View all products\n2. Search\n");
                            String choices = scanner.nextLine();
                            choice = readInt(choices);
                        } while (choice == -1);

                        ArrayList<Product> productList = buyer.viewMarketPlace(choice, scanner);

                        if (productList == null) {
                            System.out.println("Sorry! Sellers have not yet posted anything to the marketplace.");
                            System.out.println("Come back later when sellers have stocked their stores!");
                            System.out.println("Logging you out...");
                            leave = true;
                        } else {
                            do {
                                int i = 1;
                                for (Product product: productList) {
                                    System.out.printf("%d. %s\n", i+1, product.marketplaceString());
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
                                } while (choice == -1);

                                if (choice == 1) {

                                    Store store = buyer.viewStore(product);

                                    int numProductsForPurchase;
                                    do {
                                        System.out.printf("How many of %s would you like to purchase?\n", product.getName());
                                        String numProductsForPurchases = scanner.nextLine();
                                        numProductsForPurchase = readInt(numProductsForPurchases);
                                    } while (numProductsForPurchase == -1);

                                    buyer.buyProduct(product, numProductsForPurchase, store, scanner);

                                } else if (choice == 2) {
                                    Store store = buyer.viewStore(product);

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
                                        removeFromCart = buyer.purchaseCart();

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
                            seller = buyer.shopBySeller(scanner);
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
                        } else {
                            do {
                                int i = 1;
                                for (Product product: productList) {
                                    System.out.printf("%d. %s\n", i+1, product.marketplaceString());
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
                                } while (choice == -1);

                                if (choice == 1) {

                                    Store store = buyer.viewStore(product);

                                    int numProductsForPurchase;
                                    do {
                                        System.out.printf("How many of %s would you like to purchase?\n", product.getName());
                                        String numProductsForPurchases = scanner.nextLine();
                                        numProductsForPurchase = readInt(numProductsForPurchases);
                                    } while (numProductsForPurchase == -1);

                                    buyer.buyProduct(product, numProductsForPurchase, store, scanner);

                                } else if (choice == 2) {
                                    Store store = buyer.viewStore(product);

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
                                        removeFromCart = buyer.purchaseCart();

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
                    }

                } while (continueShopping == 1);
            }
        } else {
            Seller seller = new Seller(user.getUniqueIdentifier());
            seller.sellerMenu(scanner);
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
