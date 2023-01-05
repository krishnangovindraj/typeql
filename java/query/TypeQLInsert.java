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
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;
import com.vaticle.typeql.lang.pattern.variable.Variable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.vaticle.typeql.lang.common.TypeQLToken.Command.INSERT;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.NO_VARIABLE_IN_SCOPE_INSERT;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public class TypeQLInsert extends TypeQLWritable.InsertOrDelete {

    private List<UnboundVariable> namedVariablesUnbound;

    public TypeQLInsert(List<ThingVariable<?>> variables) {
        this(null, variables);
    }

    TypeQLInsert(@Nullable TypeQLMatch.Unfiltered match, List<ThingVariable<?>> variables) {
        super(INSERT, match, validInsertVars(match, variables));
    }

    static List<ThingVariable<?>> validInsertVars(@Nullable TypeQLMatch.Unfiltered match, List<ThingVariable<?>> variables) {
        if (match != null) {
            if (variables.stream().noneMatch(var -> var.isNamed() && match.namedUnboudDollarVariables().contains(var.toUnbound())
                    || var.variables().anyMatch(nestedVar -> match.namedUnboundVariables().contains(nestedVar.toUnbound())))) {
                throw TypeQLException.of(NO_VARIABLE_IN_SCOPE_INSERT.message(variables, match.namedUnboundVariables()));
            }
        }
        return variables;
    }

    public List<UnboundVariable> namedUnboundVariables() {
        if (namedVariablesUnbound == null) {
            namedVariablesUnbound = variables.stream().flatMap(v -> concat(Stream.of(v), v.variables()))
                    .filter(Variable::isNamed).map(BoundVariable::toUnbound).distinct().collect(toList());
        }
        return namedVariablesUnbound;
    }


    public Optional<TypeQLMatch.Unfiltered> match() {
        return Optional.ofNullable(match);
    }

    public List<ThingVariable<?>> variables() { return variables; }
}
