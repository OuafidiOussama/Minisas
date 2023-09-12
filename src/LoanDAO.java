import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class LoanDAO {
    private Connection connection;

    public LoanDAO(Connection connection) {
        this.connection = connection;
    }

    public void addLoan(Loan loan) {
        String isbn = loan.getIsbn();
        String userId = loan.getBorrowerId();
        BookDAO bookDAO = new BookDAO(connection);
        UserDAO userDAO = new UserDAO(connection);

        if (isBookBorrowed(isbn)) {
            Date returnDate = getReturnDate(isbn);
            System.out.println("Error: The book with ISBN " + isbn + " is already borrowed");
            System.out.println("Return Date: " + returnDate);
            return;
        }

        if (!bookDAO.isISBNExists(isbn)) {
            System.out.println("Error: The provided ISBN does not exist in the book database");
            return;
        }

        if (!userDAO.isUserIdExists(userId)) {
            System.out.println("Error: The provided User ID does not exist in the user database");
            return;
        }

        if (loan.getReturnDate().before(loan.getBorrowDate())) {
            System.out.println("Error: Invalid Return date. Return date must be after Today");
            return;
        }

        String sql = "INSERT INTO loan (isbn, borrowerId, borrowDate, returnDate) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, loan.getIsbn());
            preparedStatement.setString(2, loan.getBorrowerId());
            preparedStatement.setDate(3, new java.sql.Date(loan.getBorrowDate().getTime()));
            preparedStatement.setDate(4, new java.sql.Date(loan.getReturnDate().getTime()));

            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Loan added successfully");
            } else {
                System.out.println("Failed to add the loan");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Date getReturnDate(String isbn) {
        String sql = "SELECT returnDate FROM loan WHERE isbn = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, isbn);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getDate("returnDate");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean isBookBorrowed(String isbn) {
        String sql = "SELECT status FROM book WHERE isbn = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, isbn);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String status = resultSet.getString("status");
                return "borrowed".equalsIgnoreCase(status) || "missing".equalsIgnoreCase(status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void deleteLoanByISBN(String isbn) {
        String sql = "DELETE FROM loan WHERE isbn = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, isbn);

            int rowsDeleted = preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Loan record deleted successfully");
            } else {
                System.out.println("Error: Loan record with ISBN " + isbn + " not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
