<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Personal Finance Tracker</title>
</head>
<body>
<h2>Enter Transaction</h2>
<form method="post" action="transactions">
    Type:
    <select name="type">
        <option value="Income">Income</option>
        <option value="Expense">Expense</option>
    </select><br>
    Category: <input type="text" name="category"><br>
    Amount: <input type="number" name="amount" step="0.01"><br>
    Date: <input type="date" name="date"><br>
    Note: <input type="text" name="note"><br>
    <input type="submit" value="Add Transaction">
</form>

<h2>Filter Transactions</h2>
<form method="get" action="transactions">
    Type:
    <select name="filterType">
        <option value="">All</option>
        <option value="Income">Income</option>
        <option value="Expense">Expense</option>
    </select>

    Category:
    <input type="text" name="filterCategory" placeholder="e.g. Groceries">

    Start Date:
    <input type="date" name="startDate">

    End Date:
    <input type="date" name="endDate">

    <input type="submit" value="Apply Filters">
</form>

<h2>Transaction History</h2>
<c:if test="${not empty transactions}">
    <table border="1">
        <tr>
            <th>Date</th>
            <th>Type</th>
            <th>Category</th>
            <th>Amount</th>
            <th>Note</th>
        </tr>
        <c:forEach var="t" items="${transactions}">
            <tr>
                <td>${t.date}</td>
                <td>${t.type}</td>
                <td>${t.category}</td>
                <td>$${t.amount}</td>
                <td>${t.note}</td>
                <td>
                    <form method="post" action="transactions" style="display:inline;">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" value="${t.id}">
                        <input type="submit" value="Delete" onclick="return confirm('Are you sure you want to delete this transaction?');">
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</c:if>
<c:if test="${empty transactions}">
    <p>No transactions found.</p>
</c:if>

<h2>Set Monthly Budget</h2>
<form method="post" action="transactions">
    <input type="hidden" name="action" value="setBudget">
    Budget for this month: $<input type="number" name="monthlyBudget" step="0.01" required>
    <input type="submit" value="Set Budget">
</form>

<br>

<h3>Summary</h3>
<table border="1" cellpadding="5" cellspacing="0">
    <tr>
        <th>Total Income</th>
        <th>Total Expenses</th>
        <th>Net Balance</th>
    </tr>
    <tr>
        <td>$${totalIncome}</td>
        <td>$${totalExpense}</td>
        <td>
            <c:choose>
                <c:when test="${netBalance >= 0}">
                    <span style="color: green;">$${netBalance}</span>
                </c:when>
                <c:otherwise>
                    <span style="color: red;">$${netBalance}</span>
                </c:otherwise>
            </c:choose>
        </td>
    </tr>
</table>

<c:if test="${not empty sessionScope.monthlyBudget}">
    <h3>Budget Overview</h3>
    <table border="1" cellpadding="5" cellspacing="0">
        <tr>
            <th>Budgeted Amount</th>
            <th>Total Expenses</th>
            <th>Remaining</th>
        </tr>
        <tr>
            <td>$${sessionScope.monthlyBudget}</td>
            <td>$${totalExpense}</td>
            <td>
                <c:set var="remaining" value="${sessionScope.monthlyBudget - totalExpense}" />
                <c:choose>
                    <c:when test="${remaining >= 0}">
                        <span style="color: green;">$${remaining}</span>
                    </c:when>
                    <c:otherwise>
                        <span style="color: red;">$${remaining}</span>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </table>
</c:if>

</body>
</html>