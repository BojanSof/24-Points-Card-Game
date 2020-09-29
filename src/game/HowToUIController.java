package game;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

public class HowToUIController {
	@FXML private BorderPane borderPane;
	@FXML private Label lblInstructions;
	@FXML private TableView<Card> tvCardValues;
	@FXML private TableColumn<Card, String> tcCard;
	@FXML private TableColumn<Card, String> tcValue;
	
	@FXML
	private void initialize() {
		tcCard.setCellValueFactory(new PropertyValueFactory<>("card"));
		tcValue.setCellValueFactory(new PropertyValueFactory<>("value"));
		tvCardValues.getItems().addAll(
										new Card("Ace", "1"),
										new Card("Two", "2"),
										new Card("Three", "3"),
										new Card("Four", "4"),
										new Card("Five", "5"),
										new Card("Six", "6"),
										new Card("Seven", "7"),
										new Card("Eight", "8"),
										new Card("Nine", "9"),
										new Card("Ten", "10"),
										new Card("Jack", "11"),
										new Card("Queen", "12"),
										new Card("King", "13")
									);
	}
}