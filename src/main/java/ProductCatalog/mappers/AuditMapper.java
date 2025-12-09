package ProductCatalog.mappers;

import ProductCatalog.dto.AuditEntryDTO;
import ProductCatalog.models.AuditEntry;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "default")
public interface AuditMapper {
    AuditMapper INSTANCE = Mappers.getMapper(AuditMapper.class);

    AuditEntryDTO toDTO(AuditEntry entity);

    AuditEntry toEntity(AuditEntryDTO dto);
}
