package wood.mike.maydenshopping.mapper;

import org.springframework.stereotype.Service;
import wood.mike.maydenshopping.dto.ShoppingListDTO;
import wood.mike.maydenshopping.dto.ShoppingListItemDTO;
import wood.mike.maydenshopping.model.ShoppingList;
import wood.mike.maydenshopping.model.ShoppingListItem;

import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class ShoppingListMapper {

    public ShoppingListDTO toDTO(ShoppingList list) {
        return new ShoppingListDTO(
                list.getId(),
                list.getCreatedAt(),
                list.getItems().stream()
                        .sorted(Comparator.comparingInt(ShoppingListItem::getItemIdx))
                        .map(this::toDTO)
                        .collect(Collectors.toList())
        );
    }


    public ShoppingListItemDTO toDTO(ShoppingListItem item) {
        return new ShoppingListItemDTO(
                item.getId(),
                item.getItemName(),
                item.getItemIdx(),
                item.getCreatedAt(),
                item.getPurchased(),
                item.getItemPrice()
        );
    }
}
