package wood.mike.maydenshopping.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wood.mike.maydenshopping.dto.ShoppingListDTO;
import wood.mike.maydenshopping.mapper.ShoppingListMapper;
import wood.mike.maydenshopping.model.AppUser;
import wood.mike.maydenshopping.model.ShoppingList;
import wood.mike.maydenshopping.repository.ShoppingListRepository;

import java.util.List;
import java.util.Optional;
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

    public Optional<ShoppingListDTO> getListForUserById(AppUser user, long id) {
        return shoppingListRepository.findByIdAndUser(id, user)
                .stream()
                .map(mapper::toDTO)
                .findFirst();
    }

    public ShoppingList createList(AppUser user) {
        ShoppingList list = new ShoppingList();
        list.setUser(user);
        return shoppingListRepository.save(list);
    }

    public void deleteList(AppUser user, long id) {
        ShoppingList list = shoppingListRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("List not found or not yours"));
        shoppingListRepository.delete(list);
    }
}
