/*
 * Copyright  2020 The BtrPlace Authors. All rights reserved.
 * Use of this source code is governed by a LGPL-style
 * license that can be found in the LICENSE.txt file.
 */

package org.btrplace.btrpsl.template;

import org.btrplace.btrpsl.Script;
import org.btrplace.btrpsl.element.BtrpElement;
import org.btrplace.btrpsl.element.BtrpOperand;
import org.btrplace.model.DefaultModel;
import org.btrplace.model.Model;
import org.btrplace.model.Node;
import org.btrplace.model.VM;
import org.btrplace.model.view.NamingService;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Unit tests for {@link DefaultTemplateFactory}.
 *
 * @author Fabien Hermenier
 */
public class DefaultTemplateFactoryTest {

    public static class MockVMTemplate implements Template {

        NamingService<Node> srvNodes;
        NamingService<VM> srvVMs;
        String tplName;

        @Override
        public BtrpOperand.Type getElementType() {
            return BtrpOperand.Type.VM;
        }

        public MockVMTemplate(String n) {
            tplName = n;
        }

        @Override
        public BtrpElement check() throws ElementBuilderException {
            return null;
        }

        @Override
        public String getIdentifier() {
            return tplName;
        }

        @Override
        public void setNamingServiceNodes(NamingService<Node> srvNodes) {
            this.srvNodes = srvNodes;
        }

        @Override
        public void setNamingServiceVMs(NamingService<VM> srvVMs) {
            this.srvVMs = srvVMs;
        }
    }

    public static class MockNodeTemplate implements Template {

        String tplName;

        public Model mo;

        @Override
        public BtrpOperand.Type getElementType() {
            return BtrpOperand.Type.NODE;
        }

        public MockNodeTemplate(String n) {
            tplName = n;
        }

        @Override
        public BtrpElement check() throws ElementBuilderException {
            BtrpElement el = new BtrpElement(getElementType(), "foo", mo.newVM());
            mo.getAttributes().put(el.getElement(), "template", getIdentifier());
            return el;
        }

        @Override
        public String getIdentifier() {
            return tplName;
        }

        @Override
        public void setNamingServiceVMs(NamingService<VM> srvVMs) {
        }

        @Override
        public void setNamingServiceNodes(NamingService<Node> srvNodes) {
        }
    }

    @Test
    public void testInstantiation() {
        DefaultTemplateFactory tplf = new DefaultTemplateFactory(NamingService.newNodeNS(), NamingService.newVMNS(), new DefaultModel());
        Assert.assertTrue(tplf.getAvailables().isEmpty());
    }

    @Test(dependsOnMethods = {"testInstantiation"})
    public void testRegister() {
        NamingService<Node> srvNodes = NamingService.newNodeNS();
        NamingService<VM> srvVMs = NamingService.newVMNS();
        DefaultTemplateFactory tplf = new DefaultTemplateFactory(srvNodes, srvVMs, new DefaultModel());
        MockVMTemplate t1 = new MockVMTemplate("mock1");
        Assert.assertNull(tplf.register(t1));
        Assert.assertEquals(t1.srvNodes, srvNodes);
        Assert.assertEquals(t1.srvVMs, srvVMs);
        Assert.assertTrue(tplf.getAvailables().contains("mock1"));
        MockVMTemplate t2 = new MockVMTemplate("mock2");
        Assert.assertNull(tplf.register(t2));
        Assert.assertEquals(t2.srvNodes, srvNodes);
        Assert.assertEquals(t2.srvVMs, srvVMs);
        Assert.assertTrue(tplf.getAvailables().contains("mock2") && tplf.getAvailables().size() == 2);

    }

    /*@Test(dependsOnMethods = {"testInstantiation", "testRegister"})
    public void testAccessibleWithStrict() throws ElementBuilderException {
        Model mo = new DefaultModel();
        DefaultTemplateFactory tplf = new DefaultTemplateFactory(new InMemoryNamingService(mo));
        tplf.register(new MockVMTemplate("mock1"));
        Script scr = new Script();
        tplf.check(scr, "mock1", null, new HashMap<String, String>());
        Assert.assertEquals(mo.getAttributes().get(el.getElement(), "template"), "mock1");
    }         */

    /*@Test(dependsOnMethods = {"testInstantiation", "testRegister"}, expectedExceptions = {ElementBuilderException.class})
    public void testInaccessibleWithStrict() throws ElementBuilderException {
        DefaultTemplateFactory tplf = new DefaultTemplateFactory(new InMemoryNamingService(), new DefaultModel());
        Script scr = new Script();
        tplf.check(scr, "bar", , "foo", new HashMap<String, String>());
    } */

    /*@Test(dependsOnMethods = {"testInstantiation", "testRegister"})
    public void testAccessibleWithoutStrict() throws ElementBuilderException {
        Model mo = new DefaultModel();
        DefaultTemplateFactory tplf = new DefaultTemplateFactory(new InMemoryNamingService(), mo);
        tplf.register(new MockVMTemplate("mock1"));
        Script scr = new Script();
        tplf.check(scr, "mock1", null, new HashMap<String, String>());
        Assert.assertEquals(mo.getAttributes().get(el.getElement(), "template"), "mock1");
    } */

    /*@Test(dependsOnMethods = {"testInstantiation", "testRegister"})
    public void testInaccessibleWithoutStrict() throws ElementBuilderException {
        Model mo = new DefaultModel();
        DefaultTemplateFactory tplf = new DefaultTemplateFactory(new InMemoryNamingService(), mo);
        Map<String, String> m = new HashMap<>();
        m.put("migratable", null);
        m.put("foo", "7.5");
        m.put("bar", "1243");
        m.put("template", "bar");
        Script scr = new Script();
        tplf.check(scr, "bar", null, m);
        Assert.assertEquals(mo.getAttributes().get(el.getElement(), "template"), "bar");
        Assert.assertEquals(el.getName(), "foo");
        Assert.assertTrue(mo.getAttributes().getBoolean(el.getElement(), "migratable"));
        Assert.assertEquals(mo.getAttributes().getInteger(el.getElement(), "bar").longValue(), 1243);
        Assert.assertEquals(mo.getAttributes().getDouble(el.getElement(), "foo"), 7.5);
        Assert.assertEquals(mo.getAttributes().getKeys(el.getElement()), m.keySet());
    }                    */

    @Test(expectedExceptions = {ElementBuilderException.class})
    public void testTypingIssue() throws ElementBuilderException {
        Model mo = new DefaultModel();
        DefaultTemplateFactory tplf = new DefaultTemplateFactory(NamingService.newNodeNS(), NamingService.newVMNS(), mo);
        tplf.register(new MockVMTemplate("mock1"));
        Script scr = new Script();
        tplf.check(scr, "mock1", mo.newNode(), new HashMap<>());
    }

}
