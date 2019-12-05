package tabs;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import login.Login;
import main.MainController;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class Tabs implements Initializable {
    @FXML
    private TabPane tabs;

    @FXML
    public Tab logout;

    public void onTabSelectionChanged(Event event) {
        Tab test = (Tab) event.getTarget();
        if(test.getId().equals("logout")){
            MainController.setContent(MainController.loadFXML(Login.class.getResource("/login/login.fxml")));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        VBox.setVgrow(tabs, Priority.ALWAYS);

        this.tabs.getTabs()
                .stream()
                .map(tab->(ScrollPane) tab.getContent())
                .filter(Objects::nonNull)
                .map(scrollPane->(VBox)scrollPane.getContent())
                .forEach(vBox -> vBox.prefWidthProperty()
                                .bind(this.tabs.widthProperty()
                                        .subtract(30)));
    }
}
