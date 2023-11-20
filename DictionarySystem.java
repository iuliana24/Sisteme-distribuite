import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

class DictionarySystem {
    private final Map<String, String> keyValueMap;
    private final Map<String, Integer> updateConfirmations;
    private final Set<String> confirmingUsers;
    private final Set<User> users;
    private final Map<String, String> termToKeyMap;
    private final List<String> changeLogs;

    public DictionarySystem(Set<User> users) {
        this.keyValueMap = new HashMap<>();
        this.updateConfirmations = new HashMap<>();
        this.confirmingUsers = new HashSet<>();
        this.users = users;
        this.termToKeyMap = new HashMap<>();
        this.changeLogs = new ArrayList<>();
    }

    public void executeUserActions() {
        Scanner scanner = new Scanner(System.in);
        boolean exitRequested = false;

        while (!exitRequested) {
            printMenu();

            int actionNumber = scanner.nextInt();
            scanner.nextLine();

            switch (actionNumber) {
                case 1 -> searchTermFromInput();
                case 2 -> addTerm();
                case 3 -> updateTerm();
                case 4 -> deleteTerm();
                case 0 -> {
                    exitRequested = true;
                    System.out.println("Ieșire din aplicație. La revedere!");
                }
                default -> System.out.println("Opțiune invalidă. Vă rugăm să introduceți o opțiune validă.");
            }
        }
    }

    private void printMenu() {
        System.out.println("Selectați acțiunea dorită:");
        System.out.println("1. Căutați un termen");
        System.out.println("2. Introduceți un termen");
        System.out.println("3. Modificați un termen");
        System.out.println("4. Ștergeți un termen");
        System.out.println("0. Ieșiți din aplicație");
        System.out.println("Introduceți numărul acțiunii: ");
    }



    private void addTermToDictionary(String term, String explanation) {
        String key = Integer.toString(term.hashCode());

        // Adăugăm termenul în dicționar
        keyValueMap.put(key, explanation);
        termToKeyMap.put(term, key);

        // Inițializăm sau resetăm confirmările pentru termenul adăugat
        updateConfirmations.put(term, 0);

        String logEntry = "Termenul '" + term + "' a fost adăugat de către utilizatorul " + getCurrentUser();
        changeLogs.add(logEntry);

        System.out.println("Termenul '" + term + "' a fost adăugat în dicționar.");
    }

    // Funcție pentru adăugarea unui termen în dicționar
    public void addTerm() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Introduceți termenul nou: ");
        String term = scanner.nextLine().trim();

        // Verificăm dacă termenul există deja în dicționar
        if (termToKeyMap.containsKey(term)) {
            System.out.println("Termenul există deja în dicționar. Utilizați funcția de actualizare pentru a modifica termenul.");
        } else {
            System.out.print("Introduceți explicația pentru termenul '" + term + "': ");
            String explanation = scanner.nextLine().trim();

            confirmAction("adăugarea", term, () -> addTermToDictionary(term, explanation));
        }
    }


    private void updateTermInDictionary(String term, String newExplanation) {
        String key = termToKeyMap.get(term);

        // Actualizăm termenul în dicționar
        keyValueMap.put(key, newExplanation);

        // Inițializăm sau resetăm confirmările pentru termenul modificat
        updateConfirmations.put(term, 0);

        String logEntry = "Termenul '" + term + "' a fost actualizat de către utilizatorul " + getCurrentUser();
        changeLogs.add(logEntry);

        System.out.println("Termenul '" + term + "' a fost actualizat în dicționar.");
    }

    // Funcție pentru actualizarea unui termen în dicționar
    public void updateTerm() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Introduceți termenul pe care doriți să-l modificați: ");
        String term = scanner.nextLine().trim();

        if (termToKeyMap.containsKey(term)) {

            System.out.print("Introduceți noua explicație pentru termenul '" + term + "': ");
            String newExplanation = scanner.nextLine().trim();

            // Actualizăm termenul în dicționar
            confirmAction("actualizarea", term, () -> updateTermInDictionary(term, newExplanation));
        } else {
            System.out.println("Termenul nu există în dicționar.");
        }
    }


    private void performDelete(String term, String key) {
        keyValueMap.remove(key);
        termToKeyMap.remove(term);

        // Adăugăm o intrare în sistemul de log-uri
        String logEntry = "Termenul '" + term + "' a fost șters de către utilizatorul " + getCurrentUser();
        changeLogs.add(logEntry);

        System.out.println("Termenul '" + term + "' a fost șters din dicționar.");
    }

    // Funcție pentru ștergerea unui termen din dicționar
    public void deleteTerm() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Introduceți termenul pe care doriți să-l ștergeți: ");
        String term = scanner.nextLine().trim();

        if (termToKeyMap.containsKey(term)) {
            String key = termToKeyMap.get(term);

            // Ștergem termenul din dicționar
            confirmAction("ștergerea", term, () -> performDelete(term, key));
        } else {
            System.out.println("Termenul nu există în dicționar.");
        }
    }

    public void searchTermFromInput() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Introduceți termenul pe care doriți să-l căutați: ");
        String searchTerm = scanner.nextLine().trim();

        String result = searchTerm(searchTerm);
        System.out.println(result);
    }

    // Funcție pentru căutarea termenului
    public String searchTerm(String term) {
        // Verificăm dacă termenul există în maparea termeni -> chei
        if (termToKeyMap.containsKey(term)) {
            String key = termToKeyMap.get(term);
            // Returnăm textul explicativ utilizând numele termenului
            String logEntry = "Utilizatorul " + getCurrentUser() + " a căutat termenul '" + term + "' în dicționar";
            changeLogs.add(logEntry);
            return "Termen: " + term + "\nExplicatie: " + keyValueMap.get(key);
        } else {
            String logEntry = "Utilizatorul " + getCurrentUser() + " a căutat termenul inexistent '" + term + "' în dicționar";
            changeLogs.add(logEntry);
            return "Termenul nu există în dicționar.";
        }
    }

    // Funcția de confirmare pentru actualizare
    public void confirmAction(String actionType, String term, Runnable actionToConfirm) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Confirmați " + actionType + " termenului '" + term + "'? (Da/Nu)");

        String confirmationInput = scanner.nextLine().trim().toLowerCase();
        if (confirmationInput.equals("da")) {
            // Utilizatorul a confirmat
            confirmingUsers.add(getCurrentUser());
            confirmUpdate(term);

            // Executăm acțiunea care necesită confirmare
            actionToConfirm.run();
        } else {
            // Utilizatorul a refuzat confirmarea
            System.out.println(actionType + " a fost anulată. Termenul '" + term + "' nu a fost actualizat.");
        }
    }

    // Funcția de confirmare pentru actualizare
    public void confirmUpdate(String term) {
        int totalUsers = users.size();
        if (updateConfirmations.containsKey(term)) {
            int currentConfirmations = updateConfirmations.get(term);
            updateConfirmations.put(term, currentConfirmations + 1);

            // Verificăm dacă majoritatea utilizatorilor au confirmat
            if (currentConfirmations + 1 >= Math.ceil((double) totalUsers / 2)) {
                String logEntry = "Actualizarea termenului '" + term + "' a fost confirmată de majoritatea utilizatorilor.";
                changeLogs.add(logEntry);

                // Adăugăm utilizatorul curent în lista de utilizatori care au confirmat
                confirmingUsers.add(getCurrentUser());
            }
            else {
                String logEntry = "Actualizarea termenului '" + term + "' a fost refuzată de majoritatea utilizatorilor.";
                changeLogs.add(logEntry);
            }
        }
    }

    public void printLogs() {
        // Afișăm sistemul de log-uri
        System.out.println("\nSistem de log-uri:");

        for (String logEntry : changeLogs) {
            System.out.println(logEntry);
        }
    }

    private String getCurrentUser() {
        // Obținem numele utilizatorului curent
        return "UtilizatorDummy";
    }

}
