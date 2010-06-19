/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package frontend.view.report;

import javax.xml.transform.TransformerConfigurationException;
import model.SDB;
import frontend.controller.resource.report.ReportResource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import view.QueryStringBuilder;
import view.QueryXMLResult;

/**
 *
 * @author Pumba
 */
public class ReportUpdateView extends ReportView
{

    public ReportUpdateView(ReportResource resource) throws TransformerConfigurationException
    {
	super(resource);
	setStyleSheet(new File(getController().getServletConfig().getServletContext().getRealPath("/xslt/report/ReportCreateView.xsl")));
    }

    @Override
    public ReportResource getResource()
    {
	return (ReportResource)super.getResource();
    }
    
    @Override
    public void display(HttpServletRequest request, HttpServletResponse response) throws IOException, TransformerException, ParserConfigurationException
    {
        setEndpoints(request, response);
	//setQueryResult(request, response);
        setDataTypes(request, response);

	super.display(request, response);

	response.setStatus(HttpServletResponse.SC_OK);
    }

    protected void setDataTypes(HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, IOException
    {
	String dataTypes = QueryXMLResult.select(SDB.getDataset(), QueryStringBuilder.build(getController().getServletConfig().getServletContext().getRealPath("/sparql/ontology/data-types.rq")));

	getResolver().setArgument("data-types", dataTypes);
    }
}
