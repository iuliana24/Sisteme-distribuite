import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class PeripheralNode3 {
    private Socket socket;
    private PrintWriter out;
    private Scanner in;
    private Scanner userInputScanner;

    public PeripheralNode3() {
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
                    sendSearchRequest();
                    break;
                case 2:
                    sendAddRequest();
                    break;
                case 3:
                    sendUpdateRequest();
                    break;
                case 4:
                    sendDeleteRequest();
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

    private void sendRequest(String action, String term, String explanation) {
        // Trimite cererea la HubNode
        out.println(action + ":" + term + ":" + explanation);
    }

    private void sendSearchRequest() {
        System.out.print("Introduceți termenul pentru Cautare: ");
        String term = userInputScanner.nextLine();

        // Trimite cererea de căutare la HubNode
        out.println("Cautare:" + term);
    }

    private void sendAddRequest() {
        System.out.print("Introduceți termenul pentru Adaugare: ");
        String term = userInputScanner.nextLine();

        System.out.print("Introduceți explicația pentru termenul '" + term + "': ");
        String explanation = userInputScanner.nextLine().trim();

        // Trimite cererea de adăugare la HubNode
        sendRequest("Adaugare", term, explanation);
    }

    private void sendUpdateRequest() {
        System.out.print("Introduceți termenul pe care doriți să-l modificați: ");
        String term = userInputScanner.nextLine();

        System.out.print("Introduceți noua explicație pentru termenul '" + term + "': ");
        String newExplanation = userInputScanner.nextLine().trim();

        // Trimite cererea de actualizare la HubNode
        out.println("Modificare:" + term + ":" + newExplanation);
    }

    private void sendDeleteRequest() {
        System.out.print("Introduceți termenul pe care doriți să-l ștergeți: ");
        String term = userInputScanner.nextLine();

        // Verificăm dacă utilizatorul dorește să șteargă termenul
        System.out.print("Sunteți sigur că doriți să ștergeți termenul '" + term + "'? (Da/Nu): ");
        String confirmation = userInputScanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("da")) {
            // Trimite cererea de ștergere la HubNode
            out.println("Stergere:" + term);
        } else {
            System.out.println("Ștergerea termenului '" + term + "' a fost anulată.");
        }
    }

    private void sendDisconnectRequest() {
        // Trimite cererea de deconectare la HubNode
        out.println("Deconectare");
    }

    private void disconnect() {
        System.out.println("Deconectare de la HubNode.");
        sendDisconnectRequest();
        try {
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new PeripheralNode3();
    }
}
