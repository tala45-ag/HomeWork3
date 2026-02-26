import java.io.*;
import java.nio.file.*;
import java.util.*;

public class LibraryBookTracker {

    public static int validRecords = 0;
    public static int searchResults = 0;
    public static int booksAdded = 0;
    public static int errorsCount = 0;

    public static void main(String[] args) {

        List<Book> catalog = new ArrayList<>();
        Path catalogPath = null;

        try {
           
            if (args.length < 2) {
                InsufficientArgumentsException e =
                        new InsufficientArgumentsException("Insufficient arguments");
                Path dummyPath = Path.of("catalog.txt");
                ErrorHandler.logInputError(dummyPath, "NO ARGUMENTS", e);
                errorsCount++;
                System.out.println("Error: " + e.getMessage());
                return;
            }

            String fileName = args[0];
            String operation = args[1];
            catalogPath = Paths.get(fileName);

            // ======= CHECK FILE NAME =======
            if (!fileName.endsWith(".txt")) {
                InvalidFileNameException e =
                        new InvalidFileNameException("File must end with .txt");
                ErrorHandler.logInputError(catalogPath, fileName, e);
                errorsCount++;
                System.out.println("Error: " + e.getMessage());
                return;
            }

            
            if (catalogPath.getParent() != null)
                Files.createDirectories(catalogPath.getParent());

            if (!Files.exists(catalogPath))
                Files.createFile(catalogPath);

            
            Thread fileThread = new Thread(new FileReaderThread(catalog, catalogPath));
            Thread opThread = new Thread(new OperationAnalyzerThread(catalog, catalogPath, operation));

            fileThread.start();
            fileThread.join();   

            opThread.start();
            opThread.join();     

        } catch (InterruptedException e) {
            System.out.println("Thread interrupted");
            errorsCount++;
            if (catalogPath != null)
                ErrorHandler.logInputError(catalogPath, "THREAD INTERRUPTED", e);
        } catch (Exception e) { 
            System.out.println("Error: " + e.getMessage());
            errorsCount++;
            if (catalogPath != null)
                ErrorHandler.logInputError(catalogPath, "PROGRAM ERROR", e);
        } finally {
            
            System.out.println();
            System.out.println("Valid records processed: " + validRecords);
            System.out.println("Search results: " + searchResults);
            System.out.println("Books added: " + booksAdded);
            System.out.println("Errors encountered: " + errorsCount);
            System.out.println("Thank you for using the Library Book Tracker.");
        }
    }

    /**
     * 
     * @param books
     */
    public static void sortBooksByTitle(List<Book> books) {
        Collections.sort(books,
                Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));
    }

    
    public static void printHeader() {
        System.out.printf("%-30s %-20s %-15s %5s%n",
                "Title", "Author", "ISBN", "Copies");
        System.out.println("---------------------------------------------------------------------");
    }
}