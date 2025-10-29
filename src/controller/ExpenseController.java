package controller;

import model.Expense;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ExpenseController {
    private List<Expense> expenses;
    private Map<String, Double> categoryBudgets;
    private static final String DATA_FILE = "data/expenses.csv";
    private static final String BUDGET_FILE = "data/budgets.csv";

    public ExpenseController() {
        this.expenses = new ArrayList<>();
        this.categoryBudgets = new HashMap<>();
        loadExpenses();
        loadBudgets();
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
        saveExpenses();
    }

    public void updateExpense(String id, LocalDate date, String category, double amount, String description) {
        for (Expense expense : expenses) {
            if (expense.getId().equals(id)) {
                expense.setDate(date);
                expense.setCategory(category);
                expense.setAmount(amount);
                expense.setDescription(description);
                saveExpenses();
                return;
            }
        }
    }

    public void deleteExpense(String id) {
        expenses.removeIf(expense -> expense.getId().equals(id));
        saveExpenses();
    }

    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expenses);
    }

    public List<Expense> getExpensesByCategory(String category) {
        return expenses.stream()
                .filter(e -> e.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    public List<Expense> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        return expenses.stream()
                .filter(e -> !e.getDate().isBefore(startDate) && !e.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    public List<Expense> searchExpenses(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return expenses.stream()
                .filter(e -> e.getDescription().toLowerCase().contains(lowerKeyword)
                        || e.getCategory().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    public Map<String, Double> getCategorySummary() {
        Map<String, Double> summary = new HashMap<>();
        for (Expense expense : expenses) {
            summary.merge(expense.getCategory(), expense.getAmount(), Double::sum);
        }
        return summary;
    }

    public double getTotalExpenses() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }

    public double getMonthlyTotal(int year, int month) {
        return expenses.stream()
                .filter(e -> e.getDate().getYear() == year && e.getDate().getMonthValue() == month)
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public double getWeeklyTotal(LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);
        return getExpensesByDateRange(weekStart, weekEnd).stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public Map<String, Double> getMonthlyCategorySummary(int year, int month) {
        Map<String, Double> summary = new HashMap<>();
        expenses.stream()
                .filter(e -> e.getDate().getYear() == year && e.getDate().getMonthValue() == month)
                .forEach(e -> summary.merge(e.getCategory(), e.getAmount(), Double::sum));
        return summary;
    }

    public void setBudget(String category, double budget) {
        categoryBudgets.put(category, budget);
        saveBudgets();
    }

    public Double getBudget(String category) {
        return categoryBudgets.get(category);
    }

    public Map<String, Double> getAllBudgets() {
        return new HashMap<>(categoryBudgets);
    }

    public boolean isBudgetExceeded(String category) {
        Double budget = categoryBudgets.get(category);
        if (budget == null) return false;

        double spent = expenses.stream()
                .filter(e -> e.getCategory().equals(category))
                .mapToDouble(Expense::getAmount)
                .sum();

        return spent > budget;
    }

    public double getBudgetRemaining(String category) {
        Double budget = categoryBudgets.get(category);
        if (budget == null) return 0;

        double spent = expenses.stream()
                .filter(e -> e.getCategory().equals(category))
                .mapToDouble(Expense::getAmount)
                .sum();

        return budget - spent;
    }

    private void saveExpenses() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            writer.println("ID,Date,Category,Amount,Description");
            for (Expense expense : expenses) {
                // ✅ Save plain numeric values (no ₹ symbol)
                writer.println(expense.getId() + "," +
                        expense.getDate() + "," +
                        expense.getCategory() + "," +
                        expense.getAmount() + "," +
                        expense.getDescription());
            }
        } catch (IOException e) {
            System.err.println("Error saving expenses: " + e.getMessage());
        }
    }

    private void loadExpenses() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 5);
                if (parts.length >= 5) {
                    String id = parts[0];
                    LocalDate date = Expense.parseDate(parts[1]);
                    String category = parts[2];

                    // ✅ Remove ₹, commas, and other symbols before parsing
                    String amountStr = parts[3].replaceAll("[^0-9.]", "");
                    double amount = Double.parseDouble(amountStr);

                    String description = parts[4];
                    expenses.add(new Expense(id, date, category, amount, description));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading expenses: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing amount in expenses.csv: " + e.getMessage());
        }
    }

    private void saveBudgets() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BUDGET_FILE))) {
            writer.println("Category,Budget");
            for (Map.Entry<String, Double> entry : categoryBudgets.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
        } catch (IOException e) {
            System.err.println("Error saving budgets: " + e.getMessage());
        }
    }

    private void loadBudgets() {
        File file = new File(BUDGET_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(BUDGET_FILE))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    categoryBudgets.put(parts[0], Double.parseDouble(parts[1]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading budgets: " + e.getMessage());
        }
    }
}
