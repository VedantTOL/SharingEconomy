import java.io.*;
import java.util.*;

public class Seller2 {

    private final String STORES_FILE = "Stores.txt";

    public List<Store> readStoresFromFile() throws IOException {
        List<Store> stores = new ArrayList<>();
        File file = new File(STORES_FILE);
        if (!file.exists()) {
            throw new FileNotFoundException("Stores file not found.");
        }
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                if (line.startsWith("Store")) {
                    String storeName = line.substring(line.indexOf(". ") + 2);
                    List<Product> products = new ArrayList<>();
                    while ((line = fileReader.readLine()) != null) {
                        if (line.startsWith("Store")) {
                            break;
                        }
                        String[] itemFields = line.split(", ");
                        String itemName = itemFields[0];
                        String itemDescription = itemFields[1];
                        double itemPrice = Double.parseDouble(itemFields[2]);
                    }
                }
            }
        }
        return stores;
    }

}
