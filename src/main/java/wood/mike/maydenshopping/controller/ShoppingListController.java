package wood.mike.maydenshopping.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import wood.mike.maydenshopping.model.AppUser;
import wood.mike.maydenshopping.repository.AppUserRepository;
import wood.mike.maydenshopping.service.ShoppingListService;

@Slf4j
@Controller
public class ShoppingListController {

    private final ShoppingListService shoppingListService;
    private final AppUserRepository appUserRepository;

    public ShoppingListController(ShoppingListService shoppingListService, 
                                  AppUserRepository appUserRepository) {
        this.shoppingListService = shoppingListService;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping("/shoppingLists")
    public String shoppingLists(Model model, @AuthenticationPrincipal UserDetails principal) {
        AppUser user = appUserRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        model.addAttribute("pageTitle", "Shopping Lists");
        model.addAttribute("templateName", "shoppingLists");
        model.addAttribute("shoppingLists", shoppingListService.getListsForUser(user));
        return "layout";
    }
}
