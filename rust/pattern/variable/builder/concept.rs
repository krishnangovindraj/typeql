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
 *
 */

use crate::{pattern::*, ErrorMessage};

pub trait ConceptConstrainable {
    fn constrain_is(self, is: IsConstraint) -> ConceptVariable;
}

pub trait ConceptVariableBuilder: Sized {
    fn is(self, is: impl Into<IsConstraint>) -> Result<ConceptVariable, ErrorMessage>;
}

impl<U: ConceptConstrainable> ConceptVariableBuilder for U {
    fn is(self, is: impl Into<IsConstraint>) -> Result<ConceptVariable, ErrorMessage> {
        Ok(self.constrain_is(is.into()))
    }
}

impl<U: ConceptVariableBuilder> ConceptVariableBuilder for Result<U, ErrorMessage> {
    fn is(self, is: impl Into<IsConstraint>) -> Result<ConceptVariable, ErrorMessage> {
        self?.is(is)
    }
}