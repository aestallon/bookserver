package hu.aestallon.bookserver.bookstatprotocol.message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Message {
    public static final String DELIMIT_REGEX = "\\|";
    public static final String DELIMIT_SIGN = "|";

    public enum Type {
        // query types:
        BOOK_LIST, GET, LOAD, DROP,
        // response types:
        ERROR, RESULT,
        // unknown type:
        UNKNOWN;

        public static Type parse(String s) {
            try {
                return Type.valueOf(s);
            } catch (IllegalArgumentException e) {
                return UNKNOWN;
            }
        }
    }

    private final LocalDateTime timestamp;
    private final Type type;
    private final String[] contents;

    private Message(Type type, String[] contents) {
        timestamp = LocalDateTime.now();
        this.type = type;
        this.contents = contents;
    }

    private Message(Builder messageBuilder) {
        timestamp = LocalDateTime.now();
        this.type = messageBuilder.type;
        this.contents = messageBuilder.contentList.toArray(String[]::new);
    }

    public static Message parse(String s) {
        if (!s.contains(DELIMIT_SIGN))
            return new Message(Type.parse(s), new String[0]);
        String[] elements = s.split(DELIMIT_REGEX);
        Type type = Type.parse(elements[0]);
        int contentLength = elements.length - 1;
        String[] contents = new String[contentLength];
        System.arraycopy(elements, 1, contents, 0, contentLength);
        return new Message(type, contents);
    }

    public String convertToString() {
        var sb = new StringBuilder(type.toString());
        for (String s : contents) {
            sb.append('|').append(s);
        }
        return sb.toString();
    }

    public static Builder getBuilderOfType(Type type) {
        return new Builder(type);
    }

    public static class Builder {
        private final Type type;
        List<String> contentList;

        private Builder(Type type) {
            this.type = type;
            this.contentList = new ArrayList<>();
        }

        public Builder append(String arg) {
            this.contentList.add(arg);
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Type getType() {
        return type;
    }

    public String[] getContents() {
        return contents;
    }

    public Stream<String> contentStream() {
        return Arrays.stream(contents);
    }

    public int getContentLength() {
        return contents.length;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder("Message received at: ");
        sb.append(timestamp.toString()).append(System.lineSeparator())
                .append(" TYPE: ").append(type.toString())
                .append(" ARGS:");
        for (String s : contents) {
            sb.append(' ').append(s);
        }
        return sb.toString();
    }
}
