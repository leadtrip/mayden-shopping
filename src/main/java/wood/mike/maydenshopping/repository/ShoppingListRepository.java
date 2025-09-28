package wood.mike.maydenshopping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wood.mike.maydenshopping.model.AppUser;
import wood.mike.maydenshopping.model.ShoppingList;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, Integer> {
    List<ShoppingList> findByUser(AppUser user);

    Optional<ShoppingList> findByIdAndUser(Integer id, AppUser user);
}
