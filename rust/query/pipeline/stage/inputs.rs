use std::fmt;
use crate::common::{Span, Spanned};
use crate::pretty::Pretty;
use crate::schema::definable::function::Argument;
use crate::token::Keyword;
use crate::util::write_joined;
use crate::Variable;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Inputs {
    pub span: Option<Span>,
    pub variables: Vec<Argument>,
}

impl Inputs {
    pub fn new(span: Option<Span>, variables: Vec<Argument>) -> Self {
        Self { span, variables }
    }
}

impl Spanned for Inputs {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Inputs {}

impl fmt::Display for Inputs {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} ", Keyword::Inputs)?;
        write_joined!(f, ", ", self.variables)?;
        Ok(())
    }
}