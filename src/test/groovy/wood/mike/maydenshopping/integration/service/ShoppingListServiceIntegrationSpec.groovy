package wood.mike.maydenshopping.integration.service

import wood.mike.maydenshopping.MaydenShoppingApplication
import wood.mike.maydenshopping.dto.ShoppingListDTO
import wood.mike.maydenshopping.model.AppUser
import wood.mike.maydenshopping.model.ShoppingList
import wood.mike.maydenshopping.repository.AppUserRepository
import wood.mike.maydenshopping.repository.ShoppingListRepository
import wood.mike.maydenshopping.repository.ShoppingListItemRepository
import wood.mike.maydenshopping.service.ShoppingListService
import wood.mike.maydenshopping.mapper.ShoppingListMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional // <-- Essential
import spock.lang.Specification
import spock.lang.Shared


@SpringBootTest
@ContextConfiguration(classes = [MaydenShoppingApplication])
@Transactional
class ShoppingListServiceIntegrationSpec extends Specification {

    @Autowired
    ShoppingListService shoppingListService

    @Autowired
    ShoppingListRepository shoppingListRepository

    @Autowired
    ShoppingListItemRepository itemRepository

    @Autowired
    ShoppingListMapper shoppingListMapper

    @Autowired
    AppUserRepository appUserRepository

    @Shared
    AppUser testUser

    def setup() {
        testUser = appUserRepository.save(new AppUser(username: "bob", password: "password", enabled: true))
    }

    def "getListsForUser returns DTOs for a user when lists exist in DB"() {
        given: "A user and a shopping list saved to the database"
            def shoppingList1 = new ShoppingList(user: testUser, items: [])
            def shoppingList2 = new ShoppingList(user: testUser, items: [])

            shoppingListRepository.saveAll([shoppingList1, shoppingList2])

        when: "The service method is called with the user"
            def lists = shoppingListService.getListsForUser(testUser)

        then: "The service returns the correct number of DTOs, built by the real mapper"
            lists.size() == 2
            lists.each { it instanceof ShoppingListDTO }
            lists.any { it.id() == shoppingList1.id }
            lists.any { it.id() == shoppingList2.id }
    }
}