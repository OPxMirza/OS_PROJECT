import javax.swing.*;
import java.awt.*;
import java.util.*;

public class ReplacementAlgorithm {
    public static void handleReplacementAlgorithm(JDialog parentDialog, String framesInput, String referenceStringInput, String selectedAlgorithm) {
        try {
            int frames = Integer.parseInt(framesInput);
            if (frames <= 0) {
                throw new NumberFormatException("Frames must be greater than zero.");
            }

            String[] referenceStringTokens = referenceStringInput.split(",");
            int[] referenceString = new int[referenceStringTokens.length];

            for (int i = 0; i < referenceStringTokens.length; i++) {
                referenceString[i] = Integer.parseInt(referenceStringTokens[i].trim());
            }

            Object[][] result;
            if ("FIFO".equals(selectedAlgorithm)) {
                result = performFIFOReplacement(referenceString, frames);
            } else if ("LRU".equals(selectedAlgorithm)) {
                result = performLRUReplacement(referenceString, frames);
            } else {
                result = performOptimalReplacement(referenceString, frames);
            }

            JScrollPane tableScrollPane = QueueDisplayDialog.createResultTable(result);

            JDialog resultDialog = new JDialog(parentDialog, "Replacement Result - " + selectedAlgorithm, true);
            resultDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            resultDialog.setSize(600, 400);
            resultDialog.setLocationRelativeTo(parentDialog);
            resultDialog.setLayout(new BorderLayout());

            resultDialog.add(tableScrollPane, BorderLayout.CENTER);

            JButton closeButton = OSSimulation.createHoverStyledButton("Back");
            closeButton.addActionListener(e -> resultDialog.dispose());
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(closeButton);

            resultDialog.add(buttonPanel, BorderLayout.SOUTH);

            resultDialog.setVisible(true);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    parentDialog,
                    "Invalid input! Please enter valid numeric values for frames and reference string.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    public static Object[][] performLRUReplacement(int[] incomingStream, int frames) {
        int n = incomingStream.length;
        int[] queue = new int[frames];
        int[] distance = new int[frames];
        int occupied = 0;
        int pagefault = 0;

        ArrayList<Object[]> tableData = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            int currentPage = incomingStream[i];

            if (checkHit(currentPage, queue, occupied)) {
                String pagesInMemory = Arrays.toString(queue);
                tableData.add(new Object[]{currentPage, pagesInMemory, "No"});
            } else if (occupied < frames) {
                queue[occupied] = currentPage;
                pagefault++;
                occupied++;
                String pagesInMemory = Arrays.toString(queue);
                tableData.add(new Object[]{currentPage, pagesInMemory, "Yes"});
            } else {
                int max = Integer.MIN_VALUE;
                int index = -1;
                for (int j = 0; j < frames; j++) {
                    distance[j] = 0;
                    for (int k = i - 1; k >= 0; k--) {
                        ++distance[j];
                        if (queue[j] == incomingStream[k]) break;
                    }
                    if (distance[j] > max) {
                        max = distance[j];
                        index = j;
                    }
                }
                queue[index] = currentPage;
                pagefault++;
                String pagesInMemory = Arrays.toString(queue);
                tableData.add(new Object[]{currentPage, pagesInMemory, "Yes"});
            }
            System.out.println();
        }

        Object[][] result = new Object[tableData.size()][3];
        for (int i = 0; i < tableData.size(); i++) {
            result[i] = tableData.get(i);
        }

        return result;
    }

    static boolean checkHit(int incomingPage, int[] queue, int occupied) {
        for (int i = 0; i < occupied; i++)
        {
            if (incomingPage == queue[i])
                return true;
        }
        return false;
    }

    public static Object[][] performOptimalReplacement(int[] referenceString, int frames) {
        ArrayList<Object[]> tableData = new ArrayList<>();
        int[] buffer = new int[frames];
        int faults = 0, hits = 0, pointer = 0;
        boolean isFull = false;

        for (int i = 0; i < frames; i++) {
            buffer[i] = -1;
        }

        for (int i = 0; i < referenceString.length; i++) {
            boolean hit = false;

            for (int j = 0; j < frames; j++) {
                if (buffer[j] == referenceString[i]) {
                    hit = true;
                    hits++;
                    break;
                }
            }

            String pagesInMemory;

            if (!hit) {
                if (isFull) {
                    int[] futureIndex = new int[frames];
                    boolean[] found = new boolean[frames];

                    for (int k = 0; k < frames; k++) {
                        futureIndex[k] = Integer.MAX_VALUE;
                    }

                    for (int j = i + 1; j < referenceString.length; j++) {
                        for (int k = 0; k < frames; k++) {
                            if (buffer[k] == referenceString[j] && !found[k]) {
                                futureIndex[k] = j;
                                found[k] = true;
                                break;
                            }
                        }
                    }

                    int farthest = -1, replaceIndex = -1;
                    for (int k = 0; k < frames; k++) {
                        if (!found[k]) {
                        replaceIndex = k;
                        break;
                    } else if (futureIndex[k] > farthest) {
                        farthest = futureIndex[k];
                        replaceIndex = k;
                    }
                    }

                    buffer[replaceIndex] = referenceString[i];
                } else {
                    buffer[pointer] = referenceString[i];
                    pointer++;
                    if (pointer == frames) {
                        isFull = true;
                    }
                }

                faults++;
            }

            pagesInMemory = Arrays.toString(buffer).replaceAll("[\\[\\],]", "");
            tableData.add(new Object[]{referenceString[i], pagesInMemory, hit ? "No" : "Yes"});
        }

        Object[][] result = new Object[tableData.size()][3];
        for (int i = 0; i < tableData.size(); i++) {
            result[i] = tableData.get(i);
        }

        System.out.println("Faults: " + faults);
        System.out.println("Hits: " + hits);
        System.out.println("Hit Ratio: " + (float) hits / referenceString.length);

        return result;
    }

    public static Object[][] performFIFOReplacement(int[] incomingStream, int frames) {
        HashSet<Integer> pageSet = new HashSet<>(frames);
        Queue<Integer> pageQueue = new LinkedList<>();

        ArrayList<Object[]> tableData = new ArrayList<>();
        int pageFaults = 0;

        for (int i = 0; i < incomingStream.length; i++) {
            int currentPage = incomingStream[i];
            boolean isHit = pageSet.contains(currentPage);

            if (!isHit) {
                if (pageSet.size() < frames) {
                    if (!pageSet.contains(currentPage)) {
                        pageSet.add(currentPage);
                        pageQueue.add(currentPage);
                        pageFaults++;
                    }
                } else {
                    int removedPage = pageQueue.poll();
                    pageSet.remove(removedPage);

                    pageSet.add(currentPage);
                    pageQueue.add(currentPage);
                    pageFaults++;
                }
            }

            String pagesInMemory = pageQueue.toString();
            tableData.add(new Object[]{currentPage, pagesInMemory, isHit ? "No" : "Yes"});
        }

        Object[][] result = new Object[tableData.size()][3];
        for (int i = 0; i < tableData.size(); i++) {
            result[i] = tableData.get(i);
        }

        return result;
    }

}
