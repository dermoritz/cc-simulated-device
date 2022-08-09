package com.bosch.ccsimulateddevice.configuration;

import com.google.common.base.Preconditions;
import com.microsoft.azure.sdk.iot.provisioning.security.SecurityProvider;
import com.microsoft.azure.sdk.iot.provisioning.security.hsm.SecurityProviderX509Cert;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.Key;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;

/**
 * Encapsulates creation of {@link SecurityProvider}. Code is based on given JavaExample:
 * <a href="https://github.com/Azure/azure-iot-sdk-java/blob/main/provisioning/provisioning-samples/provisioning-X509-sample/src/main/java/samples/com/microsoft/azure/sdk/iot/ProvisioningX509Sample.java">azure-iot-sdk-java DPS x509sample</a>
 * (from August 2022)
 */
@Configuration
public class SecurityProviderFactory {
    private final Certificate certificateData;

    private final SecurityProvider instance;

    public SecurityProviderFactory(Certificate certificateData){
        this.certificateData = Preconditions.checkNotNull(certificateData);
        this.instance = new SecurityProviderX509Cert(parsePublicKeyCertificate(), parsePrivateKey(), Collections.emptyList());
    }


    @Bean
    public SecurityProvider get(){
        return  instance;
    }

    private X509Certificate parsePublicKeyCertificate()
    {
        Security.addProvider(new BouncyCastleProvider());
        PemReader publicKeyCertificateReader = new PemReader(new StringReader(certificateData.getPublicKey()));
        PemObject possiblePublicKeyCertificate;
        try {
            possiblePublicKeyCertificate = publicKeyCertificateReader.readPemObject();
        } catch (IOException e) {
            throw new IllegalStateException("Problem reading given device x.509 public key: ", e);
        }
        CertificateFactory certFactory;
        try {
            certFactory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(possiblePublicKeyCertificate.getContent()));
        } catch (CertificateException e) {
            throw new IllegalStateException("Problem creating certificate for given public key: ", e);
        }

    }

    private Key parsePrivateKey()
    {
        Security.addProvider(new BouncyCastleProvider());
        PEMParser privateKeyParser = new PEMParser(new StringReader(certificateData.getPrivateKey()));
        Object possiblePrivateKey;
        try {
            possiblePrivateKey = privateKeyParser.readObject();
        } catch (IOException e) {
            throw new IllegalStateException("Problem reading private Key info: ", e);
        }
        return getPrivateKey(possiblePrivateKey);
    }

    private static Key getPrivateKey(Object possiblePrivateKey)
    {
        if (possiblePrivateKey instanceof PEMKeyPair)
        {
            try {
                return new JcaPEMKeyConverter().getKeyPair((PEMKeyPair) possiblePrivateKey)
                        .getPrivate();
            } catch (PEMException e) {
                throw new IllegalStateException("Problem creating Key from Pem key pair: ", e);
            }
        }
        else if (possiblePrivateKey instanceof PrivateKeyInfo)
        {
            try {
                return new JcaPEMKeyConverter().getPrivateKey((PrivateKeyInfo) possiblePrivateKey);
            } catch (PEMException e) {
                throw new IllegalStateException("Problem creating Key from Private Key Info: ", e);
            }
        }
        else
        {
            throw new IllegalStateException("Unable to parse private key, type unknown");
        }
    }
}
