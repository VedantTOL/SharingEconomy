import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        User loginAccess = new User();
        User user;

        Scanner scanner = new Scanner(System.in);

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
    }
}