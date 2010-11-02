package playgame;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

public class VizPanel extends JPanel
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public void addColor(Color color)
    {
        colors.add(color);
    }

    private void initRenderer()
    {
        try
        {
            bgImage = ImageIO.read(getClass().getResource("/space.jpg"));
        }
        catch(IOException ioexception)
        {
            bgImage = null;
        }
        _ginit();
        renderInit = true;
    }

    private void _ginit()
    {
        if(img != null)
        {
            _g.dispose();
            img.flush();
        }
        GraphicsConfiguration graphicsconfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        img = graphicsconfiguration.createCompatibleImage(getWidth(), getHeight());
        _g = img.createGraphics();
        _g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        _g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        _g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    public VizPanel()
    {
        frameCache = 0.0D;
        colors = new ArrayList<Color>();
        renderInit = false;
        oldW = 0;
        oldH = 0;
        try
        {
            ImageIcon imageicon = new ImageIcon("img/loader.gif");
            loader = new JLabel(imageicon);
            imageicon.setImageObserver(loader);
            setLayout(new BorderLayout());
            add(loader, "Center");
        }
        catch(Exception exception)
        {
            loader = null;
        }
    }

    public void prepare(Game game, double d)
    {
        if(loader != null)
        {
            initRenderer();
            remove(loader);
            loader = null;
        }
        gameCache = game;
        frameCache = Math.IEEEremainder(d, 1.0D);
    }

    public void paint(Graphics g)
    {
        int i = getWidth();
        int j = getHeight();
        if(!renderInit || gameCache == null)
        {
            if(loader != null)
                loader.paint(g);
        } else
        {
            if(i != oldW || j != oldH)
            {
                _ginit();
                oldW = i;
                oldH = j;
            }
            gameCache.Render(i, j, frameCache, bgImage, colors, _g);
            g.drawImage(img, 0, 0, null);
        }
    }

    private Game gameCache;
    private double frameCache;
    private JLabel loader;
    private BufferedImage img;
    private Graphics2D _g;
    private ArrayList<Color> colors;
    private boolean renderInit;
    private BufferedImage bgImage;
    private int oldW;
    private int oldH;
}
