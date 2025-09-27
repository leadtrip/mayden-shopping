package wood.mike.maydenshopping.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ShoppingListController {

    @GetMapping
    public String shoppingLists(Model model) {
        model.addAttribute("pageTitle", "Shopping Lists");
        model.addAttribute("templateName", "shoppingLists");
        return "layout";
    }
}
