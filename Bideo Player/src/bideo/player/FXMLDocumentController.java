package bideo.player;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class FXMLDocumentController implements Initializable {
    
    
    
    
   private MediaPlayer mediaPlayer; 
   private String filePath;
   Duration duration;
   
    @FXML
    private Label label;
   
    @FXML
    private Slider seek;
   
    @FXML
    private Slider volume;
   
    @FXML
    private MediaView mediaView;
    
    @FXML
    private void handleButtonAction(ActionEvent event) throws InterruptedException {
       
        
        
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Select File (*.mp4)", "*.mp4");
            fileChooser.getExtensionFilters().add(filter);
            File file = fileChooser.showOpenDialog(null);
            filePath = file.toURI().toString();
        
            if(filePath!=null)
            {
                Media media = new Media(filePath);
                mediaPlayer = new MediaPlayer(media);
                
                duration = mediaPlayer.getMedia().getDuration();
                
                mediaView.setMediaPlayer(mediaPlayer);
                    
                    DoubleProperty width = mediaView.fitWidthProperty();
                    DoubleProperty height = mediaView.fitHeightProperty();
                    
                    width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
                    height.bind(Bindings.selectDouble(mediaView.sceneProperty(),"height"));
                    
                    volume.setValue(mediaPlayer.getVolume() * 100);
                    volume.valueProperty().addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(Observable observable) {
                       mediaPlayer.setVolume(volume.getValue() / 100);
                    }
                }); 
                    
                    
                    
                    /*mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                    @Override
                    public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                        seek.setValue(newValue.toSeconds());
                    }
                });
                    
                    seek.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        mediaPlayer.seek(Duration.seconds(seek.getValue()));
                    }
                });*/
                    
                    mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(Observable observable) {
                        updateValues();
                    }
                });
                    
                    seek.valueProperty().addListener(new InvalidationListener() {
                    public void invalidated(Observable ov) {
                    if (seek.isValueChanging() || seek.isPressed()) {
                            // multiply duration by percentage calculated by slider position
                               mediaPlayer.seek(duration.multiply(seek.getValue() / 100.0));
                            }
                         }
                     });          
                    
                mediaPlayer.setOnReady(new Runnable() {

                @Override
                public void run() 
                        {
                            //label.setText("Duration: "+media.getDuration().toSeconds());
                            duration = media.getDuration();
                        }
                        
                    });
                mediaPlayer.play();
            }
    }
    
    @FXML
    private void playVideo(ActionEvent event)
    {
        mediaPlayer.play();
        mediaPlayer.setRate(1);
    }
    
    @FXML
    private void pauseVideo(ActionEvent event)
    {
        mediaPlayer.pause();
    }
    
    @FXML
    private void stopVideo(ActionEvent event)
    {
        mediaPlayer.stop();
    }
    
    @FXML
    private void fastVideo(ActionEvent event)
    {
        mediaPlayer.setRate(2);
    }
    
    @FXML
    private void endVideo(ActionEvent event)
    {
        mediaPlayer.seek(mediaPlayer.getTotalDuration());
        updateValues();
    }
    
    @FXML
    private void slowVideo(ActionEvent event)
    {
        mediaPlayer.setRate(0.5);
    }
    
    @FXML
    private void startVideo(ActionEvent event)
    {
        mediaPlayer.seek(mediaPlayer.getStartTime());
        updateValues();
    }
    
    @FXML
    private void exitVideo(ActionEvent event)
    {
         System.exit(0);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    protected void updateValues()
                    {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Duration currentTime = mediaPlayer.getCurrentTime();
                                label.setText(formatTime(currentTime,duration));
                                seek.setDisable(duration.isUnknown());
                                if(!seek.isDisabled()&&duration.greaterThan(Duration.ZERO)&&!seek.isValueChanging())
                                {
                                    seek.setValue(currentTime.divide(duration).toMillis()*100);
                                }
                            }
                        });
                    }
    private static String formatTime(Duration elapsed, Duration duration)
    {
        int intElapsed = (int)Math.floor(elapsed.toSeconds());
   int elapsedHours = intElapsed / (60 * 60);
   if (elapsedHours > 0) {
       intElapsed -= elapsedHours * 60 * 60;
   }
   int elapsedMinutes = intElapsed / 60;
   int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 
                           - elapsedMinutes * 60;
 
   if (duration.greaterThan(Duration.ZERO)) {
      int intDuration = (int)Math.floor(duration.toSeconds());
      int durationHours = intDuration / (60 * 60);
      if (durationHours > 0) {
         intDuration -= durationHours * 60 * 60;
      }
      int durationMinutes = intDuration / 60;
      int durationSeconds = intDuration - durationHours * 60 * 60 - 
          durationMinutes * 60;
      if (durationHours > 0) {
         return String.format("%d:%02d:%02d/%d:%02d:%02d", 
            elapsedHours, elapsedMinutes, elapsedSeconds,
            durationHours, durationMinutes, durationSeconds);
      } else {
          return String.format("%02d:%02d/%02d:%02d",
            elapsedMinutes, elapsedSeconds,durationMinutes, 
                durationSeconds);
      }
      } else {
          if (elapsedHours > 0) {
             return String.format("%d:%02d:%02d", elapsedHours, 
                    elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d",elapsedMinutes, 
                    elapsedSeconds);
            }
        }
    }
}
