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

package com.vaticle.typeql.lang.pattern.variable.builder;

import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.pattern.constraint.ThingConstraint;
import com.vaticle.typeql.lang.pattern.expression.Predicate;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;

import java.time.LocalDateTime;

import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.EQ;
public interface ThingVariableBuilder {

    interface Common<T> {

        default T isa(TypeQLToken.Type type) {
            return isa(type.toString());
        }

        default T isa(String type) {
            return constrain(new ThingConstraint.Isa(type, false));
        }

        default T isa(UnboundVariable var) {
            return constrain(new ThingConstraint.Isa(var, false));
        }

        default T isaX(TypeQLToken.Type type) {
            return isa(type.toString());
        }

        default T isaX(String type) {
            return constrain(new ThingConstraint.Isa(type, true));
        }

        default T isaX(UnboundVariable var) {
            return constrain(new ThingConstraint.Isa(var, true));
        }

        default T has(String type, long value) {
            return has(type, new ThingConstraint.Value(new Predicate.Long(EQ, value)));
        }

        default T has(String type, double value) {
            return has(type, new ThingConstraint.Value(new Predicate.Double(EQ, value)));
        }

        default T has(String type, boolean value) {
            return has(type, new ThingConstraint.Value(new Predicate.Boolean(EQ, value)));
        }

        default T has(String type, String value) {
            return has(type, new ThingConstraint.Value(new Predicate.String(EQ, value)));
        }

        default T has(String type, LocalDateTime value) {
            return has(type, new ThingConstraint.Value(new Predicate.DateTime(EQ, value)));
        }

        default T has(String type, ThingConstraint.Value<?> value) {
            return constrain(new ThingConstraint.Has(type, value));
        }

        default T has(String type, UnboundVariable variable) {
            return constrain(new ThingConstraint.Has(type, variable));
        }

        default T has(UnboundVariable variable) {
            return constrain(new ThingConstraint.Has(variable));
        }

        T constrain(ThingConstraint.Isa constraint);

        T constrain(ThingConstraint.Has constraint);
    }

    interface Thing {

        default ThingVariable.Thing iid(String iid) {
            return constrain(new ThingConstraint.IID(iid));
        }

        ThingVariable.Thing constrain(ThingConstraint.IID constraint);
    }

    interface Relation {

        default ThingVariable.Relation rel(String playerVar) {
            return rel(UnboundVariable.named(playerVar));
        }

        default ThingVariable.Relation rel(UnboundVariable playerVar) {
            return constrain(new ThingConstraint.Relation.RolePlayer(playerVar));
        }

        default ThingVariable.Relation rel(String roleType, String playerVar) {
            return constrain(new ThingConstraint.Relation.RolePlayer(roleType, UnboundVariable.named(playerVar)));
        }

        default ThingVariable.Relation rel(String roleType, UnboundVariable playerVar) {
            return constrain(new ThingConstraint.Relation.RolePlayer(roleType, playerVar));
        }

        default ThingVariable.Relation rel(UnboundVariable roleTypeVar, UnboundVariable playerVar) {
            return constrain(new ThingConstraint.Relation.RolePlayer(roleTypeVar, playerVar));
        }

        ThingVariable.Relation constrain(ThingConstraint.Relation.RolePlayer rolePlayer);
    }

    interface Attribute {
        ThingVariable.Attribute constrain(ThingConstraint.Value<?> constraint);
    }
}
