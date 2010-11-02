package viewgame;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import javax.swing.JFrame;
import javax.swing.JLabel;

import playgame.ViewerPanel;

public class CLViewer extends JFrame
{
  private ViewerPanel vp;

  public static void main(String[] paramArrayOfString)
  {
    String[] arrayOfString = new String[4];
    for (int i = 1; i <= 4; i++) {
      arrayOfString[(i - 1)] = new StringBuilder().append("P").append(i).toString();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    InputStreamReader localInputStreamReader = new InputStreamReader(System.in);
    BufferedReader localBufferedReader = new BufferedReader(localInputStreamReader);
    try
    {
      String str;
      while ((str = localBufferedReader.readLine()) != null)
        localStringBuilder.append(str);
    } catch (Exception localException) {
      System.err.println("ERROR: game player failed while reading game playback string.");

      System.exit(1);
    }
    CLViewer localCLViewer = new CLViewer(arrayOfString, localStringBuilder.toString().trim());
    localCLViewer.show();
  }

  public CLViewer(String[] paramArrayOfString, String paramString)
  {
    try {
      setTitle("Planet Wars Visualizer");
      setSize(640, 480);
      setDefaultCloseOperation(3);

      this.vp = new ViewerPanel(paramArrayOfString, paramString);

      add(this.vp);
    }
    catch (Exception localException) {
      JLabel localJLabel = new JLabel(new StringBuilder().append("<HTML><STRONG>Error:  Visualizer was unable to correctly parse the game data!</STRONG><BR><BR>").append(localException.getMessage()).toString());

      add(localJLabel);
    }
  }
}