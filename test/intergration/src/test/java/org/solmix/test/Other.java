/*
 * SOLMIX PROJECT
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */

package org.solmix.test;

import java.util.Random;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013年11月24日
 */

public class Other
{

    /**
     * @param args
     */
    public static void main(String[] args) {

        double a = 0;
        double b = 1;
        Random random = new Random();
        Double[] sums = new Double[100];
        for (int i = 0; i < 100; i++) {
            sums[i] = random.nextGaussian();
            System.out.println(random.nextGaussian());
        }
        double sum = 0;
        for (int i = 0; i < 100; i++) {
            sum += sums[i];
        }
        System.out.println("----");
        System.out.println(sum);
    }

}
