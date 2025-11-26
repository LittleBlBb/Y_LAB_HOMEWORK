package ProductCatalog.Mappers;

import ProductCatalog.DTO.CatalogDTO;
import ProductCatalog.Models.Catalog;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "default")
public interface CatalogMapper {
    CatalogMapper INSTANCE = Mappers.getMapper(CatalogMapper.class);

    CatalogDTO toDTO(Catalog entity);

    Catalog toEntity(CatalogDTO dto);
}
