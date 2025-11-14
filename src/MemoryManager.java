import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MemoryManager {

    static ArrayList<Segment> segments = new ArrayList<>();
    static int nextSegment = 1;

    static class MemoryConfigure {
        static int memorySize = 500;
        static int pageSize = 4;
        static String replacementAlgorithm = "FIFO";
        static int numFrames = 0;
    }

    static class Segment {
        int segmentId;
        int baseAddress;
        int limit;
        boolean trap;
        int physicalAddressSpace;
    }

    static int getNextSegment() {
        return nextSegment++;
    }

    static void setNextSegment(int id) {
        nextSegment = id;
    }

    static boolean createSegment(int id, int baseAddress, int limit) {
        Segment segment = new Segment();
        segment.segmentId = id;
        segment.baseAddress = baseAddress;
        segment.limit = limit;
        segments.add(segment);
        return true;
    }

    public static void showMemoryManagementMenu(JFrame parentFrame) {
        JFrame memoryFrame = ProcessEvents.createFrame(parentFrame);
        memoryFrame.setSize(500, 380);
        MemoryConfigure config = new MemoryConfigure();

        JLabel titleLabel = ProcessEvents.createLabel("Memory Management");
        memoryFrame.add(titleLabel, BorderLayout.NORTH);

        JPanel processPanel = new JPanel();
        processPanel.setLayout(new GridLayout(3, 2, 12, 12));
        processPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        processPanel.setBackground(new Color(244, 244, 244));

        JButton allocateButton = OSSimulation.createHoverStyledButton("Allocate Memory");
        JButton pagingButton = OSSimulation.createHoverStyledButton("Process Paging");
        JButton configureButton = OSSimulation.createHoverStyledButton("Configuration");
        JButton replacementButton = OSSimulation.createHoverStyledButton("Replacement");
        JButton framingButton = OSSimulation.createHoverStyledButton("Memory Framing");
        JButton segmentButton = OSSimulation.createHoverStyledButton("Segmentation");

        processPanel.add(allocateButton);
        processPanel.add(pagingButton);
        processPanel.add(configureButton);
        processPanel.add(replacementButton);
        processPanel.add(framingButton);
        processPanel.add(segmentButton);

        allocateButton.addActionListener(e -> MemoryEvents.showAllocateMemory(memoryFrame));
        pagingButton.addActionListener(e -> MemoryEvents.showPagingMemory(memoryFrame));
        replacementButton.addActionListener(e -> MemoryEvents.showReplacementDialog(memoryFrame, config));
        configureButton.addActionListener(e -> MemoryEvents.showMemoryConfigurationDialog(memoryFrame, config));
        framingButton.addActionListener(e -> MemoryEvents.showFramingDialog(memoryFrame, config));
        segmentButton.addActionListener(e -> Segmentation.showSegmentMenu(memoryFrame));

        memoryFrame.add(processPanel, BorderLayout.CENTER);

        JButton backButton = OSSimulation.createHoverStyledButton("Back");
        backButton.addActionListener(e -> {
            memoryFrame.dispose();
            parentFrame.setVisible(true);
        });

        JPanel backPanel = new JPanel(new BorderLayout(5, 5));
        JLabel footerLabel = OSSimulation.createFooterLabel("GeneSect");
        backPanel.add(backButton, BorderLayout.WEST);
        backPanel.add(footerLabel, BorderLayout.CENTER);
        backPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        backPanel.setBackground(new Color(244, 244, 244));

        memoryFrame.add(backPanel, BorderLayout.SOUTH);

        memoryFrame.setLocationRelativeTo(parentFrame);
        memoryFrame.setVisible(true);
        parentFrame.setVisible(false);

    }
}