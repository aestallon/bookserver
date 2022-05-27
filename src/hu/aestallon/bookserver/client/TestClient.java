package hu.aestallon.bookserver.client;

import hu.aestallon.bookserver.bookstatprotocol.message.Message;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TestClient {
    private final String hostAddress;
    private final int hostPortNumber;

    public TestClient(String hostAddress, int hostPortNumber) {
        this.hostAddress = hostAddress;
        this.hostPortNumber = hostPortNumber;
    }

    public void start() throws IOException {
        try (Socket hostSocket = new Socket(hostAddress, hostPortNumber);
             var in = new BufferedReader(new InputStreamReader(hostSocket.getInputStream()));
             var out = new PrintWriter(new OutputStreamWriter(hostSocket.getOutputStream()), true)) {
            Scanner sc = new Scanner(System.in);
            while (true) {
                String input = sc.nextLine();
                String[] helper = input.split(" ");
                input = String.join("|", helper);
                out.println(input);
                Message response = Message.parse(in.readLine());
                if (response.getType() == Message.Type.RESULT) {
                    response.contentStream().forEach(System.out::println);
                } else response.contentStream().forEach(System.err::println);
            }
        }
    }

}
