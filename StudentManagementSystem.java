import java.util.*;
import java.io.*;

public class StudentManagementSystem {

 static Scanner input = new Scanner(System.in);

      // Files
     static final String STUDENTS_FILE = "students.txt";   // roll name
     static final String PASSWORDS_FILE = "passwords.txt"; // roll password
     static final String MARKS_FILE = "marks.txt";         // eng math sci
     static final String ATTENDANCE_FILE = "attendance.txt"; // single value per line
     static final String FEES_FILE = "fees.txt";           // fee status per line

    // Login credentials for admin (changeable)
     static String adminID = "admin";
     static String adminPassword = "adminpass";

    // Arrays (PF requirement)
    static final int MAX_STUDENTS = 500; // generous limit
    static int[] rollNumbers = new int[MAX_STUDENTS];
    static String[] names = new String[MAX_STUDENTS];
    static String[] studentPasswords = new String[MAX_STUDENTS]; // per-student password

    // Subject marks arrays
    static int[] english = new int[MAX_STUDENTS];
    static int[] maths = new int[MAX_STUDENTS];
    static int[] science = new int[MAX_STUDENTS];

    // Attendance and fee
    static int[] attendance = new int[MAX_STUDENTS]; // percentage
    static String[] feeStatus = new String[MAX_STUDENTS]; // "Paid" / "NotPaid"

    static int studentCount = 0;

    // Default password for new students
    static final String DEFAULT_STUDENT_PASSWORD = "student123";


    public static void main(String[] args) {

        mainMenu();
    }

    // ----------------- Main Menu -----------------
    static void mainMenu() {
        while (true) {
            System.out.println("\n======== STUDENT MANAGEMENT SYSTEM ========");
            System.out.println("1) Admin Login");
            System.out.println("2) Student Login");
            System.out.println("3) Exit");
            System.out.print("Enter choice: ");

            String raw = input.nextLine().trim();
            if (raw.isEmpty()) { System.out.println("Invalid choice!"); continue; }
            int choice;
            try { choice = Integer.parseInt(raw); }
            catch (Exception e) { System.out.println("Invalid choice!"); continue; }

            switch (choice) {
                case 1 : adminLogin();
                case 2 : studentLogin();
                case 3 : { System.out.println("Exiting system... Goodbye!"); return; }
                default : System.out.println("Invalid choice!");
            }
        }
    }

    // ----------------- Admin Login -----------------
    static void adminLogin() {
        System.out.print("Enter Admin ID: ");
        String u = input.nextLine().trim();
        System.out.print("Enter password: ");
        String p = input.nextLine().trim();

        if (u.equals(adminID) && p.equals(adminPassword)) {
            System.out.println("Login Successful!");
            // Load all data into arrays at admin login so admin works on latest data
            loadAllData();
            adminMenu();
        } else {
            System.out.println("Invalid Credentials!");
        }
    }

    // ----------------- Student Login (B2) -----------------
    static void studentLogin() {
        // load students & passwords so we can authenticate
       /*  loadStudentsFromFile();
        loadPasswordsFromFile();
        loadMarksFromFile(); // optional to show marks immediately
        loadAttendanceFromFile();
        loadFeesFromFile();*/

        System.out.print("Enter Roll Number: ");
        String line = input.nextLine().trim();
        int roll;
        try { roll = Integer.parseInt(line); }
        catch (Exception e) { System.out.println("Invalid roll number."); return; }

        int idx = getIndexByRoll(roll);
        if (idx == -1) {
            System.out.println("Roll number not found.");
            return;
        }

        System.out.print("Enter Password: ");
        String pass = input.nextLine().trim();

        String stored = studentPasswords[idx];
        if (stored == null) stored = ""; // safety
        if (pass.equals(stored)) {
            System.out.println("Login successful. Welcome, " + names[idx] + "!");
            studentMenu(idx);
        } else {
            System.out.println("Invalid password.");
        }
    }
 }


  // ----------------- Admin Menu (hierarchical) -----------------
    static void adminMenu() {
        while (true) {
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("1) Student Particulars");
            System.out.println("2) Result Management");
            System.out.println("3) Attendance Record");
            System.out.println("4) Fee Progression");
            System.out.println("5) Generate Report Card");
            System.out.println("6) Change Admin Password");
            System.out.println("7) Logout");
            System.out.print("Choose: ");

            String raw = input.nextLine().trim();
            int choice;
            try { choice = Integer.parseInt(raw); } catch (Exception e) { System.out.println("Invalid choice!"); continue; }

            switch (choice) {
                case 1 -> studentParticularsMenu();
                case 2 -> resultManagementMenu();
                case 3 -> attendanceMenu();
                case 4 -> feeMenu();
                case 5 -> generateReportCardMenu();
                case 6 -> changeAdminPassword();
                case 7 -> {
                    // save everything before logging out
                    saveAllData();
                    System.out.println("Logging out admin.");
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    // ----------------- Student Particulars Menu -----------------
    static void studentParticularsMenu() {
        while (true) {
            System.out.println("\n--- STUDENT PARTICULARS ---");
            System.out.println("1) Add Student");
            System.out.println("2) Edit Student");
            System.out.println("3) View All Students");
            System.out.println("4) Back");
            System.out.print("Choose: ");

            String raw = input.nextLine().trim();
            int choice;
            try { choice = Integer.parseInt(raw); } catch (Exception e) { System.out.println("Invalid choice!"); continue; }

            switch (choice) {
                case 1 -> { addStudentInteractive(); saveAllData(); }
                case 2 -> { editStudentInteractive(); saveAllData(); }
                case 3 -> { loadStudentsFromFile(); viewStudents(); }
                case 4 -> { return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    // Add student (interactive)
    static void addStudentInteractive() {
        loadStudentsFromFile(); // load existing to append correctly

        if (studentCount >= MAX_STUDENTS) { System.out.println("Student limit reached!"); return; }

        System.out.print("Enter Roll No: ");
        String rollLine = input.nextLine().trim();
        int roll;
        try { roll = Integer.parseInt(rollLine); }
        catch (Exception e) { System.out.println("Invalid roll number."); return; }

        // check no duplicate roll
        if (getIndexByRoll(roll) != -1) { System.out.println("Roll number already exists!"); return; }

        System.out.print("Enter Name (no spaces preferred): ");
        String name = input.nextLine().trim();
        if (name.isEmpty()) { System.out.println("Invalid name."); return; }

        // store in arrays at studentCount
        rollNumbers[studentCount] = roll;
        names[studentCount] = name;
        studentPasswords[studentCount] = DEFAULT_STUDENT_PASSWORD; // default password

        // initialize other fields
        english[studentCount] = 0;
        maths[studentCount] = 0;
        science[studentCount] = 0;
        attendance[studentCount] = 0;
        feeStatus[studentCount] = "NotPaid";

        studentCount++;

        System.out.println("Student added. Default password: " + DEFAULT_STUDENT_PASSWORD);
    }

    // Edit student name by roll
    static void editStudentInteractive() {
        loadStudentsFromFile();

        System.out.print("Enter Roll Number to edit: ");
        String rl = input.nextLine().trim();
        int roll;
        try { roll = Integer.parseInt(rl); } catch (Exception e) { System.out.println("Invalid roll."); return; }

        int idx = getIndexByRoll(roll);
        if (idx == -1) { System.out.println("Student not found!"); return; }

        System.out.println("Current name: " + names[idx]);
        System.out.print("Enter new name (no spaces preferred): ");
        String newName = input.nextLine().trim();
        if (newName.isEmpty()) { System.out.println("Invalid name."); return; }
        names[idx] = newName;
        System.out.println("Name updated.");
    }

    static void viewStudents() {
        if (studentCount == 0) { System.out.println("No students found."); return; }

        System.out.println("\n------ ALL STUDENT RECORDS ------");
        for (int i = 0; i < studentCount; i++) {
            System.out.println((i+1) + ") Roll: " + rollNumbers[i] + " | Name: " + names[i]);
        }
    }

    // ----------------- Result Management Menu (subject-wise option 2) -----------------
    static void resultManagementMenu() {
        while (true) {
            System.out.println("\n--- RESULT MANAGEMENT ---");
            System.out.println("1) Add Marks (subject-wise)");
            System.out.println("2) Update Marks (subject-wise)");
            System.out.println("3) View Marks (by roll)");
            System.out.println("4) View Grade (by roll)");
            System.out.println("5) Back");
            System.out.print("Choose: ");

            String raw = input.nextLine().trim();
            int ch;
            try { ch = Integer.parseInt(raw); } catch (Exception e) { System.out.println("Invalid choice!"); continue; }

            switch (ch) {
                case 1 -> { addMarksInteractive(); saveMarksToFile(); }
                case 2 -> { updateMarksInteractive(); saveMarksToFile(); }
                case 3 -> { loadMarksFromFile(); viewMarksAdmin(); }
                case 4 -> { loadMarksFromFile(); viewGradeAdmin(); }
                case 5 -> { return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    // choose subject helper
    static int chooseSubjectInteractive() {
        while (true) {
            System.out.println("\nSelect Subject:");
            System.out.println("1) English");
            System.out.println("2) Maths");
            System.out.println("3) Science");
            System.out.print("Choose: ");
            String raw = input.nextLine().trim();
            try {
                int s = Integer.parseInt(raw);
                if (s >=1 && s <=3) return s;
            } catch (Exception ignored) {}
            System.out.println("Invalid subject choice.");
        }
    }

    // add marks for one subject
    static void addMarksInteractive() {
        loadStudentsFromFile();
        loadMarksFromFile();

        System.out.print("Enter Roll Number: ");
        String rl = input.nextLine().trim();
        int roll;
        try { roll = Integer.parseInt(rl); } catch (Exception e) { System.out.println("Invalid roll number."); return; }

        int idx = getIndexByRoll(roll);
        if (idx == -1) { System.out.println("Student not found!"); return; }

        int sub = chooseSubjectInteractive();
        System.out.print("Enter Marks (0-100): ");
        String mline = input.nextLine().trim();
        int m;
        try { m = Integer.parseInt(mline); if (m<0||m>100) throw new Exception(); }
        catch (Exception e) { System.out.println("Invalid marks."); return; }

        switch (sub) {
            case 1 -> english[idx] = m;
            case 2 -> maths[idx] = m;
            case 3 -> science[idx] = m;
        }
        System.out.println("Marks saved for " + names[idx]);
    }

    // update marks (same UI)
    static void updateMarksInteractive() {
        addMarksInteractive(); // same flow works for update as well
    }

    // view marks for a student (by roll)
    static void viewMarksAdmin() {
        System.out.print("Enter Roll Number: ");
        String rl = input.nextLine().trim();
        int roll;
        try { roll = Integer.parseInt(rl); } catch (Exception e) { System.out.println("Invalid roll."); return; }

        int idx = getIndexByRoll(roll);
        if (idx == -1) { System.out.println("Student not found!"); return; }

        System.out.println("\n--- MARKS for " + names[idx] + " ---");
        System.out.println("English: " + english[idx]);
        System.out.println("Maths  : " + maths[idx]);
        System.out.println("Science: " + science[idx]);
    }

    // view grade for a student
    static void viewGradeAdmin() {
        System.out.print("Enter Roll Number: ");
        String rl = input.nextLine().trim();
        int roll;
        try { roll = Integer.parseInt(rl); } catch (Exception e) { System.out.println("Invalid roll."); return; }

        int idx = getIndexByRoll(roll);
        if (idx == -1) { System.out.println("Student not found!"); return; }

        int total = english[idx] + maths[idx] + science[idx];
        double percentage = total / 3.0;
        String grade;
        if (percentage >= 90) grade = "A+";
        else if (percentage >= 80) grade = "A";
        else if (percentage >= 70) grade = "B";
        else if (percentage >= 60) grade = "C";
        else if (percentage >= 50) grade = "D";
        else grade = "F";

        System.out.println("Percentage: " + String.format("%.2f", percentage) + "%");
        System.out.println("Grade: " + grade);
    }

    // ----------------- Attendance Menu -----------------
    static void attendanceMenu() {
        while (true) {
            System.out.println("\n--- ATTENDANCE RECORD ---");
            System.out.println("1) Mark Attendance (set percentage)");
            System.out.println("2) Update Attendance");
            System.out.println("3) View Attendance Percentage (by roll)");
            System.out.println("4) Back");
            System.out.print("Choose: ");

            String raw = input.nextLine().trim();
            int ch;
            try { ch = Integer.parseInt(raw); } catch (Exception e) { System.out.println("Invalid choice!"); continue; }

            switch (ch) {
                case 1 -> { markAttendanceInteractive(); saveAttendanceToFile(); }
                case 2 -> { markAttendanceInteractive(); saveAttendanceToFile(); }
                case 3 -> { loadAttendanceFromFile(); viewAttendanceAdmin(); }
                case 4 -> { return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    static void markAttendanceInteractive() {
        loadStudentsFromFile();
        loadAttendanceFromFile();

        System.out.print("Enter Roll Number: ");
        String rl = input.nextLine().trim();
        int roll;
        try { roll = Integer.parseInt(rl); } catch (Exception e) { System.out.println("Invalid roll."); return; }

        int idx = getIndexByRoll(roll);
        if (idx == -1) { System.out.println("Student not found!"); return; }

        System.out.print("Enter Attendance Percentage (0-100): ");
        String pline = input.nextLine().trim();
        int p;
        try { p = Integer.parseInt(pline); if (p < 0 || p > 100) throw new Exception(); }
        catch (Exception e) { System.out.println("Invalid percentage."); return; }

        attendance[idx] = p;
        System.out.println("Attendance updated for " + names[idx] + ": " + p + "%");
    }

    static void viewAttendanceAdmin() {
        System.out.print("Enter Roll Number: ");
        String rl = input.nextLine().trim();
        int roll;
        try { roll = Integer.parseInt(rl); } catch (Exception e) { System.out.println("Invalid roll."); return; }

        int idx = getIndexByRoll(roll);
        if (idx == -1) { System.out.println("Student not found!"); return; }

        System.out.println(names[idx] + " - Attendance: " + attendance[idx] + "%");
    }

    // ----------------- Fee Menu -----------------
    static void feeMenu() {
        while (true) {
            System.out.println("\n--- FEE PROGRESSION ---");
            System.out.println("1) View Fee Status (by roll)");
            System.out.println("2) Update Fee Status");
            System.out.println("3) View Scholarship Status (by roll)");
            System.out.println("4) Back");
            System.out.print("Choose: ");

            String raw = input.nextLine().trim();
            int ch;
            try { ch = Integer.parseInt(raw); } catch (Exception e) { System.out.println("Invalid choice!"); continue; }

            switch (ch) {
                case 1 -> { loadFeesFromFile(); viewFeeStatusAdmin(); }
                case 2 -> { updateFeeInteractive(); saveFeesToFile(); }
                case 3 -> { loadMarksFromFile(); viewScholarshipStatus(); }
                case 4 -> { return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    static void viewFeeStatusAdmin() {
        System.out.print("Enter Roll Number: ");
        String rl = input.nextLine().trim();
        int roll;
        try { roll = Integer.parseInt(rl); } catch (Exception e) { System.out.println("Invalid roll."); return; }

        int idx = getIndexByRoll(roll);
        if (idx == -1) { System.out.println("Student not found!"); return; }

        System.out.println(names[idx] + " - Fee Status: " + feeStatus[idx]);
    }

    static void updateFeeInteractive() {
        loadStudentsFromFile();
        loadFeesFromFile();

        System.out.print("Enter Roll Number: ");
        String rl = input.nextLine().trim();
        int roll;
        try { roll = Integer.parseInt(rl); } catch (Exception e) { System.out.println("Invalid roll."); return; }

        int idx = getIndexByRoll(roll);
        if (idx == -1) { System.out.println("Student not found!"); return; }

        System.out.print("Enter Fee Status (Paid/NotPaid): ");
        String status = input.nextLine().trim();
        if (!(status.equalsIgnoreCase("Paid") || status.equalsIgnoreCase("NotPaid"))) {
            System.out.println("Invalid status. Use Paid or NotPaid.");
            return;
        }
        feeStatus[idx] = status.equalsIgnoreCase("Paid") ? "Paid" : "NotPaid";
        System.out.println("Fee status updated for " + names[idx]);
    }

    static void viewScholarshipStatus() {
        System.out.print("Enter Roll Number: ");
        String rl = input.nextLine().trim();
        int roll;
        try { roll = Integer.parseInt(rl); } catch (Exception e) { System.out.println("Invalid roll."); return; }

        int idx = getIndexByRoll(roll);
        if (idx == -1) { System.out.println("Student not found!"); return; }

        int total = english[idx] + maths[idx] + science[idx];
        double percentage = total / 3.0;
        String status;
        if (percentage >= 85) status = "Eligible for Scholarship";
        else if (percentage >= 70) status = "Partial Scholarship";
        else status = "Not Eligible";
        System.out.println(names[idx] + " - " + status + " (" + String.format("%.2f", percentage) + "%)");
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

    // -------------------------------------------------------------------------
    // -------------------------------- SEARCH ----------------------------------
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // --------------------------- VALIDATION HELPERS ---------------------------
    // -------------------------------------------------------------------------

    // CHANGE: helper to safely parse integer input
    static int parseInt(String value) {
        try {
            int intChoice = Integer.parseInt(value);
            return intChoice;
        }catch(NumberFormatException e){
            System.out.println("Enter a valid numeric value.");
            return 0;
        } 
        catch (Exception e) {
            System.out.println("Invalid numeric input! Defaulting to 0.");
            return 0;
        }
    }


