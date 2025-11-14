import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.*;

class QueueDisplayDialog extends JDialog {
    private final ArrayList<ProcessManager.Process> readyQueue;

    public static class RemoteEventSource {
        private static ArrayList<RemoteEventListener> listeners = new ArrayList<>();

        public static void addListener(RemoteEventListener listener) {
            listeners.add(listener);
        }

        public static void triggerEvent(EventObject e) {
            for (RemoteEventListener listener : listeners) {
                listener.handleRemoteEvent(e);
            }
        }
    }

    public interface RemoteEventListener {
        void handleRemoteEvent(EventObject e);
    }

    public QueueDisplayDialog(Frame owner, ArrayList<ProcessManager.Process> readyQueue) {
        super(owner, "Process Queues", true);
        this.readyQueue = readyQueue;

        setLayout(new BorderLayout());

        JPanel readyPanel = ProcessEvents.createPanel();

        boolean isTerminated = false;
        for (ProcessManager.Process proc : readyQueue) {
            if(proc.state.equals("Completed")) {
                isTerminated = true;
                break;
            }
        }

        if(isTerminated) {
            readyPanel.add(createExecutedTable(readyQueue));
        } else {
            readyPanel.add(createTableForQueue(readyQueue));
        }

        add(readyPanel, BorderLayout.CENTER);

        JButton closeButton = OSSimulation.createHoverStyledButton("Close");
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(owner);
    }

    private static JScrollPane createTableForQueue(ArrayList<ProcessManager.Process> queue) {
        String[] columnNames = {"ID", "Arrival Time", "Burst Time", "Priority", "State"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);


        for (ProcessManager.Process p : queue) {
            tableModel.addRow(new Object[]{
                    p.id,
                    p.at,
                    p.bt,
                    p.priority,
                    p.state
            });
        }

        JTable table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.getTableHeader().setReorderingAllowed(false);

        return new JScrollPane(table);
    }

    private static JScrollPane createExecutedTable(ArrayList<ProcessManager.Process> queue) {
        String[] columnNames = {"ID", "Arrival Time", "Burst Time", "Completion Time", "TAT", "WT", "Priority", "State"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        for (ProcessManager.Process p : queue) {
            tableModel.addRow(new Object[]{
                    p.id,
                    p.at,
                    p.remainingBt,
                    p.ct >= 0 ? p.ct : "N/A",
                    p.tat >= 0 ? p.tat : "N/A",
                    p.wt >= 0 ? p.wt : "N/A",
                    p.priority,
                    p.state
            });
        }

        JTable table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.getTableHeader().setReorderingAllowed(false);

        return new JScrollPane(table);
    }

    private static JScrollPane createEditableTableForQueue(ArrayList<ProcessManager.Process> queue, String editQueue) {
        String[] columnNames = {"ID", "Arrival Time", "Burst Time", "Priority", "State"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);


        for (ProcessManager.Process p : queue) {
            tableModel.addRow(new Object[]{
                    p.id,
                    p.at,
                    p.bt,
                    p.priority,
                    p.state
            });
        }

        JTable table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.getTableHeader().setReorderingAllowed(false);

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow >= 0) {
                        if (editQueue.equals("Priority")) {
                            int newPriority =  ProcessEvents.changePriorityMethod(queue.get(selectedRow).id, queue.get(selectedRow).priority);
                            if (newPriority != -1) {
                                queue.get(selectedRow).priority = newPriority;
                                tableModel.setValueAt(newPriority, selectedRow, 3);
                            }
                        } else {
                            int confirm = JOptionPane.showConfirmDialog(
                                    null,
                                    "Are you sure you want to remove this process?",
                                    "Confirm Removal",
                                    JOptionPane.YES_NO_OPTION);

                            if (confirm == JOptionPane.YES_OPTION) {
                                ProcessManager.removeFromQueue(queue, selectedRow, editQueue);
                                RemoteEventSource.triggerEvent(e);
                                ((DefaultTableModel) table.getModel()).removeRow(selectedRow);
                            }
                        }
                        table.clearSelection();
                    }
                }
            }
        });

        return new JScrollPane(table);
    }

    public static JPanel createProcessQueuePanel(ArrayList<ProcessManager.Process> readyQueue, String editable, String queue) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel readyPanel = ProcessEvents.createPanel();

        if(editable.equals("True")) {
            readyPanel.add(createEditableTableForQueue(readyQueue, queue));
        } else {
            readyPanel.add(createTableForQueue(readyQueue));
        }

        panel.add(readyPanel, BorderLayout.CENTER);

        return panel;
    }

    private static JScrollPane createTableWithMemoryAllocation(ArrayList<ProcessManager.Process> queue, String line) {
        String[] columnNames = {"ID", "Arrival Time", "Burst Time", "Priority", "State", "Size", "Pages"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        for (ProcessManager.Process p : queue) {
            tableModel.addRow(new Object[]{
                    p.id,
                    p.at,
                    p.bt,
                    p.priority,
                    p.state,
                    p.memory,
                    p.pages
            });
        }

        JTable table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.getTableHeader().setReorderingAllowed(false);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 1 && table.getSelectedRow() != -1) {
                    int selectedRow = table.getSelectedRow();
                    int processId = (int) table.getValueAt(selectedRow, 0);
                    int memory = (int) table.getValueAt(selectedRow, 5);
                    if(line.equals("Allocated")) {
                        int memoryRequired = MemoryEvents.showMemoryAllocator(null, processId, memory);
                        tableModel.setValueAt(memoryRequired, selectedRow, 5);
                    } else {
                        int pages = MemoryEvents.showPagingDialog(null, memory, processId);
                        tableModel.setValueAt(pages, selectedRow, 6);
                    }
                }
            }
        });

        return new JScrollPane(table);
    }

    public static JPanel showQueueDisplayPanel(ArrayList<ProcessManager.Process> readyQueue, String line) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel readyPanel = ProcessEvents.createPanel();

        readyPanel.add(createTableWithMemoryAllocation(readyQueue, line));

        panel.add(readyPanel, BorderLayout.CENTER);

        return panel;
    }

    public static JScrollPane createResultTable(Object[][] data) {
        String[] columnNames = {"Current Page", "Pages in Memory", "Page Fault"};

        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }

        JTable table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JPanel statsPanel = new JPanel(new GridBagLayout());
        statsPanel.setBorder(new TitledBorder("Page Replacement Statistics"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER ;

        int pageFaults = 0;
        int pageHits = 0;

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            boolean isFault = false;
            if(tableModel.getValueAt(i, 2) == "Yes") {
                isFault = true;
            }
            if (isFault) {
                pageFaults++;
            } else {
                pageHits++;
            }
        }

        int totalAccesses = pageFaults + pageHits;
        double hitRatio = (totalAccesses == 0) ? 0 : (double) pageHits / totalAccesses;
        double faultRatio = (totalAccesses == 0) ? 0 : (double) pageFaults / totalAccesses;

        DecimalFormat df = new DecimalFormat("#.##");

        JTextField totalAccessesField = new JTextField(String.valueOf(totalAccesses));
        JTextField pageHitsField = new JTextField(String.valueOf(pageHits));
        JTextField pageFaultsField = new JTextField(String.valueOf(pageFaults));
        JTextField hitRatioField = new JTextField(df.format(hitRatio * 100) + "%");
        JTextField faultRatioField = new JTextField(df.format(faultRatio * 100) + "%");

        totalAccessesField.setEditable(false);
        pageHitsField.setEditable(false);
        pageFaultsField.setEditable(false);
        hitRatioField.setEditable(false);
        faultRatioField.setEditable(false);

        addLabelAndField(statsPanel, "Total Page Accesses:", totalAccessesField, gbc, 0);
        addLabelAndField(statsPanel, "Page Hits:", pageHitsField, gbc, 1);
        addLabelAndField(statsPanel, "Page Faults:", pageFaultsField, gbc, 2);
        addLabelAndField(statsPanel, "Hit Ratio:", hitRatioField, gbc, 3);
        addLabelAndField(statsPanel, "Miss Ratio:", faultRatioField, gbc, 4);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0);
        splitPane.setTopComponent(new JScrollPane(table));
        splitPane.setBottomComponent(statsPanel);

        return new JScrollPane(splitPane);
    }

    private static void addLabelAndField(JPanel panel, String labelText, JTextField textField, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(textField, gbc);
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
    }

    public static JScrollPane createSegmentTable(JDialog dialog, ArrayList<MemoryManager.Segment> segments) {
        String[] columnNames = {"Segment ID", "Base Address", "Limit", "Trap", "Physical Address Space"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (MemoryManager.Segment segment : segments) {
            Object[] rowData = {segment.segmentId, segment.baseAddress, segment.limit, segment.trap, segment.physicalAddressSpace};
            model.addRow(rowData);
        }

        JTable table = new JTable(model);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int segmentId = (int) model.getValueAt(row, 0);
                    int baseAddress = (int) model.getValueAt(row, 1);
                    int limit = (int) model.getValueAt(row, 2);
                    boolean trap = (boolean) model.getValueAt(row, 3);
                    int physicalAddressSpace = (int) model.getValueAt(row, 4);

                    int address = Segmentation.CreateAddressCalculationDialog(dialog, segmentId, baseAddress, limit);

                    if(address != 0) {
                        model.setValueAt(address, row, 4);
                        model.setValueAt("No", row, 3);
                    } else {
                        model.setValueAt(null, row, 4);
                        model.setValueAt("Yes", row, 3);
                    }

                    String message = "Selected Segment:\n" +
                            "Segment ID: " + segmentId + "\n" +
                            "Base Address: " + baseAddress + "\n" +
                            "Limit: " + limit + "\n" +
                            "Trap: " + trap + "\n" +
                            "Physical Address Space: " + physicalAddressSpace;

                    JOptionPane.showMessageDialog(null, message, "Segment Information", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        return new JScrollPane(table);
    }
}