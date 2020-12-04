package com.adus.wardrobepicker;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WardrobeCloset {
    private final Map<ItemKind, AtomicInteger> idGenerators;
    private final Map<String, ClothingItem> clothingItems;
    private final List<List<ClothingItem>> clothingMatchTuples;

    public WardrobeCloset() {
        this.idGenerators = Arrays.stream(ItemKind.values())
                .collect(Collectors.toMap(Function.identity(), ign -> new AtomicInteger(1)));
        this.clothingItems = new LinkedHashMap<>();
        this.clothingMatchTuples = new ArrayList<>();
    }

    public ClothingItem addTop(String name) {
        ClothingItem top = new ClothingItem(name, ItemKind.TOP);
        this.clothingItems.put(name, top);
        return top;
    }

    public ClothingItem addBottom(String name) {
        ClothingItem bottom = new ClothingItem(name, ItemKind.BOTTOM);
        this.clothingItems.put(name, bottom);
        return bottom;
    }

    public static ClothingMatch createMatch() {
        return new ClothingMatch();
    }

    public WardrobeCloset addMatch(ClothingMatch clothingMatch) {
        List<ClothingItem> matchingTuple = clothingMatch.getItems()
                .stream()
                .map(this.clothingItems::get)
                .peek(item -> Objects.requireNonNull(item, "'" + item + "' not found in closet!"))
                .collect(Collectors.toList());
        long itemKindsInMatchingTuple = matchingTuple.stream()
                .map(ClothingItem::getItemKind).distinct()
                .count();
        if (itemKindsInMatchingTuple < 2) {
            throw new IllegalArgumentException("At least two kinds of clothing-items are needed to create a match");
        }
        this.clothingMatchTuples.add(matchingTuple);
        return this;
    }

    public WardrobeSelector.Input getInputForSelector() {
        var itemKindWiseClothing = clothingItems.values()
                .stream()
                .collect(Collectors.groupingBy(ClothingItem::getItemKind,
                        Collectors.mapping(item -> Pair.of(item.getId(), item.getFreshness()), Collectors.toList())
                ));

        List<Pair<Integer, Integer>> matchingTuples = exportMatchingTuples();

        return new WardrobeSelector.Input(
                itemKindWiseClothing.get(ItemKind.TOP).size(),
                itemKindWiseClothing.get(ItemKind.BOTTOM).size(),
                itemKindWiseClothing.get(ItemKind.TOP),
                itemKindWiseClothing.get(ItemKind.BOTTOM),
                matchingTuples
        );
    }

    public WardrobeSelection hydrateSelectorOutput(WardrobeSelector.Output selectorOutput) {
        var idToNameMappings = this.clothingItems.values()
                .stream()
                .collect(Collectors.toMap(item -> Pair.of(item.getItemKind(), item.getId()), ClothingItem::getName));
        var assignmentOfTheDays = selectorOutput.getAssignmentOfTheDays()
                .stream()
                .map(daysAssignment -> new WardrobeSelection.AssignmentOfTheDay(
                        daysAssignment.getDay(),
                        idToNameMappings.get(Pair.of(ItemKind.TOP, daysAssignment.getTop())),
                        idToNameMappings.get(Pair.of(ItemKind.BOTTOM, daysAssignment.getBottom()))
                ))
                .collect(Collectors.toList());
        return new WardrobeSelection(assignmentOfTheDays);
    }

    private List<Pair<Integer, Integer>> exportMatchingTuples() {
        return clothingMatchTuples
                .stream()
                .flatMap(matchingTuple ->
                        {
                            var kindBasedMatchingTupleSplit = matchingTuple.stream()
                                    .collect(Collectors.groupingBy(ClothingItem::getItemKind));
                            var topsInTuple = kindBasedMatchingTupleSplit.get(ItemKind.TOP);
                            var bottomsInTuple = kindBasedMatchingTupleSplit.get(ItemKind.BOTTOM);
                            return topsInTuple.stream()
                                    .flatMap(top -> bottomsInTuple.stream()
                                            .map(bottom -> Pair.of(top.getId(), bottom.getId()))
                                    );
                        }
                )
                .collect(Collectors.toList());
    }

    public static class ClothingMatch {
        private final List<String> clothingItems = new ArrayList<>();

        public ClothingMatch top(String name) {
            return addItem(name);
        }

        public ClothingMatch bottom(String name) {
            return addItem(name);
        }

        public ClothingMatch matchWith() {
            return this;
        }

        public ClothingMatch and() {
            return this;
        }

        private ClothingMatch addItem(String item) {
            this.clothingItems.add(item);
            return this;
        }

        private List<String> getItems() {
            return this.clothingItems;
        }
    }

    enum ItemKind {
        TOP,
        BOTTOM
    }

    @Getter(AccessLevel.PRIVATE)
    public class ClothingItem {
        private final int id;
        private final ItemKind itemKind;
        private final String name;
        private int freshness;

        public ClothingItem(String name, ItemKind itemKind) {
            this.id = WardrobeCloset.this.idGenerators.get(itemKind).getAndIncrement();
            this.name = name;
            this.itemKind = itemKind;
        }

        public WardrobeCloset withFreshness(int days) {
            this.freshness = days;
            return WardrobeCloset.this;
        }
    }
}
