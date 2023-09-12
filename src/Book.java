public class Book {
    private String isbn;
    private String title;
    private String author;
    private String status;

    @Override
    public String toString() {
        return "isbn: " + isbn + ", title: " + title + ", author: " + author + ", status: " + status;
    }
    public Book(String isbn, String title, String author, String status) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.status = status;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
