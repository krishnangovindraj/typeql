package com.vaticle.typeql.lang.pattern.variable.builder;

import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.pattern.Pattern;
import com.vaticle.typeql.lang.pattern.constraint.EvaluableConstraint;
import com.vaticle.typeql.lang.pattern.expression.EvaluableExpression;
import com.vaticle.typeql.lang.pattern.variable.EvaluableVariable;
import com.vaticle.typeql.lang.pattern.variable.Reference;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;

import java.time.LocalDateTime;
import java.util.function.BiFunction;

import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.EQ;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.GT;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.GTE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.LT;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.LTE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.NEQ;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.SubString.CONTAINS;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.SubString.LIKE;

public class EvaluableVariableBuilder {

    private final Reference.NamedVal reference;

    public EvaluableVariableBuilder(Reference.NamedVal reference) {
        this.reference = reference;
    }

    public EvaluableVariable eq(long value) {
        return eq(EvaluableConstraint.Value.Long::new, value);
    }

    public EvaluableVariable eq(double value) {
        return eq(EvaluableConstraint.Value.Double::new, value);
    }

    public EvaluableVariable eq(boolean value) {
        return eq(EvaluableConstraint.Value.Boolean::new, value);
    }

    public EvaluableVariable eq(String value) {
        return eq(EvaluableConstraint.Value.String::new, value);
    }

    public EvaluableVariable eq(LocalDateTime value) {
        return eq(EvaluableConstraint.Value.DateTime::new, value);
    }

    public EvaluableVariable eq(UnboundVariable variable) {
        return constrain(new EvaluableConstraint.Value.Variable(EQ, variable));
    }

    public EvaluableVariable eq(EvaluableVariable variable) {
        return constrain(new EvaluableConstraint.Value.ValueVariable(EQ, variable));
    }

    public EvaluableVariable eq(EvaluableExpression expression) {
        return constrain(new EvaluableConstraint.Value.Expression(EQ, expression));
    }

    public <T> EvaluableVariable eq(BiFunction<TypeQLToken.Predicate.Equality, T, EvaluableConstraint.Value<T>> constructor, T value) {
        return constrain(constructor.apply(EQ, value));
    }

    // Attribute value inequality constraint

    public EvaluableVariable neq(long value) {
        return neq(EvaluableConstraint.Value.Long::new, value);
    }

    public EvaluableVariable neq(double value) {
        return neq(EvaluableConstraint.Value.Double::new, value);
    }

    public EvaluableVariable neq(boolean value) {
        return neq(EvaluableConstraint.Value.Boolean::new, value);
    }

    public EvaluableVariable neq(String value) {
        return neq(EvaluableConstraint.Value.String::new, value);
    }

    public EvaluableVariable neq(LocalDateTime value) {
        return neq(EvaluableConstraint.Value.DateTime::new, value);
    }

    public EvaluableVariable neq(UnboundVariable variable) {
        return constrain(new EvaluableConstraint.Value.Variable(NEQ, variable));
    }

    public EvaluableVariable neq(EvaluableVariable variable) {
        return constrain(new EvaluableConstraint.Value.ValueVariable(NEQ, variable));
    }

    public EvaluableVariable neq(EvaluableExpression expression) {
        return constrain(new EvaluableConstraint.Value.Expression(NEQ, expression));
    }

    public <T> EvaluableVariable neq(BiFunction<TypeQLToken.Predicate.Equality, T, EvaluableConstraint.Value<T>> constructor, T value) {
        return constrain(constructor.apply(NEQ, value));
    }

    // Attribute value greater-than constraint

    public EvaluableVariable gt(long value) {
        return gt(EvaluableConstraint.Value.Long::new, value);
    }

    public EvaluableVariable gt(double value) {
        return gt(EvaluableConstraint.Value.Double::new, value);
    }

    public EvaluableVariable gt(boolean value) {
        return gt(EvaluableConstraint.Value.Boolean::new, value);
    }

    public EvaluableVariable gt(String value) {
        return gt(EvaluableConstraint.Value.String::new, value);
    }

    public EvaluableVariable gt(LocalDateTime value) {
        return gt(EvaluableConstraint.Value.DateTime::new, value);
    }

    public EvaluableVariable gt(UnboundVariable variable) {
        return constrain(new EvaluableConstraint.Value.Variable(GT, variable));
    }

    public EvaluableVariable gt(EvaluableVariable variable) {
        return constrain(new EvaluableConstraint.Value.ValueVariable(GT, variable));
    }

    public EvaluableVariable gt(EvaluableExpression expression) {
        return constrain(new EvaluableConstraint.Value.Expression(GT, expression));
    }

    public <T> EvaluableVariable gt(BiFunction<TypeQLToken.Predicate.Equality, T, EvaluableConstraint.Value<T>> constructor, T value) {
        return constrain(constructor.apply(GT, value));
    }

    // Attribute value greater-than-or-equals constraint

    public EvaluableVariable gte(long value) {
        return gte(EvaluableConstraint.Value.Long::new, value);
    }

    public EvaluableVariable gte(double value) {
        return gte(EvaluableConstraint.Value.Double::new, value);
    }

    public EvaluableVariable gte(boolean value) {
        return gte(EvaluableConstraint.Value.Boolean::new, value);
    }

    public EvaluableVariable gte(String value) {
        return gte(EvaluableConstraint.Value.String::new, value);
    }

    public EvaluableVariable gte(LocalDateTime value) {
        return gte(EvaluableConstraint.Value.DateTime::new, value);
    }

    public EvaluableVariable gte(UnboundVariable variable) {
        return constrain(new EvaluableConstraint.Value.Variable(GTE, variable));
    }

    public EvaluableVariable gte(EvaluableVariable variable) {
        return constrain(new EvaluableConstraint.Value.ValueVariable(GTE, variable));
    }

    public EvaluableVariable gte(EvaluableExpression expression) {
        return constrain(new EvaluableConstraint.Value.Expression(GTE, expression));
    }

    public <T> EvaluableVariable gte(BiFunction<TypeQLToken.Predicate.Equality, T, EvaluableConstraint.Value<T>> constructor, T value) {
        return constrain(constructor.apply(GTE, value));
    }

    // Attribute value less-than constraint

    public EvaluableVariable lt(long value) {
        return lt(EvaluableConstraint.Value.Long::new, value);
    }

    public EvaluableVariable lt(double value) {
        return lt(EvaluableConstraint.Value.Double::new, value);
    }

    public EvaluableVariable lt(boolean value) {
        return lt(EvaluableConstraint.Value.Boolean::new, value);
    }

    public EvaluableVariable lt(String value) {
        return lt(EvaluableConstraint.Value.String::new, value);
    }

    public EvaluableVariable lt(LocalDateTime value) {
        return lt(EvaluableConstraint.Value.DateTime::new, value);
    }

    public EvaluableVariable lt(UnboundVariable variable) {
        return constrain(new EvaluableConstraint.Value.Variable(LT, variable));
    }

    public EvaluableVariable lt(EvaluableVariable variable) {
        return constrain(new EvaluableConstraint.Value.ValueVariable(LT, variable));
    }

    public EvaluableVariable lt(EvaluableExpression expression) {
        return constrain(new EvaluableConstraint.Value.Expression(LT, expression));
    }

    public <T> EvaluableVariable lt(BiFunction<TypeQLToken.Predicate.Equality, T, EvaluableConstraint.Value<T>> constructor, T value) {
        return constrain(constructor.apply(LT, value));
    }

    // Attribute value less-than-or-equals constraint

    public EvaluableVariable lte(long value) {
        return lte(EvaluableConstraint.Value.Long::new, value);
    }

    public EvaluableVariable lte(double value) {
        return lte(EvaluableConstraint.Value.Double::new, value);
    }

    public EvaluableVariable lte(boolean value) {
        return lte(EvaluableConstraint.Value.Boolean::new, value);
    }

    public EvaluableVariable lte(String value) {
        return lte(EvaluableConstraint.Value.String::new, value);
    }

    public EvaluableVariable lte(LocalDateTime value) {
        return lte(EvaluableConstraint.Value.DateTime::new, value);
    }

    public EvaluableVariable lte(UnboundVariable variable) {
        return constrain(new EvaluableConstraint.Value.Variable(LTE, variable));
    }

    public EvaluableVariable lte(EvaluableVariable variable) {
        return constrain(new EvaluableConstraint.Value.ValueVariable(LTE, variable));
    }

    public EvaluableVariable lte(EvaluableExpression expression) {
        return constrain(new EvaluableConstraint.Value.Expression(LTE, expression));
    }

    public <T> EvaluableVariable lte(BiFunction<TypeQLToken.Predicate.Equality, T, EvaluableConstraint.Value<T>> constructor, T value) {
        return constrain(constructor.apply(LTE, value));
    }

    public EvaluableVariable contains(String value) {
        return constrain(new EvaluableConstraint.Value.String(CONTAINS, value));
    }

    public EvaluableVariable like(String regex) {
        return constrain(new EvaluableConstraint.Value.String(LIKE, regex));
    }

    public EvaluableVariable constrain(EvaluableConstraint.Value<?> constraint) {
        return new EvaluableVariable(reference, constraint);
    }

    public EvaluableVariable assign(EvaluableExpression.Operation expr) {
        return constrain(new EvaluableConstraint.Expression(expr));
    }

    public EvaluableVariable constrain(EvaluableConstraint.Expression constraint) {
        return new EvaluableVariable(reference, constraint);
    }

    public EvaluableVariable toEvaluable() {
        return new EvaluableVariable(reference);
    }
}
