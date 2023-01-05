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

package com.vaticle.typeql.lang.query;

import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.variable.BoundVariable;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundDollarVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;
import com.vaticle.typeql.lang.pattern.variable.Variable;

import java.util.List;
import java.util.stream.Stream;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typeql.lang.common.TypeQLToken.Command.DELETE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.VARIABLE_OUT_OF_SCOPE_DELETE;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public class TypeQLDelete extends TypeQLWritable.InsertOrDelete {

    private List<UnboundDollarVariable> namedDollarVariablesUnbound;

    TypeQLDelete(TypeQLMatch.Unfiltered match, List<ThingVariable<?>> variables) {
        super(DELETE, requireNonNull(match), validDeleteVars(match, variables));
    }

    static List<ThingVariable<?>> validDeleteVars(TypeQLMatch.Unfiltered match, List<ThingVariable<?>> variables) {
        variables.forEach(var -> {
            if (var.isNamed() && !match.namedUnboudDollarVariables().contains(var.toUnbound())) {
                throw TypeQLException.of(VARIABLE_OUT_OF_SCOPE_DELETE.message(var.reference()));
            }
            var.variables().forEach(nestedVar -> {
                if (nestedVar.isNamed() && !match.namedUnboudDollarVariables().contains(nestedVar.toUnbound())) {
                    throw TypeQLException.of(VARIABLE_OUT_OF_SCOPE_DELETE.message(nestedVar.reference()));
                }
            });
        });
        return variables;
    }

    public TypeQLMatch.Unfiltered match() {
        assert match != null;
        return match;
    }

    public List<ThingVariable<?>> variables() { return variables; }

    public List<UnboundDollarVariable> namedUnboundDollarVariables() {
        if (namedDollarVariablesUnbound == null) {
            namedDollarVariablesUnbound = variables.stream().flatMap(v -> concat(Stream.of(v), v.variables()))
                    .filter(Variable::isNamed).map(BoundVariable::toUnbound)
                    .filter(UnboundVariable::isDollarVariable).map(UnboundVariable::asDollarVariable)
                    .distinct().collect(toList());
        }
        return namedDollarVariablesUnbound;
    }

    public TypeQLUpdate insert(ThingVariable<?>... things) {
        return insert(list(things));
    }

    public TypeQLUpdate insert(List<ThingVariable<?>> things) {
        return new TypeQLUpdate(this.match(), variables, things);
    }
}
