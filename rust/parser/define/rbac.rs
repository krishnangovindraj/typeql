/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{
    common::{Spanned, error::TypeQLError},
    parser::{IntoChildNodes, Node, Rule, RuleMatcher, type_::visit_label},
    schema::definable::rbac::{RBAC, RBACTypeAccess},
};

pub(in crate::parser) fn visit_definition_rbac(node: Node<'_>) -> RBAC {
    debug_assert_eq!(node.as_rule(), Rule::definition_rbac);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::RBAC);
    let label = visit_label(children.consume_expected(Rule::label));
    let type_access = children.map(visit_rbac_access).collect();
    RBAC::new(span, label, type_access)
}

pub(in crate::parser) fn visit_rbac_access(node: Node<'_>) -> RBACTypeAccess {
    debug_assert_eq!(node.as_rule(), Rule::rbac_access);
    let mut children = node.into_children();
    let keyword = children.consume_any();
    let labels = children.map(visit_label).collect();
    match keyword.as_rule() {
        Rule::READS => RBACTypeAccess::Reads(labels),
        Rule::WRITES => RBACTypeAccess::Writes(labels),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: keyword.as_str().to_owned() }),
    }
}
