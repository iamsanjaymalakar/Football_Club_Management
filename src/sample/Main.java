package sample;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.scene.layout.VBox;
import resources.BoardStaff;
import resources.ManagerStaff;
import resources.Team;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


import java.sql.*;
import java.util.concurrent.atomic.AtomicReference;


public class Main extends Application {
    public static Stage stage;
    public static DropShadow borderGlow = new DropShadow();
    public static Alert alert;
    public static Connection con;
    public static PreparedStatement pst;
    public static ResultSet rs;
    public static TableView table;
    public static TableView<ManagerStaff> managerStaffTable;
    public static ObservableList<Team> data;
    public static ObservableList<ManagerStaff> msdata;
    public static int tableSize;
    public static Popup popup;
    public static int managerSceneFlag;

    //
    public static TableView<BoardStaff> boardStaffTable;
    public static ObservableList<BoardStaff> bsdata;
    public static int boardMemberSceneFlag;

    public Main() {
        borderGlow.setOffsetY(0f);
        borderGlow.setOffsetX(0f);
        borderGlow.setColor(Color.CORNFLOWERBLUE);
        borderGlow.setWidth(40);
        borderGlow.setHeight(40);
        data = FXCollections.observableArrayList();
        msdata = FXCollections.observableArrayList();
        tableSize = 0;
        managerSceneFlag = 0;
        popup = new Popup();
        //
        bsdata = FXCollections.observableArrayList();
        boardMemberSceneFlag = 0;
        //
        try {
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:globaldb", "clubmanagement", "football");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        //stage.setWidth(1550);
        //stage.setHeight(830);
        playerPage(3);
    }


    public static void loginScreen() {
        AnchorPane mainPane = new AnchorPane();
        mainPane.setPrefHeight(795);
        mainPane.setPrefWidth(1089);
        mainPane.setStyle("-fx-background-color: #ccfbff;");
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
        //table
        createTable();
        updateTable();
        //login
        TextField userName = new TextField();
        userName.setLayoutX(900);
        userName.setLayoutY(70);
        userName.setPrefHeight(35);
        userName.setPrefWidth(200);
        userName.setEffect(borderGlow);
        userName.setPromptText("Username");
        userName.setText("noah");
        PasswordField passWord = new PasswordField();
        passWord.setLayoutX(1110);
        passWord.setLayoutY(70);
        passWord.setPrefHeight(35);
        passWord.setPrefWidth(200);
        passWord.setEffect(borderGlow);
        passWord.setPromptText("Password");
        passWord.setText("noah");
        Button login = new Button("Login");
        login.setLayoutX(1330);
        login.setLayoutY(68);
        login.setOnMouseClicked((MouseEvent event) -> {
            String user = userName.getText();
            String pass = passWord.getText();
            int count = 0, id = 0;
            String userType;
            String sql = "SELECT COUNT(*), TYPE, ID FROM USERS WHERE USERNAME = '" + user + "' and PASSWORD='" + pass + "' GROUP BY TYPE,ID";
            try {
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    count = rs.getInt(1);
                    userType = rs.getString(2);
                    id = rs.getInt(3);
                    if (count > 0) {
                        if (userType.equals("Manager")) {
                            managerPage(id);
                        } else if (userType.equals("Board Member")) {
                            boardMemberPage(id);
                        } else if (userType.equals("Player")) // new
                        {
                            playerPage(id);
                        }
                    }
                }
                if (count == 0)
                    errorAlert("Invalid Username/Password", "Invalid Username/Password", "Invalid Username/Password");
            } catch (SQLException e) {
                errorAlert("Invalid Username/Password", "Invalid Username/Password", "Invalid Username/Password");
            }
        });
        mainPane.getChildren().addAll(titlePane, userName, passWord, login, table);
        mainPane.getStylesheets().add(Main.class.getResource("button.css").toExternalForm());
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Football Club Management");
        stage.show();
        stage.getIcons().add(new Image("icon.png"));
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });
    }

    //new
    public static void playerPage(int pid) {
        VBox box = new VBox();
        box.setPrefHeight(720);
        box.setPrefWidth(1300);
        box.setStyle("-fx-background-color: white");
        box.getStylesheets().add(Main.class.getResource("table.css").toExternalForm());
        AnchorPane topPane = new AnchorPane();
        topPane.setPrefHeight(125);
        topPane.setPrefWidth(1300);
        topPane.setStyle("-fx-background-color: white");
        Image logoImage = new Image("logo.png");
        ImageView logo = new ImageView(logoImage);
        logo.setFitWidth(125);
        logo.setFitHeight(126);
        logo.setPickOnBounds(true);
        logo.setPreserveRatio(true);
        Text title = new Text("Clubname");
        title.setLayoutX(143);
        title.setLayoutY(83);
        title.setFont(new Font(51));
        Image pimg = new Image("playericon.png");
        ImageView playerIcon = new ImageView(pimg);
        playerIcon.setFitHeight(138);
        playerIcon.setFitWidth(200);
        playerIcon.setLayoutX(957);
        playerIcon.setPickOnBounds(true);
        playerIcon.setPreserveRatio(true);
        //sql to get the player name
        String name = "";
        String sql = "select PLAYER_NAME from PLAYERS where PLAYER_ID=" + pid;
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                name = rs.getString(1);
            }
        } catch (SQLException e) {
            errorAlert("Error", "Error", null);
        }
        Text playerName = new Text(name);
        playerName.setLayoutX(1095);
        playerName.setLayoutY(64);
        playerName.setFont(new Font("System Bold", 21));
        Text logout = new Text("(Logout)");
        logout.setLayoutX(1209);
        logout.setLayoutY(94);
        logout.setFont(new Font("System Bold Italic", 17));
        logout.setOnMouseEntered((MouseEvent event) -> {
            logout.setFont(new Font("System Bold Italic", 20));
            logout.setFill(Color.DARKGRAY);
        });
        logout.setOnMouseExited((MouseEvent event) -> {
            logout.setFont(new Font("System Bold Italic", 17));
            logout.setFill(Color.BLACK);
        });
        logout.setOnMouseClicked((MouseEvent event) -> {
            loginScreen();
        });
        topPane.getChildren().addAll(logo, title, playerIcon, playerName, logout);
        TabPane tabs = new TabPane();
        tabs.setPrefHeight(634);
        tabs.setPrefWidth(1300);
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Tab homeTab = new Tab();
        homeTab.setText("Home");
        AnchorPane homePane = new AnchorPane();
        homePane.setPrefHeight(180);
        homePane.setPrefWidth(200);
        homePane.setStyle("-fx-background-color: #ccfbff");
        //sql
        String nationality = "", position = "", contract = "";
        sql = "select NATIONALITY,POSITION,(trunc(SYSDATE)-trunc(CONTACT_TILL)) Time from PLAYERS where PLAYER_ID=" + pid;
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                nationality = rs.getString(1);
                position = rs.getString(2);
                contract = rs.getString(3);
            }
        } catch (SQLException e) {
            errorAlert("Error", "Error", null);
        }
        Text nameText = new Text(name);
        nameText.setLayoutX(540);
        nameText.setLayoutY(62);
        nameText.setFont(new Font("System Bold", 35));
        Text natpos = new Text(nationality + "    -    " + position);
        natpos.setLayoutX(528);
        natpos.setLayoutY(107);
        natpos.setFont(new Font(26));
        Text contactEx = new Text("Contact expires in : " + contract + " days");
        contactEx.setLayoutX(983);
        contactEx.setLayoutY(79);
        contactEx.setFont(new Font(23));
        //sql
        int cnt = 0, sgoals = 0, sfouls = 0, smp = 0;
        double avgrating = 0;
        sql = "select count(*),SUM(GOALS),SUM(FOULS),SUM(MINUTES_PLAYED),AVG(RATING) from PLAYER_MATCH where PLAYER_ID=" + pid;
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                cnt = rs.getInt(1);
                sgoals = rs.getInt(2);
                sfouls = rs.getInt(3);
                smp = rs.getInt(4);
                avgrating = rs.getDouble(5);
            }
        } catch (SQLException e) {
            errorAlert("Error", "Error", null);
        }
        Text tmp = new Text("Total Matches Played : ");
        tmp.setLayoutX(69);
        tmp.setLayoutY(221);
        tmp.setFont(new Font(25));
        Text tmpc = new Text(String.valueOf(cnt));
        tmpc.setLayoutX(333);
        tmpc.setLayoutY(224);
        tmpc.setFont(new Font("System Bold", 33));
        Text tgs = new Text("Total Goals Scored     : ");
        tgs.setLayoutX(69);
        tgs.setLayoutY(271);
        tgs.setFont(new Font(25));
        Text tgsc = new Text(String.valueOf(sgoals));
        tgsc.setLayoutX(333);
        tgsc.setLayoutY(274);
        tgsc.setFont(new Font("System Bold", 33));
        Text gpg = new Text("Goals Per Game");
        gpg.setLayoutX(619);
        gpg.setLayoutY(265);
        gpg.setFont(new Font(26));
        double temp = (double) sgoals / (double) cnt;
        temp = temp * 10;
        temp = Math.round(temp);
        temp = temp / 10;
        Text gpgc = new Text(String.valueOf(temp));
        gpgc.setLayoutX(553);
        gpgc.setLayoutY(269);
        gpgc.setFont(new Font("System Bold", 41));
        Text gpm = new Text("Minutes Per Goal");
        gpm.setLayoutX(619);
        gpm.setLayoutY(321);
        gpm.setFont(new Font(26));
        temp = (double) smp / (double) sgoals;
        temp = temp * 10;
        temp = Math.round(temp);
        temp = temp / 10;
        Text gpmc = new Text(String.valueOf(temp));
        gpmc.setLayoutX(535);
        gpmc.setLayoutY(326);
        gpmc.setFont(new Font("System Bold", 41));
        Text tf = new Text("Total Fouls                 : ");
        tf.setLayoutX(69);
        tf.setLayoutY(324);
        tf.setFont(new Font(25));
        Text tfc = new Text(String.valueOf(sfouls));
        tfc.setLayoutX(333);
        tfc.setLayoutY(328);
        tfc.setFont(new Font("System Bold", 33));
        Text tminp = new Text("Total Minutes Played : ");
        tminp.setLayoutX(69);
        tminp.setLayoutY(374);
        tminp.setFont(new Font(25));
        Text tminpc = new Text(String.valueOf(smp));
        tminpc.setLayoutX(333);
        tminpc.setLayoutY(377);
        tminpc.setFont(new Font("System Bold", 33));
        ProgressIndicator pi = new ProgressIndicator(avgrating / 10);
        pi.setLayoutX(966);
        pi.setLayoutY(174);
        pi.setPrefHeight(193);
        pi.setPrefWidth(279);
        Text ar = new Text("Averege Rating :");
        ar.setLayoutX(990);
        ar.setLayoutY(404);
        ar.setFont(new Font(25));
        Text arc = new Text(String.valueOf(avgrating));
        arc.setLayoutX(1188);
        arc.setLayoutY(410);
        arc.setFont(new Font("System Bold", 41));
        homePane.getChildren().addAll(nameText, natpos, contactEx, tmp, tmpc, tgs, tgsc, gpg, gpgc, tf, tfc, tminp, tminpc, pi, ar, arc, gpm, gpmc);
        homeTab.setContent(homePane);
        Tab profileTab = new Tab();
        profileTab.setText("Profile");
        AnchorPane profilePane = new AnchorPane();
        profilePane.setPrefHeight(180);
        profilePane.setPrefWidth(200);
        profilePane.setStyle("-fx-background-color: #ccfbff");
        //sql
        sql = "select TO_CHAR(DATE_OF_BIRTH,'dd-MON-yyyy'),HEIGHT,WEIGHT,CONTACT_NO,WAGE,MARKET_VALUE,BUY_OUT_CLAUSE,AGENT_NAME,TO_CHAR(CONTACT_TILL,'dd-MON-yyyy') from PLAYERS where PLAYER_ID=" + pid;
        String dob = "", height = "", weight = "", contact = "", wage = "", mv = "", boc = "",agent="",contracttill="",jdate="";
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                dob=rs.getString(1);
                height=rs.getString(2);
                weight=rs.getString(3);
                contact=rs.getString(4);
                wage=rs.getString(5);
                mv=rs.getString(6);
                boc=rs.getString(7);
                agent=rs.getString(8);
                contracttill=rs.getString(9);
            }
        } catch (SQLException e) {
            errorAlert("Error", "Error", null);
        }
        sql = "SELECT jdate from PLAYER_TEAM where PLAYER_ID=" + pid;
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                jdate=rs.getString(1);
            }
        } catch (SQLException e) {
            errorAlert("Errorr", "Errorr", null);
        }

        profileTab.setContent(profilePane);
        Tab statsTab = new Tab();
        statsTab.setText("Stats");
        AnchorPane statsPane = new AnchorPane();
        statsPane.setPrefHeight(180);
        statsPane.setPrefWidth(200);
        statsPane.setStyle("-fx-background-color: #ccfbff");
        statsTab.setContent(statsPane);
        Tab teamTab = new Tab();
        teamTab.setText("Team");
        AnchorPane teamPane = new AnchorPane();
        teamPane.setPrefHeight(180);
        teamPane.setPrefWidth(200);
        teamPane.setStyle("-fx-background-color: #ccfbff");
        teamTab.setContent(teamPane);
        Tab editTab = new Tab();
        editTab.setText("Edit Profile");
        AnchorPane editPane = new AnchorPane();
        editPane.setPrefHeight(180);
        editPane.setPrefWidth(200);
        editPane.setStyle("-fx-background-color: #ccfbff");
        editTab.setContent(editPane);
        tabs.getTabs().addAll(homeTab, profileTab, statsTab, teamTab, editTab);
        box.getChildren().addAll(topPane, tabs);
        Scene scene = new Scene(box);
        stage.setScene(scene);
        stage.setTitle(name);
        stage.show();
        stage.getIcons().add(new Image("icon.png"));
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });
    }
    //new

    public static void managerPage(int mid) {
        AnchorPane mainPane = new AnchorPane();
        mainPane.setPrefWidth(1550);
        mainPane.setPrefHeight(830);
        // query to retrive managers name
        AtomicReference<String> sql = new AtomicReference<>("SELECT STAFF_NAME FROM STAFFS WHERE STAFF_ID= (SELECT STAFF_ID FROM MANAGERS WHERE MANAGER_ID = " + mid + ")");
        String managerName = "";
        try {
            pst = con.prepareStatement(sql.get());
            rs = pst.executeQuery();
            while (rs.next()) {
                managerName = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Text t1 = new Text(managerName);
        t1.setX(1300);
        t1.setY(40);
        Font f = new Font("System Bold", 30);
        t1.setFont(f);
        Text t2 = new Text("(Logout)");
        t2.setFont(f);
        t2.setX(1330);
        t2.setY(80);
        t2.setOnMouseEntered((MouseEvent event) -> {
            t2.setFont(new Font("System Bold", 35));
            t2.setFill(Color.DARKGRAY);
        });
        t2.setOnMouseExited((MouseEvent event) -> {
            t2.setFont(new Font("System Bold", 30));
            t2.setFill(Color.BLACK);
        });
        t2.setOnMouseClicked((MouseEvent event) -> {
            loginScreen();
        });
        Text viewEmployees = new Text("View Employees");
        viewEmployees.setLayoutX(70);
        viewEmployees.setLayoutY(130);
        viewEmployees.setFont(new Font("System Bold", 40));
        Text editProfile = new Text("Edit Profile");
        editProfile.setLayoutX(450);
        editProfile.setLayoutY(130);
        editProfile.setFont(new Font("System Bold", 40));
        if (managerSceneFlag == 0) {
            viewEmployees.setFill(Color.BLACK);
            editProfile.setFill(Color.DARKGRAY);
        } else {
            viewEmployees.setFill(Color.DARKGRAY);
            editProfile.setFill(Color.BLACK);
        }
        viewEmployees.setOnMouseClicked((MouseEvent event) -> {
            managerSceneFlag = 0;
            managerPage(mid);
        });
        editProfile.setOnMouseClicked((MouseEvent event) -> {

            managerSceneFlag = 1;
            managerPage(mid);
        });
        Text name, address, contact;
        TextField nameField, addressField, contactField;
        Button updateButton;
        if (managerSceneFlag == 0) { // view employees
            // create table 
            managerStaffTable = new TableView<ManagerStaff>();
            managerStaffTable.setLayoutX(80);
            managerStaffTable.setLayoutY(200);
            managerStaffTable.setPrefHeight(530);
            managerStaffTable.setPrefWidth(955);
            managerStaffTable.setEditable(false);
            TableColumn<ManagerStaff, Integer> c1 = new TableColumn("Staff ID");
            TableColumn<ManagerStaff, String> c2 = new TableColumn("Staff Name");
            TableColumn<ManagerStaff, String> c3 = new TableColumn("Staff Address");
            TableColumn<ManagerStaff, Integer> c4 = new TableColumn("Contact No");
            TableColumn<ManagerStaff, String> c5 = new TableColumn("Type");
            TableColumn<ManagerStaff, Integer> c6 = new TableColumn("Salary");
            c1.setCellValueFactory(new PropertyValueFactory<>("staff_id"));
            c2.setCellValueFactory(new PropertyValueFactory<>("staff_name"));
            c3.setCellValueFactory(new PropertyValueFactory<>("staff_address"));
            c4.setCellValueFactory(new PropertyValueFactory<>("contact_no"));
            c5.setCellValueFactory(new PropertyValueFactory<>("type"));
            c6.setCellValueFactory(new PropertyValueFactory<>("salary"));
            c1.setMinWidth(150);
            c2.setMinWidth(150);
            c3.setMinWidth(200);
            c4.setMinWidth(150);
            c5.setMinWidth(150);
            c6.setMinWidth(150);
            managerStaffTable.getColumns().addAll(c1, c2, c3, c4, c5, c6);
            // run sql
            sql.set("( SELECT ST.STAFF_ID,ST.STAFF_NAME, ST.STAFF_ADDRESS,ST.CONTACT_NO,ST.TYPE,ST.SALARY FROM SCOUTS SC JOIN STAFFS ST ON (SC.STAFF_ID=ST.STAFF_ID) WHERE TEAM_ID = ( SELECT TEAM_ID FROM MANAGERS WHERE MANAGER_ID=" + mid + ") ) UNION ( SELECT ST.STAFF_ID,ST.STAFF_NAME, ST.STAFF_ADDRESS,ST.CONTACT_NO,ST.TYPE,ST.SALARY FROM MEDICALS MC,MEDICAL_TEAMS_TEAMS MTT, STAFFS ST WHERE TEAM_ID = ( SELECT TEAM_ID FROM MANAGERS WHERE MANAGER_ID=" + mid + ") AND MC.MTEAM_ID = MTT.MTEAM_ID AND MC.STAFF_ID = ST.STAFF_ID )");
            // update table
            try {
                pst = con.prepareStatement(sql.get());
                rs = pst.executeQuery();
                msdata.clear();
                while (rs.next()) {
                    msdata.add(new ManagerStaff(new SimpleIntegerProperty(rs.getInt(1)), new SimpleStringProperty(rs.getString(2)), new SimpleStringProperty(rs.getString(3)), new SimpleIntegerProperty(rs.getInt(4)), new SimpleStringProperty(rs.getString(5)), new SimpleIntegerProperty(rs.getInt(6))));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            managerStaffTable.setItems(msdata);
            mainPane.getChildren().addAll(t1, t2, viewEmployees, editProfile, managerStaffTable);
        } else { // edit profile
            //sql to retrive information about manager
            sql.set("SELECT STAFF_ADDRESS,CONTACT_NO FROM STAFFS WHERE STAFF_ID=(SELECT STAFF_ID FROM MANAGERS WHERE MANAGER_ID = " + mid + ")");
            String managerAddress = "";
            int managerContact = 0;
            try {
                pst = con.prepareStatement(sql.get());
                rs = pst.executeQuery();
                while (rs.next()) {
                    managerAddress = rs.getString(1);
                    managerContact = rs.getInt(2);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            name = new Text("Name :");
            name.setLayoutX(150);
            name.setLayoutY(220);
            name.setFont(new Font("System Bold", 30));
            nameField = new TextField();
            nameField.setLayoutX(150);
            nameField.setLayoutY(240);
            nameField.setPrefHeight(35);
            nameField.setPrefWidth(300);
            nameField.setEffect(borderGlow);
            nameField.setText(managerName);
            address = new Text("Address :");
            address.setLayoutX(150);
            address.setLayoutY(320);
            address.setFont(new Font("System Bold", 30));
            addressField = new TextField();
            addressField.setLayoutX(150);
            addressField.setLayoutY(340);
            addressField.setPrefHeight(35);
            addressField.setPrefWidth(300);
            addressField.setEffect(borderGlow);
            addressField.setText(managerAddress);
            contact = new Text("Contact No :");
            contact.setLayoutX(150);
            contact.setLayoutY(420);
            contact.setFont(new Font("System Bold", 30));
            contactField = new TextField();
            contactField.setLayoutX(150);
            contactField.setLayoutY(440);
            contactField.setPrefHeight(35);
            contactField.setPrefWidth(300);
            contactField.setEffect(borderGlow);
            contactField.setText(String.valueOf(managerContact));
            updateButton = new Button("Update");
            updateButton.setLayoutX(380);
            updateButton.setLayoutY(500);
            updateButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    String tempName, tempAddress;
                    int tempContact = 0;
                    tempName = nameField.getText();
                    tempAddress = addressField.getText();
                    try {
                        tempContact = Integer.parseInt(contactField.getText());
                        String query = "UPDATE STAFFS SET STAFF_NAME = '" + tempName + "', STAFF_ADDRESS='" + tempAddress + "', CONTACT_NO = " + tempContact + "  WHERE STAFF_ID = (SELECT STAFF_ID FROM MANAGERS WHERE MANAGER_ID = " + mid + ")";
                        System.out.println(query);
                        try {
                            pst = con.prepareStatement(query);
                            rs = pst.executeQuery();
                            errorAlert("Updated", "Profile updated successfully", null);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            errorAlert("Error", "Invalid input", null);
                        }
                    } catch (Exception e) {
                        errorAlert("Error", "Invalid input", null);
                    }
                }
            });
            mainPane.getChildren().addAll(t1, t2, viewEmployees, editProfile, name, nameField, address, addressField, contact, contactField, updateButton);
        }
        mainPane.getStylesheets().add(Main.class.getResource("table.css").toExternalForm());
        mainPane.setStyle("-fx-background-color: #d8fbff");
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Football Club Management");
        stage.show();
        stage.getIcons().add(new Image("icon.png"));
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });
    }


    public static void showData() {
        AnchorPane mainPane = new AnchorPane();
        mainPane.setPrefHeight(795);
        mainPane.setPrefWidth(1089);
        mainPane.setStyle("-fx-background-color: #ccfbff;");
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
        logg.setLayoutY(256.0 - 100 + 20);
        f = new Font("System Bold", 27);
        logg.setFont(f);
        TextField teamName = new TextField();
        teamName.setLayoutX(1100);
        teamName.setLayoutY(338 - 100);
        teamName.setPrefHeight(35);
        teamName.setPrefWidth(300);
        teamName.setEffect(borderGlow);
        Text t1 = new Text("Team Name :");
        t1.setLayoutX(1100);
        t1.setLayoutY(321 - 100);
        f = new Font("System Bold", 18);
        t1.setFont(f);
        Text t2 = new Text("Manager Id :");
        t2.setLayoutX(1100);
        t2.setLayoutY(408 - 100);
        f = new Font("System Bold", 18);
        t2.setFont(f);
        Text t3 = new Text("Medical Id :");
        t3.setLayoutX(1100);
        t3.setLayoutY(321 - 100 + 200 - 30);
        f = new Font("System Bold", 18);
        t3.setFont(f);
        Text t4 = new Text("Scout Id :");
        t4.setLayoutX(1100);
        t4.setLayoutY(408 - 100 + 200 - 40);
        f = new Font("System Bold", 18);
        t4.setFont(f);
        Text t5 = new Text("Captain :");
        t5.setLayoutX(1100);
        t5.setLayoutY(408 - 100 + 200 + 100 - 70);
        f = new Font("System Bold", 18);
        t5.setFont(f);
        TextField managerID = new TextField();
        managerID.setLayoutX(1100);
        managerID.setLayoutY(423 - 100);
        managerID.setPrefHeight(35);
        managerID.setPrefWidth(300);
        managerID.setEffect(borderGlow);
        TextField medicID = new TextField();
        medicID.setLayoutX(1100);
        medicID.setLayoutY(423 - 100 + 100 - 20);
        medicID.setPrefHeight(35);
        medicID.setPrefWidth(300);
        medicID.setEffect(borderGlow);
        TextField scoutID = new TextField();
        scoutID.setLayoutX(1100);
        scoutID.setLayoutY(423 - 100 + 100 + 100 - 45);
        scoutID.setPrefHeight(35);
        scoutID.setPrefWidth(300);
        scoutID.setEffect(borderGlow);
        TextField captain = new TextField();
        captain.setLayoutX(1100);
        captain.setLayoutY(423 - 100 + 100 + 200 - 70);
        captain.setPrefHeight(35);
        captain.setPrefWidth(300);
        captain.setEffect(borderGlow);
        // button
        Button loginButton = new Button("Add Data");
        loginButton.setLayoutX(1310);
        loginButton.setLayoutY(495 - 100 + 300 - 80);
        loginButton.setOnMouseClicked((MouseEvent event) -> {
            int temp = tableSize + 1;
            String sql = "INSERT INTO TEAMS(TEAM_ID,TEAM_NAME,MANAGER_ID,SCOUT_ID,MEDIC_ID, CAPTAIN) VALUES (" + "'" + temp + "', '" + teamName.getText() + "','" + managerID.getText() + "', '" + medicID.getText() + "', '" + scoutID.getText() + "', '" + captain.getText() + "')";
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
                errorAlert("Invalid Data.", "Invalid Data", null);
            }
        });
        table.setOnMouseClicked((MouseEvent event) -> {
            Team app = new Team(1, "ss", "s", "s", "s", "s");
            if (!table.getSelectionModel().isEmpty()) {
                //table.getSelectionModel().getSelectedCells().get
                app = (Team) table.getSelectionModel().getSelectedItem();
                showPopup(app, event.getScreenX(), event.getScreenY());
            }
        });
        mainPane.getChildren().addAll(titlePane, log, table, logg, loginButton, teamName, t1, t2, t3, t4, t5, managerID, medicID, scoutID, captain);
        mainPane.getStylesheets().add(Main.class.getResource("table.css").toExternalForm());
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Teams table");
        stage.show();
        stage.getIcons().add(new Image("icon.png"));
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

    public static void updateTable() {
        String sql = "SELECT * FROM TEAMS ORDER BY TEAM_ID";
        try {
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:globaldb", "clubmanagement", "football");
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            data.clear();
            while (rs.next()) {
                data.add(new Team(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)));
                tableSize++;
                // System.out.println(rs.getInt("team_id") + " , " + rs.getString(2) + " , " + rs.getString(3) + " , " + rs.getString(4) + " , " + rs.getString(5) + " , " + rs.getString(6));
            }
            table.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTable() {
        table = new TableView<Team>();
        table.setLayoutX(33);
        table.setLayoutY(200);
        table.setPrefHeight(512);
        table.setPrefWidth(652);
        table.setEditable(false);
        TableColumn<Team, Integer> c1 = new TableColumn("Team ID");
        TableColumn<Team, String> c2 = new TableColumn("Name");
        TableColumn<Team, String> c3 = new TableColumn("Manager ID");
        TableColumn<Team, String> c4 = new TableColumn("Scout ID");
        TableColumn<Team, String> c5 = new TableColumn("Medic ID");
        TableColumn<Team, String> c6 = new TableColumn("Captain");
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
        table.getColumns().addAll(c1, c2, c3, c4, c5, c6);
    }

    public static void showPopup(Team team, double x, double y) {
        AnchorPane pane = new AnchorPane();
        pane.setStyle("-fx-background-color: linear-gradient(#5595fc 0%, #3a84fc 50%, #1e73fc 100%);-fx-text-fill: white;");
        pane.setPrefHeight(200);
        pane.setPrefWidth(200);
        Text t = new Text();
        t.setText(team.captain.getValue());
        t.setLayoutX(50);
        t.setLayoutY(50);
        t.setStyle("-fx-font-size: 20;-fx-text-fill: blue;-fx-font-weight: bold;");
        pane.getChildren().add(t);
        popup.getContent().clear();
        popup.getContent().addAll(pane);
        popup.setX(x);
        popup.setY(y);
        popup.setAutoHide(true);
        popup.show(stage);
    }

    //
    public static void boardMemberPage(int id) {
        //boardMemberSceneFlag: 0 for view employees, 1 for profile update, 2 for manager, 3 for scout, 4 for board members, 5 for medical staffs
        AnchorPane mainPane = new AnchorPane();
        mainPane.setPrefWidth(1550);
        mainPane.setPrefHeight(830);
        AtomicReference<String> sql = new AtomicReference<>("SELECT STAFF_NAME FROM STAFFS WHERE STAFF_ID = (SELECT STAFF_ID FROM BOARD_MEMBERS WHERE BMEMBER_ID=" + id + ")");
        String memberName = "";
        try {
            pst = con.prepareStatement(sql.get());
            rs = pst.executeQuery();
            while (rs.next()) {
                memberName = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Text t1 = new Text(memberName);
        t1.setX(1000);
        t1.setY(40);
        Font f = new Font("System Bold", 30);
        t1.setFont(f);
        Text t2 = new Text("(Logout)");
        t2.setFont(f);
        t2.setX(1000);
        t2.setY(80);
        t2.setOnMouseEntered((MouseEvent event) -> {
            t2.setFont(new Font("System Bold", 35));
            t2.setFill(Color.DARKGRAY);
        });
        t2.setOnMouseExited((MouseEvent event) -> {
            t2.setFont(new Font("System Bold", 30));
            t2.setFill(Color.BLACK);
        });
        t2.setOnMouseClicked((MouseEvent event) -> {
            loginScreen();
        });
        Text viewEmployees = new Text("View Employees");
        viewEmployees.setLayoutX(70);
        viewEmployees.setLayoutY(130);
        viewEmployees.setFont(new Font("System Bold", 40));
        Text editProfile = new Text("Edit Profile");
        editProfile.setLayoutX(450);
        editProfile.setLayoutY(130);
        editProfile.setFont(new Font("System Bold", 40));
        if (boardMemberSceneFlag == 0) {
            viewEmployees.setFill(Color.BLACK);
            editProfile.setFill(Color.DARKGRAY);
        } else {
            viewEmployees.setFill(Color.DARKGRAY);
            editProfile.setFill(Color.BLACK);
        }
        viewEmployees.setOnMouseClicked((MouseEvent event) -> {
            boardMemberSceneFlag = 0;
            boardMemberPage(id);
        });
        editProfile.setOnMouseClicked((MouseEvent event) -> {

            boardMemberSceneFlag = 1;
            boardMemberPage(id);
        });
        Button back = new Button("back");
        back.setLayoutX(1000);
        back.setLayoutY(80);
        Button manager = new Button("Managers");
        manager.setLayoutX(90);
        manager.setLayoutY(180);
        Button scout = new Button("Scouts");
        scout.setLayoutX(90);
        scout.setLayoutY(220);
        back.setOnMouseClicked((MouseEvent event) -> {
                    boardMemberSceneFlag = 0;
                    boardMemberPage(id);
                }
        );
        manager.setOnMouseClicked((MouseEvent event) -> {
                    boardMemberSceneFlag = 2;
                    boardMemberPage(id);
                }
        );
        scout.setOnMouseClicked((MouseEvent event) -> {
                    boardMemberSceneFlag = 3;
                    boardMemberPage(id);
                }
        );
        if (boardMemberSceneFlag == 1) {
            sql.set("SELECT STAFF_NAME FROM STAFFS WHERE STAFF_ID= (SELECT STAFF_ID FROM BOARD_MEMBERS WHERE BMEMBER_ID=" + id + ")");
            String Name = "";
            try {
                pst = con.prepareStatement(sql.get());
                rs = pst.executeQuery();
                while (rs.next()) {
                    Name = rs.getString(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            sql.set("SELECT STAFF_ADDRESS,CONTACT_NO FROM STAFFS WHERE STAFF_ID= (SELECT STAFF_ID FROM BOARD_MEMBERS WHERE BMEMBER_ID=" + id + ")");
            String Address = "";
            int Contact = 0;
            try {
                pst = con.prepareStatement(sql.get());
                rs = pst.executeQuery();
                while (rs.next()) {
                    Address = rs.getString(1);
                    Contact = rs.getInt(2);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Text name = new Text("Name :");
            name.setLayoutX(150);
            name.setLayoutY(220);
            name.setFont(new Font("System Bold", 30));
            TextField nameField = new TextField();
            nameField.setLayoutX(150);
            nameField.setLayoutY(240);
            nameField.setPrefHeight(35);
            nameField.setPrefWidth(300);
            nameField.setEffect(borderGlow);
            nameField.setText(Name);
            Text address = new Text("Address :");
            address.setLayoutX(150);
            address.setLayoutY(320);
            address.setFont(new Font("System Bold", 30));
            TextField addressField = new TextField();
            addressField.setLayoutX(150);
            addressField.setLayoutY(340);
            addressField.setPrefHeight(35);
            addressField.setPrefWidth(300);
            addressField.setEffect(borderGlow);
            addressField.setText(Address);
            Text contact = new Text("Contact No :");
            contact.setLayoutX(150);
            contact.setLayoutY(420);
            contact.setFont(new Font("System Bold", 30));
            TextField contactField = new TextField();
            contactField.setLayoutX(150);
            contactField.setLayoutY(440);
            contactField.setPrefHeight(35);
            contactField.setPrefWidth(300);
            contactField.setEffect(borderGlow);
            contactField.setText(String.valueOf(Contact));
            Button updateButton = new Button("Update");
            updateButton.setLayoutX(380);
            updateButton.setLayoutY(500);
            updateButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    String tempName, tempAddress;
                    int tempContact = 0;
                    tempName = nameField.getText();
                    tempAddress = addressField.getText();
                    try {
                        tempContact = Integer.parseInt(contactField.getText());
                        String query = "UPDATE STAFFS SET STAFF_NAME = '" + tempName + "', STAFF_ADDRESS='" + tempAddress + "', CONTACT_NO = " + tempContact + "  WHERE STAFF_ID = (SELECT STAFF_ID FROM BOARD_MEMBERS WHERE BMEMBER_ID=" + id + ")";
                        System.out.println(query);
                        try {
                            pst = con.prepareStatement(query);
                            rs = pst.executeQuery();
                            errorAlert("Updated", "Profile updated successfully", null);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            errorAlert("Error", "Invalid input", null);
                        }
                    } catch (Exception e) {
                        errorAlert("Error", "Invalid input", null);
                    }
                }
            });
            mainPane.getChildren().addAll(t1, t2, viewEmployees, editProfile, name, nameField, address, addressField, contact, contactField, updateButton);
        }
        if (boardMemberSceneFlag == 2) {
            boardStaffTable = new TableView<BoardStaff>();
            boardStaffTable.setLayoutX(80);
            boardStaffTable.setLayoutY(200);
            boardStaffTable.setPrefHeight(530);
            boardStaffTable.setPrefWidth(955);
            boardStaffTable.setEditable(false);
            TableColumn<BoardStaff, Integer> c1 = new TableColumn("Staff ID");
            TableColumn<BoardStaff, String> c2 = new TableColumn("Staff Name");
            TableColumn<BoardStaff, String> c3 = new TableColumn("Staff Address");
            TableColumn<BoardStaff, Integer> c4 = new TableColumn("Contact No");
            //TableColumn<BoardStaff, String> c5 = new TableColumn("Type");
            TableColumn<BoardStaff, Integer> c6 = new TableColumn("Salary");
            c1.setCellValueFactory(new PropertyValueFactory<>("staff_id"));
            c2.setCellValueFactory(new PropertyValueFactory<>("staff_name"));
            c3.setCellValueFactory(new PropertyValueFactory<>("staff_address"));
            c4.setCellValueFactory(new PropertyValueFactory<>("contact_no"));
            //c5.setCellValueFactory(new PropertyValueFactory<>("type"));
            c6.setCellValueFactory(new PropertyValueFactory<>("salary"));
            c1.setMinWidth(150);
            c2.setMinWidth(150);
            c3.setMinWidth(200);
            c4.setMinWidth(150);
            //c5.setMinWidth(150);
            c6.setMinWidth(150);
            boardStaffTable.getColumns().addAll(c1, c2, c3, c4, c6);
            sql.set("SELECT STAFF_ID, STAFF_NAME, STAFF_ADDRESS, CONTACT_NO, SALARY FROM STAFFS WHERE TYPE='Manager' ");
            try {
                pst = con.prepareStatement(sql.get());
                rs = pst.executeQuery();
                System.out.println("rs passed");
                bsdata.clear();
                while (rs.next()) {
                    bsdata.add(new BoardStaff(new SimpleIntegerProperty(rs.getInt(1)), new SimpleStringProperty(rs.getString(2)), new SimpleStringProperty(rs.getString(3)), new SimpleIntegerProperty(rs.getInt(4)), new SimpleIntegerProperty(rs.getInt(5))));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            boardStaffTable.setItems(bsdata);
            mainPane.getChildren().addAll(boardStaffTable, back);
        }
        if (boardMemberSceneFlag == 3) {
            boardStaffTable = new TableView<BoardStaff>();
            boardStaffTable.setLayoutX(80);
            boardStaffTable.setLayoutY(200);
            boardStaffTable.setPrefHeight(530);
            boardStaffTable.setPrefWidth(955);
            boardStaffTable.setEditable(false);
            TableColumn<BoardStaff, Integer> c1 = new TableColumn("Staff ID");
            TableColumn<BoardStaff, String> c2 = new TableColumn("Staff Name");
            TableColumn<BoardStaff, String> c3 = new TableColumn("Staff Address");
            TableColumn<BoardStaff, Integer> c4 = new TableColumn("Contact No");
            //TableColumn<BoardStaff, String> c5 = new TableColumn("Type");
            TableColumn<BoardStaff, Integer> c6 = new TableColumn("Salary");
            c1.setCellValueFactory(new PropertyValueFactory<>("staff_id"));
            c2.setCellValueFactory(new PropertyValueFactory<>("staff_name"));
            c3.setCellValueFactory(new PropertyValueFactory<>("staff_address"));
            c4.setCellValueFactory(new PropertyValueFactory<>("contact_no"));
            //c5.setCellValueFactory(new PropertyValueFactory<>("type"));
            c6.setCellValueFactory(new PropertyValueFactory<>("salary"));
            c1.setMinWidth(150);
            c2.setMinWidth(150);
            c3.setMinWidth(200);
            c4.setMinWidth(150);
            //c5.setMinWidth(150);
            c6.setMinWidth(150);
            boardStaffTable.getColumns().addAll(c1, c2, c3, c4, c6);
            sql.set("SELECT STAFF_ID, STAFF_NAME, STAFF_ADDRESS, CONTACT_NO, SALARY FROM STAFFS WHERE TYPE='Scout' ");
            try {
                pst = con.prepareStatement(sql.get());
                rs = pst.executeQuery();
                System.out.println("rs passed");
                bsdata.clear();
                while (rs.next()) {
                    bsdata.add(new BoardStaff(new SimpleIntegerProperty(rs.getInt(1)), new SimpleStringProperty(rs.getString(2)), new SimpleStringProperty(rs.getString(3)), new SimpleIntegerProperty(rs.getInt(4)), new SimpleIntegerProperty(rs.getInt(5))));

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            boardStaffTable.setItems(bsdata);
            mainPane.getChildren().addAll(boardStaffTable, back);
        }
        if (boardMemberSceneFlag == 0) {
            mainPane.getChildren().addAll(t1, t2, viewEmployees, editProfile, manager, scout);
        }
        mainPane.getStylesheets().add(Main.class.getResource("table.css").toExternalForm());
        mainPane.setStyle("-fx-background-color: #abfaff");
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Football Club Management");
        stage.show();
        stage.getIcons().add(new Image("icon.png"));
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });
    }
}
