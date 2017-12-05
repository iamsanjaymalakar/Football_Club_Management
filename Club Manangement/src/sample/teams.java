package sample;

import javafx.beans.property.SimpleStringProperty;

import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public class teams {
    private final SimpleStringProperty team_id;
    private final SimpleStringProperty team_name;

    public String getTeam_id() {
        return team_id.get();
    }

    public SimpleStringProperty team_idProperty() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id.set(team_id);
    }

    public String getTeam_name() {
        return team_name.get();
    }

    public SimpleStringProperty team_nameProperty() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name.set(team_name);
    }

    public String getManager_id() {
        return manager_id.get();
    }

    public SimpleStringProperty manager_idProperty() {
        return manager_id;
    }

    public void setManager_id(String manager_id) {
        this.manager_id.set(manager_id);
    }

    public String getScout_id() {
        return scout_id.get();
    }

    public SimpleStringProperty scout_idProperty() {
        return scout_id;
    }

    public void setScout_id(String scout_id) {
        this.scout_id.set(scout_id);
    }

    public String getMedic_id() {
        return medic_id.get();
    }

    public SimpleStringProperty medic_idProperty() {
        return medic_id;
    }

    public void setMedic_id(String medic_id) {
        this.medic_id.set(medic_id);
    }

    public String getCaptain() {
        return captain.get();
    }

    public SimpleStringProperty captainProperty() {
        return captain;
    }

    public void setCaptain(String captain) {
        this.captain.set(captain);
    }

    private final SimpleStringProperty manager_id;
    private final SimpleStringProperty scout_id;
    private final SimpleStringProperty medic_id;
    private final SimpleStringProperty captain;

    teams(String id, String name, String mid,String sid,String mdid,String cpt) {
        this.team_id = new SimpleStringProperty(id);
        this.team_name = new SimpleStringProperty(name);
        this.manager_id = new SimpleStringProperty(mid);
        this.scout_id = new SimpleStringProperty(sid);
        this.medic_id = new SimpleStringProperty(mdid);
        this.captain = new SimpleStringProperty(cpt);

    }

}

