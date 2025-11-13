package ProductCatalog.Services;

import ProductCatalog.Models.AuditEntry;
import ProductCatalog.UnitOfWork;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для управления журналом аудита.
 * Отвечает за запись, получение и очистку логов действий пользователей.
 */
public class AuditService implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UnitOfWork unitOfWork;

    /**
     * Создает экземпляр {@code AuditService}.
     *
     * @param unitOfWork объект, управляющий данными приложения
     */
    public AuditService(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    /**
     * Добавляет новую запись в журнал аудита.
     *
     * @param username имя пользователя, выполнившего действие
     * @param action   название действия
     * @param details  дополнительные детали действия
     */
    public void logAction(String username, String action, String details) {
        unitOfWork.getAuditLog().add(new AuditEntry(username, action, details));
    }

    /**
     * Возвращает список всех записей аудита.
     *
     * @return список записей {@link AuditEntry}
     */
    public List<AuditEntry> getAuditLog() {
        return new ArrayList<>(unitOfWork.getAuditLog());
    }

    /**
     * Очищает журнал аудита.
     */
    public void clearLog() {
        unitOfWork.getAuditLog().clear();
    }
}
