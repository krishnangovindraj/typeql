package com.vaticle.typeql.lang.pattern.variable;

import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.constraint.EvaluableConstraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.vaticle.typeql.lang.common.exception.ErrorMessage.ILLEGAL_CONSTRAINT_REPETITION;

public class EvaluableVariable extends BoundVariable {
    private EvaluableConstraint assignment;
    private final List<EvaluableConstraint> constraints;

    public EvaluableVariable(Reference reference) {
        super(reference);
        this.constraints = new ArrayList<>();
    }

    public EvaluableVariable(Reference reference, EvaluableConstraint constraint) {
        this(reference);
        constrain(constraint);
    }

    public EvaluableConstraint evaluable() {
        return constraints.get(0);
    }

    public EvaluableVariable constrain(EvaluableConstraint constraint) {
        if (assignment != null) {throw TypeQLException.of(ILLEGAL_CONSTRAINT_REPETITION.message(reference, EvaluableConstraint.class, constraint));}
        this.assignment = constraint;
        this.constraints.add(constraint);
        return this;
    }

    @Override
    public List<EvaluableConstraint> constraints() {
        return constraints;
    }

    @Override
    public String toString(boolean pretty) {
        return reference().syntax() + " <- " + assignment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EvaluableVariable that = (EvaluableVariable) o;
        return (this.reference.equals(that.reference)); // TODO: // && set(this.constraints).equals(set(that.constraints)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference); //TODO: //, constraints);
    }
}
