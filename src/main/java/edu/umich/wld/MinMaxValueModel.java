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
/*
 * MinMaxValueHolderl.java
 * 
 * recoded/renamed from TimeLine.java
 * Created May, 2005
 * 
 * COPYRIGHT (c) 2005, THE REGENTS OF THE UNIVERSITY OF MICHIGAN,
 * ALL RIGHTS RESERVED; see the file COPYRIGHT.txt in this folder for details
 * 
 * This work is supported in part by the George E. Brown, Jr. Network
 * for Earthquake Engineering Simulation (NEES) Program of the National
 * Science Foundation under Award Numbers CMS-0117853 and CMS-0402490.
 * 
 * Used as part of the NEES project's Tivo project; Terry Weymouth and Paul Hubbard, May, 2005
 * 
 */
package edu.umich.wld;

import java.util.Vector;


/**
 * A model and container for min value, max value ordered pair, represented as double values.
 * The values are bounded by in initial, read only, minMinValue and maxMaxValue.
 * 
 * @author Terry E. Weymouth
 */
public class MinMaxValueModel {

    // values from the entire range
    private double minMinValue = Double.NaN;
    private double maxMaxValue = Double.NaN;

    // current values
    private double minValue = Double.NaN;
    private double maxValue = Double.NaN;

    public MinMaxValueModel(double minMin, double maxMax) {
    	if (minMin > maxMax) {
    		double d = minMin;
    		minMin = maxMax;
    		maxMax = d;
    	}
        setMinMinValue(minMin);
        setMaxMaxValue(maxMax);
        resetMinMax();
    }

    private Vector<MinMaxChangeListener> listeners = new Vector<MinMaxChangeListener>();

    /**
     * Register a MinMaxListener to be notified of changes in values in this model.
     * 
     * @param listener the MinMaxListener that will be notified.
     * 
     */
    public void addMinMaxListener(MinMaxChangeListener u) {
        listeners.addElement(u);
    }

    /**
     * Remove a MinMaxListener from this list of listeners. If this MinMaxListener
     * in not in the list, then this method has no effect on the list.
     * 
     * @param listener the MinMaxListener that will be removed.
     * 
     */
    public void removeMinMaxListener(MinMaxChangeListener u) {
        listeners.removeElement(u);
    }

    /**
     * Clear the list of MinMaxListener.
     *
     */
    public void removeAllMinMaxListeners() {
        listeners.removeAllElements();
    }

    private void notifyAllListeners() {
    	for (MinMaxChangeListener l: listeners){
    		l.valuesChanged(this);
    	}
	}

    /**
     * Reset current min and max value to their outside boundaries
     */
    public void resetMinMax() {
        setMinValue(minMinValue);
        setMaxValue(maxMaxValue);
    }

    public double getMinMinValue() {
		return minMinValue;
	}

	public void setMinMinValue(double minMinValue) {
		if (minMinValue > maxMaxValue) return;
		this.minMinValue = minMinValue;
		if (minValue < minMinValue) minValue = minMinValue;
		notifyAllListeners();
	}

	public double getMaxMaxValue() {
		return maxMaxValue;
	}

	public void setMaxMaxValue(double maxMaxValue) {
		if (maxMaxValue < minMinValue) return;
		this.maxMaxValue = maxMaxValue;
		if (maxValue > maxMaxValue) maxValue = maxMaxValue;
		notifyAllListeners();
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		if (minValue < minMinValue) return;
		if (minValue > maxValue) return;
		this.minValue = minValue;
		notifyAllListeners();
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		if (maxValue > maxMaxValue) return;
		if (maxValue < minValue) return;
		this.maxValue = maxValue;
		notifyAllListeners();
	}

	public String toString() {
		return  "Values: " + minMinValue + "," + minValue + "," +
        		maxValue + "," + maxMaxValue;
	}
}
