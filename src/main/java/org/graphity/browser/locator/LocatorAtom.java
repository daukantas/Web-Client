/*
 * Copyright (C) 2012 Martynas Jusevičius <martynas@graphity.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graphity.browser.locator;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.xml.transform.stream.StreamSource;
import org.graphity.util.locator.LocatorGRDDL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Martynas Jusevičius <martynas@graphity.org>
 */
public class LocatorAtom extends LocatorGRDDL
{
    private static final Logger log = LoggerFactory.getLogger(LocatorAtom.class);

    public LocatorAtom(ServletContext context) throws MalformedURLException, URISyntaxException
    {
	super(new StreamSource(context.getResource("/WEB-INF/xsl/grddl/atom-grddl.xsl").toURI().toString()));
    }

    @Override
    public String getName()
    {
	return "LocatorAtom";
    }

    @Override
    public Map<String, Double> getQualifiedTypes()
    {
	Map<String, Double> xmlType = new HashMap<String, Double>();
	xmlType.put("application/atom+xml", null);
	return xmlType;
    }
    
}