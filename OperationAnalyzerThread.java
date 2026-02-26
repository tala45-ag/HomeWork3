import java.nio.file.*;
import java.util.*;

public class OperationAnalyzerThread implements Runnable {

    private List<Book> catalog;
    private Path catalogPath;
    private String operation;

    public OperationAnalyzerThread(List<Book> catalog,
                                   Path catalogPath,
                                   String operation) {
        this.catalog = catalog;
        this.catalogPath = catalogPath;
        this.operation = operation;
    }

    @Override
    public void run() {

        try {

            if (operation.matches("\\d{13}")) {

                LibraryBookTracker.printHeader();

                for (Book b : catalog) {
                    if (b.getIsbn().equals(operation)) {
                        System.out.println(b);
                        LibraryBookTracker.searchResults++;
                    }
                }

            } else if (operation.contains(":")) {

                try {
                    Book newBook = Book.parse(operation);
                    catalog.add(newBook);
                    LibraryBookTracker.sortBooksByTitle(catalog);
                    LibraryBookTracker.booksAdded++;

                    List<String> updatedLines = new ArrayList<>();
                    for (Book b : catalog) {
                        updatedLines.add(
                                b.getTitle() + ":" +
                                b.getAuthor() + ":" +
                                b.getIsbn() + ":" +
                                b.getCopies());
                    }

                    Files.write(catalogPath, updatedLines);

                    LibraryBookTracker.printHeader();
                    System.out.println(newBook);

                } catch (BookCatalogException e) {
                    ErrorHandler.logInputError(catalogPath, operation, e);
                    LibraryBookTracker.errorsCount++;
                }

            } else {

                LibraryBookTracker.printHeader();
                String keyword = operation.toLowerCase();

                for (Book b : catalog) {
                    if (b.getTitle().toLowerCase().contains(keyword)) {
                        System.out.println(b);
                        LibraryBookTracker.searchResults++;
                    }
                }
            }

        } catch (Exception e) {
            LibraryBookTracker.errorsCount++;
        }
    }
}