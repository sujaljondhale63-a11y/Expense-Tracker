EXPENSE TRACKER - JAVA SWING APPLICATION
=========================================

IMPORTANT: This application is a desktop GUI application built with Java Swing.
It is designed to run on your LOCAL COMPUTER, not in the cloud.

TO RUN ON YOUR LOCAL MACHINE:
------------------------------
1. Make sure you have Java JDK 11 or higher installed
   - Download from: https://www.oracle.com/java/technologies/downloads/

2. Download all the project files to your computer

3. Open a terminal/command prompt in the project directory

4. Compile the application:
   javac -d bin src/Main.java src/model/*.java src/controller/*.java src/view/*.java

5. Run the application:
   java -cp bin Main

6. The Expense Tracker window will open!


FEATURES:
---------
✓ Add, Edit, and Delete Expenses
✓ View all expenses in a table
✓ Search expenses by keyword
✓ Set budgets for each category
✓ Get warnings when exceeding budgets
✓ View monthly and weekly summaries
✓ Category-wise expense analysis
✓ Automatic data saving to CSV files


PROJECT STRUCTURE:
------------------
src/
  model/
    Expense.java          - Expense data model
  controller/
    ExpenseController.java - Business logic and data management
  view/
    ExpenseGUI.java       - GUI interface
  Main.java              - Application entry point

data/
  expenses.csv           - Your expense data (auto-created)
  budgets.csv            - Your budget settings (auto-created)


CATEGORIES AVAILABLE:
--------------------
• Food
• Transport
• Bills
• Entertainment
• Shopping
• Healthcare
• Education
• Other


USING THE APPLICATION:
---------------------
1. ADD EXPENSE: Fill in date, category, amount, and description, then click "Add Expense"
2. EDIT EXPENSE: Click on a row to select it, modify the fields, then click "Update Expense"
3. DELETE EXPENSE: Select a row and click "Delete Expense"
4. SEARCH: Type keywords in the search box and click "Search"
5. VIEW SUMMARY: Click "View Summary" for total expenses and category breakdown
6. MANAGE BUDGET: Click "Manage Budget" to set spending limits for categories


TIPS FOR YOUR COLLEGE PROJECT:
-------------------------------
• Demonstrate all features during your presentation
• Show the CSV files to explain data persistence
• Explain the MVC (Model-View-Controller) architecture
• Highlight the budget warning feature
• Show the search and filter functionality
• Display the summary report with category breakdown


For questions or issues, check that:
- Java JDK is properly installed
- All source files are in the correct directories
- You're running from the project root directory


Good luck with your college project!
