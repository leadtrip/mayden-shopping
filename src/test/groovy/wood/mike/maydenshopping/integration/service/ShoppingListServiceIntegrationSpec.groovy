package wood.mike.maydenshopping.integration.service

import wood.mike.maydenshopping.MaydenShoppingApplication
import wood.mike.maydenshopping.dto.ShoppingListDTO
import wood.mike.maydenshopping.model.AppUser
import wood.mike.maydenshopping.model.ShoppingList
import wood.mike.maydenshopping.model.ShoppingListItem
import wood.mike.maydenshopping.repository.AppUserRepository
import wood.mike.maydenshopping.repository.ShoppingListRepository
import wood.mike.maydenshopping.repository.ShoppingListItemRepository
import wood.mike.maydenshopping.service.ShoppingListService
import wood.mike.maydenshopping.mapper.ShoppingListMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification
import spock.lang.Shared

import java.util.stream.Collectors


@SpringBootTest
@ContextConfiguration(classes = [MaydenShoppingApplication.class])
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

    def "getListForUserById returns DTO" () {
        given:
            ShoppingList shoppingList = shoppingListRepository.save(createList(testUser))
        when:
            Optional<ShoppingListDTO> optList = shoppingListService.getListForUserById(testUser, shoppingList.id)
        then:
            optList.isPresent()
            !optList.get().items()
    }

    def "createList persists a new list"() {
        when:
            ShoppingList shoppingList = shoppingListService.createList(testUser)
        then:
            shoppingList.id
    }

    def "addItemToList adds an item to an existing list"() {
        given:
            int totalItems = 10
            ShoppingList shoppingList = shoppingListRepository.save(createList(testUser, totalItems))
        when:
            shoppingListService.addItemToList(testUser, shoppingList.id, 'beans', 80)
        then:
            shoppingListRepository.findById(shoppingList.id).get().items.size() == totalItems  + 1
    }

    def "togglePurchased toggles the purchased field"() {
        given:
            ShoppingList shoppingList = shoppingListRepository.save(createList(testUser, 10))
            int itemId = shoppingList.items.get(5).id
        when:
            shoppingListService.togglePurchased(testUser, itemId)
        then:
            ShoppingListItem updatedItem = itemRepository.findById(itemId).get()
            updatedItem.purchased
    }

    def "removeItem removes an item from a list and re-indexes remaining items"() {
        given:
            int listSize = 10
            int itemToRemoveIndex = 5

            ShoppingList shoppingList = shoppingListRepository.save(createList(testUser, listSize))

            def itemToRemove = shoppingList.getItems().stream()
                    .filter { it.itemIdx == itemToRemoveIndex }
                    .findFirst().get()
            def itemIdToRemove = itemToRemove.id

            shoppingListRepository.flush()

        when:
            shoppingListService.removeItem(testUser, itemIdToRemove)

        then:
            itemRepository.findById(itemIdToRemove).isEmpty()

            ShoppingList updatedList = shoppingListRepository.findById(shoppingList.id).get()
            updatedList.getItems().size() == listSize - 1

            updatedList.getItems().stream()
                    .filter { it.itemName == "item-6" }
                    .findFirst().get().itemIdx == itemToRemoveIndex

            def indices = updatedList.getItems().stream()
                    .map { it.itemIdx }
                    .collect(Collectors.toSet())

            indices.size() == listSize - 1
            indices.contains(1)
            indices.contains(9)
    }

    def "moveItem swaps the index of 2 neighbour items"() {
        given:
            ShoppingList shoppingList = shoppingListRepository.save(createList(testUser, 5))
        when:
            ShoppingListDTO updatedList = shoppingListService.moveItem(testUser, shoppingList.id, shoppingList.items.get(3).id, 'up')
        then:
            updatedList.items().get(0).itemName() == 'item-0'
            updatedList.items().get(1).itemName() == 'item-1'
            updatedList.items().get(2).itemName() == 'item-3'   // moved up
            updatedList.items().get(3).itemName() == 'item-2'   // moved down
            updatedList.items().get(4).itemName() == 'item-4'
    }

    ShoppingList createList(AppUser user, int numberOfItems = 0) {
        ShoppingList list = new ShoppingList(user: user, items: [])
        numberOfItems.times {
            list.getItems().add(new ShoppingListItem( itemName: "item-${it}", itemIdx: it, itemPrice: 100, shoppingList: list))
        }
        return list
    }
}