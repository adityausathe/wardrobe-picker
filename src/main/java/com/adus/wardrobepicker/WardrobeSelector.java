package com.adus.wardrobepicker;

import com.codepoetics.protonpack.StreamUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.MathUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class WardrobeSelector {

    public WardrobeSelection select(int days, int nTops, int nBottoms,
                                    List<Pair<Integer, Integer>> topsFreshness, List<Pair<Integer, Integer>> bottomsFreshness,
                                    List<Pair<Integer, Integer>> matchingPairs) {
        Model model = new Model("Wardrobe Picking");

        IntVar[] tops = model.intVarArray("tops", days, 1, nTops);
        IntVar[] bottoms = model.intVarArray("bottoms", days, 1, nBottoms);

        // hard-constraints
        addMatchingPairsConstraint(days, matchingPairs, model, tops, bottoms);
        addFreshnessConstraint(days, topsFreshness, model, tops);
        addFreshnessConstraint(days, bottomsFreshness, model, bottoms);

        // soft-constraints to add variety in selection
        IntVar topL1Deviation = computeDeviationInItemAssignment(model, tops, nTops, days, "top");
        IntVar bottomL1Deviation = computeDeviationInItemAssignment(model, bottoms, nBottoms, days, "bottom");
        // give more preference to tops' variety
        IntVar totalL1Deviation = topL1Deviation.mul(2).add(bottomL1Deviation).intVar();

        // solve
        Solver solver = model.getSolver();
        solver.showShortStatistics();
        solver.limitTime("10s");
        Solution optimalSolution = solver.findOptimalSolution(totalL1Deviation, Model.MINIMIZE);
        if (optimalSolution != null) {
            log.debug("Solution found! Solution: " + optimalSolution);
            return prepareWardrobeSelection(tops, bottoms, optimalSolution);
        }
        log.debug("Solution not found!");
        return null;
    }

    private void addMatchingPairsConstraint(int days, List<Pair<Integer, Integer>> matchingPairs, Model model, IntVar[] topsOfTheDays, IntVar[] bottomsOfTheDays) {
        for (int i = 0; i < days; i++) {
            Tuples allowedPairs = new Tuples(true);
            for (Pair<Integer, Integer> pair : matchingPairs) {
                allowedPairs.add(pair.getLeft(), pair.getRight());
            }
            model.table(new IntVar[]{topsOfTheDays[i], bottomsOfTheDays[i]}, allowedPairs).post();
        }
        log.debug("Added {} tuples-constraints to encode {} matching-pairs requirement.", days, matchingPairs.size());
    }

    private void addFreshnessConstraint(int days, List<Pair<Integer, Integer>> topsFreshness, Model model, IntVar[] topsOfTheDays) {
        int constraintCount = 0;
        for (Pair<Integer, Integer> freshness : topsFreshness) {
            Integer item = freshness.getKey();
            Integer freshForDays = freshness.getValue();
            for (int startDay = 0; startDay < days - freshForDays; startDay++) {
                // Do not assign this item consecutively for more than "freshForDays" days
                model.not(model.count(item, ArrayUtils.subarray(topsOfTheDays, startDay, startDay + freshForDays + 1), model.intVar(freshForDays + 1))).post();
                constraintCount++;
            }
        }
        log.debug("Added {} constraints to encode freshness requirement.", constraintCount);
    }

    private IntVar computeDeviationInItemAssignment(Model model, IntVar[] itemsOfTheDays, int nItems, int days, String itemKind) {
        // find histogram
        IntVar[] itemFrequencies = model.intVarArray(itemKind + "Frequencies", nItems, 0, days);
        for (int itemId = 1; itemId <= nItems; itemId++) {
            model.count(itemId, itemsOfTheDays, itemFrequencies[itemId - 1]).post();
        }
        // find expected frequency
        int itemMeanFrequency = MathUtils.divCeil(days, nItems);

        // find deviation
        IntVar[] itemFrequenciesResidues = model.intVarArray(itemKind + "FrequenciesResidues", nItems, 0, days);
        for (int i = 0; i < itemFrequencies.length; i++) {
            itemFrequenciesResidues[i] = itemFrequencies[i].add(-itemMeanFrequency).abs().intVar();
        }
        IntVar itemL1Deviation = model.intVar(itemKind + "L1Deviation", 0, days);
        model.sum(itemFrequenciesResidues, "=", itemL1Deviation).post();
        return itemL1Deviation;
    }

    private WardrobeSelection prepareWardrobeSelection(IntVar[] tops, IntVar[] bottoms, Solution optimalSolution) {
        AtomicInteger dayCounter = new AtomicInteger(1);
        return new WardrobeSelection(
                StreamUtils.zip(Arrays.stream(tops), Arrays.stream(bottoms), Pair::of)
                        .map(pair -> new WardrobeSelection.AssignmentOfTheDay(
                                dayCounter.getAndIncrement(),
                                optimalSolution.getIntVal(pair.getLeft()),
                                optimalSolution.getIntVal(pair.getRight())))
                        .collect(Collectors.toList())
        );
    }
}
