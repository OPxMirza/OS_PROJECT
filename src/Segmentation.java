import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.awt.event.*;

public class Segmentation {

    public static void displaySegmentsInDialog(JFrame parentFrame) {
        JDialog dialog = ProcessEvents.createDialog(parentFrame);
        dialog.setSize(600, 400);

        JLabel titleLabel = ProcessEvents.createLabel("Memory Allocation");
        dialog.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = ProcessEvents.createPanel();
        inputPanel.setLayout(new BorderLayout(10, 10));

        JPanel readyPanel = new JPanel(new CardLayout());
        readyPanel.add(QueueDisplayDialog.createSegmentTable(dialog, MemoryManager.segments));

        readyPanel.setPreferredSize(new Dimension(300, 150));
        inputPanel.add(readyPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton backButton = OSSimulation.createHoverStyledButton("Back");

        buttonPanel.add(backButton);

        backButton.addActionListener(e -> {
            dialog.dispose();
            parentFrame.setVisible(true);
        });

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                parentFrame.setVisible(true);
            }
        });

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    static void showSegmentMenu(JFrame parentFrame) {
        JFrame processFrame = ProcessEvents.createFrame(parentFrame);
        processFrame.setSize(400, 250);

        JLabel titleLabel = ProcessEvents.createLabel("Segmentation");
        processFrame.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = ProcessEvents.createPanel();
        inputPanel.setLayout(new GridLayout(0, 2, 10, 10));
        JButton createSegment = OSSimulation.createHoverStyledButton("Create Segment");
        JButton resources = OSSimulation.createHoverStyledButton("Resources");

        inputPanel.add(createSegment);
        inputPanel.add(resources);

        processFrame.add(inputPanel, BorderLayout.CENTER);

        JButton backButton = OSSimulation.createHoverStyledButton("Back");
        backButton.addActionListener(e -> {
            processFrame.dispose();
            parentFrame.setVisible(true);
        });

        createSegment.addActionListener(e -> {
            processFrame.dispose();
            CreateSegmentationMenu(processFrame);
        });

        resources.addActionListener(e -> {
            processFrame.dispose();
            displaySegmentsInDialog(processFrame);
        });

        JPanel backPanel = new JPanel(new BorderLayout(5, 5));
        JLabel footerLabel = OSSimulation.createFooterLabel("GeneSect");
        backPanel.add(backButton, BorderLayout.WEST);
        backPanel.add(footerLabel, BorderLayout.CENTER);
        backPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        backPanel.setBackground(new Color(244, 244, 244));
        processFrame.add(backPanel, BorderLayout.SOUTH);

        processFrame.setLocationRelativeTo(parentFrame);
        processFrame.setVisible(true);
        parentFrame.setVisible(false);
    }

    static void CreateSegmentationMenu(JFrame parentFrame) {

        JDialog createProcessDialog = new JDialog(parentFrame, "Create Segment", true);

        createProcessDialog.setSize(400, 300);
        createProcessDialog.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Segmentation", JLabel.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.ITALIC, 24));
        titleLabel.setForeground(new Color(80, 80, 129));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        createProcessDialog.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        inputPanel.setBackground(new Color(244, 244, 244));

        JLabel idLabel = new JLabel("Segment ID: ");
        JTextField idField = new JTextField(String.valueOf(MemoryManager.getNextSegment()));
        idField.setFocusable(false);
        idField.setEditable(false);
        JLabel limitLabel = new JLabel("Limit: ");
        JTextField limitField = new JTextField();
        JLabel baseAddress = new JLabel("Base Address: ");
        JTextField baseAddressField = new JTextField();

        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(baseAddress);
        inputPanel.add(baseAddressField);
        inputPanel.add(limitLabel);
        inputPanel.add(limitField);

        createProcessDialog.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton submitButton = OSSimulation.createHoverStyledButton("Submit");
        JButton backButton = OSSimulation.createHoverStyledButton("Back");

        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);

        createProcessDialog.add(buttonPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> {
            createProcessDialog.dispose();
            MemoryManager.setNextSegment(Integer.parseInt(idField.getText()));
            parentFrame.setVisible(true);
        });

        submitButton.addActionListener(e -> {
            try {
                int baseAdd = Integer.parseInt(baseAddressField.getText());
                int limit = Integer.parseInt(limitField.getText());
                if(MemoryManager.createSegment(Integer.parseInt(idField.getText()), baseAdd, limit)) {
                    JOptionPane.showMessageDialog(createProcessDialog, "Segment created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    createProcessDialog.dispose();
                    parentFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(createProcessDialog, "Segment creation failed!", "Failure", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(createProcessDialog, "Invalid input! Please enter integers for base address and limit.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        createProcessDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                MemoryManager.setNextSegment(Integer.parseInt(idField.getText()));
                parentFrame.setVisible(true);
            }
        });
        createProcessDialog.setLocationRelativeTo(parentFrame);
        createProcessDialog.setVisible(true);
    }

    static int CreateAddressCalculationDialog(JDialog parentFrame, int id, int address, int limit) {

        JDialog calculateAddressDialog = new JDialog(parentFrame, "Address Calculation", true);
        calculateAddressDialog.setSize(450, 450);
        calculateAddressDialog.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Address Calculation", JLabel.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.ITALIC, 24));
        titleLabel.setForeground(new Color(80, 80, 129));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        calculateAddressDialog.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        inputPanel.setBackground(new Color(244, 244, 244));

        JLabel idLabel = new JLabel("Segment ID: ");
        JTextField idField = new JTextField(String.valueOf(id));
        idField.setEditable(false);
        idField.setFocusable(false);
        JLabel baseLabel = new JLabel("Base Address: ");
        JTextField baseField = new JTextField(String.valueOf(address));
        baseField.setEditable(false);
        baseField.setFocusable(false);
        JLabel limitLabel = new JLabel("Limit: ");
        JTextField limitField = new JTextField(String.valueOf(limit));
        limitField.setEditable(false);
        limitField.setFocusable(false);
        JLabel offsetLabel = new JLabel("Segment Offset: ");
        JTextField offsetField = new JTextField();
        JLabel trapLabel = new JLabel("Trap: ");
        JTextField trapField = new JTextField();
        trapField.setEditable(false);
        trapField.setFocusable(false);
        JLabel spaceLabel = new JLabel("Physical Address: ");
        JTextField spaceField = new JTextField();
        spaceField.setEditable(false);
        spaceField.setFocusable(false);

        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(baseLabel);
        inputPanel.add(baseField);
        inputPanel.add(limitLabel);
        inputPanel.add(limitField);
        inputPanel.add(offsetLabel);
        inputPanel.add(offsetField);
        inputPanel.add(trapLabel);
        inputPanel.add(trapField);
        inputPanel.add(spaceLabel);
        inputPanel.add(spaceField);

        calculateAddressDialog.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton calculateButton = OSSimulation.createHoverStyledButton("Calculate");
        JButton backButton = OSSimulation.createHoverStyledButton("Back");

        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(calculateButton);
        buttonPanel.add(backButton);

        calculateAddressDialog.add(buttonPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> {
            calculateAddressDialog.dispose();
            parentFrame.setVisible(true);
        });

        calculateButton.addActionListener(e -> {
            try {
                int offset = Integer.parseInt(offsetField.getText());

                if (offset >= 0 && offset < limit) {
                    int physicalAddress = address + offset;
                    JOptionPane.showMessageDialog(
                            calculateAddressDialog,
                            "Physical Address: " + physicalAddress,
                            "Calculation Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    spaceField.setText(String.valueOf(physicalAddress));
                    trapField.setText("No");
                } else {
                    JOptionPane.showMessageDialog(
                            calculateAddressDialog,
                            "Trap! Offset exceeds segment limit.",
                            "Trap Detected",
                            JOptionPane.ERROR_MESSAGE
                    );
                    spaceField.setText("");
                    trapField.setText("Yes");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        calculateAddressDialog,
                        "Invalid input! Please enter integers for base address, limit, and offset.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        calculateAddressDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                parentFrame.setVisible(true);
            }
        });

        calculateAddressDialog.setLocationRelativeTo(parentFrame);
        calculateAddressDialog.setVisible(true);

        if(spaceField.getText() == null || spaceField.getText().isEmpty()) {
            return 0;
        }

        return (Integer.parseInt(offsetField.getText()) + address);
    }

}
