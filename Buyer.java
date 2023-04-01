public class Buyer {
    private String name;
    private double balance;
    private String item;
    private double price;

    public Buyer(String name, double balance) {
        this.name = name;
        this.balance = balance;
        this.item = item;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getItem() {
        return item;
    }
}

    /*/ This code would implement the Product class
    public void buyItem(Item item) {
        if (balance >= item.getPrice()) {
            balance -= item.getPrice();
            System.out.println(name + " bought " + item.getName() + " for $" + item.getPrice());
        } else {
            System.out.println(name + " cannot afford " + item.getName());
        }
    }
}

     */
