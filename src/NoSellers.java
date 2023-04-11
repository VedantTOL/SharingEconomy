/**
 * The NoSellers class allows the program to throw an exception when reading the SellerDatabase but no Sellers exist.
 *
 *
 * @author Roger, Somansh, Ethan, Vedant
 * @version April 4, 2023
 */

public class NoSellers extends Exception{

    public NoSellers (String message) {
        super(message);
    }

}

