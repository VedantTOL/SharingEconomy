import java.util.Scanner;
import java.util.ArrayList;
public class Main {
    public static void main(String[] args) {
        User loginAccess = new User();
        User user;
        //Seller seller;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Buyer or Seller");
        int choice = scanner.nextInt();
        while (true) {
            System.out.println("Create new account or login?");
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

        if (choice == 1) {
            Seller seller = new Seller(user.getUniqueIdentifier());
            System.out.println(user.getName());
            System.out.println(seller.getName());
        }

        //user.changeAccount(scanner);

    //ArrayList<Seller> database = seller.readSellerDatabase();
    //System.out.println(database.get(0));
    }
}