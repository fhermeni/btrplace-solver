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

package org.btrplace.model.view;

import org.btrplace.model.DefaultModel;
import org.btrplace.model.Model;
import org.btrplace.model.Node;
import org.btrplace.model.view.network.Link;
import org.btrplace.model.view.network.Network;
import org.btrplace.model.view.network.StaticRouting;
import org.btrplace.model.view.network.Switch;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link org.btrplace.model.view.network.Network}.
 *
 * @author Vincent Kherbache
 * @see org.btrplace.model.view.network.Network
 */
public class NetworkTest {

    /**
     * Test the instantiation and the creation of the objects using the default routing implementation.
     */
    @Test
    public void defaultTest() {

        Model mo = new DefaultModel();
        Network net = new Network();
        Switch s = net.newSwitch(1000);
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        net.connect(2000, s, n1, n2);
        mo.attach(net);

        Assert.assertTrue(net.getSwitches().size() == 1);
        Assert.assertEquals(net.getSwitches().get(0), s);
        Assert.assertTrue(s.getCapacity() == 1000);
        Assert.assertTrue(net.getLinks().size() == 2);
        Assert.assertTrue(net.getLinks().size() == 2);
        for (Link l : net.getLinks()) {
            Assert.assertTrue(l.getCapacity() == 2000);
            Assert.assertTrue(l.getSwitch() == s || l.getElement() instanceof Switch);
        }
        
        Assert.assertTrue(net.getRouting().getPath(n1, n2).size() == 2);
        Assert.assertTrue(net.getRouting().getPath(n1, n2).containsAll(net.getLinks()));
    }

    /**
     * Test the static routing implementation.
     */
    @Test
    public void staticRoutingTest() {

        Model mo = new DefaultModel();
        Network net = new Network(new StaticRouting());
        Switch s = net.newSwitch(1000);
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        net.connect(2000, s, n1, n2);
        ((StaticRouting) net.getRouting()).setStaticRoute(new StaticRouting.NodesMap(n1, n2), net.getLinks());
        mo.attach(net);

        Assert.assertTrue(net.getRouting().getPath(n1, n2).size() == 2);
        Assert.assertTrue(net.getRouting().getPath(n1, n2).containsAll(net.getLinks()));
    }
}