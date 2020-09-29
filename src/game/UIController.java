package game;

import java.util.Random;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UIController {
	@FXML private BorderPane borderPane;
	@FXML private HBox hboxSolution;
	@FXML private Button btnFindSolution;
	@FXML private TextField tfSolution;
	@FXML private Button btnShuffle;
	@FXML private Button btnHowToPlay;
	@FXML private HBox hboxImages;
	@FXML private ImageView imageView1;
	@FXML private ImageView imageView2;
	@FXML private ImageView imageView3;
	@FXML private ImageView imageView4;
	@FXML private VBox vboxExpression;
	@FXML private HBox hboxExpression;
	@FXML private Label lblExpression;
	@FXML private TextField tfExpression;
	@FXML private Button btnVerify;
	@FXML private Label lblStatus;
	private Random rng = new Random();
	private TwentyFourPoints game = new TwentyFourPoints();
	
	@FXML
	private void initialize() {
		btnShuffle.fire();
	}
	
	@FXML
	private void btnFindSolutionAction(ActionEvent event) {
		tfSolution.setText(game.findSolution());
		lblStatus.setText("Total Number of solutions: " + game.getNumberSolutions() + ((game.getNumberSolutions() != 0) ? ". Current solution: " + game.getCurrentSolution() : ""));
	}
	
	@FXML
	private void tfSolutionAction(ActionEvent event) {
		btnFindSolution.fire();
	}
	
	@FXML
	private void btnShuffleAction(ActionEvent event) {
		tfExpression.clear();
		tfSolution.clear();
		lblStatus.setText("Ready");
		game.generateNumbers();
		imageView1.setImage(new Image("/card/" + (game.getNumbers().get(0) + 13 * rng.nextInt(4)) + ".png"));
		imageView2.setImage(new Image("/card/" + (game.getNumbers().get(1) + 13 * rng.nextInt(4)) + ".png"));
		imageView3.setImage(new Image("/card/" + (game.getNumbers().get(2) + 13 * rng.nextInt(4)) + ".png"));
		imageView4.setImage(new Image("/card/" + (game.getNumbers().get(3) + 13 * rng.nextInt(4)) + ".png"));
	}
	
	@FXML
	private void btnHowToPlayAction(ActionEvent event) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("HowToUI.fxml"));
		Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setTitle("How to play");
		stage.show();
		stage.setResizable(false);
	}
	
	@FXML
	private void tfExpressionAction(ActionEvent event) {
		btnVerify.fire();
	}
	
	@FXML
	private void btnVerifyAction(ActionEvent event) {
		tfSolution.setText(game.verifyExpression(tfExpression.getText()));
		lblStatus.setText(tfSolution.getText());
	}
}