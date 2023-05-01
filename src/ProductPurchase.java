//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
/**
 * The product purchase class is simply an extension of the product class that allows the users of the program to keep
 * track of how many of the associated product have been ordered.
 *
 *
 * @author Roger, Somansh, Ethan, Vedant
 * @version June 13, 2022
 */

public class ProductPurchase extends Product {
    private int orderQuantity;

    public int getOrderQuantity() {
        return this.orderQuantity;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public ProductPurchase(int uniqueID, int orderQuantity) {
        super(uniqueID);
        this.orderQuantity = orderQuantity;
    }

    public String toString() {
        return String.format("%d:%d", this.getUniqueID(), this.orderQuantity);
    }

    public String viewOrder(){
        return String.format("Product Name: %s Quantity: %d", this.getName(), this.orderQuantity);
    }
}

