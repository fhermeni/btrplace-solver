/*
 * Copyright (c) 2012 University of Nice Sophia-Antipolis
 *
 * This file is part of btrplace.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package btrplace.solver;

import btrplace.model.Model;

/**
 * An exception that indicate an error in the reconfiguration algorithm.
 *
 * @author Fabien Hermenier
 */
public class SolverException extends Exception {

    private Model model;

    /**
     * Make a new exception.
     *
     * @param m   the model that lead to the exception
     * @param msg the error message
     */
    public SolverException(Model m, String msg) {
        super(msg);
        model = m;
    }

    /**
     * Make a new exception.
     *
     * @param m   the model that lead to the exception
     * @param msg the error message
     * @param t   the throwable to re-throw
     */
    public SolverException(Model m, String msg, Throwable t) {
        super(msg, t);
        model = m;
    }

    /**
     * Get the model at the source of the exception.
     *
     * @return a Model
     */
    public Model getModel() {
        return model;
    }
}
