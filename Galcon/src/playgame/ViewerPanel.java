package playgame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class ViewerPanel extends JPanel
    implements ActionListener, Runnable
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ViewerPanel(String players[], String gameData)
    {
        turn = 0.0D;
        lastFrameTime = 0L;
        turnsPerSecond = 2.5D;
        desiredFramerate = 15D;
        this.players = players;
        makeInterface();
        loadGame(gameData);
        lastFrameTime = System.currentTimeMillis();
    }

    private void loadGame(String gameData)
    {
        this.gameData = gameData;
        loadThread = new Thread(this);
        loadThread.start();
    }

    public void run()
    {
        String as[] = gameData.split("\\|");
        String as1[] = as[0].split(":");
        StringBuilder stringbuilder = new StringBuilder();
        String as3[] = as1;
        int i = as3.length;
        for(int j = 0; j < i; j++)
        {
            String s1 = as3[j];
            String as2[] = s1.split("\\,");
            StringBuilder stringbuilder1 = new StringBuilder("P");
            String as5[] = as2;
            int j1 = as5.length;
            for(int l1 = 0; l1 < j1; l1++)
            {
                String s2 = as5[l1];
                stringbuilder1.append(" ");
                stringbuilder1.append(s2);
            }

            stringbuilder.append(stringbuilder1.toString());
            stringbuilder.append("\n");
        }

        String s = stringbuilder.toString();
        String as4[] = as[1].split(":");
        games = new Game[as4.length];
        Game game = new Game(s, as4.length + 1, 1, "log.txt");
        game.Init();
        for(int k = 0; k < as4.length; k++)
        {
            Game game1 = (Game)game.clone();
            String as6[] = as4[k].split("\\,");
            for(int i2 = 0; i2 < game.NumPlanets(); i2++)
            {
                Planet planet = game1.GetPlanet(i2);
                String as8[] = as6[i2].split("\\.");
                planet.Owner(Integer.parseInt(as8[0]));
                planet.NumShips(Integer.parseInt(as8[1]));
            }

            for(int j2 = game.NumPlanets(); j2 < as6.length; j2++)
            {
                String as7[] = as6[j2].split("\\.");
                Fleet fleet = new Fleet(Integer.parseInt(as7[0]), Integer.parseInt(as7[1]), Integer.parseInt(as7[2]), Integer.parseInt(as7[3]), Integer.parseInt(as7[4]), Integer.parseInt(as7[5]));
                game1.AddFleet(fleet);
            }

            games[k] = game1;
        }

        JButton ajbutton[] = buttons;
        int i1 = ajbutton.length;
        for(int k1 = 0; k1 < i1; k1++)
        {
            JButton jbutton = ajbutton[k1];
            if(jbutton != pauseButton)
                jbutton.setEnabled(true);
        }

        lastClicked = pauseButton;
        progress.setIndeterminate(false);
        progress.setMaximum(100 * games.length);
        int l = (int)(1000D / desiredFramerate);
        timer = new Timer(l, this);
        timer.setActionCommand("animTick");
        update();
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        String s = actionevent.getActionCommand();
        if(s.equals("gotoStart"))
        {
            pauseButton.doClick();
            gotoStart();
            update();
        } else
        if(s.equals("stepBack"))
        {
            pauseButton.doClick();
            stepBack();
            update();
        } else
        if(s.equals("playBack"))
        {
            lastFrameTime = System.currentTimeMillis();
            playBack();
            updateLastClicked((JButton)(JButton)actionevent.getSource());
        } else
        if(s.equals("pause"))
        {
            pause();
            updateLastClicked((JButton)(JButton)actionevent.getSource());
        } else
        if(s.equals("playForward"))
        {
            lastFrameTime = System.currentTimeMillis();
            playForward();
            updateLastClicked((JButton)(JButton)actionevent.getSource());
        } else
        if(s.equals("stepForward"))
        {
            pauseButton.doClick();
            stepForward();
            update();
        } else
        if(s.equals("gotoEnd"))
        {
            pauseButton.doClick();
            gotoend();
            update();
        } else
        if(s.equals("animTick"))
            animate();
    }

    private void updateLastClicked(JButton jbutton)
    {
        if(lastClicked != null)
            lastClicked.setEnabled(true);
        jbutton.setEnabled(false);
        lastClicked = jbutton;
    }

    public void gotoStart()
    {
        turn = 0.0D;
    }

    public void stepBack()
    {
        turn--;
        if(turn <= 0.0D)
            turn = 0.0D;
    }

    public void playBack()
    {
        timer.start();
    }

    public void pause()
    {
        timer.stop();
    }

    public void playForward()
    {
        timer.start();
    }

    public void stepForward()
    {
        turn++;
        double d = games.length - 1;
        if(turn > d)
            turn = d;
    }

    public void gotoend()
    {
        turn = games.length - 1;
    }

    public void update()
    {
        progress.setValue((int)(turn * 100D));
        viz.prepare(games[(int)turn], turn);
        viz.repaint();
    }

    private void animate()
    {
        long l = System.currentTimeMillis();
        double d = (turnsPerSecond * (double)(l - lastFrameTime)) / 1000D;
        turn += d;
        if(turn >= (double)(games.length - 1))
        {
            pauseButton.doClick();
            turn = games.length - 1;
        }
        if(turn <= 0.0D)
        {
            pauseButton.doClick();
            turn = 0.0D;
        }
        lastFrameTime = System.currentTimeMillis();
        update();
    }

    private void makeInterface()
    {
        setSize(800, 600);
        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        viz = new VizPanel();
        viz.setSize(640, 480);
        JLabel ajlabel[] = new JLabel[players.length];
        viz.addColor(new Color(106, 74, 60));
        for(int i = 0; i < players.length; i++)
        {
            JLabel jlabel = new JLabel(players[i], new ColorBox(colors[i]), 10);
            ajlabel[i] = jlabel;
            viz.addColor(colors[i]);
        }

        sep1 = new JSeparator();
        progress = new JProgressBar();
        progress.setIndeterminate(true);
        buttons = new JButton[7];
        String as[] = {
            "gotoStart", "stepBack", "playBack", "pause", "playForward", "stepForward", "gotoEnd"
        };
        for(int j = 0; j < as.length; j++)
        {
            JButton jbutton = new JButton(new ImageIcon(getClass().getResource((new StringBuilder()).append("img/").append(as[j]).append(".png").toString())));
            if(as[j].equals("pause"))
                pauseButton = jbutton;
            jbutton.setName(as[j]);
            jbutton.addActionListener(this);
            jbutton.setActionCommand(as[j]);
            jbutton.setEnabled(false);
            jbutton.setFocusPainted(false);
            buttons[j] = jbutton;
        }

        gridbagconstraints.fill = 1;
        gridbagconstraints.anchor = 10;
        gridbagconstraints.weightx = 0.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        JPanel jpanel = new JPanel();
        jpanel.setBackground(Color.WHITE);
        jpanel.setLayout(new FlowLayout());
        for(int k = 0; k < ajlabel.length; k++)
            jpanel.add(ajlabel[k]);

        add(jpanel, gridbagconstraints);
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 1.0D;
        gridbagconstraints.gridy++;
        add(viz, gridbagconstraints);
        gridbagconstraints.fill = 2;
        gridbagconstraints.weightx = 0.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbagconstraints.gridy++;
        add(progress, gridbagconstraints);
        gridbagconstraints.gridy++;
        add(sep1, gridbagconstraints);
        JPanel jpanel1 = new JPanel();
        jpanel1.setBackground(Color.WHITE);
        jpanel1.setLayout(new GridBagLayout());
        GridBagConstraints gridbagconstraints1 = new GridBagConstraints();
        gridbagconstraints.fill = 0;
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy++;
        gridbagconstraints1.fill = 0;
        gridbagconstraints1.gridx = 0;
        gridbagconstraints1.gridy = 0;
        for(int l = 0; l < buttons.length; l++)
        {
            jpanel1.add(buttons[l], gridbagconstraints1);
            gridbagconstraints1.gridx++;
        }

        add(jpanel1, gridbagconstraints);
    }

//    private JLabel info;
    private VizPanel viz;
    private JSeparator sep1;
    private JButton buttons[];
    private JButton lastClicked;
    private JButton pauseButton;
    private JProgressBar progress;
    private Color colors[] = {
        new Color(255, 64, 64), new Color(64, 255, 64), new Color(64, 64, 255), new Color(255, 255, 64)
    };
    private Timer timer;
    private Game games[];
    private double turn;
    private long lastFrameTime;
    private double turnsPerSecond;
    private double desiredFramerate;
    private Thread loadThread;
    private String gameData;
    private String players[];
}
