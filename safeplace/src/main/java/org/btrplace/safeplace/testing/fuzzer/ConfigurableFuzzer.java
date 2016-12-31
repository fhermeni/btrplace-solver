/*
 * Copyright (c) 2016 University Nice Sophia Antipolis
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

package org.btrplace.safeplace.testing.fuzzer;


import org.btrplace.safeplace.testing.fuzzer.domain.Domain;

import java.io.Writer;
import java.util.Set;

/**
 * A configurable fuzzer where it is possible to configure the {@link org.btrplace.plan.ReconfigurationPlan}
 * but also the constraint arguments.
 * @author Fabien Hermenier
 */
public interface ConfigurableFuzzer extends Fuzzer, ReconfigurationPlanParams {

    /**
     * Set the value for a constraint int argument.
     *
     * @param arg the argument name
     * @param val the value
     * @return {@code this}
     */
    ConfigurableFuzzer with(String arg, int val);

    /**
     * Set the domain for a constraint int argument.
     * The fuzzer will pick a value among this domain
     *
     * @param arg the argument name
     * @param min the lower bound
     * @param max the upper bound
     * @return {@code this}
     */
    ConfigurableFuzzer with(String arg, int min, int max);

    /**
     * Set the domain for a constraint int argument.
     * The fuzzer will pick a value among this domain
     *
     * @param arg  the argument name
     * @param vals the possible values
     * @return {@code this}
     */
    ConfigurableFuzzer with(String arg, int[] vals);

    /**
     * Set the value for a constraint String argument.
     *
     * @param arg the argument name
     * @param val the value
     * @return {@code this}
     */
    ConfigurableFuzzer with(String arg, String val);

    /**
     * Set the domain for a constraint String argument.
     * The fuzzer will pick a value among this domain
     *
     * @param arg  the argument name
     * @param vals the possible values
     * @return {@code this}
     */
    ConfigurableFuzzer with(String arg, String[] vals);

    /**
     * Set the domain for a constraint argument.
     * The fuzzer will pick a value among this domain
     *
     * @param arg the argument name
     * @param d   the argument domain
     * @return {@code this}
     */
    ConfigurableFuzzer with(String arg, Domain d);

    /**
     * Set the restriction domain for the constraint to fuzz.
     * The fuzzer will pick a value among this domain
     *
     * @param domain the domain
     * @return {@code this}
     */
    ConfigurableFuzzer restriction(Set<Restriction> domain);

    /**
     * Write the generated test cases for a later replay.
     * Each {@link org.btrplace.safeplace.testing.TestCase} is serialised to a JSON format.
     * One line per test case.
     * @see Replay to provide the saved test cases
     * @param w the output stream
     * @return {@code this}
     */
    ConfigurableFuzzer save(Writer w);

    /**
     * Write the generated test cases for a later replay.
     *
     * @see #save(Writer)
     * @param path the output file
     * @return {@code this}
     */
    ConfigurableFuzzer save(String path);
}
