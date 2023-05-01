//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
/**
 * The UserDataBaseFormatError class allows the program to throw an exception when the user database that stores all
 * the user data contains data that is malformed/not in the correct format to be read from the file.
 *
 *
 * @author Roger, Somansh, Ethan, Vedant
 * @version June 13, 2022
 */

public class UserDatabaseFormatError extends Exception {
    public UserDatabaseFormatError(String message) {
        super(message);
    }
}


