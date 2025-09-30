package wood.mike.maydenshopping.integration.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification
import wood.mike.maydenshopping.MaydenShoppingApplication
import wood.mike.maydenshopping.dto.ShoppingListDTO
import wood.mike.maydenshopping.model.ShoppingList
import wood.mike.maydenshopping.repository.AppUserRepository
import wood.mike.maydenshopping.repository.ShoppingListRepository

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Transactional
@ContextConfiguration(classes = MaydenShoppingApplication.class)
@WebAppConfiguration
class ShoppingListItemControllerIntegrationTest extends Specification implements ShoppingListTestSetup{

    static final String USERNAME_SUE = 'sue'     // sue is added by flyway
    static final Integer DEFAULT_NUMBER_OF_ITEMS = 5

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ShoppingListRepository shoppingListRepository

    @Autowired
    private AppUserRepository appUserRepository

    MockMvc mvc

    def setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build()
    }

    @WithUserDetails(value = USERNAME_SUE)
    def "addItemFragment adds an item to the shopping list"() {
        given:
            Integer listId = addTestData().id
        when:
            def result = mvc.perform (
                    post("/shoppingLists/${listId}/items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content('{"itemName": "Milk", "price": 150}')
            )
        then:
            result.andExpect(status().isOk())
        and:
            result.andExpect(model().attributeExists('list'))
        and:
            def modelMap = result.andReturn().getModelAndView().getModel()
            ShoppingListDTO listDto = modelMap.get('list')

            listDto != null
            listDto.id() == listId
            listDto.items().size() == DEFAULT_NUMBER_OF_ITEMS + 1
            listDto.items()*.itemPrice().sum() == DEFAULT_NUMBER_OF_ITEMS * 100 + 150
    }

    @WithUserDetails(value = USERNAME_SUE)
    def "toggleItemFragment updates the purchased field"() {
        given:
            ShoppingList shoppingList = addTestData()
            Integer listId = shoppingList.id
            Integer itemId = shoppingList.items.getFirst().id
        when:
            def result = mvc.perform (
                    post("/shoppingLists/${listId}/items/${itemId}/toggle")
            )
        then:
            result.andExpect(status().isOk())
        and:
            result.andExpect(model().attributeExists('list'))
        and:
            def modelMap = result.andReturn().getModelAndView().getModel()
            ShoppingListDTO listDto = modelMap.get('list')

            listDto != null
            listDto.id() == listId
            listDto.items().size() == DEFAULT_NUMBER_OF_ITEMS
            listDto.items().getFirst().purchased()
            listDto.items().tail().every { it.purchased() == false }
    }

    @WithUserDetails(value = USERNAME_SUE)
    def "removeItemFragment removes an item from the list"() {
        given:
            ShoppingList shoppingList = addTestData()
            Integer listId = shoppingList.id
            Integer itemId = shoppingList.items.getFirst().id
        when:
            def result = mvc.perform (
                    post("/shoppingLists/${listId}/items/${itemId}/remove")
            )
        then:
            result.andExpect(status().isOk())
        and:
            result.andExpect(model().attributeExists('list'))
        and:
            def modelMap = result.andReturn().getModelAndView().getModel()
            ShoppingListDTO listDto = modelMap.get('list')

            listDto != null
            listDto.id() == listId
            listDto.items().size() == DEFAULT_NUMBER_OF_ITEMS - 1
            !listDto.items().find{it.id() == itemId }
    }

    @WithUserDetails(value = USERNAME_SUE)
    def "moveItemFragment moves an item in the list"() {
        given:
            ShoppingList shoppingList = addTestData()
            Integer listId = shoppingList.id
            Integer itemId = shoppingList.items.getFirst().id
        when:
            def result = mvc.perform (
                    post("/shoppingLists/${listId}/items/${itemId}/move?direction=down")
            )
        then:
            result.andExpect(status().isOk())
        and:
            result.andExpect(model().attributeExists('list'))
        and:
            def modelMap = result.andReturn().getModelAndView().getModel()
            ShoppingListDTO listDto = modelMap.get('list')

            listDto != null
            listDto.id() == listId
            listDto.items().size() == DEFAULT_NUMBER_OF_ITEMS
            listDto.items().get(0).itemName() == 'item-1'   // moved up
            listDto.items().get(1).itemName() == 'item-0'   // moved down
            listDto.items().get(2).itemName() == 'item-2'
            listDto.items().get(3).itemName() == 'item-3'
            listDto.items().get(4).itemName() == 'item-4'
    }
}
