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

package org.btrplace.bench;

import org.btrplace.json.JSONConverterException;
import org.btrplace.json.plan.ReconfigurationPlanConverter;
import org.btrplace.plan.ReconfigurationPlan;
import org.btrplace.scheduler.choco.ChocoScheduler;
import org.btrplace.scheduler.choco.DefaultChocoScheduler;
import org.btrplace.scheduler.choco.Parameters;
import org.btrplace.scheduler.choco.runner.SolvingStatistics;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

/**
 * Simple benching tool.
 * @author Fabien Hermenier
 */
public class Bench {

    /**
     * The CSV file that will contains the output statistics.
     */
    public static final String SCHEDULER_STATS = "scheduler.csv";

    public static void main(String[] args) throws IOException, JSONConverterException {
        Options opts = new Options();

        // Parse the cmdline arguments
        CmdLineParser cli = new CmdLineParser(opts);
        try {
            cli.getProperties().withUsageWidth(80);
            cli.parseArgument(args);
        } catch (CmdLineException ex) {
            System.err.println(ex.getMessage());
            cli.printUsage(System.err);
            System.exit(1);
        }

        Parameters ps = opts.parameters();
        ChocoScheduler s = new DefaultChocoScheduler().setParameters(ps);

        File output = opts.output();
        List<LabelledInstance> instances = opts.instances();
        int v = opts.verbosity();
        for (LabelledInstance i : instances) {

            s.solve(i);
            if (opts.single()) {
                System.out.println(s.getStatistics());
            } else {
                SolvingStatistics stats = s.getStatistics();
                if (v == 0) {
                    System.out.println(i.label + ": OK");
                } else if (v > 0) {
                    System.out.println("----- " + i.label + " -----");
                    System.out.println(stats);
                    System.out.println();
                }
                store(i, stats, output);
            }
        }

    }

    private static void store(LabelledInstance i, SolvingStatistics stats, File base) throws IOException, JSONConverterException {

        Files.createDirectories(base.toPath());
        //Stats about the solving process
        Path p = Paths.get(base.getAbsolutePath(), SCHEDULER_STATS);
        Files.write(p, Collections.singletonList(i.label + ";" + stats.toCSV()), UTF_8, CREATE, APPEND);
        ReconfigurationPlan best = stats.lastSolution();

        //The resulting plan
        if (best != null) {
            ReconfigurationPlanConverter c = new ReconfigurationPlanConverter();
            String path = base.getAbsolutePath() + File.separator + i.label + "-plan.json.gz";
            System.out.println(path);
            OutputStreamWriter out = new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(path)), UTF_8);
            c.toJSON(best, out);
            out.close();
        }
    }
}