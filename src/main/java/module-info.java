module com.gymmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    // Deschide pachetele pentru JavaFX
    opens com.gymmanagement.app to javafx.fxml;
    opens com.gymmanagement.controllers to javafx.fxml;
    opens com.gymmanagement.models to javafx.base;

    // Exportează pachetele utilizabile în alte module (dacă este cazul)
    exports com.gymmanagement.app;
    exports com.gymmanagement.controllers;
}

