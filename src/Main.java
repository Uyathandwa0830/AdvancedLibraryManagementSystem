import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.InputMismatchException;
import java.io.*;
import java.util.*;

 class NotificationSender extends Thread {
     // Time interval for sending notifications (in milliseconds)
    private static final long INTERVAL = 100000;//

    @Override
public void run() {
    while (!Thread.currentThread().isInterrupted()) {
        try {
            // Sleep for the specified interval
            Thread.sleep(INTERVAL);
            // Send notifications
            sendNotifications();
        } catch (InterruptedException e) {
            // Handle interruption by breaking out of the loop
            System.out.println("NotificationSender thread interrupted. Exiting...");
            break;
        }
    }
}
 // Method to send notifications to members
    private void sendNotifications() {
         // Get the current date
        LocalDate currentDate = LocalDate.now();
         // Print a message indicating the start of notification sending process

        System.out.println("Sending notifications for due and overdue books. ..");
         // Flag to track if any notifications are sent
        boolean notificationsSent = false;
         // Iterate through each member in the collection
        for (Member member : Main.MemberCollection) {
            // Check if the member has borrowed any books
            if (!member.booksBorrowed.isEmpty() ) {
                 //Initialize a string to construct the notification message

                StringBuilder notificationMessage = new StringBuilder();
                // Construct the greeting part of the message
                notificationMessage.append("Hello ").append(member.Name).append(",\n\n");
                // Add a section header for the book statuses
                notificationMessage.append("Status of your borrowed books:\n\n");
            // Flag to track if the member has any due or overdue books
                boolean hasDueOrOverdueBooks = false;
                // Iterate through each book borrowed by the member
                for (String bookTitle : member.booksBorrowed) {
                    // Get the book object using the title
                    Book book = getBookByTitle(bookTitle);
                    // Check if the book exists

                    if (book != null) {
                        // Check if the book is overdue
                        LocalDate dueDate = member.DueDate;
                        if (currentDate.isAfter(dueDate)){
                            // Add the book to the notification message with overdue status
                            notificationMessage.append("- ").append(bookTitle).append(" (Overdue)\n");
                            // Set the flag indicating the presence of due or overdue books
                            hasDueOrOverdueBooks = true;
                            // Check if the book is due within the next 7 days
                        } else if (currentDate.plusDays(7).isAfter(dueDate)) {
                             // Add the book to the notification message with due status and days left
                            notificationMessage.append("- ").append(bookTitle).append(" (Due in ").append(dueDate.until(currentDate).getDays()).append(" days)\n");
                             // Set the flag indicating the presence of due or overdue books
                            hasDueOrOverdueBooks = true;
                        } else {
                              // If the book is not overdue or due soon, add it to the notification message
                            notificationMessage.append("- ").append(bookTitle).append("\n");
                        }
                    }
                }
// If there are due or overdue books for the member, send the notification message
                if (hasDueOrOverdueBooks) {
                    // Print the notification message
                    notificationMessage.append("\nThank you .\n");
                    System.out.println("Notification sent to " + member.Name + ":\n" + notificationMessage);
                     // Update the flag to indicate that notifications are sent
                    notificationsSent = true;
                }
            }
        }
         // Check if any notifications were sent
        if (!notificationsSent) {
            // If no notifications were sent, print a message indicating no action required

            System.out.println("No notifications to send.");
        }
        System.out.println();
    }
    // Method to retrieve a book object by its title
    private Book getBookByTitle(String title) {
        // Iterate through the book collection to find the book with the specified title
        for (Book book : Main.BookCollection) {
            // Check if the title matches
            if (book.Title.equals(title)) {
                // Return the book object if found
                return book;
            }
        }
        // Return null if the book with the specified title is not found
        return null;
    }
}
/**
 * This class represents a task that periodically checks for overdue books
 * and updates fines accordingly.
 */
class OverdueFineTask extends Thread{
     // Time interval for checking overdue books (in milliseconds)
     private static final long time = 100000;
 /**
     * Run method of the thread that periodically checks for overdue books
     * and updates fines.
     */
    @Override
     public void run(){
        while(true){
            try {Thread.sleep(time);
               checkOverdueBooks();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

      private Book getBookByTitle(String title) {
       for (Book book : Main.BookCollection) {
        if (book.Title.equals(title)) {
            return book;
        }
       }
       return null;
    }
     /**
     * Checks for overdue books for each member in the main member collection
     * and updates fines accordingly.
     */

    private void checkOverdueBooks(){
        //Get current date
        LocalDate currentDate = LocalDate.now();
         // Print message indicating the start of the process
        System.out.println("Checking for overdue books and updating fines...");
         // Flag to track if any fines have been updated
        boolean finesUpdated = false;
         // Iterate through each member in the member collection
            for (Member member : Main.MemberCollection) {
                 // Initialize the total fine for the member
        double totalFine = 0.0;
         // Iterate through each book borrowed by the member
        for (String bookTitle : member.booksBorrowed) {
             // Get the book object corresponding to the book title
        Book book = getBookByTitle(bookTitle);
        //if the book exists
        if (book != null) {
              // Get the due date of the book for the member
            LocalDate dueDate = member.DueDate;
             // Check if the current date is after the due date
            if (currentDate.isAfter(dueDate)) {
                  // Calculate the overdue fine for the book
                double fineAmount = Main.calculateOverdue();
                // Add the fine amount to the total fine for the member
                totalFine += fineAmount;
                System.out.println("Member: " + member.Name + ", Book: " + bookTitle + ", Overdue fine: $" + fineAmount);
                finesUpdated = true;
            }
        }
     }
        // If the total fine for the member is greater than 0, print the total overdue fine
            if (totalFine > 0) {
                // Print a message indicating the member, book, and overdue fine
                System.out.println("Total overdue fine for " + member.Name + ": $" + totalFine);
            }
        }

            //If no fines are updated,print a message indication so
        if (!finesUpdated) {
            System.out.println("No overdue fines to update.");
        }
        //print an empty line for formatting
        System.out.println();

    }

}
class LibraryData implements Serializable {
    public List<Book> bookCollection;
    public List<Member> memberCollection;
    public List<Transaction> transactionList;

    public LibraryData() {
        this.bookCollection = new ArrayList<>();
        this.memberCollection = new ArrayList<>();
        this.transactionList = new ArrayList<>();
    }

    public static class Transaction implements Serializable {
        public String memberName;
        public String bookTitle;
        public LocalDate transactionDate;


        public Transaction(String memberName, String bookTitle, LocalDate transactionDate, TransactionType transactionType) {
            this.memberName = memberName;
            this.bookTitle = bookTitle;
            this.transactionDate = transactionDate;

        }
    }

    public enum TransactionType implements Serializable {
        CHECKOUT,
        RETURN
    }

    private static final String DATA_FILE = "library_data.ser";

    public static void saveLibraryData(LibraryData libraryData) {
        try {

            FileOutputStream fileOut = new FileOutputStream(DATA_FILE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(libraryData);
            out.close();
            fileOut.close();
            System.out.println("Library data saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LibraryData loadLibraryData() {
        LibraryData libraryData = null;
        try {
            FileInputStream fileIn = new FileInputStream(DATA_FILE);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            libraryData = (LibraryData) in.readObject();
            in.close();
            fileIn.close();
            System.out.println("Library data loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No saved data found. Creating new library data.");
            libraryData = new LibraryData();
        }
        return libraryData;
    }


}


/**
 * The Main class serves as the entry point of the application and contains methods
 * for adding books to the collection and searching for books by title.
 */
public class Main {
    private static final Object BORROW = null;
     // Collection to store books
    static ArrayList<Book> BookCollection = new ArrayList<Book>();
     // Collection to store members
    static ArrayList<Member> MemberCollection = new ArrayList<Member>();

          static {
        // Initialize and add books to the BookCollection
        Book book1 = new Book("Things I Never Said To Myself", "Duduzile Noeleen Ngwenya", 978063972023L);
        Book book2 = new Book("All Hope Is Found", "Sarah Jakes", 9781400339877L);
        Book book3 = new Book("Hamlet", "William Shakespeare", 9780140707342L);
        Book book4 = new Book("It Ends With Us", "Collen Hover", 9781501110368L);
        Book book5 = new Book("The Alchemist", "Paulo Coelho", 9780694524440L);
        BookCollection.add(book1);
        BookCollection.add(book2);
        BookCollection.add(book3);
        BookCollection.add(book4);
        BookCollection.add(book5);

        // Initialize and add members to the MemberCollection
        Member member1 = new Member("Duduzile Noeleen Ngwenya", "noeleen@example.com");
        Member member2 = new Member("Sarah Jakes", "sarah@example.com");
        Member member3 = new Member("William Shakespeare", "willian@example.com");
        Member member4 = new Member("Collen Hover", "collen@example.com");
        Member member5 = new Member("Paulo Coelho", "paulo@example.com");
        MemberCollection.add(member1);
        MemberCollection.add(member2);
        MemberCollection.add(member3);
        MemberCollection.add(member4);
        MemberCollection.add(member5);
    }
    // Create book objects and add them to the BookCollection

        // Create an ArrayList to hold the library members

    /**
     * Method to add a new book to the collection.
     */

    public static void AddBook() {
        System.out.println("Enter Book Title: ");  // Prompt user to enter book details
        Scanner Title = new Scanner(System.in);
        String title = Title.nextLine();
        System.out.println("Enter Author Name: ");
        Scanner Author = new Scanner(System.in);
        String author = Author.nextLine();
        System.out.println("Enter ISBN: ");
        Scanner ISBN = new Scanner(System.in);
        Long isbn = ISBN.nextLong();
        Book books = new Book(title, author, isbn); // Create a new Book object with the entered details
        BookCollection.add(books);
        System.out.println(title + " by " + author + " has been added");     // Print a confirmation message
    }
/**
     * Method to search for a book by its title.
     */
    public static void searchBar() {
        //Array of sample book titles
        String[] books = new String[]{"Things I Never Said To Myself", "All Hope Is Found", "Hamlet", "It Ends With Us", "The Alchemist"};
        Scanner scanner = new Scanner(System.in);  // Prompt user to enter the title of the book to search
        System.out.println("Enter the title of the book to search:");
        String searchTitle = scanner.nextLine();
 // Call the overloaded searchBar method to perform the search
        int index = searchBar(books, searchTitle);
         // Print the result of the search
        if (index != -1) {
            System.out.println("Book found at index ");
        } else {
            System.out.println("Book not found.");
        }

    }

    private static int searchBar(String[] books, String searchTitle) {
        return 0;
    }

    public static boolean isValidEmail(String email) {
         // Regular expression for validating email format
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
           // Compile the regex pattern
        Pattern pattern = Pattern.compile(emailRegex);
           // Create matcher for input email against regex pattern
        Matcher matcher = pattern.matcher(email);
            // Return whether the email matches the regex pattern
        return matcher.matches();
    }

    public static void AddMember() {
         // Method to add a new member
        System.out.println("Enter Member Name: ");
          // Input member's name
        Scanner MemberName = new Scanner(System.in);
        String name = MemberName.nextLine();
        String email = "";
        boolean Valid = false;

        while (!Valid) {
            // Validate member's email
            System.out.println("Enter Member Email: ");
            Scanner Email = new Scanner(System.in);
            email = Email.nextLine();
            Valid = isValidEmail(email);
            if (!Valid) {
                // Print error message for invalid email format
                System.out.println("Invalid email");
            }
        }
// Create new Member object with provided name and email
        new Member(name, email);
    }

    public static void DisplayBooks() {
         // Method to display all books in the BookCollection
        int counter = 1;
        for (Book book : BookCollection) {
            // Iterate through each book and display its title and author
            System.out.println(counter + "." + book.Title + " by " + book.Author);
            counter = counter + 1;
        }
    }

    public static void BookCheckout() {
         // Method to handle the book checkout process
        System.out.println("Available Books:");
         // Display available books
        DisplayBooks();
        System.out.println("Enter the number of the book you want to checkout:");
         // Prompt user for book selection
        Scanner inputScanner = new Scanner(System.in);
        try {
            int choice = inputScanner.nextInt();
            // Validate user's book selection
            if (choice < 1 || choice > BookCollection.size()) {
                System.out.println("Invalid book selection. Please try again.");
                return;
            }
               // Get the selected book
            Book selectedBook = BookCollection.get(choice - 1);
            if (selectedBook.Availability) {
                System.out.println("Enter your member email:");
                String memberEmail = inputScanner.next();
                  // Find the member with the provided email
                Member member = null;
                LocalDate currentDate = LocalDate.now();
                for (Member m : MemberCollection) {
                    //check if member is found

                    if (m.Email.equals(memberEmail)) {
                        member = m;
                        // Add the book to the member's borrowed books
                        member.DueDate = currentDate.plusDays(14);
                        break;
                    }
                }
                // adding book
                if (member != null) {
                    member.booksBorrowed.add(selectedBook.Title);
                    selectedBook.Availability = false;
                                    // Print checkout details

                    System.out.println("Book checked out successfully for " + member.Name + " on " + currentDate);
                    System.out.println("Return the book by " + member.DueDate);

                }
            } else {
                // Inform user that the selected book is unavailable
                System.out.println("Sorry, the  book you selected is unavailable.");
            }
        } catch (InputMismatchException e) {
              // Handle invalid input
            System.out.println("Invalid input. Please enter a valid number.");
            inputScanner.nextLine(); // Clear the input
        }

    }

    public static double calculateOverdue() {
        // Method to calculate overdue fine amount
    // Define the fine rate per day (in currency units)
        final double fineRate = 0.5;

         // Get the current date
        LocalDate currentDate = LocalDate.now();
         // Placeholder for the due date, needs to be set based on the borrowed item's due date
        Temporal dueDate = null;
        // Calculate the number of days overdue
    // If due date is in the past, overdueDays will be positive, otherwise, it will be 0
        long overdueDays = ChronoUnit.DAYS.between(dueDate, currentDate);
        // Calculate the fine amount based on overdue days and fine rate,
    // Fine amount is the product of overdue days and fine rate, capped at 0 if there are no overdue days

        double fineAmount = Math.max(0, overdueDays) * fineRate;

        return fineAmount;

    }

    public static void saveBookCollection() {
        try (FileOutputStream fos = new FileOutputStream("books.ser");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(BookCollection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the member collection to a file called "members.txt" using streams.
     */
    public static void saveMemberCollection() {
        try (FileOutputStream fos = new FileOutputStream("members.ser");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(MemberCollection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



   public static void main(String[] args) {


         // Load library data
           LibraryData libraryData = new LibraryData();
            LibraryData.loadLibraryData();

            // Set up initial book and member collections if data is empty
            if (libraryData.bookCollection.isEmpty()) {Book book1 = new Book("Things I Never Said To Myself", "Duduzile Noeleen Ngwenya", 978063972023L);
             Book book2 = new Book("All Hope Is Found", "Sarah Jakes", 9781400339877L);
             Book book3 = new Book("Hamlet", "William Shakespeare", 9780140707342L);
             Book book4 = new Book("It Ends With Us", "Collen Hover", 9781501110368L);
             Book book5 = new Book("The Alchemist", "Paulo Coelho", 9780694524440L);
             libraryData.bookCollection.add(book1);
              libraryData.bookCollection.add(book2);
             libraryData.bookCollection.add(book3);
             libraryData.bookCollection.add(book4);
             libraryData.bookCollection.add(book5);
             }
              if (libraryData.memberCollection.isEmpty()) {
            Member member1 = new Member("Duduzile Noeleen Ngwenya", "noeleen@example.com");
            Member member2 = new Member("Sarah Jakes", "sarah@example.com");
            Member member3 = new Member("William Shakespeare", "willian@example.com");
            Member member4 = new Member("Collen Hover", "collen@example.com");
            Member member5 = new Member("Paulo Coelho", "paulo@example.com");
            libraryData.memberCollection.add(member1);
            libraryData.memberCollection.add(member2);
            libraryData.memberCollection.add(member3);
           libraryData.memberCollection.add(member4);
            libraryData.memberCollection.add(member5);
            }
    // Start the OverdueFineTask thread
    OverdueFineTask overdueFineTask = new OverdueFineTask();
    overdueFineTask.start();

    // Start the NotificationSender thread
    NotificationSender notificationSender = new NotificationSender();
    notificationSender.start();
    notificationSender.interrupt();




    // Display library menu options
  System.out.println("\nWELCOME TO KINDNESS ROCKS LIBRARY");
System.out.println("1. ADD A BOOK");
System.out.println("2. SEARCH FOR A BOOK");
System.out.println("3. REGISTER A NEW MEMBER");
System.out.println("4. RETURN A BOOK");
System.out.println("5. BOOK CHECKOUT");
System.out.println("6. DISPLAY BOOKS");
System.out.println("7. CHECK DUE DATES");
System.out.println("8. VIEW FINES");
System.out.println("9. MANAGE NOTIFICATIONS");
System.out.println("10. VIEW TRANSACTION RECORDS");
System.out.println("11. EXIT");
System.out.print("Choose an option: ");


    // Handle user input based on menu options
    Scanner userInput = new Scanner(System.in);
    switch (userInput.nextLine()) {
        case "1" -> AddBook();
        case "2" -> searchBar();
        case "3" -> AddMember();
        case "4" -> AddBook();
        case "5" -> BookCheckout();
        case "6" -> DisplayBooks();
        case "7" -> checkDueDates();
        case "8" -> viewFines();
        case "9" -> manageNotifications();
        case "10" -> viewTransactionRecords();

    }
    main(args);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    LibraryData.saveLibraryData(libraryData);
                }));
}

    public static void manageNotifications() {
    System.out.println("Manage Notifications:");
    System.out.println("1. Enable Notifications");
    System.out.println("2. Disable Notifications");
    System.out.print("Choose an option: ");
    Scanner userInput = new Scanner(System.in);
    String option = userInput.nextLine();
       NotificationSender notificationSender = new NotificationSender();
    switch (option) {
        case "1":
            // Enable notifications
            notificationSender.start();
            System.out.println("Notifications enabled.");
            break;
        case "2":
            // Disable notificationnotificationSender
            // Interrupt the notification thread
            notificationSender.interrupt();
            System.out.println("Notifications disabled.");
            break;
        default:
            System.out.println("Invalid option. Please try again.");
            break;
    }
}

public static void viewFines() {
        //Method to view fines for all member
    System.out.println("Viewing Fines:");
    for (Member member : MemberCollection) {
               // Iterate through each member in the collecti
        double totalFine = calculateTotalFineForMember(member);
        // Calculate total fine for the member
        if (totalFine > 0) {
              // Check if member has fines
            System.out.println("Member: " + member.Name + ", Total Fine: $" + totalFine);
        }
        else {
            System.out.println("no fines available");
        }
    }
}
   // Method to calculate total fine for a member
public static double calculateTotalFineForMember(Member member) {
    double totalFine = 0.0;
    LocalDate currentDate = LocalDate.now();
    // Iterate through books borrowed by the member
    for (String bookTitle : member.booksBorrowed) {
        Book book = getBookByTitle(bookTitle);
           // Check if book exists
        if (book != null) {
            LocalDate dueDate = member.DueDate;
                // Check if current date is after due date
            if (currentDate.isAfter(dueDate)) {
                 // Calculate overdue fine and add to total fine
                totalFine += calculateOverdue();
            }
        }
    }
    return totalFine;
}

  // Method to get book by title
public static Book getBookByTitle(String title) {
           // Iterate through book collection to find book by title
    for (Book book : BookCollection) {
        if (book.Title.equalsIgnoreCase(title)) { // Return book if found
            return book;
        }
    }
    return null;
     // Return null if book not found
}
    // Method to check due dates for all members
public static void checkDueDates() {
         // Iterate through each member in the collection
    System.out.println("Checking Due Dates:");
    for (Member member : MemberCollection) {
             // Iterate through books borrowed by the member
        System.out.println("Member: " + member.Name);
        for (String bookTitle : member.booksBorrowed) {
            Book book = getBookByTitle(bookTitle);
                // Check if book exists
            if (book != null) {
                System.out.println("Book: " + book.Title + ", Due Date: " + member.DueDate);
            }
        }
        System.out.println();
    }
}
// Method to view transaction records
public static void viewTransactionRecords() {
             // Create instance of LibraryData to access transaction list
    System.out.println("Viewing Transaction Records:");
      // Iterate through each transaction in the list
       LibraryData libraryData = new LibraryData();
            // Print transaction details
    for (LibraryData.Transaction transaction : libraryData.transactionList) {
        System.out.println("Member: " + transaction.memberName +
                ", Book: " + transaction.bookTitle +
                ", Transaction Date: " + transaction.transactionDate);

    }
}




}






