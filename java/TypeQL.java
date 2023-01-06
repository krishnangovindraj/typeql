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
import com.vaticle.typeql.lang.pattern.expression.Expression;
import com.vaticle.typeql.lang.pattern.expression.Predicate;
import com.vaticle.typeql.lang.pattern.schema.Rule;
import com.vaticle.typeql.lang.pattern.variable.BoundVariable;
import com.vaticle.typeql.lang.pattern.variable.ValueVariable;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;
import com.vaticle.typeql.lang.pattern.variable.TypeVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundDollarVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundValueVariable;
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
import static com.vaticle.typeql.lang.pattern.variable.UnboundDollarVariable.hidden;

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

    public static List<Definable> parseDefinables(String pattern) {
        return parser.parseDefinablesEOF(pattern);
    }

    public static Rule parseRule(String pattern) {
        return parser.parseSchemaRuleEOF(pattern).asRule();
    }

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

    public static UnboundDollarVariable var() {
        return UnboundDollarVariable.anonymous();
    }

    public static UnboundDollarVariable var(String name) {
        return UnboundDollarVariable.named(name);
    }

    public static UnboundValueVariable valvar(String name) {
        return UnboundValueVariable.named(name);
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

    public static ThingVariable.Relation rel(UnboundDollarVariable playerVar) {
        return hidden().rel(playerVar);
    }

    public static ThingVariable.Relation rel(String roleType, String playerVar) {
        return hidden().rel(roleType, playerVar);
    }

    public static ThingVariable.Relation rel(String roleType, UnboundDollarVariable playerVar) {
        return hidden().rel(roleType, playerVar);
    }

    public static ThingVariable.Relation rel(UnboundDollarVariable roleType, UnboundDollarVariable playerVar) {
        return hidden().rel(roleType, playerVar);
    }

    public static ThingConstraint.Predicate<Long> eq(long value) {
        return new ThingConstraint.Predicate<>(new Predicate.Long(EQ, value));
    }

    public static ThingConstraint.Predicate<Double> eq(double value) {
        return new ThingConstraint.Predicate<>(new Predicate.Double(EQ, value));
    }

    public static ThingConstraint.Predicate<Boolean> eq(boolean value) {
        return new ThingConstraint.Predicate<>(new Predicate.Boolean(EQ, value));
    }

    public static ThingConstraint.Predicate<String> eq(String value) {
        return new ThingConstraint.Predicate<>(new Predicate.String(EQ, value));
    }

    public static ThingConstraint.Predicate<LocalDateTime> eq(LocalDateTime value) {
        return new ThingConstraint.Predicate<>(new Predicate.DateTime(EQ, value));
    }

    public static ThingConstraint.Predicate<ThingVariable<?>> eq(UnboundDollarVariable variable) {
        return new ThingConstraint.Predicate<>(new Predicate.ThingVariable(EQ, variable.toThing()));
    }

    public static ThingConstraint.Predicate<ValueVariable> eq(UnboundValueVariable variable) {
        return new ThingConstraint.Predicate<>(new Predicate.ValueVariable(EQ, variable.toValue()));
    }

    public static ThingConstraint.Predicate<Long> neq(long value) {
        return new ThingConstraint.Predicate<>(new Predicate.Long(NEQ, value));
    }

    public static ThingConstraint.Predicate<Double> neq(double value) {
        return new ThingConstraint.Predicate<>(new Predicate.Double(NEQ, value));
    }

    public static ThingConstraint.Predicate<Boolean> neq(boolean value) {
        return new ThingConstraint.Predicate<>(new Predicate.Boolean(NEQ, value));
    }

    public static ThingConstraint.Predicate<String> neq(String value) {
        return new ThingConstraint.Predicate<>(new Predicate.String(NEQ, value));
    }

    public static ThingConstraint.Predicate<LocalDateTime> neq(LocalDateTime value) {
        return new ThingConstraint.Predicate<>(new Predicate.DateTime(NEQ, value));
    }

    public static ThingConstraint.Predicate<ThingVariable<?>> neq(UnboundDollarVariable variable) {
        return new ThingConstraint.Predicate<>(new Predicate.ThingVariable(NEQ, variable.toThing()));
    }

    public static ThingConstraint.Predicate<ValueVariable> neq(UnboundValueVariable variable) {
        return new ThingConstraint.Predicate<>(new Predicate.ValueVariable(NEQ, variable.toValue()));
    }

    public static ThingConstraint.Predicate<Long> gt(long value) {
        return new ThingConstraint.Predicate<>(new Predicate.Long(GT, value));
    }

    public static ThingConstraint.Predicate<Double> gt(double value) {
        return new ThingConstraint.Predicate<>(new Predicate.Double(GT, value));
    }

    public static ThingConstraint.Predicate<Boolean> gt(boolean value) {
        return new ThingConstraint.Predicate<>(new Predicate.Boolean(GT, value));
    }

    public static ThingConstraint.Predicate<String> gt(String value) {
        return new ThingConstraint.Predicate<>(new Predicate.String(GT, value));
    }

    public static ThingConstraint.Predicate<LocalDateTime> gt(LocalDateTime value) {
        return new ThingConstraint.Predicate<>(new Predicate.DateTime(GT, value));
    }

    public static ThingConstraint.Predicate<ThingVariable<?>> gt(UnboundDollarVariable variable) {
        return new ThingConstraint.Predicate<>(new Predicate.ThingVariable(GT, variable.toThing()));
    }

    public static ThingConstraint.Predicate<ValueVariable> gt(UnboundValueVariable variable) {
        return new ThingConstraint.Predicate<>(new Predicate.ValueVariable(GT, variable.toValue()));
    }

    public static ThingConstraint.Predicate<Long> gte(long value) {
        return new ThingConstraint.Predicate<>(new Predicate.Long(GTE, value));
    }

    public static ThingConstraint.Predicate<Double> gte(double value) {
        return new ThingConstraint.Predicate<>(new Predicate.Double(GTE, value));
    }

    public static ThingConstraint.Predicate<Boolean> gte(boolean value) {
        return new ThingConstraint.Predicate<>(new Predicate.Boolean(GTE, value));
    }

    public static ThingConstraint.Predicate<String> gte(String value) {
        return new ThingConstraint.Predicate<>(new Predicate.String(GTE, value));
    }

    public static ThingConstraint.Predicate<LocalDateTime> gte(LocalDateTime value) {
        return new ThingConstraint.Predicate<>(new Predicate.DateTime(GTE, value));
    }

    public static ThingConstraint.Predicate<ThingVariable<?>> gte(UnboundDollarVariable variable) {
        return new ThingConstraint.Predicate<>(new Predicate.ThingVariable(GTE, variable.toThing()));
    }

    public static ThingConstraint.Predicate<ValueVariable> gte(UnboundValueVariable variable) {
        return new ThingConstraint.Predicate<>(new Predicate.ValueVariable(GTE, variable.toValue()));
    }

    public static ThingConstraint.Predicate<Long> lt(long value) {
        return new ThingConstraint.Predicate<>(new Predicate.Long(LT, value));
    }

    public static ThingConstraint.Predicate<Double> lt(double value) {
        return new ThingConstraint.Predicate<>(new Predicate.Double(LT, value));
    }

    public static ThingConstraint.Predicate<Boolean> lt(boolean value) {
        return new ThingConstraint.Predicate<>(new Predicate.Boolean(LT, value));
    }

    public static ThingConstraint.Predicate<String> lt(String value) {
        return new ThingConstraint.Predicate<>(new Predicate.String(LT, value));
    }

    public static ThingConstraint.Predicate<LocalDateTime> lt(LocalDateTime value) {
        return new ThingConstraint.Predicate<>(new Predicate.DateTime(LT, value));
    }

    public static ThingConstraint.Predicate<ThingVariable<?>> lt(UnboundDollarVariable variable) {
        return new ThingConstraint.Predicate<>(new Predicate.ThingVariable(LT, variable.toThing()));
    }

    public static ThingConstraint.Predicate<ValueVariable> lt(UnboundValueVariable variable) {
        return new ThingConstraint.Predicate<>(new Predicate.ValueVariable(LT, variable.toValue()));
    }

    public static ThingConstraint.Predicate<Long> lte(long value) {
        return new ThingConstraint.Predicate<>(new Predicate.Long(LTE, value));
    }

    public static ThingConstraint.Predicate<Double> lte(double value) {
        return new ThingConstraint.Predicate<>(new Predicate.Double(LTE, value));
    }

    public static ThingConstraint.Predicate<Boolean> lte(boolean value) {
        return new ThingConstraint.Predicate<>(new Predicate.Boolean(LTE, value));
    }

    public static ThingConstraint.Predicate<String> lte(String value) {
        return new ThingConstraint.Predicate<>(new Predicate.String(LTE, value));
    }

    public static ThingConstraint.Predicate<LocalDateTime> lte(LocalDateTime value) {
        return new ThingConstraint.Predicate<>(new Predicate.DateTime(LTE, value));
    }

    public static ThingConstraint.Predicate<ThingVariable<?>> lte(UnboundDollarVariable variable) {
        return new ThingConstraint.Predicate<>(new Predicate.ThingVariable(LTE, variable.toThing()));
    }

    public static ThingConstraint.Predicate<ValueVariable> lte(UnboundValueVariable variable) {
        return new ThingConstraint.Predicate<>(new Predicate.ValueVariable(LTE, variable.toValue()));
    }

    public static ThingConstraint.Predicate<String> contains(String value) {
        return new ThingConstraint.Predicate<>(new Predicate.String(CONTAINS, value));
    }

    public static ThingConstraint.Predicate<String> like(String value) {
        return new ThingConstraint.Predicate<>(new Predicate.String(LIKE, value));
    }

    public static abstract class Expr {
        public static Expression.Operation op_plus(Expression a, Expression b) {
            return new Expression.Operation(Expression.Operation.OP.PLUS, a, b);
        }

        public static Expression.Operation op_minus(Expression a, Expression b) {
            return new Expression.Operation(Expression.Operation.OP.MINUS, a, b);
        }

        public static Expression.Operation op_times(Expression a, Expression b) {
            return new Expression.Operation(Expression.Operation.OP.TIMES, a, b);
        }

        public static Expression.Operation op_div(Expression a, Expression b) {
            return new Expression.Operation(Expression.Operation.OP.DIV, a, b);
        }

        public static Expression.Operation op_pow(Expression a, Expression b) {
            return new Expression.Operation(Expression.Operation.OP.POW, a, b);
        }

        public static Expression.Function func(String funcId, Expression... args) {
            return func(funcId, list(args));
        }

        public static Expression.Function func(String funcId, List<Expression> args) {
            return new Expression.Function(funcId, args);
        }

        public static Expression bracketed(Expression nestedExpr) {
            return new Expression.Bracketed(nestedExpr);
        }

        public static Expression.ThingVar thingVar(UnboundDollarVariable variable) {
            return new Expression.ThingVar(variable.toThing());
        }

        public static Expression.ValVar valVar(UnboundValueVariable variable) {
            return new Expression.ValVar(variable.toValue());
        }

        public static Expression.Constant.Boolean constant(Boolean value) {
            return new Expression.Constant.Boolean(value);
        }

        public static Expression.Constant.Long constant(long value) {
            return new Expression.Constant.Long(value);
        }

        public static Expression.Constant.Double constant(double value) {
            return new Expression.Constant.Double(value);
        }

        public static Expression.Constant.String constant(String value) {
            return new Expression.Constant.String(value);
        }

        public static Expression.Constant.DateTime constant(LocalDateTime value) {
            return new Expression.Constant.DateTime(value);
        }
    }
}
