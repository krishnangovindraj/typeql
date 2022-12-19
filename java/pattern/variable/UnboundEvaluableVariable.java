package com.vaticle.typeql.lang.pattern.variable;

import com.vaticle.typeql.lang.pattern.constraint.Constraint;
import com.vaticle.typeql.lang.pattern.constraint.EvaluableConstraint;
import com.vaticle.typeql.lang.pattern.expression.EvaluableExpression;
import com.vaticle.typeql.lang.pattern.expression.Predicate;
import com.vaticle.typeql.lang.pattern.variable.builder.PredicateBuilder;

import java.util.List;
public class UnboundEvaluableVariable extends UnboundVariable implements PredicateBuilder<EvaluableVariable> {
    private UnboundEvaluableVariable(Reference reference) {
        super(reference);
        assert reference.isNamedVal();
    }

    public static UnboundEvaluableVariable namedVal(String name) {
        return new UnboundEvaluableVariable(Reference.namedVal(name));
    }

    @Override
    public boolean isEvaluableVariable() {
        return true;
    }

    @Override
    public UnboundEvaluableVariable asEvaluableVariable() {
        return this;
    }

    public EvaluableVariable assign(EvaluableExpression assignment) {
        return constrain(new EvaluableConstraint.Expression(assignment));
    }

    public EvaluableVariable constrain(Predicate<?> predicate) {
        return constrain(new EvaluableConstraint.Value<>(predicate));
    }

    public EvaluableVariable constrain(EvaluableConstraint.Value<?> constraint) {
        return new EvaluableVariable(reference.asNamedVal(), constraint);
    }

    public EvaluableVariable constrain(EvaluableConstraint.Expression constraint) {
        return new EvaluableVariable(reference.asNamedVal(), constraint);
    }

    public EvaluableVariable toEvaluable() {
        return new EvaluableVariable(reference.asNamedVal());
    }

    @Override
    public List<? extends Constraint<?>> constraints() {
        return null;
    }

    @Override
    public String toString(boolean pretty) {
        return reference.syntax();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnboundEvaluableVariable that = (UnboundEvaluableVariable) o;
        return this.reference.equals(that.reference);
    }

    @Override
    public int hashCode() {
        return reference.hashCode();
    }
}
