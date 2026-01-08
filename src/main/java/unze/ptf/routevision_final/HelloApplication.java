package unze.ptf.routevision_final;

import com.dlsc.formsfx.model.structure.Element;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import unze.ptf.routevision_final.controller.SessionManager;

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


        Scene scene = new Scene(loader.load());

        stage.setTitle("RouteVision - Prijava");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMaximized(true);
        stage.show();
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void main(String[] args) {
        launch(args);
    }



    //anesa
    public static void applyTheme(Scene scene) {
        if (scene == null) return;

        // Dohvatamo root element (to je tvoj BorderPane iz FXML-a)
        var root = scene.getRoot();


        if (SessionManager.isDarkMode()) {
            // Dodajemo klasu 'dark-mode' ako je Dark Mode aktivan
            if (!root.getStyleClass().contains("dark-mode")) {
                root.getStyleClass().add("dark-mode");
            }
        } else {
            // Brišemo je ako nije
            root.getStyleClass().remove("dark-mode");
        }
    }

    public static void Main(String[] args) {
        try {
            launch(args);
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("Pritisnuti enter za izlazak:");
            try {
                System.in.read();
            }catch (Exception Ignored) {
            }
        }
    }
}