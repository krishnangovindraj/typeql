/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{common::{Span, Spanned}, pretty::Pretty, schema::definable::function::Argument, token::Keyword, util::write_joined, Literal};
use crate::statement::thing::Iid;

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum GivenRowEntry {
    None,
    Iid(Iid),
    Literal(Literal),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct  GivenRow(Vec<GivenRowEntry>);

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Given {
    pub span: Option<Span>,
    pub variables: Vec<Argument>,
    pub inline_rows: Option<Vec<GivenRow>>,
}

impl Given {
    pub fn new(span: Option<Span>, variables: Vec<Argument>, inline_rows: Option<Vec<GivenRow>>) -> Self {
        Self { span, variables, inline_rows }
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
        if let Some(rows) = &self.inline_rows {
            writeln!(f, "{} [", Keyword::In)?;
            write_joined!(f, ",\n", rows)?;
            writeln!(f, "];")?;
        }
        write!(f, ";")?;
        Ok(())
    }
}
