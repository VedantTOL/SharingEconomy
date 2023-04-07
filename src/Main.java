import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        User loginAccess = new User();
        User user;

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Create new account or login? Enter 1 to create an account and 2 to login with" +
                    "an existing account.");
            int option = scanner.nextInt();
            scanner.nextLine();

            if (option == 1) {
                //create new account
                user = loginAccess.addUser(scanner);
                break;
            } else if (option == 2) {
                do {
                    user = loginAccess.login(scanner);
                } while (user == null);
                break;
            }
        }

        user.changeAccount(scanner);

        int person = 0;
        do {
            try {
                System.out.println("Are you a buyer or a seller?\n1.Buyer\n2.Seller");
                person = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Please enter the number 1 or 2.");
            }
            if (person != 1 && person != 2) {
                System.out.println("Pleaser enter the number 1 or 2.");
            }
        } while (person != 1 && person != 2);


        if (person == 1) {

            System.out.println("What is your balance? (Enter in the format 00.00)");
            double balance = scanner.nextInt();
            scanner.nextLine();

            Buyer buyer = new Buyer(user.getUniqueIdentifier(), user.getEmail(), user.getPassword(), user.getName(),
                    user.getAge(), balance);

            int continueShopping = 0;
            boolean leave = false;
            while (!leave) {
                do {
                    System.out.println("Do you want to view the whole marketplace or shop by seller?\n1.Marketplace\n2.Seller");
                    int shopBy = scanner.nextInt();
                    scanner.nextLine();

                    if (shopBy == 1) {
                        System.out.println("Do you want to view all products or do you want to search for a specific product?\n" +
                                "1.View all products\n 2.Search\n");
                        int choice = scanner.nextInt();
                        scanner.nextLine();

                        String productList = buyer.viewMarketPlace(choice, scanner);
                        if (productList == null) {
                            System.out.println("Sorry! Sellers have not yet posted anything to the marketplace.");
                            System.out.println("Come back later when sellers have stocked their stores!");
                        } else {
                            do {
                                System.out.println(productList);
                                System.out.println("Enter the number that corresponds to the product you would like to view.");
                                int productNum = scanner.nextInt();
                                scanner.nextLine();
                                Product product = buyer.viewProduct(productList, productNum);

                                System.out.println("Description: " + product.getDescription() +
                                        "Quantity: " + product.getQuantityForPurchase());

                                System.out.println("Would you like to buy this product now, add it to your cart, or go back to the " +
                                        "previous page?\n1. Buy now\n" + "2. Add to cart\n" + "3. Previous page\n");
                                choice = scanner.nextInt();
                                scanner.nextLine();

                                if (choice == 1) {

                                    Store store = buyer.viewStore(product);
                                    System.out.printf("How many of %s would you like to purchase?", product.getName());

                                    int numProductsForPurchase = scanner.nextInt();
                                    scanner.nextLine();

                                    buyer.buyProduct(product, numProductsForPurchase, store, scanner);


                                } else if (choice == 2) {
                                    Store store = buyer.viewStore(product);
                                    buyer.addToShoppingCart(product, store);


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
                                    ArrayList<Product> shoppingCart = buyer.getShoppingCart();
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
                        // methods related to viewing by seller



                    }
                } while (continueShopping == 1);
            }
