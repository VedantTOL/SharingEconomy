//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

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
}

