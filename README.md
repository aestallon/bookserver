### Getting started

No release version currently exists. Load the project in an IDE or compile the project with `javac` then launch the
executable classes in the following order:

1) Launch `ServerMain` with a single CLA describing the port the server will use to listen for clients - should be an
   integer between 1025 and 65_535 (inclusive).

   *When the server starts up, it should print its IPv4 address to standard output.*
2) Launch `ClientMain` with two CLAs: The first argument should be the servers IPv4 address, the second should be the
   same port number you provided for the server.

You should get a confirmation of the successful connection on the server side.

### Usage

Communicating with the server is done through the Book Statistics Protocol(BSP).

BSP recognises the following messages to the server:

- `BOOK_LIST` The server scans the hard drive for available books, and messages the titles back to the client.
- `LOAD <bookTitle>` The protocol attempts loading the specified book from the available books.
    - If the book does **not** exist, a message will be sent back to inform the client.
    - If the book exists, a success message will be sent with the format `LOAD COMPLETE`.
- `DROP` The protocol attempts dropping the currently loaded book.
    - If there is no book loaded, nothing interesting happens.
    - If there's been a book loaded, it is dropped from memory and the client is sent a message.
- `GET` The protocol attempts to fetch some information about the currently loaded book.
    - If no book is loaded yet, a message of type `ERROR` will inform the client that loading a book is mandatory.
    - Else a message of type`RESULT` will list the results.
    - Accepted arguments:
        1) `TOPWORD`          returns a message containing the most frequent word in the book.
        2) `TOPWORD <n>`      returns a message containing the `n` most frequent words in the book.
        3) `APAX`             returns a list of *άπαξ λεγόμενα* found in the book. (Words that only appear once.)
        4) `WORDCOUNT`        returns the number of words in the book.
        5) `WORDCOUNT_UNIQUE` returns the number of unique words in the book.
        6) `LONGESTWORD`      returns the longest word in the book.
        7) `LONGESTWORD <n>`  returns the `n` longest words in the book.

Responses are decoded by the client and printed to standard output.