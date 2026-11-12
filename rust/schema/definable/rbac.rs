/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
use crate::common::Span;
use crate::{Identifier, Label};

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct RBAC {
    pub span: Option<Span>,
    pub kind: Option<()>,
    pub ident: Identifier,
    pub type_access: Vec<RBACTypeAccess>,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum RBACTypeAccess {
    Reads(Vec<Label>),
    Writes(Vec<Label>),
}
