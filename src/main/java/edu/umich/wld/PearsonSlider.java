/*************************************************************************
 * Copyright 2012 Regents of the University of Michigan 
 * 
 * NCIBI - The National Center for Integrative Biomedical Informatics (NCIBI)
 *         http://www.ncib.org.
 * 
 * This product may includes software developed by others; in that case see specific notes in the code. 
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or (at your option) any later version, along with the following terms:
 * 1.	You may convey a work based on this program in accordance with section 5, 
 *      provided that you retain the above notices.
 * 2.	You may convey verbatim copies of this program code as you receive it, 
 *      in any medium, provided that you retain the above notices.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU General Public License for more details, http://www.gnu.org/licenses/.
 * 
 * This work was supported in part by National Institutes of Health Grant #U54DA021519
 *
 ******************************************************************/
package edu.umich.wld;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.NumberFormat;

import javax.swing.JSlider;

@SuppressWarnings("serial")
public class PearsonSlider extends JSlider implements MouseListener, MouseMotionListener, MinMaxChangeListener {

	private static enum DragMode {
		DRAG_NOTHING, DRAG_START, DRAG_END
	}
	
	private static final Color COLOR_SMALL_GRID = Color.DARK_GRAY;
	private static final Color COLOR_LARGE_GRID = Color.BLACK;
	private static final Color COLOR_MARKERS = new Color(0x6666FF); 
	private static final Color COLOR_DRAG = new Color(0xFF6666);
	
	private static NumberFormat defaultFormat = NumberFormat.getNumberInstance();
	
	private final static int DEFAULT_DIGITS = 2;
	private static final Double EPSILON = 1e-6;
	private static final Integer PADDING = 6;
	
	private static final Double LOWER_LIMIT = 0.0;
	private static final Double UPPER_LIMIT = 1.0;

	private Dimension minDim = new Dimension(420, 40);

	private double grid = 0.1;
	private DragMode currentMode = DragMode.DRAG_NOTHING;

	private TickMark tm = new TickMark();

    private int pressed, min, max, dragMin, dragMax;

    private Double currentMinValue, minValue, currentMaxValue, maxValue;
    
    private MinMaxValueModel rangeModel;

    public PearsonSlider(Double extent) {
    	super(0, (int) Math.round(extent), 0);
    	defaultFormat.setMinimumFractionDigits(DEFAULT_DIGITS);
		defaultFormat.setMaximumFractionDigits(DEFAULT_DIGITS);
        addMouseListener(this);
        addMouseMotionListener(this);
        rangeModel = new MinMaxValueModel(getMinMinValue(), getMaxMaxValue());
        rangeModel.addMinMaxListener(this);
	    initView();
     }
    
    public void initView() {
    	currentMinValue = rangeModel.getMinValue();
    	minValue = rangeModel.getMinMinValue();
    	currentMaxValue = rangeModel.getMaxValue();
    	maxValue = rangeModel.getMaxMaxValue();
    	updateGraphics();
    }
    
    private Double getMinMinValue() {
    	return LOWER_LIMIT;
    }
    
    private Double getMaxMaxValue() {
    	return UPPER_LIMIT;
    }
    
	public void valuesChanged(MinMaxValueModel t) {
		currentMinValue = t.getMinValue();
    	currentMaxValue = t.getMaxValue();
    	
		Integer count = AnalysisDialog.getFilteredCountFromValues(currentMinValue, currentMaxValue);
		AnalysisDialog.setFilteredCount(count);
		AnalysisDialog.getCurrentFileData().setNMetabolites(AnalysisDialog.getPearsonMaxValues().length);
		AnalysisDialog.resetCountText();
    	AnalysisDialog.getLimits().getBoxPair()[PearsonTextFieldPair.LEFT_FIELD].
    		setText(getDefaultFormat().format(currentMinValue));
    	AnalysisDialog.getLimits().getBoxPair()[PearsonTextFieldPair.RIGHT_FIELD].
		setText(getDefaultFormat().format(currentMaxValue));
 
    	updateGraphics();
	}
	
    public double snapToGrid(double pos) {
        return grid * Math.round(pos / grid);
    }

    private void paintUnderTriangleMarker(Graphics gc, int x) {
        gc.drawLine(x, 12, x, 12);
        gc.drawLine(x - 1, 13, x + 1, 13);
        gc.drawLine(x - 2, 14, x + 2, 14);
        gc.drawLine(x - 3, 15, x + 3, 15);
        gc.drawLine(x - 4, 16, x + 4, 16);
        gc.drawLine(x - 5, 17, x + 5, 17);
        gc.drawLine(x - 6, 18, x + 6, 18);
    }
        
    private void paintGrid(Graphics gc, double minPos, double maxPos, Color c, double g, int y1, int y2, Integer modulus) {
    	double x1 = g * Math.ceil(minPos / g - EPSILON);
    	double x2 = g * Math.floor(maxPos / g + EPSILON);
    	int count = 0;
    	for (double i = x1; i < x2 + EPSILON; i += g) {
    		if (modulus != null && (count++ % modulus == 0)) {
    			// avoid minor ticks too close to major ticks
    			continue;
    		}
    		double t = (i - minPos) / (maxPos - minPos);
    		int x = (int) Math.round(min + t * (max - min));
    		gc.drawLine(x, y1, x, y2);
    	}
    }

    @Override
    protected void paintComponent(Graphics gc) {
        gc.setColor(getBackground());
        gc.fillRect(0, 0, getSize().width, getSize().height);
        paintSurface(gc);
    }

    private void updateGraphics() {
        revalidate();
        repaint();
    }

    private void paintSurface(Graphics gc) {
        Dimension extent = getSize();
        gc.setColor(this.isEnabled() ? Color.black : Color.lightGray);
        
        min = PADDING;
        max = extent.width - PADDING;

        double minPos = minValue;
        double maxPos = maxValue;
        double currentMinPos = currentMinValue;
        double currentMaxPos = currentMaxValue;
        
        tm.set(minPos, maxPos, 6);
        grid = 0.1 * tm.getStep();
        paintGrid(gc, minPos, maxPos, this.isEnabled() ? COLOR_SMALL_GRID : Color.lightGray, grid, 6, 11, 10);
	    paintGrid(gc, minPos, maxPos, this.isEnabled() ? COLOR_LARGE_GRID : Color.lightGray, 5.0 * grid, 4, 13, null);
        
        // paint main bar
        gc.setColor(this.isEnabled() ? Color.darkGray : Color.lightGray);
        gc.drawLine(min, 8, max, 8);
        gc.drawLine(min, 9, max, 9);
        
        // paint labels
        String str = "0";
    	Double strWidth = gc.getFontMetrics().getStringBounds(str, gc).getWidth();
    	gc.drawString(str, (int) (0.5 + min - 0.5 * strWidth), 33);
    	for (int i = 1; i < 10; i++) {
    		paintlabel(gc, i);
    	}
    	str = "1";
    	strWidth = gc.getFontMetrics().getStringBounds(str, gc).getWidth();
    	gc.drawString(str, (int) (0.5 + max - 0.5 * strWidth), 33);

    	// paint start marker
        gc.setColor(this.isEnabled() ? (currentMode == DragMode.DRAG_START ? COLOR_DRAG : COLOR_MARKERS) : Color.lightGray);
        double t = (currentMinPos - minPos) / (maxPos - minPos);
        int x = (int) Math.round(min + t * (max - min));
        paintUnderTriangleMarker(gc, x);

        // paint end  marker
        gc.setColor(this.isEnabled() ? (currentMode == DragMode.DRAG_END ? COLOR_DRAG : COLOR_MARKERS) : Color.lightGray);
        t = (currentMaxPos - minPos) / (maxPos - minPos);
        x = (int) Math.round(min + t * (max - min));
        paintUnderTriangleMarker(gc, x);
    }
    
    private void paintlabel(Graphics gc, int i) {
    	String str = "0." + i;
    	Double strWidth = gc.getFontMetrics().getStringBounds(str, gc).getWidth();
    	gc.drawString(str, (int) (0.5 + min + (i / 10.0) * (max - min) - 0.5 * strWidth), 33);
    }

    public void mouseClicked(MouseEvent me) {}
    public void mouseMoved(MouseEvent me) {}
    public void mouseExited(MouseEvent me) {}
    public void mouseEntered(MouseEvent me) {}

    public void mousePressed(MouseEvent me) {
    	if (!this.isEnabled()) {
    		return;
    	}
        if ((me.getModifiers() & InputEvent.BUTTON2_MASK) == 0
        		&& (me.getModifiers() & InputEvent.BUTTON3_MASK) == 0) {
            pressed = me.getX();
        }
    }

    public void mouseReleased(MouseEvent me) {
    	if (!this.isEnabled()) {
    		return;
    	}
        if ((me.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
            if (currentMode != DragMode.DRAG_NOTHING) {
                currentMode = DragMode.DRAG_NOTHING;
                // the value does not change
                updateGraphics();
            }
        }
    }

    public void mouseDragged(MouseEvent me) {
    	if (!this.isEnabled()) {
    		return;
    	}
        Dimension extent = getSize();
        if ((me.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
            Insets border = getInsets();
            min = border.left + PADDING;
            max = extent.width - 1 - border.right - PADDING;
            
            double minPos = minValue;
            double maxPos = maxValue;
            double currentMinPos = currentMinValue;
            double currentMaxPos = currentMaxValue;

            // check if a marker should be dragged
            if (currentMode == DragMode.DRAG_NOTHING) {
                double t = (currentMinPos - minPos) / (maxPos - minPos);
                int xStart = (int)Math.round(min + t * (max - min));

                t = (currentMaxPos - minPos) / (maxPos - minPos);
                int xEnd = (int)Math.round(min + t * (max - min));
                
                if (Math.abs(xStart - pressed) < 4) {
                    currentMode = DragMode.DRAG_START;
                    dragMin = min;
                    dragMax = xEnd - 6;
                }
                
                if (Math.abs(xEnd - pressed) < 4) {
                    currentMode = DragMode.DRAG_END;
                    dragMin = xStart + 6;
                    dragMax = max;
                }
            }

            // do the actual dragging
            if (currentMode != DragMode.DRAG_NOTHING) {
                double x = me.getX();
                if (x < dragMin)
                    x = dragMin;
                if (x > dragMax)
                    x = dragMax;

                double t = minPos + (x - min) / (max - min) * (maxPos - minPos);
                if (me.isControlDown())
                    t = snapToGrid(t);

                if (currentMode == DragMode.DRAG_START) {
                    rangeModel.setMinValue(t);
                }
                if (currentMode == DragMode.DRAG_END) {
                    rangeModel.setMaxValue(t);
                }
            }
        }
    }
    
    public static NumberFormat getDefaultFormat() {
    	return defaultFormat;
    }
    
    public Dimension getPreferredSize() {
        return minDim;
    }
    
    public Dimension getMinimumSize() {
        return minDim;
    }
    
    public Dimension getMaximumSize() {
        return minDim;
    }
    
    public MinMaxValueModel getRangeModel() {
    	return rangeModel;
    }
}
