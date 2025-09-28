package wood.mike.maydenshopping.dto;

import java.time.LocalDateTime;

public record ShoppingListItemDTO(
        Integer id,
        String itemName,
        Integer itemIdx,
        LocalDateTime createdAt,
        Boolean purchased,
        Integer itemPrice
) {}

