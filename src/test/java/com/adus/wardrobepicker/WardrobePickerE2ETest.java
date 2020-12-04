package com.adus.wardrobepicker;

import org.junit.jupiter.api.Test;

import static com.adus.wardrobepicker.TestUtil.*;
import static com.adus.wardrobepicker.WardrobeCloset.createMatch;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WardrobePickerE2ETest {
    @Test
    void testE2EIntegration() {
        WardrobeCloset wardrobeCloset = new WardrobeCloset()
                .addTop(RED_T_SHIRT).withFreshness(2)
                .addTop(GREEN_SHIRT).withFreshness(2)
                .addTop(BLUE_KURTEE).withFreshness(2)
                .addBottom(CARGO_SHORTS).withFreshness(2)
                .addBottom(KHAKEE_JEANS).withFreshness(2)
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

        WardrobeSelector.Output selectorOutput = new WardrobeSelector()
                .select(3, wardrobeCloset.getInputForSelector());

        WardrobeSelection wardrobeSelection = wardrobeCloset.hydrateSelectorOutput(selectorOutput);

        assertEquals("WardrobeSelection" +
                "(" +
                "assignmentOfTheDays=" +
                "[" +
                "WardrobeSelection.AssignmentOfTheDay(day=1, top=Red T-Shirt, bottom=Cargo Shorts), " +
                "WardrobeSelection.AssignmentOfTheDay(day=2, top=Green Shirt, bottom=Cargo Shorts), " +
                "WardrobeSelection.AssignmentOfTheDay(day=3, top=Blue Kurtee, bottom=Khakee Jeans)" +
                "]" +
                ")", wardrobeSelection.toString());
    }
}
