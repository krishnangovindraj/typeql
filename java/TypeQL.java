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

package com.vaticle.typeql.lang;

import com.vaticle.typeql.grammar.TypeQLLexer;
import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.parser.Parser;
import com.vaticle.typeql.lang.pattern.Conjunction;
import com.vaticle.typeql.lang.pattern.Definable;
import com.vaticle.typeql.lang.pattern.Disjunction;
import com.vaticle.typeql.lang.pattern.Negation;
import com.vaticle.typeql.lang.pattern.Pattern;
import com.vaticle.typeql.lang.pattern.constraint.ThingConstraint;
import com.vaticle.typeql.lang.pattern.expression.Predicate;
import com.vaticle.typeql.lang.pattern.schema.Rule;
import com.vaticle.typeql.lang.pattern.variable.*;
import com.vaticle.typeql.lang.pattern.variable.UnboundEvaluableVariable;
import com.vaticle.typeql.lang.query.TypeQLDefine;
import com.vaticle.typeql.lang.query.TypeQLInsert;
import com.vaticle.typeql.lang.query.TypeQLMatch;
import com.vaticle.typeql.lang.query.TypeQLQuery;
import com.vaticle.typeql.lang.query.TypeQLUndefine;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.EQ;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.GT;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.GTE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.LT;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.LTE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.NEQ;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.SubString.CONTAINS;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.SubString.LIKE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.ILLEGAL_CHAR_IN_LABEL;
import static com.vaticle.typeql.lang.pattern.variable.UnboundVariable.hidden;

public class TypeQL {

    private static final Parser parser = new Parser();

    public static <T extends TypeQLQuery> T parseQuery(String queryString) {
        return parser.parseQueryEOF(queryString);
    }

    public static <T extends TypeQLQuery> Stream<T> parseQueries(String queryString) {
        return parser.parseQueriesEOF(queryString);
    }

    public static Pattern parsePattern(String pattern) {
        return parser.parsePatternEOF(pattern);
    }

    public static List<? extends Pattern> parsePatterns(String pattern) {
        return parser.parsePatternsEOF(pattern);
    }

    public static List<Definable> parseDefinables(String pattern) {return parser.parseDefinablesEOF(pattern);}

    public static Rule parseRule(String pattern) {return parser.parseSchemaRuleEOF(pattern).asRule();}

    public static BoundVariable parseVariable(String variable) {
        return parser.parseVariableEOF(variable);
    }

    public static String parseLabel(String label) {
        String parsedLabel;
        try {
            parsedLabel = parser.parseLabelEOF(label);
        } catch (TypeQLException e) {
            throw TypeQLException.of(ILLEGAL_CHAR_IN_LABEL.message(label));
        }
        if (!parsedLabel.equals(label))
            throw TypeQLException.of(ILLEGAL_CHAR_IN_LABEL.message(label)); // e.g: 'abc#123'
        return parsedLabel;
    }

    public static TypeQLLexer lexer(String string) {
        return parser.lexer(string);
    }

    public static TypeQLMatch.Unfiltered match(Pattern... patterns) {
        return match(list(patterns));
    }

    public static TypeQLMatch.Unfiltered match(List<? extends Pattern> patterns) {
        return new TypeQLMatch.Unfiltered(patterns);
    }

    public static TypeQLInsert insert(ThingVariable<?>... things) {
        return new TypeQLInsert(list(things));
    }

    public static TypeQLInsert insert(List<ThingVariable<?>> things) {
        return new TypeQLInsert(things);
    }

    public static TypeQLDefine define(Definable... definables) {
        return new TypeQLDefine(list(definables));
    }

    public static TypeQLDefine define(List<Definable> definables) {
        return new TypeQLDefine(definables);
    }

    public static TypeQLUndefine undefine(TypeVariable... types) {
        return new TypeQLUndefine(list(types));
    }

    public static TypeQLUndefine undefine(List<Definable> definables) {
        return new TypeQLUndefine(definables);
    }

    // Pattern Builder Methods

    public static Conjunction<? extends Pattern> and(Pattern... patterns) {
        return and(list(patterns));
    }

    public static Conjunction<? extends Pattern> and(List<? extends Pattern> patterns) {
        return new Conjunction<>(patterns);
    }

    public static Pattern or(Pattern... patterns) {
        return or(list(patterns));
    }

    public static Pattern or(List<Pattern> patterns) {
        // Simplify representation when there is only one alternative
        if (patterns.size() == 1) return patterns.iterator().next();
        return new Disjunction<>(patterns);
    }

    public static Negation<Pattern> not(Pattern pattern) {
        return new Negation<>(pattern);
    }

    public static Rule rule(String label) {
        return new Rule(label);
    }

    public static UnboundVariable var() {
        return UnboundVariable.anonymous();
    }

    public static UnboundVariable var(String name) {
        return UnboundVariable.named(name);
    }

    public static UnboundEvaluableVariable valvar(String name) {
        return new UnboundEvaluableVariable(Reference.namedVal(name));
    }

    public static TypeVariable type(TypeQLToken.Type type) {
        return type(type.toString());
    }

    public static TypeVariable type(String label) {
        return hidden().type(label);
    }

    public static ThingVariable.Relation rel(String playerVar) {
        return hidden().rel(playerVar);
    }

    public static ThingVariable.Relation rel(UnboundVariable playerVar) {
        return hidden().rel(playerVar);
    }

    public static ThingVariable.Relation rel(String roleType, String playerVar) {
        return hidden().rel(roleType, playerVar);
    }

    public static ThingVariable.Relation rel(String roleType, UnboundVariable playerVar) {
        return hidden().rel(roleType, playerVar);
    }

    public static ThingVariable.Relation rel(UnboundVariable roleType, UnboundVariable playerVar) {
        return hidden().rel(roleType, playerVar);
    }

    public static ThingConstraint.Value<Long> eq(long value) {
        return new ThingConstraint.Value<>(new Predicate.Long(EQ, value));
    }

    public static ThingConstraint.Value<Double> eq(double value) {
        return new ThingConstraint.Value<>(new Predicate.Double(EQ, value));
    }

    public static ThingConstraint.Value<Boolean> eq(boolean value) {
        return new ThingConstraint.Value<>(new Predicate.Boolean(EQ, value));
    }

    public static ThingConstraint.Value<String> eq(String value) {
        return new ThingConstraint.Value<>(new Predicate.String(EQ, value));
    }

    public static ThingConstraint.Value<LocalDateTime> eq(LocalDateTime value) {
        return new ThingConstraint.Value<>(new Predicate.DateTime(EQ, value));
    }

    public static ThingConstraint.Value<ThingVariable<?>> eq(UnboundVariable variable) {
        return new ThingConstraint.Value<>(new Predicate.Variable(EQ, variable.toThing()));
    }

    public static ThingConstraint.Value<EvaluableVariable> eq(UnboundEvaluableVariable variable) {
        return new ThingConstraint.Value<>(new Predicate.ValueVariable(EQ, variable.toEvaluable()));
    }
    
    public static ThingConstraint.Value<Long> neq(long value) {
        return new ThingConstraint.Value<>(new Predicate.Long(NEQ, value));
    }

    public static ThingConstraint.Value<Double> neq(double value) {
        return new ThingConstraint.Value<>(new Predicate.Double(NEQ, value));
    }

    public static ThingConstraint.Value<Boolean> neq(boolean value) {
        return new ThingConstraint.Value<>(new Predicate.Boolean(NEQ, value));
    }

    public static ThingConstraint.Value<String> neq(String value) {
        return new ThingConstraint.Value<>(new Predicate.String(NEQ, value));
    }

    public static ThingConstraint.Value<LocalDateTime> neq(LocalDateTime value) {
        return new ThingConstraint.Value<>(new Predicate.DateTime(NEQ, value));
    }

    public static ThingConstraint.Value<ThingVariable<?>> neq(UnboundVariable variable) {
        return new ThingConstraint.Value<>(new Predicate.Variable(NEQ, variable.toThing()));
    }

    public static ThingConstraint.Value<EvaluableVariable> neq(UnboundEvaluableVariable variable) {
        return new ThingConstraint.Value<>(new Predicate.ValueVariable(NEQ, variable.toEvaluable()));
    }

    public static ThingConstraint.Value<Long> gt(long value) {
        return new ThingConstraint.Value<>(new Predicate.Long(GT, value));
    }

    public static ThingConstraint.Value<Double> gt(double value) {
        return new ThingConstraint.Value<>(new Predicate.Double(GT, value));
    }

    public static ThingConstraint.Value<Boolean> gt(boolean value) {
        return new ThingConstraint.Value<>(new Predicate.Boolean(GT, value));
    }

    public static ThingConstraint.Value<String> gt(String value) {
        return new ThingConstraint.Value<>(new Predicate.String(GT, value));
    }

    public static ThingConstraint.Value<LocalDateTime> gt(LocalDateTime value) {
        return new ThingConstraint.Value<>(new Predicate.DateTime(GT, value));
    }

    public static ThingConstraint.Value<ThingVariable<?>> gt(UnboundVariable variable) {
        return new ThingConstraint.Value<>(new Predicate.Variable(GT, variable.toThing()));
    }

    public static ThingConstraint.Value<EvaluableVariable> gt(UnboundEvaluableVariable variable) {
        return new ThingConstraint.Value<>(new Predicate.ValueVariable(GT, variable.toEvaluable()));
    }

    public static ThingConstraint.Value<Long> gte(long value) {
        return new ThingConstraint.Value<>(new Predicate.Long(GTE, value));
    }

    public static ThingConstraint.Value<Double> gte(double value) {
        return new ThingConstraint.Value<>(new Predicate.Double(GTE, value));
    }

    public static ThingConstraint.Value<Boolean> gte(boolean value) {
        return new ThingConstraint.Value<>(new Predicate.Boolean(GTE, value));
    }

    public static ThingConstraint.Value<String> gte(String value) {
        return new ThingConstraint.Value<>(new Predicate.String(GTE, value));
    }

    public static ThingConstraint.Value<LocalDateTime> gte(LocalDateTime value) {
        return new ThingConstraint.Value<>(new Predicate.DateTime(GTE, value));
    }

    public static ThingConstraint.Value<ThingVariable<?>> gte(UnboundVariable variable) {
        return new ThingConstraint.Value<>(new Predicate.Variable(GTE, variable.toThing()));
    }

    public static ThingConstraint.Value<EvaluableVariable> gte(UnboundEvaluableVariable variable) {
        return new ThingConstraint.Value<>(new Predicate.ValueVariable(GTE, variable.toEvaluable()));
    }

    public static ThingConstraint.Value<Long> lt(long value) {
        return new ThingConstraint.Value<>(new Predicate.Long(LT, value));
    }

    public static ThingConstraint.Value<Double> lt(double value) {
        return new ThingConstraint.Value<>(new Predicate.Double(LT, value));
    }

    public static ThingConstraint.Value<Boolean> lt(boolean value) {
        return new ThingConstraint.Value<>(new Predicate.Boolean(LT, value));
    }

    public static ThingConstraint.Value<String> lt(String value) {
        return new ThingConstraint.Value<>(new Predicate.String(LT, value));
    }

    public static ThingConstraint.Value<LocalDateTime> lt(LocalDateTime value) {
        return new ThingConstraint.Value<>(new Predicate.DateTime(LT, value));
    }

    public static ThingConstraint.Value<ThingVariable<?>> lt(UnboundVariable variable) {
        return new ThingConstraint.Value<>(new Predicate.Variable(LT, variable.toThing()));
    }

    public static ThingConstraint.Value<EvaluableVariable> lt(UnboundEvaluableVariable variable) {
        return new ThingConstraint.Value<>(new Predicate.ValueVariable(LT, variable.toEvaluable()));
    }

    public static ThingConstraint.Value<Long> lte(long value) {
        return new ThingConstraint.Value<>(new Predicate.Long(LTE, value));
    }

    public static ThingConstraint.Value<Double> lte(double value) {
        return new ThingConstraint.Value<>(new Predicate.Double(LTE, value));
    }

    public static ThingConstraint.Value<Boolean> lte(boolean value) {
        return new ThingConstraint.Value<>(new Predicate.Boolean(LTE, value));
    }

    public static ThingConstraint.Value<String> lte(String value) {
        return new ThingConstraint.Value<>(new Predicate.String(LTE, value));
    }

    public static ThingConstraint.Value<LocalDateTime> lte(LocalDateTime value) {
        return new ThingConstraint.Value<>(new Predicate.DateTime(LTE, value));
    }

    public static ThingConstraint.Value<ThingVariable<?>> lte(UnboundVariable variable) {
        return new ThingConstraint.Value<>(new Predicate.Variable(LTE, variable.toThing()));
    }

    public static ThingConstraint.Value<EvaluableVariable> lte(UnboundEvaluableVariable variable) {
        return new ThingConstraint.Value<>(new Predicate.ValueVariable(LTE, variable.toEvaluable()));
    }

    public static ThingConstraint.Value<String> contains(String value) {
        return new ThingConstraint.Value<>(new Predicate.String(CONTAINS, value));
    }

    public static ThingConstraint.Value<String> like(String value) {
        return new ThingConstraint.Value<>(new Predicate.String(LIKE, value));
    }
}
