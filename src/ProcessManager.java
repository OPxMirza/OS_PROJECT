import javax.swing.*;
import java.lang.reflect.Array;
import java.util.*;
import java.io.*;
import java.util.stream.Collectors;

public class ProcessManager {
    static final int MAX_PROCESSES = 100000;
    private static int nextPid = 1;

    static class Process implements Serializable {
        int id;
        int at;
        int bt;
        int ct;
        int tat;
        int wt;
        int parentPid;
        int priority;
        int remainingBt;
        String state;
        int memory = 4;
        int pages = 1;
        ArrayList<Integer> children = new ArrayList<>();
    }

    static class Configuration {
        String schedulingAlgorithm = "SJF";
        int timeQuantum = 1;
        boolean preemptiveScheduling = false;
        String memoryAllocate = "1KB";
    }

    static ArrayList<Process> readyQueue = new ArrayList<>();
    static ArrayList<Process> blockedQueue = new ArrayList<>();
    static ArrayList<Process> suspendedQueue = new ArrayList<>();
    static ArrayList<Process> destroyedQueue = new ArrayList<>();
    static ArrayList<Process> runningQueue = new ArrayList<>();
    static ArrayList<Process> terminateQueue = new ArrayList<>();
    static HashMap<Integer, Process> processMap = new HashMap<>();
    static HashMap<Integer, ArrayList<Integer>> processPagingInfo = new HashMap<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
//        while (true) {
//            System.out.println("\nMenu:");
//            System.out.println("1. Create a process");
//            System.out.println("2. Destroy a process");
//            System.out.println("3. Suspend a process");
//            System.out.println("4. Resume a process");
//            System.out.println("5. Block a process");
//            System.out.println("6. Wakeup a process");
//            System.out.println("7. Dispatch a process");
//            System.out.println("8. Change process priority");
//            System.out.println("9. Display queues");
//            System.out.println("10. Execute Running Queue using LJF");
//            System.out.println("11. Exit");
//            System.out.print("Choose an option: ");
//            int choice = sc.nextInt();
//            switch (choice) {
//                case 7 -> dispatchProcess();
//                case 10 -> executeRunningQueue();
//                case 11 -> {
//                    System.out.println("Exiting...");
//                    return;
//                }
//                default -> System.out.println("Invalid choice. Try again.");
//            }
//        }
    }

    static boolean createProcess(int id, int at, int bt, int priority) {
        System.out.print("Enter Process ID: " + id);
        System.out.print("Enter Arrival Time: " + at);
        System.out.print("Enter Burst Time: " + bt);
        System.out.print("Enter Priority: " + priority);

        Process p = new Process();
        p.id = id;
        p.at = at;
        p.remainingBt = p.bt = bt;
        p.priority = priority;
        p.state = "Ready";
        processMap.put(id, p);
        readyQueue.add(p);
        System.out.println("Process created and added to Ready Queue.");
        return true;
    }

    public static void allocateMemory(int processId, int memoryRequired) {
        for (Process process : readyQueue) {
            if (process.id == processId) {
                process.memory = memoryRequired;
            }
        }
    }

    public static void addPages(int processId, int pages) {
        for (Process process : readyQueue) {
            if (process.id == processId) {
                process.pages = pages;
            }
        }
    }

    static boolean createForkProcess(int id, int at, int bt, int priority) {
        Process parentProcess = new Process();
        parentProcess.id = id;
        parentProcess.parentPid = 0;
        parentProcess.at = at;
        parentProcess.remainingBt = parentProcess.bt = bt;
        parentProcess.priority = priority;
        parentProcess.state = "Ready";

        Process childProcess = new Process();
        childProcess.id = getNextPid();
        childProcess.parentPid = parentProcess.id;
        childProcess.at = at;
        childProcess.remainingBt = childProcess.bt = bt;
        childProcess.priority = priority;
        childProcess.state = "Ready";
        parentProcess.children.add(childProcess.id);

        processMap.put(parentProcess.id, parentProcess);
        readyQueue.add(parentProcess);
        processMap.put(childProcess.id, childProcess);
        readyQueue.add(childProcess);

        System.out.println("Process created (parent: " + parentProcess.id + ", child: " + childProcess.id + ").");
        System.out.println("Parent added to Ready Queue.");
        System.out.println("Child added to Ready Queue.");
        return true;
    }

    public static int getNextPid() {
        return nextPid++;
    }

    public static void setNextPid(int id) {
        nextPid = id;
    }

    static boolean destroyProcess(int id) {
        System.out.print("Process ID to destroy: " + id);

        Process p = processMap.get(id);
        if (p == null) {
            System.out.println("Process not found.");
            return false;
        }

        removeFromAllQueues(p);
        p.state = "Destroyed";
        destroyedQueue.add(p);
        System.out.println("Process destroyed and moved to Destroyed Queue.");
        return true;
    }

    static boolean suspendProcess(int id) {
        System.out.print("Process ID to suspend: " + id);

        Process p = processMap.get(id);
        if (p == null || !readyQueue.contains(p)) {
            System.out.println("Process not found or not in Ready Queue.");
            return false;
        }

        readyQueue.remove(p);
        p.state = "Suspended";
        suspendedQueue.add(p);
        System.out.println("Process suspended and moved to Suspended Queue.");
        return true;
    }

    static boolean resumeProcess(int id) {
        System.out.print("Process ID to resume: " + id);

        Process p = processMap.get(id);
        if (p == null || !suspendedQueue.contains(p)) {
            System.out.println("Process not found or not in Suspended Queue.");
            return false;
        }

        suspendedQueue.remove(p);
        p.state = "Ready";
        readyQueue.add(p);
        System.out.println("Process resumed and moved to Ready Queue.");
        return true;
    }

    static boolean blockProcess(int id) {
        System.out.print("Process ID to block: " + id);

        Process p = processMap.get(id);
        if (p == null || !readyQueue.contains(p)) {
            System.out.println("Process not found or not in Ready Queue.");
            return false;
        }

        readyQueue.remove(p);
        p.state = "Blocked";
        blockedQueue.add(p);
        System.out.println("Process blocked and moved to Blocked Queue.");
        return true;
    }

    static boolean wakeupProcess(int id) {
        System.out.print("Process ID to wake up: " + id);

        Process p = processMap.get(id);
        if (p == null || !blockedQueue.contains(p)) {
            System.out.println("Process not found or not in Blocked Queue.");
            return false;
        }

        blockedQueue.remove(p);
        p.state = "Ready";
        readyQueue.add(p);
        System.out.println("Process woken up and moved to Ready Queue.");
        return true;
    }

    static boolean changePriority(int id, int priority) {
        System.out.print("Process ID to change priority: " + id);
        System.out.print("New Priority: " + priority);

        Process p = processMap.get(id);
        if (p == null) {
            System.out.println("Process not found.");
            return false;
        }

        p.priority = priority;
        System.out.println("Priority of Process " + id + " updated to " + priority + ".");
        return true;
    }

    static void displayReady(JFrame frame) {
        QueueDisplayDialog dialog = new QueueDisplayDialog(frame, readyQueue);
        dialog.setVisible(true);
    }

    static void displayRunning(JFrame frame) {
        QueueDisplayDialog dialog = new QueueDisplayDialog(frame, runningQueue);
        dialog.setVisible(true);
    }

    static void displaySuspended(JFrame frame) {
        QueueDisplayDialog dialog = new QueueDisplayDialog(frame, suspendedQueue);
        dialog.setVisible(true);
    }

    static void displayBlocked(JFrame frame) {
        QueueDisplayDialog dialog = new QueueDisplayDialog(frame, blockedQueue);
        dialog.setVisible(true);
    }

    static void displayTerminated() {
        QueueDisplayDialog dialog = new QueueDisplayDialog(null, terminateQueue);
        dialog.setVisible(true);
    }

    static boolean dispatchProcess() {
        if (readyQueue.isEmpty()) {
            System.out.println("No processes in Ready Queue to dispatch.");
            return false;
        }

        Process p = readyQueue.remove(0);
        p.state = "Running";

        runningQueue.add(p);
        System.out.println("Process " + p.id + " dispatched and moved to Running Queue.");
        return true;
    }


    static void executeRunningQueue(JFrame parentFrame, ProcessManager.Configuration config) {
        if (!config.preemptiveScheduling) {
            switch (config.schedulingAlgorithm) {
                case "SJF":
                    executeSJF();
                    break;
                case "LJF":
                    executeLJF();
                    break;
                case "Priority":
                    executePriority();
            }
        } else {
            switch (config.schedulingAlgorithm) {
                case "SJF":
                    executeSJFPreemptive(config.timeQuantum);
                    break;
                case "LJF":
                    executeLJFPreemptive(config.timeQuantum);
                    break;
                case "Round Robin":
                    executeRR(config.timeQuantum);
                    break;
            }
        }
    }

    static void executeSJF() {
        runningQueue.sort(Comparator.comparingInt(p -> p.bt));
        executeProcesses("Shortest Job First (SJF)");
    }

    static void executeLJF() {
        runningQueue.sort((p1, p2) -> p2.bt - p1.bt);
        executeProcesses("Longest Job First (LJF)");
    }

    static void executeProcesses(String algorithm) {
        System.out.println("\nExecuting " + algorithm + " on Running Queue:");
        int currentTime = 0;

        System.out.println("ID\tArrival Time\tBurst Time\tCompletion Time\tTurnaround Time\tWaiting Time\tState");

        while (!runningQueue.isEmpty()) {
            Process p = null;
            for (Process proc : runningQueue) {
                if (proc.at <= currentTime) {
                    p = proc;
                    break;
                }
            }

            if (p == null) {
                currentTime++;
                continue;
            }

            runningQueue.remove(p);
            currentTime += p.bt;
            p.ct = currentTime;
            p.tat = p.ct - p.at;
            p.wt = p.tat - p.bt;
            p.state = "Completed";

            terminateQueue.add(p);

            System.out.println(p.id + "\t" + p.at + "\t\t" + p.bt + "\t\t" + p.ct + "\t\t" + p.tat + "\t\t" + p.wt + "\t\t" + p.state);
        }
        displayTerminated();
    }

    static void executeRR(int timeQuantum) {
        int currentTime = 0;

        System.out.println("\nExecuting Round Robin (RR) on Running Queue:");

        if (runningQueue == null || runningQueue.isEmpty()) {
            System.out.println("No processes in the running queue.");
            return;
        }

        System.out.println("ID\tArrival Time\tBurst Time\tCompletion Time\tTurnaround Time\tWaiting Time\tState");

        Queue<Process> queue = new LinkedList<>(runningQueue);
        runningQueue.clear();

        while (!queue.isEmpty()) {
            Process p = queue.poll();

            int executedTime = Math.min(p.bt, timeQuantum);
            currentTime += executedTime;
            p.bt -= executedTime;

            if (p.bt == 0) {
                p.ct = currentTime;
                p.tat = p.ct - p.at;
                p.wt = p.tat - p.remainingBt;
                p.state = "Completed";
                System.out.println(p.id + "\t" + p.at + "\t\t" + p.remainingBt + "\t\t" + p.ct + "\t\t" + p.tat + "\t\t" + p.wt + "\t\t" + p.state);
                terminateQueue.add(p);
            } else {
                queue.add(p);
                System.out.println(p.id + "\t" + p.at + "\t\t" + executedTime + "\t\t" + currentTime + "\t\t" + "-\t\t" + "-\t\t" + "Running");
            }
        }
        displayTerminated();
    }

    static void executePriority() {
        runningQueue.sort(Comparator.comparingInt(p -> p.priority));
        executeProcesses("Priority Scheduling");
    }

    static void executeSJFPreemptive(int timeQuantum) {
        int currentTime = 0;

        System.out.println("\nExecuting Shortest Job First (SJF) Preemptive with Time Quantum (" + timeQuantum + ") on Running Queue:");

        if (runningQueue == null || runningQueue.isEmpty()) {
            System.out.println("No processes in the running queue.");
            return;
        }

        System.out.println("ID\tArrival Time\tOriginal Burst Time\tCompletion Time\tTurnaround Time\tWaiting Time\tState");

        ArrayList<Process> processList = new ArrayList<>(runningQueue);
        runningQueue.clear();

        processList.sort(Comparator.comparingInt(p -> p.at));

        while (!processList.isEmpty()) {
            Process currentProcess = null;
            for (Process p : processList) {
                if (p.at <= currentTime) {
                    if (currentProcess == null || p.bt < currentProcess.bt) {
                        currentProcess = p;
                    }
                }
            }

            if (currentProcess == null) {
                currentTime++;
                continue;
            }

            int executedTime = Math.min(currentProcess.bt, timeQuantum);
            currentProcess.bt -= executedTime;
            currentTime += executedTime;

            if (currentProcess.bt == 0) {
                currentProcess.ct = currentTime;
                currentProcess.tat = currentProcess.ct - currentProcess.at;
                currentProcess.wt = currentProcess.tat - currentProcess.remainingBt;
                currentProcess.state = "Completed";

                System.out.println(
                        currentProcess.id + "\t" + currentProcess.at + "\t\t" + currentProcess.remainingBt + "\t\t" + currentProcess.ct + "\t\t"
                                + currentProcess.tat + "\t\t" + currentProcess.wt + "\t\t" + currentProcess.state
                );

                processList.remove(currentProcess);
                terminateQueue.add(currentProcess);
            } else {
                currentProcess.state = "Running";
                System.out.println(
                        currentProcess.id + "\t" + currentProcess.at + "\t\t" + currentProcess.remainingBt + "\t\t-\t\t"
                                + "-\t\t" + "-\t\t" + currentProcess.state
                );
            }
        }

        displayTerminated();
    }

    static void executeLJFPreemptive(int timeQuantum) {
        int currentTime = 0;

        System.out.println("\nExecuting Longest Job First (LJF) Preemptive with Time Quantum (" + timeQuantum + ") on Running Queue:");

        if (runningQueue == null || runningQueue.isEmpty()) {
            System.out.println("No processes in the running queue.");
            return;
        }

        System.out.println("ID\tArrival Time\tOriginal Burst Time\tCompletion Time\tTurnaround Time\tWaiting Time\tState");

        ArrayList<Process> processList = new ArrayList<>(runningQueue);
        runningQueue.clear();

        processList.sort(Comparator.comparingInt(p -> p.at));

        while (!processList.isEmpty()) {
            Process currentProcess = null;
            for (Process p : processList) {
                if (p.at <= currentTime) {
                    if (currentProcess == null || p.bt > currentProcess.bt) {
                        currentProcess = p;
                    }
                }
            }

            if (currentProcess == null) {
                currentTime++;
                continue;
            }

            int executedTime = Math.min(currentProcess.bt, timeQuantum);
            currentProcess.bt -= executedTime;
            currentTime += executedTime;

            if (currentProcess.bt == 0) {
                currentProcess.ct = currentTime;
                currentProcess.tat = currentProcess.ct - currentProcess.at;
                currentProcess.wt = currentProcess.tat - currentProcess.remainingBt;
                currentProcess.state = "Completed";

                System.out.println(
                        currentProcess.id + "\t" + currentProcess.at + "\t\t" + currentProcess.remainingBt + "\t\t" + currentProcess.ct + "\t\t"
                                + currentProcess.tat + "\t\t" + currentProcess.wt + "\t\t" + currentProcess.state
                );

                processList.remove(currentProcess);
                terminateQueue.add(currentProcess);
            } else {
                currentProcess.state = "Running";
                System.out.println(
                        currentProcess.id + "\t" + currentProcess.at + "\t\t" + currentProcess.remainingBt + "\t\t-\t\t"
                                + "-\t\t" + "-\t\t" + currentProcess.state
                );
            }
        }

        displayTerminated();
    }

    public static void storeProcessQueue(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(readyQueue);
            System.out.println("Process queue stored successfully in " + filename);
        } catch (IOException e) {
            System.err.println("Error storing process queue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static void removeFromAllQueues(Process p) {
        readyQueue.remove(p);
        runningQueue.remove(p);
        blockedQueue.remove(p);
        suspendedQueue.remove(p);
    }

    public static void removeFromQueue(ArrayList<Process> queue, int index, String editQueue) {
        switch (editQueue) {
            case "Blocked":
                blockProcess(queue.get(index).id);
                break;
            case "Suspended":
                suspendProcess(queue.get(index).id);
                break;
            case "Resume":
                resumeProcess(queue.get(index).id);
                break;
            case "Wake":
                wakeupProcess(queue.get(index).id);
                break;
            case "Destroy":
                destroyProcess(queue.get(index).id);
                break;
        }
    }
}