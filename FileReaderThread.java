import java.nio.file.*;
import java.util.*;

public class FileReaderThread implements Runnable {
    private List<Book> catalog;
    private Path catalogPath;

    public FileReaderThread(List<Book> catalog, Path catalogPath) {
        this.catalog = catalog;
        this.catalogPath = catalogPath;
    }

    @Override
    public void run() {
        try {

            if (!Files.exists(catalogPath))
                Files.createFile(catalogPath);

            List<String> lines = Files.readAllLines(catalogPath);

            for (String line : lines) {
                try {
                    Book book = Book.parse(line);
                    catalog.add(book);
                    LibraryBookTracker.validRecords++;
                } catch (BookCatalogException e) {
                    ErrorHandler.logCatalogLineError(catalogPath, line, e);
                    LibraryBookTracker.errorsCount++;
                }
            }

        } catch (Exception e) {
            LibraryBookTracker.errorsCount++;
        }
    }
}
