package org.example.member1;

import java.util.Scanner;

public class StudentUI {
    private StudentManager studentManager;
    private Scanner scanner;

    public StudentUI(StudentManager studentManager) {
        this.studentManager = studentManager;
        this.scanner = new Scanner(System.in);
    }

    public void showStudentMenu() {
        int choice;
        do {
            System.out.println("\n--- Member 1: Student Management System ---");
            System.out.println("1. Register New Student");
            System.out.println("2. Search Student by USN");
            System.out.println("3. Modify Student Details");
            System.out.println("4. Delete Student Record");
            System.out.println("5. Exit");
            System.out.print("Select an option: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter USN: ");
                    String usn = scanner.nextLine();
                    System.out.print("Enter Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Branch: ");
                    String branch = scanner.nextLine();
                    System.out.print("Enter Semester: ");
                    int sem = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter Section: ");
                    String section = scanner.nextLine();
                    studentManager.registerStudent(usn, name, branch, sem, section);
                    break;
                case 2:
                    System.out.print("Enter USN to search: ");
                    String searchUsn = scanner.nextLine();
                    Student found = studentManager.searchStudent(searchUsn);
                    if (found != null) {
                        System.out.println("Student Found: " + found);
                    } else {
                        System.out.println("No student found with USN: " + searchUsn);
                    }
                    break;
                case 3:
                    System.out.print("Enter USN of student to modify: ");
                    String modUsn = scanner.nextLine();
                    System.out.print("Enter New Name: ");
                    String newName = scanner.nextLine();
                    System.out.print("Enter New Branch: ");
                    String newBranch = scanner.nextLine();
                    System.out.print("Enter New Semester: ");
                    int newSem = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter New Section: ");
                    String newSection = scanner.nextLine();
                    studentManager.modifyStudent(modUsn, newName, newBranch, newSem, newSection);
                    break;
                case 4:
                    System.out.print("Enter USN to delete: ");
                    String delUsn = scanner.nextLine();
                    studentManager.deleteStudent(delUsn);
                    break;
                case 5:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        } while (choice != 5);
    }
}
