package playgame;

import java.net.URL;
import java.util.Hashtable;
import java.util.Scanner;

import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class Viewer extends JApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Viewer() {
	}

	public void init() {
		super.init();
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				public void run() {
					firstrun();
				}

				// final Viewer this$0;
				//
				// {
				// this$0 = Viewer.this;
				// }
			});
		} catch (Exception exception) {
			System.out
					.println("Error loading interface!\nPlease contact a contest admin with this error!");
		}
	}

	private void firstrun() {
		try {
			String s = (new StringBuilder()).append(
					"http://www.ai-contest.com/game_info.php?game_id=").append(
					getParameter("game_id")).toString();
			URL url = new URL(s);
			Scanner scanner = new Scanner(url.openConnection().getInputStream());
			scanner.useDelimiter("\\Z");
			String s1 = scanner.next();
			System.err.println(s1);
			String playerNames[] = s1.split("\\n");
			Hashtable<String, String> hashtable = new Hashtable<String, String>();
			String players[] = playerNames;
			int k = players.length;
			for (int l = 0; l < k; l++) {
				String s2 = players[l];
				String as1[] = s2.split("=");
				if (as1.length > 1)
					hashtable.put(as1[0], as1[1]);
			}

			players = (new String[] { (String) hashtable.get("player_one"),
					(String) hashtable.get("player_two") });
			vp = new ViewerPanel(players, (String) hashtable
					.get("playback_string"));
			add(vp);
		} catch (Exception exception) {
			StringBuilder stringbuilder = new StringBuilder();
			StackTraceElement astacktraceelement[] = exception.getStackTrace();
			int i = astacktraceelement.length;
			for (int j = 0; j < i; j++) {
				StackTraceElement stacktraceelement = astacktraceelement[j];
				stringbuilder.append(stacktraceelement);
				stringbuilder.append("<BR>");
			}

			JLabel jlabel = new JLabel(
					(new StringBuilder())
							.append(
									"<HTML><STRONG>Error:  Visualizer was unable to correctly parse the game data!</STRONG><BR><BR>")
							.append(exception.getMessage()).append("<BR>")
							.append(
									getParameter("DEBUG") == null ? ""
											: stringbuilder.toString())
							.toString());
			add(jlabel);
		}
	}

	private ViewerPanel vp;

}
