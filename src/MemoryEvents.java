import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class MemoryEvents {
    public static void showAllocateMemory(JFrame parentFrame) {
        JDialog dialog = ProcessEvents.createDialog(parentFrame);
        dialog.setSize(600, 400);

        JLabel titleLabel = ProcessEvents.createLabel("Memory Allocation");
        dialog.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = ProcessEvents.createPanel();
        inputPanel.setLayout(new BorderLayout(10, 10));

        JPanel readyPanel = new JPanel(new CardLayout());
        readyPanel.add(QueueDisplayDialog.showQueueDisplayPanel(ProcessManager.readyQueue, "Allocated"));

        readyPanel.setPreferredSize(new Dimension(300, 150));
        inputPanel.add(readyPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton backButton = OSSimulation.createHoverStyledButton("Back");

        buttonPanel.add(backButton);

        backButton.addActionListener(e -> dialog.dispose());

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    public static int showMemoryAllocator(JFrame parentFrame, int id, int memory) {
        JDialog memoryAllocatorDialog = new JDialog(parentFrame, "Memory Allocator", true);

        memoryAllocatorDialog.setSize(400, 300);
        memoryAllocatorDialog.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Allocate Memory to Process", JLabel.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.ITALIC, 24));
        titleLabel.setForeground(new Color(80, 80, 129));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        memoryAllocatorDialog.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        inputPanel.setBackground(new Color(244, 244, 244));

        JLabel idLabel = new JLabel("Process ID:");
        JTextField idField = new JTextField(String.valueOf(id));
        idField.setFocusable(false);
        idField.setEditable(false);
        JLabel memoryLabel = new JLabel("Memory Required:");
        JTextField memoryField = new JTextField(String.valueOf(memory));

        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(memoryLabel);
        inputPanel.add(memoryField);

        memoryField.addKeyListener(ProcessEvents.numericCheck());

        memoryAllocatorDialog.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton allocateButton = OSSimulation.createHoverStyledButton("Allocate");
        JButton backButton = OSSimulation.createHoverStyledButton("Back");

        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(allocateButton);
        buttonPanel.add(backButton);

        memoryAllocatorDialog.add(buttonPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> memoryAllocatorDialog.dispose());

        int processId = Integer.parseInt(idField.getText());

        allocateButton.addActionListener(e -> {
                ProcessManager.allocateMemory(processId, Integer.parseInt(memoryField.getText()));
                memoryAllocatorDialog.dispose();
        });

        memoryAllocatorDialog.setLocationRelativeTo(parentFrame);
        memoryAllocatorDialog.setVisible(true);

        return Integer.parseInt(memoryField.getText());
    }

    public static void showPagingMemory(JFrame parentFrame) {
        JDialog dialog = ProcessEvents.createDialog(parentFrame);
        dialog.setSize(600, 400);

        JLabel titleLabel = ProcessEvents.createLabel("Memory Allocation");
        dialog.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = ProcessEvents.createPanel();
        inputPanel.setLayout(new BorderLayout(10, 10));

        JPanel readyPanel = new JPanel(new CardLayout());
        readyPanel.add(QueueDisplayDialog.showQueueDisplayPanel(ProcessManager.readyQueue, "Paging"));

        readyPanel.setPreferredSize(new Dimension(300, 150));
        inputPanel.add(readyPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton backButton = OSSimulation.createHoverStyledButton("Back");

        buttonPanel.add(backButton);

        backButton.addActionListener(e -> dialog.dispose());

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    public static int showPagingDialog(JFrame parentFrame, int processSize, int processID) {
        JDialog pagingDialog = new JDialog(parentFrame, "Process Paging", true);

        pagingDialog.setSize(400, 350);
        pagingDialog.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Process Paging Calculator", JLabel.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 20));
        titleLabel.setForeground(new Color(80, 80, 129));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pagingDialog.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel processIDLabel = new JLabel("Process ID:");
        JTextField processIDField = new JTextField(String.valueOf(processID));
        processIDField.setEditable(false);
        processIDField.setFocusable(false);

        JLabel processSizeLabel = new JLabel("Process Size (KB):");
        JTextField processSizeField = new JTextField(String.valueOf(processSize));
        processSizeField.setFocusable(false);
        processSizeField.setEditable(false);

        JLabel pageSizeLabel = new JLabel("Page Size (KB):");
        JTextField pageSizeField = new JTextField(String.valueOf(MemoryManager.MemoryConfigure.pageSize));

        JLabel pagesLabel = new JLabel("No. of Pages:");
        JTextField pagesField = new JTextField();
        pagesField.setFocusable(false);
        pagesField.setEditable(false);

        inputPanel.add(processIDLabel);
        inputPanel.add(processIDField);
        inputPanel.add(processSizeLabel);
        inputPanel.add(processSizeField);
        inputPanel.add(pageSizeLabel);
        inputPanel.add(pageSizeField);
        inputPanel.add(pagesLabel);
        inputPanel.add(pagesField);

        pagingDialog.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton calculateButton = OSSimulation.createHoverStyledButton("Calculate");
        JButton closeButton = OSSimulation.createHoverStyledButton("Close");

        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(calculateButton);
        buttonPanel.add(closeButton);

        pagingDialog.add(buttonPanel, BorderLayout.SOUTH);

        closeButton.addActionListener(e -> pagingDialog.dispose());

        calculateButton.addActionListener(e -> {
            int pageSize = Integer.parseInt(pageSizeField.getText());
            if (pageSize <= 0) {
                JOptionPane.showMessageDialog(
                        pagingDialog,
                        "Invalid configuration! Page size must be greater than zero.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            int pagesRequired = (int) Math.ceil((double) processSize / pageSize);

            pagesField.setText(String.valueOf(pagesRequired));

            ProcessManager.addPages(processID, pagesRequired);

            JOptionPane.showMessageDialog(
                    pagingDialog,
                    "Process Size: " + processSize + " KB\n" +
                            "Page Size: " + pageSize + " KB\n" +
                            "Pages Required: " + pagesRequired,
                    "Paging Result",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        pagingDialog.setLocationRelativeTo(parentFrame);
        pagingDialog.setVisible(true);

        return ((int) Math.ceil((double) processSize / Integer.parseInt(pageSizeField.getText())));
    }

    public static void showMemoryConfigurationDialog(JFrame parentFrame, MemoryManager.MemoryConfigure config) {
        JDialog dialog = new JDialog(parentFrame, "Configuration Settings", true);
        dialog.setSize(400, 250);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(null);

        JPanel inputPanel = ProcessEvents.createPanel();
        inputPanel.setLayout(new GridLayout(4, 2, 10 , 10));

        JLabel algorithmLabel = new JLabel("Algorithm:");
        JComboBox<String> algorithmReplacementDropdown = new JComboBox<>(new String[]{"FIFO", "Optimal", "LRU"});
        algorithmReplacementDropdown.setSelectedItem(config.replacementAlgorithm);
        algorithmReplacementDropdown.setUI(ProcessEvents.comboUI(algorithmReplacementDropdown));
        algorithmReplacementDropdown.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        algorithmReplacementDropdown.setBackground(Color.WHITE);
        algorithmReplacementDropdown.setFocusable(false);

        JLabel memoryLabel = new JLabel("Memory Size(KB):");
        JTextField memoryField = new JTextField(String.valueOf(config.memorySize));
        memoryField.addKeyListener(ProcessEvents.numericCheck());



        JLabel pageSizeLabel = new JLabel("Default Page(KB):");
        JTextField pageSizeField = new JTextField(String.valueOf(config.pageSize));
        memoryField.addKeyListener(ProcessEvents.numericCheck());

        inputPanel.add(memoryLabel);
        inputPanel.add(memoryField);
        inputPanel.add(algorithmLabel);
        inputPanel.add(algorithmReplacementDropdown);
        inputPanel.add(pageSizeLabel);
        inputPanel.add(pageSizeField);
        dialog.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton submitButton = OSSimulation.createHoverStyledButton("Submit");
        JButton backButton = OSSimulation.createHoverStyledButton("Back");

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.replacementAlgorithm = (String) algorithmReplacementDropdown.getSelectedItem();
                config.memorySize = Integer.parseInt(memoryField.getText());
                config.pageSize = Integer.parseInt(pageSizeField.getText());
                System.out.println(config.memorySize + " " + config.replacementAlgorithm);
                JOptionPane.showMessageDialog(dialog , "Configuration Saved!");
                dialog.dispose();
            }
        });

        backButton.addActionListener(e -> dialog.dispose());

        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    public static void showReplacementDialog(JFrame parentFrame, MemoryManager.MemoryConfigure config) {
        JDialog replacementDialog = new JDialog(parentFrame, "Memory Replacement", true);

        replacementDialog.setSize(500, 250);
        replacementDialog.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Memory Replacement Algorithm", JLabel.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 20));
        titleLabel.setForeground(new Color(80, 80, 129));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        replacementDialog.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = ProcessEvents.createPanel();
        inputPanel.setLayout(new GridLayout(2, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel framesLabel = new JLabel("Number of Frames:");
        JTextField framesField = new JTextField();

        JLabel referenceStringLabel = new JLabel("Reference String (Comma Separated):");
        JTextField referenceStringField = new JTextField();

        inputPanel.add(framesLabel);
        inputPanel.add(framesField);
        inputPanel.add(referenceStringLabel);
        inputPanel.add(referenceStringField);

        replacementDialog.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton calculateButton = OSSimulation.createHoverStyledButton("Calculate");
        JButton closeButton = OSSimulation.createHoverStyledButton("Close");

        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(calculateButton);
        buttonPanel.add(closeButton);

        replacementDialog.add(buttonPanel, BorderLayout.SOUTH);

        closeButton.addActionListener(e -> replacementDialog.dispose());

       calculateButton.addActionListener(e -> ReplacementAlgorithm.handleReplacementAlgorithm(
            replacementDialog,
            framesField.getText(),
            referenceStringField.getText(),
            config.replacementAlgorithm
        ));

        replacementDialog.setLocationRelativeTo(parentFrame);
        replacementDialog.setVisible(true);
    }

    public static void showFramingDialog(JFrame parentFrame, MemoryManager.MemoryConfigure config) {
        JDialog pagingDialog = new JDialog(parentFrame, "Memory Framing", true);

        pagingDialog.setSize(400, 350);
        pagingDialog.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Memory Framing Calculator", JLabel.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 20));
        titleLabel.setForeground(new Color(80, 80, 129));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pagingDialog.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel memorySizeLabel = new JLabel("Memory Size (KB):");
        JTextField memorySizeField = new JTextField(String.valueOf(config.memorySize));
        memorySizeField.setEditable(false);
        memorySizeField.setFocusable(false);

        JLabel frameSizeLabel = new JLabel("Frame Size (KB):");
        JTextField frameSizeField = new JTextField(String.valueOf(config.pageSize));


        JLabel framesLabel = new JLabel("No. of Frames:");
        JTextField framesField = new JTextField();
        framesField.setFocusable(false);
        framesField.setEditable(false);

        inputPanel.add(memorySizeLabel);
        inputPanel.add(memorySizeField);
        inputPanel.add(frameSizeLabel);
        inputPanel.add(frameSizeField);
        inputPanel.add(framesLabel);
        inputPanel.add(framesField);

        pagingDialog.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton calculateButton = OSSimulation.createHoverStyledButton("Calculate");
        JButton closeButton = OSSimulation.createHoverStyledButton("Close");

        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(calculateButton);
        buttonPanel.add(closeButton);

        pagingDialog.add(buttonPanel, BorderLayout.SOUTH);

        closeButton.addActionListener(e -> pagingDialog.dispose());

        calculateButton.addActionListener(e -> {
            int frameSize = Integer.parseInt(frameSizeField.getText());
            int memorySize = config.memorySize;
            if (frameSize <= 0) {
                JOptionPane.showMessageDialog(
                        pagingDialog,
                        "Invalid configuration! Page size must be greater than zero.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            int framesRequired = (int) Math.ceil((double) memorySize / frameSize);

            framesField.setText(String.valueOf(framesRequired));

            JOptionPane.showMessageDialog(
                    pagingDialog,
                    "Process Size: " + memorySize + " KB\n" +
                            "Page Size: " + frameSize + " KB\n" +
                            "Pages Required: " + framesRequired,
                    "Paging Result",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        pagingDialog.setLocationRelativeTo(parentFrame);
        pagingDialog.setVisible(true);
    }

}
