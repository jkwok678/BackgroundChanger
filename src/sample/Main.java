package sample;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Main extends Application {

    List<File> listOfFile = new ArrayList<File>();



    @Override
    public void start(Stage primaryStage) throws Exception{


        primaryStage.setTitle("Background Changer");
        Button chooseFiles = new Button("Choose Files");
        Button start = new Button("Start");
        Label extraName = new Label("extra name: ");
        TextField textField = new TextField ();
        Button startCopy = new Button("Start with Copy");
        HBox section1 = new HBox();
        section1.getChildren().addAll(extraName, textField, startCopy);
        //ImageView current = new ImageView();
        Label newInfo = new Label();
        ProgressBar progressBar = new ProgressBar();
        final FileChooser fileChooser = new FileChooser();
        Label oldColourLabel = new Label("Old colour: ");
        ColorPicker oldColour = new ColorPicker();
        Label newColourLabel = new Label("New colour: ");
        ColorPicker newColour = new ColorPicker();
        VBox layout = new VBox(chooseFiles, start, section1, oldColourLabel, oldColour, newColourLabel, newColour, newInfo,progressBar);


        chooseFiles.setOnAction(e -> {
            //Place all chosen files in list
            listOfFile.clear();
            listOfFile =  fileChooser.showOpenMultipleDialog(primaryStage);

        });

        start.setOnAction(e -> {
            //Make a new task that another thread will work on.
            Task task = new Task<Void>() {
                @Override public Void call() {
                    //counter and the total images that needs to be processed.
                    int count = 0;
                    int end = listOfFile.size();
                    //Go through each file
                    if (listOfFile != null) {
                        for (File file : listOfFile) {
                            try {
                                Image image = new Image(file.toURI().toString());
                                //current.setImage(image);
                                //newInfo.setText(" Now adding transparent background to " + file);
                                Thread.sleep(500);
                                Process(file, oldColour.getValue(), newColour.getValue());
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

        startCopy.setOnAction(e -> {
            Task task = new Task<Void>() {
                @Override public Void call() {
                    //counter and the total images that needs to be processed.
                    int count = 0;
                    int end = listOfFile.size();
                    //Go through each file
                    if (listOfFile != null) {
                        for (File file : listOfFile) {
                            try {
                                File newCopyImage = null;
                                try {
                                    String pureName = file.getName().substring(0, file.getName().length() - 4);
                                    newCopyImage = new File(pureName+ textField.getText() +".png");
                                    if (newCopyImage.createNewFile()) {
                                        System.out.println("File created: " + newCopyImage.getName());
                                    } else {
                                        System.out.println("File already exists.");
                                    }
                                } catch (IOException e) {
                                    System.out.println("An error occurred.");
                                    e.printStackTrace();
                                }
                                Image image = new Image(newCopyImage.toURI().toString());
                                //current.setImage(image);
                                //newInfo.setText(" Now adding transparent background to " + file);
                                Thread.sleep(500);
                                ProcessCopy(file, newCopyImage, oldColour.getValue(), newColour.getValue());
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
    public void Process(File file,Color oldColour , Color newColour) throws IOException, InterruptedException {

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
                            //System.out.println("Background removing!");
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

    /**
     * The method to actually change the background of an image, currently changes a white background to transparent.
     * This one applies to the copy.
     * @param oldFile
     * @throws IOException
     * @throws InterruptedException
     */
    public void ProcessCopy(File oldFile, File newFile,Color oldColour , Color newColour) throws IOException, InterruptedException {

        if (oldFile.getAbsolutePath().endsWith(".png")) {
            String fileURI = oldFile.toURI().toString();
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
                        //System.out.println("Background removing!");
                    } else {
                        pixelWriter.setColor(x, y, currentColor);
                    }

                }
            }
            //Write file to the folder with same name and format
            ImageIO.write(SwingFXUtils.fromFXImage(newImage, null), "png", newFile);
            System.out.println(newFile + " now has a transparent background!");

        }

    }

}

