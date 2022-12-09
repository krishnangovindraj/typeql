package com.vaticle.typeql.lang.pattern.variable;

import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.constraint.EvaluableConstraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.ILLEGAL_CONSTRAINT_REPETITION;

public class EvaluableVariable extends BoundVariable {
    private EvaluableConstraint constraint;

    public EvaluableVariable(Reference.NamedVal reference) {
        super(reference);
        this.constraint = null;
    }

    public EvaluableVariable(Reference.NamedVal reference, EvaluableConstraint constraint) {
        this(reference);
        constrain(constraint);
    }

    public EvaluableVariable constrain(EvaluableConstraint constraint) {
        if (this.constraint != null) {throw TypeQLException.of(ILLEGAL_CONSTRAINT_REPETITION.message(reference, EvaluableConstraint.class, constraint));}
        this.constraint = constraint;
        return this;
    }

    @Override
    public List<EvaluableConstraint> constraints() {
        return list(constraint);
    }

    @Override
    public boolean isEvaluable() { return true; }

    @Override
    public EvaluableVariable asEvaluable() {
        return this;
    }

    @Override
    public String toString(boolean pretty) {
        return reference().syntax() + SPACE + constraint.toString();
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
