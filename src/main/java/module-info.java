module gui.javafrontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    // Exporter les packages nécessaires pour Jackson
    opens gui.javafrontend.dto to com.fasterxml.jackson.databind;
    opens gui.javafrontend.service to com.fasterxml.jackson.databind;
    exports gui.javafrontend.enums;
    exports gui.javafrontend;
    exports gui.javafrontend.dto;
    exports gui.javafrontend.service;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires java.sql;

    opens gui.javafrontend to javafx.fxml;

}