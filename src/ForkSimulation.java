import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.io.*;
import java.util.*;

class ForkSimulation {

    private static JTree tree;
    private static JTextArea outputArea;
    private static HashMap<Integer, ProcessManager.Process> hashMap = new HashMap<>();

    public static void forkSimulator(JFrame parentFrame) {
        JDialog dialog = new JDialog(parentFrame, "Fork Simulation", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());

        ArrayList<ProcessManager.Process> readyQueue = loadProcessQueue("Processes.txt");

        try {
            for (ProcessManager.Process p : readyQueue) {
                ProcessManager.processMap.put(p.id, p);
                System.out.println(p.id);
            }
            for (ProcessManager.Process p : readyQueue) {
                hashMap.put(p.id, ProcessManager.processMap.get(p.id));
            }

            ProcessManager.Process rootProcess = new ProcessManager.Process();
            rootProcess.id = 0;
            rootProcess.parentPid = -1;
            ProcessManager.processMap.put(rootProcess.id, rootProcess);
            hashMap.put(rootProcess.id, rootProcess);

            DefaultMutableTreeNode rootNode = createTreeNodes(0);
            if (rootNode == null) {
                JOptionPane.showMessageDialog(dialog, "No root process found.", "Error", JOptionPane.ERROR_MESSAGE);
                dialog.dispose();
            }

            tree = new JTree(new DefaultTreeModel(rootNode));
            JScrollPane treeScrollPane = new JScrollPane(tree);

            outputArea = new JTextArea();
            outputArea.setEditable(false);
            JScrollPane outputScrollPane = new JScrollPane(outputArea);

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, outputScrollPane);
            splitPane.setResizeWeight(0.5);

            dialog.add(splitPane, BorderLayout.CENTER);

            displayParentChildRelationships(readyQueue);
            simulateExecution(readyQueue);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog, "Fork Simulation Failed! Create a process.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }

    private static DefaultMutableTreeNode createTreeNodes(int processId) {
        ProcessManager.Process p = hashMap.get(processId);
        if (p == null) return null;

        DefaultMutableTreeNode node;

        if(processId == 0) {
             node = new DefaultMutableTreeNode("Process " + p.id + " (Default Root)");
        } else {
            node = new DefaultMutableTreeNode("Process " + p.id + " (Parent: " + p.parentPid + ")");
        }

        for (ProcessManager.Process potentialChild : ProcessManager.processMap.values()) {
            if (potentialChild.parentPid == p.id) {
                DefaultMutableTreeNode childNode = createTreeNodes(potentialChild.id);
                if (childNode != null) {
                    node.add(childNode);
                }
            }
        }

        return node;
    }

    private static void displayParentChildRelationships(ArrayList<ProcessManager.Process> readyQueue) {
        StringBuilder output = new StringBuilder("Parent-Child Relationships:\n");
        for (ProcessManager.Process p : readyQueue) {
            output.append("Process ").append(p.id)
                    .append(" (Parent: ").append(p.parentPid == -1 ? "None" : p.parentPid)
                    .append(") -> Children: ")
                    .append(p.children.isEmpty() ? "None" : p.children)
                    .append("\n");
        }
        outputArea.setText(output.toString());
    }

    private static void simulateExecution(ArrayList<ProcessManager.Process> readyQueue) {
        StringBuilder output = new StringBuilder("\nProcess Execution:\n");
        for (ProcessManager.Process p : readyQueue) {
            output.append("Executing Process ").append(p.id)
                    .append(" (Priority: ").append(p.priority).append(", State: ").append(p.state).append(")\n");
            p.state = "Running";
            p.ct = p.at + p.bt;
            p.tat = p.ct - p.at;
            p.wt = p.tat - p.bt;
            output.append("Completion Time: ").append(p.ct).append(", Turnaround Time: ").append(p.tat)
                    .append(", Waiting Time: ").append(p.wt).append("\n");
            p.state = "Completed";
        }
        outputArea.append(output.toString());
    }

    public static ArrayList<ProcessManager.Process> loadProcessQueue(String filename) {
        ArrayList<ProcessManager.Process> readyQueue = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            readyQueue = (ArrayList<ProcessManager.Process>) ois.readObject();
            System.out.println("Process queue loaded successfully from " + filename);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading process queue: " + e.getMessage());
        }
        return readyQueue;
    }
}
