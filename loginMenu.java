import java.util.Scanner;
public class loginMenu {
    static Scanner input = new Scanner (System.in);
    static String studentID = "12345";
    static String studentPassword = "studentpass";
    static String adminID = "admin";
    static String adminPassword = "adminpass";

    public static void main(String[] args) {
       while (true) {
            System.out.println("\n======== STUDENT MANAGEMENT SYSTEM ========");
            System.out.println("1) Admin Login");
            System.out.println("2) Student Login");
            System.out.println("3) Exit");
            System.out.print("Enter choice: ");
        
            int choice = input.nextInt();

            switch (choice) {
                case 1 -> adminLogin();
                case 2 -> studentLogin();
                case 3 -> {
                    System.out.println("Exiting system... Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    // ---------------- ADMIN LOGIN ----------------
    static void adminLogin() {
        System.out.print("Enter Admin ID: ");
      
        String userName = input.next();
        System.out.print("Enter password: ");
       
        String passWord = input.next();

        if (userName.equals(adminID) && passWord.equals(adminPassword)) {
            System.out.println("Login Successful!");

        } else {
            System.out.println("Invalid Credentials!");
        }
    }

    // ---------------- STUDENT LOGIN ----------------
    static void studentLogin() {
        System.out.print("Enter Student ID: ");
        // sc.next() -> input.next()
        String id = input.next();
        System.out.print("Enter Password: ");
        // sc.next() -> input.next()
        String pass = input.next();

        if (id.equals(studentID) && pass.equals(studentPassword)) {
      ;
        } else {
            System.out.println("Invalid Login!");
        }
    }
}