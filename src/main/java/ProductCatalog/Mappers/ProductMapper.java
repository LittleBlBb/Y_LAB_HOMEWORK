package ProductCatalog.Mappers;

import ProductCatalog.DTO.ProductDTO;
import ProductCatalog.Models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "default")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductDTO toDTO(Product entity);
    Product toEntity(ProductDTO dto);
}
