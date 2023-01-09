package com.vaticle.typeql.lang.pattern.variable.builder;

import com.vaticle.typeql.lang.pattern.Predicate;
import com.vaticle.typeql.lang.pattern.constraint.ValueConstraint;
import com.vaticle.typeql.lang.pattern.variable.ValueVariable;

public interface ValueVariableBuilder extends PredicateBuilder<ValueVariable> {

    default ValueVariable assign(ValueConstraint.Assignment.Expression assignment) {
        return constrain(new ValueConstraint.Assignment(assignment));
    }

    default ValueVariable constrain(Predicate<?> predicate) {
        return constrain(new ValueConstraint.Predicate(predicate));
    }

    ValueVariable constrain(ValueConstraint.Predicate constraint);

    public ValueVariable constrain(ValueConstraint.Assignment constraint);
}
