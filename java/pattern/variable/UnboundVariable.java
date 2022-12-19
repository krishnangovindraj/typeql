package com.vaticle.typeql.lang.pattern.variable;

import com.vaticle.typeql.lang.common.exception.TypeQLException;

import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;

public abstract class UnboundVariable extends Variable {
    UnboundVariable(Reference reference) {
        super(reference);
    }

    public boolean isDollarVariable() {
        return false;
    }

    public boolean isEvaluableVariable() {
        return false;
    }

    public UnboundDollarVariable asDollarVariable() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(UnboundDollarVariable.class)));
    }

    public UnboundEvaluableVariable asEvaluableVariable() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(UnboundEvaluableVariable.class)));
    }
}
