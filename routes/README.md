Router Spring Project
===========

Example shows how WS-Policy should be processed in the integration layer of the SOA architecture represented by the camel libraries.

Of note:

If you want to access the content of the Web services messages that pass through a route, including its header information, you need to process the them 
in the normal PAYLOAD format: 

<camel:from uri="cxf:bean:proxyUserService?dataFormat=PAYLOAD" />

When in PAYLOAD format, the body of the exchange is accessible as an XML document (essentially, an org.w3c.dom.Node object). 
The key advantage of using PAYLOAD format is that you can easily process the contents of a message, by accessing the message body as an XML document.

SOAP headers are not available in MESSAGE mode and SOAP processing (and therefore WS-Policy enforcement) is skipped. This caused me some confusion at 
first but makes sense: only the MESSAGE body is passed forward in a route in MESSAGE mode; the envelope is ignored.

Processing of the WS-Policy docs is done via the cxf bus:

<cxf:bus>
	<cxf:features>
		...
		<p:policies />
	</cxf:features>
</cxf:bus>

Where policy files are feed into the engine as selected according to embedded AppliesTo rules:

<wsp:AppliesTo>
	<wsa:EndpointReference>
		<wsa:Address>/proxy/userService</wsa:Address>
	</wsa:EndpointReference>
</wsp:AppliesTo>
		
The policy files are stored centrally in the schema project and imported into all service projects. This allows us to 
centrally govern which policies get exectued without having to checkout the actual code (we simply modify the rules in the policy 
files).

See:
http://camel.apache.org/cxf.html
http://fusesource.com/docs/esbent/7.1/camel_cxf/Proxying-Payload.html
