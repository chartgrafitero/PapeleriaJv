module com.papeleria.papeleriajv {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;
    requires org.mariadb.jdbc;

    opens com.papeleria.papeleriajv to javafx.fxml;
    opens Controllers to javafx.fxml;
    opens models to javafx.base; 
    exports com.papeleria.papeleriajv;
}
