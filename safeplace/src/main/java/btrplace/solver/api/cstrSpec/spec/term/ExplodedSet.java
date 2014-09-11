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

package btrplace.solver.api.cstrSpec.spec.term;

import btrplace.solver.api.cstrSpec.spec.type.SetType;
import btrplace.solver.api.cstrSpec.spec.type.Type;
import btrplace.solver.api.cstrSpec.verification.spec.SpecModel;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Fabien Hermenier
 */
public class ExplodedSet extends Term<Set> {

    private List<Term> terms;

    private Type t;

    public ExplodedSet(List<Term> ts, Type enclType) {
        this.terms = ts;
        t = new SetType(enclType);
    }

    @Override
    public Set eval(SpecModel mo) {
        Set s = new HashSet<>();
        for (Term t : terms) {
            s.add(t.eval(mo));
        }
        return s;
    }

    @Override
    public Type type() {
        return t;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("{");
        Iterator ite = terms.iterator();
        if (ite.hasNext()) {
            b.append(ite.next().toString());
        }
        while (ite.hasNext()) {
            b.append(", ").append(ite.next());
        }
        b.append('}');
        return b.toString();
    }
}