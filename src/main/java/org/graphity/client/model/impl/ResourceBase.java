/**
 *  Copyright 2013 Martynas Jusevičius <martynas@graphity.org>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.graphity.client.model.impl;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.sun.jersey.api.core.ResourceContext;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Variant;
import org.graphity.client.vocabulary.GC;
import org.graphity.processor.vocabulary.GP;
import org.graphity.server.model.GraphStore;
import org.graphity.server.model.SPARQLEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class of generic read-write Graphity Client resources.
 * Supports pagination on containers (implemented using SPARQL query solution modifiers).
 * 
 * @author Martynas Jusevičius <martynas@graphity.org>
 * @see <a href="http://www.w3.org/TR/sparql11-query/#solutionModifiers">15 Solution Sequences and Modifiers</a>
 */
@Path("/")
public class ResourceBase extends org.graphity.processor.model.impl.ResourceBase
{
    private static final Logger log = LoggerFactory.getLogger(ResourceBase.class);

    private final URI mode;

    /**
     * JAX-RS-compatible resource constructor with injected initialization objects.
     * 
     * @param uriInfo URI information of the current request
     * @param request current request
     * @param servletConfig webapp context
     * @param endpoint SPARQL endpoint of this resource
     * @param graphStore Graph Store of this resource
     * @param ontClass sitemap ontology model
     * @param httpHeaders HTTP headers of the current request
     * @param resourceContext resource context
     */
    public ResourceBase(@Context UriInfo uriInfo, @Context Request request, @Context ServletConfig servletConfig,
            @Context SPARQLEndpoint endpoint, @Context GraphStore graphStore,
            @Context OntClass ontClass, @Context HttpHeaders httpHeaders, @Context ResourceContext resourceContext)
    {
	super(uriInfo, request, servletConfig,
                endpoint, graphStore,
                ontClass, httpHeaders, resourceContext);

        if (getUriInfo().getQueryParameters().containsKey(GC.mode.getLocalName()))
            this.mode = URI.create(getUriInfo().getQueryParameters().getFirst(GC.mode.getLocalName()));
        else mode = null;
    }
    
    @Override
    public void init()
    {
        super.init();
       
        if (getMode() != null &&
            (getMode().equals(URI.create(GC.CreateMode.getURI())) || getMode().equals(URI.create(GC.EditMode.getURI()))) &&
            getQueryBuilder().getSubSelectBuilder() != null) // && getMatchedOntClass().hasSuperClass(LDP.Container)
	{
            if (log.isDebugEnabled()) log.debug("Mode is {}, setting sub-SELECT LIMIT to zero", getMode());
            getQueryBuilder().getSubSelectBuilder().replaceLimit(Long.valueOf(0));
            getQueryBuilder().build();
        }
    }
    
    /**
     * Returns sub-resource instance.
     * By default matches any path.
     * 
     * @return resource object
     */
    @Path("{path: .+}")
    @Override
    public Object getSubResource()
    {
        if (getMatchedOntClass().equals(GP.SPARQLEndpoint))
        {
            List<MediaType> mediaTypes = getMediaTypes();
            mediaTypes.addAll(Arrays.asList(SPARQLEndpoint.RESULT_SET_MEDIA_TYPES));
            List<Variant> variants = getVariantListBuilder(mediaTypes, getLanguages(), getEncodings()).add().build();
            Variant variant = getRequest().selectVariant(variants);
            
            if (variant.getMediaType().isCompatible(MediaType.TEXT_HTML_TYPE) ||
                    variant.getMediaType().isCompatible(MediaType.APPLICATION_XHTML_XML_TYPE))
                return this;
        }
        
        return super.getSubResource();
    }
    
    /**
     * Adds run-time metadata to RDF description.
     * In case a container is requested, page resource with HATEOS previous/next links is added to the model.
     * 
     * @param model target RDF model
     * @return description model with metadata
     */
    @Override
    public Model addMetadata(Model model)
    {
        if (getMode() != null &&
            (getMode().equals(URI.create(GC.CreateMode.getURI())) || getMode().equals(URI.create(GC.EditMode.getURI()))))
	{
            return model;
        }

        return super.addMetadata(model);
    }
    
    /**
     * Builds a list of acceptable response variants
     * 
     * @return supported variants
     */
    @Override
    public List<MediaType> getMediaTypes()
    {
        List<MediaType> list = super.getMediaTypes();
        list.add(0, MediaType.APPLICATION_XHTML_XML_TYPE);

        if (getMode() != null && getMode().equals(URI.create(GC.MapMode.getURI())))
	{
	    if (log.isDebugEnabled()) log.debug("Mode is {}, returning 'text/html' media type as Google Maps workaround", getMode());
            list.add(0, MediaType.TEXT_HTML_TYPE);
	}

        return list;
    }
    
    /**
     * Handles PUT method, stores the submitted RDF model in the default graph of default SPARQL endpoint, and returns response.
     * Redirects to document URI in <pre>gc:EditMode</pre>.
     * 
     * @param model RDF payload
     * @return response
     */
    @Override
    public Response put(Model model)
    {
        if (getMode() != null && getMode().equals(URI.create(GC.EditMode.getURI())))
        {
            super.put(model);
            
            Resource document = getURIResource(model);
	    if (log.isDebugEnabled()) log.debug("Mode is {}, redirecting to document URI {} after PUT", getMode(), document.getURI());
            return Response.seeOther(URI.create(document.getURI())).build();
        }
        
        return super.put(model);
    }
    
    /**
     * Returns the layout mode query parameter value.
     * 
     * @return mode URI
     */
    public URI getMode()
    {
	return mode;
    }

    /**
     * Returns page URI builder.
     * 
     * @return URI builder
     */
    @Override
    public UriBuilder getPageUriBuilder()
    {
	if (getMode() != null) return super.getPageUriBuilder().queryParam(GC.mode.getLocalName(), getMode());
	
	return super.getPageUriBuilder();
    }

    /**
     * Returns previous page URI builder.
     * 
     * @return URI builder
     */
    @Override
    public UriBuilder getPreviousUriBuilder()
    {
	if (getMode() != null) return super.getPreviousUriBuilder().queryParam(GC.mode.getLocalName(), getMode());
	
	return super.getPreviousUriBuilder();
    }

    /**
     * Returns next page URI builder.
     * 
     * @return URI builder
     */
    @Override
    public UriBuilder getNextUriBuilder()
    {
	if (getMode() != null) return super.getNextUriBuilder().queryParam(GC.mode.getLocalName(), getMode());
	
	return super.getNextUriBuilder();
    }
    
}