const API = "http://localhost:8080";

let page = 0;
const size = 5;

async function loadProducts() {
    const category = document.getElementById("category").value;

    const res = await fetch(`${API}/v1/products?category=${category}&pageNumber=${page}&pageSize=${size}`);
    const data = await res.json();

    const div = document.getElementById("products");
    div.innerHTML = "";

    data.content.forEach(p => {
        div.innerHTML += `
            <div class="product">
                <b>${p.brand} ${p.model}</b> - ${p.price}
                <br>
                Stock: ${p.stockQuantity}
                <br>
                ID: ${p.id}
            </div>
        `;
    });

    document.getElementById("pageInfo").innerText = `Página ${data.number + 1} de ${data.totalPages}`;
}

function nextPage() {
    page++;
    loadProducts();
}

function prevPage() {
    if (page > 0) page--;
    loadProducts();
}

async function createProduct() {
    const body = {
        brand: document.getElementById("brand").value,
        model: document.getElementById("model").value,
        price: Number(document.getElementById("price").value),
        stockQuantity: Number(document.getElementById("stock").value),
        description: document.getElementById("description").value,
        category: document.getElementById("createCategory").value
    };

    const res = await fetch(`${API}/v1/products`, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(body)
    });

    const data = await res.json();
    alert("Produto criado com ID: " + data.id);
}

async function createOrder() {
    const body = {
        customerId: document.getElementById("customerId").value,
        items: [
            {
                id: Number(document.getElementById("productId").value),
                quantity: Number(document.getElementById("quantity").value)
            }
        ]
    };

    const res = await fetch(`${API}/v1/orders`, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(body)
    });

    const data = await res.json();
    document.getElementById("orderResult").innerText = JSON.stringify(data, null, 2);
}

async function cancelOrder() {
    const id = document.getElementById("cancelOrderId").value;

    await fetch(`${API}/v1/orders/${id}/cancel`, {
        method: "POST"
    });

    alert("Pedido cancelado");
}