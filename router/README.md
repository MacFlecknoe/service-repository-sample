Router
===========

Example shows how cross cutting logic should be enforced in part via WS-Policy using camel. Camel is the core technology used by W+P to help realize the integration layer capabilities defined within the SOA reference architecture. As a part of the integation layer camel routes need to be maintained and managed seperately from our services and service contracts (which exist within other logical tiers of the SOA). This is why all routing logic has been pulled into a segragate project/deliverable.

####Technical Details

If you want to access the content of web services messages that pass through a route, including its header information, you need to configure your endpoints to use the PAYLOAD data format: 

```
<camel:from uri="cxf:bean:proxyUserService?dataFormat=PAYLOAD" />
```

When in PAYLOAD format, the body of the exchange is accessible as an XML document (essentially, an org.w3c.dom.Node object). 
The key advantage of using PAYLOAD format is that you can easily process the contents of a message, by accessing the message body as an XML document.

SOAP headers are not available when MESSAGE format is used and SOAP processing (and therefore WS-Policy enforcement) is skipped if a route is configured to use it. Do NOT use the MESSAGE data format in your routes.

####Policy Configuration

Processing of WS-Policy files is done by configuring the cxf bus and by importing external `PolicyAttachment` files:

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

Policies are attached to endpoints/SOAP messages according to `AppliesTo` rules that exist within each `PolicyAttachment` file:

```
<wsp:AppliesTo>
	<wsa:EndpointReference>
		<wsa:Address>/proxy/userService</wsa:Address>
	</wsa:EndpointReference>
</wsp:AppliesTo>
```

Policy files should be stored in the schema project and imported into all service and service routing projects. This allows us to 
centrally govern how policies are executed and enforced without having to modify service code (we simply change the rules in the policy 
files; these policies will then be imported into services via shared storage or as library dependencies). This also enables us to reuse policies across services in accordance with the <a href="http://soapatterns.org/design_patterns/policy_centralization">Policy Centralization</a> design pattern.

#####Additional Information

http://camel.apache.org/cxf.html <br/>
http://fusesource.com/docs/esbent/7.1/camel_cxf/Proxying-Payload.html
http://soapatterns.org/design_patterns/policy_centralization


#####Notes

Due to a bug in cxf any whitespace introduced after the end of the Body tag in a SOAP message will creep into the payload. This causes a problem when signing the the Body as the hash will no longer equate to the hash that was initially generaged by the client. In SOAPUI be sure to set the "Strip Whitespace" setting on your messages to true when using encryption to circumvent this problem when running your tests.
