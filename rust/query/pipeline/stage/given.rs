use std::fmt;
use crate::common::{Span, Spanned};
use crate::pretty::Pretty;
use crate::schema::definable::function::Argument;
use crate::token::Keyword;
use crate::util::write_joined;
use crate::Variable;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Given {
    pub span: Option<Span>,
    pub variables: Vec<Argument>,
}

impl Given {
    pub fn new(span: Option<Span>, variables: Vec<Argument>) -> Self {
        Self { span, variables }
    }
}

impl Spanned for Given {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Given {}

impl fmt::Display for Given {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} ", Keyword::Given)?;
        write_joined!(f, ", ", self.variables)?;
        Ok(())
    }
}
