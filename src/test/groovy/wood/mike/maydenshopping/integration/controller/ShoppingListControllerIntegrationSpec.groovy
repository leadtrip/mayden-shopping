package wood.mike.maydenshopping.integration.controller

import org.springframework.beans.factory.annotation.Autowired
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

import static org.hamcrest.Matchers.hasSize
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Transactional
@ContextConfiguration(classes = MaydenShoppingApplication.class)
@WebAppConfiguration
class ShoppingListControllerIntegrationSpec extends Specification implements ShoppingListTestSetup{

    static final String USERNAME_SUE = 'sue'     // sue is added by flyway
    static final Integer DEFAULT_NUMBER_OF_ITEMS = 5

    @Autowired
    private WebApplicationContext context;

    MockMvc mvc

    def setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build()
    }

    @WithUserDetails(value = USERNAME_SUE)
    def "shoppingLists renders the list of shopping lists"() {
        given:
            addTestData()
        when:
            def result = mvc.perform (
                get("/shoppingLists")
            )
        then:
            result.andExpect(status().isOk())
        and:
            result.andExpect(model().attributeExists('pageTitle', 'templateName', 'shoppingLists'))
        and:
            result.andExpect(model().attribute('shoppingLists', hasSize(1)))
    }

    @WithUserDetails(value = USERNAME_SUE)
    def "shoppingLists{ID} renders the selected shopping list"() {
        given:
            Integer listId = addTestData().id
        when:
            def result = mvc.perform (
                    get("/shoppingLists/${listId}")
            )
        then:
            result.andExpect(status().isOk())
        and:
            result.andExpect(model().attributeExists('pageTitle', 'templateName', 'list'))
        and:
            def modelMap = result.andReturn().getModelAndView().getModel()
            ShoppingListDTO listDto = modelMap.get('list')

            listDto != null
            listDto.id() == listId
            listDto.items().size() == DEFAULT_NUMBER_OF_ITEMS
    }

    @WithUserDetails(value = USERNAME_SUE)
    def "shoppingLists/create creates a new shopping list"() {
        given:
            long listCountBeforeCreate = listCountForUser()
        when:
            def result = mvc.perform(
                    post("/shoppingLists/create")
            )
        then:
           result.andExpect(status().is3xxRedirection())
        and:
            listCountForUser() == listCountBeforeCreate+1
    }

    long listCountForUser(String username = USERNAME_SUE) {
        shoppingListRepository.findAll()
                .stream()
                .filter(shoppingList -> shoppingList.getUser().getUsername() == username)
                .count()
    }

}
