package com.vaticle.typeql.lang.pattern.constraint;

import com.vaticle.typedb.common.collection.Pair;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.variable.ConceptVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typedb.common.collection.Collections.set;
import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COMMA;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;

public class EvaluableConstraint extends Constraint<ConceptVariable> {
    private final EvaluableExpression expression;
    private final Set<ConceptVariable> inputs;

    public EvaluableConstraint(EvaluableExpression expression) {
        this.expression = expression;
        this.inputs = new HashSet<>();
        expression.variables().forEach(v -> this.inputs.add(v.toConcept()));
    }

    public EvaluableExpression expression() { return expression; }

    @Override
    public Set<ConceptVariable> variables() {
        return inputs;
    }

    @Override
    public boolean isEvaluable() { return true; }

    @Override
    public EvaluableConstraint asEvaluable() {
        return this;
    }

    @Override
    public String toString() {
        return this.expression.toString();
    }

    public abstract static class EvaluableExpression {

        public boolean isOperation() { return false; }

        public boolean isFunction() { return false; }

        public boolean isBracketed() { return false; }

        public boolean isVariable() { return false; }

        public boolean isConstant() { return false; }

        public EvaluableExpression.Operation asOperation() {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(EvaluableExpression.Operation.class)));
        }

        public EvaluableExpression.Function asFunction() {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(EvaluableExpression.Function.class)));
        }

        public EvaluableExpression.Bracketed asBracketed() {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(EvaluableExpression.Bracketed.class)));
        }

        public EvaluableExpression.Variable asVariable() {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(EvaluableExpression.Variable.class)));
        }

        public EvaluableExpression.Constant asConstant() {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(EvaluableExpression.Constant.class)));
        }

        @Override
        public abstract String toString();

        public static EvaluableExpression.Operation op(EvaluableExpression.Operation.OP op, EvaluableExpression a, EvaluableExpression b) {
            return new EvaluableExpression.Operation(op, a, b);
        }

        public static Function func(String funcId, EvaluableExpression... args) {
            return func(funcId, list(args));
        }

        public static Function func(String funcId, List<EvaluableExpression> args) {
            return new Function(funcId, args);
        }

        public static Variable var(UnboundVariable variable) {
            return new Variable(variable);
        }

        public static Constant.Double constant(double value) {
            return new Constant.Double(value);
        }

        public static EvaluableExpression bracketed(EvaluableExpression nestedExpr) {
            return new EvaluableExpression.Bracketed(nestedExpr);
        }

        public Set<UnboundVariable> variables() {
            Set<UnboundVariable> collector = new HashSet<>();
            collectVariables(collector);
            return collector;
        }

        protected abstract void collectVariables(Set<UnboundVariable> collector);

        public static class Operation extends EvaluableExpression {

            @Override
            protected void collectVariables(Set<UnboundVariable> collector) {
                a.collectVariables(collector);
                b.collectVariables(collector);
            }

            @Override
            public String toString() {
                return a.toString() + " " + op.symbol + " " + b.toString();
            }

            public enum OP {
                POW("^"), TIMES("*"), DIV("/"), PLUS("+"), MINUS("-");
                private final String symbol;

                OP(String symbol) { this.symbol = symbol; }

                public String symbol() { return symbol; }
            }

            private final OP op;
            private final EvaluableExpression a;
            private final EvaluableExpression b;

            public Operation(OP op, EvaluableExpression a, EvaluableExpression b) {
                this.op = op;
                this.a = a;
                this.b = b;
            }

            public OP operator() { return op; }

            public Pair<EvaluableExpression, EvaluableExpression> operands() { return new Pair<>(a, b); }

            @Override
            public boolean isOperation() { return true; }

            @Override
            public Operation asOperation() { return this; }
        }

        public static class Function extends EvaluableExpression {
            private final String symbol;
            private final List<EvaluableExpression> argList;

            public Function(String symbol, List<EvaluableExpression> argList) {
                this.symbol = symbol;
                this.argList = argList;
            }

            public String symbol() { return symbol; }

            public List<EvaluableExpression> arguments() { return argList; }

            @Override
            public boolean isFunction() { return true; }

            @Override
            public Function asFunction() { return this; }

            @Override
            protected void collectVariables(Set<UnboundVariable> collector) {
                argList.forEach(arg -> arg.collectVariables(collector));
            }

            @Override
            public String toString() {
                return symbol + "(" + argList.stream().map(e -> e.toString()).collect(COMMA.joiner()) + ")";
            }
        }

        public static class Variable extends EvaluableExpression {
            private final UnboundVariable variable;

            public Variable(UnboundVariable variable) {
                this.variable = variable;
            }

            public UnboundVariable variable() { return variable; }

            @Override
            public boolean isVariable() { return true; }

            @Override
            public Variable asVariable() { return this; }

            @Override
            protected void collectVariables(Set<UnboundVariable> collector) {
                collector.add(variable);
            }

            @Override
            public String toString() {
                return variable.reference().toString();
            }
        }

        // TODO: Improve
        public abstract static class Constant<T> extends EvaluableExpression {
            T value;
            public Constant(T value) {
                this.value = value;
            }

            @Override
            public boolean isConstant() { return true; }

            @Override
            public Constant<T> asConstant() { return this; }

            @Override
            protected void collectVariables(Set<UnboundVariable> collector) { } // Nothing to do

            @Override
            public java.lang.String toString() { return value.toString(); }

            public T value() { return value; }

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

            public Long asLong() {
                throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Long.class)));
            }

            public Double asDouble() {
                throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Double.class)));
            }

            public Boolean asBoolean() {
                throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Boolean.class)));
            }

            public String asString() {
                throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(String.class)));
            }

            public DateTime asDateTime() {
                throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(DateTime.class)));
            }

            public static class Long extends Constant<java.lang.Long> {
                public Long(java.lang.Long value) { super(value); }

                @Override
                public boolean isLong() { return true; }

                @Override
                public Long asLong() { return this; }
            }

            public static class Double extends Constant<java.lang.Double> {
                public Double(java.lang.Double value) { super(value); }

                @Override
                public boolean isDouble() { return true; }

                @Override
                public Double asDouble() { return this; }
            }

            public static class Boolean extends Constant<java.lang.Boolean> {
                public Boolean(java.lang.Boolean value) { super(value); }

                @Override
                public boolean isBoolean() { return true; }

                @Override
                public Boolean asBoolean() { return this; }
            }


            public static class String extends Constant<java.lang.String> {
                public String(java.lang.String value) { super(value); }

                @Override
                public boolean isString() { return true; }

                @Override
                public String asString() { return this; }
            }

            public static class DateTime extends Constant<java.time.LocalDateTime> {
                public DateTime(java.time.LocalDateTime value) { super(value); }

                @Override
                public boolean isDateTime() { return true; }

                @Override
                public DateTime asDateTime() { return this; }
            }
        }

        public static class Bracketed extends EvaluableExpression {
            private final EvaluableExpression nestedExpression;

            public Bracketed(EvaluableExpression nestedExpression) {
                this.nestedExpression = nestedExpression;
            }

            public EvaluableExpression nestedExpression() { return nestedExpression; }

            @Override
            public boolean isBracketed() { return true; }

            @Override
            public Bracketed asBracketed() { return this; }

            @Override
            protected void collectVariables(Set<UnboundVariable> collector) {
                nestedExpression.collectVariables(collector);
            }

            @Override
            public String toString() { return "( " + nestedExpression.toString() + " )"; }
        }
    }
}
