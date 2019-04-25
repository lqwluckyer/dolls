package com.game.sdk.dolls.utils;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtils {
    public static final String RSA = "RSA";
    public static final String MD5withRSA = "MD5withRSA";

    private static final String RSA_PRI="MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCeHJC17mwe7pUymsEgk3X9l5cEHjUE+N3FuQnBcLN+bCTwBx3bdet2ZvZsqaFBq/KH4NHB2IWmH5Qm8qc7AFR+3W6+YVBqzITjc+K6C14QP0DR4appEuOFnlTj3c4p1wmQ1cIv12tIxtx3ku9zLlM4mK4E3vQoYOqbpHap3DpdyfJ0bKjt24L+yDkyMX2gF/xeoLJ/bsWqNVAVnn67RdVojbGIHP3zkRlMvXfpmVC02UVYpLouyMUPD4+4vfa1Tyi04aaTHkrySMwYDzm5fKUbzTDnRnaKPPesfvZSdkyUPTsD2bqFKTVPeRPOPNqPy8MbAH00IldrSf7wK9PgbBG9AgMBAAECggEAHx84TNdVaPFFZOSZRzQ/cV85VpIrlYL3BEhb5zur2SKoUqbT9bLMWk4CC3mISEj0QFK8dw+LnxtwKgpxaGe0OhSUC91T2vHO5oaVBlHefgw1Uhq9VFlnd5Gm3AN45TtyopOw2YHpmDWsh8zgXSetMeK7vjG0lY4eH+zLfrvesT8TW0T0rWnbBbaJzS5S/rcFJ72rPU5VNt/7y8reBzIsQ7jAI5rsraptBbbu9QVf53BjareaGA10EFikSMdVSKhqFVZu+1NLpaxrGgXtkRCvgmhX6xsrrxU4l/ZvfX86t0Mf98Vj5SkMsm8rnf0HdMNg57HjO6ffORB1Dy2vL201cQKBgQDfYlbO6JIIP0pE3FFmUbAz6WWsqxrDUZAuA064Ab8D3f3A2xafqfcJhT7X2ocbTNOPzW+mJBG/1SbN/WhK5AC9z9eBP9KP/x/8LC1wa2IjKECo5qoqTM5awJ2GuEwno4r3cgzMFeg12r27/1ehQg+FFUTQ0MhMdqQXWY1Z2pKCUwKBgQC1MndUxjWL5xJTMy8SC+yq37PHna8CATlclDQdrx+2/aSU590qH4xa3qym90AmtHWT+93nMvgwX4VwqCHf5BXskQulznK55g95gfgFVhOCw15JOU5Km48BoYVdvjp+CMqRx9TL7uDArUC9ZDIxkXguPoN+ZYNDYEshn0vf7K25rwKBgHJj0UAzKFTA1C9fAu/6dNigdEhvdkz+v25Qk0b23fIFA6R2jKdGShK+AOhYJ0d7l/rToJDbd5tcc3qYgYeYKikI/bpaDGFgq+heVwZydta6DiJvvEPdAO7II+KRDa5euRLtfFAmB7tMQEy8/Qq+4WOs+IhV4bwGJREv8opIuy2xAoGAQ30qiT7STuugHbEgxR3/5p0vvxfySVLfZKYmyy+hJm1O0c+Sxs+9XVM3B3FsSX5JlPbo7eZEHvBVKWLWSygPKm/T4MwGW0tfVCieko4+8iOGiMt5Z7yNBVR98i6aZrsriCTgE51DxUBCrsgVEfVWyBKss3Vn8L56/Wjxv9PN2S0CgYB8d7pnmSHF8yq+wZHcPcaai9mRCG57iMDNUe0bKWjOQILc5gHTeyveU8jeu3eJJggCVsXntCAo+yx1S4sbqo70WJrurhloK1TMKs24SYZsqZckp+8qdP8d8gD4gs8ovJYJFfMulxicYafI4rWaCqw5yIQPL4ErCtIUDpiF+O3pEg==";
    private static final String RSA_PUB="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnhyQte5sHu6VMprBIJN1/ZeXBB41BPjdxbkJwXCzfmwk8Acd23Xrdmb2bKmhQavyh+DRwdiFph+UJvKnOwBUft1uvmFQasyE43PiugteED9A0eGqaRLjhZ5U493OKdcJkNXCL9drSMbcd5Lvcy5TOJiuBN70KGDqm6R2qdw6XcnydGyo7duC/sg5MjF9oBf8XqCyf27FqjVQFZ5+u0XVaI2xiBz985EZTL136ZlQtNlFWKS6LsjFDw+PuL32tU8otOGmkx5K8kjMGA85uXylG80w50Z2ijz3rH72UnZMlD07A9m6hSk1T3kTzjzaj8vDGwB9NCJXa0n+8CvT4GwRvQIDAQAB";
    /**
     * 签名处理
     *
     * @param priKey：RSA 私钥
     * @param data：签名源内容
     * @return
     */
    public static String sign(String priKey, String data) {
        try {
            byte[] keyArr = Base64.getDecoder().decode(priKey.getBytes(StandardCharsets.UTF_8));
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(keyArr);
            KeyFactory factory = KeyFactory.getInstance(RSA);
            PrivateKey privateKey = factory.generatePrivate(priPKCS8);
            // 用私钥对信息生成数字签名
            Signature signature = Signature.getInstance(MD5withRSA);
            signature.initSign(privateKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            byte[] signatureArr = signature.sign(); // 对信息的数字签名
            return new String(Base64.getEncoder().encode(signatureArr));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 签名验证
     *
     * @param pubKey：RSA 公钥
     * @param data：待签名源串
     * @param sign：签名结果串
     * @return
     */
    public static boolean checkSign(String pubKey, String data, String sign) {
        try {
            byte[] keyArr = Base64.getDecoder().decode(pubKey.getBytes(StandardCharsets.UTF_8));
            X509EncodedKeySpec pubPKCS8 = new X509EncodedKeySpec(keyArr);
            KeyFactory factory = KeyFactory.getInstance(RSA);
            PublicKey publicKey = factory.generatePublic(pubPKCS8);
            byte[] signArr = Base64.getDecoder().decode(sign.getBytes(StandardCharsets.UTF_8));
            Signature signature = Signature.getInstance(MD5withRSA);
            signature.initVerify(publicKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            return signature.verify(signArr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }

    private static void generateKeys() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        // 公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        System.out.println(new String(Base64.getEncoder().encode(publicKey.getEncoded())));

        // 私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        System.out.println(new String(Base64.getEncoder().encode(privateKey.getEncoded())));

    }

    private static void test(){
        String data = "{\"appID\":[\"74\"],\"channelID\":[\"99104\"],\"deviceID\":[\"a9c7fa71-438c-385d-991e-8ff040c99e2f\"],\"extension\":[\"{\\\"uid\\\":\\\"maowan86080407\\\",\\\"uuid\\\":\\\"869435034413573\\\",\\\"ucid\\\":\\\"7147306\\\"}\"],\"sdkVersionCode\":[\"1\"],\"sign\":[\"18955be0fd68fc3886aaa0f2a39fb46b\"]} KRWujOrExWbDyJ5jH7jDWgwrBM3lJAuiAW2GBDKAQIqKMc2UbenYb31reIPYNcunrsIeXz+AZbC2fnxx7G9jWGe/zdzm+fiRj6YtPhOUo1nkNd2KYQJklMuNN+zyU6lJOmKjJ+xw5jWlllrcek+RJONiQpyHTtLM8dYtWz5S3p0=\n" +
                "k0xAfRELPBZ4ShRK5tJ45FGCzD+ei0qTncoI19EEnBeSnbyp/vS6SWKQQ7rsAqjDhjUxpnFAJSAe53le23lgXUHUeb1tIX5gITuCTAvHbLMSawOsvKNLEABajLnDmkfNSSrdFqXIVt1eKH9bd1wEAZlOIO4XOrsXpAOIP6mtgrL8\n" +
                "XVTnaf0jSU/0+GBqy6PTXac+6rz7Vnkk6FIFIJbpiBdFc7JdC144WqgLLViJqw3/mDlSV+f/AWKJHkcM8wy12xVhLIZd/0Uf6MDfx92AdPN2rrDDIS9huTJnFbo4e6La5UB9zxsJI/BvR2MAz+RCjNchI4VhvtJU1Uaa4t5nLg==KRWujOrExWbDyJ5jH7jDWgwrBM3lJAuiAW2GBDKAQIqKMc2UbenYb31reIPYNcunrsIeXz+AZbC2fnxx7G9jWGe/zdzm+fiRj6YtPhOUo1nkNd2KYQJklMuNN+zyU6lJOmKjJ+xw5jWlllrcek+RJONiQpyHTtLM8dYtWz5S3p0=\n" +
                "k0xAfRELPBZ4ShRK5tJ45FGCzD+ei0qTncoI19EEnBeSnbyp/vS6SWKQQ7rsAqjDhjUxpnFAJSAe53le23lgXUHUeb1tIX5gITuCTAvHbLMSawOsvKNLEABajLnDmkfNSSrdFqXIVt1eKH9bd1wEAZlOIO4XOrsXpAOIP6mtgrL8\n" +
                "XVTnaf0jSU/0+GBqy6PTXac+6rz7Vnkk6FIFIJbpiBdFc7JdC144WqgLLViJqw3/mDlSV+f/AWKJHkcM8wy12xVhLIZd/0Uf6MDfx92AdPN2rrDDIS9huTJnFbo4e6La5UB9zxsJI/BvR2MAz+RCjNchI4VhvtJU1Uaa4t5nLg==KRWujOrExWbDyJ5jH7jDWgwrBM3lJAuiAW2GBDKAQIqKMc2UbenYb31reIPYNcunrsIeXz+AZbC2fnxx7G9jWGe/zdzm+fiRj6YtPhOUo1nkNd2KYQJklMuNN+zyU6lJOmKjJ+xw5jWlllrcek+RJONiQpyHTtLM8dYtWz5S3p0=\n" +
                "k0xAfRELPBZ4ShRK5tJ45FGCzD+ei0qTncoI19EEnBeSnbyp/vS6SWKQQ7rsAqjDhjUxpnFAJSAe53le23lgXUHUeb1tIX5gITuCTAvHbLMSawOsvKNLEABajLnDmkfNSSrdFqXIVt1eKH9bd1wEAZlOIO4XOrsXpAOIP6mtgrL8\n" +
                "XVTnaf0jSU/0+GBqy6PTXac+6rz7Vnkk6FIFIJbpiBdFc7JdC144WqgLLViJqw3/mDlSV+f/AWKJHkcM8wy12xVhLIZd/0Uf6MDfx92AdPN2rrDDIS9huTJnFbo4e6La5UB9zxsJI/BvR2MAz+RCjNchI4VhvtJU1Uaa4t5nLg==";

        String signStr = sign(RSA_PRI, data);

        System.out.println(signStr);

        boolean rs = checkSign(RSA_PUB, data, signStr);
        System.out.println(rs);

    }

    public static void main(String[] args) throws Exception{
        //generateKeys();
        test();
    }

}

