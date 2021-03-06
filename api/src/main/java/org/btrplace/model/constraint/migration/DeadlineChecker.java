/*
 * Copyright  2020 The BtrPlace Authors. All rights reserved.
 * Use of this source code is governed by a LGPL-style
 * license that can be found in the LICENSE.txt file.
 */

package org.btrplace.model.constraint.migration;

import org.btrplace.model.constraint.AllowAllConstraintChecker;

/**
 * Checker for the {@link Deadline} constraint.
 *
 * @author Vincent Kherbache
 * @see Deadline
 */
public class DeadlineChecker extends AllowAllConstraintChecker<Deadline> {

    /**
     * Make a new checker.
     *
     * @param dl the deadline constraint associated to the checker.
     */
    public DeadlineChecker(Deadline dl) {
        super(dl);
    }
}