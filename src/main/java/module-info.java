module hr.algebra.snakejavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires static lombok;


    opens hr.algebra.snakejavafx to javafx.fxml;
    exports hr.algebra.snakejavafx;
}