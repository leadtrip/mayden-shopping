package wood.mike.maydenshopping.integration.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import wood.mike.maydenshopping.controller.ShoppingListController
import wood.mike.maydenshopping.model.AppUser
import wood.mike.maydenshopping.repository.AppUserRepository
import wood.mike.maydenshopping.service.ShoppingListService

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = ShoppingListController)
@AutoConfigureMockMvc
class ShoppingListControllerIntegrationSpec extends Specification {

    @Autowired
    MockMvc mvc

    @MockBean
    ShoppingListService shoppingListService

    @MockBean
    AppUserRepository appUserRepository

    def "shoppingLists returns 200"() {
        given:
        def testUser = new AppUser(id: 1, username: "bob", password: "pw", enabled: true)
        appUserRepository.findByUsername("bob") >> Optional.of(testUser)
        shoppingListService.getListsForUser(testUser) >> []

        when:
        def result = mvc.perform(get("/shoppingLists").with(user("bob").roles("USER")))

        then:
        result.andExpect(status().isOk())
    }
}

