import javax.swing.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class GradeCalculator {
    private static final ArrayList<Double> grades = new ArrayList<>();
    private static final DecimalFormat df = new DecimalFormat("0.00");

    /**
     * Program entry point.
     * Shows welcome message, loops through menu until exit, then shows goodbye message.
     */
    public static void main(String[] args) {
        showWelcomeMessage();
        int choice;
        do {
            choice = showMenu();
            processChoice(choice);
        } while (choice != 4);
        showGoodbyeMessage();
    }

    /**
     * Shows welcome message to the user at program start.
     */
    private static void showWelcomeMessage() {
        JOptionPane.showMessageDialog(null,
                "Welcome to the Grade Calculator System!\n\n" +
                "This program will help you track your grades\n" +
                "and calculate your current average.",
                "Grade Calculator",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays the main menu and returns the user's choice.
     *
     * @return menu option chosen by the user (1-4), or -1 if invalid
     */
    private static int showMenu() {
        String menu = """
                Grade Calculator
                
                1. Add Grade
                2. View Average
                3. View Letter Grade
                4. Exit
                """;

        String input = JOptionPane.showInputDialog(null, menu, "Menu", JOptionPane.QUESTION_MESSAGE);

        if (input == null) {
            return 4; // User canceled
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1; // Invalid input
        }
    }

    /**
     * Processes the user’s menu choice and calls the appropriate method.
     *
     * @param choice menu option selected by the user
     */
    private static void processChoice(int choice) {
        switch (choice) {
            case 1 -> addGrade();
            case 2 -> viewAverage();
            case 3 -> viewLetterGrade();
            case 4 -> {} // goodbye handled after loop
            default -> JOptionPane.showMessageDialog(null, "Invalid choice. Try again.");
        }
    }

    /**
     * Prompts the user to enter a grade (0-100) and adds it to the list if valid.
     */
    private static void addGrade() {
        String input = JOptionPane.showInputDialog(null, "Enter grade (0-100):");
        if (input == null) return;
        try {
            double grade = Double.parseDouble(input);
            if (grade < 0 || grade > 100) {
                JOptionPane.showMessageDialog(null, "Grade must be between 0 and 100.");
            } else {
                grades.add(grade);
                JOptionPane.showMessageDialog(null, "Grade added: " + df.format(grade));
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid number. Please enter a valid grade.");
        }
    }

    /**
     * Calculates and displays the average grade with snarky/funny feedback.
     */
    private static void viewAverage() {
        if (grades.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No grades entered yet.", "Average", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        double avg = calculateAverage();
        String message = "Average of " + grades.size() + " grade(s): " + df.format(avg) + "%";

        // Funny/snarky feedback
        String feedback;
        if (avg >= 90) {
            feedback = "\nBruh you’re built different. Straight A vibes!";
        } else if (avg >= 80) {
            feedback = "\nSolid work, you’re lowkey cruising.";
        } else if (avg >= 70) {
            feedback = "\nMid-tier grind… could be worse, could be better.";
        } else if (avg >= 60) {
            feedback = "\nBarely surviving. You’re literally speedrunning stress.";
        } else {
            feedback = "\nOof… that grade flopped harder than a failed TikTok.";
        }

        JOptionPane.showMessageDialog(null, message + feedback, "Average", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Calculates and displays the letter grade with snarky/funny feedback.
     */
    private static void viewLetterGrade() {
        if (grades.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No grades entered yet.", "Letter Grade", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        double avg = calculateAverage();
        String letter = getLetterGrade(avg);
        String message = "Average: " + df.format(avg) + "%\nLetter grade: " + letter;

        // Funny/snarky feedback
        String feedback;
        switch (letter) {
            case "A", "A-" -> feedback = "\nTop of the food chain, go off queen";
            case "B+", "B", "B-" -> feedback = "\nRespectable… not main character energy, but close.";
            case "C+", "C", "C-" -> feedback = "\nBare minimum detected. Might need a glow-up.";
            case "D+", "D", "D-" -> feedback = "\nDanger zone! This GPA is fighting for its life.";
            default -> feedback = "\nRip… pack it up, chief. Summer school arc unlocked.";
        }

        JOptionPane.showMessageDialog(null, message + feedback, "Letter Grade", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays final average and letter grade when the program ends, with a vibe-check message.
     */
    private static void showGoodbyeMessage() {
        String message;
        if (grades.isEmpty()) {
            message = "Goodbye! You didn’t enter any grades.\nLiterally no data, no drama.";
        } else {
            double avg = calculateAverage();
            String letter = getLetterGrade(avg);
            message = "Goodbye!\nFinal average: " + df.format(avg) +
                      "%\nFinal letter grade: " + letter;

            if (avg >= 90) {
                message += "\nYou’re built like a study machine.";
            } else if (avg >= 75) {
                message += "\nNot bad… keep cruising, you’ll be fine.";
            } else if (avg >= 60) {
                message += "\nBro… clutch up next time.";
            } else {
                message += "\nBruh. Just bruh.";
            }
        }
        JOptionPane.showMessageDialog(null, message, "Goodbye", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Calculates the average of all grades.
     *
     * @return average of grades, or 0.0 if no grades
     */
    private static double calculateAverage() {
        if (grades.isEmpty()) return 0.0;
        double sum = 0.0;
        for (double g : grades) sum += g;
        return sum / grades.size();
    }

    /**
     * Determines the letter grade (with plus/minus) for a given average.
     *
     * @param average the numeric average
     * @return letter grade string
     */
    private static String getLetterGrade(double average) {
        if (average >= 93.0) return "A";
        if (average >= 90.0) return "A-";
        if (average >= 87.0) return "B+";
        if (average >= 83.0) return "B";
        if (average >= 80.0) return "B-";
        if (average >= 77.0) return "C+";
        if (average >= 73.0) return "C";
        if (average >= 70.0) return "C-";
        if (average >= 67.0) return "D+";
        if (average >= 63.0) return "D";
        if (average >= 60.0) return "D-";
        return "F";
    }
}

