import java.awt.image.AreaAveragingScaleFilter;
import java.io.*;
import java.util.ArrayList;

public class User {
    private int uniqueIdentifier;
    private String email;
    private String password;
    private String name;
    private int age;

    public User(int uniqueIdentifier, String email, String password, String name, int age) {
        this.uniqueIdentifier = uniqueIdentifier;
        this.email = email;
        this.password = password;
        this.name = name;
        this.age = age;
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

        if (!userDetails[1].contains("@")) {
            throw new IllegalArgumentException("Valid email required!");
        } else {
            this.email = email;
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
    public boolean addUser(){}
    public boolean login(String email, String password, String fileName) {




    }
}
