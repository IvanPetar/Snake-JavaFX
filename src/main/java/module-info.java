module hr.algebra.snakejavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires static lombok;
    requires org.slf4j;
    requires java.rmi;
    requires javafx.graphics;
    requires javafx.base;
    requires java.naming;

    opens hr.algebra.snakejavafx to javafx.fxml;
    exports hr.algebra.snakejavafx;
    exports hr.algebra.snakejavafx.rmi;
}