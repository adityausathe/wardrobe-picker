package com.adus.wardrobepicker;

import lombok.*;

import java.util.List;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WardrobeSelection {

    private List<AssignmentOfTheDay> assignmentOfTheDays;

    @ToString
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AssignmentOfTheDay {
        int day;
        String top;
        String bottom;
    }
}
