import java.util.*;
import java.io.*;

public class StudentManagementSystem {

 static Scanner input = new Scanner(System.in);

      // Files
    private static final String STUDENTS_FILE = "students.txt";   // roll name
    private static final String PASSWORDS_FILE = "passwords.txt"; // roll password
    private static final String MARKS_FILE = "marks.txt";         // eng math sci
    private static final String ATTENDANCE_FILE = "attendance.txt"; // single value per line
    private static final String FEES_FILE = "fees.txt";           // fee status per line

    // Login credentials for admin (changeable)
    private static String adminID = "admin";
    private static String adminPassword = "adminpass";

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
        ensureFilesExist();
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
                case 1 -> adminLogin();
                case 2 -> studentLogin();
                case 3 -> { System.out.println("Exiting system... Goodbye!"); return; }
                default -> System.out.println("Invalid choice!");
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
        loadStudentsFromFile();
        loadPasswordsFromFile();
        loadMarksFromFile(); // optional to show marks immediately
        loadAttendanceFromFile();
        loadFeesFromFile();

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

    // ----------------- Report Card generation -----------------
    static void generateReportCardMenu() {
        System.out.print("Enter Roll Number to generate report: ");
        String rl = input.nextLine().trim();
        int roll;
        try { roll = Integer.parseInt(rl); } catch (Exception e) { System.out.println("Invalid roll."); return; }

        loadAllData(); // ensure latest
        int idx = getIndexByRoll(roll);
        if (idx == -1) { System.out.println("Student not found."); return; }

        generateReportForIndex(idx);
        System.out.println("Report generated: report_" + roll + ".txt");
    }

    static void generateReportForIndex(int idx) {
        String filename = "report_" + rollNumbers[idx] + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("========== REPORT CARD ==========");
            pw.println("Name : " + names[idx]);
            pw.println("Roll : " + rollNumbers[idx]);
            pw.println();
            pw.println("Marks:");
            pw.println("  English : " + english[idx]);
            pw.println("  Maths   : " + maths[idx]);
            pw.println("  Science : " + science[idx]);
            int total = english[idx] + maths[idx] + science[idx];
            double percent = total / 3.0;
            pw.println();
            pw.println("Total      : " + total);
            pw.println("Percentage : " + String.format("%.2f", percent) + "%");
            String grade = (percent >= 90) ? "A+" :
                           (percent >= 80) ? "A" :
                           (percent >= 70) ? "B" :
                           (percent >= 60) ? "C" :
                           (percent >= 50) ? "D" : "F";
            pw.println("Grade      : " + grade);
            pw.println();
            pw.println("Attendance : " + attendance[idx] + "%");
            String scholarship = (percent >= 85) ? "Full Scholarship" :
                                 (percent >= 70) ? "Partial Scholarship" : "No Scholarship";
            pw.println("Scholarship: " + scholarship);
            pw.println("Fee Status : " + feeStatus[idx]);
            pw.println("==================================");
        } catch (Exception e) {
            System.out.println("Error writing report file: " + e.getMessage());
        }
    }

    // ----------------- Change admin password -----------------
    static void changeAdminPassword() {
        System.out.print("Enter current admin password: ");
        String current = input.nextLine().trim();
        if (!current.equals(adminPassword)) {
            System.out.println("Incorrect current password!");
            return;
        }
        System.out.print("Enter new password: ");
        String newPass = input.nextLine().trim();
        System.out.print("Confirm new password: ");
        String confirm = input.nextLine().trim();
        if (newPass.equals(confirm)) {
            adminPassword = newPass;
            System.out.println("Admin password updated successfully!");
        } else System.out.println("Passwords do not match.");
    }

    // ----------------- Student Menu (after successful login) -----------------
    static void studentMenu(int idx) {
        // idx is the student index in arrays
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

            String raw = input.nextLine().trim();
            int choice;
            try { choice = Integer.parseInt(raw); } catch (Exception e) { System.out.println("Invalid choice!"); continue; }

            switch (choice) {
                case 1 -> viewProfileForIndex(idx);
                case 2 -> viewMarksForIndex(idx);
                case 3 -> System.out.println("Attendance: " + attendance[idx] + "%");
                case 4 -> System.out.println("Fee Status: " + feeStatus[idx]);
                case 5 -> { generateReportForIndex(idx); System.out.println("Report generated."); }
                case 6 -> { changeStudentPasswordInteractive(idx); savePasswordsToFile(); }
                case 7 -> { System.out.println("Logging out student."); return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    static void viewProfileForIndex(int idx) {
        System.out.println("----- Profile -----");
        System.out.println("Student ID: " + rollNumbers[idx]);
        System.out.println("Name: " + names[idx]);
    }

    static void viewMarksForIndex(int idx) {
        System.out.println("\n--- MARKS ---");
        System.out.println("English: " + english[idx]);
        System.out.println("Maths  : " + maths[idx]);
        System.out.println("Science: " + science[idx]);
        int total = english[idx] + maths[idx] + science[idx];
        double percent = total / 3.0;
        System.out.println("Percentage: " + String.format("%.2f", percent) + "%");
        String scholarship = (percent >= 85) ? "Full Scholarship" : (percent >= 70) ? "Partial Scholarship" : "No Scholarship";
        System.out.println("Scholarship Status: " + scholarship);
    }

    static void changeStudentPasswordInteractive(int idx) {
        System.out.print("Enter current password: ");
        String current = input.nextLine().trim();
        if (!current.equals(studentPasswords[idx])) {
            System.out.println("Incorrect password!");
            return;
        }
        System.out.print("Enter new password: ");
        String newPass = input.nextLine().trim();
        System.out.print("Confirm new password: ");
        String confirm = input.nextLine().trim();
        if (newPass.equals(confirm)) {
            studentPasswords[idx] = newPass;
            System.out.println("Password updated successfully!");
        } else {
            System.out.println("Passwords do not match.");
        }
    }

    // ----------------- Utilities: Search by roll -----------------
    static int getIndexByRoll(int roll) {
        for (int i = 0; i < studentCount; i++) {
            if (rollNumbers[i] == roll) return i;
        }
        return -1;
    }

    // ----------------- File handling: ensure files exist -----------------
    static void ensureFilesExist() {
        try {
            File f1 = new File(STUDENTS_FILE); if (!f1.exists()) f1.createNewFile();
            File f2 = new File(PASSWORDS_FILE); if (!f2.exists()) f2.createNewFile();
            File f3 = new File(MARKS_FILE); if (!f3.exists()) f3.createNewFile();
            File f4 = new File(ATTENDANCE_FILE); if (!f4.exists()) f4.createNewFile();
            File f5 = new File(FEES_FILE); if (!f5.exists()) f5.createNewFile();
        } catch (Exception e) {
            System.out.println("Error ensuring files exist: " + e.getMessage());
        }
    }

    // ----------------- Load/Save routines -----------------
    static void loadAllData() {
        loadStudentsFromFile();
        loadPasswordsFromFile();
        loadMarksFromFile();
        loadAttendanceFromFile();
        loadFeesFromFile();
    }

    static void saveAllData() {
        saveStudentsToFile();
        savePasswordsToFile();
        saveMarksToFile();
        saveAttendanceToFile();
        saveFeesToFile();
    }

    // --- Students file: roll and name on same line ---
    static void saveStudentsToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(STUDENTS_FILE, false))) {
            for (int i = 0; i < studentCount; i++) {
                pw.println(rollNumbers[i] + " " + names[i]);
            }
        } catch (Exception e) {
            System.out.println("Error writing students file: " + e.getMessage());
        }
    }

    static void loadStudentsFromFile() {
        try (Scanner sc = new Scanner(new File(STUDENTS_FILE))) {
            studentCount = 0;
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                // split into two tokens: roll and name (name without spaces recommended)
                String[] parts = line.split("\\s+", 2);
                if (parts.length < 2) { System.out.println("Bad line in students file: " + line); continue; }
                rollNumbers[studentCount] = Integer.parseInt(parts[0]);
                names[studentCount] = parts[1];
                studentCount++;
                if (studentCount >= MAX_STUDENTS) break;
            }
        } catch (FileNotFoundException e) {
            // file will be created later; keep arrays empty
            studentCount = 0;
        } catch (Exception e) {
            System.out.println("Error reading students file: " + e.getMessage());
        }
    }

    // --- Passwords file: roll password ---
    static void savePasswordsToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PASSWORDS_FILE, false))) {
            for (int i = 0; i < studentCount; i++) {
                String pass = studentPasswords[i] == null ? DEFAULT_STUDENT_PASSWORD : studentPasswords[i];
                pw.println(rollNumbers[i] + " " + pass);
            }
        } catch (Exception e) {
            System.out.println("Error writing passwords file: " + e.getMessage());
        }
    }

    static void loadPasswordsFromFile() {
        try (Scanner sc = new Scanner(new File(PASSWORDS_FILE))) {
            int idx = 0;
            while (sc.hasNextLine() && idx < MAX_STUDENTS) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\s+", 2);
                if (parts.length < 2) continue;
                int roll = Integer.parseInt(parts[0]);
                String pass = parts[1];
                // find index by roll in current arrays (students should be loaded first)
                int pos = getIndexByRoll(roll);
                if (pos != -1) studentPasswords[pos] = pass;
                idx++;
            }
        } catch (FileNotFoundException e) {
            // file may not exist yet
        } catch (Exception e) {
            System.out.println("Error reading passwords file: " + e.getMessage());
        }
    }

    // --- Marks file: english maths science (one line per student) ---
    static void saveMarksToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(MARKS_FILE, false))) {
            for (int i = 0; i < studentCount; i++) {
                pw.println(english[i] + " " + maths[i] + " " + science[i]);
            }
        } catch (Exception e) {
            System.out.println("Error writing marks file: " + e.getMessage());
        }
    }

    static void loadMarksFromFile() {
        try (Scanner sc = new Scanner(new File(MARKS_FILE))) {
            int idx = 0;
            while (sc.hasNextLine() && idx < studentCount) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\s+");
                try {
                    english[idx] = Integer.parseInt(parts[0]);
                    maths[idx] = Integer.parseInt(parts[1]);
                    science[idx] = Integer.parseInt(parts[2]);
                } catch (Exception ex) {
                    // leave zeros if missing data
                }
                idx++;
            }
        } catch (FileNotFoundException e) {
            // no marks yet
        } catch (Exception e) {
            System.out.println("Error reading marks file: " + e.getMessage());
        }
    }

    // --- Attendance file: single integer per line ---
    static void saveAttendanceToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ATTENDANCE_FILE, false))) {
            for (int i = 0; i < studentCount; i++) {
                pw.println(attendance[i]);
            }
        } catch (Exception e) {
            System.out.println("Error writing attendance file: " + e.getMessage());
        }
    }

    static void loadAttendanceFromFile() {
        try (Scanner sc = new Scanner(new File(ATTENDANCE_FILE))) {
            int idx = 0;
            while (sc.hasNextLine() && idx < studentCount) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                try { attendance[idx] = Integer.parseInt(line); } catch (Exception ex) { attendance[idx] = 0; }
                idx++;
            }
        } catch (FileNotFoundException e) {
            // no attendance yet
        } catch (Exception e) {
            System.out.println("Error reading attendance file: " + e.getMessage());
        }
    }

    // --- Fees file: one token per line (Paid/NotPaid) ---
    static void saveFeesToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FEES_FILE, false))) {
            for (int i = 0; i < studentCount; i++) {
                pw.println(feeStatus[i] == null ? "NotPaid" : feeStatus[i]);
            }
        } catch (Exception e) {
            System.out.println("Error writing fees file: " + e.getMessage());
        }
    }

    static void loadFeesFromFile() {
        try (Scanner sc = new Scanner(new File(FEES_FILE))) {
            int idx = 0;
            while (sc.hasNextLine() && idx < studentCount) {
                feeStatus[idx] = sc.nextLine().trim();
                idx++;
            }
        } catch (FileNotFoundException e) {
            // no fees yet
        } catch (Exception e) {
            System.out.println("Error reading fees file: " + e.getMessage());
        }

