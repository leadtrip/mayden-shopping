package wood.mike.maydenshopping.integration.controller


import wood.mike.maydenshopping.model.AppUser
import wood.mike.maydenshopping.model.ShoppingList
import wood.mike.maydenshopping.model.ShoppingListItem
import wood.mike.maydenshopping.repository.AppUserRepository
import wood.mike.maydenshopping.repository.ShoppingListRepository
import org.springframework.beans.factory.annotation.Autowired

trait ShoppingListTestSetup {

    static final String USERNAME_SUE = 'sue'     // sue is added by flyway
    static final Integer DEFAULT_NUMBER_OF_ITEMS = 5

    @Autowired AppUserRepository appUserRepository
    @Autowired ShoppingListRepository shoppingListRepository

    ShoppingList addTestData(int numberOfItems = DEFAULT_NUMBER_OF_ITEMS) {
        AppUser bob = appUserRepository.findByUsername(USERNAME_SUE).get()
        ShoppingList shoppingList = createList(bob, numberOfItems)
        shoppingListRepository.save(shoppingList)
    }

    ShoppingList createList(AppUser user, int numberOfItems = 0) {
        ShoppingList list = new ShoppingList(user: user, items: [])
        numberOfItems.times {
            int idx = it+1
            list.getItems().add(new ShoppingListItem( itemName: "item-${idx}", itemIdx: idx, itemPrice: 100, shoppingList: list))
        }
        list
    }
}