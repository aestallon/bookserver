package hu.aestallon.bookserver.bookstatprotocol.state;

import hu.aestallon.bookserver.bookstatprotocol.BookStatProtocol;
import hu.aestallon.bookserver.bookstatprotocol.message.Message;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SingleBookState extends State {

    private enum Arg {
        TOPWORD, APAX, WORDCOUNT,
        WORDCOUNT_UNIQUE, LONGESTWORD,
        UNKNOWN;

        private static Arg parse(String s){
            try {
                return Arg.valueOf(s);
            } catch (IllegalArgumentException e) {
                return UNKNOWN;
            }
        }
    }

    public SingleBookState(BookStatProtocol protocol) {
        super(protocol);
    }

    @Override
    public Message load(String[] args) {
        String line = "The following book is already loaded: " + protocol.getBook().getTitle().toUpperCase();
        return Message.getBuilderOfType(Message.Type.ERROR).append(line).build();
    }

    @Override
    public Message drop(String[] args) {
        String line = "DROP COMPLETE (" + protocol.getBook().getTitle().toUpperCase() + ')';
        protocol.setBook(null);
        protocol.setState(new InitialState(protocol));
        return Message.getBuilderOfType(Message.Type.RESULT).append(line).build();
    }

    @Override
    protected Message get(String[] args) {
        try {
            int n = (args.length == 2) ? Integer.parseInt(args[1]) : 1;
            return switch (Arg.parse(args[0])) {
                case TOPWORD -> getTopWordsMessage(n);
                case APAX -> getHapaxLegomenaMessage();
                case WORDCOUNT -> getWordCountMessage();
                case WORDCOUNT_UNIQUE -> getUniqueWordCountMessage();
                case LONGESTWORD -> getLongestWordsMessage(n);
                default -> defaultErrorMessage();
            };
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return defaultErrorMessage();
        }
    }

    private List<String> getTopWords(int n) {
        return protocol.getBook()
                .getMostFrequentWords(n)
                .stream()
                .map(Map.Entry::getKey)
                .toList();
    }

    private Message getTopWordsMessage(int n) {
        List<String> topWords = getTopWords(n);
        Message.Builder messageBuilder = Message.getBuilderOfType(Message.Type.RESULT);
        for (int i = 0; i < topWords.size(); i++) {
            messageBuilder.append("TOPWORD #" + (i + 1) + ": " + topWords.get(i));
        }
        return messageBuilder.build();
    }

    private Message getHapaxLegomenaMessage() {
        Set<String> hapaxLegomena = protocol.getBook().getHapaxLegomena();
        Message.Builder messageBuilder = Message.getBuilderOfType(Message.Type.RESULT);
        hapaxLegomena.stream().sorted().forEach(messageBuilder::append);
        return messageBuilder.build();
    }

    private Message getWordCountMessage() {
        return Message.getBuilderOfType(Message.Type.RESULT)
                .append(String.valueOf(protocol.getBook().getWordCount()))
                .build();
    }

    private Message getUniqueWordCountMessage() {
        return Message.getBuilderOfType(Message.Type.RESULT)
                .append(String.valueOf(protocol.getBook().getDistinctWordCount()))
                .build();
    }

    private Message getLongestWordsMessage(int n) {
        List<String> longestWords = protocol.getBook().getLongestWords(n);
        Message.Builder messageBuilder = Message.getBuilderOfType(Message.Type.RESULT);
        longestWords.forEach(messageBuilder::append);
        return messageBuilder.build();
    }

}
