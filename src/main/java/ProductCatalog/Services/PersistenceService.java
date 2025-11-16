package ProductCatalog.Services;

import ProductCatalog.Config.AppConfig;
import ProductCatalog.UnitOfWork;
import lombok.Getter;

import java.io.*;

/**
 * Сервис для сохранения и загрузки состояния приложения.
 * Реализует шаблон Singleton.
 */
public class PersistenceService {
    private static PersistenceService instance;
    @Getter
    private UnitOfWork unitOfWork;
    private static final String DATA_FILE = AppConfig.getDataFile();

    /**
     * Приватный конструктор. Загружает данные из файла
     * или создаёт новый {@link UnitOfWork}, если данные отсутствуют.
     */
    private PersistenceService() {
        this.unitOfWork = loadData();
        if (unitOfWork == null) {
            unitOfWork = new UnitOfWork();
        }
    }

    /**
     * Возвращает единственный экземпляр {@code PersistenceService}.
     *
     * @return экземпляр {@code PersistenceService}
     */
    public static PersistenceService getInstance() {
        if (instance == null) {
            instance = new PersistenceService();
        }
        return instance;
    }

    /**
     * Сохраняет текущее состояние данных в файл.
     */
    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(unitOfWork);
            System.out.println("Данные успешно сохранены.");
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении данных: " + e.getMessage());
        }
    }

    /**
     * Загружает данные из файла, если он существует.
     *
     * @return объект {@link UnitOfWork} или {@code null}, если загрузка не удалась
     */
    private UnitOfWork loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (UnitOfWork) ois.readObject();
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке данных: " + e.getMessage());
            return null;
        }
    }
}
