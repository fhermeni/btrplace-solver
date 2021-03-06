/*
 * Copyright  2020 The BtrPlace Authors. All rights reserved.
 * Use of this source code is governed by a LGPL-style
 * license that can be found in the LICENSE.txt file.
 */

package org.btrplace.model;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.THashSet;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Default implementation of {@link Mapping}.
 * <p>
 * Methods {@link #getRunningVMs()}, {@link #getSleepingVMs()}, {@link #getAllVMs()}, {@link #getAllNodes()},
 * {@link #getRunningVMs(Collection)}, {@link #getSleepingVMs(java.util.Collection)} have a O(n) complexity.
 * <p>
 * Methods {@code is*()} have a O(1) complexity.
 *
 * @author Fabien Hermenier
 */
public class DefaultMapping extends AbstractMapping {

    private static final int RUNNING_STATE = 0;

    private static final int SLEEPING_STATE = 1;

    private static final int READY_STATE = 2;

    private static final int ONLINE_STATE = 0;

    private static final int OFFLINE_STATE = 1;

  /**
   * The node by states (online, offline)
   */
  private final Set<Node>[] nodeState;

    /**
     * The state of each VM.
     */
    private final TIntIntHashMap st;

  /**
   * The current location of the VMs.
   */
  private final TIntObjectHashMap<Node> place;

  /**
   * The VMs that are in the ready state.
   */
  private final Set<VM> vmReady;

  /**
   * The VMs hosted by each node, by state (running or sleeping)
   */
  private final TIntObjectHashMap<Set<VM>>[] host;

  /**
     * Create a new mapping.
     */
    @SuppressWarnings("unchecked")
    public DefaultMapping() {

        nodeState = new Set[2];
        nodeState[ONLINE_STATE] = new THashSet<>();
        nodeState[OFFLINE_STATE] = new THashSet<>();

        vmReady = new THashSet<>();

        place = new TIntObjectHashMap<>();

        host = new TIntObjectHashMap[2];
        host[RUNNING_STATE] = new TIntObjectHashMap<>();
        host[SLEEPING_STATE] = new TIntObjectHashMap<>();

        st = new TIntIntHashMap(100, 0.5f, -1, -1);
    }

    /**
     * Make a new mapping from an existing one.
     *
     * @param m the mapping to copy
     */
    public DefaultMapping(Mapping m) {
        this();
        MappingUtils.fill(m, this);
    }

    @Override
    public boolean isRunning(VM v) {
        return st.get(v.id()) == RUNNING_STATE;
    }

    @Override
    public boolean isSleeping(VM v) {
        return st.get(v.id()) == SLEEPING_STATE;
    }

    @Override
    public boolean isReady(VM v) {
        return st.get(v.id()) == READY_STATE;
    }

    @Override
    public boolean isOnline(Node n) {
        return nodeState[ONLINE_STATE].contains(n);
    }

    @Override
    public boolean isOffline(Node n) {
        return nodeState[OFFLINE_STATE].contains(n);
    }

    @Override
    public boolean addRunningVM(VM vm, Node n) {
        if (!nodeState[ONLINE_STATE].contains(n)) {
            return false;
        }
        Node old;

        int vmId = vm.id();
        int nId = n.id();
        Set<VM> on = host[RUNNING_STATE].get(nId);
        if (on == null) {
            on = new THashSet<>();
            host[RUNNING_STATE].put(nId, on);
        }
        switch (st.get(vmId)) {
            case RUNNING_STATE:
                old = place.put(vmId, n);
                if (!old.equals(n)) {
                    host[RUNNING_STATE].get(old.id()).remove(vm);
                    on.add(vm);
                }
                break;
            case SLEEPING_STATE:
                old = place.put(vmId, n);
                host[SLEEPING_STATE].get(old.id()).remove(vm);
                on.add(vm);
                st.put(vmId, RUNNING_STATE);
                break;
            case READY_STATE:
                place.put(vmId, n);
                on.add(vm);
                vmReady.remove(vm);
                st.put(vmId, RUNNING_STATE);
                break;
            default:
                place.put(vmId, n);
                on.add(vm);
                st.put(vmId, RUNNING_STATE);
                break;
        }
        return true;
    }

    @Override
    public boolean addSleepingVM(VM vm, Node n) {
        if (!nodeState[ONLINE_STATE].contains(n)) {
            return false;
        }
        int nId = n.id();
        int vmId = vm.id();
        Set<VM> on = host[SLEEPING_STATE].get(nId);
        if (on == null) {
            on = new THashSet<>();
            host[SLEEPING_STATE].put(nId, on);
        }
        Node old;
        switch (st.get(vmId)) {
            case RUNNING_STATE:
                //If was running, sync the state
                old = place.put(vmId, n);
                host[RUNNING_STATE].get(old.id()).remove(vm);
                on.add(vm);
                st.put(vmId, SLEEPING_STATE);
                break;
            case SLEEPING_STATE:
                //If was sleeping, sync the state
                old = place.put(vmId, n);
                if (!old.equals(n)) {
                    host[SLEEPING_STATE].get(old.id()).remove(vm);
                    on.add(vm);
                }
                break;
            case READY_STATE:
                place.put(vmId, n);
                on.add(vm);
                vmReady.remove(vm);
                st.put(vmId, SLEEPING_STATE);
                break;
            default:
                //it's a new VM
                place.put(vmId, n);
                host[SLEEPING_STATE].get(nId).add(vm);
                st.put(vmId, SLEEPING_STATE);
                break;
        }
        st.put(vm.id(), SLEEPING_STATE);
        return true;
    }

    @Override
    public boolean addReadyVM(VM vm) {

        Node n = place.remove(vm.id());
        int state = st.get(vm.id());
        if (state == RUNNING_STATE) {
            //If was running, sync the state
            host[RUNNING_STATE].get(n.id()).remove(vm);
        } else if (state == SLEEPING_STATE) {
                //If was sleeping, sync the state
                host[SLEEPING_STATE].get(n.id()).remove(vm);
        }
        st.put(vm.id(), READY_STATE);
        vmReady.add(vm);
        return true;
    }

    @Override
    public boolean remove(VM vm) {
        if (place.containsKey(vm.id())) {
            Node n = this.place.remove(vm.id());
            //The VM exists and is already placed
            if (st.get(vm.id()) == RUNNING_STATE) {
                host[RUNNING_STATE].get(n.id()).remove(vm);
            } else if (st.get(vm.id()) == SLEEPING_STATE) {
                host[SLEEPING_STATE].get(n.id()).remove(vm);
            }
            st.remove(vm.id());
            return true;
        } else if (st.get(vm.id()) == READY_STATE) {

            vmReady.remove(vm);
            st.remove(vm.id());
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Node n) {
        if (nodeState[ONLINE_STATE].contains(n)) {
            int nId = n.id();
            Set<VM> on = host[RUNNING_STATE].get(nId);
            if (on != null) {
                if (!on.isEmpty()) {
                    return false;
                }
                host[RUNNING_STATE].remove(nId);
            }

            on = host[SLEEPING_STATE].get(nId);
            if (on != null) {
                if (!on.isEmpty()) {
                    return false;
                }
                host[SLEEPING_STATE].remove(nId);
            }
            return nodeState[ONLINE_STATE].remove(n);
        }

        return nodeState[OFFLINE_STATE].remove(n);
    }

    @Override
    public boolean addOnlineNode(Node n) {
        nodeState[OFFLINE_STATE].remove(n);
        nodeState[ONLINE_STATE].add(n);
        return true;
    }

    @Override
    public boolean addOfflineNode(Node n) {
        int nId = n.id();
        if (nodeState[ONLINE_STATE].contains(n)) {
            Set<VM> on = host[SLEEPING_STATE].get(nId);
            if (on != null && !on.isEmpty()) {
                return false;
            }
            on = host[RUNNING_STATE].get(nId);
            if (on != null && !on.isEmpty()) {
                return false;
            }
            nodeState[ONLINE_STATE].remove(n);
        }
        nodeState[OFFLINE_STATE].add(n);
        return true;
    }

    @Override
    public Set<Node> getOnlineNodes() {
        return nodeState[ONLINE_STATE];
    }

    @Override
    public Set<Node> getOfflineNodes() {
        return nodeState[OFFLINE_STATE];
    }

    @Override
    public Set<VM> getRunningVMs() {
        return getRunningVMs(getOnlineNodes());
    }

    @Override
    public Set<VM> getSleepingVMs() {
        return getSleepingVMs(getOnlineNodes());
    }

    @Override
    public Set<VM> getSleepingVMs(Node n) {
        Set<VM> in = host[SLEEPING_STATE].get(n.id());
        if (in == null) {
            return Collections.emptySet();
        }
        return in;
    }

    @Override
    public Set<VM> getRunningVMs(Node n) {
        Set<VM> in = host[RUNNING_STATE].get(n.id());
        if (in == null) {
            return Collections.emptySet();
        }
        return in;
    }

    @Override
    public Set<VM> getReadyVMs() {
        return vmReady;
    }

    @Override
    public Set<VM> getAllVMs() {
        final Set<VM> s = new HashSet<>(vmReady);
        host[RUNNING_STATE].forEachEntry((a, b) -> {
            s.addAll(b);
            return true;
        });
        host[SLEEPING_STATE].forEachEntry((a, b) -> {
            s.addAll(b);
            return true;
        });
        return s;
    }

    @Override
    public Set<Node> getAllNodes() {
        Set<Node> ns = new THashSet<>(
                nodeState[OFFLINE_STATE].size() +
                        nodeState[ONLINE_STATE].size()
        );
        ns.addAll(nodeState[OFFLINE_STATE]);
        ns.addAll(nodeState[ONLINE_STATE]);
        return ns;
    }

    @Override
    public Node getVMLocation(VM vm) {
        return place.get(vm.id());
    }

    @Override
    public Set<VM> getRunningVMs(Collection<Node> ns) {
        Set<VM> vms = new THashSet<>();
        for (Node n : ns) {
            vms.addAll(getRunningVMs(n));
        }
        return vms;
    }

    @Override
    public Set<VM> getSleepingVMs(Collection<Node> ns) {
        Set<VM> vms = new THashSet<>();
        for (Node n : ns) {
            vms.addAll(getSleepingVMs(n));
        }
        return vms;
    }

    @Override
    public Mapping copy() {
        return new DefaultMapping(this);
    }

    @Override
    public boolean contains(Node n) {
        return nodeState[OFFLINE_STATE].contains(n) || nodeState[ONLINE_STATE].contains(n);
    }

    @Override
    public boolean contains(VM vm) {
        return st.get(vm.id()) >= 0;
    }

    @Override
    public void clear() {
        for (Set<Node> s : nodeState) {
            s.clear();
        }
        st.clear();
        vmReady.clear();
        place.clear();
        for (TIntObjectHashMap<Set<VM>> h : host) {
            h.clear();
        }
    }

    @Override
    public void clearNode(Node u) {
        //Get the VMs on the node
        for (TIntObjectHashMap<Set<VM>> h : host) {
            Set<VM> s = h.get(u.id());
            if (s != null) {
                for (VM vm : s) {
                    place.remove(vm.id());
                    st.remove(vm.id());
                }
                s.clear();
            }
        }
    }

    @Override
    public void clearAllVMs() {
        place.clear();
        st.clear();
        vmReady.clear();
        for (TIntObjectHashMap<Set<VM>> h : host) {
            h.clear();
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        for (Node n : nodeState[ONLINE_STATE]) {
            buf.append(n);
            buf.append(':');
            if (this.getRunningVMs(n).isEmpty() && this.getSleepingVMs(n).isEmpty()) {
                buf.append(" - ");
            }
            for (VM vm : this.getRunningVMs(n)) {
                buf.append(' ').append(vm);
            }
            for (VM vm : this.getSleepingVMs(n)) {
                buf.append(" (").append(vm).append(')');
            }
            buf.append('\n');
        }

        for (Node n : nodeState[OFFLINE_STATE]) {
            buf.append('(').append(n).append(")\n");
        }

        buf.append("READY");

        for (VM vm : this.getReadyVMs()) {
            buf.append(' ').append(vm);
        }

        return buf.append('\n').toString();
    }

    @Override
    public int getNbNodes() {
        return nodeState[ONLINE_STATE].size() + nodeState[OFFLINE_STATE].size();
    }

    @Override
    public int getNbVMs() {
        return st.size();
    }

}
