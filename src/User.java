import java.lang.reflect.Array;
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
        this.email = database.get(uniqueIdentifier - 1).getEmail();
        this.password = database.get(uniqueIdentifier - 1).getPassword();
        this.name = database.get(uniqueIdentifier - 1).getName();
        this.age = database.get(uniqueIdentifier - 1).getAge();
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
                System.out.println(k.getEmail());
                bw.write(toString(k));
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

    public String toString(User user) {
        return String.format("%d, %s, %s, %s, %d", user.getUniqueIdentifier(), user.getEmail(), user.getPassword(), user.getName(), user.getAge());
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
}
