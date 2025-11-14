import javax.swing.*;
import java.io.*;
import java.net.*;

public class ProcessServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server is running and waiting for client...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected!");
                OSSimulation.start();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (DataInputStream dis = new DataInputStream(socket.getInputStream());
                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

                while (true) {
                    int processId = ProcessManager.getNextPid();
                    dos.writeInt(processId);

                    int arrivalTime = dis.readInt();
                    int burstTime = dis.readInt();
                    int priority = dis.readInt();

                    if (ProcessManager.createProcess(processId, arrivalTime, burstTime, priority)) {
                        String processDetails = "<html>Process ID: " + processId +
                                "<br>Arrival Time: " + arrivalTime +
                                "<br>Burst Time: " + burstTime +
                                "<br>Priority: " + priority + "</html>";
                        JOptionPane.showMessageDialog(null, processDetails + " Process Created Successfully!",
                                "Process Creation", JOptionPane.INFORMATION_MESSAGE);

                        ProcessManager.storeProcessQueue("Processes.txt");
                        dos.writeUTF("Process created successfully!");
                    } else {
                        dos.writeUTF("Process creation failed! Process with this ID already exists.");
                    }
                }
            } catch (IOException e) {
                System.out.println("Client disconnected!");
            }
        }
    }
}
