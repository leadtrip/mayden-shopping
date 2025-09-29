package wood.mike.maydenshopping.unit.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Specification
import spock.lang.Subject
import wood.mike.maydenshopping.dto.ShoppingListDTO
import wood.mike.maydenshopping.mapper.ShoppingListMapper
import wood.mike.maydenshopping.model.AppUser
import wood.mike.maydenshopping.model.ShoppingList
import wood.mike.maydenshopping.model.ShoppingListItem
import wood.mike.maydenshopping.repository.ShoppingListItemRepository
import wood.mike.maydenshopping.repository.ShoppingListRepository
import wood.mike.maydenshopping.service.ShoppingListService

import java.time.LocalDateTime

@DataJpaTest
class ShoppingListServiceSpec extends Specification {

    ShoppingListRepository shoppingListRepository = Mock(ShoppingListRepository)

    ShoppingListItemRepository itemRepository = Mock(ShoppingListItemRepository)

    ShoppingListMapper shoppingListMapper = new ShoppingListMapper()

    @Subject
    ShoppingListService shoppingListService

    def setup() {
        shoppingListService = new ShoppingListService(shoppingListRepository, itemRepository, shoppingListMapper)
    }

    def "getListsForUser returns list of DTOs"() {
        given:
            AppUser user = standardUser()
        when:
            def lists = shoppingListService.getListsForUser(user)
        then:
            1 * shoppingListRepository.findByUser(user) >> List.of(createList(user))
            lists.size() == 1
    }

    def "getListForUserById returns DTO"() {
        given:
            AppUser user = standardUser()
            Integer listId = 1
        when:
            def optionalList = shoppingListService.getListForUserById(user, listId)
        then:
            1 * shoppingListRepository.findByIdAndUser(listId, user) >> Optional.of(createList(user))
            optionalList.isPresent()
    }

    def "createList saves to repo"() {
        when:
            shoppingListService.createList(standardUser())
        then:
            1 * shoppingListRepository.save(_)
    }

    def "addItemToList saves to repo"() {
        given:
            int listId = 1
            String name = 'eggs'
            int price = 200
            AppUser user = standardUser()
        when:
            shoppingListService.addItemToList(user, listId, name, price)
        then:
            1 * shoppingListRepository.findByIdAndUser(listId, user) >> Optional.of(createList(user))
            1 * shoppingListRepository.save(_)
    }

    def "togglePurchased updates the purchased property"() {
        given:
            AppUser user = standardUser()
            int listId = 1
            ShoppingListItem item = new ShoppingListItem(itemName: "eggs", itemIdx: 1, itemPrice: 200)  // purchased defaults to false
        when:
            shoppingListService.togglePurchased(user, listId)
        then:
            1 * itemRepository.findByIdAndShoppingListUserId(listId, user.id) >> Optional.of(item)
            item.purchased
    }

    def "removeItem removes item and reindex items"() {
        given:
            AppUser user = standardUser()
            ShoppingList list = createList(user, 10)
            ShoppingListItem item = list.getItems().get(5)
        when:
            shoppingListService.removeItem(user, item.id)
        then:
            1 * itemRepository.findByIdAndShoppingListUserId(item.id, user.id) >> Optional.of(item)
            1 * itemRepository.delete(item)
            1 * shoppingListRepository.save(list)
    }

    def "moveItem will swap items index"() {
        given:
            AppUser user = standardUser()
            ShoppingList list = createList(user, 10)
            ShoppingListItem item = list.getItems().get(5)
        when:
            int itemIdx = item.itemIdx
            String itemName = item.itemName
        then:
            itemIdx == 5
            itemName == 'item-5'
        when:
            ShoppingListDTO updatedList = shoppingListService.moveItem(user, list.id, item.id, 'up')
        then:
            1 * shoppingListRepository.findByIdAndUser(list.id, user) >> Optional.of(list)
            1 * itemRepository.saveAll(_)
            updatedList.items().size() == 10
            updatedList.items().get(4).itemName() == 'item-5'
            updatedList.items().get(5).itemName() == 'item-4'
    }

    def "getListDto converts entity to DTO"() {
        given:
            int numberOfItems = 10
            AppUser user = standardUser()
            ShoppingList list = createList(user, numberOfItems)
        when:
            ShoppingListDTO dto = shoppingListService.getListDto(list.id)
        then:
            1 * shoppingListRepository.findById(list.id) >> Optional.of(list)
            dto.items().size() == numberOfItems
            dto.items().size() == list.items.size()
    }

    AppUser standardUser() {
        return new AppUser(id: 1, username: "bob", password: "password", enabled: true)
    }

    ShoppingList createList(AppUser user, int numberOfItems = 0) {
        ShoppingList list = new ShoppingList(id: 1, createdAt: LocalDateTime.now(), user: user, items: [])
        numberOfItems.times {
            list.getItems().add(new ShoppingListItem(id: it, itemName: "item-${it}", itemIdx: it, itemPrice: 100, shoppingList: list))
        }
        return list
    }
}

