### Mayden shopping list app

A spring boot shopping list app backed by a MySql database.\
Bring it all up with\

`./startup.sh`

This will build the app with Gradle and bring it up with a MySQL database using docker.

Then navigate to:\
`http://localhost:9051/shoppingLists`

And log in with either user `jon` or `sue` who both have the password `m4yD3N`

### Testing
Unit and integration tests are handled by spock with groovy.\
Run all tests with:\
`./gradlew build`

### Improvements

* No item quantity recorded on items
* Reinstate this constraint, removed due to hibernate caching issues when swapping items: `CONSTRAINT uq_item_per_list UNIQUE (shopping_list_id, item_idx)`