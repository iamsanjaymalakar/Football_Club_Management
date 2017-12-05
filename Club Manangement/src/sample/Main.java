package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.sql.*;


public class Main extends Application {

    static Stage stage;

    static DropShadow borderGlow = new DropShadow();
    static Alert alert;
    static Connection con;
    static PreparedStatement pst;
    static ResultSet rs;
    static TableView<teams> table;
    public static ObservableList<teams> data;
    public static int tableSize;

    public Main() {
        borderGlow.setOffsetY(0f);
        borderGlow.setOffsetX(0f);
        borderGlow.setColor(Color.CORNFLOWERBLUE);
        borderGlow.setWidth(40);
        borderGlow.setHeight(40);
        data = FXCollections.observableArrayList();
        tableSize=0;

    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setWidth(1550);
        stage.setHeight(830);
        loginScreen();
    }

    public static void loginScreen() {
        AnchorPane mainPane = new AnchorPane();
        mainPane.setPrefHeight(795);
        mainPane.setPrefWidth(1089);
        mainPane.setStyle("-fx-background-color: #e2fdff;");
        //titlepane
        AnchorPane titlePane = new AnchorPane();
        titlePane.setPrefHeight(136);
        titlePane.setPrefWidth(1089);
        titlePane.setLayoutX(299);
        titlePane.setLayoutY(26);
        titlePane.setStyle("-fx-background-color: #ffffff;");
        AnchorPane.setTopAnchor(titlePane, 0.0);
        AnchorPane.setBottomAnchor(titlePane, 659.0);
        AnchorPane.setLeftAnchor(titlePane, 0.0);
        AnchorPane.setRightAnchor(titlePane, 0.0);
        //title text
        Text title = new Text();
        title.setText("Football\nClub\n");
        title.setLayoutX(200);
        title.setLayoutY(42);
        Font f = new Font("System Bold", 26);
        title.setFont(f);
        //image
        Image img = new Image("logo.png");
        ImageView imgv = new ImageView(img);
        imgv.setFitHeight(90);
        imgv.setFitWidth(90);
        imgv.setLayoutX(50);
        imgv.setLayoutY(20);
        titlePane.getChildren().addAll(title, imgv);
        //login form
        Text log = new Text("Login to DB");
        log.setLayoutX(257.0);
        log.setLayoutY(256.0);
        f = new Font("System Bold", 48);
        log.setFont(f);
        TextField user = new TextField();
        user.setLayoutX(257);
        user.setLayoutY(338);
        user.setPrefHeight(35);
        user.setPrefWidth(300);
        user.setEffect(borderGlow);
        user.setPromptText("Username");
       // user.setText("clubmanagement");
        Text t1 = new Text("Username :");
        t1.setLayoutX(257);
        t1.setLayoutY(321);
        f = new Font("System Bold", 22);
        t1.setFont(f);
        Text t2 = new Text("Password :");
        t2.setLayoutX(257);
        t2.setLayoutY(408);
        f = new Font("System Bold", 22);
        t2.setFont(f);
        PasswordField pass = new PasswordField();
        pass.setLayoutX(257);
        pass.setLayoutY(423);
        pass.setPrefHeight(35);
        pass.setPrefWidth(300);
        pass.setPromptText("Password");
        pass.setEffect(borderGlow);
       // pass.setText("football");
        //login button
        Button loginButton = new Button("Login");
        loginButton.setLayoutX(495);
        loginButton.setLayoutY(495);
        loginButton.setOnMouseClicked((MouseEvent event) -> {
            String userName = user.getText();
            String passWord = pass.getText();
            try {
                con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:globaldb", userName, passWord);
                showData();
            } catch (SQLException e) {
                errorAlert("Invalid Username/Password", "Invalid Username/Password", null);
            }
        });
        mainPane.getChildren().addAll(titlePane, log, user, pass, t1, t2, loginButton);
        mainPane.getStylesheets().add(Main.class.getResource("button.css").toExternalForm());
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Hospital Management System : Login");
        stage.show();
        // stage.getIcons().add(new Image("BinaryContent/icon.png"));
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void showData() {
        AnchorPane mainPane = new AnchorPane();
        mainPane.setPrefHeight(795);
        mainPane.setPrefWidth(1089);
        mainPane.setStyle("-fx-background-color: #e2fdff;");
        //titlepane
        AnchorPane titlePane = new AnchorPane();
        titlePane.setPrefHeight(136);
        titlePane.setPrefWidth(1089);
        titlePane.setLayoutX(299);
        titlePane.setLayoutY(26);
        titlePane.setStyle("-fx-background-color: #ffffff;");
        AnchorPane.setTopAnchor(titlePane, 0.0);
        AnchorPane.setBottomAnchor(titlePane, 850.0);
        AnchorPane.setLeftAnchor(titlePane, 0.0);
        AnchorPane.setRightAnchor(titlePane, 0.0);
        //title text
        Text title = new Text();
        title.setText("Football\nClub\nManagement");
        title.setLayoutX(200);
        title.setLayoutY(42);
        Font f = new Font("System Bold", 26);
        title.setFont(f);
        //image
        Image img = new Image("logo.png");
        ImageView imgv = new ImageView(img);
        imgv.setFitHeight(90);
        imgv.setFitWidth(90);
        imgv.setLayoutX(50);
        imgv.setLayoutY(20);
        titlePane.getChildren().addAll(title, imgv);
        //login form
        Text log = new Text("Teams :");
        log.setLayoutX(35.0);
        log.setLayoutY(180.0);
        f = new Font("System Bold", 35);
        log.setFont(f);
        //table
        createTable();
        updateTable();
        //add to table
        Text logg = new Text("Add new team :");
        logg.setLayoutX(1100);
        logg.setLayoutY(256.0-100+20);
        f = new Font("System Bold", 27);
        logg.setFont(f);
        TextField teamName = new TextField();
        teamName.setLayoutX(1100);
        teamName.setLayoutY(338-100);
        teamName.setPrefHeight(35);
        teamName.setPrefWidth(300);
        teamName.setEffect(borderGlow);
        Text t1 = new Text("Team Name :");
        t1.setLayoutX(1100);
        t1.setLayoutY(321-100);
        f = new Font("System Bold", 18);
        t1.setFont(f);
        Text t2 = new Text("Manager Id :");
        t2.setLayoutX(1100);
        t2.setLayoutY(408-100);
        f = new Font("System Bold", 18);
        t2.setFont(f);
        Text t3 = new Text("Medical Id :");
        t3.setLayoutX(1100);
        t3.setLayoutY(321-100+200-30);
        f = new Font("System Bold", 18);
        t3.setFont(f);
        Text t4 = new Text("Scout Id :");
        t4.setLayoutX(1100);
        t4.setLayoutY(408-100+200-40);
        f = new Font("System Bold", 18);
        t4.setFont(f);
        Text t5 = new Text("Captain :");
        t5.setLayoutX(1100);
        t5.setLayoutY(408-100+200+100-70);
        f = new Font("System Bold", 18);
        t5.setFont(f);
        TextField managerID = new TextField();
        managerID.setLayoutX(1100);
        managerID.setLayoutY(423-100);
        managerID.setPrefHeight(35);
        managerID.setPrefWidth(300);
        managerID.setEffect(borderGlow);
        TextField medicID = new TextField();
        medicID.setLayoutX(1100);
        medicID.setLayoutY(423-100+100-20);
        medicID.setPrefHeight(35);
        medicID.setPrefWidth(300);
        medicID.setEffect(borderGlow);
        TextField scoutID = new TextField();
        scoutID.setLayoutX(1100);
        scoutID.setLayoutY(423-100+100+100-45);
        scoutID.setPrefHeight(35);
        scoutID.setPrefWidth(300);
        scoutID.setEffect(borderGlow);
        TextField captain = new TextField();
        captain.setLayoutX(1100);
        captain.setLayoutY(423-100+100+200-70);
        captain.setPrefHeight(35);
        captain.setPrefWidth(300);
        captain.setEffect(borderGlow);
        // button
        Button loginButton = new Button("Add Data");
        loginButton.setLayoutX(1310);
        loginButton.setLayoutY(495-100+300-80);
        loginButton.setOnMouseClicked((MouseEvent event) -> {
            int temp=tableSize+1;
            String sql = "INSERT INTO TEAMS(TEAM_ID,TEAM_NAME,MANAGER_ID,SCOUT_ID,MEDIC_ID, CAPTAIN) VALUES ("+"'"+temp+"', '"+teamName.getText()+"','"+managerID.getText()+"', '"+medicID.getText()+"', '"+scoutID.getText()+"', '"+captain.getText()+"')";
            try {
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                teamName.clear();
                managerID.clear();
                medicID.clear();
                scoutID.clear();
                captain.clear();
                updateTable();
            } catch (SQLException e) {
                errorAlert("Invalid Data.","Invalid Data",null);
            }
        });
        mainPane.getChildren().addAll(titlePane, log,table,logg,loginButton,teamName,t1,t2,t3,t4,t5,managerID,medicID,scoutID,captain);
        mainPane.getStylesheets().add(Main.class.getResource("button.css").toExternalForm());
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Teams table");
        stage.show();
        // stage.getIcons().add(new Image("BinaryContent/icon.png"));
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void errorAlert(String title, String header, String content) {
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void updateTable()
    {
        String sql = "SELECT * FROM TEAMS ORDER BY TEAM_ID";
        try {
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:globaldb", "clubmanagement", "football");
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            data.clear();
            while (rs.next()) {
                data.add(new teams(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6)));
                tableSize++;
                // System.out.println(rs.getInt("team_id") + " , " + rs.getString(2) + " , " + rs.getString(3) + " , " + rs.getString(4) + " , " + rs.getString(5) + " , " + rs.getString(6));
            }
            table.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTable()
    {
        table = new TableView<teams>();
        table.setLayoutX(33);
        table.setLayoutY(200);
        table.setPrefHeight(512);
        table.setPrefWidth(788);
        table.setEditable(false);
        TableColumn<teams,String> c1 = new TableColumn("Team ID");
        TableColumn<teams,String>  c2 = new TableColumn("Name");
        TableColumn<teams,String>  c3 = new TableColumn("Manager ID");
        TableColumn<teams,String>  c4 = new TableColumn("Scout ID");
        TableColumn<teams,String>  c5 = new TableColumn("Medic ID");
        TableColumn<teams,String>  c6 = new TableColumn("Captain");
        c1.setCellValueFactory(new PropertyValueFactory<>("team_id"));
        c2.setCellValueFactory(new PropertyValueFactory<>("team_name"));
        c3.setCellValueFactory(new PropertyValueFactory<>("manager_id"));
        c4.setCellValueFactory(new PropertyValueFactory<>("scout_id"));
        c5.setCellValueFactory(new PropertyValueFactory<>("medic_id"));
        c6.setCellValueFactory(new PropertyValueFactory<>("captain"));
        c1.setMinWidth(100);
        c2.setMinWidth(100);
        c3.setMinWidth(100);
        c4.setMinWidth(100);
        c5.setMinWidth(100);
        c6.setMinWidth(150);
        table.getColumns().addAll(c1,c2,c3,c4,c5,c6);
    }
}
