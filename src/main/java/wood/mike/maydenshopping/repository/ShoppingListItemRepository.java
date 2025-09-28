package wood.mike.maydenshopping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wood.mike.maydenshopping.model.ShoppingListItem;

import java.util.Optional;

@Repository
public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, Integer> {

    Optional<ShoppingListItem> findByIdAndShoppingListUserId(Integer itemId, Integer userId);

}
