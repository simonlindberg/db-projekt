Ingridients(Ingredient, Quantity, Unit)
Ingredient → Quantity, Unit

IngredientDelivery(DeliveryID, Ingredient, Date, Quantity)
DeliveryID -> Ingredient, Date, Quantity

Recipes(ProductName)

RecipieIngredients(ProductName, Ingredient, Quantity)
ProductName, Ingredient → Quantity

Pallets(PalletID, ProductName, Blocked, ProductionDate)
PalletID → ProductName, Blocked, ProductionDate

Storage(PalletID)

Orders(OrderID, Customer)
OrderID → Customer

PartialOrders(OrderID, ProductName, Quantity)
OrderID, ProductName → Quantity

PalletDelivery(PalletID, OrderID, DeliveryDate)
PalletID → OrderID, DeliveryDate

Customers(Customer, Address)
