package wood.mike.maydenshopping.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ShoppingListDTO(
        Integer id,
        LocalDateTime createdAt,
        List<ShoppingListItemDTO> items
) {}

