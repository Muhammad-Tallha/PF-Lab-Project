import java.util.Scanner;
import java.io.*;
public class StudentManagementSystem {

    static Scanner input = new Scanner(System.in);

    // Login credentials (now changeable)
    private static String adminID = "admin";
    private static String adminPassword = "adminpass";

    private static String studentID = "12345";
    private static String studentPassword = "studentpass";

    // Student data arrays for storage later on.(next week)
    static int[] rollNumbers = new int[50];
    static String[] names = new String[50];
    static int[] marks = new int[50];
    static int[] attendance = new int[50];
    static String[] feeStatus = new String[50];

    static int studentCount = 0;

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

    // ADMIN LOGIN
    static void adminLogin() {
        System.out.print("Enter Admin ID: ");
      
        String u = input.next();
        System.out.print("Enter password: ");
       
        String p = input.next();

        if (u.equals(adminID) && p.equals(adminPassword)) {
            System.out.println("Login Successful!");
            adminMenu();
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
            studentMenu();
        } else {
            System.out.println("Invalid Login!");
        }
    }

    // ---------------- ADMIN MENU (UPDATED) ----------------
    static void adminMenu() {
        while (true) {
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("1) Result Management");
            System.out.println("2) Student Particulars");
            System.out.println("3) Attendance Record");
            System.out.println("4) Fee Progression");
            System.out.println("5) Generate Report Card");
            System.out.println("6) Change Admin Password");
            System.out.println("7) Logout");
            System.out.print("Choose: ");

            // sc.nextInt() -> input.nextInt()
            int choice = input.nextInt();

            switch (choice) {
                case 1 -> resultManagementMenu();
                case 2 -> studentParticularsMenu();
                case 3 -> attendanceMenu();
                case 4 -> feeMenu();
                case 5 -> generateReport();
                case 6 -> changeAdminPassword();
                case 7 -> { return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    // ---------------- RESULT MANAGEMENT MENU ----------------
    static void resultManagementMenu() {
        while (true) {
            System.out.println("\n--- RESULT MANAGEMENT ---");
            System.out.println("1) Add Marks");
            System.out.println("2) Update Marks");
            System.out.println("3) View Marks");
            System.out.println("4) View Grade");
            System.out.println("5) Back");
            System.out.print("Choose: ");

            // sc.nextInt() -> input.nextInt()
            int choice = input.nextInt();

            switch (choice) {
                case 1 -> updateMarks();
                case 2 -> updateMarks();
                case 3 -> viewMarksAdmin();
                case 4 -> viewGradeAdmin();
                case 5 -> { return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    static void viewMarksAdmin() {
        System.out.print("Enter Roll Number: ");
     
        int roll = input.nextInt();
        int idx = searchStudent(roll);

        if (idx == -1)
            System.out.println("Student not found!");
        else
            System.out.println("Marks: " + marks[idx]);
    }

    static void viewGradeAdmin() {
        System.out.print("Enter Roll Number: ");
    
        int roll = input.nextInt();
        int idx = searchStudent(roll);

        if (idx == -1) {
            System.out.println("Student not found!");
            return;
        }

        int m = marks[idx];
        String grade = (m >= 90) ? "A+" :
                         (m >= 80) ? "A" :
                         (m >= 70) ? "B" :
                         (m >= 60) ? "C" :
                         (m >= 50) ? "D" : "F";

        System.out.println("Grade: " + grade);
    }

    // ---------------- STUDENT PARTICULARS MENU ----------------
    static void studentParticularsMenu() {
        while (true) {
            System.out.println("\n--- STUDENT PARTICULARS ---");
            System.out.println("1) Add Student");
            System.out.println("2) Edit Student Info");
            System.out.println("3) View All Students");
            System.out.println("4) Back");
            System.out.print("Choose: ");

      
            int choice = input.nextInt();

            switch (choice) {
                case 1 -> addStudent();
                case 2 -> editStudent();
                case 3 -> viewStudents();
                case 4 -> { return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    // ---------------- ATTENDANCE MENU ----------------
    static void attendanceMenu() {
        while (true) {
            System.out.println("\n--- ATTENDANCE RECORD ---");
            System.out.println("1) Mark Attendance");
            System.out.println("2) Update Attendance");
            System.out.println("3) View Attendance Percentage");
            System.out.println("4) Back");
            System.out.print("Choose: ");

          
            int choice = input.nextInt();

            switch (choice) {
                case 1 -> updateAttendance();
                case 2 -> updateAttendance();
                case 3 -> viewAttendanceAdmin();
                case 4 -> { return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    static void viewAttendanceAdmin() {
        System.out.print("Enter Roll Number: ");
    
        int roll = input.nextInt();
        int idx = searchStudent(roll);

        if (idx == -1)
            System.out.println("Student not found!");
        else
            System.out.println("Attendance: " + attendance[idx] + "%");
    }

    // ---------------- FEE PROGRESSION MENU ----------------
    static void feeMenu() {
        while (true) {
            System.out.println("\n--- FEE PROGRESSION ---");
            System.out.println("1) View Fee Status");
            System.out.println("2) Update Fee Status");
            System.out.println("3) View Scholarship Status");
            System.out.println("4) Back");
            System.out.print("Choose: ");

            int choice = input.nextInt();

            switch (choice) {
                case 1 -> viewFeeStatusAdmin();
                case 2 -> updateFee();
                case 3 -> viewScholarshipStatus();
                case 4 -> { return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    static void viewFeeStatusAdmin() {
        System.out.print("Enter Roll Number: ");
    
        int roll = input.nextInt();
        int idx = searchStudent(roll);

        if (idx == -1)
            System.out.println("Student not found!");
        else
            System.out.println("Fee Status: " + feeStatus[idx]);
    }

    static void viewScholarshipStatus() {
        System.out.print("Enter Roll Number: ");
    
        int roll = input.nextInt();
        int idx = searchStudent(roll);

        if (idx == -1) {
            System.out.println("Student not found!");
            return;
        }

        int m = marks[idx];
        String status = (m >= 85) ? "Eligible for Scholarship" :
                         (m >= 70) ? "Partial Scholarship" :
                         "Not Eligible";

        System.out.println("Scholarship Status: " + status);
    }

    // ---------------- STUDENT MENU ----------------
    static void studentMenu() {
        while (true) {
            System.out.println("\n====== STUDENT MENU ======");
            System.out.println("1) View Profile");
            System.out.println("2) View Marks");
            System.out.println("3) View Attendance");
            System.out.println("4) View Fee Status");
            System.out.println("5) Download Report Card");
            System.out.println("6) Change Password");
            System.out.println("7) Logout");
            System.out.print("Choose: ");

        
            int choice = input.nextInt();

            switch (choice) {
                case 1 -> viewProfile();
                // Assumes the currently logged-in student is at index 0 for now based on studentLogin logic
                case 2 -> System.out.println("Marks: " + marks[0]);
                case 3 -> System.out.println("Attendance: " + attendance[0] + "%");
                case 4 -> System.out.println("Fee Status: " + feeStatus[0]);
                case 5 -> System.out.println("Report Downloaded (placeholder).");
                case 6 -> changeStudentPassword();
                case 7 -> { return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    // ---------------- BASIC FUNCTIONS ----------------

    static void addStudent() {
        System.out.print("Enter Roll No: ");
   
        rollNumbers[studentCount] = input.nextInt();

        System.out.print("Enter Name: ");
        
        names[studentCount] = input.next();

        marks[studentCount] = 0;
        attendance[studentCount] = 0;
        feeStatus[studentCount] = "Not Paid";

        studentCount++;
        System.out.println("Student Added Successfully!");
    }

    static void viewStudents() {
        System.out.println("\n------ ALL STUDENT RECORDS ------");
        for (int i = 0; i < studentCount; i++) {
            System.out.println(rollNumbers[i] + " - " + names[i]);
        }
    }

    static void editStudent() {
        System.out.print("Enter Roll Number: ");
      
        int roll = input.nextInt();
        int idx = searchStudent(roll);

        if (idx == -1) {
            System.out.println("Student not found!");
            return;
        }

        System.out.print("Enter new name: ");
        names[idx] = input.next();
        System.out.println("Student updated!");
    }

    static void updateMarks() {
        System.out.print("Enter Roll Number: ");
       
        int roll = input.nextInt();
        int idx = searchStudent(roll);

        if (idx == -1) {
             System.out.println("Student not found!");
             return;
        }

        System.out.print("Enter Marks: ");
        
        marks[idx] = input.nextInt();

        System.out.println("Marks Updated!");
    }

    static void updateAttendance() {
        System.out.print("Enter Roll Number: ");
       
        int roll = input.nextInt();
        int idx = searchStudent(roll);

        if (idx == -1) {
             System.out.println("Student not found!");
             return;
        }

        System.out.print("Enter Attendance Percentage: ");
       
        
        attendance[idx] = input.nextInt();

        System.out.println("Attendance Updated!");
    }

    static void updateFee() {
        System.out.print("Enter Roll Number: ");
       
        int roll = input.nextInt();
        int idx = searchStudent(roll);

        if (idx == -1) {
             System.out.println("Student not found!");
             return;
        }

        System.out.print("Enter Fee Status (Paid/NotPaid): ");
       
        feeStatus[idx] = input.next();

        System.out.println("Fee Status Updated!");
    }

    static void generateReport() {
        System.out.println("Report Card Generated (placeholder).");
    }

    // ---------------- CHANGE PASSWORDS ----------------

    static void changeAdminPassword() {
        System.out.print("Enter current admin password: ");
      
        String current = input.next();

        if (!current.equals(adminPassword)) {
            System.out.println("Incorrect current password!");
            return;
        }

        System.out.print("Enter new password: ");
        
        String newPass = input.next();

        System.out.print("Confirm new password: ");
        
        String confirm = input.next();

        if (newPass.equals(confirm)) {
            adminPassword = newPass;
            System.out.println("Admin password updated successfully!");
        } else {
            System.out.println("Passwords do not match.");
        }
    }

    static void changeStudentPassword() {
        System.out.print("Enter current password: ");
        
        String current = input.next();

        if (!current.equals(studentPassword)) {
            System.out.println("Incorrect password!");
            return;
        }

        System.out.print("Enter new password: ");
     
        String newPass = input.next();

        System.out.print("Confirm new password: ");
     
        String confirm = input.next();

        if (newPass.equals(confirm)) {
            studentPassword = newPass;
            System.out.println("Student password updated successfully!");
        } else {
            System.out.println("Passwords do not match.");
        }
    }

    //SEARCH STUDENT
    static int searchStudent(int roll) {
        for (int i = 0; i < studentCount; i++) {
            if (rollNumbers[i] == roll)
                return i;
        }
        return -1;
    }

    static void viewProfile() {
        System.out.println("----- Profile -----");
        System.out.println("Student ID: " + studentID);
        System.out.println("Name: " + names[0]);
    }
}

