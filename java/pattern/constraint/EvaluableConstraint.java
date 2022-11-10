package com.vaticle.typeql.lang.pattern.constraint;

import com.vaticle.typeql.lang.pattern.variable.EvaluableVariable;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;

import java.util.List;
import java.util.Set;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typedb.common.collection.Collections.set;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COMMA;

public class EvaluableConstraint extends Constraint<EvaluableVariable> {
    private final EvaluableExpression expression;

    public EvaluableConstraint(EvaluableExpression expression) {
        this.expression = expression;
    }

    public EvaluableExpression expression() { return expression; }

    @Override
    public Set<EvaluableVariable> variables() {
        return set(); // TODO: Recursively include all variables?
    }

    @Override
    public String toString() {
        return this.expression.toString();
    }

    public abstract static class EvaluableExpression {
        @Override
        public abstract String toString();

        public static EvaluableExpression.Operation op(EvaluableExpression.Operation.OP op, EvaluableExpression a, EvaluableExpression b) {
            return new EvaluableExpression.Operation(op, a, b);
        }

        public static EvaluableExpression.EvaluableFunction func(String funcId, EvaluableExpression... args) {
            return func(funcId, list(args));
        }

        public static EvaluableExpression.EvaluableFunction func(String funcId, List<EvaluableExpression> args) {
            return new EvaluableExpression.EvaluableFunction(funcId, args);
        }

        public static EvaluableAtom.Variable var(EvaluableVariable variable) {
            return new EvaluableAtom.Variable(variable);
        }

        public static EvaluableExpression.EvaluableAtom.Constant.Numeric constant(double value) {
            return new EvaluableExpression.EvaluableAtom.Constant.Numeric(value);
        }

        public static EvaluableExpression bracketed(EvaluableExpression nestedExpr) {
            return new EvaluableExpression.Bracketed(nestedExpr);
        }

        public static class Operation extends EvaluableExpression {
            @Override
            public String toString() {
                return a.toString() + " " + op.symbol + " " + b.toString();
            }

            public enum OP {
                POW("^"), TIMES("*"), DIV("/"), PLUS("+"), MINUS("-");
                private final String symbol;
                OP(String symbol) { this.symbol = symbol; }
            }

            private final OP op;
            private final EvaluableExpression a;
            private final EvaluableExpression b;

            public Operation(OP op, EvaluableExpression a, EvaluableExpression b) {
                this.op = op;
                this.a = a;
                this.b = b;
            }
        }

        public static class EvaluableFunction extends EvaluableExpression {
            private final String id;
            private final List<EvaluableExpression> argList;

            public EvaluableFunction(String id, List<EvaluableExpression> argList) {
                this.id = id;
                this.argList = argList;
            }

            @Override
            public String toString() {
                return id + "(" + argList.stream().map(e -> e.toString()).collect(COMMA.joiner()) + ")";
            }
        }

        public abstract static class EvaluableAtom extends EvaluableExpression {

            public static class Variable extends EvaluableAtom {
                private final EvaluableVariable variable;

                public Variable(EvaluableVariable variable) {
                    this.variable = variable;
                }

                @Override
                public String toString() {
                    return variable.reference().toString();
                }
            }

            // TODO: Improve
            public abstract static class Constant<T> extends EvaluableAtom {
                T value;
                public Constant(T value) {
                    this.value = value;
                }

                @Override
                public String toString() { return value.toString(); }

                public static class Numeric extends Constant<Double> {
                    public Numeric(Double value) { super(value); }
                }
            }
        }

        private static class Bracketed extends EvaluableExpression {
            private final EvaluableExpression nestedExpr;

            public Bracketed(EvaluableExpression nestedExpr) {
                this.nestedExpr = nestedExpr;
            }

            @Override
            public String toString() { return "( " + nestedExpr.toString() + " )"; }
        }
    }
}
