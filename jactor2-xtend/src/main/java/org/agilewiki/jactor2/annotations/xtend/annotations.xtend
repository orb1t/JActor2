/*
 * Copyright (C) 2014 Sebastien Diot.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.agilewiki.jactor2.annotations.xtend

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target
import org.eclipse.xtend.lib.macro.Active
import org.agilewiki.jactor2.annotations.xtend.codegen.SReqProcessor
import org.agilewiki.jactor2.annotations.xtend.codegen.AReqProcessor

/** Marks an instance method that should be wrapped in a SyncRequest */
@Active(typeof(SReqProcessor))
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
annotation SReq {
}

/** Marks an instance method that should be wrapped in a AsyncRequest */
@Active(typeof(AReqProcessor))
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
annotation AReq {
}