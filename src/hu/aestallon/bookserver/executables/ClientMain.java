package hu.aestallon.bookserver.executables;

import hu.aestallon.bookserver.client.TestClient;

import java.io.IOException;

public class ClientMain {

    public static void main(String[] args) {
        // Validating cla:
        if (args.length != 2 || !hostAddressValid(args[0]) || !hostPortNumberValid(args[1])) {
            printHelp();
            return;
        }
        TestClient client = new TestClient(args[0], Integer.parseInt(args[1]));

        try {
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This method does not check if the IPv4 address bytes are
    // correctly in the range of 0...255, but the user being
    // familiar with the format is a precondition.
    private static boolean hostAddressValid(String arg0) {
        return arg0.matches("(\\d{1,3}\\.){3}\\d{1,3}");
    }

    private static boolean hostPortNumberValid(String arg1) {
        try {
            int hostPortNumber = Integer.parseInt(arg1);
            return (hostPortNumber > 1024 && hostPortNumber < 65_535);
        } catch (NumberFormatException e) {
            return false;
        }

    }

    private static void printHelp() {
        System.out.println("Usage: The program needs two arguments:");
        System.out.println("1) An IPv4 address in the format <*.*.*.*>");
        System.out.println("2. A port number between 1025 and 65535 (inclusive).");
        System.out.println("The above arguments resolve the address the program attempts to connect.");
    }

}
