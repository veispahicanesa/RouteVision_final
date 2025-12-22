package unze.ptf.routevision_final;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {

    // Moramo deklarisati varijablu da bi je statička metoda mogla vratiti
    private static Stage mainStage;

    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage; // Inicijalizacija stage-a

        // Putanja koju si navela: /unze/ptf/view/
        FXMLLoader loader = new FXMLLoader(
                HelloApplication.class.getResource("/unze/ptf/view/LoginView.fxml")
        );

        // Ispravljeno: koristimo 'loader' (ne fxmlLoader)
        // Uklonjene su fiksne dimenzije (320x240) da bi se prozor prilagodio dizajnu iz FXML-a
        Scene scene = new Scene(loader.load());

        stage.setTitle("RouteVision - Prijava");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}