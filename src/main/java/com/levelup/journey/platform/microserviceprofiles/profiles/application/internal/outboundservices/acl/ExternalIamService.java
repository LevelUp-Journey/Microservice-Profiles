package com.levelup.journey.platform.microserviceprofiles.profiles.application.internal.outboundservices.acl;

// import com.levelup.journey.microserviceiam.iam.domain.model.valueobjects.OAuth2UserInfo;
// import com.levelup.journey.microserviceiam.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Service;

// import java.util.Map;

/**
 * External IAM Service
 * ACL implementation for accessing IAM context to get OAuth2 user data
 */
@Service
public class ExternalIamService {
    // private final IamContextFacade iamContextFacade;

    // public ExternalIamService(IamContextFacade iamContextFacade) {
    //     this.iamContextFacade = iamContextFacade;
    // }

    /**
     * Extract OAuth2 user information from provider attributes
     * @param providerName The OAuth2 provider name (google, github)
     * @param attributes The OAuth2 user attributes from provider
     * @return OAuth2UserInfo with extracted user data
     */
    // public OAuth2UserInfo extractOAuth2UserInfo(String providerName, Map<String, Object> attributes) {
    //     return iamContextFacade.extractOAuth2UserInfo(providerName, attributes);
    // }
}