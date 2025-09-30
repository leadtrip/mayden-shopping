package wood.mike.maydenshopping.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wood.mike.maydenshopping.dto.ShoppingListDTO;
import wood.mike.maydenshopping.mapper.ShoppingListMapper;
import wood.mike.maydenshopping.model.AppUser;
import wood.mike.maydenshopping.model.ShoppingList;
import wood.mike.maydenshopping.model.ShoppingListItem;
import wood.mike.maydenshopping.repository.ShoppingListItemRepository;
import wood.mike.maydenshopping.repository.ShoppingListRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListItemRepository itemRepository;
    private final ShoppingListMapper mapper;

    public ShoppingListService(ShoppingListRepository shoppingListRepository,
                               ShoppingListItemRepository itemRepository,
                               ShoppingListMapper mapper) {
        this.shoppingListRepository = shoppingListRepository;
        this.itemRepository = itemRepository;
        this.mapper = mapper;
    }

    public List<ShoppingListDTO> getListsForUser(AppUser user) {
        List<ShoppingList> lists = shoppingListRepository.findByUser(user);
        return lists.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<ShoppingListDTO> getListForUserById(AppUser user, int id) {
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


    public void addItemToList(AppUser user, int listId, String name, int price) {
        ShoppingList list = shoppingListRepository.findByIdAndUser(listId, user)
                .orElseThrow(() -> new IllegalStateException("List not found"));
        int idx = list.getItems().size() + 1;
        ShoppingListItem item = new ShoppingListItem();
        item.setItemName(name);
        item.setItemPrice(price);
        item.setItemIdx(idx);
        item.setPurchased(false);
        item.setShoppingList(list);
        list.getItems().add(item);
        shoppingListRepository.save(list);
    }

    public void togglePurchased(AppUser user, Integer itemId) {
        ShoppingListItem item = itemRepository.findByIdAndShoppingListUserId(itemId, user.getId())
                .orElseThrow(() -> new IllegalStateException("Item not found"));
        item.setPurchased(!item.getPurchased());
        itemRepository.save(item);
    }

    public void removeItem(AppUser user, Integer itemId) {
        ShoppingListItem item = itemRepository.findByIdAndShoppingListUserId(itemId, user.getId())
                .orElseThrow(() -> new IllegalStateException("Item not found"));

        ShoppingList list = item.getShoppingList();

        list.getItems().remove(item);

        itemRepository.delete(item);

        List<ShoppingListItem> items = list.getItems().stream()
                .sorted(Comparator.comparingInt(ShoppingListItem::getItemIdx))
                .toList();

        for (int i = 0; i < items.size(); i++) items.get(i).setItemIdx(i + 1);

        shoppingListRepository.save(list);
    }

    @Transactional
    public ShoppingListDTO moveItem(AppUser user, Integer listId, Integer itemId, String direction) {
        ShoppingList list = shoppingListRepository.findByIdAndUser(listId, user)
                .orElseThrow(() -> new IllegalStateException("List not found"));

        List<ShoppingListItem> items = list.getItems().stream()
                .sorted(Comparator.comparingInt(ShoppingListItem::getItemIdx))
                .toList();

        int index = IntStream.range(0, items.size())
                .filter(i -> items.get(i).getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Item not found"));

        int neighborIndex = switch (direction.toLowerCase()) {
            case "up" -> index - 1;
            case "down" -> index + 1;
            default -> index;
        };

        if (neighborIndex < 0 || neighborIndex >= items.size()) return mapper.toDTO(list);

        ShoppingListItem current = items.get(index);
        ShoppingListItem neighbor = items.get(neighborIndex);

        int tempIdx = current.getItemIdx();
        current.setItemIdx(neighbor.getItemIdx());
        neighbor.setItemIdx(tempIdx);

        itemRepository.saveAll(List.of(current, neighbor));

        return mapper.toDTO(list);
    }

    public ShoppingListDTO getListDto(Integer listId) {
        ShoppingList list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new IllegalStateException("List not found"));
        return mapper.toDTO(list);
    }
}
