package view;

import controller.ExpenseController;
import model.Expense;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ExpenseGUI extends JFrame {
    private ExpenseController controller;
    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private JTextField dateField, amountField, descriptionField, searchField;
    private JComboBox<String> categoryCombo;
    private JButton addButton, editButton, deleteButton, summaryButton, budgetButton, searchButton, refreshButton;
    
    private static final String[] CATEGORIES = {
        "Food", "Transport", "Bills", "Entertainment", "Shopping", 
        "Healthcare", "Education", "Other"
    };
    
    public ExpenseGUI() {
        controller = new ExpenseController();
        initializeUI();
        loadExpensesIntoTable();
    }
    
    private void initializeUI() {
        setTitle("Expense Tracker");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        add(createInputPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        
        setLocationRelativeTo(null);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Add/Edit Expense"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        dateField = new JTextField(LocalDate.now().toString(), 15);
        amountField = new JTextField(15);
        descriptionField = new JTextField(20);
        categoryCombo = new JComboBox<>(CATEGORIES);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        panel.add(dateField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        panel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 3;
        panel.add(categoryCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        panel.add(amountField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 3;
        panel.add(descriptionField, gbc);
        
        return panel;
    }
    
    private JScrollPane createTablePanel() {
        String[] columns = {"ID", "Date", "Category", "Amount", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        expenseTable = new JTable(tableModel);
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        expenseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedExpense();
            }
        });
        
        expenseTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        expenseTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        expenseTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        expenseTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        expenseTable.getColumnModel().getColumn(4).setPreferredWidth(300);
        
        return new JScrollPane(expenseTable);
    }
    
    private JPanel createButtonPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        addButton = new JButton("Add Expense");
        editButton = new JButton("Update Expense");
        deleteButton = new JButton("Delete Expense");
        summaryButton = new JButton("View Summary");
        budgetButton = new JButton("Manage Budget");
        refreshButton = new JButton("Refresh");
        
        addButton.addActionListener(e -> addExpense());
        editButton.addActionListener(e -> updateExpense());
        deleteButton.addActionListener(e -> deleteExpense());
        summaryButton.addActionListener(e -> showSummary());
        budgetButton.addActionListener(e -> manageBudget());
        refreshButton.addActionListener(e -> loadExpensesIntoTable());
        
        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(new JSeparator(SwingConstants.VERTICAL));
        actionPanel.add(summaryButton);
        actionPanel.add(budgetButton);
        actionPanel.add(refreshButton);
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Search & Filter"));
        
        searchField = new JTextField(15);
        searchButton = new JButton("Search");
        JButton filterButton = new JButton("Date/Category Filter");
        
        searchButton.addActionListener(e -> searchExpenses());
        filterButton.addActionListener(e -> showFilterDialog());
        
        filterPanel.add(new JLabel("Keyword:"));
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        filterPanel.add(filterButton);
        
        mainPanel.add(filterPanel, BorderLayout.NORTH);
        mainPanel.add(actionPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private void addExpense() {
        try {
            LocalDate date = LocalDate.parse(dateField.getText());
            String category = (String) categoryCombo.getSelectedItem();
            double amount = Double.parseDouble(amountField.getText());
            String description = descriptionField.getText();
            
            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Description cannot be empty!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Expense expense = new Expense(date, category, amount, description);
            controller.addExpense(expense);
            
            if (controller.isBudgetExceeded(category)) {
                JOptionPane.showMessageDialog(this, 
                    "Warning: Budget exceeded for category " + category + "!\n" +
                    "Remaining budget: $" + String.format("%.2f", controller.getBudgetRemaining(category)),
                    "Budget Alert", JOptionPane.WARNING_MESSAGE);
            }
            
            loadExpensesIntoTable();
            clearInputFields();
            JOptionPane.showMessageDialog(this, "Expense added successfully!");
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense to update!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            String id = (String) tableModel.getValueAt(selectedRow, 0);
            LocalDate date = LocalDate.parse(dateField.getText());
            String category = (String) categoryCombo.getSelectedItem();
            double amount = Double.parseDouble(amountField.getText());
            String description = descriptionField.getText();
            
            controller.updateExpense(id, date, category, amount, description);
            
            if (controller.isBudgetExceeded(category)) {
                JOptionPane.showMessageDialog(this, 
                    "Warning: Budget exceeded for category " + category + "!\n" +
                    "Remaining budget: $" + String.format("%.2f", controller.getBudgetRemaining(category)),
                    "Budget Alert", JOptionPane.WARNING_MESSAGE);
            }
            
            loadExpensesIntoTable();
            clearInputFields();
            JOptionPane.showMessageDialog(this, "Expense updated successfully!");
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                "Update Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense to delete!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this expense?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String id = (String) tableModel.getValueAt(selectedRow, 0);
            controller.deleteExpense(id);
            loadExpensesIntoTable();
            clearInputFields();
            JOptionPane.showMessageDialog(this, "Expense deleted successfully!");
        }
    }
    
    private void searchExpenses() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadExpensesIntoTable();
            return;
        }
        
        List<Expense> results = controller.searchExpenses(keyword);
        updateTable(results);
    }
    
    private void showSummary() {
        JDialog summaryDialog = new JDialog(this, "Expense Summary", true);
        summaryDialog.setSize(500, 400);
        summaryDialog.setLayout(new BorderLayout(10, 10));
        
        JTextArea summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        StringBuilder summary = new StringBuilder();
        summary.append("=== EXPENSE SUMMARY ===\n\n");
        
        LocalDate now = LocalDate.now();
        double monthlyTotal = controller.getMonthlyTotal(now.getYear(), now.getMonthValue());
        double totalExpenses = controller.getTotalExpenses();
        
        LocalDate weekStart = now.minusDays(now.getDayOfWeek().getValue() - 1);
        double weeklyTotal = controller.getWeeklyTotal(weekStart);
        
        summary.append(String.format("Total Expenses: $%.2f\n", totalExpenses));
        summary.append(String.format("This Month: $%.2f\n", monthlyTotal));
        summary.append(String.format("This Week: $%.2f\n\n", weeklyTotal));
        
        summary.append("--- Category Breakdown ---\n");
        Map<String, Double> categorySummary = controller.getCategorySummary();
        for (Map.Entry<String, Double> entry : categorySummary.entrySet()) {
            summary.append(String.format("%-15s: $%.2f\n", entry.getKey(), entry.getValue()));
        }
        
        summary.append("\n--- Budget Status ---\n");
        Map<String, Double> budgets = controller.getAllBudgets();
        for (Map.Entry<String, Double> entry : budgets.entrySet()) {
            String category = entry.getKey();
            double budget = entry.getValue();
            double remaining = controller.getBudgetRemaining(category);
            String status = remaining >= 0 ? "OK" : "EXCEEDED";
            summary.append(String.format("%-15s: Budget $%.2f, Remaining $%.2f [%s]\n", 
                category, budget, remaining, status));
        }
        
        summaryArea.setText(summary.toString());
        
        summaryDialog.add(new JScrollPane(summaryArea), BorderLayout.CENTER);
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> summaryDialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        summaryDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        summaryDialog.setLocationRelativeTo(this);
        summaryDialog.setVisible(true);
    }
    
    private void manageBudget() {
        JDialog budgetDialog = new JDialog(this, "Manage Budget", true);
        budgetDialog.setSize(400, 300);
        budgetDialog.setLayout(new BorderLayout(10, 10));
        
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JComboBox<String> budgetCategoryCombo = new JComboBox<>(CATEGORIES);
        JTextField budgetAmountField = new JTextField();
        
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(budgetCategoryCombo);
        inputPanel.add(new JLabel("Budget Amount:"));
        inputPanel.add(budgetAmountField);
        
        JButton setBudgetButton = new JButton("Set Budget");
        setBudgetButton.addActionListener(e -> {
            try {
                String category = (String) budgetCategoryCombo.getSelectedItem();
                double budget = Double.parseDouble(budgetAmountField.getText());
                controller.setBudget(category, budget);
                JOptionPane.showMessageDialog(budgetDialog, 
                    "Budget set successfully for " + category + "!");
                budgetAmountField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(budgetDialog, 
                    "Please enter a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        inputPanel.add(setBudgetButton);
        
        JTextArea currentBudgetsArea = new JTextArea();
        currentBudgetsArea.setEditable(false);
        currentBudgetsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        StringBuilder budgetList = new StringBuilder("Current Budgets:\n\n");
        Map<String, Double> budgets = controller.getAllBudgets();
        for (Map.Entry<String, Double> entry : budgets.entrySet()) {
            budgetList.append(String.format("%-15s: $%.2f\n", entry.getKey(), entry.getValue()));
        }
        currentBudgetsArea.setText(budgetList.toString());
        
        budgetDialog.add(inputPanel, BorderLayout.NORTH);
        budgetDialog.add(new JScrollPane(currentBudgetsArea), BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> budgetDialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        budgetDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        budgetDialog.setLocationRelativeTo(this);
        budgetDialog.setVisible(true);
    }
    
    private void loadExpensesIntoTable() {
        List<Expense> expenses = controller.getAllExpenses();
        updateTable(expenses);
    }
    
    private void updateTable(List<Expense> expenses) {
        tableModel.setRowCount(0);
        for (Expense expense : expenses) {
            Object[] row = {
                expense.getId(),
                expense.getDateString(),
                expense.getCategory(),
                String.format("$%.2f", expense.getAmount()),
                expense.getDescription()
            };
            tableModel.addRow(row);
        }
    }
    
    private void loadSelectedExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow != -1) {
            dateField.setText((String) tableModel.getValueAt(selectedRow, 1));
            categoryCombo.setSelectedItem(tableModel.getValueAt(selectedRow, 2));
            String amountStr = (String) tableModel.getValueAt(selectedRow, 3);
            amountField.setText(amountStr.replace("$", ""));
            descriptionField.setText((String) tableModel.getValueAt(selectedRow, 4));
        }
    }
    
    private void clearInputFields() {
        dateField.setText(LocalDate.now().toString());
        categoryCombo.setSelectedIndex(0);
        amountField.setText("");
        descriptionField.setText("");
        expenseTable.clearSelection();
    }
    
    private void showFilterDialog() {
        JDialog filterDialog = new JDialog(this, "Filter Expenses", true);
        filterDialog.setSize(450, 250);
        filterDialog.setLayout(new BorderLayout(10, 10));
        
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JTextField startDateField = new JTextField(LocalDate.now().minusMonths(1).toString());
        JTextField endDateField = new JTextField(LocalDate.now().toString());
        JComboBox<String> filterCategoryCombo = new JComboBox<>();
        filterCategoryCombo.addItem("All Categories");
        for (String cat : CATEGORIES) {
            filterCategoryCombo.addItem(cat);
        }
        
        inputPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        inputPanel.add(startDateField);
        inputPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        inputPanel.add(endDateField);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(filterCategoryCombo);
        
        JButton applyFilterButton = new JButton("Apply Filter");
        JButton clearFilterButton = new JButton("Clear Filter");
        
        applyFilterButton.addActionListener(e -> {
            try {
                LocalDate startDate = LocalDate.parse(startDateField.getText());
                LocalDate endDate = LocalDate.parse(endDateField.getText());
                String selectedCategory = (String) filterCategoryCombo.getSelectedItem();
                
                List<Expense> filtered = controller.getExpensesByDateRange(startDate, endDate);
                
                if (selectedCategory != null && !selectedCategory.equals("All Categories")) {
                    filtered = filtered.stream()
                        .filter(exp -> exp.getCategory().equals(selectedCategory))
                        .collect(java.util.stream.Collectors.toList());
                }
                
                updateTable(filtered);
                filterDialog.dispose();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(filterDialog, 
                    "Error: " + ex.getMessage(), 
                    "Filter Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        clearFilterButton.addActionListener(e -> {
            loadExpensesIntoTable();
            filterDialog.dispose();
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(applyFilterButton);
        buttonPanel.add(clearFilterButton);
        
        filterDialog.add(inputPanel, BorderLayout.CENTER);
        filterDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        filterDialog.setLocationRelativeTo(this);
        filterDialog.setVisible(true);
    }
}
