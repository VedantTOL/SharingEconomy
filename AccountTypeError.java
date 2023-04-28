/**
 * The AccountTypeError class allows the program to throw an exception when a user tries to log in with the incorrect
 * account type. For example, a user with an account type of buyer attempts to log in as a seller.
 *
 *
 * @author Roger, Somansh, Ethan, Vedant
 * @version June 13, 2022
 */

public class AccountTypeError extends Exception{

    public AccountTypeError (String message) {
        super(message);
    }

}



