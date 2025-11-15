package ProductCatalog.Services;

import ProductCatalog.Models.AuditEntry;
import ProductCatalog.Repositories.AuditRepository;

import java.util.List;

/**
 * Сервис для управления журналом аудита.
 * Отвечает за запись логов действий пользователей.
 */
public class AuditService {
    private final AuditRepository auditRepository;

    /**
     * Создает экземпляр {@code AuditService}.
     *
     * @param auditRepository объект, управляющий журналом аудита из БД
     */
    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    /**
     * Добавляет новую запись в журнал аудита.
     *
     * @param username имя пользователя, выполнившего действие
     * @param action   название действия
     * @param details  дополнительные детали действия
     */
    public void log(String username, String action, String details) {
        auditRepository.save(new AuditEntry(username, action, details));
    }

    public List<AuditEntry> getAll() {
        return auditRepository.findAll();
    }
}