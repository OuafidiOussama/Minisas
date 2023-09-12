import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private Connection connection;

    public BookDAO(Connection connection) {
        this.connection = connection;
    }

    public void addBook(Book book) {
        if (isISBNExists(book.getIsbn())) {
            System.out.println("Error: The provided ISBN already corresponds to a book");
            return;
        }

        String sql = "INSERT INTO book (isbn, title, author, status) VALUES (?, ?, ?, ?)";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1,book.getIsbn());
            preparedStatement.setString(2, book.getTitle());
            preparedStatement.setString(3, book.getAuthor());
            preparedStatement.setString(4, book.getStatus());

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Book added successfully");
            } else {
                System.out.println("Failed to add the book");
            }
        } catch (SQLException e){
            System.out.println(e);
        }
    }

    public boolean isISBNExists(String isbn) {
        String sql = "SELECT COUNT(*) FROM book WHERE isbn = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, isbn);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public Book getBookByISBN(String isbn) {
        String sql = "SELECT * FROM book WHERE isbn = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, isbn);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String status = resultSet.getString("status");

                return new Book(isbn, title, author, status);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public List<Book> getBooksByTitle(String title) {
        List<Book> booksFound = new ArrayList<>();
        String sql = "SELECT * FROM book WHERE title LIKE ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + title + "%");

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String isbn = resultSet.getString("isbn");
                String bookTitle = resultSet.getString("title");
                String author = resultSet.getString("author");
                String status = resultSet.getString("status");

                Book book = new Book(isbn, bookTitle, author, status);
                booksFound.add(book);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return booksFound;
    }
    public List<Book> getBooksByAuthor(String author) {
        List<Book> booksFound = new ArrayList<>();
        String sql = "SELECT * FROM book WHERE author LIKE ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + author + "%");

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String isbn = resultSet.getString("isbn");
                String title = resultSet.getString("title");
                String bookAuthor = resultSet.getString("author");
                String status = resultSet.getString("status");

                Book book = new Book(isbn, title, bookAuthor, status);
                booksFound.add(book);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return booksFound;
    }

    public List<Book> getAllBooks() {
        List<Book> allBooks = new ArrayList<>();
        String sql = "SELECT * FROM book";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String isbn = resultSet.getString("isbn");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String status = resultSet.getString("status");

                Book book = new Book(isbn, title, author, status);
                allBooks.add(book);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return allBooks;
    }

    public void updateBook(Book updatedBook) {
        String sql = "UPDATE book SET title = ?, author = ?, status = ? WHERE isbn = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, updatedBook.getTitle());
            preparedStatement.setString(2, updatedBook.getAuthor());
            preparedStatement.setString(3, updatedBook.getStatus());
            preparedStatement.setString(4, updatedBook.getIsbn());

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Book updated successfully");
            } else {
                System.out.println("Book with ISBN " + updatedBook.getIsbn() + " not found");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void deleteBookByISBN(String isbn) {
        String sql = "DELETE FROM book Where isbn = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, isbn);

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Book deleted successfully");
            } else {
                System.out.println("Book with ISBN " + isbn + " not found");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void updateBookStatus(String isbn, String newStatus) {
        String sql = "UPDATE book SET status = ? WHERE isbn = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, newStatus);
            preparedStatement.setString(2, isbn);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Book status updated successfully");
            } else {
                System.out.println("Failed to update book status");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public static void updateBookStatusToMissing(Connection connection) {
        String sql = "UPDATE book b " +
                "INNER JOIN loan l ON b.isbn = l.isbn " +
                "SET b.status = 'missing' " +
                "WHERE l.returnDate < CURDATE() AND b.status = 'borrowed'";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Book status updated to 'missing' for overdue books");
            } else {
                System.out.println("No overdue books found");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }



}
