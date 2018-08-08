package project.view;

import java.io.IOException;

import javafx.fxml.FXML;
import project.Main;

public class StartViewController {
	
	@FXML
	private void startGame() throws IOException {
		Main.showGameScene();
	}
	
}
