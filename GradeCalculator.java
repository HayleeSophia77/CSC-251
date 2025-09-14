import javax.swing.JOptionPane;

public class GradeCalculator {
    public static void main(String[] args) {
        boolean runAgain = true; 

        while (runAgain) {
            // Ask user how many grades they want to enter
            int numGrades = 0;
            boolean validNum = false;

            // Validate number of grades
            while (!validNum) {
                try {
                    numGrades = Integer.parseInt(JOptionPane.showInputDialog("How many grades would you like to enter?"));
                    if (numGrades > 0) {
                        validNum = true;
                    } else {
                        JOptionPane.showMessageDialog(null, "Please enter a number greater than 0.");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid input! Please enter a whole number.");
                }
            }

            double total = 0.0;
            double highest = Double.MIN_VALUE;
            double lowest = Double.MAX_VALUE;

            // Loop to get each grade
            for (int i = 1; i <= numGrades; i++) {
                double grade = -1;
                boolean validGrade = false;

                while (!validGrade) {
                    try {
                        String gradeInput = JOptionPane.showInputDialog("Enter grade #" + i + " (0 - 100):");
                        grade = Double.parseDouble(gradeInput);

                        if (grade >= 0 && grade <= 100) {
                            validGrade = true;
                        } else {
                            JOptionPane.showMessageDialog(null, "Grade must be between 0 and 100.");
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Invalid input! Please enter a number.");
                    }
                }

                total += grade;
                if (grade > highest) highest = grade;
                if (grade < lowest) lowest = grade;
            }

            // Calculate avg.
            double average = total / numGrades;

            // Get letter grade and get funny feedback :)
            String letterGrade;
            String feedback;

            if (average >= 90) {
                letterGrade = "A";
                feedback = "Excellent work! You’re basically the teacher now.";
            } else if (average >= 80) {
                letterGrade = "B";
                feedback = "Great job! Just a few more late-night study snacks and you’ll hit that A.";
            } else if (average >= 70) {
                letterGrade = "C";
                feedback = "Not bad! But maybe spend less time on Netflix and more time on notes.";
            } else if (average >= 60) {
                letterGrade = "D";
                feedback = "Well… at least you tried. Time to make best friends with a tutor.";
            } else {
                letterGrade = "F";
                feedback = "Oof. Maybe your calculator is broken? If not… time to panic (or study really hard).";
            }

            // Show result
            JOptionPane.showMessageDialog(null,
                    "Number of Grades: " + numGrades +
                    "\nAverage: " + String.format("%.2f", average) +
                    "\nFinal Letter Grade: " + letterGrade +
                    "\nHighest Grade: " + highest +
                    "\nLowest Grade: " + lowest +
                    "\n\nFeedback: " + feedback,
                    "Grade Calculator Result",
                    JOptionPane.INFORMATION_MESSAGE);

            // Ask if user wants to run again
            int choice = JOptionPane.showConfirmDialog(null, "Would you like to calculate again?", 
                                                       "Run Again?", JOptionPane.YES_NO_OPTION);
            if (choice != JOptionPane.YES_OPTION) {
                runAgain = false;
            }
        }

        JOptionPane.showMessageDialog(null, "Thanks for using the Grade Calculator!");
    }
}
