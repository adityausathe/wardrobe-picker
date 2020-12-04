package com.adus.wardrobepicker;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.adus.wardrobepicker.TestUtil.*;
import static com.adus.wardrobepicker.WardrobeCloset.createMatch;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WardrobeClosetTest {

    @Test
    void composeCloset_HappyPath() {
        WardrobeCloset wardrobeCloset = new WardrobeCloset()
                .addTop(RED_T_SHIRT).withFreshness(3)
                .addTop(GREEN_SHIRT).withFreshness(2)
                .addTop(BLUE_KURTEE).withFreshness(1)
                .addBottom(CARGO_SHORTS).withFreshness(4)
                .addBottom(KHAKEE_JEANS).withFreshness(3)
                .addMatch(createMatch()
                        .top(BLUE_KURTEE)
                        .matchWith()
                        .bottom(KHAKEE_JEANS)
                )
                .addMatch(createMatch()
                        .top(RED_T_SHIRT).and().top(GREEN_SHIRT)
                        .matchWith()
                        .bottom(CARGO_SHORTS).and().bottom(KHAKEE_JEANS)
                );

        assertEquals("WardrobeSelector.Input" +
                "(" +
                "nTops=3, " +
                "nBottoms=2, " +
                "topsFreshness=[(1,3), (2,2), (3,1)], " +
                "bottomsFreshness=[(1,4), (2,3)], " +
                "matchingPairs=[(3,2), (1,1), (1,2), (2,1), (2,2)]" +
                ")", wardrobeCloset.getInputForSelector().toString());
    }

    @Test
    void composeCloset_matchBetweenItemsOfSameKindIsNotAllowed() {
        assertThrows(IllegalArgumentException.class, () -> new WardrobeCloset()
                .addTop(RED_T_SHIRT).withFreshness(3)
                .addTop(GREEN_SHIRT).withFreshness(2)
                .addTop(BLUE_KURTEE).withFreshness(1)
                .addBottom(CARGO_SHORTS).withFreshness(4)
                .addBottom(KHAKEE_JEANS).withFreshness(3)
                .addMatch(createMatch()
                        .top(BLUE_KURTEE)
                        .matchWith()
                        .top(GREEN_SHIRT)
                ));
    }

    @Test
    void hydratesSelectorOutput() {
        WardrobeCloset wardrobeCloset = new WardrobeCloset()
                .addTop(RED_T_SHIRT).withFreshness(3)
                .addTop(GREEN_SHIRT).withFreshness(2)
                .addTop(BLUE_KURTEE).withFreshness(1)
                .addBottom(CARGO_SHORTS).withFreshness(4)
                .addBottom(KHAKEE_JEANS).withFreshness(3)
                .addMatch(createMatch()
                        .top(BLUE_KURTEE)
                        .matchWith()
                        .bottom(KHAKEE_JEANS)
                )
                .addMatch(createMatch()
                        .top(RED_T_SHIRT).and().top(GREEN_SHIRT)
                        .matchWith()
                        .bottom(CARGO_SHORTS).and().bottom(KHAKEE_JEANS)
                );

        WardrobeSelector.Output selectorOutput = new WardrobeSelector.Output(List.of(
                new WardrobeSelector.Output.AssignmentOfTheDay(1, 1, 1),
                new WardrobeSelector.Output.AssignmentOfTheDay(2, 2, 2)
        ));
        WardrobeSelection wardrobeSelection = wardrobeCloset.hydrateSelectorOutput(selectorOutput);
        assertEquals("WardrobeSelection" +
                        "(" +
                        "assignmentOfTheDays=" +
                        "[" +
                        "WardrobeSelection.AssignmentOfTheDay(day=1, top=Red T-Shirt, bottom=Cargo Shorts), " +
                        "WardrobeSelection.AssignmentOfTheDay(day=2, top=Green Shirt, bottom=Khakee Jeans)" +
                        "]" +
                        ")",
                wardrobeSelection.toString());
    }
}