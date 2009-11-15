/*
 * ResourceMapping.java
 *
 * Created on Antradienis, 2007, Balandžio 17, 22.12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package controller;

import dk.semantic_web.diy.controller.Resource;
import frontend.controller.resource.report.ReportListResource;
import frontend.controller.resource.report.ReportResource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import model.Report;
import model.SDB;
import thewebsemantic.RDF2Bean;

/**
 *
 * @author Pumba
 */
public class ResourceMapping extends dk.semantic_web.diy.controller.ResourceMapping
{    
    @Override
    public Resource findByURI(String uri)
    {
	Resource resource = null;
	//Individual instance = Ontology.getJointOntology().getIndividual(URI);
	String[] relativeUris = uri.split("/");
	
	//if (relativeUris.length == 0) return ReportResource.getInstance();
	
	if (relativeUris.length >= 1)
	{
	    if (relativeUris[0].equals(ReportListResource.getInstance().getRelativeURI()))
	    {
		resource = ReportListResource.getInstance();
		if (relativeUris.length >= 2)
		{
		    String fullUri = getHost() + resource.getURI() + relativeUris[1];
                    RDF2Bean reader = new RDF2Bean(SDB.getInstanceModel());
                    //reader.bindAll();
		    Report report = reader.load(Report.class, fullUri);

		    if (report != null)
                    {
                        report.setId(relativeUris[1]);
                        return new ReportResource(report, (ReportListResource)resource);
                    }
		    //return null;
		}
		return resource;
	    }
	}

        return null;
    }

    public static String urlDecode(String url)
    {
	try
	{
	    return URLDecoder.decode(url, "UTF-8");
	}
	catch (UnsupportedEncodingException ex)
	{
	    ex.printStackTrace(System.out);
	}
	return url;
    }

}
