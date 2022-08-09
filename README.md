### very bare bone - first try - WIP
To get this running just put in your device-provisioning-service parameters.
Just set in application.properties:
- idScope
- globalEndpoint

You will find both in DeviceProvisioningService blade in Overview.

Add private-key.properties with private key of leaf certificate to use in this format (don't push):
    
    certificate.private-key=-----BEGIN RSA PRIVATE KEY-----\n\
    MIIJKgIBAAKCAgEA1BHjgtcqmFP+TUdfBYmGU/CqYfEEtD6XaVI8BpJIyx7y58aS\n\
    noPWxek8iM86PL8E8RtMKGZkB29JSLp7BIWuEAgkSZYbtN/cmTCT0r9ctLkURbR9\n\
    L8rsKKxfReX+q6AeV4CWUirGbp+6qEz36GrrbFfleATo2v+p96Qt4LEjAq3LGpDJ\n\
    ZagJzaXN/CkDOOBJZMgDhcYx2fwuABr/T0QMYUMkAPCSPz1jcdkki00rvgkAeSf4\n\
    OlsNsB6foPrL8fz2Z6Q55lh5jG7b2+aMUvKXQILr7CmZI7aVNFvR7cSIMBi9iD4z\n\
    -----END RSA PRIVATE KEY-----

Add public-key.properties with public key in same format:

    certificate.public-key=-----BEGIN CERTIFICATE-----\n\
    MIIFfzCCA2egAwIBAgIBBDANBgkqhkiG9w0BAQsFADAqMSgwJgYDVQQDDB9BenVy\n\
    ZSBJb1QgSHViIENBIENlcnQgVGVzdCBPbmx5MB4XDTIyMDgwNDEzMjg1MVoXDTIy\n\
    MDkwMzEzMjg1MVowFDESMBAGA1UEAwwJamF2YS1zaW0xMIICIjANBgkqhkiG9w0B\n\
    AQEFAAOCAg8AMIICCgKCAgEA1BHjgtcqmFP+TUdfBYmGU/CqYfEEtD6XaVI8BpJI\n\
    d9DpBz67xm2WJJa5p/wE9xB9lA==\n\
    -----END CERTIFICATE-----

Both files must be "added" as additional property source to be found by Spring:
![img.png](img.png)

Alternatively you can add this as VM parameter: ``-Dspring.config.additional-location=classpath:...``



See https://github.com/Azure/azure-iot-sdk-java/tree/main/provisioning/provisioning-samples/provisioning-X509-sample
Tools to create the certificates are found here (with documentation):

https://github.com/Azure/azure-iot-sdk-c/tree/main/tools/CACertificates

I used this doc to extract the key and certificate:
https://www.ibm.com/docs/en/arl/9.7?topic=certification-extracting-certificate-keys-from-pfx-file

