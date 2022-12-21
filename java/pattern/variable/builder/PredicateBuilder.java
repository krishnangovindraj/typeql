package com.vaticle.typeql.lang.pattern.variable.builder;

import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.pattern.expression.Predicate;
import com.vaticle.typeql.lang.pattern.variable.BoundVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundEvaluableVariable;
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

public interface PredicateBuilder<VAR_TYPE extends BoundVariable> {
    default VAR_TYPE eq(long value) {
        return eq(Predicate.Long::new, value);
    }

    default VAR_TYPE eq(double value) {
        return eq(Predicate.Double::new, value);
    }

    default VAR_TYPE eq(boolean value) {
        return eq(Predicate.Boolean::new, value);
    }

    default VAR_TYPE eq(String value) {
        return eq(Predicate.String::new, value);
    }

    default VAR_TYPE eq(LocalDateTime value) {
        return eq(Predicate.DateTime::new, value);
    }

    default VAR_TYPE eq(UnboundVariable variable) {
        return eq(Predicate.Variable::new, variable.toThing());
    }

    default VAR_TYPE eq(UnboundEvaluableVariable variable) {
        return eq(Predicate.ValueVariable::new, variable.toEvaluable());
    }

    default <T> VAR_TYPE eq(BiFunction<TypeQLToken.Predicate.Equality, T, Predicate<T>> constructor, T value) {
        return constrain(constructor.apply(EQ, value));
    }

    // Attribute value inequality constraint
    default VAR_TYPE neq(long value) {
        return neq(Predicate.Long::new, value);
    }

    default VAR_TYPE neq(double value) {
        return neq(Predicate.Double::new, value);
    }

    default VAR_TYPE neq(boolean value) {
        return neq(Predicate.Boolean::new, value);
    }

    default VAR_TYPE neq(String value) {
        return neq(Predicate.String::new, value);
    }

    default VAR_TYPE neq(LocalDateTime value) {
        return neq(Predicate.DateTime::new, value);
    }

    default VAR_TYPE neq(UnboundVariable variable) {
        return neq(Predicate.Variable::new, variable.toThing());
    }
    default VAR_TYPE neq(UnboundEvaluableVariable variable) {
        return neq(Predicate.ValueVariable::new, variable.toEvaluable());
    }

    default <T> VAR_TYPE neq(BiFunction<TypeQLToken.Predicate.Equality, T, Predicate<T>> constructor, T value) {
        return constrain(constructor.apply(NEQ, value));
    }

    // Attribute value greater-than constraint

    default VAR_TYPE gt(long value) {
        return gt(Predicate.Long::new, value);
    }

    default VAR_TYPE gt(double value) {
        return gt(Predicate.Double::new, value);
    }

    default VAR_TYPE gt(boolean value) {
        return gt(Predicate.Boolean::new, value);
    }

    default VAR_TYPE gt(String value) {
        return gt(Predicate.String::new, value);
    }

    default VAR_TYPE gt(LocalDateTime value) {
        return gt(Predicate.DateTime::new, value);
    }

    default VAR_TYPE gt(UnboundVariable variable) {
        return gt(Predicate.Variable::new, variable.toThing());
    }

    default VAR_TYPE gt(UnboundEvaluableVariable variable) {
        return gt(Predicate.ValueVariable::new, variable.toEvaluable());
    }


    default <T> VAR_TYPE gt(BiFunction<TypeQLToken.Predicate.Equality, T, Predicate<T>> constructor, T value) {
        return constrain(constructor.apply(GT, value));
    }

    // Attribute value greater-than-or-equals constraint

    default VAR_TYPE gte(long value) {
        return gte(Predicate.Long::new, value);
    }

    default VAR_TYPE gte(double value) {
        return gte(Predicate.Double::new, value);
    }

    default VAR_TYPE gte(boolean value) {
        return gte(Predicate.Boolean::new, value);
    }

    default VAR_TYPE gte(String value) {
        return gte(Predicate.String::new, value);
    }

    default VAR_TYPE gte(LocalDateTime value) {
        return gte(Predicate.DateTime::new, value);
    }

    default VAR_TYPE gte(UnboundVariable variable) {
        return gte(Predicate.Variable::new, variable.toThing());
    }

    default VAR_TYPE gte(UnboundEvaluableVariable variable) {
        return gte(Predicate.ValueVariable::new, variable.toEvaluable());
    }

    default <T> VAR_TYPE gte(BiFunction<TypeQLToken.Predicate.Equality, T, Predicate<T>> constructor, T value) {
        return constrain(constructor.apply(GTE, value));
    }

    // Attribute value less-than constraint

    default VAR_TYPE lt(long value) {
        return lt(Predicate.Long::new, value);
    }

    default VAR_TYPE lt(double value) {
        return lt(Predicate.Double::new, value);
    }

    default VAR_TYPE lt(boolean value) {
        return lt(Predicate.Boolean::new, value);
    }

    default VAR_TYPE lt(String value) {
        return lt(Predicate.String::new, value);
    }

    default VAR_TYPE lt(LocalDateTime value) {
        return lt(Predicate.DateTime::new, value);
    }


    default VAR_TYPE lt(UnboundVariable variable) {
        return lt(Predicate.Variable::new, variable.toThing());
    }

    default VAR_TYPE lt(UnboundEvaluableVariable variable) {
        return lt(Predicate.ValueVariable::new, variable.toEvaluable());
    }

    default <T> VAR_TYPE lt(BiFunction<TypeQLToken.Predicate.Equality, T, Predicate<T>> constructor, T value) {
        return constrain(constructor.apply(LT, value));
    }

    // Attribute value less-than-or-equals constraint

    default VAR_TYPE lte(long value) {
        return lte(Predicate.Long::new, value);
    }

    default VAR_TYPE lte(double value) {
        return lte(Predicate.Double::new, value);
    }

    default VAR_TYPE lte(boolean value) {
        return lte(Predicate.Boolean::new, value);
    }

    default VAR_TYPE lte(String value) {
        return lte(Predicate.String::new, value);
    }

    default VAR_TYPE lte(LocalDateTime value) {
        return lte(Predicate.DateTime::new, value);
    }

    default VAR_TYPE lte(UnboundVariable variable) {
        return lte(Predicate.Variable::new, variable.toThing());
    }

    default VAR_TYPE lte(UnboundEvaluableVariable variable) {
        return lte(Predicate.ValueVariable::new, variable.toEvaluable());
    }

    default <T> VAR_TYPE lte(BiFunction<TypeQLToken.Predicate.Equality, T, Predicate<T>> constructor, T value) {
        return constrain(constructor.apply(LTE, value));
    }

    default VAR_TYPE contains(String value) {
        return constrain(new Predicate.String(CONTAINS, value));
    }

    default VAR_TYPE like(String regex) {
        return constrain(new Predicate.String(LIKE, regex));
    }

    VAR_TYPE constrain(Predicate<?> predicate);
}
