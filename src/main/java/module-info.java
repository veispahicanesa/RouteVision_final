

module unze.ptf.routevision_final {

        requires javafx.controls;
        requires javafx.fxml;
        requires java.sql;
        requires jbcrypt;
        requires org.controlsfx.controls;
        requires com.dlsc.formsfx;
        requires net.synedra.validatorfx;
        requires org.kordamp.ikonli.javafx;
        requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires openhtmltopdf.core;
    requires openhtmltopdf.pdfbox;

    // 1. Dozvoljava pristup glavnoj aplikaciji (HelloApplication)
    opens unze.ptf.routevision_final to javafx.fxml;

    // 2. OVO JE KLJUČNO: Dozvoljava pristup tvom LoginControlleru
    opens unze.ptf.routevision_final.controller to javafx.fxml;

    // 3. Dozvoljava pristup FXML fajlovima u novom 'view' paketu
    opens unze.ptf.view to javafx.fxml;

    exports unze.ptf.routevision_final;


        }


