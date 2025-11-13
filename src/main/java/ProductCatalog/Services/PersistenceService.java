package ProductCatalog.Services;

import ProductCatalog.Config.AppConfig;
import ProductCatalog.UnitOfWork;
import lombok.Getter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PersistenceService {
    private static PersistenceService instance;
    @Getter
    private UnitOfWork unitOfWork;
    private static final String DATA_FILE = AppConfig.getDataFile();

    private PersistenceService(){
        this.unitOfWork = loadData();
        if(unitOfWork == null){
            unitOfWork = new UnitOfWork();
        }
    }

    public static PersistenceService getInstance(){
        if (instance == null){
            instance = new PersistenceService();
        }
        return instance;
    }

    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(unitOfWork);
            System.out.println("Данные успешно сохранены.");
        } catch (IOException e){
            System.err.println("Ошибка при сохранении данных: " + e.getMessage());
        }
    }

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
