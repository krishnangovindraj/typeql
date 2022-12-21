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
