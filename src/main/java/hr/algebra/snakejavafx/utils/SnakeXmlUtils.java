package hr.algebra.snakejavafx.utils;


import hr.algebra.snakejavafx.model.Direction;
import hr.algebra.snakejavafx.model.SnakeMoveTag;
import org.w3c.dom.Document;
import org.w3c.dom.*;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SnakeXmlUtils {
    private SnakeXmlUtils(){}

    private static final String DTD = "dtd/gameMoves.dtd";
    private static final String FILE = "xml/snakeMoves.xml";
    private static final ExecutorService XML_EXECUTOR = Executors.newSingleThreadExecutor();

    public static void saveMove(Direction direction, int x, int y) {
        try {
            Document document = Files.exists(Path.of(FILE))
                    ? loadDocument()
                    : createDocument();

            appendMove(document, Direction.valueOf(String.valueOf(direction)), x, y);
            saveDocument(document);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveMoveAsync(Direction direction, int x, int y) {
        XML_EXECUTOR.execute(() -> saveMove(direction, x, y));
    }

    private static Document loadDocument() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        factory.setIgnoringElementContentWhitespace(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(xmlErrorHandler());

        return builder.parse(new File(FILE));
    }

    private static Document createDocument() throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        DOMImplementation dom = builder.getDOMImplementation();

        DocumentType docType = dom.createDocumentType(
                SnakeMoveTag.SNAKE_MOVES.getTagName(),
                null,
                DTD
        );

        return dom.createDocument(
                null,
                SnakeMoveTag.SNAKE_MOVES.getTagName(),
                docType
        );
    }

    private static void appendMove(Document document, Direction direction, int x, int y) {
        Element move = document.createElement(
                SnakeMoveTag.MOVE.getTagName()
        );
        document.getDocumentElement().appendChild(move);

        move.appendChild(createElement(
                document,
                SnakeMoveTag.DIRECTION.getTagName(),
                direction.name()
        ));

        move.appendChild(createElement(
                document,
                SnakeMoveTag.POSITION_X.getTagName(),
                String.valueOf(x)
        ));

        move.appendChild(createElement(
                document,
                SnakeMoveTag.POSITION_Y.getTagName(),
                String.valueOf(y)
        ));
    }

    private static Node createElement(Document document, String name, String value) {
        Element element = document.createElement(name);
        element.appendChild(document.createTextNode(value));
        return element;
    }

    private static void saveDocument(Document document) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(
                "{https://xml.apache.org/xslt}indent-amount", "2"
        );
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, DTD);

        transformer.transform(
                new DOMSource(document),
                new StreamResult(new File(FILE))
        );
    }

    private static ErrorHandler xmlErrorHandler() {
        return new ErrorHandler() {
            @Override
            public void warning(SAXParseException e) {
                System.err.println("XML Warning: " + e.getMessage());
            }

            @Override
            public void error(SAXParseException e) throws SAXException {
                throw e;
            }

            @Override
            public void fatalError(SAXParseException e) throws SAXException {
                throw e;
            }
        };
    }
}
