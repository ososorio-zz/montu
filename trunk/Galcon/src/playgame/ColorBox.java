package playgame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;

public class ColorBox
    implements Icon
{

    public ColorBox(Color color1)
    {
        color = color1;
    }

    public int getIconWidth()
    {
        return 12;
    }

    public int getIconHeight()
    {
        return 12;
    }

    public void paintIcon(Component component, Graphics g, int i, int j)
    {
        Graphics2D graphics2d = (Graphics2D)g;
        graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        java.awt.geom.RoundRectangle2D.Float float1 = new java.awt.geom.RoundRectangle2D.Float(i, j, 12F, 12F, 4F, 4F);
        graphics2d.setColor(color);
        graphics2d.fill(float1);
    }

    private Color color;
//    private final int SIZE = 12;
//    private final int ROUND = 4;
}
