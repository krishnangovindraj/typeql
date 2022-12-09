package com.vaticle.typeql.lang.pattern.constraint;

import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.common.util.Strings;
import com.vaticle.typeql.lang.pattern.expression.EvaluableExpression;
import com.vaticle.typeql.lang.pattern.variable.BoundVariable;
import com.vaticle.typeql.lang.pattern.variable.EvaluableVariable;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.vaticle.typedb.common.collection.Collections.set;
import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.EQ;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.SubString.LIKE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CONSTRAINT_DATETIME_PRECISION;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MISSING_CONSTRAINT_PREDICATE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MISSING_CONSTRAINT_VALUE;
import static com.vaticle.typeql.lang.common.util.Strings.escapeRegex;
import static com.vaticle.typeql.lang.common.util.Strings.quoteString;

public abstract class EvaluableConstraint extends Constraint<BoundVariable> {

    @Override
    public boolean isEvaluable() {
        return true;
    }

    @Override
    public EvaluableConstraint asEvaluable() {
        return this;
    }

    public boolean isValue() {
        return false;
    }

    public EvaluableConstraint asValue() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(EvaluableConstraint.Value.class)));
    }

    public abstract static class Value<T> extends EvaluableConstraint {

        private final TypeQLToken.Predicate predicate;
        private final T value;
        private final int hash;

        Value(TypeQLToken.Predicate predicate, T value) {
            if (predicate == null) throw TypeQLException.of(MISSING_CONSTRAINT_PREDICATE);
            else if (value == null) throw TypeQLException.of(MISSING_CONSTRAINT_VALUE);

            assert !predicate.isEquality() || value instanceof Comparable
                    || value instanceof ThingVariable<?> || value instanceof EvaluableVariable
                    || value instanceof EvaluableExpression;
            assert !predicate.isSubString() || value instanceof java.lang.String;

            this.predicate = predicate;
            this.value = value;
            this.hash = Objects.hash(Value.class, this.predicate, this.value);
        }

        @Override
        public Set<BoundVariable> variables() {
            return set();
        }

        @Override
        public boolean isValue() {
            return true;
        }

        @Override
        public Value<?> asValue() {
            return this;
        }

        public TypeQLToken.Predicate predicate() {
            return predicate;
        }

        public T value() {
            return value;
        }

        public boolean isLong() {
            return false;
        }

        public boolean isDouble() {
            return false;
        }

        public boolean isBoolean() {
            return false;
        }

        public boolean isString() {
            return false;
        }

        public boolean isDateTime() {
            return false;
        }

        public boolean isVariable() {
            return false;
        }

        public boolean isValueVariable() {
            return false;
        }

        public Value.Long asLong() {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Value.Long.class)));
        }

        public Value.Double asDouble() {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Value.Double.class)));
        }

        public Value.Boolean asBoolean() {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Value.Boolean.class)));
        }

        public Value.String asString() {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Value.String.class)));
        }

        public Value.DateTime asDateTime() {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Value.DateTime.class)));
        }

        public Value.Variable asVariable() {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Value.Variable.class)));
        }

        public Value.ValueVariable asValueVariable() {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Value.ValueVariable.class)));
        }

        @Override
        public java.lang.String toString() {
            if (predicate.equals(EQ) && !isVariable()) return Strings.valueToString(value);
            else return predicate.toString() + SPACE + Strings.valueToString(value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Value<?> that = (Value<?>) o;
            return (this.predicate.equals(that.predicate) && this.value.equals(that.value));
        }

        @Override
        public int hashCode() {
            return hash;
        }

        public static class Long extends Value<java.lang.Long> {

            public Long(TypeQLToken.Predicate.Equality predicate, long value) {
                super(predicate, value);
            }

            @Override
            public boolean isLong() {
                return true;
            }

            @Override
            public Long asLong() {
                return this;
            }
        }

        public static class Double extends Value<java.lang.Double> {

            public Double(TypeQLToken.Predicate.Equality predicate, double value) {
                super(predicate, value);
            }

            @Override
            public boolean isDouble() {
                return true;
            }

            @Override
            public Double asDouble() {
                return this;
            }
        }

        public static class Boolean extends Value<java.lang.Boolean> {

            public Boolean(TypeQLToken.Predicate.Equality predicate, boolean value) {
                super(predicate, value);
            }

            @Override
            public boolean isBoolean() {
                return true;
            }

            @Override
            public Boolean asBoolean() {
                return this;
            }
        }

        public static class String extends Value<java.lang.String> {

            public String(TypeQLToken.Predicate predicate, java.lang.String value) {
                super(predicate, value);
            }

            @Override
            public boolean isString() {
                return true;
            }

            @Override
            public java.lang.String toString() {
                StringBuilder operation = new StringBuilder();

                if (predicate().equals(LIKE)) {
                    operation.append(LIKE).append(SPACE).append(quoteString(escapeRegex(value())));
                } else if (predicate().equals(EQ)) {
                    operation.append(quoteString(value()));
                } else {
                    operation.append(predicate()).append(SPACE).append(quoteString(value()));
                }

                return operation.toString();
            }

            @Override
            public String asString() {
                return this;
            }
        }

        public static class DateTime extends Value<LocalDateTime> {

            public DateTime(TypeQLToken.Predicate.Equality predicate, LocalDateTime value) {
                super(predicate, value);
                // validate precision of fractional seconds, which are stored as nanos in LocalDateTime
                int nanos = value.toLocalTime().getNano();
                final long nanosPerMilli = 1000000L;
                long remainder = nanos % nanosPerMilli;
                if (remainder != 0) {
                    throw TypeQLException.of(INVALID_CONSTRAINT_DATETIME_PRECISION.message(value));
                }
            }

            @Override
            public boolean isDateTime() {
                return true;
            }

            @Override
            public DateTime asDateTime() {
                return this;
            }
        }

        public static class Variable extends Value<ThingVariable<?>> {

            public Variable(TypeQLToken.Predicate.Equality predicate, UnboundVariable variable) {
                super(predicate, variable.toThing());
            }

            @Override
            public Set<BoundVariable> variables() {
                return set(value());
            }

            @Override
            public boolean isVariable() {
                return true;
            }

            @Override
            public Variable asVariable() {
                return this;
            }
        }

        public static class ValueVariable extends Value<EvaluableVariable> {

            public ValueVariable(TypeQLToken.Predicate.Equality predicate, EvaluableVariable variable) {
                super(predicate, variable);
            }

            @Override
            public Set<BoundVariable> variables() {
                return set(value());
            }

            @Override
            public boolean isValueVariable() {
                return true;
            }

            @Override
            public ValueVariable asValueVariable() {
                return this;
            }
        }

        public static class Expression extends Value<EvaluableExpression> {
            private final Set<BoundVariable> inputs;

            public Expression(TypeQLToken.Predicate predicate, EvaluableExpression expression) {
                super(predicate, expression);
                this.inputs = new HashSet<>(expression.variables());
            }

            @Override
            public Set<BoundVariable> variables() {
                return this.inputs;
            }

            @Override
            public java.lang.String toString() {
                return predicate().toString() + SPACE + this.value().toString();
            }
        }
    }
}
