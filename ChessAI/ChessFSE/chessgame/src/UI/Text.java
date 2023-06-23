package UI;

import java.awt.*;
public class Text {
    private String t;
    private Font f;
    private int x, y, oldX, oldY;
    private int align;

    public final static int CENTER=0, LEFT=1;

    FontMetrics fontMetrics;

    public Text(String text, Font font, int posX, int posY, int alignment) {
        t = text;
        f = font;
        x = posX;
        y = posY;
        oldX = posX;
        oldY = posY;
        align = alignment;
    }

    public void setYPos(int newY) { y = newY; }
    public int getOriginalYPos() { return oldY; }

    public void draw(Graphics g) {
        g.setFont(f);
        fontMetrics = g.getFontMetrics();
        if (align == CENTER) {
            int centerX = x - fontMetrics.stringWidth(t)/2;
            g.drawString(t, centerX, y);
        } else if (align == LEFT) {
            g.drawString(t, x, y);
        }
    }
}
