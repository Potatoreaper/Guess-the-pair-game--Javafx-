package project.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import project.Main;

public class MainViewController {
	private static int mins = 0, secs = 0, millis = 0;
	private Timeline timeline;
	private boolean isTimerOn = false;
	private ArrayList<Image> imageArr;
	private Image matchImg = null;
	int pair = 0;
	private Button prevBtn;
	
	@FXML
	private Label timerLabel, movesLabel, pairsLabel; 
	@FXML
	private BorderPane mainPane;
	
	@FXML
	private void exit() {
		Platform.exit();
	}
	
	@FXML
	private void showHelp() {
		Alert info = new Alert(AlertType.INFORMATION,
				"The goal of this game is to find\n"
				+ "all the matching pairs with the\n"
				+ "least amount of moves!\n"
				+ "Start by clicking any two buttons.");
		info.setHeaderText("Help");
		info.setTitle("Guess the Pairs");
		info.showAndWait();
		
	}
	
	@FXML
	private void reset() throws IOException {
		mins = 0;
    	secs = 0;
    	millis = 0;
    	timeline.pause();
    	timerLabel.setText("00:00");
    	isTimerOn = false;//set the boolean value to false
    	movesLabel.setText("0");
    	Main.showGameScene();

	}
	
	@FXML
	private void initialize() {
		//handles the image pairing & shuffling
		imageArr = new ArrayList<>(16);
		
		for (int i=1 ; i<=8 ; i++) {
			String path = "project/view/image/"+i+".png";
			Image img = new Image(path);
			//adding in pairs
			imageArr.add(img);
			imageArr.add(img);
		}
		
		Collections.shuffle(imageArr,new Random(System.currentTimeMillis()));//randomizing the images
	}
	
	@FXML
	private void flipButton(ActionEvent event){

		if (!isTimerOn) {
			this.startTimer();
			isTimerOn = true;
		}
		movesLabel.setText(Integer.toString(Integer.valueOf(movesLabel.getText())+1)); //incrementing the moves each time a button is clicked
		
		((Button)event.getTarget()).setPadding(Insets.EMPTY);
		String btnId = ((Button)event.getTarget()).getId();
		int btnNumb  = Integer.valueOf(btnId.replaceAll("\\D+",""));
		((Button)event.getTarget()).setGraphic(new ImageView(imageArr.get(btnNumb-1))); //image binding
		((Button)event.getTarget()).setMouseTransparent(true);//to prevent multiple clicks on the same button
		pair++;
		
		if (pair == 1) {
			prevBtn = ((Button)event.getSource());
			matchImg = imageArr.get(btnNumb-1);
		}
		
		if ((matchImg == imageArr.get(btnNumb-1)) && (pair == 2)) { //FOUND!! keep the images shown
			pairsLabel.setText(Integer.toString(Integer.valueOf(pairsLabel.getText())+1)); //incrementing the number of pairs found (if found)
			pair = 0;
			prevBtn = null;

		} else if ((matchImg != imageArr.get(btnNumb-1)) && (pair != 1) && (pair != 0)){ //NOT FOUND
			
			new Thread(() -> {
		        try {
		        	mainPane.setMouseTransparent(true);
		            Thread.sleep(1000);
		            Task<Void> sleeper = new Task<Void>() {
		                @Override
		                public Void call() throws Exception {
		                	((Button)event.getSource()).setGraphic(null);
		        			prevBtn.setGraphic(null);
		        			prevBtn.setMouseTransparent(false);
		        			((Button)event.getSource()).setMouseTransparent(false);
		        			mainPane.setMouseTransparent(false);
		                    return null;
		                }
		            };
		            Platform.runLater(sleeper);
		            
		        } catch (InterruptedException ex) {
		        	System.out.print(ex);
		        }
		    }).start();
		}
		if (pair == 2) { //RESETTING
			pair = 0;
			//prevBtn = null;
		}
		
		//winning case
		if (Integer.valueOf(pairsLabel.getText()) == 8) {
			int seconds, minutes;
			
			minutes = Integer.valueOf((timerLabel.getText()).substring(0, timerLabel.getText().indexOf(':')));
			seconds = Integer.valueOf((timerLabel.getText()).substring(timerLabel.getText().indexOf(':')+1));
			timeline.pause();
			Alert info = new Alert(AlertType.INFORMATION,
					"You've found all the matching pairs\n"
					+ "in "+minutes+(minutes == 1 ? " minute":" minutes")
					+" and "+seconds+(seconds == 1? " second":" seconds")
					+ " with "+movesLabel.getText()+" moves\n"
					+ "Congratulations!\n"
					+ "Press reset to play again.");
			info.setHeaderText("You Win!");
			info.setTitle("Guess the Pairs");
			info.showAndWait();
		}
	}
	
	private void startTimer() { 
		timeline = new Timeline(new KeyFrame(Duration.millis(1), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
            	change(timerLabel);
			}
		}));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.setAutoReverse(false);
		timeline.play();
	}
	
	//handles the increments of seconds/minutes: 
	private static void change(Label timer) {
		if (timer.getText().equals("99:59")) return;
		
		if(millis == 1000) {
			secs++;
			millis = 0;
		}
		if(secs == 60) {
			mins++;
			secs = 0;
		}
		
		timer.setText((((mins/10) == 0) ? "0" : "") + mins + ":"
		 + (((secs/10) == 0) ? "0" : "") + secs);
		millis++;
	}
}
