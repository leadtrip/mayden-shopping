package wood.mike.maydenshopping.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wood.mike.maydenshopping.dto.ShoppingListDTO;
import wood.mike.maydenshopping.mapper.ShoppingListMapper;
import wood.mike.maydenshopping.model.AppUser;
import wood.mike.maydenshopping.model.ShoppingList;
import wood.mike.maydenshopping.repository.ShoppingListRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListMapper mapper;

    public List<ShoppingListDTO> getListsForUser(AppUser user) {
        List<ShoppingList> lists = shoppingListRepository.findByUser(user);
        return lists.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }
}
