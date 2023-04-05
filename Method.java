import java.util.InputMismatchException;
import java.util.Scanner;
public class Method {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("What actions would you like to take?\n" +
                "1. Delete store\n" +
                "2. Add store\n" +
                "3. Edit store");
        int decision = scanner.nextInt();
        scanner.nextLine();
        if (decision == 1) { //Add store
            System.out.println("What is the name of the store you want to add?");
            String s1toreName = scanner.nextLine();
            System.out.println("How many items do you want to add?");
            int items = scanner.nextInt();
            scanner.nextLine();

            for (int i = 0; i < items; i++) {
                int num = i + 1;

                System.out.print("Enter the " + num);
                String numCode = Integer.toString(num);
                String lastChar = numCode.substring(numCode.length() - 1); // the last character of the string
                String suffix = null;
                if (numCode.length() > 1) {
                    numCode = numCode.substring(numCode.length() - 2);
                    if (numCode.charAt(0) == '1') {
                        suffix = "th";
                    } else {
                        if (lastChar.equals("1")) {
                            suffix = "st";
                        } else if (lastChar.equals("2")) {
                            suffix = "nd";
                        } else if (lastChar.equals("3")) {
                            suffix = "rd";
                        } else {
                            suffix = "th";
                        }

                    }
                } else {

                    if (lastChar.equals("1")) {
                        suffix = "st";
                    } else if (lastChar.equals("2")) {
                        suffix = "nd";
                    } else if (lastChar.equals("3")) {
                        suffix = "rd";
                    } else {
                        suffix = "th";
                    }

                }

                System.out.println(suffix + " item name.");
                String name = scanner.nextLine();

                System.out.println("What is the description?");
                String description = scanner.nextLine();

                System.out.println("How much does this item cost?");
                scanner.nextLine();

            }
            //TODO Write this to the updated file
        }


    }


}
