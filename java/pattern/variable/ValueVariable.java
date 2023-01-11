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

import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.constraint.ValueConstraint;

import java.util.List;
import java.util.Objects;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.ILLEGAL_CONSTRAINT_REPETITION;

public class ValueVariable extends BoundVariable {
    private ValueConstraint constraint;

    public ValueVariable(Reference.Name reference) {
        super(reference);
        assert reference.refersToValue();
        this.constraint = null;
    }

    public ValueVariable(Reference.Name reference, ValueConstraint constraint) {
        this(reference);
        constrain(constraint);
    }

    @Override
    public UnboundValueVariable toUnbound() {
        return new UnboundValueVariable(reference);
    }

    public ValueVariable constrain(ValueConstraint constraint) {
        if (this.constraint != null) {
            throw TypeQLException.of(ILLEGAL_CONSTRAINT_REPETITION.message(reference, ValueConstraint.class, constraint));
        }
        this.constraint = constraint;
        return this;
    }

    @Override
    public List<ValueConstraint> constraints() {
        return constraint != null ? list(constraint) : list();
    }

    @Override
    public boolean isValue() {
        return true;
    }

    @Override
    public ValueVariable asValue() {
        return this;
    }

    @Override
    public String toString(boolean pretty) {
        return reference().syntax() + ((constraint != null) ? SPACE + constraint.toString() : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueVariable that = (ValueVariable) o;
        return this.reference.equals(that.reference) && Objects.equals(this.constraint, that.constraint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference, constraint);
    }
}
