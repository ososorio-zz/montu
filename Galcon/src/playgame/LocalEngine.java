package playgame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.JButton;

import bots.BullyBot;
import bots.Test;

public class LocalEngine extends JApplet{

	private VizPanel viz;
	private Color colors[] = {
			new Color(255, 64, 64), new Color(64, 255, 64), new Color(64, 64, 255), new Color(255, 255, 64)
	};
	private static final long serialVersionUID = 1L;

	
	public void init() 
	{
		super.init();
		try {
			firstrun();
		} catch (Exception exception) {
			exception.printStackTrace();
			System.out
			.println("Error loading interface!\nPlease contact a contest admin with this error!");
		}
	}

	public  void firstrun() {

		Container c=getContentPane();
		c.setLayout(new BorderLayout());
		int playerslenght=2;
		
		viz = new VizPanel();
		viz.setSize(640, 480);
		viz.addColor(new Color(106, 74, 60));

		for(int i = 0; i < playerslenght; i++)
		{
			viz.addColor(colors[i]);
		}
		c.add(BorderLayout.CENTER,viz);



		String mapFileName = "C:/java_starter_package/maps/map1.txt";
		int maxNumTurns = 300;
		String logFileName = "C:/java_starter_package/logs/logFile.txt";
		final Game game = new Game(mapFileName, maxNumTurns, 0, logFileName);
		final PlanetWars war = new PlanetWars(game);
		//optimize(war);
		final List<PlayerBot> players = new ArrayList<PlayerBot>();
		players.add(new Test());
		players.add(new BullyBot());

		JButton jbutton = new JButton("Siguiente");
		jbutton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (game.Winner() < 0  ) {

					int i = 1;
					for (Iterator<PlayerBot> iterator = players.iterator(); iterator
					.hasNext(); i++) {
						PlayerBot playerBot = (PlayerBot) iterator.next();
						try {
							war.setPlayer(i);
							playerBot.DoTurn(war);
							viz.repaint();
						} catch (Throwable e) {
							e.printStackTrace();
							break;
						}
					}
					game.DoTimeStep();
				}


			}
		});
		c.add(BorderLayout.SOUTH,jbutton);
		if (game.Init() == 0)
			System.err.println((new StringBuilder()).append(
					"ERROR: failed to start game. map: ").append(mapFileName)
					.toString());

		viz.prepare(game, 0.0d);
		//haga una clase que vca a hacer le jugador que utilize el Conkeroptimizer y diga la primera jugada
	}

   /*	private static void optimize(PlanetWars war) {
		long time = System.currentTimeMillis();
		ConquerOptimizer opt = new ConquerOptimizer(war);
		opt.optimize();
		System.out.println("Time:"+(System.currentTimeMillis()-time));
		System.out.println("Done");
	}*/


}
