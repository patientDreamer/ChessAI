package UI;

import java.awt.*;
import javax.swing.*;

public class Button {
    private int x, y, width, height;
    private Image buttonImg, buttonImgHover;
    private Rectangle collisionRect;

    public Button(int x,
                  int y,
                  int width,
                  int height,
                  String buttonImgDir,
                  String buttonImgHoverDir) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // rescale image to fit given width and height
        buttonImg = new ImageIcon(buttonImgDir).getImage();
        buttonImg = buttonImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        if (buttonImgHoverDir != null) {
            buttonImgHover = new ImageIcon(buttonImgHoverDir).getImage();
            buttonImgHover = buttonImgHover.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        }

        // make rect used for collisions
        collisionRect = new Rectangle(x, y, width, height);
    }

    // if the mouse is hovering on the button
    public boolean isHovering(Point mousePos) {
        return collisionRect.contains(mousePos);
    }

    public void drawBorder(Graphics g) {
        // draw a white border on the button
        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);
    }

    public void draw(Graphics g, Point mousePos) {
        if (isHovering(mousePos) && buttonImgHover != null) g.drawImage(buttonImgHover, x, y, null);

        else g.drawImage(buttonImg, x, y, null);
    }
}
