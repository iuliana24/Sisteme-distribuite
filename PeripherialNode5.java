import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class PeripherialNode5 {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 12345);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner in = new Scanner(socket.getInputStream());

            // Primirea unui mesaj de la HubNode
            String message = in.nextLine();
            System.out.println("HubNode spune: " + message);

            // Trimite un mesaj către HubNode
            out.println("Mesaj de la nod periferic 5!");

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
