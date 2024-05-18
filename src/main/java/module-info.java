module ui.a3basic {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens ui.a3basic to javafx.fxml;
    exports ui.a3basic;
}