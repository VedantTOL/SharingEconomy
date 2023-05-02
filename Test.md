<!DOCTYPE html>
<html>
 <head>
   <title>Test 1: User log in</title>
   <style>
     /* Define bigger font and bold for the title */
     h1 {
       font-size: 36px;
       font-weight: bold;
     }
   </style>
 </head>
 <body>
   <h1>Test cases</h1>
   <h2>Test 1: User log in</h2>


<h3>Steps</h3>

     1.User launches application.
     2.User clicks exit button


<h3>Expected Results</h3>


     Java GUI window closes and program closes


<h2>Test 2: New Buyer views all products and buys a product </h2>


<h3>Steps</h3>


     1.User launches application.
     2.User clicks "Buyer" button
     3. Buyer clicks create account button
     4. Buyer enters login credentials
     5. Buyer selectes to view marketplace
     6. Buyer enters a budget of $300.00
     7. Buyer sorts by price
     8. Buyer selects top item from JComboBox (enter item name)
     9. Clicks add to cart (only chooses 1)
     10. Clicks view cart
     11. Hits buy now button


<h3>Expected Results</h3>


     A new purchase is listed in the buyer database
     The buyer's budget is subtracted by (enter price) and a new value is set

<h2>Test 3: Buyer shops by Seller</h2>
<h3>Steps</h3>


     1. User launches application
     2. User clicks "Buyer" button
     3. Buyer clicks "Login button"
     4. Buyer successfully logs in to account using credentials (Enter credentials of buyer login and password)
     5. Buyer clicks on the "Shop by Seller Button"
     6. Buyer enters a budget of $300.00
     7. Buyer selects the top Seller in the JComboBox (Enter infor)
     8. Buyer views the stores and items of that seller (Enter info)
     9. Buyer hits the Buy now button of the top item (name)
     10. Two of the items are bought
     11. All windows are closed and the program exits


<h3>Expected Results</h3>


     Buyer's budget decreases by 2 times the price of unit
     Purchase is written to file
     A new account is created in the User Database file


<h2>Test 4: Seller adds store</h2>
<h3>Steps</h3>


     1. User launches client before server starts
     2. Error window is shown asking to start server
     3. User starts application properly
     4. User clicks the "Seller" button
     5. Seller enters invalid credentials the first time
     6. Error window pops up saying it is the wrong information
     7. Seller successfully logs in using (enter credentials)
     8. Seller adds a store called "Store A", and adds 1 product called "X"
     9. Seller decides to delete store
     10. Seller selects store they want to delete (Enter name)
     11. Seller logs out


<h3>Expected Results</h3>


     1. The store that the seller deleted no longer appears in the Seller database
     2. The new Store A with product X now appears in the Seller Database


<h2>Test 5: Seller edits store name</h2>
<h3>Steps</h3>


     1. User launches client before server starts
     2. Error window is shown asking to start server
     3. User starts application properly
     4. User clicks the "Seller" button
     5. Seller successfully logs in using (enter credentials)
     5. Seller selects edit store
     6. Seller selects the store they wish to edit (enter store name)
     7. Seller elects to chnage the store name to "Store XYZ"
     8. Seller logs out, all windows are closed


<h3>Expected Results</h3>


     Seller Database updates so that the new store name is updated


<h2>Test 6: Seller edits store contents</h2>
<h3>Steps</h3>



     1. User starts application properly
     2. User clicks the "Seller" button
     3. Seller successfully logs in using (enter credentials)
     4. Seller selects edit store
     5. Seller selects the store they wish to edit (enter store name)
     6. Seller adds a product to the store called "product XYZ"
     7. Seller logs out, all windows are closed


<h3>Expected Results</h3>


     Seller Database updates so that the new store product is updated


<h2>Test 7: User edits account, and then closes</h2>


     1. User starts application properly
     2. User clicks the "Buyer button"
     3. User successfully logs in (enter credentials)
     4. User selects edit account button
     5. User edits password to '12345678'
     6. Password is saved
     7. User exits application


<h3>Expected Results</h3>


     User database password is updated for that account


<h2>Test 8: User deletes account</h2>


     1. User starts application properly
     2. User clicks the "Buyer button"
     3. User successfully logs in (enter credentials)
     4. User selects delete account buttonm
     5. JPaneWindow pops up asking for confirmation
     6. User confirms
     7. All windows close


<h3>Expected Results</h3>


     1. Account contents are removed from the User database









 </body>
</html>
