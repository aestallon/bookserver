package hu.aestallon.bookserver.bookstatprotocol.state;

import hu.aestallon.bookserver.book.Book;
import hu.aestallon.bookserver.bookstatprotocol.BookStatProtocol;
import hu.aestallon.bookserver.bookstatprotocol.message.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class InitialState extends State {

    public InitialState(BookStatProtocol protocol) {
        super(protocol);
    }

    @Override
    public Message load(String[] args) {
        if (args.length != 1) return defaultErrorMessage();
        try {
            String filePath = "resources" + File.separator + "books"
                    + File.separator + args[0] + ".txt";
            Book book = new Book(filePath);
            protocol.setBook(book);
            protocol.setState(new SingleBookState(protocol));
            return Message.getBuilderOfType(Message.Type.RESULT).append("LOAD COMPLETE!").build();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return Message.getBuilderOfType(Message.Type.ERROR)
                    .append("Requested book cannot be found!")
                    .append("To list the available books at any time, type `BOOK_LIST`")
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            return Message.getBuilderOfType(Message.Type.ERROR)
                    .append("Unknown error occurred while loading the requested book.")
                    .append("Please try later!")
                    .build();

        }
    }

    @Override
    public Message drop(String[] args) {
        String line = "There are no book(s) loaded currently!";
        return Message.getBuilderOfType(Message.Type.ERROR).append(line).build();
    }

    @Override
    public Message get(String[] args) {
        String line = "There is no book loaded at the moment. Use the LOAD command to load a book!";
        return Message.getBuilderOfType(Message.Type.ERROR).append(line).build();
    }
}
