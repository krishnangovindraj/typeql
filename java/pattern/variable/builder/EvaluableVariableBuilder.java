package com.vaticle.typeql.lang.pattern.variable.builder;

import com.vaticle.typeql.lang.pattern.constraint.EvaluableConstraint;
import com.vaticle.typeql.lang.pattern.variable.EvaluableVariable;

public interface EvaluableVariableBuilder {
    EvaluableVariable constrain(EvaluableConstraint constraint);
}
