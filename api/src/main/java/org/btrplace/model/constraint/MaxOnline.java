/*
 * Copyright  2020 The BtrPlace Authors. All rights reserved.
 * Use of this source code is governed by a LGPL-style
 * license that can be found in the LICENSE.txt file.
 */

package org.btrplace.model.constraint;

import org.btrplace.model.Node;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

/**
 * A constraint to force a set of nodes to have a maximum number of nodes to
 * be online.
 * <p>
 * In discrete restriction mode, the constraint only ensures that the set of
 * nodes have at most {@code n} nodes being online at by end of the reconfiguration
 * process. The set of nodes may have more number than n nodes being online in
 * the reconfiguration process.
 * <p>
 * In continuous restriction mode, a boot node action is performed only when the
 * number of online nodes is smaller than n.
 *
 * @author Tu Huynh Dang
 */
@SideConstraint(args = {"ns <: nodes", "nb : int"}, inv = "card({i. i : ns , nodeState(i) = online}) <= nb")
public class MaxOnline extends SimpleConstraint {

    /**
     * number of reserved nodes
     */
    private final int qty;

  private final Set<Node> nodes;

    /**
     * Make a new constraint specifying restriction explicitly.
     *
     * @param nodes      The set of nodes
     * @param n          The maximum number of online nodes
     * @param continuous {@code true} for continuous restriction
     */
    public MaxOnline(Set<Node> nodes, int n, boolean continuous) {
        super(continuous);
        this.nodes = nodes;
        qty = n;
    }

    /**
     * Make a new discrete constraint.
     *
     * @param nodes the set of nodes
     * @param n     the maximum number of online nodes
     */
    public MaxOnline(Set<Node> nodes, int n) {
        this(nodes, n, false);
    }

    /**
     * Get the maximum number of online nodes.
     *
     * @return a positive integer
     */
    public int getAmount() {
        return qty;
    }


    @Override
    public String toString() {
        return "maxOnline(" + "nodes=" + nodes +
                ", amount=" + qty + ", " + (isContinuous() ? "continuous" : "discrete") + ')';
    }

    @Override
    public SatConstraintChecker<MaxOnline> getChecker() {
        return new MaxOnlineChecker(this);
    }


    @Override
    public Collection<Node> getInvolvedNodes() {
        return nodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MaxOnline maxOnline = (MaxOnline) o;
        return qty == maxOnline.qty &&
                isContinuous() == maxOnline.isContinuous() &&
                Objects.equals(nodes, maxOnline.nodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qty, nodes, isContinuous());
    }
}
