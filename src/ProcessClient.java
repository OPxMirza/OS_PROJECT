import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class ProcessClient {
    public static void main(String[] args) {
        try {
            new ProcessClientGUI();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ProcessClientGUI {
    private int processId;

    public ProcessClientGUI() throws IOException {
        JFrame frame = new JFrame("Process Creator");
        frame.setSize(400, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        frame.add(panel);
        setupGUI(panel, frame);

        frame.setVisible(true);
    }

    private void setupGUI(JPanel panel, JFrame frame) throws IOException {
        JLabel titleLabel = new JLabel("Create New Process", JLabel.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.ITALIC, 24));
        titleLabel.setForeground(new Color(80, 80, 129));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        inputPanel.setBackground(new Color(244, 244, 244));

        JLabel idLabel = new JLabel("Process ID:");
        JTextField idField = new JTextField();
        idField.setFocusable(false);
        idField.setEditable(false);

        JLabel arrivalTimeLabel = new JLabel("Arrival Time:");
        JTextField arrivalTimeField = new JTextField();

        JLabel burstTimeLabel = new JLabel("Burst Time:");
        JTextField burstTimeField = new JTextField();

        JLabel priorityLabel = new JLabel("Priority:");
        JTextField priorityField = new JTextField();

        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(arrivalTimeLabel);
        inputPanel.add(arrivalTimeField);
        inputPanel.add(burstTimeLabel);
        inputPanel.add(burstTimeField);
        inputPanel.add(priorityLabel);
        inputPanel.add(priorityField);

        panel.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton submitButton = OSSimulation.createHoverStyledButton("Submit");
        JButton clearButton = OSSimulation.createHoverStyledButton("Clear");

        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        Socket socket = new Socket("localhost", 12345);
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        processId = dis.readInt();
        idField.setText(String.valueOf(processId));

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int arrivalTime = Integer.parseInt(arrivalTimeField.getText());
                    int burstTime = Integer.parseInt(burstTimeField.getText());
                    int priority = Integer.parseInt(priorityField.getText());

                    dos.writeInt(arrivalTime);
                    dos.writeInt(burstTime);
                    dos.writeInt(priority);

                    String response = dis.readUTF();

                    JOptionPane.showMessageDialog(frame, response, "Success", JOptionPane.INFORMATION_MESSAGE);

                    processId = dis.readInt();
                    idField.setText(String.valueOf(processId));
                    arrivalTimeField.setText("");
                    burstTimeField.setText("");
                    priorityField.setText("");
                } catch (IOException | NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                arrivalTimeField.setText("");
                burstTimeField.setText("");
                priorityField.setText("");
            }
        });

    }
}
