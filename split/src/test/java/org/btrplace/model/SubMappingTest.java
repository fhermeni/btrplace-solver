/*
 * Copyright  2020 The BtrPlace Authors. All rights reserved.
 * Use of this source code is governed by a LGPL-style
 * license that can be found in the LICENSE.txt file.
 */

package org.btrplace.model;

import org.btrplace.scheduler.runner.disjoint.model.SubMapping;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Unit tests for {@link SubMapping}.
 *
 * @author Fabien Hermenier
 */
public class SubMappingTest {

    public SubMapping make() {
        Mapping m = new DefaultMapping();
        Set<Node> scope = new HashSet<>();
        Set<VM> subReady = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            Node n = new Node(i);
            if (i < 5) {
                m.addOnlineNode(n);
                m.addRunningVM(new VM((i + 1) * 100), n);
                m.addSleepingVM(new VM((i + 1) * 100 + i), n);
            } else {
                m.addOfflineNode(n);
            }

            if (i % 2 == 0) {
                scope.add(n);
            }
        }
        for (int i = 1000; i < 1010; i++) {
            VM v = new VM(i);
            m.addReadyVM(v);
            if (i % 2 == 0) {
                subReady.add(v);
            }
        }
        return new SubMapping(m, scope, subReady);
    }

    /**
     * Check the content of the getters.
     */
    @Test
    public void testInstantiation() {
        SubMapping sm = make();
        //Set getters
        Assert.assertEquals(sm.getAllNodes().size(), 5);
        Assert.assertEquals(sm.getOnlineNodes().size(), 3); //0, 2, 4
        for (Node n : sm.getOnlineNodes()) {
            Assert.assertTrue(n.id() % 2 == 0);
        }

        Assert.assertEquals(sm.getOfflineNodes().size(), 2); //6, 8
        for (Node n : sm.getOfflineNodes()) {
            Assert.assertTrue(n.id() % 2 == 0);
        }


        Assert.assertEquals(sm.getRunningVMs().size(), 2);
        for (VM v : sm.getRunningVMs()) {
            Assert.assertTrue(v.id() % 2 == 0);
        }

        Assert.assertEquals(sm.getSleepingVMs().size(), 3);
        for (VM v : sm.getSleepingVMs()) {
            Assert.assertTrue(v.id() % 2 == 0);
        }

        Assert.assertEquals(sm.getReadyVMs().size(), 5);
        for (VM v : sm.getReadyVMs()) {
            Assert.assertTrue(v.id() % 2 == 0);
        }
        Assert.assertEquals(sm.getAllVMs().size(), 10);

        //State checkers
        Assert.assertTrue(sm.contains(new VM(100)));
        Assert.assertTrue(sm.contains(new VM(1000)));
        Assert.assertFalse(sm.contains(new VM(201))); //In the parent, out of scope
        Assert.assertFalse(sm.contains(new VM(1009))); //In the parent, out of scope

        Assert.assertTrue(sm.isRunning(new VM(300)));
        Assert.assertFalse(sm.isRunning(new VM(1000)));
        Assert.assertTrue(sm.isReady(new VM(1000)));
        Assert.assertTrue(sm.isSleeping(new VM(504)));
        Assert.assertFalse(sm.isRunning(new VM(201)));
        System.err.println(sm);
    }

    @Test
    public void testAddRunning() {
        SubMapping sm = make();
        //Set running a VM that does not belong to the submapping but belong to the parent
        //System.err.println(sm.getParent());
        Assert.assertFalse(sm.addRunningVM(new VM(201), new Node(2)));
        Assert.assertFalse(sm.addRunningVM(new VM(200), new Node(2)));
        //Node not in the scope
        Assert.assertFalse(sm.addRunningVM(new VM(1000), new Node(3)));

        //1000 is in the ready scope, it's ok
        Assert.assertTrue(sm.addRunningVM(new VM(1000), new Node(2)));
    }

    @Test
    public void testAddReady() {
        SubMapping sm = make();
        //Set running a VM that does not belong to the submapping but belong to the parent
        Assert.assertFalse(sm.addReadyVM(new VM(400))); //VM is elsewhere. No way
        Assert.assertTrue(sm.addReadyVM(new VM(300))); //in my scope, it's ok
        Assert.assertTrue(sm.addReadyVM(new VM(678))); //new VM, it's ok
    }

    @Test
    public void testGetVMLocation() {
        SubMapping sm = make();
        System.err.println(sm.getParent());
        //Get a location of a VM in the sub-mapping
        Assert.assertNotNull(sm.getVMLocation(new VM(100))); //VM located on node#0
        //Get a location of a VM out of the sub-mapping
        Assert.assertNull(sm.getVMLocation(new VM(200))); //VM located on node#1
        System.err.println(sm);
    }

    @Test
    public void testGetRunningVMs() {
        SubMapping sm = make();
        //Get all the VMs on the sub-mapping by querying
        //using all the nodes in the parent mapping.
        //Must return only the VMs in the sub-mapping
        Assert.assertEquals(sm.getRunningVMs(sm.getParent().getAllNodes()), sm.getRunningVMs());
    }

    @Test
    public void testGetSleepingVMs() {
        SubMapping sm = make();
        //Get all the VMs on the sub-mapping by querying
        //using all the nodes in the parent mapping.
        //Must return only the VMs in the sub-mapping
        Assert.assertEquals(sm.getSleepingVMs(sm.getParent().getAllNodes()), sm.getSleepingVMs());
    }

    @Test
    public void testRemoveVMs() {
        SubMapping sm = make();
        Mapping parent = sm.getParent();
        //Remove a VM in the sub. Should be removed in the parent too
        VM v = new VM(100);
        Assert.assertTrue(sm.remove(v));
        Assert.assertFalse(sm.contains(v));
        Assert.assertFalse(parent.contains(v));

        //Remove a VM out of the sub. Failure
        VM v2 = new VM(200);
        Assert.assertFalse(sm.remove(v2));
        Assert.assertFalse(sm.contains(v2));
        Assert.assertTrue(parent.contains(v2));
    }
}