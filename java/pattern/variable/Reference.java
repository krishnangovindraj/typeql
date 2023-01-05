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

import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.exception.TypeQLException;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_VARIABLE_NAME;

public abstract class Reference {

    final Type type;
    final boolean isVisible;

    enum Type {NAMED_DOLLAR, ANONYMOUS, NAMED_VALUE, LABEL}

    Reference(Type type, boolean isVisible) {
        this.type = type;
        this.isVisible = isVisible;
    }

    public static Reference.Name.NamedDollar namedDollar(String name) {
        return new Name.NamedDollar(name);
    }

    public static Name.NamedValue namedValue(String name) {
        return new Name.NamedValue(name);
    }

    public static Reference.Label label(String label) {
        return new Label(label);
    }

    public static Reference.Anonymous anonymous(boolean isVisible) {
        return new Reference.Anonymous(isVisible);
    }

    protected Reference.Type type() {
        return type;
    }

    public abstract String name();

    public String syntax() {
        return TypeQLToken.Char.$ + name();
    }

    protected boolean isVisible() {
        return isVisible;
    }

    public boolean isReferable() {
        return !isAnonymous();
    }

    public boolean isName() {
        return isNamedDollar() || isNamedValue();
    }

    public boolean isNamedDollar() {
        return type == Type.NAMED_DOLLAR;
    }

    public boolean isNamedValue() {
        return type == Type.NAMED_VALUE;
    }

    public boolean isLabel() {
        return type == Type.LABEL;
    }

    public boolean isAnonymous() {
        return type == Type.ANONYMOUS;
    }

    public Referable asReferable() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Referable.class)));
    }

    public Reference.Name asName() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Name.class)));
    }

    public Reference.Name.NamedDollar asNamedDollar() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Name.NamedDollar.class)));
    }

    public Name.NamedValue asNamedValue() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Name.NamedValue.class)));
    }

    public Reference.Label asLabel() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Label.class)));
    }

    public Reference.Anonymous asAnonymous() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Anonymous.class)));
    }

    @Override
    public String toString() {
        return syntax();
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    public static abstract class Referable extends Reference {

        Referable(Type type, boolean isVisible) {
            super(type, isVisible);
        }

        @Override
        public Referable asReferable() {
            return this;
        }
    }

    public static abstract class Name extends Referable {
        Name(Type type, boolean isVisible) {
            super(type, isVisible);
        }

        @Override
        public Reference.Name asName() {
            return this;
        }

        public static class NamedDollar extends Name {

            private static final Pattern REGEX = Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9_-]*");
            protected final String name;
            private final int hash;

            protected NamedDollar(String name) {
                super(Type.NAMED_DOLLAR, true);
                if (!REGEX.matcher(name).matches()) {
                    throw TypeQLException.of(INVALID_VARIABLE_NAME.message(name, REGEX.toString()));
                }
                this.name = name;
                this.hash = Objects.hash(this.type, this.isVisible, this.name);
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public NamedDollar asNamedDollar() {
                return this;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                NamedDollar that = (NamedDollar) o;
                return (this.type == that.type &&
                        this.isVisible == that.isVisible &&
                        this.name.equals(that.name));
            }

            @Override
            public int hashCode() {
                return hash;
            }
        }

        public static class NamedValue extends Name {

            private static final Pattern REGEX = Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9_-]*");
            protected final String name;
            private final int hash;

            protected NamedValue(String name) {
                super(Type.NAMED_VALUE, true);
                if (!REGEX.matcher(name).matches()) {
                    throw TypeQLException.of(INVALID_VARIABLE_NAME.message(name, REGEX.toString()));
                }
                this.name = name;
                this.hash = Objects.hash(this.type, this.isVisible, this.name);
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public boolean isNamedValue() {
                return true;
            }

            @Override
            public NamedValue asNamedValue() {
                return this;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                NamedValue that = (NamedValue) o;
                return (this.type == that.type &&
                        this.isVisible == that.isVisible &&
                        this.name.equals(that.name));
            }

            @Override
            public int hashCode() {
                return hash;
            }

            @Override
            public String syntax() {
                return TypeQLToken.Char.QUESTION_MARK + name();
            }
        }
    }

    public static class Label extends Referable {

        private final String label;
        private final int hash;

        Label(String label) {
            super(Type.LABEL, false);
            this.label = label;
            this.hash = Objects.hash(this.type, this.isVisible, this.label);
        }

        public String label() {
            return label;
        }

        @Override
        public String name() {
            return TypeQLToken.Char.UNDERSCORE.toString() + label;
        }

        @Override
        public Reference.Label asLabel() {
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Label that = (Label) o;
            return (this.type == that.type &&
                    this.isVisible == that.isVisible &&
                    this.label.equals(that.label));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Anonymous extends Reference {

        private final int hash;

        private Anonymous(boolean isVisible) {
            super(Type.ANONYMOUS, isVisible);
            this.hash = Objects.hash(this.type, this.isVisible);
        }

        @Override
        public String name() {
            return TypeQLToken.Char.UNDERSCORE.toString();
        }

        @Override
        public Reference.Anonymous asAnonymous() {
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Anonymous that = (Anonymous) o;
            return (this.type == that.type && this.isVisible == that.isVisible);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
