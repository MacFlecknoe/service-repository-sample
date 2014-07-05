Router
===========

Example shows how cross cutting logic should be enforced in part via WS-Policy using camel. Camel is the core technology used by W+P to help realize the integration layer capabilities defined within the SOA reference architecture. As a part of the integation layer camel routes need to be maintained and managed seperately from our services and service contracts (which exist within other logical tiers of the SOA). This is why all routing logic has been pulled into a segragate project/deliverable.

Of note:

If you want to access the content of web services messages that pass through a route, including its header information, you need to configure your endpoints to use the PAYLOAD data format: 

```
<camel:from uri="cxf:bean:proxyUserService?dataFormat=PAYLOAD" />
```

When in PAYLOAD format, the body of the exchange is accessible as an XML document (essentially, an org.w3c.dom.Node object). 
The key advantage of using PAYLOAD format is that you can easily process the contents of a message, by accessing the message body as an XML document.

SOAP headers are not available in MESSAGE mode and SOAP processing (and therefore WS-Policy enforcement) is skipped when using MESSAGE format. Do NOT use MESSAGE format in your routes.

Processing of WS-Policy files is done by configuring the cxf bus and by importing the process files into the policy engine:

```
<cxf:bus>
	<cxf:features>
		...
		<p:policies />
	</cxf:features>
</cxf:bus>
<p:externalAttachment location="classpath:policy/authentication-policy.xml"/>
<p:externalAttachment location="classpath:policy/i18n-policy.xml"/>
```

Policies are attached to endpoints/SOAP messages according to embedded AppliesTo rules:

```
<wsp:AppliesTo>
	<wsa:EndpointReference>
		<wsa:Address>/proxy/userService</wsa:Address>
	</wsa:EndpointReference>
</wsp:AppliesTo>
```

The policy files are stored centrally in the schema project and imported into all service and service routing projects. This allows us to 
centrally govern which policies get exectued without having to modify any code (we simply change the rules in the policy 
files and they will automatically be applied to the correct URLs/service contracts when the service is republished).

See:

http://camel.apache.org/cxf.html <br/>
http://fusesource.com/docs/esbent/7.1/camel_cxf/Proxying-Payload.html
