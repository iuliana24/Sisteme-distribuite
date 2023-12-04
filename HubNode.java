import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HubNode {
    private List<Socket> connectedNodes;

    public HubNode() {
        connectedNodes = new ArrayList<>();
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("HubNode este activ. Astept conexiuni...");

            while (true) {
                Socket socket = serverSocket.accept();
                connectedNodes.add(socket);
                System.out.println("Conexiune stabilita cu un nod periferic.");

                new Thread(() -> handleNodeCommunication(socket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleNodeCommunication(Socket socket) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner in = new Scanner(socket.getInputStream());

            out.println("Salut, nod periferic!");

            String message = in.nextLine();
            System.out.println("Nod periferic spune: " + message);

            // Verificăm dacă nodul conectat este indisponibil și redirecționăm către următorul nod disponibil
            if (isNodeUnavailable(socket)) {
                out.println("Redirecționează către alt nod disponibil!");
                // Implementează logica pentru a redirecționa comunicarea către următorul nod disponibil
            } else {
                out.println("Mesaj de la HubNode!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Verifică indisponibilitatea nodului
    private boolean isNodeUnavailable(Socket socket) {
        // Implementează logica de verificare a indisponibilității nodului
        // Folosește informațiile din socket (IP sau identificator) pentru a determina disponibilitatea nodului
        // Returnează true dacă nodul este indisponibil, altfel returnează false
        return false; // sau true, în funcție de condiții
    }

    public static void main(String[] args) {
        HubNode hub = new HubNode();
        hub.start();
    }
}
