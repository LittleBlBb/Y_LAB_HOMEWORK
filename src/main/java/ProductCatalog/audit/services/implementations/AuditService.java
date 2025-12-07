package ProductCatalog.audit.services.implementations;

import ProductCatalog.models.AuditEntry;
import ProductCatalog.repositories.interfaces.IAuditRepository;
import ProductCatalog.services.interfaces.IAuditService;

import java.util.List;

/**
 * Сервис для управления журналом аудита.
 * Отвечает за запись логов действий пользователей.
 */
public class AuditService implements IAuditService {
    private final IAuditRepository auditRepository;

    /**
     * Создает экземпляр {@code AuditService}.
     *
     * @param auditRepository объект, управляющий журналом аудита из БД
     */
    public AuditService(IAuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    /**
     * Добавляет новую запись в журнал аудита.
     *
     * @param username имя пользователя, выполнившего действие
     * @param action   название действия
     * @param details  дополнительные детали действия
     */
    public void save(String username, String action, String details) {
        auditRepository.save(new AuditEntry(username, action, details));
    }

    public List<AuditEntry> getAll() {
        return auditRepository.findAll();
    }
}