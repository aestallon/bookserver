package hu.aestallon.bookserver.server;

import hu.aestallon.bookserver.bookstatprotocol.BookStatProtocol;

import java.io.IOException;
import java.net.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class BookServer {
    private final String address;
    private final int portNumber;

    public BookServer(int portNumber) {
        if (portNumber < 1025 || portNumber > 65_535)
            throw new IllegalArgumentException("Port " + portNumber + " is restricted!");
        this.address = findAddress();
        this.portNumber = portNumber;
    }

    private String findAddress() {
        try (var dSocket = new DatagramSocket()) {
            dSocket.connect(InetAddress.getByName("8.8.8.8"), 10_002);
            return dSocket.getLocalAddress().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getAddress() {
        return address;
    }

    private String getCurrentTimeFormatted() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(portNumber);
        LocalTime now = LocalTime.now();
        LocalTime end = LocalTime.of(23, 59);
        System.out.println(getCurrentTimeFormatted() + " --- Server started!");
        // Server is up until 23:00
        while (now.isBefore(end)) {
            Socket clientSocket = serverSocket.accept();
            System.out.println(getCurrentTimeFormatted() + " --- Client connected!");
            BookStatProtocol bsp = new BookStatProtocol(clientSocket);
            Thread t = new Thread(bsp);
            t.start();
            now = LocalTime.now();
        }
    }
}
