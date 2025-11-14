import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class OSSimulation {

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        showWelcomeScreen();
    }

    private static void showWelcomeScreen() {
        JFrame splashFrame = new JFrame();
        splashFrame.setUndecorated(true);
        splashFrame.setSize(600, 400);
        splashFrame.setLayout(new BorderLayout());
        splashFrame.setLocationRelativeTo(null);

        JLabel welcomeLabel = new JLabel("Welcome to GeneSect OS", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Ubuntu", Font.ITALIC, 36)); // Set heading to italic
        welcomeLabel.setForeground(new Color(80, 80, 129)); // Updated heading color
        splashFrame.add(welcomeLabel, BorderLayout.CENTER);

        splashFrame.getContentPane().setBackground(new Color(39, 39, 87)); // Background color for welcome screen
        splashFrame.setVisible(true);

        Timer splashTimer = new Timer(3000, e -> {
            splashFrame.dispose();
            showMainMenu();
        });
        splashTimer.setRepeats(false);
        splashTimer.start();
    }

    private static void showMainMenu() {
        String osName = "GeneSect OS";

        JFrame frame = new JFrame(osName + " - Control Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel(osName + " Control Panel", JLabel.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.ITALIC, 28));
        titleLabel.setForeground(new Color(80, 80, 129));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonPanel.setBackground(new Color(244, 244, 244));

        JButton processButton = createHoverStyledButton("Process Management");
        JButton memoryButton = createHoverStyledButton("Memory Management");
        JButton ioButton = createHoverStyledButton("Bankers Algorithm");
        JButton otherButton = createHoverStyledButton("Fork Simulator");

        buttonPanel.add(processButton);
        buttonPanel.add(memoryButton);
        buttonPanel.add(ioButton);
        buttonPanel.add(otherButton);

        frame.add(buttonPanel, BorderLayout.CENTER);

        JLabel footerLabel = createFooterLabel(osName);
        frame.add(footerLabel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                File file = new File("Processes.txt");
                System.out.println(file.delete());
            }
        });

        processButton.addActionListener(e -> showProcessManagementMenu(frame));
        memoryButton.addActionListener(e -> MemoryManager.showMemoryManagementMenu(frame));
        ioButton.addActionListener(e -> BankersAlgorithm.bankers(frame) );
        otherButton.addActionListener(e -> ForkSimulation.forkSimulator(frame));
    }

    public static JLabel createFooterLabel(String osName) {
        JLabel footerLabel = new JLabel("Powered by " + osName, JLabel.CENTER);
        footerLabel.setFont(new Font("Roboto", Font.ITALIC, 12));
        footerLabel.setForeground(new Color(97, 97, 97));
        footerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return footerLabel;
    }

    private static void showProcessManagementMenu(JFrame parentFrame) {
        JFrame processFrame = ProcessEvents.createFrame(parentFrame);
        ProcessManager.Configuration config = new ProcessManager.Configuration();

        JLabel titleLabel = ProcessEvents.createLabel("Process Management");
        processFrame.add(titleLabel, BorderLayout.NORTH);

        JPanel processPanel = new JPanel();
        processPanel.setLayout(new GridLayout(6, 2, 12, 12));
        processPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        processPanel.setBackground(new Color(244, 244, 244));

        JButton createButton = createHoverStyledButton("Create Process");
        JButton destroyButton = createHoverStyledButton("Destroy Process");
        JButton suspendButton = createHoverStyledButton("Suspend Process");
        JButton resumeButton = createHoverStyledButton("Resume Process");
        JButton blockButton = createHoverStyledButton("Block Process");
        JButton wakeupButton = createHoverStyledButton("Wakeup Process");
        JButton dispatchButton = createHoverStyledButton("Dispatch Process");
        JButton priorityButton = createHoverStyledButton("Change Priority");
        JButton executeButton = createHoverStyledButton("Execute Processes");
        JButton displayButton = createHoverStyledButton("Display Processes");
        JButton forkButton = createHoverStyledButton("Fork Process");
        JButton configButton = createHoverStyledButton("Configuration");

        processPanel.add(createButton);
        processPanel.add(forkButton);
        processPanel.add(destroyButton);
        processPanel.add(suspendButton);
        processPanel.add(resumeButton);
        processPanel.add(blockButton);
        processPanel.add(wakeupButton);
        processPanel.add(dispatchButton);
        processPanel.add(priorityButton);
        processPanel.add(executeButton);
        processPanel.add(displayButton);
        processPanel.add(configButton);

        processFrame.add(processPanel, BorderLayout.CENTER);

        JButton backButton = createHoverStyledButton("Back");
        backButton.addActionListener(e -> {
            ProcessManager.storeProcessQueue("Processes.txt");
            processFrame.dispose();
            parentFrame.setVisible(true);
        });

        JPanel backPanel = new JPanel(new BorderLayout(5, 5));
        JLabel footerLabel = createFooterLabel("GeneSect");
        backPanel.add(backButton, BorderLayout.WEST);
        backPanel.add(footerLabel, BorderLayout.CENTER);
        backPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        backPanel.setBackground(new Color(244, 244, 244));

        processFrame.add(backPanel, BorderLayout.SOUTH);

        processFrame.setLocationRelativeTo(parentFrame);
        processFrame.setVisible(true);
        parentFrame.setVisible(false);

        createButton.addActionListener(e -> ProcessEvents.showCreateProcessMenu(processFrame));
        forkButton.addActionListener(e ->ProcessEvents.showForkProcessMenu(processFrame));
        displayButton.addActionListener(e -> ProcessEvents.showDisplayMenu(processFrame));
        destroyButton.addActionListener(e -> ProcessEvents.showDestroyProcessMenu(processFrame));
        blockButton.addActionListener(e -> ProcessEvents.showBlockProcessMenu(processFrame));
        dispatchButton.addActionListener(e -> ProcessEvents.showDispatchProcessDialog(processFrame));
        suspendButton.addActionListener(e -> ProcessEvents.showSuspendProcessMenu(processFrame));
        wakeupButton.addActionListener(e -> ProcessEvents.showWakeUpProcessMenu(processFrame));
        resumeButton.addActionListener(e -> ProcessEvents.showResumeProcessMenu(processFrame));
        priorityButton.addActionListener(e -> ProcessEvents.showChangePriorityMenu(processFrame));
        executeButton.addActionListener(e -> ProcessManager.executeRunningQueue(processFrame, config));
        configButton.addActionListener(e -> ProcessEvents.showConfigurationDialog(processFrame, config));
    }

    public static JButton createHoverStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                // Draw centered text
                FontMetrics fm = g2.getFontMetrics();
                int stringWidth = fm.stringWidth(getText());
                int stringHeight = fm.getAscent();
                int x = (getWidth() - stringWidth) / 2;
                int y = (getHeight() + stringHeight) / 2 - 2; // Adjust text alignment
                g2.setColor(getForeground());
                g2.drawString(getText(), x, y);

                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw border
                g2.setColor(Color.GRAY);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
            }
        };

        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Roboto", Font.PLAIN, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(39, 39, 87)); // Button color updated
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(80, 80, 129)); // Hover effect updated
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(39, 39, 87)); // Reset to original color
            }
        });

        return button;
    }
}