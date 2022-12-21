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

import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.expression.EvaluableExpression;
import com.vaticle.typeql.lang.pattern.expression.Predicate;
import com.vaticle.typeql.lang.pattern.variable.BoundVariable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.vaticle.typedb.common.collection.Collections.set;
import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;

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

    public boolean isExpression() {
        return false;
    }

    public Value<?> asValue() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(EvaluableConstraint.Value.class)));
    }

    public EvaluableConstraint.Expression asExpression() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(EvaluableConstraint.Expression.class)));
    }


    public static class Value<T> extends EvaluableConstraint {

        private final Predicate<T> predicate;
        private final int hash;

        public Value(Predicate<T> predicate) {
            this.predicate = predicate;
            this.hash = Objects.hash(Value.class, this.predicate);
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

        public Predicate predicate() {
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
            Value<?> that = (Value<?>) o;
            return this.predicate.equals(that.predicate);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Expression extends EvaluableConstraint {
        private final EvaluableExpression expression;
        private final Set<BoundVariable> inputs;

        public Expression(EvaluableExpression expression) {
            this.expression = expression;
            this.inputs = new HashSet<>();
            expression.variables().forEach(v -> this.inputs.add(v));
        }

        public EvaluableExpression expression() { return expression; }

        @Override
        public Set<BoundVariable> variables() {
            return inputs;
        }

        public boolean isExpression() {
            return true;
        }

        public EvaluableConstraint.Expression asExpression() {
            return this;
        }

        @Override
        public String toString() {
            return TypeQLToken.Char.ASSIGN.toString() + SPACE + this.expression.toString();
        }
    }
}
