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
 * TickMark.java
 * Created May, 2005
 * 
 * COPYRIGHT (c) 2005, THE REGENTS OF THE UNIVERSITY OF MICHIGAN,
 * ALL RIGHTS RESERVED; see the file COPYRIGHT.txt in this folder for details
 * 
 * This work is supported in part by the George E. Brown, Jr. Network
 * for Earthquake Engineering Simulation (NEES) Program of the National
 * Science Foundation under Award Numbers CMS-0117853 and CMS-0402490.
 * 
 * Copied unmodified from code developed by Lars Schumann 2003/09/01
 * Used as part of the NEES project's Tivo project; Terry Weymouth and Paul Hubbard, May, 2005
 * 
 */
package edu.umich.wld;

/**
 * A utility for computing the positions for tick marks on a scale.
 * Copied unmodified from code developed by Lars Schumann 2003/09/01
 * (Nice work Lars! - tew) 
 *
 * @author Lars Schumann
 */
public class TickMark {
    public static final double aSmallNumber = 1e-6;
    public static final double log10 = Math.log(10);

    double min;
    double max;
    double step;
    int count;

    public TickMark() {}

    /**
     * Construct a new set of scaling factors for tick marks on a scale
     * @param min - the desired min value
     * @param max - the desired max value
     * @param averageCnt - the desired number of tickmarks
     */
    public TickMark(double min, double max, int averageCnt) {
        set(min, max, averageCnt);
    }

    /**
     * Reset the set of scaling factors for tick marks on a scale
     * @param min - the desired min value
     * @param max - the desired max value
     * @param averageCnt - the desired number of tickmarks
     */
    public void set(double l, double h, int averageCnt) {
        double diff = Math.abs(h - l) / (double)averageCnt;
        double log = Math.log(diff) / log10;
        double floor = Math.floor(log);
        double rest = Math.pow(10, log - floor);

        int x;
        if (rest - aSmallNumber < 1.0)
            x = 1;
        else if (rest - aSmallNumber < 2.0)
            x = 2;
        else if (rest - aSmallNumber < 5.0)
            x = 5;
        else {
            x = 1;
            floor++;
        }

        step = (double)x * Math.pow(10, floor);
        min = Math.floor(l / step) * step;
        max = Math.ceil(h / step) * step;
        count = 1 + (int)Math.round((max - min) / step);
    }

    public double min() {
        return min;
    }
    public double max() {
        return max;
    }
    public double getStep() {
        return step;
    }
    public int getCount() {
        return count;
    }
}
