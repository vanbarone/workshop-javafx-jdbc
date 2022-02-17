package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;

public class DepartmentFormController implements Initializable{
	
	private Department entity;

	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtnome;
	
	@FXML 
	private Label lblErrorName;
	
	@FXML
	private Button btnSave;
	
	@FXML
	private Button btnCancel;
	
	public void setEntity(Department entity) {
		this.entity = entity;
	}

	@FXML
	public void onBtnSaveAction() {
		System.out.println("Save");
	}
	
	@FXML
	public void onBtnCancelAction() {
		System.out.println("Cancel");
	}
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializrNodes();
	}
	
	private void initializrNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtnome, 30);
	}

	public void updateFormData() {
		if (entity == null ) {
			throw new IllegalStateException("Entity was null");
		}
		
		txtId.setText(String.valueOf(entity.getId()));
		txtnome.setText(entity.getName());
	}
	
}
