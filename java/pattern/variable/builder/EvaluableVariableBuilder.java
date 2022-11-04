package com.vaticle.typeql.lang.pattern.variable.builder;

import com.vaticle.typeql.lang.pattern.constraint.EvaluableConstraint;
import com.vaticle.typeql.lang.pattern.constraint.ThingConstraint;
import com.vaticle.typeql.lang.pattern.variable.EvaluableVariable;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;

public interface EvaluableVariableBuilder {

    default EvaluableVariable assign(EvaluableConstraint.EvaluableExpression expr) {
        return constrain(new EvaluableConstraint(expr));
    }

    EvaluableVariable constrain(EvaluableConstraint constraint);
}
