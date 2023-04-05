package com.example.demojavafx;

import com.example.demojavafx.tools.Notification;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class StudentController implements Initializable {

    Connection conn;

    PreparedStatement preparedStatement;

    int index, id;

    @FXML
    private TableView<Student> table;


    @FXML
    private TextField txtName, txtMatiere;

    @FXML
    private TableColumn<Student, String> idCol, nameCol, matiereCol;

    private IStudent studentDao = new StudentImpl();

    @FXML
    private Button addBtn, updateBtn, deleteBtn;


    public void Connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tp_javafx", "root", "");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Connect();
        Table();
    }
    @FXML
    void Add(ActionEvent actionEvent) {
        String name, matiere;

        name = txtName.getText();
        matiere = txtMatiere.getText();

        if (name.isEmpty() || matiere.isEmpty()) {
            //show error alert
            Notification.NotifError( "Feilds are empty", "Please fill all the feilds");
        } else {
            Student student = new Student();
            student.setName(name);
            student.setMatiere(matiere);
            if(studentDao.add(student) == 1){
                Notification.NotifSuccess("Success", "Etudiant ajouté avec succes");
                reset();
                Table();
            }else{
                Notification.NotifError("Erreur","Insertion échouée !");
            }

        }
    }


    @FXML
    void Update(ActionEvent actionEvent) {
        Connect();
        //String name, matiere;
        index = table.getSelectionModel().getSelectedIndex();
        if (index <= -1) {
            Notification.NotifError("Error", "Please select a record to update");
            return;
        }
        Student student = new Student();
        //id = Integer.parseInt(String.valueOf(table.getItems().get(index).getId()));
        student.setId(table.getItems().get(index).getId());
        student.setName(txtName.getText());
        student.setMatiere(txtMatiere.getText());
        if(studentDao.update(student) == 1){
            Notification.NotifSuccess("Success", "Etudiant modifié avec succes");
            Table();
        }else{
            Notification.NotifError("Erreur","Modification échouée !");
        }


    }

    @FXML
    void Delete(ActionEvent actionEvent) {

        index = table.getSelectionModel().getSelectedIndex();
        if (index <= -1) {
            Notification.NotifError("Erreur", "Please select a record to update");
            return;
        }
        id = Integer.parseInt(String.valueOf(table.getItems().get(index).getId()));
        if(studentDao.delete(id) == 1){
            Notification.NotifSuccess("Success", "Etudiant supprimé avec succes");
            reset();
            Table();
        }else{
            Notification.NotifError("Erreur","Suppression échouée !");
        }

    }


    public void reset() {
        txtName.setText("");
        txtMatiere.setText("");
    }

    public void Table() {
        Connect();
        ObservableList<Student> students = FXCollections.observableArrayList();
        try {
            preparedStatement = conn.prepareStatement("select * from student");
            ResultSet result = preparedStatement.executeQuery();

            while (result.next()) {
                Student student = new Student();
                student.setId(result.getString("id"));
                student.setName(result.getString("name"));
                student.setMatiere(result.getString("matiere"));


                students.add(student);

                table.setItems(students);
                idCol.setCellValueFactory(data -> data.getValue().idProperty());
                nameCol.setCellValueFactory(data -> data.getValue().nameProperty());
                matiereCol.setCellValueFactory(data -> data.getValue().matiereProperty());

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //display selected row data in textfields
        table.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    Student rowData = row.getItem();
                    index = row.getIndex();
                    id = Integer.parseInt(rowData.getId());
                    txtName.setText(rowData.getName());
                    txtMatiere.setText(rowData.getMatiere());
                }
            });
            return row;
        });


    }

    @FXML
    void Reset(ActionEvent actionEvent) {
        reset();
    }
}
