module unze.ptf.routevision_final {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens unze.ptf.routevision_final to javafx.fxml;
    exports unze.ptf.routevision_final;
}