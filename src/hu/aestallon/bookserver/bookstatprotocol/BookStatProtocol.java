package hu.aestallon.bookserver.bookstatprotocol;

import hu.aestallon.bookserver.book.Book;
import hu.aestallon.bookserver.bookstatprotocol.message.Message;
import hu.aestallon.bookserver.bookstatprotocol.state.InitialState;
import hu.aestallon.bookserver.bookstatprotocol.state.State;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class BookStatProtocol implements Runnable {

    private final Socket clientSocket;
    private State state;
    private Book book;

    public BookStatProtocol(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.state = new InitialState(this);
    }

    @Override
    public void run() {
        try (var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             var out = new PrintStream(clientSocket.getOutputStream(), true)) {
            while (true) {
                Message clientMessage = Message.parse(in.readLine());
                Message response = state.generateResponse(clientMessage);
                out.println(response.convertToString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
