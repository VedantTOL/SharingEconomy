/**
 * The NoAccountError class allows the program to throw an exception when a user tries to log in and they do not yet
 * have an account of any type.
 *
 *
 * @author Roger, Somansh, Ethan, Vedant
 * @version June 13, 2022
 */

public class NoAccountError extends Exception{
    public NoAccountError (String message) {
        super(message);
    }

}
