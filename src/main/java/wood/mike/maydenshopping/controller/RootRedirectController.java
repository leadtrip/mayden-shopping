package wood.mike.maydenshopping.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootRedirectController {

    @GetMapping({"/", "/shoppingLists/"})
    public String redirectToShoppingLists() {
        return "redirect:/shoppingLists";
    }
}
