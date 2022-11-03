package com.vaticle.typeql.lang.pattern.variable;

import com.vaticle.typeql.lang.pattern.constraint.Constraint;
import com.vaticle.typeql.lang.pattern.constraint.EvaluableConstraint;

import java.util.List;

import static com.vaticle.typedb.common.collection.Collections.set;

public class EvaluableVariable extends BoundVariable {

    private List<EvaluableConstraint> constraints;

    EvaluableVariable(Reference reference) {
        super(reference);
    }

    @Override
    public List<EvaluableConstraint> constraints() {
        return constraints;
    }

    @Override
    public String toString(boolean pretty) {
        return "TODO: EvaluableVariable::toString(boolean)";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EvaluableVariable that = (EvaluableVariable) o;
        return (this.reference.equals(that.reference) &&
                set(this.constraints).equals(set(that.constraints)));
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
