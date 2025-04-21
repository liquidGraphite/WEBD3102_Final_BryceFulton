package com.nscc.finance;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("setBudget".equals(action)) {
            double monthlyBudget = Double.parseDouble(req.getParameter("monthlyBudget"));
            HttpSession session = req.getSession();
            session.setAttribute("monthlyBudget", monthlyBudget);
            resp.sendRedirect("transactions");
            return;
        }

        if ("delete".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            try (Connection conn = SQLServerDBHelper.getConnection()) {
                String deleteSql = "DELETE FROM Transactions WHERE id = ?";
                PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                deleteStmt.setInt(1, id);
                deleteStmt.executeUpdate();
                req.getSession().setAttribute("success", "Transaction deleted successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
                req.getSession().setAttribute("error", "Failed to delete transaction: " + e.getMessage());
            }
            resp.sendRedirect("transactions");
            return;
        }

        // Add transaction
        String type = req.getParameter("type");
        String category = req.getParameter("category");
        double amount = Double.parseDouble(req.getParameter("amount"));
        String date = req.getParameter("date");
        String note = req.getParameter("note");

        try (Connection conn = SQLServerDBHelper.getConnection()) {
            // Insert new transaction
            String insertSql = "INSERT INTO Transactions (type, category, amount, date, note) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, type);
            insertStmt.setString(2, category);
            insertStmt.setDouble(3, amount);
            insertStmt.setDate(4, java.sql.Date.valueOf(date));
            insertStmt.setString(5, note);
            insertStmt.executeUpdate();

            req.setAttribute("success", "Transaction saved successfully.");

            // Retrieve all transactions
            String selectSql = "SELECT * FROM Transactions ORDER BY date DESC";
            PreparedStatement selectStmt = conn.prepareStatement(selectSql);
            var rs = selectStmt.executeQuery();

            List<Transaction> transactions = new ArrayList<>();

            double totalIncome = 0;
            double totalExpense = 0;

            while (rs.next()) {
                Transaction tx = new Transaction();
                tx.setId(rs.getInt("id"));
                tx.setType(rs.getString("type"));
                tx.setCategory(rs.getString("category"));
                tx.setAmount(rs.getDouble("amount"));
                tx.setDate(rs.getDate("date").toString());
                tx.setNote(rs.getString("note"));
                transactions.add(tx);

                if ("Income".equalsIgnoreCase(tx.getType())) {
                    totalIncome += tx.getAmount();
                } else if ("Expense".equalsIgnoreCase(tx.getType())) {
                    totalExpense += tx.getAmount();
                }
            }

            double netBalance = totalIncome - totalExpense;

            req.setAttribute("transactions", transactions);
            req.setAttribute("totalIncome", totalIncome);
            req.setAttribute("totalExpense", totalExpense);
            req.setAttribute("netBalance", netBalance);

        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Database error: " + e.getMessage());
        }

        resp.sendRedirect("transactions");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filterType = req.getParameter("filterType");
        String filterCategory = req.getParameter("filterCategory");
        String startDate = req.getParameter("startDate");
        String endDate = req.getParameter("endDate");

        List<Transaction> transactions = new ArrayList<>();
        double totalIncome = 0;
        double totalExpense = 0;

        try (Connection conn = SQLServerDBHelper.getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT * FROM Transactions WHERE 1=1");

            if (filterType != null && !filterType.isEmpty()) {
                sql.append(" AND type = ?");
            }
            if (filterCategory != null && !filterCategory.isEmpty()) {
                sql.append(" AND category LIKE ?");
            }
            if (startDate != null && !startDate.isEmpty()) {
                sql.append(" AND date >= ?");
            }
            if (endDate != null && !endDate.isEmpty()) {
                sql.append(" AND date <= ?");
            }

            sql.append(" ORDER BY date DESC");

            PreparedStatement stmt = conn.prepareStatement(sql.toString());

            int paramIndex = 1;
            if (filterType != null && !filterType.isEmpty()) {
                stmt.setString(paramIndex++, filterType);
            }
            if (filterCategory != null && !filterCategory.isEmpty()) {
                stmt.setString(paramIndex, "%" + filterCategory + "%");
            }
            if (startDate != null && !startDate.isEmpty()) {
                stmt.setDate(paramIndex++, java.sql.Date.valueOf(startDate));
            }
            if (endDate != null && !endDate.isEmpty()) {
                stmt.setDate(paramIndex++, java.sql.Date.valueOf(endDate));
            }

            var rs = stmt.executeQuery();
            while (rs.next()) {
                Transaction tx = new Transaction();
                tx.setId(rs.getInt("id"));
                tx.setType(rs.getString("type"));
                tx.setCategory(rs.getString("category"));
                tx.setAmount(rs.getDouble("amount"));
                tx.setDate(rs.getDate("date").toString());
                tx.setNote(rs.getString("note"));
                transactions.add(tx);

                if ("Income".equalsIgnoreCase(tx.getType())) {
                    totalIncome += tx.getAmount();
                } else if ("Expense".equalsIgnoreCase(tx.getType())) {
                    totalExpense += tx.getAmount();
                }
            }

            req.setAttribute("transactions", transactions);
            req.setAttribute("totalIncome", totalIncome);
            req.setAttribute("totalExpense", totalExpense);
            req.setAttribute("netBalance", totalIncome - totalExpense);

        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Database error: " + e.getMessage());
        }

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}