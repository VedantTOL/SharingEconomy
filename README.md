

<html>
 <head>
   <title>README</title>
   <style>
     body {
       font-family: Acumin Pro, sans-serif;
       font-size: 16px;
       line-height: 1.0;
       background-color: #000000;
     }
     .header {
       background-color: #000000;
       color: #CEB888;
       padding: 20px;
       text-align: center;
       font-size: 36px;
       font-weight: bold;
       letter-spacing: 2px;
     }
     .section {
       padding: 20px;
       margin: 30px 0;
       background-color: #fff;
       box-shadow: 0 2px 6px rgba(0,0,0,0.1);
     }
     h1 {
       font-size: 28px;
       color: #CEB888;
       margin: 0 0 20px 0;
       padding: 0;
     }
     h2 {
       font-size: 18px;
       color: #CEB888;
       margin: 0 0 10px 0;
       padding: 0;
     }
     p {
       margin: 0 0 10px 0;
       padding: 0;
       color: #ffffff;
     }
    h3 {
     font-size: 14px;
color: #CEB888;
font-weight: bold;
}
     ul {
       margin: 0 0 10px 20px;
       padding: 0;
     }
     li {
       margin: 0;
       padding: 0;
     }
   </style>
 </head>
 <body>
   <div class="header">README</div>
 <div class="surrounding">
     <h1>Instructions</h1>
     <p> First, the user needs to launch the Server. After the server is ran, the client needs to be ran which launches
the GUI. Once the GUI pops up, the user selects whether they are a Buyer or Seller. They have the option to exit as well. When
they click on either buyer or seller, they are prompted to either login or create a new account. When they log in, they can also edit or delete accounts. Once logged in, as a buyer, they
have the option to view by seller, which the customer can choose a particular seller to shop from. Then they are asked for their budget in a JOptionPane. They can choose items to buy. The other option, is to view the marketplace,
Where the user can view the marketplace or search. If they decide to search, they get a prompt asking them to find the product, and the
corresponding product will show up. The view marketplace shows all stores and products. They have the option to sort by things such as price, and quantity.
They can then either add items to their shopping cart, or purchase now. Purchasing now subtracts their budget and buys the products. They can also view cart, and either remove
the product or buy it. Finally, they have the option to keep shopping or logout. As for Seller, the seller is asked if they want to view stats,
add a store, or edit a store. If they choose view statisitcs, they can look by customer, which shows their customers and items bought.
edit a store, add a store, view statistics, or logout. They can also look at the stores and which was most popular. Editing stores
sllows them to select a store to edit, and then edit, prices, quantities, and names. Adding a store adds a new store, and then the
seller adds the items, prices, and quantities. They also have the option to go to the seller menu when they wish.




GitHub Repository: /VedantTOL/SharingEconomy </p>
</div>
____________________________________________________________
<div class="surrounding">
     <h1>Server</h1>
   <p>The Server class is responsible for managing the overall performance of the online marketplace. It listens to incoming
connections from clients and creates new threads to handle each client's requests. This class stores two ArrayList objects:
buyers and sellers. These lists store the Buyer and Seller items. Each Buyer or Seller item contains information
about the user, such as their username, password, and purchase/sale history. The Server class also includes methods for
adding and removing buyers and sellers from the list of buyers and sellers, as well as methods for handling various customer
requests. For example, the Server class contains a method for validating customer login information, a method for processing
a purchase request from a customer, and a method for returning statistics to the seller's store. This class overall
is the backbone of the sharing economy, and controls the flow of information and coordinates the actions of different
customers - it essentially is the processing unit of our whole project. </p>
____________________________________________________________________________________
<div class="surrounding">
     <h1>Client</h1>
   <p>The Client class in our project is responsible for handling the user interface and interaction with the server.


The Client class contains several inner, mainly static classes, each of which implements a specific panel of the user interface. These
include the LoginPanel, the RegisterPanel, the StorePanel, the ProductPanel, the CartPanel, the ViewStatisticsPanel, and
the UpdateProductPanel. Each panel is designed to display information to the user or allow the user to enter information.
The LoginPanel class is responsible for allowing users to log in to the system. It contains text fields for the user to
enter their username and password, as well as a button to submit the information to the server for verification. If the user's credentials are valid, they are redirected to the StorePanel.
The RegisterPanel class allows users to create a new account. It contains text fields for the user to enter their username,
password, and email address. Once the user enters this information and clicks the "Create Account" button, the information
is sent to the server for validation. If the user's account is successfully created, they are redirected to the StorePanel.
The StorePanel class displays all of the stores available in the system. It contains a search bar that allows the user to
search for stores by name, and a list of stores that match the search query. When a store is selected, the StorePanel redirects
the user to the ProductPanel for that store.
The ProductPanel class displays all of the products available in the selected store. It contains a search bar that allows
the user to search for products by name, and a list of products that match the search query. When a product is selected,
the user can view its details and add it to their cart.
The CartPanel class displays all of the items currently in the user's cart. It allows the user to remove items from their
cart or proceed to checkout. When the user clicks the "Checkout" button, the server is notified, and the user is redirected
to the ViewStatisticsPanel.
The ViewStatisticsPanel class displays various statistics about the user's purchases. This includes the total amount spent,
the most popular store, and the most popular products. The statistics are presented in a tabular format, making it easy
for the user to understand and analyze their purchases.
Finally, the UpdateProductPanel class allows the user to update information about a product they have previously added to
the system. This includes changing the product's name, description, price, and image. Once the user has made their changes,
they click the "Update Product" button, and the information is sent to the server.
The Client class communicates with the server through a socket connection. When the user submits information, the Client
class creates a new Socket object and sends the data to the server. The server then processes the data and responds appropriately.
The Client class receives the server's response and updates the user interface accordingly.
</p>


<div class="surrounding">
     <h1>Seller</h1>
     <p>The seller menu offers a menu that has the options to add, remove, or edit a store.
The following methods are:
<h3>Add Store</h3>
<p>If the user wants to add a store, they are prompted to add all the details of the store,
including the number of products, the store name, each price, quantity, and description. This
then adds the store to the text file containing all the stores and products.</p>
<h3>Delete Store</h3>
<p>If the user elects to delete the store, a list of stores appears that are numbered.
They then type in the number of the store that they would like to delete, and then the store
and all of it's contents are then removed. The text file is then updated not including the name
of the store and it's products that were removed by the seller.</p>
<h3>Edit Store</h3>
<p>If the user elects to edit the store, another menu pops up that lists the stores, exactly the same
way it is shown when asking the seller to delete a store. They then enter the number of the store
they would like to edit. After choosing the store to edit, they are presented with the options to
edit the store name, add products to a store, edit products, and delete products.<p></p>
<p>If the user elects to edit the store name, they are prompted to enter the new name, which is then
updated to the text containing the stores and products. <p>
<p>If they choose to add products, they are presented with a series
of questions to identify all the necessary information about the product.<p>
<p>If they elect to edit products, they are presented with a list of all the products in the store which
are numbered, and they type the number of the product they would like to edit. They are then asked to set
the new details of the product, which are updated to the stores text file. Lastly, the options to remove
products displays the list of products contained within the store. The seller then types the number of the
product, and it is removed and updated to
the file.</p>
<h3>View statistics</h3>
<p>The seller could view data that includes a list of customers with the number of items that they bought,
as well as a list of products with the number of total sales.</p>
<h3>Log out</h3>
<p>This exits the program and logs out the user.</p>
   </div>
____________________________________________________________
<div class = "surrounding">
<h1>Customer</h1>
<p>The customer after logging in or creating an account is asked to
enter their budget. They are then asked if they want to shop by seller
or view the entire marketplace.<p>
<h3>View Marketplace</h3>
<p>If they elect to view the marketplace,
they can then view all of the stores and products, which they are then
asked to enter a product number. They are then asked to either add the item
to their shopping cart or purchase right away.</p>
<p>If they purchase, the budget subtracts the item price assuming it is positive,
if not it throws an exception, and the stores are updated depnding on how many times
the item was bought.</p>
<h3>Shop by Seller</h3>
<p>If they elect to view by seller, they can type the name of the seller which is
stored in the User Database. The corresponsing seller's stores and products are
then shown, to which they can again choose to add the item to their cart
or purchase it right away. The customers always have the option to go back
to the page they were viewing before as well.</p>
</div>
____________________________________________________________
<div class = "surrounding">
<h1>Submitting</h1>
<p>The following components of this project were submitted by the following team members:</p>
<p>Report:</p>
<p>Ethan Garcia</p>
<p>Github repository in Vocareum workspace:</p>
<p>Vedant Thakur</p>
</div>
___________________________________________________________






<div class = "surrounding">
<h2>Class #1: Buyer</h2>
<p>This is a Java class for an online marketplace including buyers,
sellers, stores, and products. It allows customers to browse and purchase
products, add to shopping carts, and manage purchases. The class includes
methods for writing and reading customer and vendor databases from a text file.
The BuyProduct method enables customers to purchase a product, change the inventory
levels in the store, and add the purchase to the customer’s purchase history. Another method
such as the removeFromShoppingCart method removes an item from the customer's shopping cart.
The viewProduct method displays information about an object on the console. The viewStore method
returns the store where an item is sold. Lastly, the addToShoppingCart method adds an item to the shopping
cart if the item is in the store and has an available quantity. As a note, the account that the buyer has will
classify it as a buyer account and the account that a seller has will classify it as a seller account. This will
make sure that the shop by seller functions work and that the data is written to the proper file.<p>
</div>
___________________________________________________________




<div class = "surrounding">
<h2>Class #2: Seller</h2>
<p>The Seller class represents a seller of products. It contains information
about the seller's stores, products, and sales. The class has methods for adding,
removing, and updating stores and products. It also has a method for calculating
the total sales of a specific product. The class can write and read seller data
to/from a text file. It can also generate statistics on customer purchases and
store performance. </p>
</div>
___________________________________________________________


<div class = "surrounding">
<h2>Class #3: Main</h2>
<p>This is the main method that implements the functionalities described above.
This handles printing everything, whereas the other classes does it's own respective
processing and is then utilized by the main method to print to the person using the
application.</p>
</div>
___________________________________________________________


<div class = "surrounding">
<h2>Class #4: ProductPurchase</h2>
<p>The class "ProductPurchase" extends the "Product" class and adds an "orderQuantity"
attribute. There are getter and setter methods for the "orderQuantity" attribute. It contains
constructors that takes a "uniqueID" and an "orderQuantity" as arguments. The "toString" method
returns a string representation of the "uniqueID" and "orderQuantity".</p>
</div>
___________________________________________________________


<div class = "surrounding">
<h2>Class #5: User</h2>
<p>This class implements all of the functionalities of a user. It has the code
that covers the account of the user. It also has different constructors and
variables of the Seller and Buyer, it is the superclass of each, they have similar
variables such as their account, name and age.</p>
</div>
___________________________________________________________


<div class = "surrounding">
<h2>Class #6: Product</h2>
<p>This class contains all of the information about the products contained within the
stores. It makes sure that every product contains a unique ID, name, description, price,
and quantity. Any time information about the product is edited or requested to update the
databases, the methods contained within this class will be called.</p>
</div>
___________________________________________________________


<div class = "surrounding">
<h2>Class #7: Store</h2>
<p>The Store class represents a store that contains a list of items. It has methods for obtaining
remaining inventory, total sales price, and total sales volume. The class also has methods to add
and obtain items, and to set and obtain the name of the store. Finally, it has a toString() method
that returns the name of the store.</p>
</div>
___________________________________________________________


<div class = "surrounding">
<h2>Class #8: UserDataBaseFormatError</h2>
<p>This is an error that is thrown when there is a formatting error in the text files
containing the information about the user, stores, or products.</p>
</div>
___________________________________________________________


<div class = "surrounding">
<h2>Class #9: NoAccountError</h2>
<p>Throws an error if a user tries to login to an account that
does not exist.</p>
</div>


<div class = "surrounding">
<h2>Class #10: AccountTypeError</h2>
<p>This error is thrown when a seller tries to login with a buyer
account and/or when a buyer tries to login with a seller account.<p>
</div>
____________________________________________________________












 </body>
</html>


