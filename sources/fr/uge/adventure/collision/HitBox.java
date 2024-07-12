package fr.uge.adventure.collision;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class HitBox {
	private double wrldX; private double wrldY; 
	private double offSetX; private double offSetY; 
	private double width; private double height;
	
	public HitBox(double offSetX, double offSetY,
					double width, double height) {
		this.offSetX = offSetX;
		this.offSetY = offSetY;
		this.width = width;
		this.height = height;
	}
	
	public void update(double objWrldX, double objWrldY) {
		wrldX = objWrldX + offSetX;
		wrldY = objWrldY + offSetY;
	}
	
	public void draw(Graphics2D g2, double x, double y) {
		Rectangle rect = new Rectangle((int)(wrldX - x),(int) (wrldY - y),(int) width,(int) height);
		g2.setColor(Color.RED);
		g2.draw(rect);
	}
	
	public boolean intersect(HitBox otherHb) {
		double tw = this.width;
		double th = this.height;
		double rw = otherHb.width;
		double rh = otherHb.height;
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
            return false;
        }
        double tx = this.wrldX;
        double ty = this.wrldY;
        double rx = otherHb.wrldX;
        double ry = otherHb.wrldY;
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;
        //      overflow || intersect
        return ((rw < rx || rw > tx) &&
                (rh < ry || rh > ty) &&
                (tw < tx || tw > rx) &&
                (th < ty || th > ry));
	}
	
	public boolean intersectInDistance(HitBox otherHb, double distanceX, double distanceY) {
		double tw = this.width;
		double th = this.height;
		double rw = otherHb.width;
		double rh = otherHb.height;
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
            return false;
        }
        double tx = this.wrldX + distanceX;
        double ty = this.wrldY + distanceY;
        double rx = otherHb.wrldX;
        double ry = otherHb.wrldY;
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;
        //      overflow || intersect
        return ((rw < rx || rw > tx) &&
                (rh < ry || rh > ty) &&
                (tw < tx || tw > rx) &&
                (th < ty || th > ry));
	}

	public double wrldX() {
		return wrldX;
	}

	public void setWrldX(double wrldX) {
		this.wrldX = wrldX;
	}

	public double wrldY() {
		return wrldY;
	}

	public void setWrldY(double wrldY) {
		this.wrldY = wrldY;
	}

	public double width() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double height() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double offSetY() {
		return offSetY;
	}

	public void setOffSetY(double offSetY) {
		this.offSetY = offSetY;
	}

	public double offSetX() {
		return offSetX;
	}

	public void setOffSetX(double offSetX) {
		this.offSetX = offSetX;
	}
}
