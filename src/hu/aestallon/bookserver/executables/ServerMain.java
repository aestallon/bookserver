package hu.aestallon.bookserver.executables;

import hu.aestallon.bookserver.server.BookServer;

import java.io.IOException;

public class ServerMain {

    public static void main(String[] args) {
        if (args.length != 1) {
            printHelp();
            return;
        }
        BookServer server = new BookServer(Integer.parseInt(args[0]));
        System.out.println("Server located at: " + server.getAddress());
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printHelp() {
        System.out.println("Usage: The program needs a single command-line argument,");
        System.out.println("a port number between 1025 and 65_535 (inclusive).");
    }

}
