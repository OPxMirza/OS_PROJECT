import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class BankersAlgorithm {
    private static int[][] allocation, max, need, available;
    private static int numProcesses, numResources;
    private static JFrame frame;
    private static JTable inputTable;
    private static JTextField[] availableFields;

    public static void bankers(JFrame parentFrame) {
        JDialog dialog = ProcessEvents.createDialog(parentFrame);
        dialog.setSize(800, 600);
        dialog.setLayout(new BorderLayout());

        JPanel configPanel = new JPanel();
        configPanel.setLayout(new GridLayout(3, 2, 15, 15));
        configPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel processLabel = new JLabel("Number of Processes:");
        processLabel.setFont(new Font("Arial", Font.BOLD, 14));
        configPanel.add(processLabel);

        JComboBox<Integer> processDropdown = new JComboBox<>(new Integer[]{2, 3, 4, 5});
        configPanel.add(processDropdown);

        JLabel resourceLabel = new JLabel("Number of Resources:");
        resourceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        configPanel.add(resourceLabel);

        JComboBox<Integer> resourceDropdown = new JComboBox<>(new Integer[]{2, 3, 4});
        configPanel.add(resourceDropdown);

        JButton startButton = new JButton("Start");
        startButton.setBackground(new Color(100, 149, 237));
        startButton.setForeground(Color.WHITE);
        configPanel.add(startButton);

        dialog.add(configPanel, BorderLayout.NORTH);

        startButton.addActionListener(e -> {
            numProcesses = (int) processDropdown.getSelectedItem();
            numResources = (int) resourceDropdown.getSelectedItem();
            showInputTable();
        });

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                parentFrame.setVisible(true);
            }
        });

        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private static void showInputTable() {
        frame.getContentPane().removeAll();

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());

        String[] columnNames = new String[numResources * 2 + 1];
        columnNames[0] = "Process";
        for (int i = 0; i < numResources; i++) {
            columnNames[i + 1] = "Alloc R" + i;
            columnNames[i + 1 + numResources] = "Max R" + i;
        }

        String[][] data = new String[numProcesses][numResources * 2 + 1];
        for (int i = 0; i < numProcesses; i++) {
            data[i][0] = "P" + i;
            for (int j = 0; j < numResources; j++) {
                data[i][j + 1] = "0"; // Default allocation
                data[i][j + 1 + numResources] = "0"; // Default max
            }
        }

        inputTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(inputTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel availablePanel = new JPanel();
        availablePanel.setLayout(new FlowLayout());
        availablePanel.add(new JLabel("Available Resources:"));
        availableFields = new JTextField[numResources];

        for (int i = 0; i < numResources; i++) {
            availableFields[i] = new JTextField("0", 5);
            availablePanel.add(availableFields[i]);
        }

        JButton checkButton = new JButton("Check Safe State");
        checkButton.setBackground(new Color(50, 205, 50));
        checkButton.setForeground(Color.WHITE);

        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (loadTableData()) {
                    String result = isSafe();
                    showResults(result);
                }
            }
        });

        tablePanel.add(availablePanel, BorderLayout.NORTH);
        frame.add(tablePanel, BorderLayout.CENTER);
        frame.add(checkButton, BorderLayout.SOUTH);

        frame.revalidate();
        frame.repaint();
    }

    private static boolean loadTableData() {
        allocation = new int[numProcesses][numResources];
        max = new int[numProcesses][numResources];
        available = new int[1][numResources];
        need = new int[numProcesses][numResources];

        try {
            for (int i = 0; i < numProcesses; i++) {
                for (int j = 0; j < numResources; j++) {
                    allocation[i][j] = Integer.parseInt((String) inputTable.getValueAt(i, j + 1));
                    max[i][j] = Integer.parseInt((String) inputTable.getValueAt(i, j + 1 + numResources));
                    if (max[i][j] < allocation[i][j]) {
                        throw new IllegalArgumentException("Max cannot be less than Allocation.");
                    }
                }
            }

            for (int j = 0; j < numResources; j++) {
                available[0][j] = Integer.parseInt(availableFields[j].getText().trim());
                if (available[0][j] < 0) {
                    throw new IllegalArgumentException("Available cannot be negative.");
                }
            }

            for (int i = 0; i < numProcesses; i++) {
                for (int j = 0; j < numResources; j++) {
                    need[i][j] = max[i][j] - allocation[i][j];
                }
            }
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Please enter valid integer values.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static String isSafe() {
        boolean[] finish = new boolean[numProcesses];
        int[] safeSequence = new int[numProcesses];
        int[] work = new int[numResources];
        System.arraycopy(available[0], 0, work, 0, numResources);

        int count = 0;
        while (count < numProcesses) {
            boolean found = false;
            for (int p = 0; p < numProcesses; p++) {
                if (!finish[p]) {
                    int j;
                    for (j = 0; j < numResources; j++) {
                        if (need[p][j] > work[j]) {
                            break;
                        }
                    }
                    if (j == numResources) {
                        for (int k = 0; k < numResources; k++) {
                            work[k] += allocation[p][k];
                        }
                        safeSequence[count++] = p;
                        finish[p] = true;
                        found = true;
                    }
                }
            }
            if (!found) {
                return "Deadlock detected! The system is not in a safe state.";
            }
        }

        StringBuilder result = new StringBuilder("Safe state detected. Safe sequence is: ");
        for (int i = 0; i < numProcesses; i++) {
            result.append("P").append(safeSequence[i]);
            if (i < numProcesses - 1) {
                result.append(" -> ");
            }
        }
        return result.toString();
    }

    private static void showResults(String result) {
        frame.getContentPane().removeAll();

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BorderLayout());

        String[] columnNames = new String[numResources * 3 + 1];
        columnNames[0] = "Process";
        for (int i = 0; i < numResources; i++) {
            columnNames[i + 1] = "Alloc R" + i;
            columnNames[i + 1 + numResources] = "Max R" + i;
            columnNames[i + 1 + numResources * 2] = "Need R" + i;
        }

        String[][] data = new String[numProcesses][numResources * 3 + 1];
        for (int i = 0; i < numProcesses; i++) {
            data[i][0] = "P" + i;
            for (int j = 0; j < numResources; j++) {
                data[i][j + 1] = String.valueOf(allocation[i][j]);
                data[i][j + 1 + numResources] = String.valueOf(max[i][j]);
                data[i][j + 1 + numResources * 2] = String.valueOf(need[i][j]);
            }
        }

        JTable resultTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        JLabel resultLabel = new JLabel(result, SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        frame.add(resultPanel, BorderLayout.CENTER);
        frame.add(resultLabel, BorderLayout.SOUTH);

        frame.revalidate();
        frame.repaint();
    }
}