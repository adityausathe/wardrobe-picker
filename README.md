# Wardrobe Picker 
### :heavy_check_mark: Working

## Information
Given information about available apparels, the tool assigns them to the specified days. It views this task as a combinatorial optimization problem and employs Constraint-Programming based modeling to come up with a solution.     
## Functionality
- The tool collects information about apparels like- their availability, kinds, matching-pairs, laundry requirements, etc
- It then uses this information to come up with a wardrobe choice for each day.
- It tries to maximize the variety in the wardrobe-selection, while honoring the enforced restrictions.
## Implementation 
- The tool uses *choco-solver - a constraint-programming library* to implement the model and solve it. 

## Problem Modeling

## Dependencies
- JDK 9+
- Gradle