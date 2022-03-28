package schule.projects.e2e_chat;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    private final static String SERVER_IP = "localhost";
    private final static int SERVER_PORT = 5555;

    public static void main(String[] args) throws IOException {
        String auswahl;
        if(args.length > 0) {
            auswahl = args[0];
        }
        else {
            Scanner s = new Scanner(System.in);
            System.out.println("Was soll gestartet werden?");
            System.out.println("1. Server");
            System.out.println("2. Client");
            auswahl = s.nextLine();
        }

        switch (auswahl) {
            case "1" -> {
                System.out.println("Starte Server mit IP " + SERVER_IP + " und Port " + SERVER_PORT);
                Server s = new Server();
                s.listen(SERVER_PORT);
            }
            case "2" -> {
                System.out.println("Starte Client mit IP " + SERVER_IP + " und Port " + SERVER_PORT);
                Client client = new Client();
                client.connect(SERVER_IP, SERVER_PORT);
                client.send("Hallo");
            }
            default -> System.out.println("Falsche Eingabe");

        }
    }
}
