/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.graphity.analytics.model.visualization;

import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author Pumba
 */

//@Namespace("http://code.google.com/apis/visualization/")
public class ScatterChart extends Visualization
{
    private String xBinding = null;
    private Collection<String> yBindings = new LinkedList<String>();

    public String getXBinding()
    {
	return xBinding;
    }

    public void setXBinding(String xBinding)
    {
	this.xBinding = xBinding;
    }

    public Collection<String> getYBindings()
    {
	return yBindings;
    }

    public void setYBindings(Collection<String> yBindings)
    {
	this.yBindings = yBindings;
    }

    public void addYBinding(String yBinding)
    {
	yBindings.add(yBinding);
    }

}