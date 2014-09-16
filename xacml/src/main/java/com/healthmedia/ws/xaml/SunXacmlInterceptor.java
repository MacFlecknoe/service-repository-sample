package com.healthmedia.ws.xaml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.cxf.message.Message;
import org.apache.cxf.rt.security.xacml.*;
import org.jboss.security.xacml.sunxacml.PDP;
import org.jboss.security.xacml.sunxacml.PDPConfig;
import org.jboss.security.xacml.sunxacml.ctx.RequestCtx;
import org.jboss.security.xacml.sunxacml.ctx.ResponseCtx;
import org.jboss.security.xacml.sunxacml.finder.AttributeFinder;
import org.jboss.security.xacml.sunxacml.finder.AttributeFinderModule;
import org.jboss.security.xacml.sunxacml.finder.PolicyFinder;
import org.jboss.security.xacml.sunxacml.finder.PolicyFinderModule;
import org.jboss.security.xacml.sunxacml.finder.impl.CurrentEnvModule;
import org.jboss.security.xacml.sunxacml.finder.impl.FilePolicyModule;
import org.jboss.security.xacml.sunxacml.finder.impl.SelectorModule;
import org.opensaml.xacml.ctx.RequestType;
import org.opensaml.xacml.ctx.ResponseType;
import org.opensaml.xacml.ctx.impl.ResponseTypeUnmarshaller;
import org.opensaml.xml.XMLObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This filter acts as the Policy Enforcement Point in the XACML reference architecture. It forwards requests to the Sun XACML Policy Decision Point implementation which is configured here
 * locally but can be moved to another server in the future if deemed necessary. As all calls are local and executed within the integration layer of the SOA policys can be managed as
 * a set of files.
 * 
 * @author mlamber7
 *
 */
public class SunXacmlInterceptor extends AbstractXACMLAuthorizingInterceptor {
	
	private final PDP pdp;
	private final ResponseTransformer transformer;
	
	public SunXacmlInterceptor(PolicyFinderModule finderModule) {
		//
		// initialize the PDP
		//
		Set<PolicyFinderModule> policyModules = new HashSet<PolicyFinderModule>();
		policyModules.add(finderModule);
		
		List<AttributeFinderModule> attrModules = new ArrayList<AttributeFinderModule>();
		attrModules.add(new CurrentEnvModule());
		attrModules.add(new SelectorModule());
		
		PolicyFinder policyFinder = new PolicyFinder();
		policyFinder.setModules(policyModules);
		
		AttributeFinder attFinder = new AttributeFinder();
		attFinder.setModules(attrModules);
		
		this.pdp = new PDP(new PDPConfig(attFinder, policyFinder, null));
		this.transformer = new ResponseTransformer();
	}
	
	public SunXacmlInterceptor(List<String> filePaths) {
		this(new FilePolicyModuleBuilder(filePaths).build());
	}
	
	public SunXacmlInterceptor() {
		this(new FilePolicyModuleBuilder("/home/mlamber7/Programming/service-repository-sample/xacml/src/main/resources/sample_xacml.xml").build());
	}

	@Override
	public ResponseType performRequest(RequestType request, Message message) throws Exception {
		//
		// EnvironmentType envType = request.getEnvironment();
		// may add IP address here
		//
		// ActionType actionType = request.getAction();
		//
		// List<SubjectType> subjectTypes = request.getSubjects();
		//
		// List<ResourceType> resourceTypes = request.getResources();
		// may add accessCode here
		//
		RequestCtx ctx = RequestCtx.getInstance(request.getDOM());
		ResponseCtx responseCtx = pdp.evaluate(ctx);
		
		return transformer.transform(responseCtx);
	}
	
	private static class FilePolicyModuleBuilder {
		
		private final List<String> filePaths;
		
		public FilePolicyModuleBuilder(List<String> filePaths) {
			this.filePaths = filePaths;
		}
		
		public FilePolicyModuleBuilder(String filePath) {
			this(Arrays.asList(filePath));
		}
		
		public FilePolicyModule build() {
			
			FilePolicyModule policyModule = new FilePolicyModule();
			for(String filePath : filePaths) {
				policyModule.addPolicy(filePath);
			}
			return policyModule;
		}
	}
	
	/**
	 * Serialize to standard XML and leverage API marshallers to do the conversion for us. This is expensive but the reduction in
	 * complexity is worth the nominal overhead.
	 * 
	 * @author mlamber7
	 *
	 */
	private static class ResponseTransformer {
		
		private final DocumentBuilderFactory factory;
		
		public ResponseTransformer() {
			this.factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true); // this is required. it was a hard lesson learned.
		}
		
		public ResponseType transform(ResponseCtx responseCtx) throws Exception {
			
			DocumentBuilder db = factory.newDocumentBuilder();
			
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			responseCtx.encode(outStream);
			
			Document doc = db.parse(new ByteArrayInputStream(outStream.toByteArray()));
			//
			// opensaml requires that the oasis namespace be attached to the document 
			// before unmarshalling
			//
			appendNamespace(doc.getDocumentElement(), "urn:oasis:names:tc:xacml:2.0:context:schema:os");
			
			XMLObject responseType = new ResponseTypeUnmarshaller().unmarshall(doc.getDocumentElement());
			
			return (ResponseType) responseType;
		}
		
		private void appendNamespace(Node node, String namespace) {
			
			Document document = node.getOwnerDocument();
			
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				document.renameNode(node, namespace, node.getNodeName());
			}
			NodeList list = node.getChildNodes();
			for (int i = 0; i < list.getLength(); ++i) {
				appendNamespace(list.item(i), namespace);
			}
		}
	}
}
