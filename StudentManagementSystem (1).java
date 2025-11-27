import java.util.Scanner;

public class StudentManagementSystem {

    private static final String STUDENT_ID = "12345";
    private static final String STUDENT_PASSWORD = "studentpass";
    private static final String ADMIN_ID = "admin";
    private static final String ADMIN_PASSWORD = "adminpass";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("........Welcome to the Student Management System........");
        System.out.println("1. Student Login");
        System.out.println("2. Admin Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option (1, 2, or 3): ");

        int choice = scanner.nextInt();
        scanner.nextLine(); 

        switch (choice) {
            case 1:
                studentLogin(scanner);
                break;
            case 2:
                adminLogin(scanner);
                break;
            case 3:
                System.out.println("Exiting...");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice. Exiting...");
                System.exit(0);
        }

        scanner.close();
    }

    private static void studentLogin(Scanner scanner) {
        boolean loggedIn = false;
        while (!loggedIn) {
            System.out.print("Enter Student ID: ");
            String id = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            if (STUDENT_ID.equals(id) && STUDENT_PASSWORD.equals(password)) {
                System.out.println("Student login successful. Continuing to the program...");
                loggedIn = true;
                // Here you can add code to proceed to the student menu or main functionality
            } else {
                System.out.println("Invalid ID or password. Try again.");
            }
        }
    }

    private static void adminLogin(Scanner scanner) {
        boolean loggedIn = false;
        while (!loggedIn) {
            System.out.print("Enter Admin ID: ");
            String id = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            if (ADMIN_ID.equals(id) && ADMIN_PASSWORD.equals(password)) {
                System.out.println("Admin login successful. Continuing to the program...");
                loggedIn = true;
                // Here you can add code to proceed to the admin menu or main functionality
            } else {
                System.out.println("Invalid ID or password. Try again.");
            }
        }
    }
}
