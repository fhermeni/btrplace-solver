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

package org.btrplace.btrpsl.constraint;

import org.btrplace.btrpsl.element.BtrpNumber;
import org.btrplace.btrpsl.element.BtrpOperand;
import org.btrplace.btrpsl.element.IgnorableOperand;
import org.btrplace.btrpsl.tree.BtrPlaceTree;

/**
 * A parameter for a constraint that denotes a number.
 *
 * @author Fabien Hermenier
 */
public class NumberParam extends DefaultConstraintParam<Number> {

    /**
     * Make a new number parameter.
     *
     * @param n the parameter value
     */
    public NumberParam(String n) {
        super(n, "number");
    }

    @Override
    public Number transform(SatConstraintBuilder cb, BtrPlaceTree tree, BtrpOperand op) {
        if (op == IgnorableOperand.getInstance()) {
            throw new UnsupportedOperationException();
        }
        BtrpNumber n = (BtrpNumber) op;
        if (n.isInteger()) {
            return n.getIntValue();
        }
        return n.getDoubleValue();

    }

    @Override
    public boolean isCompatibleWith(BtrPlaceTree t, BtrpOperand o) {
        return o == IgnorableOperand.getInstance() || (o.type() == BtrpOperand.Type.NUMBER && o.degree() == 0);
    }
}
