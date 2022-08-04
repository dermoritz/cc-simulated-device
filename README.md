### very bare bone - first try - WIP
To get this running just put in your device-provisioning-service parameters.
Just set constants in ProvisioningX509Sample:
- idScope
- globalEndpoint
- leafPublicPem
- leafPrivateKey

you can leave/ push your data but try not to push "leafPrivateKey". 

See https://github.com/Azure/azure-iot-sdk-java/tree/main/provisioning/provisioning-samples/provisioning-X509-sample

I used this doc to extract the key and certificate: https://www.ibm.com/docs/en/arl/9.7?topic=certification-extracting-certificate-keys-from-pfx-file

