package com.vaticle.typeql.lang.pattern.constraint;

import com.vaticle.typeql.lang.pattern.variable.EvaluableVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;

import java.util.List;
import java.util.Set;

public class EvaluableConstraint extends Constraint<EvaluableVariable> {
    private final EvaluableExpression.EvaluableAtom.EvaluableVariable assignTo;
    private final EvaluableExpression expression;

    public EvaluableConstraint(EvaluableExpression.EvaluableAtom.EvaluableVariable assignTo, EvaluableExpression expression) {
        this.assignTo = assignTo;
        this.expression = expression;
    }

    @Override
    public Set<EvaluableVariable> variables() {
        return null;
    }

    @Override
    public String toString() {
        return null;
    }

    public abstract static class EvaluableExpression {

        public static class Operation extends EvaluableExpression {
            public enum OP {POW, TIMES, DIV, PLUS, MINUS}

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
        }

        public static class EvaluableAtom extends EvaluableExpression {

            public static class NumericConstant extends EvaluableAtom {
                private final double value;

                public NumericConstant(double value) {
                    super();
                    this.value = value;
                }
            }

            // TODO: Unify them a bit better
            public static class EvaluableVariable extends EvaluableAtom {
                private final String id;

                public EvaluableVariable(String id) {
                    this.id = id;
                }
            }

            public static class AttributeVariable extends EvaluableAtom {
                private final UnboundVariable variable;

                public AttributeVariable(UnboundVariable variable) {
                    this.variable = variable;
                }
            }

            public static class Constant extends EvaluableAtom {
                Object value;

                public Constant(Object value) {
                    this.value = value; // TODO: Improve
                }
            }
        }
    }
}
