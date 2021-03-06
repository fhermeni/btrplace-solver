/*
 * Copyright  2020 The BtrPlace Authors. All rights reserved.
 * Use of this source code is governed by a LGPL-style
 * license that can be found in the LICENSE.txt file.
 */

package org.btrplace.model.constraint;

import org.btrplace.model.Model;
import org.btrplace.plan.event.Allocate;
import org.btrplace.plan.event.AllocateEvent;
import org.btrplace.plan.event.BootNode;
import org.btrplace.plan.event.BootVM;
import org.btrplace.plan.event.ForgeVM;
import org.btrplace.plan.event.KillVM;
import org.btrplace.plan.event.MigrateVM;
import org.btrplace.plan.event.ResumeVM;
import org.btrplace.plan.event.ShutdownNode;
import org.btrplace.plan.event.ShutdownVM;
import org.btrplace.plan.event.SubstitutedVMEvent;
import org.btrplace.plan.event.SuspendVM;

/**
 * Check if a constraint is satisfied by a reconfiguration plan.
 * <p>
 * The checking process is performed following an event-based approach
 * using an instance of {@link org.btrplace.plan.ReconfigurationPlanChecker}.
 * <p>
 * First, the checker is notified for the model at the origin of the
 * reconfiguration. It is then notified each time an action starts or ends
 * and finally, it is notified about the resulting model.
 * <p>
 * Actions notifications are propagated with regards to their starting
 * and ending moment. If an action ends at the same moment another action
 * starts, the notification for the ending action is send first.
 *
 * @author Fabien Hermenier
 * @see org.btrplace.plan.ReconfigurationPlanChecker
 */
public interface SatConstraintChecker<C extends SatConstraint> {

    /**
     * Notify for the model at the source of the reconfiguration.
     *
     * @param mo the model
     * @return {@code true} iff the model is valid wrt. the constraint
     */
    boolean startsWith(Model mo);

    /**
     * Notify for the beginning of an action.
     *
     * @param a the executed that will be executed
     * @return {@code true} iff the action execution is valid wrt. the constraint
     */
    boolean start(MigrateVM a);

    /**
     * Notify for the end of an action.
     *
     * @param a the action that ends
     */
    void end(MigrateVM a);

    /**
     * Notify for the beginning of an action.
     *
     * @param a the executed that will be executed
     * @return {@code true} iff the action execution is valid wrt. the constraint
     */
    boolean start(BootVM a);

    /**
     * Notify for the end of an action.
     *
     * @param a the action that ends
     */
    void end(BootVM a);

    /**
     * Notify for the beginning of an action.
     *
     * @param a the executed that will be executed
     * @return {@code true} iff the action execution is valid wrt. the constraint
     */
    boolean start(BootNode a);

    /**
     * Notify for the end of an action.
     *
     * @param a the action that ends
     */
    void end(BootNode a);

    /**
     * Notify for the beginning of an action.
     *
     * @param a the action that will be executed
     * @return {@code true} iff the action execution is valid wrt. the constraint
     */
    boolean start(ShutdownVM a);

    /**
     * Notify for the end of an action.
     *
     * @param a the action that ends
     */
    void end(ShutdownVM a);

    /**
     * Notify for the beginning of an action.
     *
     * @param a the action that will be executed
     * @return {@code true} iff the action execution is valid wrt. the constraint
     */
    boolean start(ShutdownNode a);

    /**
     * Notify for the end of an action.
     *
     * @param a the action that ends
     */
    void end(ShutdownNode a);

    /**
     * Notify for the beginning of an action.
     *
     * @param a the action that will be executed
     * @return {@code true} iff the action execution is valid wrt. the constraint
     */
    boolean start(ResumeVM a);

    /**
     * Notify for the end of an action.
     *
     * @param a the action that ends
     */
    void end(ResumeVM a);

    /**
     * Notify for the beginning of an action.
     *
     * @param a the action that will be executed
     * @return {@code true} iff the action execution is valid wrt. the constraint
     */
    boolean start(SuspendVM a);

    /**
     * Notify for the end of an action.
     *
     * @param a the action that ends
     */
    void end(SuspendVM a);

    /**
     * Notify for the beginning of an action.
     *
     * @param a the action that will be executed
     * @return {@code true} iff the action execution is valid wrt. the constraint
     */
    boolean start(KillVM a);

    /**
     * Notify for the end of an action.
     *
     * @param a the action that ends
     */
    void end(KillVM a);

    /**
     * Notify for the beginning of an action.
     *
     * @param a the action that will be executed
     * @return {@code true} iff the action execution is valid wrt. the constraint
     */
    boolean start(ForgeVM a);

    /**
     * Notify for the end of an action.
     *
     * @param a the action that ends
     */
    void end(ForgeVM a);

    /**
     * Notify for the beginning of an event.
     *
     * @param e the event that will be executed
     * @return {@code true} iff the action execution is valid wrt. the constraint
     */
    boolean consume(SubstitutedVMEvent e);

    /**
     * Notify for the beginning of an event.
     *
     * @param e the event that will be executed
     * @return {@code true} iff the action execution is valid wrt. the constraint
     */
    boolean consume(AllocateEvent e);

    /**
     * Notify for the beginning of an action.
     *
     * @param a the action that will be executed
     * @return {@code true} iff the action execution is valid wrt. the constraint
     */
    boolean start(Allocate a);

    /**
     * Notify for the end of an action.
     *
     * @param e the action that ends
     */
    void end(Allocate e);

    /**
     * Notify for the model that is reached once the reconfiguration has been applied.
     *
     * @param mo the model
     * @return {@code true} iff the model is valid wrt. the constraint
     */
    boolean endsWith(Model mo);

    /**
     * Get the constraint associated to the checker.
     *
     * @return a non-null constraint
     */
    C getConstraint();
}
