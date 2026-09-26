package ua.foxminded.university.storage.s3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import ua.foxminded.university.storage.PresignedUpload;
import ua.foxminded.university.storage.UploadPresigner;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class S3UploadPresigner implements UploadPresigner {

    private static final String HTTP_METHOD = "POST";
    private static final String SIGNING_ALGORITHM =
            "AWS4-HMAC-SHA256";
    private static final String SERVICE = "s3";
    private static final String TERMINATOR = "aws4_request";
    private static final String HMAC_SHA_256 = "HmacSHA256";

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter
                    .ofPattern("yyyyMMdd")
                    .withZone(ZoneOffset.UTC);

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter
                    .ofPattern("yyyyMMdd'T'HHmmss'Z'")
                    .withZone(ZoneOffset.UTC);

    private final ObjectMapper objectMapper;
    private final AwsCredentialsProvider credentialsProvider;
    private final String bucket;
    private final String region;
    private final Duration uploadUrlTtl;
    private final Clock clock;
    private final URL uploadUrl;

    public S3UploadPresigner(
            ObjectMapper objectMapper,
            AwsCredentialsProvider credentialsProvider,
            AwsRegionProvider regionProvider,
            String bucket,
            Duration uploadUrlTtl,
            Clock clock
    ) {
        this.objectMapper = Objects.requireNonNull(
                objectMapper,
                "Object mapper must not be null."
        );

        this.credentialsProvider = Objects.requireNonNull(
                credentialsProvider,
                "AWS credentials provider must not be null."
        );

        Objects.requireNonNull(
                regionProvider,
                "AWS region provider must not be null."
        );

        this.clock = Objects.requireNonNull(
                clock,
                "Clock must not be null."
        );

        if (bucket == null || bucket.isBlank()) {
            throw new IllegalArgumentException(
                    "S3 bucket must not be blank."
            );
        }

        if (uploadUrlTtl == null
                || uploadUrlTtl.isZero()
                || uploadUrlTtl.isNegative()) {
            throw new IllegalArgumentException(
                    "Upload URL TTL must be positive."
            );
        }

        Region resolvedRegion = Objects.requireNonNull(
                regionProvider.getRegion(),
                "AWS region must not be null."
        );

        this.bucket = bucket.strip();
        this.region = resolvedRegion.id();
        this.uploadUrlTtl = uploadUrlTtl;
        this.uploadUrl = createUploadUrl(
                this.bucket,
                this.region
        );
    }

    @Override
    public PresignedUpload createUpload(
            String objectKey,
            String contentType,
            long expectedSizeBytes
    ) {
        validateUploadRequest(
                objectKey,
                contentType,
                expectedSizeBytes
        );

        Instant issuedAt = Instant.now(clock);
        Instant expiresAt = issuedAt.plus(uploadUrlTtl);

        AwsCredentials credentials = Objects.requireNonNull(
                credentialsProvider.resolveCredentials(),
                "AWS credentials must not be null."
        );

        String date = DATE_FORMAT.format(issuedAt);
        String dateTime = DATE_TIME_FORMAT.format(issuedAt);

        String credentialScope = String.join(
                "/",
                date,
                region,
                SERVICE,
                TERMINATOR
        );

        String credential = credentials.accessKeyId()
                + "/"
                + credentialScope;

        String encodedPolicy = createEncodedPolicy(
                objectKey,
                contentType,
                expectedSizeBytes,
                expiresAt,
                credentials,
                credential,
                dateTime
        );

        String signature = createSignature(
                credentials.secretAccessKey(),
                date,
                encodedPolicy
        );

        Map<String, String> formFields =
                createFormFields(
                        objectKey,
                        contentType,
                        credentials,
                        credential,
                        dateTime,
                        encodedPolicy,
                        signature
                );

        return new PresignedUpload(
                uploadUrl,
                HTTP_METHOD,
                expiresAt,
                contentType,
                formFields
        );
    }

    private String createEncodedPolicy(
            String objectKey,
            String contentType,
            long expectedSizeBytes,
            Instant expiresAt,
            AwsCredentials credentials,
            String credential,
            String dateTime
    ) {
        List<Object> conditions = new ArrayList<>();

        conditions.add(Map.of("bucket", bucket));
        conditions.add(Map.of("key", objectKey));
        conditions.add(Map.of("Content-Type", contentType));
        conditions.add(Map.of(
                "x-amz-algorithm",
                SIGNING_ALGORITHM
        ));
        conditions.add(Map.of(
                "x-amz-credential",
                credential
        ));
        conditions.add(Map.of("x-amz-date", dateTime));
        conditions.add(Map.of(
                "success_action_status",
                "204"
        ));

        if (credentials instanceof AwsSessionCredentials session) {
            conditions.add(Map.of(
                    "x-amz-security-token",
                    session.sessionToken()
            ));
        }

        conditions.add(List.of(
                "content-length-range",
                expectedSizeBytes,
                expectedSizeBytes
        ));

        Map<String, Object> policy = new LinkedHashMap<>();

        policy.put(
                "expiration",
                DateTimeFormatter.ISO_INSTANT.format(expiresAt)
        );

        policy.put("conditions", conditions);

        try {
            byte[] policyJson =
                    objectMapper.writeValueAsBytes(policy);

            return Base64.getEncoder()
                    .encodeToString(policyJson);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Failed to create S3 upload policy.",
                    exception
            );
        }
    }

    private Map<String, String> createFormFields(
            String objectKey,
            String contentType,
            AwsCredentials credentials,
            String credential,
            String dateTime,
            String encodedPolicy,
            String signature
    ) {
        Map<String, String> fields = new LinkedHashMap<>();

        fields.put("key", objectKey);
        fields.put("Content-Type", contentType);
        fields.put("x-amz-algorithm", SIGNING_ALGORITHM);
        fields.put("x-amz-credential", credential);
        fields.put("x-amz-date", dateTime);
        fields.put("success_action_status", "204");

        if (credentials instanceof AwsSessionCredentials session) {
            fields.put(
                    "x-amz-security-token",
                    session.sessionToken()
            );
        }

        fields.put("policy", encodedPolicy);
        fields.put("x-amz-signature", signature);

        return Map.copyOf(fields);
    }

    private String createSignature(
            String secretAccessKey,
            String date,
            String encodedPolicy
    ) {
        byte[] dateKey = hmac(
                ("AWS4" + secretAccessKey)
                        .getBytes(StandardCharsets.UTF_8),
                date
        );

        byte[] regionKey = hmac(dateKey, region);
        byte[] serviceKey = hmac(regionKey, SERVICE);
        byte[] signingKey = hmac(serviceKey, TERMINATOR);

        return HexFormat.of().formatHex(
                hmac(signingKey, encodedPolicy)
        );
    }

    private byte[] hmac(byte[] key, String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA_256);

            mac.init(new SecretKeySpec(
                    key,
                    HMAC_SHA_256
            ));

            return mac.doFinal(
                    value.getBytes(StandardCharsets.UTF_8)
            );
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException(
                    "Failed to sign S3 upload policy.",
                    exception
            );
        }
    }

    private void validateUploadRequest(
            String objectKey,
            String contentType,
            long expectedSizeBytes
    ) {
        if (objectKey == null || objectKey.isBlank()) {
            throw new IllegalArgumentException(
                    "Object key must not be blank."
            );
        }

        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException(
                    "Content type must not be blank."
            );
        }

        if (expectedSizeBytes < 1) {
            throw new IllegalArgumentException(
                    "Expected upload size must be positive."
            );
        }
    }

    private URL createUploadUrl(
            String bucket,
            String region
    ) {
        try {
            return URI.create(
                    "https://"
                            + bucket
                            + ".s3."
                            + region
                            + ".amazonaws.com/"
            ).toURL();
        } catch (MalformedURLException exception) {
            throw new IllegalArgumentException(
                    "Failed to create S3 upload URL.",
                    exception
            );
        }
    }
}