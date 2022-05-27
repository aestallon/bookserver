package hu.aestallon.bookserver.bookstatprotocol.state;

import hu.aestallon.bookserver.bookstatprotocol.BookStatProtocol;
import hu.aestallon.bookserver.bookstatprotocol.message.Message;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public abstract class State {

    private static final String UNKNOWN_CMD_MSG = "The following command cannot be interpreted. " +
            "Please consult the protocol documentation for the list of available commands!";
    // context
    protected BookStatProtocol protocol;

    public State(BookStatProtocol protocol) {
        this.protocol = protocol;
    }

    public final Message bookList() {
        try (Stream<Path> paths = Files.walk(Paths.get("resources", "books"))) {
            List<String> titles = paths
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .map(fileName -> fileName.split("\\."))
                    .map(array -> array[0])
                    .toList();
            Message.Builder messageBuilder = Message.getBuilderOfType(Message.Type.RESULT)
                    .append("The currently available books are:");
            titles.forEach(messageBuilder::append);
            messageBuilder.append("Use the LOAD command followed by any title to load a book for analysis.");
            return messageBuilder.build();
        } catch (IOException e) {
            e.printStackTrace();
            return defaultErrorMessage();
        }
    }

    public final Message generateResponse(Message clientMessage) {
        return switch (clientMessage.getType()) {
            case BOOK_LIST -> bookList();
            case LOAD -> {
                if (clientMessage.getContentLength() < 1) yield defaultErrorMessage();
                else yield load(clientMessage.getContents());
            }
            case DROP -> drop(clientMessage.getContents());
            case GET  -> {
                if (clientMessage.getContentLength() < 1) yield defaultErrorMessage();
                else yield get(clientMessage.getContents());
            }
            default -> defaultErrorMessage();
        };
    }

    protected Message defaultErrorMessage() {
        return Message.getBuilderOfType(Message.Type.ERROR)
                .append(UNKNOWN_CMD_MSG)
                .build();
    }

    protected abstract Message load(String[] args);
    protected abstract Message drop(String[] args);
    protected abstract Message get(String[] args);

}
