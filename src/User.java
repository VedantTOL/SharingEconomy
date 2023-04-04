import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;

public class User {
    private int uniqueIdentifier;
    private String email;
    private String password;
    private String name;
    private int age;

    public User() {
        this.uniqueIdentifier = -1;
        this.email = null;
        this.password = null;
        this.name = null;
        this.age = 0;
    }

    public User(int uniqueIdentifier, String email, String password, String name, int age) {
        this.uniqueIdentifier = uniqueIdentifier;
        this.email = email;
        this.password = password;
        this.name = name;
        this.age = age;
    }
    public User(int uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
        ArrayList<User> database = readUserDatabase("./src/UserDatabase.txt");
        this.email = database.get(uniqueIdentifier).getEmail();
        this.password = database.get(uniqueIdentifier).getPassword();
        this.name = database.get(uniqueIdentifier).getName();
        this.age = database.get(uniqueIdentifier).getAge();
    }

    public User(String[] userDetails) throws UserDatabaseFormatError {
        if (userDetails.length != 5) {
            throw new UserDatabaseFormatError("Insufficient Details, please try again!");
        }

        try {
            this.uniqueIdentifier = Integer.parseInt(userDetails[0]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("UniqueIdentifier must be an Integer");
        }

        if (!checkEmailFormat(userDetails[1])) {
            throw new IllegalArgumentException("Valid email required!");
        } else {
            this.email = userDetails[1];
        }

        this.password = userDetails[2];
        this.name = userDetails[3];

        try {
            int tempAge = Integer.parseInt(userDetails[4]);
            if (tempAge < 0 ) {
                throw new IllegalArgumentException("Valid age required!");
            } else {
                this.age = tempAge;
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Age must be an Integer");
        }


    }


    public int getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(int uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }


    public ArrayList<User> readUserDatabase(String fileName) {
        File f;
        FileReader fr;
        BufferedReader bfr;
        String line;
        ArrayList<User> database= new ArrayList<User>();

        try {
            bfr = new BufferedReader(new FileReader(new File(fileName)));

            while (true) {
                line = bfr.readLine();

                if (line == null) {
                    break;
                }
                String[] userDetails = line.split(", ");
                try {
                    User newUser = new User(userDetails);
                    database.add(newUser);
                } catch (UserDatabaseFormatError e) {
                    e.printStackTrace();
                }


            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return database;

    }
    public User addUser(Scanner scanner){
        String email;
        String password;
        String name;
        int age;
        int uniqueId;

        do {
            System.out.println("Enter your email: ");
            email = scanner.nextLine();

        } while (!checkEmailFormat(email));

        System.out.println("Enter a password: ");
        password = scanner.nextLine();

        //insert method to check password strength

        System.out.println("What's your name?");
        name = scanner.nextLine();

        System.out.println("What's your age?");
        age = scanner.nextInt();
        scanner.nextLine();

        ArrayList<User> database = readUserDatabase("./src/UserDatabase.txt");
        uniqueId = database.get(database.size() - 1).getUniqueIdentifier() + 1;
        User user = new User(uniqueId, email, password, name, age);
        database.add(user);

        File f;
        FileWriter fw;
        BufferedWriter bw;

        try {
            bw = new BufferedWriter(new FileWriter(new File("./src/UserDatabase.txt")));
            for (User k: database) {
                bw.write(k.constructorString());
                bw.write("\n");
            }
            bw.close();
            return user;
        } catch (IOException e) {
            return null;
        }
    }

    public boolean checkEmailFormat(String email) {
        String[] emailChecker = email.split("@");
        if (emailChecker.length == 2) {
            return true;
        } else {
            System.out.println("Please enter a valid email!");
            return false;
        }
    }

    public String constructorString() {
        return String.format("%d, %s, %s, %s, %d", this.getUniqueIdentifier(), this.getEmail(), this.getPassword(), this.getName(), this.getAge());
    }

    public String toString() {
        return String.format("ID = <%d>\nEmail = <%s>\nPassword = <****>\nName = <%s>\nAge <%d>", this.getUniqueIdentifier(), this.getEmail(), this.getPassword(), this.getName(), this.getAge());
    }
    public User login(Scanner scanner) {
        ArrayList<User> database = readUserDatabase("./src/UserDatabase.txt");
        System.out.println("Email: ");
        String emailCheck = scanner.nextLine();
        System.out.println("Password: ");
        String passwordCheck = scanner.nextLine();

        for (User user: database) {
            if (user.getEmail().equals(emailCheck)) {
                if(user.getPassword().equals(passwordCheck)) {
                    System.out.println("Login Successful!");
                    return user;
                } else {
                    System.out.println("Incorrect Password!");
                    return null;
                }
            }
        }
        System.out.println("Invalid email!");
        return null;
    }

    public User changeAccount(Scanner scanner) {
        System.out.println("Here are your details: ");
        System.out.println(this.toString());
        while (true) {
            System.out.println("What would you like to change?");
            System.out.println("1. Email\n 2. Password\n 3.Name\n 4.Age");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> {
                    while (true) {
                        System.out.println("Enter new email: ");
                        String email = scanner.nextLine();
                        if (checkEmailFormat(email)) {
                            setEmail(email);
                            break;
                        } else {
                            System.out.println("Please enter a valid email!");
                        }
                    }
                }
                case 2 -> {
                    while (true) {
                        System.out.println("Please enter your old password: ");
                        String checkPassword = scanner.nextLine();
                        if (checkPassword.equals(this.getPassword())) {
                            System.out.println("Please enter your new password: ");
                            setPassword(scanner.nextLine());
                            break;
                        } else {
                            System.out.println("Incorrect password, please try again.");
                        }
                    }
                }
                case 3 -> {
                    System.out.println("Please enter a new name");
                    setName(scanner.nextLine());
                }
                case 4 -> {
                    int age;
                    System.out.println("Please enter a new age");
                    while (true) {
                        age = scanner.nextInt();
                        if (age > 0) {
                            setAge(age);
                            break;
                        } else {
                            System.out.println("Please enter a valid age!");
                        }
                    }
                }
                default -> {
                    System.out.println("Please select a valid menu option!");
                    continue;
                }
            }
                break;
            }
        return this;
    }

    public boolean deleteAccount(Scanner scanner) {
        System.out.println("Would you like to delete your account? This cannot be undone.");
        System.out.println("1.Yes\n2.No");
        int choice = scanner.nextInt();
        scanner.nextLine();
        while (true) {
            if (choice == 1) {
                ArrayList<User> database = readUserDatabase("./src/UserDatabase.txt");
                database.remove(this.uniqueIdentifier - 1);
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(new File("./src/UserDatabase.txt")));
                    for (User k : database) {
                        bw.write(k.constructorString());
                        bw.write("\n");
                    }
                    bw.close();
                    return true;
                } catch (IOException e) {
                    System.out.println("An error occurred please try again!");
                    return false;
                }
            } else if (choice == 2) {
                return false;
            } else {
                System.out.println("Please select a valid menu option!");
            }
        }
    }


}
