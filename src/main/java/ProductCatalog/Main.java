package ProductCatalog;

import ProductCatalog.UI.ProductCatalogUI;

public class Main {
    public static void main(String[] args) {
        System.out.println("Добро пожаловать в систему каталога товаров!");
        System.out.println("=============================================\n");

        ProductCatalogUI ui = new ProductCatalogUI();
        ui.run();

        System.out.println("\nПриложение завершено.");
    }
}
