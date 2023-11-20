import java.util.Set;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) {
        // Creare utilizatori
        User user1 = new User("user1");
        User user2 = new User("user2");

        // Creare set de utilizatori
        Set<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);

        // Creare sistem de dicționar
        DictionarySystem dictionarySystem = new DictionarySystem(users);

        // Exemplu de utilizare
        dictionarySystem.executeUserActions();

        // Afisare log-uri
        dictionarySystem.printLogs();
    }
}
