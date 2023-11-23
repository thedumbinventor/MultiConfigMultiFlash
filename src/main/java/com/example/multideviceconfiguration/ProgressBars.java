package com.example.multideviceconfiguration;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProgressBars extends Application {
    private ProgressBar progressBar;
    private Button startButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Progress Bar Example");

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);

        startButton = new Button("Start");
        startButton.setOnAction(e -> simulateProgress());

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(progressBar, startButton);
        vbox.setPadding(new javafx.geometry.Insets(10));

        Scene scene = new Scene(vbox, 320, 150);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void simulateProgress() {
        // Simulate progress updates on a separate thread
        new Thread(() -> {
            for (double progress = 0; progress <= 1.0; progress += 0.01) {
                final double finalProgress = progress;
                Platform.runLater(() -> progressBar.setProgress(finalProgress));

                try {
                    Thread.sleep(50); // Simulate some time-consuming task
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
