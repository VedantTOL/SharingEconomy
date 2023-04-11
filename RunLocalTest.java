import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


import java.io.*;
import java.util.ArrayList;


import static org.junit.Assert.*;


/**
 * RunLocalTest
 *
 * A framework to run public test cases.
 *
 * @author Somansh Shah, Project 4
 * @version April 10, 2023
 */




public class RunLocalTest {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestCase.class);
        // Add a comment here
        if (result.wasSuccessful()) {
            System.out.println("Excellent - Tests ran successfully");
        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }


    /**
     * TestCase
     * <p>
     * A set of public test cases.
     *
     * @author Somansh Shah, L17
     * @version November 14, 2021
     */


    public static class TestCase {
        private final PrintStream originalOutput = System.out;
        private final InputStream originalSysin = System.in;


        @SuppressWarnings("FieldCanBeLocal")
        private ByteArrayInputStream testIn;


        @SuppressWarnings("FieldCanBeLocal")
        private ByteArrayOutputStream testOut;


        @Before
        public void outputStart() {
            testOut = new ByteArrayOutputStream();
            System.setOut(new PrintStream(testOut));
        }


        @After
        public void restoreInputAndOutput() {
            System.setIn(originalSysin);
            System.setOut(originalOutput);
        }


        private String getOutput() {
            return testOut.toString();
        }


        @SuppressWarnings("SameParameterValue")
        private void receiveInput(String str) {
            testIn = new ByteArrayInputStream(str.getBytes());
            System.setIn(testIn);
        }


        // testExpectedOne tests the basic account login and logout feature.


        @Test(timeout = 1000)
        public void testExpectedOne() {
            try {
                String expected = "Are you a Customer or Seller?\n" + "1. Customer\n" + "2. Seller\n" + "3. Exit\n" +
                        "Thank you! Please come again!\n";


                       /*"Enter your password:\n" + "Logged in successfully!\n" +
                       "What would you like to do? Choose from the menu choice below:\n" +
                       "1. Edit account\n" +
                       "2. Delete account\n" +
                       "3. Access teacher specific operations\n" +
                       "4. Log Out" + System.lineSeparator() + "Logged Out!\n" +
                       "Select a valid menu choice to continue:\n" +
                       "1. Log in\n" +
                       "2. Create a new account\n" +
                       "3. Exit application" + System.lineSeparator() + "Thank you for using this application!"; */


                String input = "3\n1\n";
                expected = expected.replaceAll("\r\n", "\n");


                // Runs the program with the input values
                receiveInput(input);
                Main.main(new String[0]);


                // Retrieves the output from the program
                String actual = getOutput();


                // Trims the output and verifies it is correct.
                actual = actual.replace("\r\n", "\n");
                assertEquals("Verify that account login and logout is conducted properly!",
                        expected.trim(), actual.trim());


            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        }




        @Test(timeout = 1000)
        public void testExpectedTwo() {
            try {
                String expected = "Are you a Customer or Seller?\n1. Customer\n2. Seller\n3. Exit\n" +
                        "Create new account or login?\n1. Create an account\n2. Login\n3. Exit\n" +
                        "Enter your email: " + System.lineSeparator() +
                        "Enter a password: " + System.lineSeparator() + "What's your name?\n" + "What's your age?\n" +
                        "What actions would you like to take?\n" +
                        "1. Add store\n" +
                        "2. Delete store\n" +
                        "3. Edit store\n" +
                        "4. View statistics\n" +
                        "5. Edit account\n" +
                        "6. Delete account\n" +
                        "7. Logout\n" + "Thank you for shopping with us!\n";


                String input = "2\n1\nshah672@purdue.edu\nshah1122\nSomansh\n22\n7\n";
                expected = expected.replaceAll("\r\n", "\n");


                // Runs the program with the input values
                receiveInput(input);
                Main.main(new String[0]);


                // Retrieves the output from the program
                String actual = getOutput();


                // Trims the output and verifies it is correct.
                actual = actual.replace("\r\n", "\n");
                assertEquals("Verify logout the logout feature of the code!",
                        expected.trim(), actual.trim());


            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        }








        @Test(timeout = 1000)
        public void testExpectedThree() {
            try {
                String expected = "Are you a Customer or Seller?\n1. Customer\n2. Seller\n3. Exit\n" +
                        "Create new account or login?\n1. Create an account\n2. Login\n3. Exit\n" +
                        "Enter your email: " + System.lineSeparator() +
                        "Enter a password: " + System.lineSeparator() + "What's your name?\n" + "What's your age?\n" +
                        "What actions would you like to take?\n" +
                        "1. Add store\n" +
                        "2. Delete store\n" +
                        "3. Edit store\n" +
                        "4. View statistics\n" +
                        "5. Edit account\n" +
                        "6. Delete account\n" +
                        "7. Log out\n" +
                        "What is the name of the store you want to add?\n" +
                        "How many products do you want to add?\n" +
                        "Enter the 1st item name\n" +
                        "What is the description?\n" +
                        "How many items in stock?\n" +
                        "How much does this item cost?\n" +
                        "Enter the 2nd item name\n" +
                        "What is the description?\n" +
                        "How many items in stock?\n" +
                        "How much does this item cost?\n" +
                        "What actions would you like to take?\n" +
                        "1. Add store\n" +
                        "2. Delete store\n" +
                        "3. Edit store\n" +
                        "4. View statistics\n" +
                        "5. Edit account\n" +
                        "6. Delete account\n" +
                        "7. Logout\n" +
                        "Thank you for shopping with us!\n";


                String input = "2\n1\nbraun43@purdue.edu\njbob40\nRoger\n22\n1\nRoger'sStore\n2\nBMW\nmatteblack\n12\n12.00\nCorvette\nsilver\n25.00\n10\n7\n";


                expected = expected.replaceAll("\r\n", "\n");


                // Runs the program with the input values
                receiveInput(input);
                Main.main(new String[0]);


                // Retrieves the output from the program
                String actual = getOutput();


                // Trims the output and verifies it is correct.
                actual = actual.replace("\r\n", "\n");
                assertEquals("Verify the seller class properly!",
                        expected.trim(), actual.trim());


            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        }


    }
}
