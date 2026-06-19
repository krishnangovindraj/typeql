/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    Literal,
    common::{Span, Spanned},
    pretty::Pretty,
    schema::definable::function::Argument,
    statement::thing::Iid,
    token::Keyword,
    util::write_joined,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum GivenRowEntry {
    None,
    Iid(Iid),
    Literal(Literal),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct GivenRow(Vec<GivenRowEntry>);

impl GivenRow {
    pub fn new(entries: Vec<GivenRowEntry>) -> Self {
        Self(entries)
    }
}

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

impl fmt::Display for GivenRowEntry {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::None => write!(f, "none"),
            Self::Iid(iid) => write!(f, "{}", iid.iid),
            Self::Literal(lit) => write!(f, "{}", lit),
        }
    }
}

impl fmt::Display for GivenRow {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "    [")?;
        write_joined!(f, ", ", self.0)?;
        write!(f, "]")
    }
}

impl Pretty for Given {}

impl fmt::Display for Given {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} ", Keyword::Given)?;
        write_joined!(f, ", ", self.variables)?;
        if let Some(rows) = &self.inline_rows {
            write!(f, " {} [\n", Keyword::In)?;
            write_joined!(f, ",\n", rows)?;
            write!(f, "\n]")?;
        }
        write!(f, ";")?;
        Ok(())
    }
}
