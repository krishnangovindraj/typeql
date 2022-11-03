package com.vaticle.typeql.lang.pattern.constraint;

import com.vaticle.typeql.lang.pattern.variable.EvaluableVariable;

import java.util.Set;

public class EvaluableConstraint extends Constraint<EvaluableVariable> {

    @Override
    public Set<EvaluableVariable> variables() {
        return null;
    }

    @Override
    public String toString() {
        return null;
    }

    public static class EvaluableExpression {
        public static class Operation {
            public static enum OP { POW, TIMES, DIV, PLUS, MINUS };
        }

        public static class Function {

        }

    }
}
