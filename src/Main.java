import java.io.IOException;
import java.util.*;
import java.util.zip.DataFormatException;

public class Main {
    public static final String EXIT = "Thank you! Please come again!"; //Displayed when user exits the program.
    public static final String INVALID_OPTION = "Please select a valid menu option!";
    public static void main(String[] args) throws DataFormatException, IOException {
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

        decisionA = 0;

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
                    user = null;
                }
            } while (user == null);
        } else if (decisionA == 3) {
            System.out.println(EXIT);
            return;
        }


        if (!isSeller) {

            System.out.println("What is your balance?");
            double balance = scanner.nextDouble();
            scanner.nextLine();

            Buyer buyer = new Buyer(user.getUniqueIdentifier(), user.getEmail(), user.getPassword(), user.getName(),
                    user.getAge(), balance);

            int continueShopping = 0;
            boolean leave = false;
            while (!leave) {
                do {
                    System.out.println("Do you want to view the whole marketplace or shop by Seller?\n1. Marketplace\n2. Seller");
                    int shopBy = scanner.nextInt();
                    scanner.nextLine();

                    if (shopBy == 1) {
                        System.out.println("Do you want to view all products or do you want to search for a specific product?\n" +
                                "1. View all products\n2. Search\n");
                        int choice = scanner.nextInt();
                        scanner.nextLine();

                        ArrayList<Product> productList = buyer.viewMarketPlace(choice, scanner);
                        if (productList == null) {
                            System.out.println("Sorry! Sellers have not yet posted anything to the marketplace.");
                            System.out.println("Come back later when sellers have stocked their stores!");
                        } else {
                            do {
                                int i = 1;
                                for (Product product: productList) {
                                    System.out.printf("%d. %s\n", i, product.marketplaceString());
                                    i++;
                                }

                                System.out.println("Enter the number that corresponds to the product you would like to view.");
                                int productNum = scanner.nextInt();
                                scanner.nextLine();

                                Product product = buyer.viewProduct(productList, productNum);

                                System.out.println("Would you like to buy this product now, add it to your cart, or go back to the " +
                                        "previous page?\n1. Buy now\n" + "2. Add to cart\n" + "3. Previous page\n");
                                choice = scanner.nextInt();
                                scanner.nextLine();

                                if (choice == 1) {

                                    //Store store = buyer.viewStore(product);
                                    System.out.printf("How many of %s would you like to purchase?", product.getName());

                                    int numProductsForPurchase = scanner.nextInt();
                                    scanner.nextLine();

                                    //buyer.buyProduct(product, numProductsForPurchase, store, scanner);


                                } else if (choice == 2) {
                                    //Store store = buyer.viewStore(product);
                                    //buyer.addToShoppingCart(product, store);


                                } else if (choice == 3) {
                                    System.out.println("Taking you back to the product list...");
                                }


                            } while (choice == 3);

                            System.out.println("Would you like to continue shopping, view your cart, view your " +
                                    "purchases, or log out?\n1. Continue shopping\n2. View cart\n3. View purchases" +
                                    "\n4. Log out");

                            continueShopping = scanner.nextInt();
                            scanner.nextLine();

                            if (continueShopping == 1) {
                                System.out.println("Taking you back to the marketplace menu...");

                            } else if (continueShopping == 2) {
                                // call viewCart method here

                                System.out.println("Would you like to remove anything from your cart, purchase your cart," +
                                        " or continue shopping?\n1. Remove item from cart.\n2. Purchase cart.\n3. Continue.");

                                int purchaseCart = scanner.nextInt();
                                scanner.nextLine();

                                if (purchaseCart == 1) {
                                    System.out.println("Which product would you like to remove from your cart? Please" +
                                            "type the name of the product.");
                                    // iterate through arrayList that was made from viewCart method and select the product
                                    // that matches the name of the product the user typed
                                    // buyer.removeFromShoppingCart(product);

                                } else if (purchaseCart == 2) {
                                    ArrayList<ProductPurchase> shoppingCart = buyer.getShoppingCart();
                                    // need method to purchase whole cart
                                }

                            } else if (continueShopping == 3) {
                                // need a method to view purchases

                            } else if (continueShopping == 4) {
                                System.out.println("Thank you for shopping with us!");
                                // write to purchases and shopping cart file
                                leave = true;

                            }

                            if (continueShopping != 1 && continueShopping != 4) {
                                System.out.println("Would you like to continue shopping or log out?\n 1. Continue shopping\n" +
                                        "2. Log Out\n");
                                int logOut = scanner.nextInt();

                                if (logOut == 1) {
                                    continueShopping = 1;

                                } else if (logOut == 2) {
                                    // write to purchases and shopping cart file
                                    leave = true;
                                }
                            }
                        }


                    } else if (shopBy == 2) {
                        // methods related to viewing by isSeller


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

}
