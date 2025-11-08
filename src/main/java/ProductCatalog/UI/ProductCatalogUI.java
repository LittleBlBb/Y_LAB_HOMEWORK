package ProductCatalog.UI;

import ProductCatalog.Models.AuditEntry;
import ProductCatalog.Models.Product;
import ProductCatalog.Services.ProductCatalogService;
import ProductCatalog.Services.UserService;
import ProductCatalog.UnitOfWork;

import java.util.List;
import java.util.Scanner;

public class ProductCatalogUI {
    private final ProductCatalogService service;
    private final UserService userService;

    public ProductCatalogUI() {
        this.service = ProductCatalogService.getInstance();
        this.userService = UserService.getInstance();
    }

    public void run() {
        Scanner console = new Scanner(System.in);
        displayMainMenu(console);
    }

    private void displayMainMenu(Scanner console) {
        int choice;
        do {
            System.out.println("\n\t\tMENU");
            if (userService.isAuthenticated()){
                System.out.println("Вы вошли как: " + userService.getCurrentUser().getUsername() +
                        " (" + userService.getCurrentUser().getRole() + ")");
                System.out.println("4. Выйти из аккаунта");
                if(userService.isAdmin()){
                    System.out.println("5. Просмотр аудита (admin)");
                }
            } else {
                System.out.println("Вы не авторизованы");
            }
            System.out.println("1. Просмотреть каталоги");
            System.out.println("2. Войти");
            System.out.println("3. Зарегистрироваться");
            System.out.println("0. Выход из программы");
            System.out.print("Ваш выбор: ");

            try {
                choice = console.nextInt();
                switch (choice) {
                    case 1 -> displayCatalogsMenu(console);
                    case 2 -> loginMenu(console);
                    case 3 -> registerMenu(console);
                    case 4 -> {
                        if (userService.isAuthenticated()){
                            userService.logout();
                            System.out.println("Вы вышли из аккаунта.");
                        } else {
                            System.out.println("Вы не вошли в систему.");
                        }
                    }
                    case 5 -> {
                        if (userService.isAdmin()){
                            displayAuditLog();
                        } else {
                            System.out.println("недостаточно прав.");
                        }
                    }
                    case 0 -> {
                        System.out.println("До свидания!");
                        return;
                    }
                    default -> System.out.println("Некорректный выбор.");
                }
            } catch (Exception e) {
                pauseForInput(console);
                choice = -1;
            }
        } while (choice != 0);
    }

    private void loginMenu(Scanner console){
        console.nextLine();
        System.out.println("Введите логин: ");
        String username = console.nextLine();
        System.out.println("Введите пароль: ");
        String password = console.nextLine();

        if (userService.login(username, password)){
            System.out.println("Добро пожаловать, " + username + "!");
        } else {
            System.out.println("Неверный логин или пароль.");
        }
    }

    private void registerMenu(Scanner console){
        console.nextLine();
        System.out.println("Придумайте логин: ");
        String username = console.nextLine();
        System.out.println("придумайте пароль: ");
        String password = console.nextLine();

        if (userService.register(username, password))
        {
            System.out.println("Регистрация успешна! Теперь вы можете войти.");
        } else {
            System.out.println("Пользователь с таким логином уже существует.");
        }

    }

    private void displayCatalogsMenu(Scanner console) {
        var catalogs = service.getAllCatalogs();
        int choice;
        do {
            System.out.println("\n\t\tCATALOGS");
            for (int i = 0; i < catalogs.size(); i++) {
                System.out.printf("%-5d %-25s%n", i + 1, catalogs.get(i).getName());
            }
            System.out.println("0. Назад");
            System.out.print("Выберите каталог: ");

            try {
                choice = console.nextInt();
                if (choice > 0 && choice <= catalogs.size()) {
                    displayProductsMenu(choice - 1, console);
                } else if (choice != 0) {
                    System.out.println("Некорректный выбор.");
                }
            } catch (Exception e) {
                pauseForInput(console);
                choice = -1;
            }
        } while (choice != 0);
    }

    private void displayProductsMenu(int catalogIndex, Scanner console) {
        List<Product> products = service.getProductsByCatalog(catalogIndex);
        int choice = -1;
        do {
            System.out.println("\n\t" + service.getCatalogByIndex(catalogIndex).getName());
            for (int i = 0; i < products.size(); i++) {
                System.out.printf("%-5d %-50s%n", i + 1, products.get(i).toShortString());
            } if(userService.isAdmin()) {
                System.out.println("a. Добавить новый товар");
            }
            System.out.println("f. Фильтровать товары");
            System.out.println("0. Назад");
            System.out.print("Выберите товар: ");

            String input = console.next();
            if (input.equalsIgnoreCase("a")) {
                if(userService.isAdmin()) {
                    createProduct(catalogIndex, console);
                    products = service.getProductsByCatalog(catalogIndex); // обновляем список
                } else {
                    System.out.println("Только администратор может добавлять товары!");
                }
            } else if (input.equalsIgnoreCase("f")) {
                products = filterProductsMenu(catalogIndex, console);
            } else {
                try {
                    choice = Integer.parseInt(input);
                    if (choice > 0 && choice <= products.size()) {
                        displayProductMenu(products.get(choice - 1), console);
                    } else if (choice != 0) {
                        System.out.println("Некорректный выбор.");
                    }
                } catch (Exception e) {
                    pauseForInput(console);
                    choice = -1;
                }
            }
        } while (choice != 0);
    }

    private void createProduct(int catalogIndex, Scanner console) {
        console.nextLine(); // очистка буфера
        System.out.print("Название товара: ");
        String name = console.nextLine();
        System.out.print("Цена товара: ");
        double price = console.nextDouble();
        console.nextLine();
        System.out.print("Описание товара: ");
        String description = console.nextLine();
        System.out.print("Бренд: ");
        String brand = console.nextLine();
        System.out.print("Категория: ");
        String category = console.nextLine();

        if(!userService.isAdmin()){
            System.out.println("Только администратор может добавлять товары!");
            return;
        }

        boolean ok = service.createProduct(new Product(name, price, description, brand, category), catalogIndex);
        System.out.println("Товар успешно добавлен.");

        if(ok){
            System.out.println("товар успешно добавлен.");
        } else{
            System.out.println("Ошибка при добавлении товара.");
        }
    }




    private void displayProductMenu(Product product, Scanner console) {
        int choice;
        do {
            System.out.println("\n" + product);
            if(userService.isAdmin()) {
                System.out.println("1. Удалить");
                System.out.println("2. Изменить");
            }
            System.out.println("0. Назад");
            System.out.print("Ваш выбор: ");
            try {
                choice = console.nextInt();
                switch (choice) {
                    case 1 -> {
                        if(!userService.isAdmin()){
                            System.out.println("Только администратор может удалять товары!");
                            break;
                        }
                        boolean deleted = service.deleteProduct(product);
                        if (deleted) {
                            System.out.println("Товар удалён.");
                        } else {
                            System.out.println("Ошибка при удалении.");
                        }
                        return;
                    }
                    case 2 -> {
                        if (!userService.isAdmin()) {
                            System.out.println("Только администратор может изменять товары!");
                            break;
                        }
                        editProduct(product, console);
                    }
                    case 0 -> { return; }
                    default -> System.out.println("Некорректный выбор.");
                }
            } catch (Exception e) {
                pauseForInput(console);
                choice = -1;
            }
        } while (choice != 0);
    }

    private void editProduct(Product product, Scanner console) {
        Product temp = new Product(product.getName(), product.getPrice(), product.getDescription(), product.getBrand(), product.getCategory());
        int choice;
        do {
            System.out.println("\n" + temp);
            System.out.println("1. Изменить название");
            System.out.println("2. Изменить цену");
            System.out.println("3. Изменить описание");
            System.out.println("4. Изменить бренд");
            System.out.println("5. Изменить категорию");
            System.out.println("6. Сохранить");
            System.out.println("0. Отмена");
            System.out.print("Ваш выбор: ");

            try {
                choice = console.nextInt();
                console.nextLine(); // очистка буфера
                switch (choice) {
                    case 1 -> {
                        System.out.print("Новое название: ");
                        temp.setName(console.nextLine());
                    }
                    case 2 -> {
                        System.out.print("Новая цена: ");
                        temp.setPrice(console.nextDouble());
                        console.nextLine();
                    }
                    case 3 -> {
                        System.out.print("Новое описание: ");
                        temp.setDescription(console.nextLine());
                    }
                    case 4 -> {
                        System.out.print("Новый бренд: ");
                        temp.setBrand(console.nextLine());
                    }
                    case 5 -> {
                        System.out.print("Новая категория: ");
                        temp.setCategory(console.nextLine());
                    }
                    case 6 -> {
                        boolean edited = service.updateProduct(product, temp);
                        if (edited){
                            System.out.println("Изменения сохранены.");
                        } else {
                            System.out.println("Ошибка при сохранении изменений.");
                        }
                        return;
                    }
                    case 0 -> System.out.println("Изменения отменены.");
                    default -> System.out.println("Некорректный выбор.");
                }
            } catch (Exception e) {
                pauseForInput(console);
                choice = -1;
            }
        } while (choice != 0);
    }


    private void pauseForInput(Scanner console) {
        System.out.println("Нажмите Enter для продолжения...");
        console.nextLine();
    }

    private List<Product> filterProductsMenu(int catalogIndex, Scanner console) {
        console.nextLine();
        System.out.println("\nФильтры:");
        System.out.println("1. По имени");
        System.out.println("2. По диапазону цены");
        System.out.println("3. По бренду");
        System.out.println("4. По категории");
        System.out.println("0. Назад");
        System.out.print("Выберите фильтр: ");
        int choice = console.nextInt();
        console.nextLine();

        List<Product> filtered = service.getProductsByCatalog(catalogIndex);

        switch (choice) {
            case 1 -> {
                System.out.print("Введите часть названия: ");
                String name = console.nextLine();
                filtered = service.filterProductsByName(catalogIndex, name);
            }
            case 2 -> {
                System.out.print("Минимальная цена: ");
                double min = console.nextDouble();
                System.out.print("Максимальная цена: ");
                double max = console.nextDouble();
                console.nextLine();
                filtered = service.filterProductsByPriceRange(catalogIndex, min, max);
            }
            case 3 -> {
                System.out.print("Бренд: ");
                String brand = console.nextLine();
                filtered = service.filterProductsByBrand(catalogIndex, brand);
            }
            case 4 -> {
                System.out.print("Категория: ");
                String category = console.nextLine();
                filtered = service.filterProductsByCategory(catalogIndex, category); // метод добавляем в сервис
            }
            case 0 -> System.out.println("Возврат к списку товаров.");
        }

        System.out.println("\nОтфильтрованные товары:");
        for (int i = 0; i < filtered.size(); i++) {
            System.out.printf("%-5d %-25s%n", i + 1, filtered.get(i).getName());
        }
        return filtered;
    }

    private void displayAuditLog(){
        List<AuditEntry> log = UnitOfWork.getInstance().getAuditLog();
        if(log.isEmpty()){
            System.out.println("Журнал аудита пуст.");
            return;
        }
        System.out.println("\n ==== Журнал аудита ====");
        for (AuditEntry entry : log){
            System.out.println(entry);
        }
        System.out.println("==== Конец журнала ====");
    }
}
