/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

import database.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import login.Login;
import main.MainController;
import main.PropsManager;
import registration.Registration;

import java.util.Locale;

public class Main extends Application {

    public static void main(String[] args) {
        Locale.setDefault(Locale.GERMAN);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        PropsManager.init();
        // DatabaseManager.InsertUser(new User("admin", "Passw0rd"));

        Parent root = FXMLLoader.load(MainController.class.getResource("/main/main.fxml"));

        if(DatabaseManager.databaseExists()){
            MainController.setContent(MainController.loadFXML(Login.class.getResource("/login/login.fxml")));
        }
        else {
            MainController.setContent(MainController.loadFXML(Registration.class.getResource("/registration/registration.fxml")));
        }

        primaryStage.setTitle("Wahlprogramm");

        double width = 800;
        double height = 600;
        try {
            Rectangle2D bounds = Screen.getScreens().get(0).getBounds();
            width = bounds.getWidth() / 2.5;
            height = bounds.getHeight() / 1.35;
        }catch (Exception ignored){ }

        Scene scene = new Scene(root, width, height);

        primaryStage.setScene(scene);
        new JMetro(root.getScene(), Style.valueOf(PropsManager.Props.getProperty("app.style").toUpperCase()));
        primaryStage.show();
    }
}
