package lk.ijse.gdse69.javafx.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import lk.ijse.gdse69.javafx.Alert.ShowAlert;
import lk.ijse.gdse69.javafx.Model.Inmate;
import lk.ijse.gdse69.javafx.Model.InmateRecord;
import lk.ijse.gdse69.javafx.Model.Section;
import lk.ijse.gdse69.javafx.Model.SetFirstInmateRecord;
import lk.ijse.gdse69.javafx.Repository.InmateRepo;
import lk.ijse.gdse69.javafx.Repository.SectionRepo;
import lk.ijse.gdse69.javafx.Repository.SetFirstInmateRecordRepo;
import lk.ijse.gdse69.javafx.Util.Util;
import org.controlsfx.control.textfield.TextFields;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddInmateController extends MainDashBoard implements Initializable {

    public AnchorPane MainAnchorPane;
    public PieChart freeSpace;  ////////////////////
    /////////
    public TextField inmateSearchId;


    public Text tGenderInmateCount;
    public Text femaleInmateCount;
    public Text maleInmateCount;
    public Text totalInmateCount;
    ////////////
    @FXML
    private TextField inmateId;
    @FXML
    private TextField inmateNIC;
    @FXML
    private TextField inmateFName;
    @FXML
    private TextField inmateLName;
    @FXML
    private ComboBox<String> inmateGender;
    @FXML
    private DatePicker inmateDOB;
    @FXML
    private TextField inmateAddress;
    @FXML
    private ComboBox<String> inmateStatus;

    @FXML
    private ComboBox<String> inRecoSectionId;
    @FXML
    private TextField inRecoCrime;
    @FXML
    private DatePicker inRecoReleseDate;

    @FXML
    private ComboBox<String> caseStatusComboBox;

    ShowAlert showAlert;

    @FXML
    public Button inmateBtn;
    public Button officerBtn;
    public Button dashBoardBtn;
    public Button settingBtn;
    public Button manyBtn;
    public Button sectionBtn;
    public Button visitorBtn;

    private byte[] imageDate;
    public static String inmateIdForSearch;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setToolTip();

        try {
            setGenderCount();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            setValues();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            setComboBoxValues();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        searchInmateId();

    }

    private void searchInmateId() {
        List<String> inmateIds = new ArrayList<>();

        try {
            List<Inmate> allInmates = InmateRepo.getAllInmates();
            for (Inmate inmate : allInmates) {
                inmateIds.add(inmate.getInmateId()+" - "+inmate.getInmateFirstName()+" "+inmate.getInmateLastName());
            }
            String[] possibleNames = inmateIds.toArray(new String[0]);

            TextFields.bindAutoCompletion(inmateSearchId, possibleNames);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setValues() throws SQLException {
        List<Section> allSections = SectionRepo.getJailSections();

        int totalSpase=0;

        for (Section section : allSections) {
            totalSpase+=section.getCapacity();
        }

        int totalInmates = InmateRepo.getAllInmates().size();

        int freeSpaseCount = totalSpase-totalInmates;

        this.freeSpace.getData().add(new PieChart.Data("Free Spase",freeSpaseCount));
        this.freeSpace.getData().add(new PieChart.Data("Occupied Spase",totalInmates));



    }
    private void setGenderCount() throws SQLException {

        maleInmateCount.setText(String.valueOf(InmateRepo.getInmatesByGender("Male").size())+" Inmates");
        femaleInmateCount.setText(String.valueOf(InmateRepo.getInmatesByGender("Female").size())+" Inmates");
        tGenderInmateCount.setText(String.valueOf(InmateRepo.getInmatesByGender("Transgender").size())+" Inmates");
        totalInmateCount.setText(String.valueOf(InmateRepo.getAllInmates().size())+" Inmates");

    }

    private void setComboBoxValues() throws SQLException {
        caseStatusComboBox.getItems().addAll("Low", "Medium", "High");
        inmateGender.getItems().addAll("Male", "Female");
        inmateStatus.getItems().addAll("Active", "Inactive");

        List<Section> jailSections = SectionRepo.getJailSections();

        for (Section section : jailSections){
            inRecoSectionId.getItems().add(section.getSectionId());
        }
    }

    private void setToolTip() {
        Tooltip.install(inmateBtn, new Tooltip("Inmate Management"));
        Tooltip.install(officerBtn, new Tooltip("Officer Management"));
        Tooltip.install(dashBoardBtn, new Tooltip("DashBoard"));
        Tooltip.install(settingBtn, new Tooltip("Setting"));
        Tooltip.install(manyBtn, new Tooltip("Financial Management"));
        Tooltip.install(sectionBtn, new Tooltip("Section Management"));
        Tooltip.install(visitorBtn, new Tooltip("Visitor Management"));
    }

    @FXML
    public void submitBtn(ActionEvent actionEvent) throws IOException, SQLException {

        checkEmptyFields();

        if (checkEmptyFields()) {
            System.out.println("All fields are filled");

            if (checkValidateInmateId()){}else {return;}

            if (checkInmateId()){}else {return;}

            if(checkSectionId()){}else {return;}

            if(imageDate.length != 0){}else {
                showAlert = new ShowAlert("Error", "Not Found Image", "Please Capture Image.", Alert.AlertType.INFORMATION);

            }

            Inmate inmate = new Inmate(inmateId.getText(), inmateFName.getText(), inmateLName.getText(), inmateDOB.getValue(),inmateNIC.getText(), inmateGender.getSelectionModel().getSelectedItem(), inmateAddress.getText(), inmateStatus.getSelectionModel().getSelectedItem(),imageDate);
            InmateRecord inmateRecord = new InmateRecord( inmateId.getText(),inRecoSectionId.getSelectionModel().getSelectedItem() , Date.valueOf(LocalDate.now()), Date.valueOf(inRecoReleseDate.getValue()),inRecoCrime.getText() ,caseStatusComboBox.getSelectionModel().getSelectedItem());

            SetFirstInmateRecord firstInmateRecord =new SetFirstInmateRecord(inmate,inmateRecord);

            if (SetFirstInmateRecordRepo.setFirstInmateRecord(firstInmateRecord)) {
                System.out.println("Inmate record added successfully");
                showAlert = new ShowAlert("Success", "Record Added", "Record added successfully", Alert.AlertType.INFORMATION);
                clearFields();
            }
            else {
                System.out.println("Inmate record not added successfully");
                showAlert = new ShowAlert("Error", "Record Not Added", "Record not added successfully", Alert.AlertType.INFORMATION);
                clearFields();
            }
        }
        else {
            System.out.println("Please fill all the fields");
            showAlert = new ShowAlert("Error", "Empty Fields", "Please fill all the fields", Alert.AlertType.INFORMATION);
            clearFields();
            // Show an alert to the user to fill all the fields
        }
    }

    private boolean checkValidateInmateId() {
        String id = this.inmateId.getText().trim();

        if (id.matches("I\\d{3}")){
            return true;
        }
        ShowAlert showAlert = new ShowAlert("Error", "Invalid Inmate Id", "Invalid Inmate Id Ex : IXXX", Alert.AlertType.ERROR);
        return false;
    }

    private boolean checkSectionId() {
        List<Section> sections = new ArrayList<>();

        try {
            sections = SectionRepo.getAllSections();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (Section section : sections){
            if (section.getSectionId().equals(this.inRecoSectionId.getSelectionModel().getSelectedItem())){
                return true;
            }
        }
        ShowAlert showAlert = new ShowAlert("Error", "Section Id Not Found", "Section Id Not Found", Alert.AlertType.ERROR);
        return false;
    }

    private boolean checkInmateId() {
        List<Inmate> inmates = new ArrayList<>();

        try {
            inmates = InmateRepo.getAllInmates();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (inmates.size() == 0){
            System.out.println("inmate count 0 ");
            return true;
        }

        for (Inmate inmate : inmates){
            if (!inmate.getInmateId().equals(this.inmateId.getText())){
                return true;
            }
        }
        ShowAlert showAlert = new ShowAlert("Error", "Inmate Id Already Exist", "Inmate Id Already Exist", Alert.AlertType.WARNING);
        return false;

    }

    private Boolean checkEmptyFields() {
        if (inmateId.getText().isEmpty() || inmateNIC.getText().isEmpty() || inmateFName.getText().isEmpty() || inmateLName.getText().isEmpty() || inmateGender.getSelectionModel().getSelectedItem() == null || inmateDOB.getValue() == null || inmateAddress.getText().isEmpty() || inmateStatus.getSelectionModel().getSelectedItem() == null || inRecoSectionId.getSelectionModel().getSelectedItem() == null|| inRecoCrime.getText().isEmpty() || inRecoReleseDate.getValue() == null || caseStatusComboBox.getSelectionModel().isEmpty()) {
            System.out.println("Please fill all the fields");

            return false;
        }
        else {
            System.out.println("All fields are filled");
            return true;
        }
    }

    private void clearFields() {
        inmateId.clear();
        inmateNIC.clear();
        inmateFName.clear();
        inmateLName.clear();
        inmateGender.getSelectionModel().clearSelection();
        inmateDOB.getEditor().clear();
        inmateAddress.clear();
        inmateStatus.getSelectionModel().clearSelection();
        inRecoSectionId.getSelectionModel().clearSelection();
        inRecoCrime.clear();
        inRecoReleseDate.getEditor().clear();
        caseStatusComboBox.getSelectionModel().clearSelection();
    }




    public void canselBtn(ActionEvent actionEvent) {
        clearFields();
    }

    public void inmateSearchOnAction(ActionEvent actionEvent) throws IOException {
        inmateIdForSearch = inmateSearchId.getText();

        createStage("/View/InmateProfile.fxml");
    }

    public static String getInmateIdForSearch() {
        return inmateIdForSearch;
    }

    public void takeImageBtn(ActionEvent actionEvent) {
        try {
            captureImage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void captureImage() throws IOException {
        try {
            ProcessBuilder builder = new ProcessBuilder("python3","/home/shen/Documents/myProject/NewManazinePrison/pyCapturePhoto/app.py");
            builder.redirectErrorStream(true);
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String imagepath = null;
            String line;
            while ((line = reader.readLine()) != null) {
                imagepath = line;
            }
            int exitCode = process.waitFor();
            File file = new File(imagepath);
            this.imageDate = Util.readImage(file);

            showImage(imageDate);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void showImage(byte[] imageDate) {
        Image image = Util.showImage(imageDate);
        Alert qrCodeAlert = new Alert(Alert.AlertType.INFORMATION);
        qrCodeAlert.setTitle("Inmate Image");
        qrCodeAlert.setHeaderText("Inmate Profile Image");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(300);
        imageView.setFitHeight(300);
        qrCodeAlert.getDialogPane().setContent(imageView);
        qrCodeAlert.showAndWait();
    }
}
