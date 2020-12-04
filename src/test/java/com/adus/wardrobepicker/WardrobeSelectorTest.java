package com.adus.wardrobepicker;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WardrobeSelectorTest {

    @Test
    void testDegenerateCase() {
        // given
        int days = 2;
        int nTops = 2;
        int nBottoms = 1;

        List<Pair<Integer, Integer>> topsFreshness = List.of(
                Pair.of(1, 1),
                Pair.of(1, 1)
        );
        List<Pair<Integer, Integer>> bottomsFreshness = List.of(
                Pair.of(1, 2)
        );
        List<Pair<Integer, Integer>> matchingPairs = List.of(
                Pair.of(1, 1),
                Pair.of(2, 1)
        );

        // when
        WardrobeSelector.Output wardrobeSelection = new WardrobeSelector()
                .select(days, nTops, nBottoms, topsFreshness, bottomsFreshness, matchingPairs);

        // then
        assertEquals(
                "WardrobeSelector.Output" +
                        "(" +
                        "assignmentOfTheDays=" +
                        "[" +
                        "WardrobeSelector.Output.AssignmentOfTheDay(day=1, top=1, bottom=1), " +
                        "WardrobeSelector.Output.AssignmentOfTheDay(day=2, top=2, bottom=1)" +
                        "]" +
                        ")",
                wardrobeSelection.toString());
    }

    @Test
    void testMixCase() {
        // given
        int days = 3;
        int nTops = 3;
        int nBottoms = 2;

        List<Pair<Integer, Integer>> topsFreshness = List.of(
                Pair.of(1, 2),
                Pair.of(2, 2),
                Pair.of(3, 2)
        );
        List<Pair<Integer, Integer>> bottomsFreshness = List.of(
                Pair.of(1, 2),
                Pair.of(2, 2)
        );
        List<Pair<Integer, Integer>> matchingPairs = List.of(
                Pair.of(1, 1),
                Pair.of(2, 1), Pair.of(2, 2),
                Pair.of(3, 1), Pair.of(3, 2));

        // when
        WardrobeSelector.Output wardrobeSelection = new WardrobeSelector()
                .select(days, nTops, nBottoms, topsFreshness, bottomsFreshness, matchingPairs);

        // then
        assertEquals(
                "WardrobeSelector.Output" +
                        "(" +
                        "assignmentOfTheDays=" +
                        "[" +
                        "WardrobeSelector.Output.AssignmentOfTheDay(day=1, top=1, bottom=1), " +
                        "WardrobeSelector.Output.AssignmentOfTheDay(day=2, top=3, bottom=1), " +
                        "WardrobeSelector.Output.AssignmentOfTheDay(day=3, top=2, bottom=2)" +
                        "]" +
                        ")",
                wardrobeSelection.toString());
    }

    @Test
    void testMixCase_daysEqualsItemCount() {
        // given
        int days = 5;
        int nTops = 5;
        int nBottoms = 2;

        List<Pair<Integer, Integer>> topsFreshness = List.of(
                Pair.of(1, 1),
                Pair.of(2, 1),
                Pair.of(3, 1),
                Pair.of(4, 1),
                Pair.of(5, 1)
        );
        List<Pair<Integer, Integer>> bottomsFreshness = List.of(
                Pair.of(1, 4),
                Pair.of(2, 3)
        );
        List<Pair<Integer, Integer>> matchingPairs = List.of(
                Pair.of(1, 1),
                Pair.of(2, 1),
                Pair.of(3, 1),
                Pair.of(4, 2),
                Pair.of(5, 2)
        );

        // when
        WardrobeSelector.Output wardrobeSelection = new WardrobeSelector()
                .select(days, nTops, nBottoms, topsFreshness, bottomsFreshness, matchingPairs);

        // then
        assertEquals(
                "WardrobeSelector.Output" +
                        "(" +
                        "assignmentOfTheDays=" +
                        "[" +
                        "WardrobeSelector.Output.AssignmentOfTheDay(day=1, top=3, bottom=1), " +
                        "WardrobeSelector.Output.AssignmentOfTheDay(day=2, top=2, bottom=1), " +
                        "WardrobeSelector.Output.AssignmentOfTheDay(day=3, top=5, bottom=2), " +
                        "WardrobeSelector.Output.AssignmentOfTheDay(day=4, top=4, bottom=2), " +
                        "WardrobeSelector.Output.AssignmentOfTheDay(day=5, top=1, bottom=1)" +
                        "]" +
                        ")",
                wardrobeSelection.toString());
    }

    @Test
    void testMixCase_daysLessThanItemCount() {
        // given
        int days = 4;
        int nTops = 5;
        int nBottoms = 2;

        List<Pair<Integer, Integer>> topsFreshness = List.of(
                Pair.of(1, 1),
                Pair.of(2, 1),
                Pair.of(3, 1),
                Pair.of(4, 1),
                Pair.of(5, 1)
        );
        List<Pair<Integer, Integer>> bottomsFreshness = List.of(
                Pair.of(1, 4),
                Pair.of(2, 3)
        );
        List<Pair<Integer, Integer>> matchingPairs = List.of(
                Pair.of(1, 1),
                Pair.of(2, 1),
                Pair.of(3, 1),
                Pair.of(4, 2),
                Pair.of(5, 2)
        );

        // when
        WardrobeSelector.Output wardrobeSelection = new WardrobeSelector()
                .select(days, nTops, nBottoms, topsFreshness, bottomsFreshness, matchingPairs);

        // then
        assertEquals(
                "WardrobeSelector.Output" +
                        "(" +
                        "assignmentOfTheDays=" +
                        "[" +
                        "WardrobeSelector.Output.AssignmentOfTheDay(day=1, top=5, bottom=2), " +
                        "WardrobeSelector.Output.AssignmentOfTheDay(day=2, top=3, bottom=1), " +
                        "WardrobeSelector.Output.AssignmentOfTheDay(day=3, top=2, bottom=1), " +
                        "WardrobeSelector.Output.AssignmentOfTheDay(day=4, top=4, bottom=2)" +
                        "]" +
                        ")",
                wardrobeSelection.toString());
    }

    @Test
    void testInfeasibleCase() {
        // given
        int days = 2;
        int nTops = 2;
        int nBottoms = 1;

        List<Pair<Integer, Integer>> topsFreshness = List.of(
                Pair.of(1, 1),
                Pair.of(1, 1)
        );
        List<Pair<Integer, Integer>> bottomsFreshness = List.of(
                Pair.of(1, 1)
        );
        List<Pair<Integer, Integer>> matchingPairs = List.of(
                Pair.of(1, 1),
                Pair.of(2, 1)
        );

        // when
        WardrobeSelector.Output wardrobeSelection = new WardrobeSelector()
                .select(days, nTops, nBottoms, topsFreshness, bottomsFreshness, matchingPairs);

        // then
        assertNull(wardrobeSelection);
    }

}