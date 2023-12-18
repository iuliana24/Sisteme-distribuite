import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class PeripherialNode4 {
    private Socket socket;
    private PrintWriter out;
    private Scanner in;
    private Scanner userInputScanner;

    public PeripherialNode4() {
        userInputScanner = new Scanner(System.in);

        try {
            socket = new Socket("127.0.0.1", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new Scanner(socket.getInputStream());
            System.out.println("Conectat la HubNode");

            new Thread(() -> {
                while (in.hasNextLine()) {
                    String message = in.nextLine();
                    System.out.println("HubNode: " + message);
                }
            }).start();

            startUserInput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startUserInput() {
        while (true) {
            printMenu();
            int option = userInputScanner.nextInt();
            userInputScanner.nextLine(); // Consumăm newline

            switch (option) {
                case 1:
                    sendRequest("Adăugare");
                    break;
                case 2:
                    sendRequest("Ștergere");
                    break;
                case 3:
                    sendRequest("Actualizare");
                    break;
                case 4:
                    sendRequest("Stergere");
                    break;
                case 0:
                    System.out.println("Deconectare de la HubNode.");
                    out.println("exit");
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    return;
                default:
                    System.out.println("Opțiune invalidă.");
            }
        }
    }

    private void printMenu() {
        System.out.println("Selectați acțiunea dorită:");
        System.out.println("1. Cautare termen");
        System.out.println("2. Adaugare termen");
        System.out.println("3. Modificare termen");
        System.out.println("4. Stergere termen");
        System.out.println("0. Deconectare");
        System.out.print("Introduceți numărul acțiunii: ");
    }

    private void sendRequest(String action) {
        System.out.print("Introduceți termenul pentru " + action + ": ");
        String term = userInputScanner.nextLine();

        out.println(action + ":" + term);
    }

    public static void main(String[] args) {
        new PeripherialNode4();
    }
}
