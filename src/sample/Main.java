package sample;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{


        primaryStage.setTitle("Background Changer");
        Button chooseFiles = new Button("Choose Files");
        //ImageView current = new ImageView();
        Label newInfo = new Label();
        ProgressBar progressBar = new ProgressBar();
        final FileChooser fileChooser = new FileChooser();
        Label oldColourLabel = new Label("Old colour: ");
        ColorPicker oldColour = new ColorPicker();
        Label newColourLabel = new Label("New colour: ");
        ColorPicker newColour = new ColorPicker();
        VBox layout = new VBox(chooseFiles, oldColourLabel, oldColour, newColourLabel, newColour, newInfo,progressBar);

        chooseFiles.setOnAction(e -> {
            //Place all chosen files in list
            List<File> list =fileChooser.showOpenMultipleDialog(primaryStage);
            //Make a new task that another thread will work on.
            Task task = new Task<Void>() {
                @Override public Void call() {
                    //counter and the total images that needs to be processed.
                    int count = 0;
                    int end = list.size();
                    //Go through each file
                    if (list != null) {
                        for (File file : list) {
                            try {
                                Image image = new Image(file.toURI().toString());
                                //current.setImage(image);
                                //newInfo.setText(" Now adding transparent background to " + file);
                                Thread.sleep(500);
                                process(file, oldColour.getValue(), newColour.getValue());
                                count++;
                                //Update the Progress bar
                                updateProgress(count, end);
                            } catch (IOException | InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }

                    }

                    return null ;
                }

            };
            //Connect the progressBar to the task.
            progressBar.progressProperty().bind(task.progressProperty());
            new Thread(task).start();


        });

        Scene scene = new Scene(layout,360,360);
        primaryStage.setScene(scene);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The method to actually change the background of an image, currently changes a white background to transparent.
     * @param file
     * @throws IOException
     * @throws InterruptedException
     */
    public void process(File file,Color oldColour , Color newColour) throws IOException, InterruptedException {

        if (file.getAbsolutePath().endsWith(".png")) {
                String fileURI = file.toURI().toString();
                Image image = new Image(fileURI);

                double height = image.getHeight();
                double width = image.getWidth();

                WritableImage newImage = new WritableImage((int) width, (int) height);
                PixelWriter pixelWriter = newImage.getPixelWriter();

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        Color currentColor = image.getPixelReader().getColor(x, y);
                        //if the colour equals white#, make it transparent, otherwise just leave it.
                        if (currentColor.equals(oldColour)) {
                            pixelWriter.setColor(x, y, newColour);
                            System.out.println("Background removing!");
                        } else {
                            pixelWriter.setColor(x, y, currentColor);

                        }


                    }
                }
                //Write file to the folder with same name and format
                ImageIO.write(SwingFXUtils.fromFXImage(newImage, null), "png", file);
                System.out.println(file + " now has a transparent background!");

            }

    }
}

