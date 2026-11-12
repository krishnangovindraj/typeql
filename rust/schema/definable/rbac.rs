/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{Span, Spanned, token},
    pretty::{Pretty, indent},
    type_::Label,
};

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct RBAC {
    pub span: Option<Span>,
    pub kind: Option<()>,
    pub label: Label,
    pub type_access: Vec<RBACTypeAccess>,
}

impl RBAC {
    pub fn new(span: Option<Span>, label: Label, type_access: Vec<RBACTypeAccess>) -> Self {
        Self { span, kind: None, label, type_access }
    }
}

impl Spanned for RBAC {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for RBAC {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Keyword::Rbac, self.label)?;
        for access in &self.type_access {
            write!(f, ",")?;
            writeln!(f)?;
            indent(indent_level, f)?;
            write!(f, "{}", access)?;
        }
        f.write_str(";")
    }
}

impl fmt::Display for RBAC {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Keyword::Rbac, self.label)?;
        for access in &self.type_access {
            write!(f, ", {}", access)?;
        }
        f.write_str(";")
    }
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum RBACTypeAccess {
    Reads(Vec<Label>),
    Writes(Vec<Label>),
}

impl fmt::Display for RBACTypeAccess {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        let (access, labels) = match self {
            Self::Reads(labels) => (token::Keyword::Reads, labels),
            Self::Writes(labels) => (token::Keyword::Writes, labels),
        };

        write!(f, "{} {{ {}", access, labels[0])?;
        for label in &labels[1..] {
            write!(f, ", {}", label)?;
        }
        write!(f, " }}")
    }
}
