import javax.swing.*;
import java.util.ArrayList;

class Student {
    String name;
    double grade;

    public Student(String name, double grade) {
        this.name = name;
        this.grade = grade;
    }
}

public class StudentTrackSwing {
    static ArrayList<Student> students = new ArrayList<>();

    public static void main(String[] args) {
        // create the window frame setup
        JFrame frame = new JFrame("Student Grade Manager");
        frame.setSize(380, 580);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // create input field and labels
        JLabel nameLabel = new JLabel("student name");
        nameLabel.setBounds(20, 20, 100, 25);
        frame.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(130, 20, 200, 25);
        frame.add(nameField);

        JLabel gradeLabel = new JLabel("student grade");
        gradeLabel.setBounds(20, 60, 100, 25);
        frame.add(gradeLabel);

        JTextField gradeField = new JTextField();
        gradeField.setBounds(130, 60, 200, 25);
        frame.add(gradeField);

        JButton addButton = new JButton("Add Student");
        addButton.setBounds(20, 100, 150, 30);
        frame.add(addButton);

        JButton reportButton = new JButton("generate report");
        reportButton.setBounds(180, 100, 150, 30);
        frame.add(reportButton);

        JTextArea displayArea = new JTextArea();
        displayArea.setBounds(20, 150, 310, 280);
        displayArea.setEditable(false);
        frame.add(displayArea);

        // =================action for add student nutton

        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String gradeStr = gradeField.getText().trim();

            if (name.isEmpty() || gradeStr.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "please fill out the both name and grid filled");
                return;
            }

            try {
                double grade = Double.parseDouble(gradeStr);

                if (grade < 0 || grade > 100) {
                    JOptionPane.showMessageDialog(frame, "grade must be betn 0 and 100");
                    return;
                }

                students.add(new Student(name, grade));

                nameField.setText("");
                gradeField.setText("");

                JOptionPane.showMessageDialog(frame, name + "added Successfully");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "please enter the valid grade ");
            }
        });

        // action for generate report button

        reportButton.addActionListener(e -> {
            // do not run report calculation if emp-ty notebook

            if (students.isEmpty()) {
                displayArea.setText("no student data available");
                return;
            }

            double total = 0;
            double highest = students.get(0).grade;
            double lowest = students.get(0).grade;

            for (int i = 0; i < students.size(); i++) {
                double currentGrade = students.get(i).grade;
                total = total + currentGrade;

                if (currentGrade > highest)
                    highest = currentGrade;
                if (currentGrade < lowest)
                    lowest = currentGrade;
            }

            double average = total / students.size();

            String report = " ...........................";
            report = report + "       SUMMARY REPORT       ";
            report = report + "                             ";
            report = report + "Name\t\tGrade\n";
            report = report + "..............................";

            for (int i = 0; i < students.size(); i++) {
                report = report + students.get(i).name + "\t\t" + students.get(i).grade + "\n";

            }

            report = report + " ...................................";
            report = report + "total students" + students.size() + "\n";
            report += "Average Score:  " + String.format("%.2f", average) + "\n";
            report += "Highest Score:  " + highest + "\n";
            report += "Lowest Score:   " + lowest + "\n";
            report += "=============================";

            displayArea.setText(report);

        });

        // show the frame on monitor
        frame.setVisible(true);

    }
}
