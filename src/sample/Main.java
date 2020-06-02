package sample;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
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

        primaryStage.setTitle("Hello World");
        Button chooseFiles = new Button("Choose Files");

        final FileChooser fileChooser = new FileChooser();
        VBox layout = new VBox(chooseFiles);
        chooseFiles.setOnAction(e -> {
            List<File> list =fileChooser.showOpenMultipleDialog(primaryStage);
            if (list != null) {
                for (File file : list) {
                    try {
                        process(file);
                    } catch (IOException | InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        Scene scene = new Scene(layout);


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
    public void process(File file) throws IOException, InterruptedException {

        if (file.getAbsolutePath().endsWith(".png")) {
                String fileURI = file.toURI().toString();
                Image image = new Image(fileURI);
                Thread.sleep(500);
                double height = image.getHeight();
                double width = image.getWidth();

                WritableImage newImage = new WritableImage((int) width, (int) height);
                PixelWriter pixelWriter = newImage.getPixelWriter();


                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        Color currentColor = image.getPixelReader().getColor(x, y);
                        if (currentColor.equals(Color.WHITE)) {
                            pixelWriter.setColor(x, y, Color.color(0, 0, 0, 0.0));
                            System.out.println("Background removing!");
                        } else {
                            pixelWriter.setColor(x, y, currentColor);

                        }


                    }
                }
                ImageIO.write(SwingFXUtils.fromFXImage(newImage, null), "png", file);
                System.out.println(file + " now has a transparent background!");

            }
    }
}

