/*
 * Copyright (C) 2022 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.vaticle.typeql.lang.pattern.constraint;

import com.vaticle.typedb.common.collection.Pair;
import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.variable.BoundVariable;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;
import com.vaticle.typeql.lang.pattern.variable.ValueVariable;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.vaticle.typedb.common.collection.Collections.set;
import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COMMA_SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;

public abstract class ValueConstraint extends Constraint<BoundVariable> {

    @Override
    public boolean isValue() {
        return true;
    }

    @Override
    public ValueConstraint asValue() {
        return this;
    }

    public boolean isPredicate() {
        return false;
    }

    public boolean isExpression() {
        return false;
    }

    public Predicate asPredicate() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Predicate.class)));
    }

    public Assignment asExpression() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Assignment.class)));
    }

    public static class Predicate extends ValueConstraint {

        private final com.vaticle.typeql.lang.pattern.Predicate<?> predicate;
        private final int hash;

        public Predicate(com.vaticle.typeql.lang.pattern.Predicate<?> predicate) {
            this.predicate = predicate;
            this.hash = Objects.hash(Predicate.class, this.predicate);
        }

        @Override
        public Set<BoundVariable> variables() {
            return set();
        }

        @Override
        public boolean isPredicate() {
            return true;
        }

        @Override
        public Predicate asPredicate() {
            return this;
        }

        public com.vaticle.typeql.lang.pattern.Predicate<?> predicate() {
            return predicate;
        }

        @Override
        public java.lang.String toString() {
            return predicate.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Predicate that = (Predicate) o;
            return this.predicate.equals(that.predicate);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Assignment extends ValueConstraint {
        private final Expression expression;
        private final Set<BoundVariable> inputs;
        private final int hash;

        public Assignment(Expression expression) {
            this.expression = expression;
            this.inputs = new HashSet<>();
            this.inputs.addAll(expression.variables());
            this.hash = Objects.hash(Assignment.class, this.expression);
        }

        public Expression expression() {
            return expression;
        }

        @Override
        public Set<BoundVariable> variables() {
            return inputs;
        }

        public boolean isExpression() {
            return true;
        }

        public Assignment asExpression() {
            return this;
        }

        @Override
        public String toString() {
            return TypeQLToken.Arithmetic.ASSIGN.toString() + SPACE + this.expression.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Assignment that = (Assignment) o;
            return this.expression.equals(that.expression);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        public abstract static class Expression {

            public boolean isOperation() {
                return false;
            }

            public boolean isFunction() {
                return false;
            }

            public boolean isBracketed() {
                return false;
            }

            public boolean isThingVar() {
                return false;
            }

            public boolean isValVar() {
                return false;
            }

            public boolean isConstant() {
                return false;
            }

            public Operation asOperation() {
                throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Operation.class)));
            }

            public Function asFunction() {
                throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Function.class)));
            }

            public Bracketed asBracketed() {
                throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Bracketed.class)));
            }

            public ThingVar asThingVar() {
                throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(ThingVar.class)));
            }

            public ValVar asValVar() {
                throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(ValVar.class)));
            }

            public Constant<?> asConstant() {
                throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Constant.class)));
            }

            @Override
            public abstract String toString();

            public Set<BoundVariable> variables() {
                Set<BoundVariable> collector = new HashSet<>();
                collectVariables(collector);
                return collector;
            }

            protected abstract void collectVariables(Set<BoundVariable> collector);

            @Override
            public abstract boolean equals(Object o);

            @Override
            public abstract int hashCode();

            public static class Operation extends Expression {

                public enum OP {
                    POW(TypeQLToken.Arithmetic.POW),
                    TIMES(TypeQLToken.Arithmetic.TIMES), DIV(TypeQLToken.Arithmetic.DIV), MOD(TypeQLToken.Arithmetic.MOD),
                    PLUS(TypeQLToken.Arithmetic.PLUS), MINUS(TypeQLToken.Arithmetic.MINUS);
                    private final TypeQLToken.Arithmetic symbol;

                    OP(TypeQLToken.Arithmetic symbol) {
                        this.symbol = symbol;
                    }

                    public TypeQLToken.Arithmetic symbol() {
                        return symbol;
                    }
                }

                private final Operation.OP op;
                private final Expression a;
                private final Expression b;
                private final int hash;

                public Operation(Operation.OP op, Expression a, Expression b) {
                    this.op = op;
                    this.a = a;
                    this.b = b;
                    this.hash = Objects.hash(op, a, b);
                }

                public Operation.OP operator() {
                    return op;
                }

                public Pair<Expression, Expression> operands() {
                    return new Pair<>(a, b);
                }

                @Override
                public boolean isOperation() {
                    return true;
                }

                @Override
                public Operation asOperation() {
                    return this;
                }

                @Override
                protected void collectVariables(Set<BoundVariable> collector) {
                    a.collectVariables(collector);
                    b.collectVariables(collector);
                }

                @Override
                public String toString() {
                    return a.toString() + " " + op.symbol + " " + b.toString();
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) return true;
                    if (o == null || getClass() != o.getClass()) return false;
                    Operation that = (Operation) o;
                    return (this.op.equals(that.op) && this.a.equals(that.a) && this.b.equals(that.b));
                }

                @Override
                public int hashCode() {
                    return hash;
                }
            }

            public static class Function extends Expression {
                private final String symbol;
                private final List<Expression> argList;
                private final int hash;

                public Function(String symbol, List<Expression> argList) {
                    this.symbol = symbol;
                    this.argList = argList;
                    this.hash = Objects.hash(this.symbol, this.argList);
                }

                public String symbol() {
                    return symbol;
                }

                public List<Expression> arguments() {
                    return argList;
                }

                @Override
                public boolean isFunction() {
                    return true;
                }

                @Override
                public Function asFunction() {
                    return this;
                }

                @Override
                protected void collectVariables(Set<BoundVariable> collector) {
                    argList.forEach(arg -> arg.collectVariables(collector));
                }

                @Override
                public String toString() {
                    return symbol + "(" + argList.stream().map(Expression::toString).collect(COMMA_SPACE.joiner()) + ")";
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) return true;
                    if (o == null || getClass() != o.getClass()) return false;
                    Function that = (Function) o;
                    return (this.symbol.equals(that.symbol) && this.argList.equals(that.argList));
                }

                @Override
                public int hashCode() {
                    return hash;
                }
            }

            public static class ThingVar extends Expression {
                private final ThingVariable<?> variable;
                private final int hash;

                public ThingVar(ThingVariable<?> variable) {
                    this.variable = variable;
                    this.hash = Objects.hash(ThingVar.class, variable);
                }

                public ThingVariable<?> variable() {
                    return variable;
                }

                @Override
                public boolean isThingVar() {
                    return true;
                }

                @Override
                public ThingVar asThingVar() {
                    return this;
                }

                @Override
                protected void collectVariables(Set<BoundVariable> collector) {
                    collector.add(variable);
                }

                @Override
                public String toString() {
                    return variable.reference().toString();
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) return true;
                    if (o == null || getClass() != o.getClass()) return false;
                    ThingVar that = (ThingVar) o;
                    return this.variable.equals(that.variable);
                }

                @Override
                public int hashCode() {
                    return hash;
                }
            }

            public static class ValVar extends Expression {
                private final ValueVariable variable;
                private final int hash;

                public ValVar(ValueVariable variable) {
                    this.variable = variable;
                    this.hash = Objects.hash(ValVar.class, variable);
                }

                public ValueVariable variable() {
                    return variable;
                }

                @Override
                public boolean isValVar() {
                    return true;
                }

                @Override
                public ValVar asValVar() {
                    return this;
                }

                @Override
                protected void collectVariables(Set<BoundVariable> collector) {
                    collector.add(variable);
                }

                @Override
                public String toString() {
                    return variable.reference().toString();
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) return true;
                    if (o == null || getClass() != o.getClass()) return false;
                    ValVar that = (ValVar) o;
                    return this.variable.equals(that.variable);
                }

                @Override
                public int hashCode() {
                    return hash;
                }
            }

            public abstract static class Constant<T> extends Expression {
                T value;

                public Constant(T value) {
                    this.value = value;
                }

                @Override
                public boolean isConstant() {
                    return true;
                }

                @Override
                public Constant<T> asConstant() {
                    return this;
                }

                @Override
                protected void collectVariables(Set<BoundVariable> collector) {
                }

                @Override
                public java.lang.String toString() {
                    return value.toString();
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

                public Constant.Long asLong() {
                    throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Constant.Long.class)));
                }

                public Constant.Double asDouble() {
                    throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Constant.Double.class)));
                }

                public Constant.Boolean asBoolean() {
                    throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Constant.Boolean.class)));
                }

                public Constant.String asString() {
                    throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Constant.String.class)));
                }

                public Constant.DateTime asDateTime() {
                    throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Constant.DateTime.class)));
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) return true;
                    if (o == null || getClass() != o.getClass()) return false;
                    Constant<?> that = (Constant<?>) o;
                    return this.value.equals(that.value);
                }

                @Override
                public int hashCode() {
                    return value.hashCode();
                }

                public static class Long extends Constant<java.lang.Long> {
                    public Long(java.lang.Long value) {
                        super(value);
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

                public static class Double extends Constant<java.lang.Double> {
                    public Double(java.lang.Double value) {
                        super(value);
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

                public static class Boolean extends Constant<java.lang.Boolean> {
                    public Boolean(java.lang.Boolean value) {
                        super(value);
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

                public static class String extends Constant<java.lang.String> {
                    public String(java.lang.String value) {
                        super(value);
                    }

                    @Override
                    public boolean isString() {
                        return true;
                    }

                    @Override
                    public String asString() {
                        return this;
                    }
                }

                public static class DateTime extends Constant<java.time.LocalDateTime> {
                    public DateTime(java.time.LocalDateTime value) {
                        super(value);
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
            }

            public static class Bracketed extends Expression {
                private final Expression nestedExpression;
                private final int hash;

                public Bracketed(Expression nestedExpression) {
                    this.nestedExpression = nestedExpression;
                    this.hash = Objects.hash(Bracketed.class, nestedExpression);
                }

                public Expression nestedExpression() {
                    return nestedExpression;
                }

                @Override
                public boolean isBracketed() {
                    return true;
                }

                @Override
                public Bracketed asBracketed() {
                    return this;
                }

                @Override
                protected void collectVariables(Set<BoundVariable> collector) {
                    nestedExpression.collectVariables(collector);
                }

                @Override
                public String toString() {
                    return "( " + nestedExpression.toString() + " )";
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) return true;
                    if (o == null || getClass() != o.getClass()) return false;
                    Bracketed that = (Bracketed) o;
                    return this.nestedExpression.equals(that.nestedExpression);
                }

                @Override
                public int hashCode() {
                    return hash;
                }
            }
        }
    }
}
