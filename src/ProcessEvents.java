import javax.swing.*;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.*;

import static java.lang.Character.isDigit;


public class ProcessEvents {
    static void showCreateProcessMenu(JFrame parentFrame) {
        JDialog createProcessDialog = new JDialog(parentFrame, "Create Process", true);

        createProcessDialog.setSize(400, 350);
        createProcessDialog.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Create New Process", JLabel.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.ITALIC, 24));
        titleLabel.setForeground(new Color(80, 80, 129));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        createProcessDialog.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        inputPanel.setBackground(new Color(244, 244, 244));

        JLabel idLabel = new JLabel("Process ID:");
        JTextField idField = new JTextField(String.valueOf(ProcessManager.getNextPid()));
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

        arrivalTimeField.addKeyListener(numericCheck());
        burstTimeField.addKeyListener(numericCheck());
        priorityField.addKeyListener(numericCheck());

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
            ProcessManager.setNextPid(Integer.parseInt(idField.getText()));
        });

        submitButton.addActionListener(e -> {
            String processId = idField.getText();
            String arrivalTime = arrivalTimeField.getText();
            String burstTime = burstTimeField.getText();
            String priority = priorityField.getText();

            if (ProcessManager.createProcess(Integer.parseInt(processId), Integer.parseInt(arrivalTime), Integer.parseInt(burstTime), Integer.parseInt(priority))) {

                String processDetails = "<html>Process ID: " + processId + "<br>Arrival Time: " + arrivalTime + "<br>Burst Time: " + burstTime + "<br>Priority: " + priority + "</html>";
                JOptionPane.showMessageDialog(createProcessDialog, processDetails + "Process Created Successfully!", "Process Creation", JOptionPane.INFORMATION_MESSAGE);
                ProcessManager.storeProcessQueue("Processes.txt");
            } else {
                String processDetails = "<html>Process ID: " + processId + "</html>";
                JOptionPane.showMessageDialog(createProcessDialog, processDetails + "<html>Process Creation Failed!<br>Process already exists</br></html>", "Process Creation", JOptionPane.ERROR_MESSAGE);
            }

            createProcessDialog.dispose();
        });

        createProcessDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                ProcessManager.setNextPid(Integer.parseInt(idField.getText()));
            }
        });
        createProcessDialog.setLocationRelativeTo(parentFrame);
        createProcessDialog.setVisible(true);
    }

    static void showForkProcessMenu(JFrame parentFrame) {

        JDialog createProcessDialog = new JDialog(parentFrame, "Fork Process", true);

        createProcessDialog.setSize(400, 350);
        createProcessDialog.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Create Fork Process", JLabel.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.ITALIC, 24));
        titleLabel.setForeground(new Color(80, 80, 129));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        createProcessDialog.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        inputPanel.setBackground(new Color(244, 244, 244));

        JLabel idLabel = new JLabel("Process ID:");
        JTextField idField = new JTextField(String.valueOf(ProcessManager.getNextPid()));
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

        arrivalTimeField.addKeyListener(numericCheck());
        burstTimeField.addKeyListener(numericCheck());
        priorityField.addKeyListener(numericCheck());

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
            ProcessManager.setNextPid(Integer.parseInt(idField.getText()));
        });

        submitButton.addActionListener(e -> {
            String processId = idField.getText();
            String arrivalTime = arrivalTimeField.getText();
            String burstTime = burstTimeField.getText();
            String priority = priorityField.getText();

            if (ProcessManager.createForkProcess(Integer.parseInt(processId), Integer.parseInt(arrivalTime), Integer.parseInt(burstTime), Integer.parseInt(priority))) {

                String processDetails = "<html>Process ID: " + processId + "<br>Arrival Time: " + arrivalTime + "<br>Burst Time: " + burstTime + "<br>Priority: " + priority + "</html>";
                JOptionPane.showMessageDialog(createProcessDialog, processDetails + "Process Created Successfully!", "Process Creation", JOptionPane.INFORMATION_MESSAGE);
                ProcessManager.storeProcessQueue("Processes.txt");
            } else {
                String processDetails = "<html>Process ID: " + processId + "</html>";
                JOptionPane.showMessageDialog(createProcessDialog, processDetails + "<html>Process Creation Failed!<br>Process already exists</br></html>", "Process Creation", JOptionPane.ERROR_MESSAGE);
            }

            createProcessDialog.dispose();
        });

        createProcessDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                ProcessManager.setNextPid(Integer.parseInt(idField.getText()));
            }
        });
        createProcessDialog.setLocationRelativeTo(parentFrame);
        createProcessDialog.setVisible(true);
    }

    public static void showDestroyProcessMenu(JFrame parentFrame) {
        JDialog dialog = createDialog(parentFrame);

        JLabel titleLabel = createLabel("Destroy Process");
        dialog.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = createPanel();
        inputPanel.setLayout(new BorderLayout(10, 10));

        JPanel readyPanel = new JPanel(new CardLayout());
        readyPanel.add(QueueDisplayDialog.createProcessQueuePanel(ProcessManager.readyQueue, "True", "Destroy"));

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

    public static void showBlockProcessMenu(JFrame parentFrame) {
        JDialog dialog = createDialog(parentFrame);

        JLabel titleLabel = createLabel("Block Process");
        dialog.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = createPanel();
        inputPanel.setLayout(new BorderLayout(10, 10));

        JPanel readyPanel = new JPanel(new CardLayout());
        readyPanel.add(QueueDisplayDialog.createProcessQueuePanel(ProcessManager.readyQueue, "True", "Blocked"));

        JPanel blockPanel = new JPanel(new CardLayout());
        blockPanel.add(QueueDisplayDialog.createProcessQueuePanel(ProcessManager.blockedQueue, "False", "Blocked"));

        QueueDisplayDialog.RemoteEventSource.addListener(e -> {
            blockPanel.removeAll();
            blockPanel.add(QueueDisplayDialog.createProcessQueuePanel(ProcessManager.blockedQueue, "False", "Blocked"));
            blockPanel.repaint();
            blockPanel.revalidate();
        });

        readyPanel.setPreferredSize(new Dimension(300, 150));
        blockPanel.setPreferredSize(new Dimension(300, 150));
        inputPanel.add(readyPanel, BorderLayout.NORTH);
        inputPanel.add(blockPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        JButton backButton = OSSimulation.createHoverStyledButton("Back");

        buttonPanel.add(backButton);

        backButton.addActionListener(e -> dialog.dispose());

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    public static void showDispatchProcessDialog(JFrame parentFrame) {
        if (ProcessManager.dispatchProcess()) {
            String processDetails = "<html>Process " + ProcessManager.runningQueue.getLast().id + " Dispatched Successfully to Running Queue</html>";
            JOptionPane.showMessageDialog(parentFrame, processDetails, "Process Block", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String processDetails = "<html>Process Dispatching Failed</html>";
            JOptionPane.showMessageDialog(parentFrame, processDetails, "Process Block", JOptionPane.ERROR_MESSAGE);
        }
    }

    static void showSuspendProcessMenu(JFrame parentFrame) {
        JDialog dialog = createDialog(parentFrame);

        JLabel titleLabel = createLabel("Suspend Process");
        dialog.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = createPanel();
        inputPanel.setLayout(new BorderLayout(10, 10));

        JPanel readyPanel = new JPanel(new CardLayout());
        readyPanel.add(QueueDisplayDialog.createProcessQueuePanel(ProcessManager.readyQueue, "True", "Suspended"));

        JPanel blockPanel = new JPanel(new CardLayout());
        blockPanel.add(QueueDisplayDialog.createProcessQueuePanel(ProcessManager.suspendedQueue, "False", "Suspended"));

        QueueDisplayDialog.RemoteEventSource.addListener(e -> {
            blockPanel.removeAll();
            blockPanel.add(QueueDisplayDialog.createProcessQueuePanel(ProcessManager.suspendedQueue, "False", "Suspended"));
            blockPanel.repaint();
            blockPanel.revalidate();
        });

        readyPanel.setPreferredSize(new Dimension(300, 150));
        blockPanel.setPreferredSize(new Dimension(300, 150));
        inputPanel.add(readyPanel, BorderLayout.NORTH);
        inputPanel.add(blockPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        JButton backButton = OSSimulation.createHoverStyledButton("Back");

        buttonPanel.add(backButton);

        backButton.addActionListener(e -> dialog.dispose());

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    static void showWakeUpProcessMenu(JFrame parentFrame) {
        JDialog dialog = createDialog(parentFrame);

        JLabel titleLabel = createLabel("Wake-up Process");
        dialog.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = createPanel();
        inputPanel.setLayout(new BorderLayout(10, 10));

        JPanel readyPanel = new JPanel(new CardLayout());
        readyPanel.add(QueueDisplayDialog.createProcessQueuePanel(ProcessManager.blockedQueue, "True", "Wake"));

        JPanel blockPanel = new JPanel(new CardLayout());
        blockPanel.add(QueueDisplayDialog.createProcessQueuePanel(ProcessManager.readyQueue, "False", "Wake"));

        QueueDisplayDialog.RemoteEventSource.addListener(e -> {
            blockPanel.removeAll();
            blockPanel.add(QueueDisplayDialog.createProcessQueuePanel(ProcessManager.readyQueue, "False", "Wake"));
            blockPanel.repaint();
            blockPanel.revalidate();
        });

        readyPanel.setPreferredSize(new Dimension(300, 150));
        blockPanel.setPreferredSize(new Dimension(300, 150));
        inputPanel.add(readyPanel, BorderLayout.NORTH);
        inputPanel.add(blockPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        JButton backButton = OSSimulation.createHoverStyledButton("Back");

        buttonPanel.add(backButton);

        backButton.addActionListener(e -> dialog.dispose());

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    static void showResumeProcessMenu(JFrame parentFrame) {
        JDialog dialog = createDialog(parentFrame);

        JLabel titleLabel = createLabel("Resume Process");
        dialog.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = createPanel();
        inputPanel.setLayout(new BorderLayout(10, 10));

        JPanel readyPanel = new JPanel(new CardLayout());
        readyPanel.add(QueueDisplayDialog.createProcessQueuePanel(ProcessManager.suspendedQueue, "True", "Resume"));

        JPanel blockPanel = new JPanel(new CardLayout());
        blockPanel.add(QueueDisplayDialog.createProcessQueuePanel(ProcessManager.readyQueue, "False", "Resume"));

        QueueDisplayDialog.RemoteEventSource.addListener(e -> {
            blockPanel.removeAll();
            blockPanel.add(QueueDisplayDialog.createProcessQueuePanel(ProcessManager.readyQueue, "False", "Resume"));
            blockPanel.repaint();
            blockPanel.revalidate();
        });

        readyPanel.setPreferredSize(new Dimension(300, 150));
        blockPanel.setPreferredSize(new Dimension(300, 150));
        inputPanel.add(readyPanel, BorderLayout.NORTH);
        inputPanel.add(blockPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        JButton backButton = OSSimulation.createHoverStyledButton("Back");

        buttonPanel.add(backButton);

        backButton.addActionListener(e -> dialog.dispose());

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    static void showChangePriorityMenu(JFrame parentFrame) {
        JDialog dialog = createDialog(parentFrame);

        JLabel titleLabel = createLabel("Change Priority");
        dialog.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = createPanel();
        inputPanel.setLayout(new BorderLayout(10, 10));

        JPanel readyPanel = new JPanel(new CardLayout());
        readyPanel.add(QueueDisplayDialog.createProcessQueuePanel(ProcessManager.readyQueue, "True", "Priority"));

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

    static void showDisplayMenu(JFrame parentFrame) {
        JFrame processFrame = createFrame(parentFrame);
        processFrame.setSize(400, 300);

        JLabel titleLabel = createLabel("Process Queues");
        processFrame.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = createPanel();
        inputPanel.setLayout(new GridLayout(2, 2, 10, 10));
        JButton readyQueue = OSSimulation.createHoverStyledButton("Ready Queue");
        JButton runningQueue = OSSimulation.createHoverStyledButton("Running Queue");
        JButton blockedQueue = OSSimulation.createHoverStyledButton("Blocked Queue");
        JButton suspendedQueue = OSSimulation.createHoverStyledButton("Suspended Queue");

        inputPanel.add(readyQueue);
        inputPanel.add(runningQueue);
        inputPanel.add(suspendedQueue);
        inputPanel.add(blockedQueue);
        processFrame.add(inputPanel, BorderLayout.CENTER);

        JButton backButton = OSSimulation.createHoverStyledButton("Back");
        backButton.addActionListener(e -> {
            processFrame.dispose();
            parentFrame.setVisible(true);
        });

        JPanel backPanel = new JPanel(new BorderLayout(5, 5));
        JLabel footerLabel = OSSimulation.createFooterLabel("GeneSect");
        backPanel.add(backButton, BorderLayout.WEST);
        backPanel.add(footerLabel, BorderLayout.CENTER);
        backPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        backPanel.setBackground(new Color(244, 244, 244));
        processFrame.add(backPanel, BorderLayout.SOUTH);

        readyQueue.addActionListener(e -> ProcessManager.displayReady(processFrame));
        runningQueue.addActionListener(e -> ProcessManager.displayRunning(processFrame));
        suspendedQueue.addActionListener(e -> ProcessManager.displaySuspended(processFrame));
        blockedQueue.addActionListener(e -> ProcessManager.displayBlocked(processFrame));

        processFrame.setLocationRelativeTo(parentFrame);
        processFrame.setVisible(true);
        parentFrame.setVisible(false);
    }

    public static void showConfigurationDialog(JFrame parentFrame, ProcessManager.Configuration config) {
        JDialog dialog = new JDialog(parentFrame, "Configuration Settings", true);
        dialog.setSize(350, 250);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(null);

        JPanel inputPanel = createPanel();
        inputPanel.setLayout(new GridLayout(4, 2, 10 , 10));

        JLabel algorithmLabel = new JLabel("Scheduling Algorithm:");
        String[] algorithms = {"SJF", "LJF", "Round Robin"};
        JComboBox<String> algorithmDropdown = new JComboBox<>(algorithms);
        algorithmDropdown.setSelectedItem(config.schedulingAlgorithm);
        algorithmDropdown.setUI(comboUI(algorithmDropdown));
        algorithmDropdown.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        algorithmDropdown.setBackground(Color.WHITE);
        algorithmDropdown.setFocusable(false);

        JLabel preemptiveLabel = new JLabel("Preemptive Scheduling:");
        String[] preemptiveOptions = {"Yes", "No"};
        JComboBox<String> preemptiveDropdown = new JComboBox<>(preemptiveOptions);
//        preemptiveDropdown.setSelectedIndex(config.preemptiveScheduling ? 0 : 1);
        preemptiveDropdown.setUI(comboUI(preemptiveDropdown));
        preemptiveDropdown.setFocusable(false);
        preemptiveDropdown.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        preemptiveDropdown.setBackground(Color.WHITE);

        JLabel quantumLabel = new JLabel("Time Quantum");
        JTextField timeQuantum = new JTextField(config.timeQuantum);
        timeQuantum.addKeyListener(numericCheck());

        preemptiveDropdown.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(preemptiveDropdown.getSelectedItem().equals("No")) {
                    DefaultComboBoxModel<String> newBox = new DefaultComboBoxModel<>();
                    newBox.addElement("SJF");
                    newBox.addElement("LJF");
                    newBox.addElement("Priority");
                    algorithmDropdown.setModel(newBox);
                    quantumLabel.setEnabled(false);
                    timeQuantum.setEditable(false);
                    timeQuantum.setFocusable(false);
                } else {
                    DefaultComboBoxModel<String> newBox = new DefaultComboBoxModel<>();
                    newBox.addElement("SJF");
                    newBox.addElement("LJF");
                    newBox.addElement("Round Robin");
                    quantumLabel.setEnabled(true);
                    timeQuantum.setEditable(true);
                    timeQuantum.setFocusable(true);
                    algorithmDropdown.setModel(newBox);
                }
            }
        });

        JLabel memoryLabel = new JLabel("Memory Allocation:");
        JTextField memoryField = new JTextField(config.memoryAllocate);
        memoryField.addKeyListener(numericCheck());

        inputPanel.add(preemptiveLabel);
        inputPanel.add(preemptiveDropdown);
        inputPanel.add(algorithmLabel);
        inputPanel.add(algorithmDropdown);
        inputPanel.add(quantumLabel);
        inputPanel.add(timeQuantum);
        inputPanel.add(memoryLabel);
        inputPanel.add(memoryField);
        dialog.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton submitButton = OSSimulation.createHoverStyledButton("Submit");
        JButton backButton = OSSimulation.createHoverStyledButton("Back");

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (preemptiveDropdown.getSelectedItem() != null && preemptiveDropdown.getSelectedItem().equals("Yes")) {
                    config.schedulingAlgorithm = (String) algorithmDropdown.getSelectedItem();
                    config.preemptiveScheduling = preemptiveDropdown.getSelectedIndex() == 0;
                    config.memoryAllocate = memoryField.getText();
                    config.timeQuantum = Integer.parseInt(timeQuantum.getText());
                } else {
                    config.schedulingAlgorithm = (String) algorithmDropdown.getSelectedItem();
                    config.preemptiveScheduling = preemptiveDropdown.getSelectedIndex() == 0;
                    config.memoryAllocate = memoryField.getText();
                }
                dialog.dispose();
                JOptionPane.showMessageDialog(dialog, "Configuration Saved!");
            }
        });

        backButton.addActionListener(e -> dialog.dispose());

        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    public static int changePriorityMethod(int id, int priority) {
        JDialog dialog = createDialog(null);
        dialog.setSize(350, 250);

        JLabel titleLabel = createLabel("Change Priority");

        JPanel inputPanel = createPanel();
        inputPanel.setLayout(new GridLayout(2, 2, 10, 10));

        JLabel idLabel = new JLabel("Process ID:");
        JTextField idField = new JTextField(String.valueOf(id));
        idField.setFocusable(false);
        idField.setEditable(false);
        JLabel priorityLabel = new JLabel("Priority:");
        JTextField priorityField = new JTextField(String.valueOf(priority));

        priorityField.addKeyListener(numericCheck());

        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(priorityLabel);
        inputPanel.add(priorityField);

        JPanel buttonPanel = new JPanel();
        JButton submitButton = OSSimulation.createHoverStyledButton("Submit");
        JButton backButton = OSSimulation.createHoverStyledButton("Back");

        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);

        backButton.addActionListener(e -> {
            dialog.dispose();
        });

        submitButton.addActionListener(e -> {
            ProcessManager.changePriority(Integer.parseInt(idField.getText()), Integer.parseInt(priorityField.getText()));
            dialog.dispose();
        });

        dialog.add(titleLabel, BorderLayout.NORTH);
        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        return Integer.parseInt(priorityField.getText());
    }

    public static JFrame createFrame (JFrame parentFrame){
        JFrame processFrame = new JFrame("Process Execution");
        processFrame.setSize(600, 600);
        processFrame.setLayout(new BorderLayout());
        processFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                processFrame.dispose();
                parentFrame.setVisible(true);
            }
        });
        return processFrame;
    }

    public static JDialog createDialog (JFrame parentFrame){
        JDialog dialog = new JDialog(parentFrame, "Block Process", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(500, 600);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(parentFrame);
        return dialog;
    }

    public static JLabel createLabel (String text){
        JLabel titleLabel = new JLabel(text, JLabel.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.ITALIC, 24));
        titleLabel.setForeground(new Color(80, 80, 129));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return titleLabel;
    }

    public static JPanel createPanel () {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(1, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        inputPanel.setBackground(new Color(244, 244, 244));
        return inputPanel;
    }

    public static KeyAdapter numericCheck () {
            return new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    super.keyTyped(e);
                    if (!isDigit(e.getKeyChar())) {
                        e.consume();
                    }
                }
            };
        }

    public static ComboBoxUI comboUI(JComboBox<String> categoryField) {
        return new BasicComboBoxUI() {
            @Override
            protected ComboBoxEditor createEditor() {
                return new ComboBoxEditor() {
                    private final JTextField editor = new JTextField();
                    {
                        editor.setBorder(BorderFactory.createLineBorder(Color.black));
                    }
                    @Override
                    public Component getEditorComponent() {
                        return editor;
                    }

                    @Override
                    public void setItem(Object anObject) {

                    }

                    @Override
                    public Object getItem() {
                        return null;
                    }

                    @Override
                    public void selectAll() {

                    }

                    @Override
                    public void addActionListener(ActionListener l) {

                    }

                    @Override
                    public void removeActionListener(ActionListener l) {

                    }
                };
            }
            protected JButton createArrowButton() {
                JButton button = new JButton();
                button.setIcon(new ImageIcon("images/dropdown.png"));
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setBackground(comboBox.getBackground());
                return button;
            }
        };
    }
}
