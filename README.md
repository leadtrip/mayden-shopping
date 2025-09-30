### Mayden shopping list app

A spring boot shopping list app backed by a MySql database.\
Bring it all up with:\

`./startup.sh`

This will build the app with Gradle and bring it up with a MySQL database using docker.

Then navigate to:\
`http://localhost:9051/shoppingLists`

Log in with either user `jon` or `sue` who both have the password `m4yD3N`\
`jon` has an existing shopping list on boot with a couple of items added

### Testing
Unit and integration tests are handled by spock with groovy.\
Run all tests with:\
`./gradlew build`

### Improvements

* Add a way to specify the quantity required for a given item
* Reinstate this constraint, removed due to hibernate caching issues when swapping items: `CONSTRAINT uq_item_per_list UNIQUE (shopping_list_id, item_idx)`
* Better handling of price/currency input
* Add UI tests e.g. selenium
* Add ability to show/hide items that have been crossed off/purchased
* Add spending limit and email sharing functions from spec
