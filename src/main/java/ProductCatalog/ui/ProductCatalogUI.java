package ProductCatalog.ui;

import ProductCatalog.models.AuditEntry;
import ProductCatalog.models.Product;
import ProductCatalog.services.AuditService;
import ProductCatalog.services.CatalogService;
import ProductCatalog.services.MetricsService;
import ProductCatalog.services.ProductFilterService;
import ProductCatalog.services.ProductService;
import ProductCatalog.services.UserService;

import java.util.List;
import java.util.Scanner;

/**
 * Консольный пользовательский интерфейс для работы с системой каталога товаров.
 * Поддерживает навигацию по каталогам, управление товарами и пользователями.
 */
public class ProductCatalogUI {
    private final UserService userService;
    private final CatalogService catalogService;
    private final ProductService productService;
    private final ProductFilterService filterService;
    private final AuditService auditService;

    /**
     * Создает экземпляр {@code ProductCatalogUI}.
     *
     * @param catalogService сервис каталогов
     * @param productService сервис товаров
     * @param userService сервис пользователей
     * @param auditService сервис аудита
     */
    public ProductCatalogUI(CatalogService catalogService, ProductService productService, UserService userService, AuditService auditService) {
        this.catalogService = catalogService;
        this.userService = userService;
        this.productService = productService;
        this.auditService = auditService;
        MetricsService.getInstance(catalogService);
        this.filterService = ProductFilterService.getInstance();
    }

    /**
     * Запускает главное меню программы.
     */
    public void run() {
        Scanner console = new Scanner(System.in);
        displayMainMenu(console);
    }

    private void displayMainMenu(Scanner sc) {
        int choice;
        do {
            System.out.println("\n=== МЕНЮ ===");

            if (userService.isAuthenticated()) {
                System.out.println("Вы вошли как: " +
                        userService.getCurrentUser().getUsername() +
                        " (" + userService.getCurrentUser().getRole() + ")");
                System.out.println("4. Выйти");
                if (userService.isAdmin()) {
                    System.out.println("5. Журнал аудита");
                }
            } else {
                System.out.println("Вы не авторизованы");
            }

            System.out.println("""
            1. Каталоги
            2. Вход
            3. Регистрация
            0. Выход
            """);

            System.out.print("Ваш выбор: ");
            try {
                choice = sc.nextInt();
                switch (choice) {
                    case 1 -> displayCatalogsMenu(sc);
                    case 2 -> login(sc);
                    case 3 -> register(sc);
                    case 4 -> userService.logout();
                    case 5 -> {
                        if (userService.isAdmin()) displayAuditLog();
                    }
                    case 0 -> System.out.println("Выход...");
                    default -> System.out.println("Ошибка ввода.");
                }
            } catch (Exception e) {
                sc.nextLine();
                choice = -1;
            }

        } while (choice != 0);
    }

    private void login(Scanner sc) {
        sc.nextLine();
        System.out.print("Логин: ");
        String u = sc.nextLine();
        System.out.print("Пароль: ");
        String p = sc.nextLine();

        if (userService.login(u, p)) {
            System.out.println("Добро пожаловать, " + u);
        } else {
            System.out.println("Ошибка входа.");
        }
    }

    private void register(Scanner sc) {
        sc.nextLine();
        System.out.print("Логин: ");
        String u = sc.nextLine();
        System.out.print("Пароль: ");
        String p = sc.nextLine();

        if (userService.register(u, p))
            System.out.println("Готово.");
        else
            System.out.println("Пользователь уже существует.");
    }

    private void displayCatalogsMenu(Scanner sc) {
        var catalogs = catalogService.getAll();
        int choice;

        do {
            System.out.println("\n=== КАТАЛОГИ ===");
            for (int i = 0; i < catalogs.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, catalogs.get(i).getName());
            }
            System.out.println("0. Назад");

            System.out.print("Ваш выбор: ");
            try {
                choice = sc.nextInt();
                if (choice > 0 && choice <= catalogs.size()) {
                    long catalogId = catalogs.get(choice - 1).getId();
                    displayProductsMenu(catalogId, sc);
                }
            } catch (Exception e) {
                sc.nextLine();
                choice = -1;
            }

        } while (choice != 0);
    }

    private void displayProductsMenu(long catalogId, Scanner sc) {
        while (true) {

            List<Product> products = productService.getProducts(catalogId);

            System.out.println("\n=== ТОВАРЫ ===");
            for (int i = 0; i < products.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, products.get(i).toShortString());
            }

            if (userService.isAdmin())
                System.out.println("A. Добавить");

            System.out.println("F. Фильтр");
            System.out.println("0. Назад");
            System.out.print("Ваш выбор: ");

            String input = sc.next();

            if (input.equalsIgnoreCase("0")) return;

            if (input.equalsIgnoreCase("A") && userService.isAdmin()) {
                createProduct(catalogId, sc);
                continue;
            }

            if (input.equalsIgnoreCase("F")) {
                filterProductsMenu(catalogId, sc);
                continue;
            }

            try {
                int n = Integer.parseInt(input);
                if (n > 0 && n <= products.size()) {
                    displayProductMenu(products.get(n - 1), sc);
                }
            } catch (NumberFormatException ignore) {}
        }
    }

    private void createProduct(long catalogId, Scanner sc) {
        sc.nextLine();
        System.out.print("Название: ");
        String n = sc.nextLine();
        System.out.print("Цена: ");
        double price = sc.nextDouble();
        sc.nextLine();
        System.out.print("Описание: ");
        String d = sc.nextLine();
        System.out.print("Бренд: ");
        String b = sc.nextLine();
        System.out.print("Категория: ");
        String c = sc.nextLine();

        Product p = new Product(0, catalogId, n, price, d, b, c);

        if (productService.createProduct(p))
            System.out.println("Добавлено.");
        else
            System.out.println("Ошибка!");
    }

    private void displayProductMenu(Product p, Scanner sc) {
        int ch;
        do {
            System.out.println("\n" + p);

            if (userService.isAdmin())
                System.out.println("1. Удалить\n2. Изменить");

            System.out.println("0. Назад");

            System.out.print("Ваш выбор: ");
            try {
                ch = sc.nextInt();
                switch (ch) {
                    case 1 -> {
                        if (productService.deleteProduct(p.getId()))
                            System.out.println("Удалено.");
                        return;
                    }
                    case 2 -> editProduct(p, sc);
                    case 0 -> { return; }
                }
            } catch (Exception e) {
                sc.nextLine();
                ch = -1;
            }
        } while (ch != 0);
    }

    private void editProduct(Product p, Scanner sc) {
        Product tmp = new Product(
                p.getId(),
                p.getCatalogId(),
                p.getName(),
                p.getPrice(),
                p.getDescription(),
                p.getBrand(),
                p.getCategory()
        );

        int ch;
        do {
            System.out.println("\n" + tmp);
            System.out.println("""
            1. Название
            2. Цена
            3. Описание
            4. Бренд
            5. Категория
            6. Сохранить
            0. Отмена
            """);

            System.out.print("Ваш выбор: ");
            try {
                ch = sc.nextInt();
                sc.nextLine();

                switch (ch) {
                    case 1 -> {
                        System.out.print("Новое название: ");
                        tmp.setName(sc.nextLine());
                    }
                    case 2 -> {
                        System.out.print("Новая цена: ");
                        tmp.setPrice(sc.nextDouble());
                        sc.nextLine();
                    }
                    case 3 -> {
                        System.out.print("Новое описание: ");
                        tmp.setDescription(sc.nextLine());
                    }
                    case 4 -> {
                        System.out.print("Новый бренд: ");
                        tmp.setBrand(sc.nextLine());
                    }
                    case 5 -> {
                        System.out.print("Новая категория: ");
                        tmp.setCategory(sc.nextLine());
                    }
                    case 6 -> {
                        if (productService.updateProduct(tmp))
                            System.out.println("Сохранено.");
                        else
                            System.out.println("Ошибка сохранения.");
                        return;
                    }
                }

            } catch (Exception e) {
                sc.nextLine();
                ch = -1;
            }

        } while (ch != 0);
    }

    private void filterProductsMenu(long catalogId, Scanner sc) {
        List<Product> list = productService.getProducts(catalogId);

        sc.nextLine();
        System.out.println("""
        1. По имени
        2. По цене
        3. По бренду
        4. По категории
        0. Назад
        """);

        System.out.print("Выбор: ");
        int ch = sc.nextInt();
        sc.nextLine();

        List<Product> filtered = list;

        switch (ch) {
            case 1 -> {
                System.out.print("Название содержит: ");
                filtered = filterService.filterByName(list, sc.nextLine());
            }
            case 2 -> {
                System.out.print("Минимум: ");
                double min = sc.nextDouble();
                System.out.print("Максимум: ");
                double max = sc.nextDouble();
                sc.nextLine();
                filtered = filterService.filterByPrice(list, min, max);
            }
            case 3 -> {
                System.out.print("Бренд: ");
                filtered = filterService.filterByBrand(list, sc.nextLine());
            }
            case 4 -> {
                System.out.print("Категория: ");
                filtered = filterService.filterByCategory(list, sc.nextLine());
            }
            case 0 -> { return; }
        }

        System.out.println("\n=== РЕЗУЛЬТАТ ===");
        for (int i = 0; i < filtered.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, filtered.get(i).toShortString());
        }
    }

    private void displayAuditLog() {
        List<AuditEntry> log = auditService.getAll();
        System.out.println("\n=== АУДИТ ===");
        for (AuditEntry e : log) System.out.println(e);
        System.out.println("============");
    }
}