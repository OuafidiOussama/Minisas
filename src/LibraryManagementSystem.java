import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class LibraryManagementSystem {
    public static void main(String[] args) {
        Connection connection = DatabaseConnection.getConnection();
        BookDAO bookDAO = new BookDAO(connection);
        LoanDAO loanDAO = new LoanDAO(connection);
        UserDAO userDAO = new UserDAO(connection);

        BookDAO.updateBookStatusToMissing(connection);

        Scanner input = new Scanner(System.in);
        int option;

        do {
            menu();
            option = input.nextInt();

            switch (option) {
                case 1 -> {
                    // Create a new book
                    Book newBook = addBook();
                    bookDAO.addBook(newBook);
                }
                case 2 ->
                    // Update a book
                        updateBook(bookDAO);
                case 3 ->
                    // Delete a book
                        bookDAO.deleteBookByISBN(deleteBook());
                case 4 -> {
                    // Search for a book
                    int sOption;
                    do{
                        searchMenu();
                        sOption = input.nextInt();

                        switch (sOption){
                            case 1 ->
                                // Search for a book by title
                                searchBooksByTitle(bookDAO);
                            case 2 ->
                                // Search for a book by author
                                searchBooksByAuthor(bookDAO);
                            default -> System.out.println("\n---Invalid input!!---\n");
                        }
                    }while(sOption !=0);
                }
                case 5 -> {
                    // Display all books
                    List<Book> allBooks = bookDAO.getAllBooks();
                    displayBooks(allBooks);
                }
                case 6 -> {
                    // Loan a book
                    int lOption;
                    do{
                        loanMenu();
                        lOption = input.nextInt();

                        switch (lOption){
                            case 1 ->{
                                // To a new User
                                User newUser = addUser();
                                userDAO.addUser(newUser);
                            }
                            case 2 ->{
                                // To an existing User
                                Loan newLoan = createNewLoan();
                                loanDAO.addLoan(newLoan);
                            }
                            default -> System.out.println("\n---Invalid input!!---\n");
                        }
                    }while(lOption !=0);

                }
                case 7 -> {
                    // Return a book
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("Enter the ISBN of the book to return: ");
                    String isbnToReturn = scanner.nextLine();
                    returnBook(isbnToReturn, bookDAO, loanDAO);
                }
                case 8 -> {
                    // Generate Report
                    String reportFileName = "report.txt";
                    ReportGenerator.generateReport(connection, reportFileName);
                }
                default -> System.out.println("\n---Invalid input!!---\n");
            }
        } while (option != 0);

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void menu() {
        System.out.println("-----Library Management System-----");
        System.out.println("1: Add Book");
        System.out.println("2: Edit/Update Book");
        System.out.println("3: Delete Book");
        System.out.println("4: Search For Book");
        System.out.println("5: Display All Books");
        System.out.println("6: Loan Book");
        System.out.println("7: Return Book");
        System.out.println("8: Generate Report");
        System.out.println("0: Exit program");
        System.out.print("Enter your option : ");
    }
    public static void searchMenu(){
        System.out.println("-----Search For A Book-----");
        System.out.println("1: By Title");
        System.out.println("2: By Author");
        System.out.println("0: <--Back");
        System.out.print("Enter your option : ");
    }

    public static void loanMenu(){
        System.out.println("-----Loan A Book To-----");
        System.out.println("1: A New User ");
        System.out.println("2: An Existing User");
        System.out.println("0: <--Back");
        System.out.print("Enter your option : ");
    }
    private static Book addBook(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Book Details:");
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Author: ");
        String author = scanner.nextLine();

        return new Book(isbn, title, author, "available");
    }

    private static void updateBook(BookDAO bookDAO) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the ISBN of the book to update: ");
        String isbnToUpdate = scanner.nextLine();

        Book existingBook = bookDAO.getBookByISBN(isbnToUpdate);

        if (existingBook != null) {
            System.out.print("Enter the new title: ");
            String newTitle = scanner.nextLine();
            System.out.print("Enter the new author: ");
            String newAuthor = scanner.nextLine();
            System.out.print("Enter the new status: ");
            String newStatus = scanner.nextLine();

            Book updatedBook = new Book(isbnToUpdate, newTitle, newAuthor, newStatus);
            bookDAO.updateBook(updatedBook);
        } else {
            System.out.println("Book with ISBN " + isbnToUpdate + " not found");
        }
    }

    private static String deleteBook(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the ISBN of the book to delete: ");
        String isbnToDelete = scanner.nextLine();
        return isbnToDelete;
    }

    private static void searchBooksByTitle(BookDAO bookDAO) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the title to search for: ");
        String titleToSearch = scanner.nextLine();

        List<Book> booksFound = bookDAO.getBooksByTitle(titleToSearch);
        if (!booksFound.isEmpty()) {
            System.out.println("Books found with the title '" + titleToSearch + "':");
            for (Book book : booksFound) {
                System.out.println(book);
            }
        } else {
            System.out.println("No books found with the title '" + titleToSearch);
        }
    }

    private static void searchBooksByAuthor(BookDAO bookDAO) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the author to search for: ");
        String authorToSearch = scanner.nextLine();

        List<Book> booksFound = bookDAO.getBooksByAuthor(authorToSearch);
        if (!booksFound.isEmpty()) {
            System.out.println("Books found with the author '" + authorToSearch + "':");
            for (Book book : booksFound) {
                System.out.println(book);
            }
        } else {
            System.out.println("No books found with the author '" + authorToSearch);
        }
    }

    private static void displayBooks(List<Book> books) {
        if (!books.isEmpty()) {
            System.out.println("All Books:");
            for (Book book : books) {
                System.out.println(book);
            }
        } else {
            System.out.println("No books found in the library");
        }
    }

    private static User addUser(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter User Details:");
        System.out.print("UserId: ");
        String userId = scanner.nextLine();
        System.out.print("First Name: ");
        String firstName = scanner.nextLine();
        System.out.print("Last Name: ");
        String lastName = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Phone Number: ");
        String phone = scanner.nextLine();
        System.out.print("Address: ");
        String address = scanner.nextLine();

        return new User(userId, firstName, lastName, email, phone, address);
    }

    private static Loan createNewLoan() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Loan Details:");
        System.out.print("ISBN of the Book: ");
        String isbn = scanner.nextLine();
        System.out.print("Borrower's Id: ");
        String borrowerId = scanner.nextLine();


        Date borrowDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        System.out.print("Return Date (Day-Month-Year): ");
        String returnDateStr = scanner.nextLine();

        Date returnDate = null;
        try {
            returnDate = dateFormat.parse(returnDateStr);
        } catch (java.text.ParseException e) {
            System.out.println("Invalid date format! Please use Day-Month-Year.");
            return null;
        }

        return new Loan(isbn, borrowerId, borrowDate, returnDate);
    }
    public static void returnBook(String isbn, BookDAO bookDAO, LoanDAO loanDAO) {
        if (loanDAO.isBookBorrowed(isbn)) {
            bookDAO.updateBookStatus(isbn, "available");
            loanDAO.deleteLoanByISBN(isbn);
            System.out.println("Book returned successfully");
        } else {
            System.out.println("Error: The book with ISBN " + isbn + " is not currently borrowed");
        }
    }

}
