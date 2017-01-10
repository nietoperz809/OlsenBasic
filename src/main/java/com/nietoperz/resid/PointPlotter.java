/**
 * encoding: UTF-8
 * This file is part of reSID, a MOS6581 SID emulator engine.
 * Copyright (C) 2004  Dag Lem <resid@nimrod.no>
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * @author Ken Händel
 */
package com.nietoperz.resid;

/**
 * Class for plotting integers into an array.
 *
 * @author Ken Händel
 *
 */
public class PointPlotter
{
    private final int[] /* sound_sample */f;

    public PointPlotter (int /* sound_sample */arr[])
    {
        this.f = arr;
    }

    void plot (int x, int y)
    {
        // Clamp negative values to zero.
        if (y < 0)
        {
            y = 0;
        }

        f[x] = y;
    }
}
