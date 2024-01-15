import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class HubNode {
    private List<Socket> connectedNodes;
    private List<PrintWriter> nodeWriters;

    private Map<String, String> keyValueMap;
    private List<String> changeLogs;
    public HubNode() {
        connectedNodes = new ArrayList<>();
        nodeWriters = new ArrayList<>();
        keyValueMap = new HashMap<>();
        changeLogs = new ArrayList<>();    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("HubNode este activ. Aștept conexiuni...");

            while (true) {
                Socket socket = serverSocket.accept();
                connectedNodes.add(socket);
                nodeWriters.add(new PrintWriter(socket.getOutputStream(), true));
                System.out.println("Conexiune stabilită cu un nod periferic.");

                // Trimite conexiunile la toate nodurile periferice
                transmitConnections();

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

                // Trimite informațiile din dicționar la nodul periferic
                transmitDictionaryInfo(out);

                // Trimite activitățile la nodul periferic
                transmitActivities(out);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void transmitDictionaryInfo(PrintWriter destination) {
        for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {
            destination.println("Dicționar:" + entry.getKey() + ":" + entry.getValue());
        }
    }

    private void transmitActivities(PrintWriter destination) {
        for (String logEntry : changeLogs) {
            destination.println("Activitate:" + logEntry);
        }
    }

    private void addLog(String logMessage) {
        // Adaugă o intrare în sistemul de log-uri
        changeLogs.add(logMessage);
    }

    private void transmitConnections() {
        for (PrintWriter writer : nodeWriters) {
            // Trimite conexiunile la toate nodurile periferice
            writer.println("Conexiuni:" + connectedNodes.size());
        }
    }

    // Verifică indisponibilitatea nodului
    private boolean isNodeUnavailable(Socket socket) {
        // Implementează logica de verificare a indisponibilității nodului
        // Folosește informațiile din socket (IP sau identificator) pentru a determina disponibilitatea nodului
        // Returnează true dacă nodul este indisponibil, altfel returnează false
        return false; // sau true, în funcție de condiții
    }

    public String processRequest(String request) {
        String[] parts = request.split(":");
        String action = parts[0];

        switch (action) {
            case "Cautare":
                return search(parts[1]);
            case "Adaugare":
                return add(parts[1], parts[2]);
            case "Modificare":
                return update(parts[1], parts[2]);
            case "Stergere":
                return delete(parts[1]);
            default:
                return "Comanda invalida";
        }
    }

    private String search(String term) {
        if (keyValueMap.containsKey(term)) {
            return "Termenul: " + term + "\nExplicatie: " + keyValueMap.get(term);
        } else {
            return "Termenul nu exista in dictionar.";
        }
    }

    private String add(String term, String explanation) {
        if (!keyValueMap.containsKey(term)) {
            keyValueMap.put(term, explanation);
            addLog("Termenul '" + term + "' a fost adaugat de catre un nod periferic.");
            return "Termenul a fost adaugat cu succes.";
        } else {
            return "Termenul exista deja in dictionar. Utilizati functia de actualizare pentru a modifica termenul.";
        }
    }

    private String update(String term, String newExplanation) {
        if (keyValueMap.containsKey(term)) {
            keyValueMap.put(term, newExplanation);
            addLog("Termenul '" + term + "' a fost actualizat de catre un nod periferic.");
            return "Termenul a fost actualizat cu succes.";
        } else {
            return "Termenul nu exista in dictionar. Utilizati functia de adaugare pentru a adauga un termen nou.";
        }
    }

    private String delete(String term) {
        if (keyValueMap.containsKey(term)) {
            keyValueMap.remove(term);
            addLog("Termenul '" + term + "' a fost sters de catre un nod periferic.");
            return "Termenul a fost sters cu succes.";
        } else {
            return "Termenul nu exista in dictionar.";
        }
    }
    public static void main(String[] args) {
        HubNode hub = new HubNode();
        hub.start();
    }
}

