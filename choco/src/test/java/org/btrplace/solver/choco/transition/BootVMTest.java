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

package org.btrplace.solver.choco.transition;

import org.btrplace.model.*;
import org.btrplace.plan.ReconfigurationPlan;
import org.btrplace.plan.event.Action;
import org.btrplace.solver.SolverException;
import org.btrplace.solver.choco.DefaultParameters;
import org.btrplace.solver.choco.DefaultReconfigurationProblemBuilder;
import org.btrplace.solver.choco.Parameters;
import org.btrplace.solver.choco.ReconfigurationProblem;
import org.btrplace.solver.choco.duration.ConstantActionDuration;
import org.btrplace.solver.choco.duration.DurationEvaluators;
import org.testng.Assert;
import org.testng.annotations.Test;
import solver.Cause;
import solver.Solver;
import solver.constraints.IntConstraintFactory;
import solver.exception.ContradictionException;

import java.util.HashSet;
import java.util.Iterator;


/**
 * Basic unit tests for {@link BootVM}.
 *
 * @author Fabien Hermenier
 */
public class BootVMTest {

    /**
     * Just boot a VM on a  node.
     */
    @Test
    public void testBasics() throws SolverException, ContradictionException {
        Model mo = new DefaultModel();
        Mapping map = mo.getMapping();
        final VM vm1 = mo.newVM();
        Node n1 = mo.newNode();
        Node n2 = mo.newNode();

        map.addOnlineNode(n1);
        map.addOnlineNode(n2);
        map.addReadyVM(vm1);

        Parameters ps = new DefaultParameters();
        DurationEvaluators dev = ps.getDurationEvaluators();
        dev.register(org.btrplace.plan.event.BootVM.class, new ConstantActionDuration(5));
        ReconfigurationProblem rp = new DefaultReconfigurationProblemBuilder(mo)
                .setParams(ps)
                .setNextVMsStates(new HashSet<VM>(), map.getAllVMs(), new HashSet<VM>(), new HashSet<VM>())
                .build();
        rp.getNodeActions()[0].getState().instantiateTo(1, Cause.Null);
        rp.getNodeActions()[1].getState().instantiateTo(1, Cause.Null);
        BootVM m = (BootVM) rp.getVMActions()[0];
        Assert.assertEquals(vm1, m.getVM());
        Assert.assertNull(m.getCSlice());
        Assert.assertTrue(m.getDuration().isInstantiatedTo(5));
        Assert.assertTrue(m.getState().isInstantiatedTo(1));
        Assert.assertFalse(m.getDSlice().getHoster().isInstantiated());
        Assert.assertFalse(m.getDSlice().getStart().isInstantiated());
        Assert.assertFalse(m.getDSlice().getEnd().isInstantiated());

        ReconfigurationPlan p = rp.solve(0, false);
        org.btrplace.plan.event.BootVM a = (org.btrplace.plan.event.BootVM) p.getActions().iterator().next();

        Node dest = rp.getNode(m.getDSlice().getHoster().getValue());
        Assert.assertEquals(vm1, a.getVM());
        Assert.assertEquals(dest, a.getDestinationNode());
        Assert.assertEquals(5, a.getEnd() - a.getStart());
    }

    /**
     * Test that check when the action is shorter than the end of
     * the reconfiguration process.
     * In practice, 2 boot actions have to be executed sequentially
     */
    @Test
    public void testBootSequence() throws SolverException, ContradictionException {
        Model mo = new DefaultModel();
        Mapping map = mo.getMapping();
        final VM vm1 = mo.newVM();
        final VM vm2 = mo.newVM();
        Node n1 = mo.newNode();
        Node n2 = mo.newNode();

        map.addOnlineNode(n1);
        map.addOnlineNode(n2);
        map.addReadyVM(vm1);
        map.addReadyVM(vm2);

        Parameters ps = new DefaultParameters();
        DurationEvaluators dev = ps.getDurationEvaluators();
        dev.register(org.btrplace.plan.event.BootVM.class, new ConstantActionDuration(5));
        ReconfigurationProblem rp = new DefaultReconfigurationProblemBuilder(mo)
                .setParams(ps)
                .setNextVMsStates(new HashSet<VM>(), map.getAllVMs(), new HashSet<VM>(), new HashSet<VM>())
                .build();
        BootVM m1 = (BootVM) rp.getVMActions()[rp.getVM(vm1)];
        BootVM m2 = (BootVM) rp.getVMActions()[rp.getVM(vm2)];
        rp.getNodeActions()[0].getState().instantiateTo(1, Cause.Null);
        rp.getNodeActions()[1].getState().instantiateTo(1, Cause.Null);
        Solver s = rp.getSolver();
        s.post(IntConstraintFactory.arithm(m2.getStart(), ">=", m1.getEnd()));

        ReconfigurationPlan p = rp.solve(0, false);
        Assert.assertNotNull(p);
        Iterator<Action> ite = p.iterator();
        org.btrplace.plan.event.BootVM b1 = (org.btrplace.plan.event.BootVM) ite.next();
        org.btrplace.plan.event.BootVM b2 = (org.btrplace.plan.event.BootVM) ite.next();
        Assert.assertEquals(vm1, b1.getVM());
        Assert.assertEquals(vm2, b2.getVM());
        Assert.assertTrue(b1.getEnd() <= b2.getStart());
        Assert.assertEquals(5, b1.getEnd() - b1.getStart());
        Assert.assertEquals(5, b2.getEnd() - b2.getStart());

    }
}