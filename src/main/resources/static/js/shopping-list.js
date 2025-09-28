async function updateItem(listId, itemId, action) {
    const res = await fetch(`/shoppingLists/${listId}/items/${itemId}/${action}`, { method: 'POST' });
    const html = await res.text();
    document.getElementById("itemsBody").outerHTML = html;
    updateTotal();
}

async function addItem(listId) {
    const name = document.getElementById("newItemName").value.trim();
    const price = document.getElementById("newItemPrice").value;
    const priceToPence = Math.round(parseFloat(price) * 100);

    if (!name || !price) return alert("Please enter both name and price.");

    const res = await fetch(`/shoppingLists/${listId}/items`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ itemName: name, price: priceToPence })
    });

    if (!res.ok) return alert("Failed to add item.");

    const html = await res.text();
    document.getElementById("itemsBody").outerHTML = html;

    document.getElementById("newItemName").value = '';
    document.getElementById("newItemPrice").value = '';

    updateTotal();
}

function updateTotal() {
    let total = 0;
    document.querySelectorAll(".item-price").forEach(td => total += parseFloat(td.textContent));
    document.getElementById("total").textContent = total.toFixed(2);
}