module com.example.adm {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.adm to javafx.fxml;
    exports com.example.adm;
    exports com.example.adm.Thread;
    opens com.example.adm.Thread to javafx.fxml;
    exports com.example.adm.DownloadUtils;
    opens com.example.adm.DownloadUtils to javafx.fxml;

}