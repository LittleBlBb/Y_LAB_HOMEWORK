package Product;

import java.util.ArrayList;

public class Catalog {
    private static int dynamicCatalogId = 1;
    private int catalogID;
    private String catalogName;
    private ArrayList<Product> products;

    public Catalog(String catalogName) {
        catalogID = dynamicCatalogId;
        this.catalogName = catalogName;
        products = fillProducts(catalogName);
        dynamicCatalogId ++;
    }

    private static ArrayList<Product> fillProducts(String catalogName){
        ArrayList<Product> arrayList = new ArrayList<>() {{
           switch (catalogName){
               case "Смартфоны":
                   add(new Product("Смартфон Apple Iphone 15", 999.99));
                   add(new Product("Смартфон Samsung Galaxy S10972", 100000.00));
                   add(new Product("Планшет Apple Ipad Air 10", 500.00));
                   break;
               case "Комплектующие для ПК":
                   add(new Product("Видеокарта RTX 5090 TI", 1500.00));
                   add(new Product("Видеокарта GTX 1660 Super", 130.00));
                   add(new Product("Процессор Intel Core I9-9900K", 500.00));
                   break;
               case "Бытовая техника":
                   add(new Product("Индукционная варочная поверхность DEXP EH-I4MB/B", 100.00));
                   add(new Product("Газовая варочная поверхность DEXP LD4GTB", 130.00));
                   add(new Product("Скребок OneTwo O2SR019", 1.5));
                   break;
               case "Офис и мебель":
                   add(new Product("МФУ лазерное Kyocera ECOSYS M2040dn", 820.94));
                   add(new Product("3D-сканер Creality CR-Scan Raptor", 1799.99));
                   add(new Product("Компьютерное кресло ARDOR GAMING Chaos Guard 400M серый", 140.00));
                   break;
           }
        }};
        return arrayList;
    }

    public Object getProducts() {
        return products.clone();
    }

    public void displayProducts(){
        System.out.printf("%-5s %-60s %-10s%n", "ID", "Название товара", "Цена товара");
        System.out.println("----- ------------------------------------------------------------ ----------");
        for (Product product : products){
            System.out.printf("%-5d %-60s %-10f%n",
                    product.getId(),
                    product.getName(),
                    product.getPrice());
        }
    }

    public String getCatalogName(){
        return catalogName;
    }
}
