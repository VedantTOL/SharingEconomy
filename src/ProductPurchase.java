public class ProductPurchase extends Product{
    public int getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    private int orderQuantity;

    public ProductPurchase(int uniqueID, int orderQuantity) {
        super(uniqueID);
        this.orderQuantity = orderQuantity;
    }


    public String toDatabase() {
        return String.format("%d:%d", this.getUniqueID(), orderQuantity);
    }
}
