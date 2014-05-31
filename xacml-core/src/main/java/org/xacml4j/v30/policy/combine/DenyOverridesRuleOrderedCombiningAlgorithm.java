package org.xacml4j.v30.policy.combine;

/*
 * #%L
 * Artagon XACML 3.0 Core Engine Implementation
 * %%
 * Copyright (C) 2009 - 2014 Artagon
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import org.xacml4j.v30.pdp.Rule;

public final class DenyOverridesRuleOrderedCombiningAlgorithm extends DenyOverrides<Rule>
{
	private final static String ID = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:ordered-deny-overrides";

	public DenyOverridesRuleOrderedCombiningAlgorithm(){
		super(ID);
	}
}