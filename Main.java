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
    public static void main(String[] args) throws DataFormatException, IOException, UserDatabaseFormatError {
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

        if (decisionA == 1) isSeller = false;
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

            //buyer.buyerMenu(scanner);

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