/*
 * Copyright (c) 2014 University Nice Sophia Antipolis
 *
 * This file is part of btrplace.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package btrplace.solver.api.cstrSpec.backend;

import btrplace.solver.api.cstrSpec.verification.TestCase;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Fabien Hermenier
 */
public class Counter implements Countable {

    private AtomicInteger nbDefiant;

    private AtomicInteger nbCompliant;

    public Counter() {
        nbDefiant = new AtomicInteger(0);
        nbCompliant = new AtomicInteger(0);
    }

    @Override
    public void addDefiant(TestCase c) {
        nbDefiant.incrementAndGet();
    }

    @Override
    public void addCompliant(TestCase c) {
        nbCompliant.incrementAndGet();
    }

    @Override
    public void flush() {
    }

    public int getNbDefiant() {
        return nbDefiant.get();
    }

    public int getNbCompliant() {
        return nbCompliant.get();
    }
}